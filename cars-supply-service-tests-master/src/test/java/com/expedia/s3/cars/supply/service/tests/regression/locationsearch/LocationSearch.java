package com.expedia.s3.cars.supply.service.tests.regression.locationsearch;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonLocationSearchScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.supply.service.requestsender.SSRequestSender;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by v-mechen on 9/15/2017.
 */
public class LocationSearch {
     private HttpClient httpClient;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        final SslContextFactory sslContextFactory = new SslContextFactory();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.start();
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs229806CarSSLocationSearch() {
        testCarSSLocationSearch(CommonLocationSearchScenarios.Amadeus_FRA_LatLong_10M_DeliveryCollectionOutOfOfficeTrue_NeedDetail.getTestScenario(), "229806");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs240031CarSSLocationSearchError() {
        testCarSSLocationSearch(CommonLocationSearchScenarios.Amadeus_FRA_LatLong_IATA.getTestScenario(), "240031");
    }

    public void testCarSSLocationSearch(LocationSearchTestScenario scenarios, String tuid) {
        try {
            //send search request
            SSRequestSender.locationSearch(scenarios, tuid, httpClient, null);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }
}
