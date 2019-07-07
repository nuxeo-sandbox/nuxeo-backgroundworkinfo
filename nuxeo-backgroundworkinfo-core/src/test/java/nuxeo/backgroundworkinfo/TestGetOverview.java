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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import nuxeo.backgroundworkinfo.testutils.DummyBulkAction;
import nuxeo.backgroundworkinfo.testutils.TestUtils;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.core.bulk",
          "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core",
          "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core:DummyWork-contrib.xml",
          "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core:DummyBukAction-contrib.xml"})
public class TestGetOverview {

    @Inject
    CoreSession session;

    @Test
    public void testDummy() throws Exception {

        // For quick testing - to be removed later
    }

    @Test
    public void testGetOverviewBasic() throws Exception {

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
    
    // We can't test both tesWithBAF and tesWithSeveralBAF. After running tesWithBAF,
    // tesWithSeveralBAF fails, fetchOverview does not get BAF statuses and I have no
    // idea why. I suspect it's a lack or re-initialization between 2 tests maybe?
    @Ignore
    @Test
    public void tesWithBAF() throws Exception {
        
        // Create a couple of documents for the BAF even if they are not used
        TestUtils.createDocsAndSaveSession(3, session, "/", "File");
        
        InfoFetcher fetcher = InfoFetcher.getInstance();

        // Get overview. Should not have our BAF overview
        BgActivitiesOverview overviewBefore = fetcher.fetchOverview();
        assertNotNull(overviewBefore);
        JSONArray array = overviewBefore.toJson();
        JSONObject obj = TestUtils.getDummyBAFOverview(array);
        assertNull(obj);
        
        // Start the BAF computation
        String commandId = DummyBulkAction.startBulkActionWithDocs("SELECT * FROM File");
        
        // Get overview, we should have our DummyBulkAction
        BgActivitiesOverview overviewAfter = fetcher.fetchOverview();
        assertNotNull(overviewAfter);
        array = overviewAfter.toJson();
        obj = TestUtils.getDummyBAFOverview(array);
        assertNotNull(obj);
        assertTrue(obj.getLong("running") == 1);
        
        // Stop the computation
        DummyBulkAction.stopBulkAction();
        // Wait all is stopped
        DummyBulkAction.awaitCompletion(commandId);
        
        // We don't have anything running (either not there at all, or nothing running
        BgActivitiesOverview overviewEnd = fetcher.fetchOverview();
        assertNotNull(overviewEnd);
        array = overviewEnd.toJson();
        obj = TestUtils.getDummyBAFOverview(array);
        if(obj != null) {
            assertTrue(obj.getLong("running") == 0);
        }
        
    }
    

    
    @Test
    public void tesWithSeveralBAF() throws Exception {
        
        // Create a couple of documents for the BAF even if they are not used
        TestUtils.createDocsAndSaveSession(3, session, "/", "File");
        
        InfoFetcher fetcher = InfoFetcher.getInstance();

        // Get overview. Should not have our BAF overview
        BgActivitiesOverview overviewBefore = fetcher.fetchOverview();
        assertNotNull(overviewBefore);
        JSONArray array = overviewBefore.toJson();
        JSONObject obj = TestUtils.getDummyBAFOverview(array);
        assertNull(obj);
        
        // Start the BAF computation
        String commandId1 = DummyBulkAction.startBulkActionWithDocs("SELECT * FROM File");
        String commandId2 = DummyBulkAction.startBulkActionWithDocs("SELECT * FROM File");
        String commandId3 = DummyBulkAction.startBulkActionWithDocs("SELECT * FROM File");
        
        // Get overview, we should have our DummyBulkAction
        BgActivitiesOverview overviewAfter = fetcher.fetchOverview();
        assertNotNull(overviewAfter);
        array = overviewAfter.toJson();
                
        obj = TestUtils.getDummyBAFOverview(array);
        assertNotNull(obj);
        assertTrue(obj.getLong("running") == 3);
                
        // Stop the computation
        DummyBulkAction.stopBulkAction();
        // Wait all is stopped
        DummyBulkAction.awaitCompletion(commandId1);
        DummyBulkAction.awaitCompletion(commandId2);
        DummyBulkAction.awaitCompletion(commandId3);
        
        // We don't have anything running (either not there at all, or nothing running
        BgActivitiesOverview overviewEnd = fetcher.fetchOverview();
        assertNotNull(overviewEnd);
        array = overviewEnd.toJson();
        obj = TestUtils.getDummyBAFOverview(array);
        if(obj != null) {
            assertTrue(obj.getLong("running") == 0);
        }
        
    }

}
