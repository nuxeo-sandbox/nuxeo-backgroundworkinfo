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
 * 
 * @since 10.10
 */
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        "nuxeo.backgroundworkinfo.nuxeo-backgroundworkinfo-core:fakework-contrib.xml" })
public class TestGetOverview {

    @Test
    public void shouldGetOverviewInfo() {

        InfoFetcher fetcher = InfoFetcher.getInstance();
        BackgroundActivitiesOverview overview = fetcher.getOverview();
        assertNotNull(overview);
    }

    @Test
    public void shouldGetOverviewWithAtLeastOneActiveWorker() throws Exception {

        DummyWorker.startWorkers(DummyWorker.MAX_THREADS);

        Thread.sleep(1000);

        InfoFetcher fetcher = InfoFetcher.getInstance();
        
        BackgroundActivitiesOverview overview = fetcher.getOverview();
        assertNotNull(overview);

        long runningCount = overview.running;
        assertTrue(runningCount >= DummyWorker.MAX_THREADS);

        DummyWorker.stopWorkers();
        DummyWorker.awaitWorkersCompletion();

        overview = fetcher.getOverview();
        assertNotNull(overview);
        assertTrue(overview.running <= (runningCount - DummyWorker.MAX_THREADS));

    }

}
