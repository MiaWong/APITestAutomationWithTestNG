package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fehu on 3/15/2017.
 */
public class CarbsServiceHealthCheck extends SuiteCommon {

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void mnSCSServiceHealthCheck() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        final  com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck serviceHealthCheck =
                new com.expedia.s3.cars.framework.test.common.execution.requestgeneration.ServiceHealthCheck();
        final String eMsg = serviceHealthCheck.isSserviceHealthOperational(SettingsProvider.SERVICE_ADDRESS, httpClient);
        Assert.isTrue(null == eMsg, eMsg);
    }

}
