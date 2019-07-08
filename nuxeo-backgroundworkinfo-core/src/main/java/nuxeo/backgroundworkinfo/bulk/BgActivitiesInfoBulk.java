/*
 * (C) Copyright 2019 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thibaud Arguillere
 */
package nuxeo.backgroundworkinfo.bulk;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.nuxeo.ecm.core.bulk.BulkCodecs;
import org.nuxeo.ecm.core.bulk.BulkService;
import org.nuxeo.ecm.core.bulk.BulkServiceImpl;
import org.nuxeo.ecm.core.bulk.message.BulkStatus;
import org.nuxeo.ecm.core.bulk.message.BulkStatus.State;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.kv.KeyValueStoreProvider;

import nuxeo.backgroundworkinfo.BgActivitiesOverview;
import nuxeo.backgroundworkinfo.BgActivitiesOverviewBasic;
import nuxeo.backgroundworkinfo.BgActivityOverview;
import nuxeo.backgroundworkinfo.api.BgActivitiesInfo;

/**
 * Calculate the state of all the Bulk Action Framework
 * 
 * @since 10.10
 */
public class BgActivitiesInfoBulk implements BgActivitiesInfo {

    public final String TYPE = "BAF";

    public BgActivitiesInfoBulk() {

    }

    @Override
    public BgActivitiesOverviewBasic fetchOverviewBasic() {

        BgActivitiesOverview overview = fetchOverview();
        BgActivitiesOverviewBasic overviewBasic = new BgActivitiesOverviewBasic();

        overview.forEach(oneActivity -> {
            overviewBasic.running += oneActivity.running;
            overviewBasic.scheduled += oneActivity.scheduled;
            overviewBasic.completed += oneActivity.completed;
            overviewBasic.aborted += oneActivity.aborted;
        });

        return overviewBasic;
    }

    /**
     * This class uses the KeyValueStore implementation:
     * - It scales
     * - It returns info about the cluster (if deployed in a cluster), so all the running BAF on all the nodes
     */
    @Override
    public BgActivitiesOverview fetchOverview() {

        BulkServiceImpl bulkServiceImpl = (BulkServiceImpl) Framework.getService(BulkService.class);
        KeyValueStoreProvider kv = (KeyValueStoreProvider) bulkServiceImpl.getKvStore();

        List<BulkStatus> statuses = kv.keyStream(BulkServiceImpl.STATUS_PREFIX)
                                      .map(kv::get)
                                      .map(BulkCodecs.getStatusCodec()::decode)
                                      .filter(status -> status.getState() == BulkStatus.State.RUNNING
                                              || status.getState() == BulkStatus.State.SCROLLING_RUNNING
                                              || status.getState() == BulkStatus.State.SCHEDULED
                                              || status.getState() == BulkStatus.State.COMPLETED
                                              || status.getState() == BulkStatus.State.ABORTED)
                                      .collect(Collectors.toList());

        // Group by action id
        HashMap<String, BgActivityOverview> overviewMap = new HashMap<String, BgActivityOverview>();
        for (BulkStatus oneStatus : statuses) {

            String actionName = oneStatus.getAction();

            BgActivityOverview overviewAction = overviewMap.get(actionName);
            if (overviewAction == null) {
                overviewAction = new BgActivityOverview();
                overviewAction.type = TYPE;
                overviewAction.name = actionName;
            }

            switch (oneStatus.getState()) {
            case RUNNING:
            case SCROLLING_RUNNING:
                overviewAction.running += 1;
                break;

            case SCHEDULED:
                overviewAction.scheduled += 1;
                break;

            case COMPLETED:
                overviewAction.completed += 1;
                break;

            case ABORTED:
                overviewAction.aborted += 1;
                break;

            default:
                // Nothing. Should not be here (cf. filter at beginning of loop)
                break;
            }

            overviewMap.put(actionName, overviewAction);
        }

        // Now, convert the map to a BgActivitiesOverview
        BgActivitiesOverview overview = new BgActivitiesOverview();
        overviewMap.forEach((action, actionOverview) -> {
            overview.add(actionOverview);
        });

        return overview;
    }

}
