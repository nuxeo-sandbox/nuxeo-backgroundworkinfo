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
package nuxeo.backgroundworkinfo;

import nuxeo.backgroundworkinfo.bulk.BgActivitiesInfoBulk;
import nuxeo.backgroundworkinfo.workers.BgActivitiesInfoWorkers;

/**
 * Main entry point to fetch background activities info.
 * 
 * @since 10.10
 */
public class InfoFetcher {

    private static InfoFetcher instance = null;

    public static final String LOCK = "InfoFetcherLock";

    public static final String RUN_OVERVIEWBASIC_LOCK = "RunOverviewBasicLock";

    protected boolean fetchingOverviewBasic = false;

    protected BgActivitiesOverviewBasic overviewBasic = new BgActivitiesOverviewBasic();

    public static final String RUN_OVERVIEW_LOCK = "RunOverviewLock";

    protected boolean fetchingOverview = false;

    protected BgActivitiesOverview overview = new BgActivitiesOverview();

    private InfoFetcher() {

    }

    /**
     * @return the singleton instance
     * @since 10.10
     */
    public static InfoFetcher getInstance() {

        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new InfoFetcher();
                }
            }
        }

        return instance;
    }

    /**
     * Return the basic overview: Just count of running/scheduled/etc., no names/titles/IDs, no details
     * 
     * @return the basic overview
     * @since 10.10
     */
    public BgActivitiesOverviewBasic fetchOverviewBasic() {

        if (!fetchingOverviewBasic) {
            synchronized (RUN_OVERVIEWBASIC_LOCK) {
                if (!fetchingOverviewBasic) {
                    fetchingOverviewBasic = true;

                    // Workers
                    BgActivitiesInfoWorkers workersInfo = new BgActivitiesInfoWorkers();
                    BgActivitiesOverviewBasic overviewWorkers = workersInfo.fetchOverviewBasic();

                    // BAF
                    BgActivitiesInfoBulk bafInfo = new BgActivitiesInfoBulk();
                    BgActivitiesOverviewBasic overviewBAF = bafInfo.fetchOverviewBasic();

                    // Others...

                    // Merge
                    overviewBasic.reset();
                    overviewBasic.add(overviewWorkers);
                    overviewBasic.add(overviewBAF);
                    // . . .

                }
            }
            fetchingOverviewBasic = false;
        }
        return overviewBasic;

    }

    /**
     * Return an array of background activities. Each entry has the namle of the activity (name/title/id of the object)
     * and the number of running/scheduled/etc.
     * 
     * @return an array of activities
     * @since 10.10
     */
    public BgActivitiesOverview fetchOverview() {

        if (!fetchingOverview) {
            synchronized (RUN_OVERVIEW_LOCK) {
                if (!fetchingOverview) {
                    fetchingOverview = true;

                    // Workers
                    BgActivitiesInfoWorkers workersInfo = new BgActivitiesInfoWorkers();
                    BgActivitiesOverview overviewWorkers = workersInfo.fetchOverview();

                    // BAF
                    BgActivitiesInfoBulk bafInfo = new BgActivitiesInfoBulk();
                    BgActivitiesOverview overviewBAF = bafInfo.fetchOverview();

                    // Others...

                    // Merge
                    overview = new BgActivitiesOverview();
                    overview.addAll(overviewWorkers);
                    overview.addAll(overviewBAF);
                    // . . .

                }
            }
            fetchingOverview = false;
        }
        return overview;

    }

}
