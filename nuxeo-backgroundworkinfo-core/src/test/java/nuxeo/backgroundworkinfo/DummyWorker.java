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

import java.util.concurrent.TimeUnit;

import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Doing nothing but sleeping until the end of the test
 * 
 * @since 10.10
 */
public class DummyWorker extends AbstractWork {

    private static final long serialVersionUID = 5847044151039940207L;

    // As defined in fakework-contrib.xml
    public static final String CATEGORY = "Fake Workers";

    // As defined in fakework-contrib.xml
    public static final String QUEUE_ID = "FakeWorker";

    // As defined in fakework-contrib.xml
    public static final int MAX_THREADS = 3;

    protected static boolean doRun = true;

    public DummyWorker(String id) {
        super(id);
    }

    @Override
    public String getTitle() {
        return "Fake Worker for Test: " + getId();
    }

    @Override
    public String getCategory() {
        return CATEGORY;
    }

    @Override
    public void work() {

        setStatus("Running");
        while (doRun) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setStatus("Done");

    }

    // ==================== Utilities to centralize start/stop of a FakeWorker
    public static DummyWorker startWorker(String workId) {
        
        doRun = true;

        DummyWorker fakeWorker = new DummyWorker(workId);
        WorkManager workManager = Framework.getService(WorkManager.class);

        workManager.schedule(fakeWorker);

        return fakeWorker;
    }

    public static void startWorkers(int count) {

        for (int i = 1; i <= count; i++) {
            startWorker("DummyWorker-" + i);
        }
    }

    public static void stopWorkers() {

        doRun = false;
    }

    public static void awaitWorkersCompletion() throws InterruptedException {

        WorkManager workManager = Framework.getService(WorkManager.class);

        workManager.awaitCompletion(QUEUE_ID, 10, TimeUnit.SECONDS);

    }

}
