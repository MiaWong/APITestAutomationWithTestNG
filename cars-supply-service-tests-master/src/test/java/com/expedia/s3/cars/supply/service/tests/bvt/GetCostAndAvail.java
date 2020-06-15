package com.expedia.s3.cars.supply.service.tests.bvt;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.supply.service.requestgenerators.SSRequestGenerator;
import com.expedia.s3.cars.supply.service.requestsender.SSRequestSender;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by yyang4 on 8/19/2016.
 */
public class GetCostAndAvail extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs43355CarSSGetCostAndAvailForAgencyCar() throws IOException {
        testCarSSGetCostAndAvail(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "43355");
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs44432CarSSGetCostAndAvailForGDSPCar() {
        testCarSSGetCostAndAvail(CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario(), "44432");
    }


    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs186892TestCarMicroNexusSSGetCostAndAvailGdsp() {
        testCarSSGetCostAndAvail(CommonScenarios.MN_UK_GDSP_Standalone_nonUKLocation_OnAirport.getTestScenario(), "186892");
    }

    public void testCarSSGetCostAndAvail(TestScenario scenarios, String tuid) {
        try {
            final String guid = UUID.randomUUID().toString();
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.bvtSearch(scenarios, tuid, httpClient, guid);
            SSRequestSender.getCostAndAvail(scenarios, httpClient, guid, requestGenerator);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }
}
