package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.bvt;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


/**
 * Created by miawang on 3/12/17.
 */

public class ServiceHealthCheck  extends SuiteCommon {

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_362054_ServiceHealthCheck() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck serviceHealthCheck =
                new com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck();
        String eMsg = serviceHealthCheck.isSserviceHealthOperational(SettingsProvider.SERVICE_ADDRESS, httpClient);
        Assert.isTrue(null == eMsg, eMsg);
    }
}
