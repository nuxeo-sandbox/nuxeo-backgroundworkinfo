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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * "Quick" access on current states: Just numbers, no details.
 * 
 * @since 10.10
 */
public class BackgroundActivitiesOverview {

    public long scheduled = 0;

    public long running = 0;

    public long completed = 0;

    public long aborted = 0;

    public BackgroundActivitiesOverview() {

    }

    public void reset() {
        scheduled = 0;
        running = 0;
        completed = 0;
        aborted = 0;
    }

    public void add(BackgroundActivitiesOverview other) {
        scheduled += other.scheduled;
        running += other.running;
        completed += other.completed;
        aborted += other.aborted;
    }

    public JSONObject toJson() throws JSONException {

        JSONObject obj = new JSONObject();

        obj.put("scheduled", scheduled);
        obj.put("running", running);
        obj.put("completed", completed);
        obj.put("aborted", aborted);

        return obj;

    }

}
