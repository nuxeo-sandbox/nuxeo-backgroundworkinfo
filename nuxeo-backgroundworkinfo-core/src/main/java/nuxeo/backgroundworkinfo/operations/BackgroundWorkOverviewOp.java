/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/) and others.
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
package nuxeo.backgroundworkinfo.operations;

import org.json.JSONException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

import nuxeo.backgroundworkinfo.BackgroundActivitiesOverview;
import nuxeo.backgroundworkinfo.InfoFetcher;

/**
 * Return a JSON object as string, quick info about bg activities (numbers, not details)
 */
@Operation(id = BackgroundWorkOverviewOp.ID, category = Constants.CAT_DOCUMENT, label = "Background Work: Overview", description = "Sets the BackgroundWorkOverview context variable to a JSON object, quick infos about background activities (numbers, not details)")
public class BackgroundWorkOverviewOp {

    public static final String ID = "BackgroundWork.Overview";

    public static final String CTX_VAR_NAME = "BackgroundWorkOverview";

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public void run() throws JSONException {

        InfoFetcher fetcher = InfoFetcher.getInstance();
        BackgroundActivitiesOverview overview = fetcher.getOverview();

        ctx.put(CTX_VAR_NAME, overview.toJson().toString());

    }
}
