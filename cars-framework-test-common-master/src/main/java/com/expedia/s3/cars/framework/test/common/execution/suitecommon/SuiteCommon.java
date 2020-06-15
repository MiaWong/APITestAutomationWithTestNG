package com.expedia.s3.cars.framework.test.common.execution.suitecommon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;


/**
 * Created by jiyu on 12/14/16.
 */
@SuppressWarnings("PMD")
public class SuiteCommon
{
    public static Logger logger = null;
    public static HttpClient httpClient = null;

    @BeforeTest(alwaysRun = true)
    public void suiteSetup() throws Exception
    {
        if (logger == null) {
            //  logger
            logger = Logger.getLogger(getClass());
        }

        logger.setLevel(Level.INFO);

        if (httpClient == null) {
            //  http client
            SslContextFactory sslContextFactory = new SslContextFactory(true);
            httpClient = new HttpClient(sslContextFactory);
            httpClient.start();
        }
    }

    @AfterTest
    public void tearDown() throws Exception
    {
        if (httpClient != null) {
            httpClient.stop();
        }
    }

}
