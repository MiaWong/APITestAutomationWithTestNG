package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.search;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandlingValue;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.SpecialTestCasesParam;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.TP95CommonVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiErrorMappingSearchVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapSearchVerification;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by yyang4 on 9/20/2017.
 */
public class UapiMappingSearch extends SuiteCommon {


    //User Story 974770 WSCS uAPI - uAPI support for search mapping test cases
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050753TestCarWorldspanSCSSearchUAPIAgencyOffairport() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050753";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        final SpecialTestCasesParam specialTestCasesParam = new SpecialTestCasesParam();
        specialTestCasesParam.setSpecialOffAirPort(true);
        testData.setSpecialTestCasesParam(specialTestCasesParam);
        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050764TestCarWorldspanSCSSearchUAPIAgencyCurrency() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050764";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050769TestCarWorldspanSCSSearchUAPIAgencyOnairport() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050769";
        final TestScenario testScenario = CommonScenarios.Worldspan_IND_10111_1185_Agency_Standalone_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})

    public void casss1050777TestCarWorldspanSCSSearchUAPIAgencyCA() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050777";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_CALocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})

    public void casss1050759TestCarWorldspanSCSSearchUAPIAgencyOneway() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050759";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050760TestCarWorldspanSCSSearchUAPIAgencyWeeklyExtra() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050760";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MCO_LAX_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050789TestCarWorldspanSCSSearchUAPIPackage() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050789";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050817TestCarWorldspanSCSSearchUAPIFRPackage() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050817";
        final TestScenario testScenario = CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050837TestCarWorldspanSCSSearchUAPIGDSPStandalone() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050837";
        final TestScenario testScenario = CommonScenarios.Worldspan_BRA_1161_GDSP_Standalone_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050828TestCarWorldspanSCSSearchUAPICAGDSPPackage() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {

        final String tuid = "1050828";
        final TestScenario testScenario = CommonScenarios.Worldspan_Travelocity_CA_GDSP_Standalone_nonCALocation_OffAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays7);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050836TestCarWorldspanSCSSearchUAPIDEGDSP() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050836";
        final TestScenario testScenario = CommonScenarios.Worldspan_DEU_10111_1055_GDSP_Standalone_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050818TestCarWorldspanSCSSearchUAPIUKGDSPMonthly() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050818";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport_Oneway.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {"Merchant"})
    public void casss1050852TestCarWorldspanSCSSearchUAPIMerchant3Day() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050852";
        final TestScenario testScenario = CommonScenarios.Worldspan_FR_Merchant_Package_nonFRLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testData.setMerchantBoolean(true);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {"Merchant"})
    public void casss1050853TestCarWorldspanSCSSearchUAPIMerchantWeeklyExtra() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050853";
        final TestScenario testScenario = CommonScenarios.Worldspan_AUT_1070_Merchant_Package_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testData.setMerchantBoolean(true);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {"Merchant"})
    public void casss1050861TestCarWorldspanSCSSearchUAPIMerchantMonthly() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050861";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport_Oneway.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testData.setMerchantBoolean(true);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {"Merchant"})
    public void casss1050862TestCarWorldspanSCSSearchUAPIMerchantWeekly() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050862";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OffAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);
        testData.setMerchantBoolean(true);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, true);
    }

    @Test(groups = {"Merchant"})
    public void casss1050865TestCarWorldspanSCSSearchUAPIMonthlyExtra() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050865";
        final TestScenario testScenario = CommonScenarios.Worldspan_DE_Merchant_Standalone_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays);
        testData.setMerchantBoolean(true);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050863TestCarWorldspanSCSSearchUAPIGDSPDaily() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050863";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_GDSP_Standalone_CALocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testData.setMerchantBoolean(true);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }


    //cases merge together 1050871
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050871TestCarWorldspanSCSSearchUAPIAgencyWithCD() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050871";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        //set cd code
        CarRate rate = new CarRate();
        rate.setCdCode(RequestDefaultValues.MULTIPLE_CD_CODE);
        testData.setCarRate(rate);


        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    //cases merge together 1050890,1064662,1064661
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050890TestCarWorldspanSCSSearchUAPIPromoCode() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050890";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_USLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        //set cd code
        CarRate rate = new CarRate();
        rate.setPromoCode(RequestDefaultValues.PROMOCODE_DAILY_ZE);
        testData.setCarRate(rate);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_EP);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);


        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }


    //cases merge together 1051505,1051505
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1051505TestCarWorldspanSCSSearchUAPIGDSPPac() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1051505";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1050878TestCarWorldspanSCSSearchUAPLoyaltyNum() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "1050878";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        //set LoyaltyNumber
        CarRate rate = new CarRate();
        rate.setLoyaltyNum(RequestDefaultValues.LOYALTYNUMBER_ALAMO);
        testData.setCarRate(rate);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AL);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);

        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    //add new special scenario Roundtrip, Onairport - offairport, like SEA-SEAC001
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2000001TestCarWorldspanSCSSearchUAPIOnToOff() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "2000001";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        final SpecialTestCasesParam specialTestCasesParam = new SpecialTestCasesParam();
        specialTestCasesParam.setSpecialOffAirPort(true);
        testData.setSpecialTestCasesParam(specialTestCasesParam);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setVendorSupplierID(15);
        specialHandleParam.setDropOffCarVendorLocationCode("C007");
        specialHandleParam.setSearchCriteriaCount(1);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }


    //add new special scenario Roundtrip, Offairport - onairport, like SEAC001-SEA
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2000002TestCarWorldspanSCSSearchUAPIOffToOn() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "2000002";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        final SpecialTestCasesParam specialTestCasesParam = new SpecialTestCasesParam();
        specialTestCasesParam.setSpecialOffAirPort(true);
        testData.setSpecialTestCasesParam(specialTestCasesParam);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setVendorSupplierID(15);
        specialHandleParam.setPickUpCarVendorLocationCode("C007");
        specialHandleParam.setSearchCriteriaCount(1);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    //add new special scenario Oneway, onairport-offairport, like LHR-EDIT001
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2000003TestCarWorldspanSCSSearchUAPIOneWayOnToOff() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        final String tuid = "2000003";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway.getTestScenario();
        //set request parama
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        final SpecialTestCasesParam specialTestCasesParam = new SpecialTestCasesParam();
        specialTestCasesParam.setSpecialOffAirPort(true);
        testData.setSpecialTestCasesParam(specialTestCasesParam);

        //set userDays to crate StartDateTimeRange and EndDateTimeRange
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays7);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setPickupOnAirport(Boolean.TRUE.toString());
        specialHandleParam.setVendorSupplierID(3);
        specialHandleParam.setDropOffCarVendorLocationCode("T001");
        specialHandleParam.setSearchCriteriaCount(1);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        //send request
        testCarSCSSearchUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, false);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78118TP95TestCarWorldspanSCSSearchUAPIAgency() throws Exception {
        final String tuid = "78118";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78126TP95TestCarWorldspanSCSSearchUAPIAgency() throws Exception {
        final String tuid = "78126";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78122TP95TestCarWorldspanSCSSearchUAPIAgency() throws Exception {
        final String tuid = "78122";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78130TP95TestCarWorldspanSCSSearchUAPIAgency() throws Exception {
        final String tuid = "78130";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Bundle_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78135TP95TestCarWorldspanSCSSearchUAPIAgency() throws Exception {
        final String tuid = "78135";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Package_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78144TP95TestCarWorldspanSCSSearchUAPIAgency() throws Exception {
        final String tuid = "78144";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_PMI_OnAirport.getTestScenario();
        doTP95Test(testScenario, tuid);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss1058356UAPISearchErrorMapping() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException {
        final String tuid = "1058356";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        doUAPIErrorMapping(testScenario, tuid, testData, spooferTransport, guid, CommonEnumManager.ErrorHandlingType.InvalidCorporateDiscountCode, ErrorHandlingValue.ErrorMapping_RESPONSEWASNULLORNOTINADOM_VAQ,null);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION}, enabled = false)
    public void casss1058856UAPISearchErrorMapping() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException {
        final String tuid = "1058856";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ErrorMapping_Reserve");
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        ErrorHandlingValue errorHandlingValue=ErrorHandlingValue.ErrorMapping_RESPONSEWASNULLORNOTINADOM_VAQ;
        errorHandlingValue.getErrorHandling().setErrorType("unclassifiederror");
        errorHandlingValue.getErrorHandling().setErrormessage("Received an unknown error. Code: 4000; Message:  00257 TRANSACTION DENIED, DENEGADA $EY$");
        doUAPIErrorMapping(testScenario, tuid, testData, spooferTransport, guid, CommonEnumManager.ErrorHandlingType.InvalidCorporateDiscountCode, errorHandlingValue,"EM");
    }

    public void testCarSCSSearchUAPIMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid, boolean needFilter) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {
        doUAPIMapping(testScenario, tuid, testData, spooferTransport, guid, needFilter);
    }

    public void doUAPIMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid, boolean needFilter) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException {

        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSpecialSearchRequest(testData, 10);
        if (needFilter) {
            PropertyResetHelper.filterReqSearchList(searchRequest, DataSourceHelper.getWSCSDataSourse());
        }
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        System.out.println("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        ExecutionHelper.searchVerification(verificationInput, spooferTransport, testScenario, guid, logger);
        UapiMapSearchVerification.uapiMapVerifierWSCSSearch(basicVerificationContext, verificationInput,
                DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient, true);
    }

    public void doUAPIErrorMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid, CommonEnumManager.ErrorHandlingType handlingType, ErrorHandlingValue errorHandlingValue,String errorMap) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException {
        CarRate rate = new CarRate();
        if(null!=errorMap) {
            rate.setCdCode("TO-" + errorHandlingValue.getErrorHandling().getInvalidValue());
        }
        else {
            rate.setCdCode("ET-" + errorHandlingValue.getErrorHandling().getInvalidValue());
        }
        testData.setCarRate(rate);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSpecialSearchRequest(testData, 10);
        if(null!=errorMap) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "ErrorMapping_Reserve").build(), guid);
        }
        else {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "ErrorMap").build(), guid);
        }

        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        System.out.println("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        if(null!=errorMap) {
            UapiErrorMappingSearchVerification.errorMappingVerifierTransaction(verificationInput.getResponse().getErrorCollectionList(), errorHandlingValue.getErrorHandling());
        }
        else
        {
            UapiErrorMappingSearchVerification.errorMappingVerifier(verificationInput.getResponse().getErrorCollectionList(), errorHandlingValue.getErrorHandling());
        }

    }

    public void doTP95Test(TestScenario testScenario, String tuid) throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setSpooferTransport(spooferTransport);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSpecialSearchRequest(testData, 10);

        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());
        TP95CommonVerification.tp95PerfMetricsSearchVerify(testData, verificationInput);

    }
}

