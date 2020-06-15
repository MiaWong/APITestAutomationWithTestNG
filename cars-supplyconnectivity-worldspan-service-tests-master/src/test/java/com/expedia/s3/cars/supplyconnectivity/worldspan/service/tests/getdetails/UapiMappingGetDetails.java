package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.getdetails;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter
        .TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.TP95CommonVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapSearchVerification;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper
        .getWSCSDataSourse;


/**
 * Created by yyang4 on 9/28/2017.
 */
public class UapiMappingGetDetails extends SuiteCommon {
    //1045116	Test Case	WSCS uAPI Getdetails - Verify Mapping correctly between SCS message and uAPI Getdetail   --  Agency Car Standalone RoundTrip offAirport with Currency change - 3 days	Monica Liu	Design
    //cases merge together 1045116,1045118,1045138
    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045116TestCarWorldspanSCSGetDetailsUAPIAgencyMonthly() throws Exception //CC required car
    {
        final String tuid = "1045116";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    //cases merge together 1045119,1045117
    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045119TestCarWorldspanSCSGetDetailsUAPIAgencyWeeklyExtra() throws Exception //CC required car
    {
        final String tuid = "1045119";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AC);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1044943,1045213,1045209,1045212,1051054
    public void casss1044943TestCarWorldspanSCSGetDetailsUAPIAgencyMonthly() throws Exception //CC required car
    {
        final String tuid = "1044943";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AC);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1045136,1064571,1064572
    public void casss1045136TestCarWorldspanSCSGetDetailsUAPIGDSPPackageWeeklyExtra() throws Exception //CC required car
    {
        final String tuid = "1045136";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_USLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_EP);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045135TestCarWorldspanSCSGetDetailsUAPIGDSPDaily() throws Exception //CC required car
    {
        final String tuid = "1045135";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045137TestCarWorldspanSCSGetDetailsUAPIGDSPMonthly() throws Exception //CC required car
    {
        final String tuid = "1045137";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045139TestCarWorldspanSCSGetDetailsUAPIGDSPWeekly() throws Exception //CC required car
    {
        final String tuid = "1045139";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1045122,1045140
    public void casss1045122TestCarWorldspanSCSGetDetailsUAPIGDSPDaily() throws Exception //CC required car
    {
        final String tuid = "1045122";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {"Merchant"})
    //cases merge together 1045141,1045142
    public void casss1045141TestCarWorldspanSCSGetDetailsUAPIMerchantWeeklyExtra() throws Exception //CC required car
    {
        final String tuid = "1045141";
        final TestScenario testScenario = CommonScenarios.Worldspan_FR_Merchant_Package_nonFRLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {"Merchant"})
    //cases merge together 1045143,1045145,1045146
    public void casss1045143TestCarWorldspanSCSGetDetailsUAPIMerchantCurrency() throws Exception //CC required car
    {
        final String tuid = "1045143";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {"Merchant"})
    public void casss1045144TestCarWorldspanSCSGetDetailsUAPIMerchantWeekly() throws Exception //CC required car
    {
        final String tuid = "1045144";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays5);
        testCarSCSGetDetailUAPIMapping(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1079649TestCarWorldspanSCSGetDetailsUAPIAgencyCurrency()throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ,DataAccessException,ParserConfigurationException //CC required car
    {
        final String tuid = "1079649";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testMileageGetdetail(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1062292TestCarWorldspanSCSGetDetailsUAPICCGurarantee()throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ,DataAccessException,ParserConfigurationException //CC required car
    {
        final String tuid = "1062292";
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_GDSP_Standalone_nonCALocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testCarSCSGetDetailCCGuarantee(testScenario, tuid, guid,testData,spooferTransport);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78684TP95TestCarWorldspanSCSGetDetailsUAPIAgency()throws Exception {
        final String tuid = "78684";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78692TP95TestCarWorldspanSCSGetDetailsUAPIAgency()throws Exception {
        final String tuid = "78692";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss78675TP95TestCarWorldspanSCSGetDetailsUAPIAgency()throws Exception {
        final String tuid = "78675";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Package_OnAirport.getTestScenario();
        doTP95Test(testScenario,tuid);
    }

    public void testCarSCSGetDetailUAPIMapping(TestScenario testScenario,String tuid, String guid,TestData testData,
                                               SpooferTransport spooferTransport) throws Exception {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("search request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("search response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(verificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);
        System.out.println("detail request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        System.out.println("detail response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        System.out.println("spoofer xml==> "+ PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput,spooferTransport,testScenario,guid,logger);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        UapiMapGetDetailsVerification.uapiMapVerifierWSCSGetDetails(basicVerificationContext,getDetailsVerificationInput, getWSCSDataSourse(),DataSourceHelper.getCarInventoryDatasource(),httpClient);
    }

    public void testMileageGetdetail(TestScenario testScenario,String tuid, String guid,TestData testData, SpooferTransport spooferTransport)throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ,DataAccessException,ParserConfigurationException{
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(getWSCSDataSourse());
        //send search request
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("search request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("search response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        //send getDetails request
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(verificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);
        System.out.println("detail request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        System.out.println("detail response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));


        //send cost&Avail request
        final GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput = ExecutionHelper.getCostAndAvailability(httpClient,requestGenerator,guid);
        System.out.println("cost&Avail request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getRequest())));
        System.out.println("cost&Avail response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        System.out.println("spoofer xml==> "+ PojoXmlUtil.toString(spooferDoc));
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        UapiMapGetDetailsVerification.uapiVerifierMileageGetdetail(basicVerificationContext,getDetailsVerificationInput,costAndAvailabilityVerificationInput,requestGenerator.getSelectedCarProduct(), getWSCSDataSourse(),DataSourceHelper.getCarInventoryDatasource(),httpClient);

    }

    public void testCarSCSGetDetailCCGuarantee(TestScenario testScenario,String tuid, String guid,TestData testData, SpooferTransport spooferTransport)throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException ,DataAccessException,ParserConfigurationException{

        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(getWSCSDataSourse());
        //send search request
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("search request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("search response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        //send getDetails request
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(verificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);
        System.out.println("detail request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        System.out.println("detail response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        System.out.println("spoofer xml==> "+ PojoXmlUtil.toString(spooferDoc));
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        UapiMapGetDetailsVerification.uapiCCGuaranteeVerifier(basicVerificationContext,getDetailsVerificationInput,getWSCSDataSourse(),DataSourceHelper.getCarInventoryDatasource(),httpClient);


    }

    public void doTP95Test(TestScenario testScenario,String tuid)throws Exception{
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setSpooferTransport(spooferTransport);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(getWSCSDataSourse());
        //send search request
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("search request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("search response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        //send getDetails request
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(verificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);
        System.out.println("detail request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        System.out.println("detail response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        TP95CommonVerification.tp95PerfMetricsGetDetailsVerify(testData,getDetailsVerificationInput);

    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss10076HertzPrePaid()throws Exception
    {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);

        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "10076", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        hertzPrePaidTest(testData, spooferTransport);
    }

    public void hertzPrePaidTest(TestData testData, SpooferTransport spooferTransport) throws IOException, InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, ParserConfigurationException, SQLException
    {
        logger.setLevel(Level.INFO);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("search request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
            logger.info("search response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        }

        /*
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(verificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("detail request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
            logger.info("detail response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        }
        */

        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));
        }

//        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput, spooferTransport, testScenario, guid, logger);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), testData.getScenarios());

        UapiMapSearchVerification.uapiMapVerifierWSCSSearch(basicVerificationContext, verificationInput,
                DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient, false);

        UapiMapSearchVerification.verifyIfPrePayBooleanReturnInSearchResponseForHertz(verificationInput,
                DataSourceHelper.getCarInventoryDatasource());
    }
}
