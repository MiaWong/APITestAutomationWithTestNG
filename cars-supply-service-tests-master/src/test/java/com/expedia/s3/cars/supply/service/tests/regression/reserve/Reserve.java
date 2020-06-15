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
import com.expedia.s3.cars.supply.service.verification.ReserveVerifier;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Node;

import java.io.IOException;

/**
 * Created by v-mechen on 12/20/2017.
 */
public class Reserve {
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

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 0)
    public void tfs79226TestCarSSReserveSI()
    {
        testCarSSReserveAndCancel(CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario(), "79226", 15111);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 0)
    public void tfs79234TestCarSSReserveVO()
    {
        testCarSSReserveAndCancel(CommonScenarios.Worldspan_CA_GDSP_Standalone_CALocation_OnAirport.getTestScenario(), "79234", 13703);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 0)
    public void tfs79213TestCarSSReserveNoSIVO()
    {
        testCarSSReserveAndCancel(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "79213", 2834);
    }

    public void testCarSSReserveAndCancel(TestScenario scenarios, String tuid, long supplysubsetID){
        try {
            final String  guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);;
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.search(scenarios,tuid,httpClient, guid);
            //select car by vendorSupplierID when it's passed in request
            if(supplysubsetID > 0)
            {
                requestGenerator.selectCarBySupplysubsetID(supplysubsetID);
            }
            //reserve
            SSRequestSender.reserve(scenarios,tuid,httpClient, guid,requestGenerator, true);
            //Verify SI/VO
            final Node vcrrReq = spooferTransport.retrieveRecords(guid).getElementsByTagNameNS("*", "VehicleCreateReservationReq").item(0);
            ReserveVerifier.verifySIVO(requestGenerator.getReserveReq(), requestGenerator.getReserveResp(), vcrrReq);

            //Cancel
            SSRequestSender.cancel(scenarios,tuid,httpClient, guid,requestGenerator);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }
}
