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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;

import nuxeo.backgroundworkinfo.BgActivitiesOverview;
import nuxeo.backgroundworkinfo.BgActivitiesOverviewBasic;
import nuxeo.backgroundworkinfo.InfoFetcher;

/**
 * Return a JSON object as string in the "BackgroundWorkOverview" context variable, quick info about bg activities
 * (numbers, not details)
 */
@Operation(id = BackgroundWorkOverviewOp.ID, category = Constants.CAT_DOCUMENT, label = "Background Work: Overview", description = "Sets the BackgroundWorkOverview "
        + "context variable to a JSON object, quick infos about background activities."
        + " Numbers only. If infoType is Overview, BackgroundWorkOverview is a JSON array "
        + "(as string) of basic info with the name of the activity)")
public class BackgroundWorkOverviewOp {

    public static final String ID = "BackgroundWork.Overview";

    public static final String CTX_VAR_NAME = "BackgroundWorkOverview";

    @Context
    protected OperationContext ctx;

    @Param(name = "infoType", description = "Type of info to return", values = { "Basic", "Overview" })
    protected String infoType = "Basic";

    @OperationMethod
    public void run() throws JSONException {

        if (StringUtils.isBlank(infoType)) {
            infoType = "Basic";
        }

        InfoFetcher fetcher = InfoFetcher.getInstance();

        switch (infoType) {
        case "Overview":
            BgActivitiesOverview overview = fetcher.fetchOverview();
            ctx.put(CTX_VAR_NAME, overview.toJson().toString());
            break;

        default:
            BgActivitiesOverviewBasic overviewBasic = fetcher.fetchOverviewBasic();
            ctx.put(CTX_VAR_NAME, overviewBasic.toJson().toString());
            break;
        }

    }
}
