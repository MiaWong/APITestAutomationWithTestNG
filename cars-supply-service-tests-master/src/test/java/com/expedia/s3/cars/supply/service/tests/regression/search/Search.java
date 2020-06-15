package com.expedia.s3.cars.supply.service.tests.regression.search;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;
import com.expedia.s3.cars.supply.service.requestgenerators.SSRequestGenerator;
import com.expedia.s3.cars.supply.service.requestsender.SSRequestSender;
import com.expedia.s3.cars.supply.service.verification.PerfMetricsVerifier;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by v-mechen on 11/23/2017.
 */
public class Search {
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

    @Test(groups = {TestGroup.SHOPPING_REGRESSION}, priority = 3)
    public void tfs1055546CarSSErrorXPathInvalidPickup() throws IOException {
        final ErrorHandling errorHandling = new ErrorHandling("BRANCHCURRENTLYUNAVAILABLE", "FieldInvalidError",
                "Invalid pickup location",
                "/search:CarSupplySearchRequest/car:CarSearchCriteriaList/car:CarSearchCriteria/car:CarTransportationSegment/car:StartCarLocationKey");
        testCarSSSearchErrorXPath(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "1055546", errorHandling);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION}, priority = 3)
    public void tfs1055551CarSSErrorXPathInvalidVehicleType() throws IOException {
        final ErrorHandling errorHandling = new ErrorHandling("EC_10493", "FieldInvalidError",
                "Requested vehicle type is invalid",
                "/search:CarSupplySearchRequest/car:CarSearchCriteriaList/car:CarSearchCriteria/car:CarInventoryKey/car:CarCatalogKey/car:CarVehicle");
        testCarSSSearchErrorXPath(CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario(), "1055551", errorHandling);
    }

    public void testCarSSSearchErrorXPath(TestScenario scenarios, String tuid, ErrorHandling errorHandling) {
        try {
            //send search request
            final String guid = UUID.randomUUID().toString();
            //set spoofer for non-Amadeus case
            if(scenarios.getServiceProviderID() != 6) {
                spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "ErrorMap1").build(), guid);
            }
            SSRequestSender.searchWithError(scenarios, tuid, httpClient, guid, errorHandling);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION}, priority = 3)
    public void tfs78125CarSSSearchPerfMetricsStandalone() throws IOException {
        testCarSSSearchPerfMetrics(CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "78125", null);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION}, priority = 3)
    public void tfs78129CarSSSearchPerfMetricsPackage() throws IOException {
        testCarSSSearchPerfMetrics(CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario(), "78129", null);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION}, priority = 3)
    public void tfs78143CarSSSearchPerfMetricsError() throws IOException {
        final ErrorHandling errorHandling = new ErrorHandling("EC_10493", "FieldInvalidError",
                "Requested vehicle type is invalid",
                "/search:CarSupplySearchRequest/car:CarSearchCriteriaList/car:CarSearchCriteria/car:CarInventoryKey/car:CarCatalogKey/car:CarVehicle");
        testCarSSSearchPerfMetrics(CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario(), "78143", errorHandling);
    }

    public void testCarSSSearchPerfMetrics(TestScenario scenarios, String tuid, ErrorHandling errorHandling) {
        try {
            //send search request
            final String guid = UUID.randomUUID().toString();
            SSRequestGenerator requestGenerator = SSRequestSender.search(scenarios, tuid, httpClient, guid);
            if(null != errorHandling) {
                requestGenerator = SSRequestSender.searchWithError(scenarios, tuid, httpClient, guid, errorHandling);
            }

            //Get splunk data
            Thread.sleep(10000);
            final Date endTime = new Date();
            String serviceName = "com.expedia.s3.cars.supply.supplyservice";
            //Handle aws service
            final StringBuilder serviceAddress = new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
            if(serviceAddress.toString().contains("cars-supply-service"))
            {
                serviceName = "cars-supply-service";
            }
            //+ " OriginatingGUID=" + guid
            final String splunkQuery = "index=app ServiceName=" + serviceName + " LogType=PerfMetrics TUID=" + tuid +
                    " earliest=" + (endTime.getTime()/1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
            Logger.getLogger("SSRequestSender").warn(splunkQuery);
            // Splunk host address
            final String hostName = "https://splunklab6";
            // Splunk host port, default value is 8089
            final int hostPort = 8089;
            final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

            //Verify perfMetrics
            PerfMetricsVerifier.verifyPerfMetrics(requestGenerator.getSearchReq().getAuditLogTrackingData(),
                    requestGenerator.getSearchReq().getPointOfSaleKey(), PojoXmlUtil.pojoToDoc(requestGenerator.getSearchResp()),
                    requestGenerator.getSearchResp().getErrorCollectionList(), CommonConstantManager.ActionType.SEARCH,
                    splunkResult) ;

        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
    }
}
