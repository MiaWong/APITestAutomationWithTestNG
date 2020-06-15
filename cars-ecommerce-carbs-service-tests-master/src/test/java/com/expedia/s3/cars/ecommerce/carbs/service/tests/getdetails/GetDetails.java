package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification.VerifyPriceListInGetDetailsResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ARIARsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetDetails
{
    Logger logger = Logger.getLogger(getClass());

    @SuppressWarnings("CPD-START")
    SpooferTransport spooferTransport;
    private final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    private DataSource carsInventoryDatasource;
    private DataSource titaniumDatasource;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception
    {
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        carsInventoryDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
                SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
                SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

        titaniumDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER, SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }

    @AfterClass
    public void tearDown() throws Exception
    {
        httpClient.stop();
    }

    @SuppressWarnings("CPD-END")

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInGetDetails() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInGetDetailsVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2125248501");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInGetDetailsWithCurrencyConvert() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInGetDetailsVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.
                getTestScenario(), "2125248501");
    }

    //    CASSS-2125    ConditionalCostList to be returned in xml format
    //    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
    private void carRateDetailInGetDetailsVerification(String guid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);

        //send search + getCostAndAvailability + getDetail Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData,
                spooferTransport, carsInventoryDatasource, logger);

//        Document spooferDoc = spooferTransport.retrieveRecords(guid);
//        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));

        //getDetail Verification
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput
                (requestGenerator.getGetDetailsRequestType(), requestGenerator.getGetDetailsResponseType());
        VerificationHelper.carRateDetailInGetDetailsVerification(getDetailsVerificationInput, spooferTransport,
                carsInventoryDatasource, titaniumDatasource, scenarios, guid, true, logger);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInGetDetailsForAgencyExtraHourPrice61544() throws IOException, DataAccessException
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ExtraHourPrice");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario(), "212561544", CommonEnumManager.TimeDuration.Days2, true, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInGetDetailsForAgencyWeeklyPrice61545() throws IOException, DataAccessException
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListAgencyWeeklyPrice");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "212561545", CommonEnumManager.TimeDuration.WeeklyDays7, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInGetDetailsForAgencyMonthlyExtraDayPrice61548() throws IOException, DataAccessException
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "PriceListMonthlyExtraDayPrice");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario(), "212561548", CommonEnumManager.TimeDuration.MounthlyExtDays35, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInGetDetailsForGDSPDailyPrice61552() throws IOException, DataAccessException
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport , "WSCS_EUR_Daily");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway.getTestScenario(), "212561552", CommonEnumManager.TimeDuration.Days2, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyPriceListInGetDetailsForGDSPWeeklyPrice61553() throws IOException, DataAccessException
    {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "WSCS_GDSP_Weekly_Price");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OneWay_OnAirport.getTestScenario(), "212561553", CommonEnumManager.TimeDuration.WeeklyDays7, false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176697AmadeusOffAirportCurrencyConversationCheckTotalPrice() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_Off_ESP_Multilocation");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "176697", CommonEnumManager.TimeDuration.Days2, false, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176700AmadeusStandaloneOneWayCheckTotalPrice() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_GBR_Standalone_OneWay_OnAirport_Weekly");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(), "176700", CommonEnumManager.TimeDuration.Days2, false, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs181526AmadeusStandaloneRoundTripDailyExtraHoursCarCheckTotalPrice() throws IOException, DataAccessException {
        final String randomGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_OnAirport_RoundTrip_ExtraHour");
        this.carPriceListInGetDetailsVerification(randomGuid, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "181526", CommonEnumManager.TimeDuration.Daily, true, true);
    }

    private void carPriceListInGetDetailsVerification(String guid, TestScenario scenarios, String tuid, CommonEnumManager.TimeDuration timeDuration, boolean extraHours, boolean compareTotalPrice) throws IOException, DataAccessException {

        final TestData testData = new TestData(httpClient, timeDuration, scenarios, tuid, guid, extraHours);
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData, spooferTransport, carsInventoryDatasource, logger);

        //priceList Verification for GetDetails
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(requestGenerator.getGetDetailsRequestType(), requestGenerator.getGetDetailsResponseType());
        VerificationHelper.priceListInGetDetailsVerification(getDetailsVerificationInput, spooferTransport, scenarios, guid, false, logger);
        if (compareTotalPrice) {
            final ArrayList<String> errorMsg = new ArrayList<>();
            TotalPriceVerifier.verifyTotalPriceEqual(requestGenerator.getSelectedCarProduct(), getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct().get(0), testData.getScenarios().getSupplierCurrencyCode(), errorMsg, false);
            Assert.assertTrue(CompareUtil.isObjEmpty(errorMsg), "errorMsg list is not empty, please check: " + errorMsg.toString());
        }
    }


    // CASSS-2798 [CarBS] Special equipment needs to be returned with both POSa and POSu currency
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2798VerifyOptionListInGetDetails() throws IOException, DataAccessException
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListInGetDetailsVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "2798021");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2798VerifyOptionListInGetDetailsWithCurrencyConvert() throws IOException, DataAccessException
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListInGetDetailsVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(), "2798022");
    }

    public void optionListInGetDetailsVerification(String guid, TestScenario scenarios, String tuid) throws IOException, DataAccessException
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //send search + getCostAndAvailability + getDetail Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData, spooferTransport,
                carsInventoryDatasource, logger);

        //getDetail Verification
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput
                (requestGenerator.getGetDetailsRequestType(), requestGenerator.getGetDetailsResponseType());
        VerificationHelper.optionListInGetDetailsVerification(getDetailsVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs150134MNGetDetailsGDSPPackageOnAirportTotalPrice() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario();
        priceAndTermsConditionInMNGetDetails(testScenario, "Package_OnAirport_NonUK", "150134", false, false);
    }

   @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs150132MNGetDetailsGDSPStandaloneOnAirportTotalPrice() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario();
        priceAndTermsConditionInMNGetDetails(testScenario, "Standalone_OnAirport_NonUK", "150132", false, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs236192MNGetDetailsDynamicCommissionForPackageOnAirport() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario();
        priceAndTermsConditionInMNGetDetails(testScenario, "", "236192", true, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs206740MNGetDetailsTermsAndConditionsStandaloneOnAirport() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_UK_GDSP_Standalone_nonUKLocation_OnAirport.getTestScenario();
        priceAndTermsConditionInMNGetDetails(testScenario, "Standalone_OnAirport_NonUK_NoBaseRate", "206740", false, true);
    }

    private void priceAndTermsConditionInMNGetDetails(TestScenario testScenario, String scenarioName, String tuid, boolean isCommission, boolean isTermsCondition) throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), randomGuid);
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        testData.setForceDownstream(true);
        //send search + getDetail Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData, spooferTransport, carsInventoryDatasource, logger);
        if (isCommission) {
            final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(testData.getGuid()), testData.getGuid(), testScenario);
            final VRRRsp vrrRsp = new VRRRsp(basicVerificationContext.getSpooferTransactions().getElementsByTagName("VehRateRuleRS").item(0), new CarsSCSDataSource(DatasourceHelper.getMicronNexusDatasource()));
            //Verify Commission in GetDetails response
            VerifyPriceListInGetDetailsResponse.verifyDynamicCommissionInGetDetails(requestGenerator.getGetDetailsResponseType(), vrrRsp);
        } else if (isTermsCondition) {
            //verify terms and conditions of MN getDetails
            verifyTermsAndConditionsInGetDetails(requestGenerator.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0));
        } else {
            //verify the Total amount of PriceList and CostList
            VerifyPriceListInGetDetailsResponse.verifyTotalOfPriceListAndCostList(requestGenerator);
        }
    }

    private void verifyTermsAndConditionsInGetDetails(CarProductType carProduct) {
        for (final CarPolicyType carPolicy : carProduct.getCarPolicyList().getCarPolicy()) {
            if (carPolicy.getCarPolicyCategoryCode().equals("MerchantRules")) {
                Assert.assertTrue(carPolicy.getCarPolicyRawText().contains("Cancellation free of charge"), "Terms and conditions is not as expected.");
            }
        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void  tfs181529CarBSPIIDTotalPriceSameBetweenSearchGetDetailResponse() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "181529", PojoXmlUtil.getRandomGuid());

        testGetDetail(testData, "TotalPrice");
    }


    public void testGetDetail(TestData testData, String verifyType) throws IOException, DataAccessException {

        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        final CarProductType carProductType =  CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);
        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);

       final List remark = new ArrayList<>();
        if("TotalPrice".equals(verifyType))
        {
            TotalPriceVerifier.verifyTotalPriceEqual(carProductType, getDetailsResponseType.getCarProductList().getCarProduct().get(0), testData.getScenarios().getSupplierCurrencyCode(), remark, false);
        }
        if("PhoneList".equals(verifyType))
        {
            final Document spooferTransactions = spooferTransport.retrieveRecords(testData.getGuid());
            final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(), testData.getScenarios());
            final Node ariaRspNode = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(getDetailsVerificationContext, CommonConstantManager.ActionType.GETDETAILS, GDSMsgNodeTags.AmadeusNodeTags.ARIA_CAR_GET_DETAIL_RESPONSE_TYPE);
            final ARIARsp ariaRsp = new ARIARsp(ariaRspNode, new CarsSCSDataSource(DatasourceHelper.getAmadeusSCSDatasource()), getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarInventoryKey());
            isPhoneNumberEqual(ariaRsp.getCar().getCarPickupLocation(), getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarPickupLocation(), remark);
            if (ariaRsp.getCar().getCarDropOffLocation() != null)
            {
                isPhoneNumberEqual(ariaRsp.getCar().getCarDropOffLocation(), getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarDropOffLocation(), remark);

            }

        }
         if (CollectionUtils.isNotEmpty(remark))
        {
            Assert.fail(remark.toString());
        }
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void  tfs455526PhoneNumberGetDetailResponse() throws Exception
    {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "455526", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        testGetDetail(testData, "PhoneList");
    }

    private static void isPhoneNumberEqual(CarLocationType expectedLocation, CarLocationType actualLocation, List errorMsg)
    {
        //If expect and actula phoneList is NULL or count is 0, don't continue
        if ((null == expectedLocation.getPhoneList() || CollectionUtils.isEmpty(expectedLocation.getPhoneList().getPhone()))
                && (null == actualLocation.getPhoneList() || CollectionUtils.isEmpty(actualLocation.getPhoneList().getPhone())))
        {
            return;
        }
        //If count is not equal, return the error
        if (expectedLocation.getPhoneList().getPhone().size() != actualLocation.getPhoneList().getPhone().size())
        {
            errorMsg.add("Phone count is not expected, expect: " + expectedLocation.getPhoneList().getPhone().size() + ", actual: "+ actualLocation.getPhoneList().getPhone().size() +"!\r\n");
        }

        //Compare the phone number
        if (!expectedLocation.getPhoneList().getPhone().get(0).getPhoneNumber().equals(actualLocation.getPhoneList().getPhone().get(0).getPhoneNumber()))
        {
            errorMsg.add("Phone number is not expected, expect: " + expectedLocation.getPhoneList().getPhone().get(0).getPhoneNumber() + ", actual: " + actualLocation.getPhoneList().getPhone().get(0).getPhoneNumber() + "!\r\n");
        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs184968CarBSGetDetailsErrorHandlingRateCodeInvalid() throws IOException, DataAccessException
    {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "184968", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        testCarBSGetDetailsInvalide(testData);
    }


    public void testCarBSGetDetailsInvalide(TestData testData) throws IOException, DataAccessException {

        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        final CarProductType carProductType =  CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carProductType.setCarProductToken("aaacb");
        carProductType.getCarInventoryKey().getCarRate().setRateCode("XXX**");
        carProductType.getCarInventoryKey().getCarRate().setCarRateQualifierCode("XXX**");
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);

        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);

        if(!(null != getDetailsResponseType.getDetailsErrorCollection() && null != getDetailsResponseType.getDetailsErrorCollection().getFieldInvalidErrorList()
                && CollectionUtils.isNotEmpty(getDetailsResponseType.getDetailsErrorCollection().getFieldInvalidErrorList().getFieldInvalidError())
                && getDetailsResponseType.getDetailsErrorCollection().getFieldInvalidErrorList().getFieldInvalidError().get(0).getDescriptionRawText()
                .contains("RateCode is empty or contains invalid")))
        {
            Assert.fail("No expected errorHandling message returned in carBS ressponse.");
        }

    }

}
