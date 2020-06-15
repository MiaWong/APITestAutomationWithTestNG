package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.DriverAgeVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.SearchResponsesNotEmptyVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.PropertyResetHelper;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Created by mpaudel on 5/18/16.
 */
public class Search extends SuiteContext
{
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    public static final String SCENARIO_YOUNG_DRIVER_FEES = "GBR_Standalone_RoundTrip_OnAirport_With_Young_Driver_fees";

    private void testSearchDriverAge(TestScenario scenarios, String tuid, String guid, long driverAge, boolean isrequiredsRetrieveRecord) throws  Exception
    {
        final SearchVerificationInput verificationInput = SearchHelper.search(httpClient, scenarios, tuid, guid, driverAge,carSCSDatasource);
        SearchHelper.searchVerificationDriverAge( verificationInput, spooferTransport, scenarios, guid, logger, isrequiredsRetrieveRecord);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss1459SearchValidDriverAge() throws  Exception
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUIDwithOverrideTemplate(spooferTransport, "VehAvailRateRsWithYoungDriverSurcharge");
        final TestScenario scenario = new TestScenario(SCENARIO_YOUNG_DRIVER_FEES, "GBR", "10111", "1050", "GBP", "CDG", "CDG", true, PurchaseType.CarOnly, 1, 7);  // TODO temp
        final TestData testData = new TestData(httpClient, scenario, "145926", randomGuid);
        CarSupplyConnectivitySearchRequestType request = createSearchRequest(testData);

        // setting DrvierAge=26
        request = PropertyResetHelper.setDriverAge(request, 26L);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, randomGuid);

        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(randomGuid), randomGuid, scenario);

        //basic verification causes assertion errors on failure... so no need to check the result
        final SearchResponsesNotEmptyVerification verifier = new SearchResponsesNotEmptyVerification();
        verifier.verify(verificationInput, verificationContext);

        //the basic verification passed; now check driver age
        final IVerification.VerificationResult result = new DriverAgeVerification().verify(verificationInput, verificationContext);

        logger.info("Verification run: " + result);
        if(! result.isPassed())
        {
            Assert.fail(result.toString());
        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION}, description = "Car search valid driver age (26), with dynamic spoofed templates.")
    public void casss1459SearchValidDriverAgeDynamic() throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "145900", randomGuid);
        CarSupplyConnectivitySearchRequestType request = createSearchRequest(testData);

        // setting DrvierAge=26
        request = PropertyResetHelper.setDriverAge(request, 26L);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, randomGuid);

        //logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        //logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(randomGuid), randomGuid, scenario);

        //basic verification causes assertion errors on failure... so no need to check the result
        final SearchResponsesNotEmptyVerification verifier = new SearchResponsesNotEmptyVerification();
        verifier.verify(verificationInput, verificationContext);

        //the basic verification passed; now check driver age
        final IVerification.VerificationResult result = new DriverAgeVerification().verify(verificationInput, verificationContext);

        //logger.info("Verification run: " + result);
        if(! result.isPassed())
        {
            Assert.fail(result.toString());
        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss1459SearchWithoutDriverAge() throws  Exception
    {
        testSearchDriverAge(CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS.getTestScenario(), "145902", ExecutionHelper.generateNewOrigGUID(spooferTransport), -1L, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss1459SearchInvalidDriverAge() throws  Exception
    {
        testSearchDriverAge(CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS.getTestScenario(), "145903", ExecutionHelper.generateNewOrigGUID(spooferTransport), 17L, false);
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076632SearchTIMapOnairportRoundtrip() throws  Exception
    {
        //Create a GUID
        //Set ScenarioName override
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "TSCS_GBP");
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1076632", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days2);
        testTISearchMap(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076626SearchTIMapOnairportOneway() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_FRA_Standalone_OneWay_OnAirport_CDG.getTestScenario(),
                "1076626", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testTISearchMap(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076630SearchTIMapOffairportOffairport() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR.getTestScenario(),
                "1076630", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testTISearchMap(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1082780SearchTIMapPackage() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario(),
                "1082780", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);
        testTISearchMap(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076627SearchTIMapOffairportOneway() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(),
                "1076627", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testTISearchMap(testData);
    }

    //1076628 OnAirport to Offairport, limited Mileage
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076628SearchTIMapOnToOffLimitMileage() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_LimitMileage").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR.getTestScenario(),
                "1076628", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);

        testTISearchMap(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076629SearchTIMapOffToOn() throws Exception
    {
        //Create a GUID
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_LimitMileage").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR.getTestScenario(),
                "1076629", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testTISearchMap(testData);
    }

     private void testTISearchMap(TestData testData) throws Exception {
         //Create request based on testata
         final SCSSearchRequestGenerator requestGenerator = new SCSSearchRequestGenerator(carSCSDatasource);
         final CarSupplyConnectivitySearchRequestType request = requestGenerator.createSearchRequest(testData);

         //Send search request
         final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                 SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());
         //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
         //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
         //Do BVT verification
         //SearchHelper.searchVerification( searchVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger, false);
         //Verify GDS map
         SearchHelper.searchGDSMapVerification(searchVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger);
     }

    //Error handling
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1078363SearchTIErrorMap() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CostAvailError30").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1078363", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days2);
        testTIErrorMap(testData);
    }

    private void testTIErrorMap(TestData testData) throws  Exception {
        //Create request based on testata
        final SCSSearchRequestGenerator requestGenerator = new SCSSearchRequestGenerator(carSCSDatasource);
        final CarSupplyConnectivitySearchRequestType request = requestGenerator.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

     // System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
      //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));


        //Verify Error map
        SearchHelper.errorMapVerification(searchVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger);
    }

}