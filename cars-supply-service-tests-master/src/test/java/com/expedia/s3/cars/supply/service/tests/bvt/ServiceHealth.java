package com.expedia.s3.cars.supply.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by v-mechen on 3/15/2017.
 */
public class ServiceHealth extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void serviceHealthCheck() throws IOException {

        final StringBuilder serviceAddress = new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "search" -> http://cars-supply-service.us-west-2.int.expedia.com/search
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/search");
        }
        final ServiceHealthCheck healthCheck = new ServiceHealthCheck();
        final String errorMsg = healthCheck.isSserviceHealthOperational(serviceAddress.toString(), httpClient);
        if(null != errorMsg && !errorMsg.isEmpty())
        {
            Assert.fail(errorMsg);
        }

    }
}
