package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.PaymentInfoType;
import com.expedia.om.supply.messages.v1.StatusCodeCategoryType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * Created by miawang on 11/9/2016.
 */
public class PreparePurchase {
    Logger logger = Logger.getLogger(getClass());

    SpooferTransport spooferTransport;
    private final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    private DataSource carsInventoryDatasource;
    private DataSource tiDatasource;

    private static final String LOYALTY_NUMBER_HERTZ = "36710865";

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        carsInventoryDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
                SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
                SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

        tiDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER, SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInPreparePurchase() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInPreparePurchaseVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2125248501");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInPreparePurchaseWithCurrencyConvert() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInPreparePurchaseVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.
                getTestScenario(), "2125248501");
    }

    //    CASSS-2125    ConditionalCostList to be returned in xml format
    //    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a
    //                  grand total price that represents the total cost to customer
    private void carRateDetailInPreparePurchaseVerification(String guid, TestScenario scenarios, String tuid)
            throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
        //send OMS Reserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);

       // final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        //System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));

        //PreparePurchase Verification
        final PreparePurshaseVerificationInput preparePurchaseVerificationInput = new PreparePurshaseVerificationInput
                (omRequestGenerate.getPreparePurchaseRequestType(), omRequestGenerate.getPreparePurchaseResponseType());
        VerificationHelper.preparePurchaseBasicVerification(preparePurchaseVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
        VerificationHelper.carRateDetailInPreparePurchaseVerification(preparePurchaseVerificationInput, spooferTransport,
                carsInventoryDatasource, tiDatasource, scenarios, guid, true, logger);
    }

    // CASSS-2798 [CarBS] Special equipment needs to be returned with both POSa and POSu currency
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss2798VerifyOptionListInPreparePurchase() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListVerificationInPreparePurchase(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2798");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss2798VerifyOptionListInPreparePurchaseWithCurrencyConvert() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.optionListVerificationInPreparePurchase(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.
                getTestScenario(), "2798");
    }

    public void optionListVerificationInPreparePurchase(String guid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.getSpecialTestCasesParam().setOptionListSenerio(true);
        //send OMS Reserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);

        //PreparePurchase
        final PreparePurshaseVerificationInput preparePurchaseVerificationInput = new PreparePurshaseVerificationInput
                (omRequestGenerate.getPreparePurchaseRequestType(), omRequestGenerate.getPreparePurchaseResponseType());
        VerificationHelper.preparePurchaseBasicVerification(preparePurchaseVerificationInput, spooferTransport,
               scenarios, guid, false, logger);
        VerificationHelper.optionListInPreparePurchaseVerification(preparePurchaseVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss6710VerifyMerchantReference() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.referenceListInPreparePurchaseTest(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "67101");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss6710VerifyAgencyReference() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.referenceListInPreparePurchaseTest(randomGuid, CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.
                getTestScenario(), "67102");
    }

    public void referenceListInPreparePurchaseTest(String guid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.getSpecialTestCasesParam().setOptionListSenerio(true);
        //send OMS Reserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);

        //PreparePurchase
        final PreparePurshaseVerificationInput preparePurchaseVerificationInput = new PreparePurshaseVerificationInput
                (omRequestGenerate.getPreparePurchaseRequestType(), omRequestGenerate.getPreparePurchaseResponseType());
        VerificationHelper.referenceListInPreparePurchaseVerification(preparePurchaseVerificationInput, carsInventoryDatasource, logger);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs249587tfs315620AmadeusPriceListInPreparePurchaseOnewayCurrency() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.priceListTestInPreparePurchase(randomGuid, CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.
                getTestScenario(), "249587");
    }

    //249588
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs249588AmadeusPriceListInPreparePurchaseRoundtrip() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.priceListTestInPreparePurchase(randomGuid, CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.
                getTestScenario(), "249588");
    }

    public void priceListTestInPreparePurchase(String guid, TestScenario scenarios, String tuid) throws Exception {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.getSpecialTestCasesParam().setOptionListSenerio(true);
        //send OMS Reserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);

        //PreparePurchase
        final PreparePurshaseVerificationInput preparePurchaseVerificationInput = new PreparePurshaseVerificationInput
                (omRequestGenerate.getPreparePurchaseRequestType(), omRequestGenerate.getPreparePurchaseResponseType());
        VerificationHelper.preparePurchaseBasicVerification(preparePurchaseVerificationInput, spooferTransport,
                scenarios, guid, false, logger);
        VerificationHelper.priceListListInPreparePurchaseVerification(preparePurchaseVerificationInput,
                scenarios, guid,  logger);

        //Verify PointOfSupplyCurrencyCode is returned in response
        final CarProductType preparePurchaseCar = omRequestGenerate.getPreparePurchaseResponseType()
                .getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();
        final String posuCurrency = CostPriceCalculator.getCostPosuCurrencyCode(preparePurchaseCar.getCostList(), scenarios.getSupplierCurrencyCode());
        if(!preparePurchaseCar.getPointOfSupplyCurrencyCode().equals(posuCurrency)) {
            Assert.fail(String.format("PointOfSupplyCurrencyCode in preparePurchase response(%s) is not equal to expected value(%s)",
                    preparePurchaseCar.getPointOfSupplyCurrencyCode(), posuCurrency));
        }
        final CarProductType getOrderCar = omRequestGenerate.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation()
                .getCarProduct();
        if(!getOrderCar.getPointOfSupplyCurrencyCode().equals(posuCurrency)) {
            Assert.fail(String.format("PointOfSupplyCurrencyCode in preparePurchase response(%s) is not equal to expected value(%s)",
                    getOrderCar.getPointOfSupplyCurrencyCode(), posuCurrency));
        }


    }

    //281436: CarBS OMS - Reserve - Verify Pickup and DropOff Location - OnAirport - RoundTrip - SpecialEquipment - Egencia site with BillingCode.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281436SpecialEquipmentBillingCode() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.
                getTestScenario(), "281436", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_BillingCode_ZI871131370003_SpecialEquip"), spooferTransport);
        testData.setClientCode("W0DFCJ");
        testData.setSpecialTest("SpecialEquipment");
        CommonUtil.setVendor(testData, "ZI");
        testData.setBillingNumber(RequestDefaultValues.BILLING_NUMBER);
        testData.setRegression(true);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }


    //281432: CarBS OMS - Reserve - Verify Pickup and DropOff Location - OffAirport - OneWay - Egencia site with BillingCode.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281432OffOneWayBillingCode() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_VCE.getTestScenario(),
                "281432", ExecutionHelper.generateNewOrigGUID(spooferTransport), spooferTransport);
        testData.setClientCode("W0DFCJ");
        CommonUtil.setVendor(testData, "ZE");
        testData.setBillingNumber(RequestDefaultValues.BILLING_NUMBER);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        Assert.assertEquals(carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation()
                .getPaymentInfo().getBillingCode(), RequestDefaultValues.BILLING_NUMBER, "Billing number is different in Prepare purchase response, please check");
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
    }

    //281435: CarBS OMS - Reserve - Verify Pickup and DropOff Location - OnAirport - RoundTrip - MulCD - Egencia site with BillingCode.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281449OnRoundTripBillingCode() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_US_Agency_Standalone_RoundTrip_OnAirport_LHR
                .getTestScenario(), "281449", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_BillingCode_ZE871131370003_MultiCD"), spooferTransport);
        testData.setClientCode("W0DFCJ");
        CommonUtil.setVendor(testData, "ZE");
        setCdCode(testData);
        testData.setBillingNumber(RequestDefaultValues.BILLING_NUMBER);
        testData.setRegression(true);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
   }

    private void setCdCode(TestData testData)
    {
        final CarRate carRate = new CarRate();
        carRate.setCdCode("ZE-704005,ZE-676186");
        testData.setCarRate(carRate);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333508AmadeusFRLocationOnAirportCryptoSupportCCGuarantee() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadeus_FR_Crypto_Decrypted_CCGuarantee").build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, "333508", guid);
        testData.setClientCode("W0DFCJ");
        validateCryptoAndEDISupport(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333511AmadeusGBROnAirportCryptoSupportAirplusLodgedCard() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadeus_FR_OffAirport_AccredetiveEDIData").build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, "333511", guid);
        testData.setClientCode("S7JWZD");
        testData.setNeedSpecialCreditCard(true);
        testData.setNeedLoyaltyCard(true);
        ExecutionHelper.setCarRateOfTestData(testData, true, LOYALTY_NUMBER_HERTZ, "");
        validateCryptoAndEDISupport(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333519tfs319094AmadeusCryptoSupportAirplusLodgedCardDescriptiveBillingInfo() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_US_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadeus_FR_OffAirport_AccredetiveEDIData").build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, "333519", guid);
        testData.setClientCode("S7JWZD");
        testData.setNeedSpecialCreditCard(true);
        testData.setNeedDescriptiveBillingInfo(true);
        testData.setNeedLoyaltyCard(true);
        ExecutionHelper.setCarRateOfTestData(testData, true, LOYALTY_NUMBER_HERTZ, "");
        validateCryptoAndEDISupport(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333612AmadeusFRLocationOnAirportCryptoSupportCCGuaranteeBillingCode() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "Amadeus_FR_CryptoDecrypted_CCGuaranteeBillingCode").build(), guid);
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "333612", guid);
        testData.setClientCode("W0DFCJ");
        testData.setBillingNumber(RequestDefaultValues.BILLING_NUMBER);
        validateCryptoAndEDISupport(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs283422AmadeusBillingCodeAndDescriptiveBillingInfo() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "283422", guid);
        testData.setClientCode("W0DFCJ");
        testData.setNeedDescriptiveBillingInfo(true);
        testData.setBillingNumber(RequestDefaultValues.BILLING_NUMBER);
        validateCryptoAndEDISupport(testData);
    }

    //EDI test plan - https://confluence.expedia.biz/display/SSG/Test+plan+for+user+story+439223+-+Egencia+-+Pass+EDI+Data+to+Amadeus+if+received+in+request
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs442662AmadeusEDIWithoutPayment() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "442662", guid);
        testData.setClientCode("W0DFCJ");
        testData.setNeedDescriptiveBillingInfo(true);
        validateCryptoAndEDISupport(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs442669AmadeusEDIWithNormalCC() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "442669", guid);
        testData.setClientCode("W0DFCJ");
        testData.setNeedDescriptiveBillingInfo(true);
        testData.setSpecialTest("CCCard");
        validateCryptoAndEDISupport(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs456139AmadeusEDIWithAirPlus() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "456139", guid);
        testData.setClientCode("W0DFCJ");
        testData.setSpecialTest("CCCard");
        testData.setNeedEDIAndDBIData(true);
        testData.setNeedSpecialCreditCard(true);
        validateCryptoAndEDISupport(testData);
    }

    private void validateCryptoAndEDISupport(TestData testData ) throws Exception {
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(testData);
        final PaymentInfoType responsePayment = omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getPaymentInfo();
        if (!StringUtils.isEmpty(testData.getBillingNumber())) {
            Assert.assertEquals(responsePayment.getBillingCode(), RequestDefaultValues.BILLING_NUMBER,
                    "Billing number is different in Prepare purchase response, please check");
        }
        if (testData.isNeedDescriptiveBillingInfo() || testData.isNeedEDIAndDBIData()) {
            Assert.assertTrue(responsePayment.getDescriptiveBillingInfoList()!=null &&
                    !CollectionUtils.isEmpty(responsePayment.getDescriptiveBillingInfoList().getDescriptiveBillingInfo()),
                    "DescriptiveBillingInfoList should be returned in Prepare purchase response, please check");
        }
        //If request has EDI data then response should have also - key is EDIDATA
        final PaymentInfoType requestPayment = omRequestGenerate.getPreparePurchaseRequestType().getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo();
        if(((testData.isNeedDescriptiveBillingInfo()  && !testData.getNeedSpecialCreditCard()) || testData.isNeedEDIAndDBIData()) && requestPayment.getDescriptiveBillingInfoList().getDescriptiveBillingInfo().get(0)
                .getKey().equals("EDIDATA") && !responsePayment.getDescriptiveBillingInfoList().getDescriptiveBillingInfo().
                get(0).getKey().equals("EDIDATA"))
        {
            Assert.fail("EDI data should be returned in Prepare purchase response, please check");
        }
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs458347WorldspanDetectPriceChange() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "458347", guid);
        testData.setClientCode("ZCS52L");
        testData.setdifferentCostInPreparePurchase(true);
        testPriceChange(testData, guid, "");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs458356MNStandaloneGDSDetectPriceChangeON() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(),
                "458356", guid);
        testData.setClientCode("ZCS52L");
        testData.setdifferentCostInPreparePurchase(true);
        testPriceChange(testData, guid, "Standalone_OnAirport_NonUK");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs458346MNPriceChangeAtDetails() throws Exception {
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(),
                "458346", guid);
        testData.setClientCode("ZCS52L");
        testData.setdifferentPriceInPreparePurchase(true);
        testPriceChange(testData, guid, "");
    }


    private void testPriceChange(TestData testData, String guid, String spooferScenarioName) throws Exception {
        if(StringUtils.isNotBlank(spooferScenarioName)) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenarioName).build(), guid);
        }
        try {
            final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsgByBusinessModelIDAndServiceProviderID(testData);
            if(null != carOMSReqAndRespObj.getCommitPreparePurchaseResponseType()) {
                final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
                CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
                Assert.fail("Prepare purchase should not succeed");
            }

        } catch (Exception exception) {
            Assert.assertTrue(exception.getMessage().contains("Price Change"), "Prepare purchase response doesn't have Price change error");
            Assert.assertTrue(exception.getMessage().contains(StatusCodeCategoryType.BUSINESS_ERROR.value()), "Prepare purchase response doesn't have BUSINESS_ERROR");
        }
    }
}
