package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.bvt;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fehu on 3/12/2017.
 */
public class ServiceHealthCheckForMN extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void mnSCSServiceHealthCheck() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck serviceHealthCheck =
                new com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck();
        String eMsg = serviceHealthCheck.isSserviceHealthOperational(SettingsProvider.SERVICE_ADDRESS, httpClient);
        Assert.isTrue(null == eMsg, eMsg);
    }
}
