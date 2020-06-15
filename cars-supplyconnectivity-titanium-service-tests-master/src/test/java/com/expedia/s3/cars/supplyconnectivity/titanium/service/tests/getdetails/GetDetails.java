package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetDetails extends SuiteContext
{
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    //==========================================================================================================
    //  coommon flow for GetDetails
    //==========================================================================================================
    private void testGetDetails(TestScenario scenarios,
                                String tuid,
                                String guid,
                                long driverAge) throws Exception
    {

        testGetDetails(scenarios, tuid, guid, driverAge, true, 0L, 0L, 0L, 0L, 0L);
    }

    //  with car filter
    private void testGetDetails(TestScenario scenarios,
                                String tuid,
                                String guid,
                                long driverAge,         //  if not -1, driver age will get set
                                boolean isRandomPick,   //  if false, car prop will be set to the following 5 argument
                                long vendorSupplierID,
                                long carCategoryCode,
                                long carTypeCode,
                                long carTransmissionDriveCode,
                                long carFuelACCode) throws  Exception
    {
        final SearchVerificationInput searchVerificationInput = SearchHelper.search(httpClient,
                scenarios,
                tuid,
                guid,
                driverAge,
                vendorSupplierID,
                carCategoryCode,
                carTypeCode,
                carTransmissionDriveCode,
                carFuelACCode,
                carSCSDatasource);

        final String newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        final GetDetailsVerificationInput getDetailsVerificationInput =
                GetDetailsHelper.getDetails(httpClient,
                                            requestGenerator,
                                            newGuid,
                                            isRandomPick,
                                            vendorSupplierID,
                                            carCategoryCode,
                                            carTypeCode,
                                            carTransmissionDriveCode,
                                            carFuelACCode);
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));

        //  getDetails verification
        GetDetailsHelper.getDetailsConditionalCostListVerification(  getDetailsVerificationInput,
                spooferTransport,
                scenarios,
                newGuid,
                logger);

    }

    //  filter version for conditionalCost
    private void testGetDetails4ConditionalCostList(TestScenario scenarios,
                                String tuid,
                                String guid) throws  Exception
    {
        final SearchVerificationInput searchVerificationInput = SearchHelper.search( httpClient,
                                                                                        scenarios,
                                                                                        tuid,
                                                                                        guid,
                                                                                        -1,
                                                                                          carSCSDatasource);


        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        final String newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator, newGuid);

        GetDetailsHelper.getDetailsConditionalCostListVerification(  getDetailsVerificationInput,
                                                                    spooferTransport,
                                                                    scenarios,
                                                                    newGuid,
                                                                    logger);

    }

    //  filter version for conditionalCost
    private void testGetDetails4PhoneList(TestScenario scenarios,
                                          String tuid,
                                          String guid) throws  Exception
    {
        final SearchVerificationInput searchVerificationInput = SearchHelper.search( httpClient,
                                                                                        scenarios,
                                                                                        tuid,
                                                                                        guid,
                                                                                        -1,
                carSCSDatasource);


        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        final String newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator, newGuid);

        GetDetailsHelper.getDetailsPhoneListVerification(getDetailsVerificationInput,
                                                        spooferTransport,
                                                        scenarios,
                                                        newGuid,
                                                        logger);

    }
    //==========================================================================================================
    //  E2E cert test cases
    //==========================================================================================================
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss1896GetDetailValidDriverAge() throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        testGetDetails( CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(), "1896", randomGuid, 26L);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896GetDetailValidDriverAgeTestHook(@Optional("15") long vendorSupplierID,
                                                 @Optional("10") long carCategoryCode,
                                                 @Optional("4") long carTypeCode,
                                                 @Optional("1") long carTransmissionDriveCode,
                                                 @Optional("1") long carFuelACCode) throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testGetDetails( CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                "1896",
                randomGuid,
                26L,
                false,
                vendorSupplierID,
                carCategoryCode,
                carTypeCode,
                carTransmissionDriveCode,
                carFuelACCode);
    }

    //  get details for conditonal cost list
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss2125GetDetailsConditionalCostList() throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        testGetDetails4ConditionalCostList( CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS.getTestScenario(), "2125", randomGuid);
    }

    //  get details for phone list
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss2250GetDetailsPhoneList() throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        testGetDetails4PhoneList( CommonScenarios.GBR_Standalone_RoundTrip_OnAirport_TiSCS.getTestScenario(), "2250", randomGuid);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    //OnAirport, roundtrip, no currency conversation, weekly
    public void tfs1076665DetailsTIMapOnairportRoundtrip() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "1076665", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testTIDetailsMap(testData, "TSCS_GBP", false);
    }

    //OffAirport, oneway, currency conversation, daily
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076659DetailsTIMapOffairportOneway() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(),
                "1076659", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days2);
        testTIDetailsMap(testData, "TSCS_CarModelGuaranteed", false);
    }

    //TSCS_LimitMileage
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1076673DetailsTIMapOnairportMileage() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_LimitMileage").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1076673", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days2);
        testTIDetailsMap(testData, "TSCS_LimitMileage", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    //OnAirport, roundtrip, no currency conversation, weekly
    public void tfs1077840DetailsTIErrorMap() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_DetailError16").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "1077840", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testTIDetailsMap(testData, "TSCS_DetailError16", true);
    }

    private void testTIDetailsMap(TestData testData, String spooferOverrideName, boolean forErrorMap) throws Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //GetDetails with new GUID
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final String newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferOverrideName).build(), newGuid);
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator, newGuid);

        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        //Do BVT verification
        //GetDetailsHelper.getDetailsVerification(getDetailsVerificationInput, spooferTransport, testData.getScenarios(), newGuid, logger, false);

        //Verify GDS map or error map
        if(forErrorMap)
        {
            GetDetailsHelper.detailsErrorMapVerification(getDetailsVerificationInput, spooferTransport,
                    testData.getScenarios(), newGuid, logger);
        }
        else {
            GetDetailsHelper.detailsGDSMapVerification(getDetailsVerificationInput, spooferTransport,
                    testData.getScenarios(), newGuid, logger);
        }

    }




}
