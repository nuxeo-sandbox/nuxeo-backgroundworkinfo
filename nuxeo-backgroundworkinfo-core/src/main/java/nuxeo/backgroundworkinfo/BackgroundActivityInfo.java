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

/**
 * Wrapper around the misc. background activities (Workers, BAF, ...) so as to normalize their state.
 * 
 * @since 10.10
 */
public interface BackgroundActivityInfo {

    public enum State {

        UNKNOWN, SCHEDULED, RUNNING, COMPLETED, ABORTED
    }

    public enum Kind {
        WORKER, BAF, OTHER
    }

    /**
     * @return the kind of the background activity
     * @since 10.10
     */
    public Kind getKind();

    /**
     * @return the name of the background activity
     * @since 10.10
     */
    public String getName();

    /**
     * @return the state of the activity activity
     * @since 10.10
     */
    public BackgroundActivityInfo.State getState();

    /**
     * @return the % completed
     * @since 10.10
     */
    public float getProgress();

    /**
     * @return an error message, if any
     * @since 10.10
     */
    public String getErrorMessage();

}
