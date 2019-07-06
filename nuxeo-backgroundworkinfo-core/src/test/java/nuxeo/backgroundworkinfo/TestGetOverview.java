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

import static org.junit.Assert.assertEquals;
/**
 * 
 * @since 10.10
 */
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core",
        "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core:DummyWork-contrib.xml" })
public class TestGetOverview {

    @Test
    public void testGetOverviewBasic() {

        InfoFetcher fetcher = InfoFetcher.getInstance();
        BgActivitiesOverviewBasic overview = fetcher.fetchOverviewBasic();
        assertNotNull(overview);
    }

    @Test
    public void testGetOverviewBasicWithSomeWorkers() throws Exception {

        DummyWorker.startWorkers(DummyWorker.MAX_THREADS);

        InfoFetcher fetcher = InfoFetcher.getInstance();

        BgActivitiesOverviewBasic overview = fetcher.fetchOverviewBasic();
        assertNotNull(overview);

        long runningCount = overview.running;
        assertTrue(runningCount >= DummyWorker.MAX_THREADS);

        DummyWorker.stopWorkers();
        DummyWorker.awaitWorkersCompletion();

        overview = fetcher.fetchOverviewBasic();
        assertNotNull(overview);
        assertTrue(overview.running <= (runningCount - DummyWorker.MAX_THREADS));

    }

    @Test
    public void testGetOverview() throws Exception {

        InfoFetcher fetcher = InfoFetcher.getInstance();
        BgActivitiesOverview overview = fetcher.fetchOverview();
        assertNotNull(overview);

        JSONArray array = overview.toJson();
        JSONObject obj = TestUtils.getDummyWorkerOverview(array);
        assertNotNull(obj);
        assertEquals(0, obj.getLong("running"));
        assertEquals(0, obj.getLong("scheduled"));

    }

    @Test
    public void testGetOverviewWithjSomeWorkers() throws Exception {

        DummyWorker.startWorkers(DummyWorker.MAX_THREADS);

        InfoFetcher fetcher = InfoFetcher.getInstance();
        BgActivitiesOverview overview = fetcher.fetchOverview();
        assertNotNull(overview);

        JSONArray array = overview.toJson();
        JSONObject obj = TestUtils.getDummyWorkerOverview(array);
        assertNotNull(obj);
        assertEquals(DummyWorker.MAX_THREADS, obj.getLong("running"));

        DummyWorker.stopWorkers();
        DummyWorker.awaitWorkersCompletion();

        overview = fetcher.fetchOverview();
        assertNotNull(overview);
        array = overview.toJson();
        obj = TestUtils.getDummyWorkerOverview(array);
        assertNotNull(obj);
        assertEquals(0, obj.getLong("running"));
        assertTrue(obj.getLong("completed") >= DummyWorker.MAX_THREADS);

    }

}
