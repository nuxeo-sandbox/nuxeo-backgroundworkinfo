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
package nuxeo.backgroundworkinfo.api;

import nuxeo.backgroundworkinfo.BgActivitiesOverview;
import nuxeo.backgroundworkinfo.BgActivitiesOverviewBasic;

/**
 * Interface tio be used by any background activity info fetcher (workers, BAF, ...)
 * 
 * @since 10.10
 */
public interface BgActivitiesInfo {

    /**
     * Return the basic overview: Just count of running/scheduled/etc., no names/titles/IDs, no details
     * 
     * @return the basic overview, just numbers
     * @since 10.10
     */
    public BgActivitiesOverviewBasic fetchOverviewBasic();

    /**
     * Return an array of background activities. Each entry has the namle of the activity (name/title/id of the object)
     * and the number of running/scheduled/etc.
     * 
     * @return an array of activities
     * @since 10.10
     */
    public BgActivitiesOverview fetchOverview();

}
