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
public class GetDetails extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs43354CarSSGetdetailsForAgencyCar() throws IOException, DataAccessException {
        testCarSSGetdetails(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "43354");
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs44431CarSSGetdetailsForGDSPCar() throws DataAccessException {
        testCarSSGetdetails(CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario(), "44431");
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs140632TestCarMicroNexusSSGetdetailsGdsp() throws DataAccessException {
        testCarSSGetdetails(CommonScenarios.MN_UK_GDSP_Standalone_nonUKLocation_OnAirport.getTestScenario(), "140632");
    }

    public void testCarSSGetdetails(TestScenario scenarios, String tuid) throws DataAccessException {
        try {
            final String guid = UUID.randomUUID().toString();
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.bvtSearch(scenarios, tuid, httpClient, guid);
            SSRequestSender.getDetail(scenarios, tuid, httpClient, guid, requestGenerator);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
