package com.expedia.s3.cars.supply.service.tests.regression.reserve;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;
import com.expedia.s3.cars.supply.service.requestgenerators.SSRequestGenerator;
import com.expedia.s3.cars.supply.service.requestsender.SSRequestSender;
import com.expedia.s3.cars.supply.service.utils.ExecutionHelper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by v-mechen on 12/21/2017.
 */
public class ErrorHandling {
    private HttpClient httpClient;
    public SpooferTransport spooferTransport;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        final SslContextFactory sslContextFactory = new SslContextFactory();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient,
                SettingsProvider.SPOOFER_SERVER,
                SettingsProvider.SPOOFER_PORT,
                30000);
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 3)
    public void tfs1055553CarSSErrorXPathInvalidCreditCard() throws IOException {
        final com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling errorHandling = new com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling(
                "EM32", "FieldInvalidError",
                "Debit cards are not accepted for guarantee",
                "/reserve:CarSupplyReserveRequest/finance:CreditCardFormOfPayment/finance:CreditCard[1]");
        testCarSSReserveErrorXPath(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "1055553", errorHandling);
    }

    public void testCarSSReserveErrorXPath(TestScenario scenarios, String tuid, com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling errorHandling) {
        try {
            //Generate a random GUID for search
            String guid = UUID.randomUUID().toString();
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.search(scenarios, tuid, httpClient, guid);
            //Create a GUID for reserve
            guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
            //Set ScenarioName override
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "ErrorMap").build(), guid);
            //Do reserve with error
            SSRequestSender.reserveWithError(httpClient, guid, errorHandling, requestGenerator);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }

}
