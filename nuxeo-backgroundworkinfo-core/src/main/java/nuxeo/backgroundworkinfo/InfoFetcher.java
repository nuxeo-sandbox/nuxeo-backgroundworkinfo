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

import java.util.ArrayList;

import nuxeo.backgroundworkinfo.workers.WorkersInfo;

/**
 * Usage:
 * BackgroundActivitiesOverview overview = InfoFetcher.getInstance().getOverview();
 * . . . use overview . . .
 * ArrayList<BackgroundActivityInfo> activities = InfoFetcher.getInstance().getDetails();
 * . . . use activities . . .
 * 
 * @since 10.10
 */
public class InfoFetcher {

    private static InfoFetcher instance = null;

    public static final String LOCK = "InfoFetcherLock";

    public static final String RUN_OVERVIEW_LOCK = "RunOverviewLock";

    public static final String RUN_DETAILS_LOCK = "RunDetailsLock";

    protected boolean fetchingOverview = false;

    protected boolean fetchingDetails = false;

    protected BackgroundActivitiesOverview overview = new BackgroundActivitiesOverview();

    private InfoFetcher() {

    }

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

    public BackgroundActivitiesOverview getOverview() {

        if (!fetchingOverview) {
            synchronized (RUN_OVERVIEW_LOCK) {
                if (!fetchingOverview) {
                    fetchingOverview = true;

                    // Workers
                    WorkersInfo workersInfo = new WorkersInfo();
                    BackgroundActivitiesOverview overviewWorkers = workersInfo.fetchOverview();

                    // BAF
                    // ...

                    // Others...

                    // Merge
                    overview.reset();
                    overview.add(overviewWorkers);
                    // . . .

                }
            }
            fetchingOverview = false;
        }
        return overview;

    }

}
