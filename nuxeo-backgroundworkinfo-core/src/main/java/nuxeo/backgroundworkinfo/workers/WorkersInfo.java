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
package nuxeo.backgroundworkinfo.workers;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.core.work.api.WorkQueueDescriptor;
import org.nuxeo.ecm.core.work.api.WorkQueueMetrics;
import org.nuxeo.runtime.api.Framework;

import nuxeo.backgroundworkinfo.BackgroundActivitiesInfo;
import nuxeo.backgroundworkinfo.BackgroundActivitiesOverview;

/**
 * Calculate the state of all the workers (as available thru the work manager)
 * 
 * @since 10.10
 */
public class WorkersInfo implements BackgroundActivitiesInfo {

    public WorkersInfo() {

    }

    @Override
    public BackgroundActivitiesOverview fetchOverview() {

        BackgroundActivitiesOverview overview = new BackgroundActivitiesOverview();

        WorkManager workManager = Framework.getService(WorkManager.class);

        List<String> queueIds = workManager.getWorkQueueIds();
        for (String queueId : queueIds) {
            WorkQueueMetrics metrics = workManager.getMetrics(queueId);
            overview.aborted += metrics.getCanceled().longValue();
            overview.completed += metrics.getCompleted().longValue();
            overview.running += metrics.getRunning().longValue();
            overview.scheduled += metrics.getScheduled().longValue();
        }

        return overview;
    }

}
