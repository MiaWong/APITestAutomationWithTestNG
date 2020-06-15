package com.expedia.s3.cars.supply.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.supply.service.requestsender.SSRequestSender;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by yyang4 on 8/19/2016.
 */
public class Search extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs43353CarSSSearchForAgencyCar() throws IOException {
        testCarSSSearch(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "43353");
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs44430CarSSSearchForGDSPCar() {
        testCarSSSearch(CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario(), "44430");
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs140631testCarSSSearchMnscs() {
        testCarSSSearch(CommonScenarios.MN_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "140631");
    }

    public void testCarSSSearch(TestScenario scenarios, String tuid) {
        try {
            //send search request
            SSRequestSender.bvtSearch(scenarios, tuid, httpClient, null);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
