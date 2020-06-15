package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;



/**
 * Created by mpaudel on 5/18/16.
 */

public class Search extends SuiteCommon {
    Logger logger = Logger.getLogger(getClass());

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void searchBVTTestStandaloneRoundTrip() throws Exception {

        testSearch(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "70001");
    }


    private void testSearch(TestScenario scenarios, String tuid) throws Exception {
        //1,search
        final SearchVerificationInput searchVerificationInput = SearchHelper.bvtSearch(httpClient, scenarios, tuid);
        SearchHelper.searchVerification(searchVerificationInput, null, scenarios, PojoXmlUtil.getRandomGuid(), logger, false);
    }
}



