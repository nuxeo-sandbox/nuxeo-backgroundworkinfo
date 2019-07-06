package nuxeo.backgroundworkinfo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import nuxeo.backgroundworkinfo.operations.BackgroundWorkOverviewOp;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core",
        "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core:fakework-contrib.xml" })
public class TestOperations {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldGetOverviewInfo() throws Exception {

        OperationContext ctx = new OperationContext(session);
        automationService.run(ctx, BackgroundWorkOverviewOp.ID);
        String jsonStr = (String) ctx.get(BackgroundWorkOverviewOp.CTX_VAR_NAME);

        assertNotNull(jsonStr);

    }

    @Test
    public void shouldGetOverviewInfoWithWorkers() throws Exception {

        DummyWorker.startWorkers(DummyWorker.MAX_THREADS);

        Thread.sleep(1000);

        OperationContext ctx = new OperationContext(session);
        automationService.run(ctx, BackgroundWorkOverviewOp.ID);
        String jsonStr = (String) ctx.get(BackgroundWorkOverviewOp.CTX_VAR_NAME);

        assertNotNull(jsonStr);
        JSONObject obj = new JSONObject(jsonStr);
        long runningCount = obj.getInt("running");
        assertTrue(runningCount >= DummyWorker.MAX_THREADS);

        DummyWorker.stopWorkers();
        DummyWorker.awaitWorkersCompletion();

        ctx = new OperationContext(session);
        automationService.run(ctx, BackgroundWorkOverviewOp.ID);
        jsonStr = (String) ctx.get(BackgroundWorkOverviewOp.CTX_VAR_NAME);
        assertNotNull(jsonStr);
        obj = new JSONObject(jsonStr);
        long running = obj.getInt("running");
        assertTrue(running <= (runningCount - DummyWorker.MAX_THREADS));

    }
}
