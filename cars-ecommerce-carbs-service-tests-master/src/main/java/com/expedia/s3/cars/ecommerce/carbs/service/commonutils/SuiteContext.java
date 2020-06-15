package com.expedia.s3.cars.ecommerce.carbs.service.commonutils;

import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.BeforeClass;

/**
 * Created by miawang on 9/20/2018.
 */
@SuppressWarnings("PMD")
public class SuiteContext extends SuiteCommon {
    public SpooferTransport spooferTransport = null;
    @BeforeClass(alwaysRun = true)
    public void preBeforeClass() throws Exception
    {
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT,
                30000);
    }
}
