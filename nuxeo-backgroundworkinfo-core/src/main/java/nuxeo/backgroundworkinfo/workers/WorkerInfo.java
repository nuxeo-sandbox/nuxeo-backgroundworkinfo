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

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.work.api.Work;

import nuxeo.backgroundworkinfo.BackgroundActivityInfo;

/**
 * Wraps info for one worker
 * 
 * @since 10.10
 */
public class WorkerInfo implements BackgroundActivityInfo {

    protected Work work;

    public WorkerInfo(Work work) {
        this.work = work;
    }

    @Override
    public Kind getKind() {
        return Kind.WORKER;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        // return null;
        String name = work.getCategory();
        if (StringUtils.isNoneBlank(name)) {
            name += ": ";
        } else {
            name = "";
        }
        name += work.getTitle();

        return name;
    }

    @Override
    public BackgroundActivityInfo.State getState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getProgress() {
        // TODO Auto-generated method stub
        // return 0;
        throw new UnsupportedOperationException();
    }

    @Override
    public String getErrorMessage() {
        // TODO Auto-generated method stub
        // return null;
        throw new UnsupportedOperationException();
    }

}
