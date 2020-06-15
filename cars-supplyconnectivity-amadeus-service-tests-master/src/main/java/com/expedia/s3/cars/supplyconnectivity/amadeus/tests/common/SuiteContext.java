package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common;

import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.BeforeClass;

/**
 * Created by fehu on 9/7/2017.
 */
public class SuiteContext extends SuiteCommon {
    public SpooferTransport spooferTransport = null;
    @BeforeClass(alwaysRun = true)
    public void preBeforeClass() throws Exception
    {
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
    }
}
