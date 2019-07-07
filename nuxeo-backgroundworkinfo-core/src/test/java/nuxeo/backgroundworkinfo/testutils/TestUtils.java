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

import org.json.JSONArray;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;

import nuxeo.backgroundworkinfo.DummyWorker;

/**
 * 
 * @since 10.10
 */
public class TestUtils {
    
    public static JSONObject getDummyWorkerOverview(JSONArray arr) throws Exception {
        
        for(int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if(obj.getString("name").equals(DummyWorker.QUEUE_ID)) {
                return obj;
            }
        }
        
        return null;
    }
    
    public static JSONObject getDummyBAFOverview(JSONArray arr) throws Exception {
        
        for(int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if(obj.getString("name").equals(DummyBulkAction.ACTION_NAME)) {
                return obj;
            }
        }
        
        return null;
    }
    
    public static void createDocsAndSaveSession(int count, CoreSession session, String parentPath, String docType) {
        
        for(int i = 1; i <= count; i++) {
            DocumentModel doc = session.createDocumentModel(parentPath, docType + "-" + i, docType);
            doc = session.createDocument(doc);
        }
        
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
    }

}
