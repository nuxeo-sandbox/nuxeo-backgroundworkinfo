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
package nuxeo.backgroundworkinfo.testutils;

import static org.nuxeo.ecm.core.bulk.BulkServiceImpl.STATUS_STREAM;
import static org.nuxeo.lib.stream.computation.AbstractComputation.INPUT_1;
import static org.nuxeo.lib.stream.computation.AbstractComputation.OUTPUT_1;

import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.bulk.BulkService;
import org.nuxeo.ecm.core.bulk.action.computation.AbstractBulkComputation;
import org.nuxeo.ecm.core.bulk.message.BulkCommand;
import org.nuxeo.lib.stream.computation.Topology;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.stream.StreamProcessorTopology;

/**
 * @since 10.10
 */
public class DummyBulkAction implements StreamProcessorTopology {

    // Same as DummyBukAction-contrib.xml
    public static final String ACTION_NAME = "DummyBulkAction";

    // =====================================================================
    // Bulk action processing
    // =====================================================================
    @Override
    public Topology getTopology(Map<String, String> options) {
        return Topology.builder()
                       .addComputation(DummyBulkActionComputation::new,
                               Arrays.asList(INPUT_1 + ":" + ACTION_NAME, OUTPUT_1 + ":" + STATUS_STREAM))
                       .build();
    }

    public static class DummyBulkActionComputation extends AbstractBulkComputation {

        public DummyBulkActionComputation() {
            super(ACTION_NAME);
        }

        @Override
        public void startBucket(String bucketKey) {
            // Nothing
        }

        @Override
        protected void compute(CoreSession session, List<String> ids, Map<String, Serializable> properties) {

            // Not using the document list
            while (doRun) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // =====================================================================
    // Accessors to start/stop for testing
    // =====================================================================
    public static boolean doRun = true;

    /**
     * Start the BAF, return the command ID, to be used with DummyBulkAction.awaitCompletion()
     * 
     * @param query to run (must return at leats one document)
     * @return the command Id
     * @since 10.10
     */
    public static String startBulkActionWithDocs(String query) {

        // Construct query
        BulkCommand command = new BulkCommand.Builder(DummyBulkAction.ACTION_NAME, query).user("Administrator").build();

        // Submit command
        BulkService bulkService = Framework.getService(BulkService.class);
        String commandId = bulkService.submit(command);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        return commandId;

    }

    public static void stopBulkAction() {
        doRun = false;
    }
    
    public static void awaitCompletion(String commandId) {
        
        BulkService bulkService = Framework.getService(BulkService.class);
        try {
            boolean complete = false;
            while (!complete) {
                // Await end of computation
                complete = bulkService.await(commandId, Duration.ofSeconds(3));
            }

        } catch (InterruptedException iex) {
            // ignored
        }
        
    }

}
