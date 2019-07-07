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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Info for one queue/job/...
 * 
 * @since 10.10
 */
public class BgActivityOverview implements Comparable<BgActivityOverview>{

    public String type = "";

    public String name = "";

    public long scheduled = 0;

    public long running = 0;

    public long completed = 0;

    public long aborted = 0;

    public BgActivityOverview() {

    }

    /**
     * Reset all values to "" or 0.
     * 
     * @since 10.10
     */
    public void reset() {
        type = "";
        name = "";
        scheduled = 0;
        running = 0;
        completed = 0;
        aborted = 0;
    }

    /**
     * Add the values of other. type and name must match exactly, or IllegalArgumentException is throwned.
     * 
     * @param other
     * @since 10.10
     */
    public void add(BgActivityOverview other) {
        String typeAndName = type + name;
        if (!typeAndName.equals(other.type + other.name)) {
            throw new IllegalArgumentException();
        }
        scheduled += other.scheduled;
        running += other.running;
        completed += other.completed;
        aborted += other.aborted;
    }

    /**
     * Return the JSON representation of the activity
     * 
     * @return the JSON representation of the activity
     * @throws JSONException
     * @since 10.10
     */
    public JSONObject toJson() throws JSONException {

        JSONObject obj = new JSONObject();

        obj.put("type", type);
        obj.put("name", name);
        obj.put("scheduled", scheduled);
        obj.put("running", running);
        obj.put("completed", completed);
        obj.put("aborted", aborted);

        return obj;

    }

    @Override
    public int compareTo(BgActivityOverview o) {
         String typeAndName = type + "-" + name;
         return typeAndName.compareTo(o.type + "-" + o.name);
    }

}
