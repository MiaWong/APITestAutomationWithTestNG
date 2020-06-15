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
public class Reserve extends SuiteCommon{

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfs43356TestCarSSReserveandCancelAgency()throws IOException {
        testCarSSReserveAndCancel(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario(), "43356");
    }

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfs43358TestCarSSReserveandCancelGdsp()
    {
        testCarSSReserveAndCancel(CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario(), "43358");
    }

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfsTestCarSSReserveandCancelMngdsp()
    {
        testCarSSReserveAndCancel(CommonScenarios.MN_UK_GDSP_Standalone_nonUKLocation_OnAirport.getTestScenario(), "44434");
    }

    public void testCarSSReserveAndCancel(TestScenario scenarios, String tuid){
        try {
            final String  guid = UUID.randomUUID().toString();
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.bvtSearch(scenarios,tuid,httpClient, guid);
            SSRequestSender.reserve(scenarios,tuid,httpClient, guid,requestGenerator);
            SSRequestSender.cancel(scenarios,tuid,httpClient, guid,requestGenerator);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }
}
