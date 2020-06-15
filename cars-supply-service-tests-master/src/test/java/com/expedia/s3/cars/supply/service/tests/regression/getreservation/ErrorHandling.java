package com.expedia.s3.cars.supply.service.tests.regression.getreservation;

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
    public void tfs1055555CarSSErrorXPathInvalidPNR() throws IOException {
        final com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling errorHandling = new com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling(
                "111111", "ReferenceUnavailableError",
                "Invalid or unavailable PNR",
                "/getreservation:CarSupplyGetReservationRequest/car:CarReservationList/car:CarReservation/car:CarReservation/base:ReferenceList/base:Reference\\[base:ReferenceCategoryCode = 'PNR'\\]");
        testCarSSGetReservationErrorHandling(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "1055555",
                "PNRInvalidAddress", errorHandling);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 3)
    public void tfs1055549CarSSErrorXPathInvalidLastName() throws IOException {
        final com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling errorHandling = new com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling(
                "STT WO", "FieldInvalidError",
                "The traveler was not found on the reservation",
                "/getreservation:CarSupplyGetReservationRequest/car:CarReservationList/car:CarReservation/car:CarReservation/travel:TravelerList/travel:Traveler\\[1\\]/person:Person/person:PersonName/person:LastName");
        testCarSSGetReservationErrorHandling(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "1055549",
                "TravelerError", errorHandling);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 3)
    public void tfs774101CarSSCurrencyNotAvailableError() throws IOException {
        final com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling errorHandling = new com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling(
                "", "CurrencyNotAvailableError", "Invalid currency code", "");
        testCarSSGetReservationErrorHandling(CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario(), "774101",
                "CurrencyNotAvailableError", errorHandling);
    }

    public void testCarSSGetReservationErrorHandling(TestScenario scenarios, String tuid, String spooferOverrideName,
                                                     com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling errorHandling) {
        try {
            //Generate a random GUID for search
            String guid = UUID.randomUUID().toString();
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.search(scenarios, tuid, httpClient, guid);
            //reserve
            SSRequestSender.reserve(scenarios, tuid, httpClient, guid, requestGenerator);
            //Create a GUID for GetReservation
            guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
            //Set ScenarioName override
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferOverrideName).build(), guid);
            //Do getReservation with error
            SSRequestSender.getReservationWithError(httpClient, guid, errorHandling, requestGenerator);
            //Cancel
            guid = UUID.randomUUID().toString();
            SSRequestSender.cancel(scenarios, tuid, httpClient, guid, requestGenerator);

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }

}
