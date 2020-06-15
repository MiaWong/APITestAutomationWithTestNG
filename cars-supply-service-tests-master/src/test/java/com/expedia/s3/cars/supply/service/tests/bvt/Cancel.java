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
public class Cancel extends SuiteCommon{

    @Test(groups = {TestGroup.BVT})
    public void tfsCancelAllUncancelledBooking() {
        try {
            final String guid = UUID.randomUUID().toString();
            final TestScenario scenarios = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
            final String tuid = "10110";
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.bvtSearch(scenarios, tuid, httpClient, guid);
            SSRequestSender.reserve(scenarios, tuid, httpClient, guid, requestGenerator);
            SSRequestSender.cancel(scenarios, tuid, httpClient, guid, requestGenerator);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }
}
