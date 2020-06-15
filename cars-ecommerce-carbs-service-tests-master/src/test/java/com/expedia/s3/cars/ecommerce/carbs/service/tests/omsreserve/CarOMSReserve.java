package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.messagetypes.defn.v4.MessageInfoType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SuiteContext;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveErrorHandlingRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.CarOMSReserveVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.OmCancelVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn
        .v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn
        .v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestDataErrHandle;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter
        .TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.StatusCodeCategoryType;
import org.junit.Assert;
import org.springframework.util.StringUtils;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yyang4 on 11/9/2016.
 */
@SuppressWarnings("PMD")
public class CarOMSReserve extends SuiteContext
{
    final private DataSource carsInventoryDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
            SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME,
            SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    private static final String REFERENCE_SETTINGNAME = "GetDetails.referencePricing/enable";

    private static final String setPosConfigUrl = AppConfig.resolveStringValue("${setPosConfig.server.address}");

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void cassFailoverTestFeatureOff874001() throws Exception
    {
         /*scenario 1
         pos config:
         Booking.fallbackCatalogReserveRetry/enable=0
         Booking.fallbackCatalogReserveRetry/1.value=0
         client config:
         reserveSoftErrorHandling.retry/Enable=1
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=1
         Booking.softErrorHandling.retry/enable=1
         1,verify if get two VCRR response from crslog.
         2.verify the PreparePurchaseResponse booking sucess and contaied soft error.*/
        final TestScenario testScenario1 = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874001";
        //set feature off
        setPosConfigFailover(tuid, "0", "0", "200000", "0");

        //send request and verify
        failoverFeatureOffTestMethod("874001", CarCommonEnumManager.ClientID.ClientID_1.getValue(), testScenario1);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION} ,dependsOnMethods="cassFailoverTestFeatureOff874001", alwaysRun = true)
    public void cassFailoverTestFeatureOff874002() throws Exception
    {
           /*scenario 2
         pos config:
         Booking.fallbackCatalogReserveRetry/enable=0
         Booking.fallbackCatalogReserveRetry/1.value=142
         client config:
         reserveSoftErrorHandling.retry/Enable=1
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=1
         Booking.softErrorHandling.retry/enable=1
         1,verify if get two VCRR response from crslog.
         2.verify the PreparePurchaseResponse booking sucess and contaied soft error.*/
        final TestScenario testScenario2 = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874002";
        //set feature off
        setPosConfigFailover(tuid, "0", "142", "200000", "0");

        //send request and verify
        failoverFeatureOffTestMethod("874002", CarCommonEnumManager.ClientID.ClientID_1.getValue(), testScenario2);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION} ,dependsOnMethods="cassFailoverTestFeatureOff874002", alwaysRun = true)
    public void cassFailoverTestFeatureOff874003() throws Exception
    {

         /*scenario 3
         pos config:
         Booking.fallbackCatalogReserveRetry/enable=1
         Booking.fallbackCatalogReserveRetry/1.value=0
         client config:
         reserveSoftErrorHandling.retry/Enable=1
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=1
         Booking.softErrorHandling.retry/enable=1
         1,verify if get two VCRR response from crslog.
         2.verify the PreparePurchaseResponse booking sucess and contaied soft error.*/
        final TestScenario testScenario3 = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874003";
        //set feature off
        setPosConfigFailover(tuid, "1", "0", "200000", "0");

        //send request and verify
        failoverFeatureOffTestMethod("874003", CarCommonEnumManager.ClientID.ClientID_1.getValue(), testScenario3);


    }
    /* NOTE: Field invalid error can't make failover
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void cassFailoverTestFeatureOn874004() throws Exception
    {
        *//*scenario 4
         pos config:
         Booking.fallbackCatalogRetry/enable=1
         Booking.fallbackCatalogRetry/1.value=142
         client config:
         reserveSoftErrorHandling.retry/Enable=1
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=1
         Booking.softErrorHandling.retry/enable=1
         1,verify if get two VCRR response from crslog.
         2,verify if get ACAQ and ACSQ request from crslog.
         3,verify PreparePurchaseResponse  booking success and the car come from amadeus
         4.verify car in retrieve response is Amadeus car
         5.verify cancel request is sent to Amadeus APCQ*//*
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874004";

        //send request and verify
        TestDataErrHandle errHandle = new TestDataErrHandle(String.valueOf(CarCommonEnumManager.InValidFildType.InvalidCDCode), "EM1663AAAAaaaaaaaaaaaaaaaaaaaaa", "Invalid CorporateDiscountCode");
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        testData.setErrHandle(errHandle);

        //set a specific vendor  to keep  retry search successful for amdeus car.(cause some othere vendor  not have amadeus car)
        setSpecificVendorID(testData);

        failoverFeatureOnTestMethod(CarCommonEnumManager.ClientID.ClientID_1.getValue(), testScenario, 2, testData);
    }*/

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods="cassFailoverTestFeatureOff874003", alwaysRun = true)
    public void cassFailoverTestFeatureOn874005() throws Exception
    {
        /*scenario 5
         pos config:
         Booking.fallbackCatalogRetry/enable=1
         Booking.fallbackCatalogRetry/1.value=110
         client config:
         reserveSoftErrorHandling.retry/Enable=0
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=0
         Booking.softErrorHandling.retry/enable=0
         1,verify if get one VCRR response from crslog.
         2,verify if get ACAQ and ACSQ request from crslog.
         3,verify PreparePurchaseResponse  booking success and the car come from amadeus.
         4.verify car in retrieve response is Amadeus car
         5.verify cancel request is sent to Amadeus APCQ*/
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874005";
        //set feature on
        setPosConfigFailover(tuid, "1", "110", "20000", "0");

        //send request and verify
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);

        //set a specific vendor  to keep  retry search successful for amdeus car.(cause some othere vendor  not have amadeus car)
        setSpecificVendorID(testData);

        failoverFeatureOnTestMethod(CarCommonEnumManager.ClientID.ClientID_2.getValue(), testScenario, 1, testData);
    }

    private void setSpecificVendorID(TestData testData) {
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorSupplierID(41l);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods="cassFailoverTestFeatureOn874005", alwaysRun = true)
    public void cassFailoverTestFeatureOn874007() throws Exception
    {
        /*scenario 7 specail equipment(suppressSpecialEquipmentRequest/enable=0 )
         pos config:
         Booking.fallbackCatalogRetry/enable=1
         Booking.fallbackCatalogRetry/1.value=110
         Booking.fallbackCatalogRetry.suppressSpecialEquipmentRequest/enable=0
         client config :
         reserveSoftErrorHandling.retry/Enable=0
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=0
         Booking.softErrorHandling.retry/enable=0
         1,verify if get one VCRR response from crslog.
         2,verify if get ACAQ and ACSQ request from crslog.
         3,verify PreparePurchaseResponse  booking success and the car come from amadeus.
         4.verify if car special equipment info is sent to amades CCBR(Booking.fallbackCatalogRetry.suppressSpecialEquipmentRequest/enable=0)
         5.verify car in retrieve response is Amadeus car
         6.verify cancel request is sent to Amadeus APCQ*/
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874007";
        //set feature on
        setPosConfigFailover(tuid, "1", "110", "20000", "0");

        //send request and verify
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        //set a specific vendor  to keep  retry search successful for amdeus car.(cause some other vendor  not have amadeus car)
        setSpecificVendorID(testData);

        //set specail equipment code
        testData.setCarSpecialEquipmentCode(RequestDefaultValues.CAR_SPECIAL_EQUIPMENT_CODE_CSI);
        failoverFeatureOnTestMethod(CarCommonEnumManager.ClientID.ClientID_2.getValue(), testScenario, 1, testData);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods="cassFailoverTestFeatureOn874007", alwaysRun = true)
    public void cassFailoverTestFeatureOn874008() throws Exception
    {
        /*scenario 7 specail equipment(suppressSpecialEquipmentRequest/enable=1 )
         pos config:
         Booking.fallbackCatalogRetry/enable=1
         Booking.fallbackCatalogRetry/1.value=110
         Booking.fallbackCatalogRetry.suppressSpecialEquipmentRequest/enable=1
         client config :
         reserveSoftErrorHandling.retry/Enable=0
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=0
         Booking.softErrorHandling.retry/enable=0
         1,verify if get one VCRR response from crslog.
         2,verify if get ACAQ and ACSQ request from crslog.
         3,verify PreparePurchaseResponse  booking success and the car come from amadeus.
         4.verify if car special equipment info is not sent to amades CCBR(Booking.fallbackCatalogRetry.suppressSpecialEquipmentRequest/enable=1)
         5.verify car in retrieve response is Amadeus car
         6.verify cancel request is sent to Amadeus APCQ*/
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874008";
        //set feature on
        setPosConfigFailover(tuid, "1", "110", "20000", "1");

        //send request and verify
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        //set a specific vendor  to keep  retry search successful for amdeus car.(cause some othere vendor  not have amadeus car)
        setSpecificVendorID(testData);

        //set specail equipment code
        testData.setCarSpecialEquipmentCode(RequestDefaultValues.CAR_SPECIAL_EQUIPMENT_CODE_CSI);
        failoverFeatureOnTestMethod(CarCommonEnumManager.ClientID.ClientID_2.getValue(), testScenario, 1, testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods="cassFailoverTestFeatureOn874008", alwaysRun = true)
    public void cassFailoverTestFeatureOn874009() throws Exception
    {
        /*scenario 8 CC gurantee
         pos config:
         Booking.fallbackCatalogRetry/enable=1
         Booking.fallbackCatalogRetry/1.value=110
         client config : client code =3
         reserveSoftErrorHandling.retry/Enable=0
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=0
         Booking.softErrorHandling.retry/enable=0
         Booking.forceCreditCardDownstream/enable =1
         1,verify if get one VCRR response from crslog.
         2,verify if get ACAQ and ACSQ request from crslog.
         3,verify PreparePurchaseResponse  booking success and the car come from amadeus.
         4.verify if credit card payment info is sent to amades CCBR(Booking.forceCreditCardDownstream/enable =1)
         5.verify car in retrieve response is Amadeus car
         6.verify cancel request is sent to Amadeus APCQ*/
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874009";
        //set feature on
        setPosConfigFailover(tuid, "1", "110", "20000", "0");

        //send request and verify
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        //set a specific vendor  to keep  retry search successful for amdeus car.(cause some othere vendor  not have amadeus car)
        setSpecificVendorID(testData);

        testData.setSpecialTest("CCCard");
        failoverFeatureOnTestMethod(CarCommonEnumManager.ClientID.ClientID_3.getValue(), testScenario, 1, testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods="cassFailoverTestFeatureOn874009", alwaysRun = true)
    public void cassFailoverTestFeatureOn874010() throws Exception
    {
        /*scenario 9 Rate on request booking
         pos config:
         Booking.fallbackCatalogRetry/enable=1
         Booking.fallbackCatalogRetry/1.value=110
         client config:
         reserveSoftErrorHandling.retry/Enable=0
         reserveSoftErrorHandling/PreparePurchase.returnSoftErrors/Enable=0
         Booking.softErrorHandling.retry/enable=0
         1,verify if  PreparePurchaseResponse  booking status return not "booked" error.
         2,no Amades search request send out .*/
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final String tuid = "874010";
        final String randomGuid = "d60b4bb3-cde9-4d35-bc24-2922cd46f573";//the spoofer template for this guid will make the copp response retuned "reserved" not "booked"
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        //set feature on
        setPosConfigFailover(tuid, "1", "110", "200000", "0");
        //send request and verify
        DataSource carBSdataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        CarBSHelper carBSHelper = new CarBSHelper(carBSdataSource);
        String clientCode = carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_2.getValue()) == null ? "" : carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_2.getValue()).get(0).getClientCode();
        //booking
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        //set a specific vendor  to keep  retry search successful for amdeus car.(cause some othere vendor  not have amadeus car)
        setSpecificVendorID(testData);

        //set specail equipment code
        testData.setCarSpecialEquipmentCode(RequestDefaultValues.CAR_SPECIAL_EQUIPMENT_CODE_CSI);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveErrorHandlingRequestSender.oMSReserveSendWithShopMsg(testScenario, carsInventoryDatasource, randomGuid, httpClient, testData, clientCode, spooferTransport);
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));

    }

    public void failoverFeatureOffTestMethod(String tuid, String clientId, TestScenario testScenario) throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "WSCS_FAILOVER_OFF").build(), randomGuid);

        final DataSource carBSdataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        final CarBSHelper carBSHelper = new CarBSHelper(carBSdataSource);
        final String clientCode = carBSHelper.getClientListById(clientId) == null ? "" : carBSHelper.getClientListById(clientId).get(0).getClientCode();
        //booking
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        final TestDataErrHandle errHandle = new TestDataErrHandle(String.valueOf(CarCommonEnumManager.InValidFildType.InvalidCDCode), "EM1663AAAAaaaaaaaaaaaaaaaaaaaaa", "Invalid CorporateDiscountCode");
        testData.setErrHandle(errHandle);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveErrorHandlingRequestSender.oMSReserveSendWithShopMsg(testScenario, carsInventoryDatasource, randomGuid, httpClient, testData, clientCode, spooferTransport);
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        //verify
        CarOMSReserveVerify.failoverTestFeatureOffVerify(carbsOMReserveReqAndRespGenerator, basicVerificationContext, errHandle);
    }

    public void failoverFeatureOnTestMethod(String clientId, TestScenario testScenario, int vcrrCount, TestData testData) throws Exception
    {
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "WSCS_FAILOVER_ON").build(), testData.getGuid());
        final DataSource carBSdataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        final CarBSHelper carBSHelper = new CarBSHelper(carBSdataSource);
        final String clientCode = carBSHelper.getClientListById(clientId) == null ? "" : carBSHelper.getClientListById(clientId).get(0).getClientCode();
        //booking

        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveErrorHandlingRequestSender.oMSReserveSendWithShopMsg(testScenario, carsInventoryDatasource, testData.getGuid(), httpClient, testData, clientCode, spooferTransport);
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        //verify
        //CarOMSReserveVerify.failoverTestFeatureOnVerify(carbsOMReserveReqAndRespGenerator,basicVerificationContext,errHandle,carsInventoryDatasource,vcrrCount);
        if(null!= carbsOMReserveReqAndRespGenerator.getRollbackPreparePurchaseResponseType()
                && !StatusCodeCategoryType.SUCCESS.equals(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getResponseStatus().getStatusCodeCategory()))
        {
            Assert.fail("Booking failed for " + carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getResponseStatus().getStatusMessage());
        }

        //retrieve only for feature on
        CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testScenario, testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, false);
        //verify
        CarOMSReserveVerify.failoverTestFeatureOnRetrieveVerify(carbsOMRetrieveReqAndRespGenerator, carsInventoryDatasource);

        CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testScenario, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }

    public void setPosConfigFailover(String tuid, String fallbackCatalogRetry, String fallbackCatalogRetryValue, String fallbackCatalogRetryMaxElapsedTimeBeforeFallback, String suppressSpecialEquipmentRequest) throws Exception
    {
        PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.BOOKING_FALLBACKCATALOGRETRY);
        posConfig.setSettingValue(fallbackCatalogRetry);
        ConfigSetUtil.posConfigSet(posConfig, null, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
        posConfig.setSettingName(PosConfigSettingName.BOOKING_FALLBACKCATALOGRETRY_VALUE);
        posConfig.setSettingValue(fallbackCatalogRetryValue);
        ConfigSetUtil.posConfigSet(posConfig, null, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
        posConfig.setSettingName(PosConfigSettingName.BOOKING_FALLBACKCATALOGRETRY_MAXELAPSEDTIMEBEFOREFALLBACK);
        posConfig.setSettingValue(fallbackCatalogRetryMaxElapsedTimeBeforeFallback);
        ConfigSetUtil.posConfigSet(posConfig, null, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
        posConfig.setSettingName(PosConfigSettingName.BOOKING_FALLBACKCATALOGRETRY_SUPPRESSSPECIALEQUIPMENTREQUEST);
        posConfig.setSettingValue(suppressSpecialEquipmentRequest);
        ConfigSetUtil.posConfigSet(posConfig, null, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
    }

   /* //------------------------------------------------Actions-------------------------------------------
    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void CASSS5615ClientCodeLogging_Worldspan() throws Exception
    {
        //send request and verify
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "561501", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testIfClientCodeLoggingThoughtOutCarsStack(CarCommonEnumManager.ClientID.ClientID_1.getValue(), testData);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void CASSS5615ClientCodeLogging_Amadeus() throws Exception
    {
        //send request and verify
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "561502", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testIfClientCodeLoggingThoughtOutCarsStack(CarCommonEnumManager.ClientID.ClientID_1.getValue(), testData);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void CASSS5615ClientCodeLogging_MN() throws Exception
    {
        //send request and verify
        final TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "561503", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testIfClientCodeLoggingThoughtOutCarsStack(CarCommonEnumManager.ClientID.ClientID_1.getValue(), testData);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void CASSS5615ClientCodeLogging_Titanium() throws Exception
    {
        //send request and verify
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(), "561504", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testIfClientCodeLoggingThoughtOutCarsStack(CarCommonEnumManager.ClientID.ClientID_1.getValue(), testData);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void CASSS5615ClientCodeLogging_Sabre() throws Exception
    {
        //send request and verify
        final TestData testData = new TestData(httpClient, CommonScenarios.Sabre_USA_Standalone_Oneway_OffAirport_SEA.getTestScenario(), "561505", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testIfClientCodeLoggingThoughtOutCarsStack(CarCommonEnumManager.ClientID.ClientID_1.getValue(), testData);
    }
*/
    public void testIfClientCodeLoggingThoughtOutCarsStack(String clientId, TestData testData) throws Exception
    {
        StringBuffer eMsg = new StringBuffer();

        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), testData.getGuid());

        //--------------------------------search--------------------------------
        final Date startTime = new Date();

        CarECommerceSearchRequestType searchRequest = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), testData.getTuid());
        buildMessageInfo(clientId, testData, searchRequest);

        CarECommerceSearchResponseType searchResponse = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), searchRequest);

        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, searchRequest, searchResponse);

        //--------------------------------getdetails--------------------------------
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(searchRequest, searchResponse, testData);

        CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID(testData);

        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);

        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);

        //--------------------------------getCostAndAvail--------------------------------
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequestByBusinessModelIDAndServiceProviderID(testData);
        getCostAndAvailabilityRequestType.getAuditLogTrackingData().setAuditLogForceDownstreamTransaction(true);
        getCostAndAvailabilityRequestType.getAuditLogTrackingData().setAuditLogForceLogging(true);

        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);

        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(),getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        //--------------------------------OMReserve--------------------------------
        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(searchRequest, searchResponse);
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        CarbsOMReserveReqAndRespGenerator reserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);

        Thread.sleep(100000);

        //--------------------------------Retrieve--------------------------------
        //        CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(reserveReqAndRespGenerator);
        //        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator);

        //--------------------------------Cancel --------------------------------
        CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(reserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient,
                CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //-----------------Splunk logging verify----------------------------------
        //verify if search is logged for carbs carss and carscs
        String actionType = CommonConstantManager.ActionType.SEARCH.toString();
        verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(eMsg, testData, actionType, startTime);

        //verify if getdetail is logged for carbs carss and carscs
        actionType = CommonConstantManager.ActionType.GETDETAILS.toString();
        verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(eMsg, testData, actionType, startTime);

        //verify if getCostAndAvail is logged for carbs carss and carscs
        actionType = CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY.toString();
        verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(eMsg, testData, actionType, startTime);

        //verify if GetOrderProcess is logged for carbs
        String carBSserviceName = "com.expedia.s3.cars.ecommerce.carbs";
        actionType = CarCommonEnumManager.OMReserveMessageType.GetOrderProcess.toString();
        ifClientCodeLoggingThoughtOutCarsStackInSplunk6Verification(eMsg, testData, carBSserviceName, actionType, startTime);

        //verify if PreparePurchase is logged for carbs carss and carscs
        actionType = CarCommonEnumManager.OMReserveMessageType.PreparePurchase.toString();
        ifClientCodeLoggingThoughtOutCarsStackInSplunk6Verification(eMsg, testData, carBSserviceName, actionType, startTime);

        actionType = CommonConstantManager.ActionType.RESERVE.toString();
        verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(eMsg, testData, actionType, startTime);

        //verify if GetReservation is logged for carbs carss and carscs
        //        actionType = "GetReservation";
        //        verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(eMsg, testData, actionType, startTime);

        //verify if Cancel is logged for carbs carss and carscs
        actionType = CommonConstantManager.ActionType.CANCEL.toString();
        verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(eMsg, testData, actionType, startTime);


        if (!StringUtils.isEmpty(eMsg.toString()))
        {
            Assert.fail(eMsg.toString());
        }
    }

    private void buildMessageInfo(String clientId, TestData testData, CarECommerceSearchRequestType searchRequest) throws Exception
    {
        getClientCodeByClientId(clientId, testData);

        if (!StringUtils.isEmpty(testData.getClientCode()))
        {
            searchRequest.setClientCode(testData.getClientCode());
            if (null == searchRequest.getMessageInfo())
            {
                searchRequest.setMessageInfo(new MessageInfoType());
            }

            searchRequest.getMessageInfo().setClientHostnameString("PEOPLEVILLE");
            searchRequest.getMessageInfo().setClientName("UnClassified");
            searchRequest.getMessageInfo().setEndUserIPAddress("10.199.199.199");
        }
    }

    private void getClientCodeByClientId(String clientId, TestData testData) throws Exception
    {
        final DataSource carBSdataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_BS_DATABASE_SERVER, SettingsProvider.DB_CARS_BS_DATABASE_NAME, SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        final CarBSHelper carBSHelper = new CarBSHelper(carBSdataSource);
        final String clientCode = carBSHelper.getClientListById(clientId) == null ? null : carBSHelper.getClientListById(clientId).get(0).getClientCode();
        testData.setClientCode(clientCode);
    }

    //Should verify carbs/carss/scs all action and OMS COGO COGO
    private void verifyIfClientCodeLoggingThoughtOutCarsStackInSplunk(StringBuffer eMsg, TestData testData, String actionType, Date startTime) throws Exception
    {
        String carBSserviceName = "com.expedia.s3.cars.ecommerce.carbs";
        String carSSserviceName = "com.expedia.s3.cars.supply.supplyservice";
        String carSCSserviceName = getSCSServiceNameByTestScenarios(testData);

        if (!actionType.equals(CommonConstantManager.ActionType.RESERVE.toString()))
        {
            ifClientCodeLoggingThoughtOutCarsStackInSplunk6Verification(eMsg, testData, carBSserviceName, actionType, startTime);
        }
        ifClientCodeLoggingThoughtOutCarsStackInSplunk6Verification(eMsg, testData, carSSserviceName, actionType, startTime);
        ifClientCodeLoggingThoughtOutCarsStackInSplunk6Verification(eMsg, testData, carSCSserviceName, actionType, startTime);

        String gdsMsgType = getGDSMsgType(testData, actionType);
        if(!StringUtils.isEmpty(gdsMsgType))
        {
            ifClientCodeLoggingThoughtOutCarsStackInAWSSplunkVerification(eMsg, testData, carSCSserviceName, gdsMsgType, startTime);
        }
    }

    private String getSCSServiceNameByTestScenarios(TestData testData)
    {
        String serviceNameSCS = "cars-worldspan-supplyconnectivity-service";
        if (3 == testData.getScenarios().getServiceProviderID())
        {
            serviceNameSCS = "cars-supplyconnectivity-micronnexus-service";
        } else if (6 == testData.getScenarios().getServiceProviderID())
        {
            serviceNameSCS = "cars-supplyconnectivity-amadeus-service";
        } else if (7 == testData.getScenarios().getServiceProviderID())
        {
            serviceNameSCS = "cars-supplyconnectivity-titanium-service";
        } else if (8 == testData.getScenarios().getServiceProviderID())
        {
            serviceNameSCS = "cars-supplyconnectivity-sabre-service";
        }

        return serviceNameSCS;
    }

    private String getGDSMsgType(TestData testData, String actionType)
    {
        String gdsMsgType = "";
        if (1 == testData.getScenarios().getServiceProviderID())
        {
            if (actionType.equals(CommonConstantManager.ActionType.SEARCH.toString()) ||
                    actionType.equals(CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY.toString()))
            {
                gdsMsgType = "VSAR";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETDETAILS.toString()))
            {
                gdsMsgType = "VRUR";
            } else if (actionType.equals(CommonConstantManager.ActionType.RESERVE.toString()))
            {
                gdsMsgType = "VCRR";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETRESERVATION.toString()))
            {
                gdsMsgType = "URRR";
            } else if (actionType.equals(CommonConstantManager.ActionType.CANCEL.toString()))
            {
                gdsMsgType = "VCRQ";
            }
        } else if (3 == testData.getScenarios().getServiceProviderID())
        {
            if (actionType.equals(CommonConstantManager.ActionType.SEARCH.toString()) ||
                    actionType.equals(CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY.toString()))
            {
                gdsMsgType = "VAR";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETDETAILS.toString()))
            {
                gdsMsgType = "VRR";
            } else if (actionType.equals(CommonConstantManager.ActionType.RESERVE.toString()))
            {
                gdsMsgType = "VRS";
            }
        } else if (6 == testData.getScenarios().getServiceProviderID())
        {
            if (actionType.equals(CommonConstantManager.ActionType.SEARCH.toString()) || actionType.equals(CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY.toString()))
            {
                gdsMsgType = "ACAQ";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETDETAILS.toString()))
            {
                gdsMsgType = "ARIA";
            } else if (actionType.equals(CommonConstantManager.ActionType.RESERVE.toString()))
            {
                gdsMsgType = "ACSQ";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETRESERVATION.toString()))
            {
                gdsMsgType = "APRQ";
            } else if (actionType.equals(CommonConstantManager.ActionType.CANCEL.toString()))
            {
                gdsMsgType = "APCQ";
            }
        } else if (7 == testData.getScenarios().getServiceProviderID())
        {
            if (actionType.equals(CommonConstantManager.ActionType.SEARCH.toString()) || actionType.equals(CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY.toString()))
            {
                gdsMsgType = "TVAR";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETDETAILS.toString()))
            {
                gdsMsgType = "TVRR";
            } else if (actionType.equals(CommonConstantManager.ActionType.RESERVE.toString()))
            {
                gdsMsgType = "TVRS";
            } else if (actionType.equals(CommonConstantManager.ActionType.GETRESERVATION.toString()))
            {
                gdsMsgType = "TVRB";
            }
        }

        return gdsMsgType;
    }

    private void ifClientCodeLoggingThoughtOutCarsStackInSplunk6Verification(StringBuffer eMsg, TestData testData, String serviceName, String actionType, Date startTime)
    {
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;

        //index=app TUID=123456789 ActionType="Search" ServiceName=cars-business-service ClientCode="VLZNNG"
        String splunkQuery = "index=app TUID=" + testData.getTuid() + " ActionType=" + actionType + " ServiceName=" + serviceName;
        if (!actionType.equals("Cancel"))
        {
            splunkQuery = splunkQuery + " ClientCode=" + testData.getClientCode();
        }
        splunkQuery = splunkQuery + " BotClassification=UnClassified BotTreatment=PEOPLEVILLE EndUserIPAddress=10.199.199.199 ";

        iflogInSplunkVerifier(hostName, hostPort, splunkQuery, startTime, eMsg);
    }

    private void ifClientCodeLoggingThoughtOutCarsStackInAWSSplunkVerification(StringBuffer eMsg, TestData testData, String serviceName, String actionType, Date startTime)
    {
        // Splunk host address
        final String hostName = "https://splunk.us-west-2.test.expedia.com";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;

        //index=app TUID=123456789 ActionType="Search" ServiceName=cars-business-service ClientCode="VLZNNG"
        String splunkQuery = "index=app ActionType=" + actionType + " Application=" + serviceName + " ClientCode=" + testData.getClientCode();
        splunkQuery = splunkQuery + " BotClassification=UnClassified BotTreatment=PEOPLEVILLE EndUserIPAddress=10.199.199.199 ";

        iflogInSplunkVerifier(hostName, hostPort, splunkQuery, startTime, eMsg);
    }

    private void iflogInSplunkVerifier(String hostName, int hostPort, String splunkQuery, Date startTime, StringBuffer eMsg)
    {
        try
        {
            final Date endTime = new Date();
            splunkQuery = splunkQuery + " earliest=" + startTime.getTime() / 1000 + " latest=" + endTime.getTime() / 1000;
            List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

            if ((splunkResult == null) || (splunkResult != null && splunkResult.size() < 1))
            {
                eMsg.append("\n");
                eMsg.append("No related splunk result found : splunkQuery : " + splunkQuery);
            }
        } catch (Exception e)
        {
            eMsg.append("\n");
            eMsg.append("Failed to query from splunk, with Exception : " + e.getMessage());
        }
    }

    //on-airport round-trip standalone, US POS, US pickup location.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidORS() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "1007601", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setHertzPrepayTestCase(true);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);

        hertzPrePaidTest(testData, spooferTransport);
    }

    //On-airport, oneWay, posu
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidOOS() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport_oneway.getTestScenario(),
                "1007602", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setHertzPrepayTestCase(true);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        hertzPrePaidTest(testData, spooferTransport);
    }

    //Off-airport, Roundtrip
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidOffRS() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario(),
                "1007603", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setHertzPrepayTestCase(true);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays5);

        hertzPrePaidTest(testData, spooferTransport);
    }

    //do not do booking logging check, add it, after the code is there.
    private void hertzPrePaidTest(TestData testData, SpooferTransport spooferTransport) throws Exception
    {
        CarbsRequestGenerator requestGenerator = hertzPrePaidSearchTest(testData, spooferTransport);

        hertzPrePaidGetDetailTest(testData, spooferTransport, requestGenerator);

        hertzPrePaidCostAndAvailTest(testData, spooferTransport, requestGenerator);
        //in 6.53.0.3, not return in reserve anymore.
        CarbsOMReserveReqAndRespGenerator reserveReqAndRespGenerator = hertzPrePaidOMReserveTest(testData, spooferTransport, requestGenerator);

        Thread.sleep(100000);

        //--------------------------------Cancel --------------------------------
        cancel(testData, reserveReqAndRespGenerator);
    }

    private CarbsRequestGenerator hertzPrePaidSearchTest(TestData testData, SpooferTransport spooferTransport) throws
            IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            DataAccessException, ParserConfigurationException, SQLException
    {
        //send search Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, carsInventoryDatasource, logger);
        //search Verification
        logger.info("search request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(requestGenerator.getSearchRequestType())));
        logger.info("search response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(requestGenerator.getSearchResponseType())));

        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, requestGenerator.getSearchRequestType(),
                requestGenerator.getSearchResponseType());

        //Hertz prepay verify
        //search response verify, is prepay car prepay boolean is true, and hertz prepay car RC code is prepay.
        CarBSSearchVerifier.verifyIfPrePayBooleanReturnInSearchResponseForHertz(requestGenerator.getSearchResponseType(),
                carsInventoryDatasource);
        CarBSSearchVerifier.verifyIfPrePayCarReferencePriceReturnForSearch(requestGenerator.getSearchResponseType(), true);

        return requestGenerator;
    }

    private void hertzPrePaidGetDetailTest(TestData testData, SpooferTransport spooferTransport,
                                           CarbsRequestGenerator requestGenerator) throws IOException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            DataAccessException, ParserConfigurationException, SQLException
    {
        CarECommerceGetDetailsRequestType getDetailsRequestType = requestGenerator.createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID(testData);
        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);

        requestGenerator.setGetDetailsRequestType(getDetailsRequestType);
        requestGenerator.setGetDetailsResponseType(getDetailsResponseType);

        logger.info("detail request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsRequestType)));
        logger.info("detail response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsResponseType)));
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);

        //Hertz prepay verify
        //get detail request & response verify, is prepay car prepay boolean is true, and hertz prepay car RC code is prepay.
        CarBSGetDetailVerifier.verifyIfPrePayBooleanReturnInGetDetailsRequestAndResponseForHertz(getDetailsRequestType, getDetailsResponseType, carsInventoryDatasource);

        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);
        if (!posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), "1", REFERENCE_SETTINGNAME)) {
            org.testng.Assert.fail("The pos config GetDetails.referencePricing/enable is not as expect 1 ");
        }
        CarBSGetDetailVerifier.verifyIfPrePayCarReferencePriceReturnForGetDetail(getDetailsResponseType, true);
    }

    private void hertzPrePaidCostAndAvailTest(TestData testData, SpooferTransport spooferTransport,
                                              CarbsRequestGenerator requestGenerator) throws DataAccessException,
            IOException
    {
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = requestGenerator.
                createCarbsCostAndAvailRequestByBusinessModelIDAndServiceProviderID(testData);
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.
                getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);

        requestGenerator.setGetCostAndAvailabilityRequestType(getCostAndAvailabilityRequestType);
        requestGenerator.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);

        logger.info("CostAndAvailability request xml ==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getCostAndAvailabilityRequestType)));
        logger.info("CostAndAvailability response xml ==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getCostAndAvailabilityResponse)));
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(),
                getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        //Hertz prepay verify
        //search response verify, is prepay car prepay boolean is true, and hertz prepay car RC code is prepay.
        CarBSGetCostAndAvailVerifier.verifyIfPrePayBooleanReturnInGetDetailsRequestAndResponseForHertz(getCostAndAvailabilityRequestType,
                getCostAndAvailabilityResponse, carsInventoryDatasource);
    }

    private CarbsOMReserveReqAndRespGenerator hertzPrePaidOMReserveTest(TestData testData, SpooferTransport spooferTransport,
                                           CarbsRequestGenerator requestGenerator) throws Exception
    {
        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator
                (requestGenerator.getSearchRequestType(), requestGenerator.getSearchResponseType());
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(requestGenerator.getGetCostAndAvailabilityResponseType());
        carOMSReqAndRespObj.setSelectCarProduct(requestGenerator.getSelectedCarProduct());

        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        return CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);
    }

    private void cancel(TestData testData, CarbsOMReserveReqAndRespGenerator reserveReqAndRespGenerator)
            throws IOException, DataAccessException
    {
        CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(reserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient,
                CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
        OmCancelVerifier.isPrepayCarCannotCancel(omsCancelReqAndRespObj.getGetChangeProcessResponseType());
    }
}