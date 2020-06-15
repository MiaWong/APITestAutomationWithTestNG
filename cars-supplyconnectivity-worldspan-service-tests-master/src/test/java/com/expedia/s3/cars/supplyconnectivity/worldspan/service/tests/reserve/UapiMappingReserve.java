package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.reserve;

import com.expedia.e3.data.airtypes.defn.v4.AirFlightType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.SpecialTestCasesParam;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter
        .TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.CarXSDRelatedMethodManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.TP95CommonVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapCostAndAvailVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapReserveVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapSearchVerification;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper
        .getWSCSDataSourse;

/**
 * Created by yyang4 on 10/10/2017.
 */
public class UapiMappingReserve extends SuiteCommon {
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //cases merge together 1046843,1046868,1046867,1046865,1046849,1046848,1068797
    public void casss1046843UAPIReserveSCSMappingPromoCodeLoyalty() throws Exception {
        final String tuid = "1046843";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days2);
        testData.setWeekendBoolean(true);
        testData.setBillingNumber("871131370003");
        final CarRate carRate = new CarRate();
        carRate.setPromoCode("301630");
        carRate.setLoyaltyNum("43825675");
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //cases merge together 1046844,1046859
    public void casss1046844UAPIReserveSCSMappingCD() throws Exception {
        final String tuid = "1046844";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays15);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AC);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        final CarRateType carRateType = new CarRateType();
        carRateType.setCorporateDiscountCode("3000ARC");
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {"Merchant"})
    public void casss1046845UAPIReserveSCSMappingMerchant() throws Exception {
        final String tuid = "1046845";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Package_nonUKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays5);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_EP);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046863UAPIReserveSCSMappingGDSPPackage() throws Exception {
        final String tuid = "1046863";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_EP);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046879UAPIReserveSCSMappingMulitpleSpecialEquip() throws Exception {
        final String tuid = "1046879";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setCarSpecialEquipmentCode("InfantChildSeat,NavigationalSystem");
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046874UAPIReserveSCSMappingSpecialEquip() throws Exception {
        final String tuid = "1046874";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setCarSpecialEquipmentCode("InfantChildSeat");
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss1046861UAPIReserveSCSMappingGDSPStandalone() throws Exception {
        final String tuid = "1046861";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {"Merchant"})
    //cases merge together1046872,1068798
    public void casss1046872UAPIReserveSCSMappingStandaloneMerchant() throws Exception {
        final String tuid = "1046872";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setNeedVO(true);
        testData.setBillingNumber("871131370003");
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //cases merge together 1046880,1068754
    public void casss1046880UAPIReserveSCSMappingBundle() throws Exception {
        final String tuid = "1046880";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Bandle_LAS_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setNeedCC(true);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AC);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testData.setWithFliInfo(true);
        testCarSCSReserveUAPIMapping(testScenario, tuid, testData, spooferTransport, guid);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void casss392223TP95TestPNRErrorUAPIAgency() throws Exception {
        final String tuid = "392223";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
        doTP95ErrorAnalysisTest(testScenario, tuid, CommonEnumManager.ErrorHandlingType.InvalidCorporateDiscountCode);
    }



    // <summary>
    // Basic method for UAPI cost&Avail mapping
    // </summary>
    public void testCarSCSReserveUAPIMapping(TestScenario testScenario, String tuid, TestData testData, SpooferTransport spooferTransport, String guid) throws Exception {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        //Build specialEquip
        if (!CompareUtil.isObjEmpty(testData.getCarSpecialEquipmentCode())) {
            requestGenerator4.buildSpecialEquipmentList(reserveRequest, testData.getCarSpecialEquipmentCode());
        }
        //needVO
        if (testData.isNeedVO()) {
            reserveRequest.setCarProduct(CarXSDRelatedMethodManager.SelectCarWithVOFormat(searchVerificationInput.getResponse().getCarSearchResultList(), DataSourceHelper.getCarInventoryDatasource()));
        }

        //BillingNumber
        if (!CompareUtil.isObjEmpty(testData.getBillingNumber())) {
            if (CompareUtil.isObjEmpty(reserveRequest.getReferenceList())) {
                final ReferenceListType referenceListType = new ReferenceListType();
                reserveRequest.setReferenceList(referenceListType);
                final List<ReferenceType> referenceTypeList = new ArrayList<ReferenceType>();
                referenceListType.setReference(referenceTypeList);
            }
            final ReferenceType BN = new ReferenceType();
            BN.setReferenceCategoryCode("BillingNumber");
            BN.setReferenceCode(testData.getBillingNumber());
            reserveRequest.getReferenceList().getReference().add(BN);
        }

        //needCC
        if (testData.isNeedCC()) {
            reserveRequest.setCreditCardFormOfPayment(CommonDataTypesGenerator.createCreditCardFormOfPayment(null, null, null));
        }

        //Flight information
        if (testData.isWithFliInfo()) {
            //AirFlight
            final AirFlightType airNode = new AirFlightType();
            airNode.setAirCarrierCode("VT");
            airNode.setFlightNumber("121");
            reserveRequest.setAirFlight(airNode);
        }
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.reserveVerify(verificationInput, spooferTransport, testScenario, guid, logger);
        UapiMapReserveVerification.uapiMapVerifierWSCSReserve(basicVerificationContext, verificationInput, DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient);


        //3,Cancel the reserve
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(verificationInput.getResponse());
        requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator4, guid);
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testScenario, guid, logger);
    }

    public void doTP95ErrorAnalysisTest(TestScenario testScenario, String tuid, CommonEnumManager.ErrorHandlingType errorHandlingType) throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setSpooferTransport(spooferTransport);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));


        //2,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();

        //Set the Invalid CarRateQualifierCode to request
        if (errorHandlingType == CommonEnumManager.ErrorHandlingType.InvalidCorporateDiscountCode) {
            reserveRequest.getCarProduct().getCarInventoryKey().getCarRate().setCorporateDiscountCode(RequestDefaultValues.INVALID_CORPORATE_DISCOUNT_CODE);
            reserveRequest.getCarProduct().getCarInventoryKey().getCarRate().setCarRateQualifierCode(RequestDefaultValues.INVALID_CARRATE_QUALIFIERCODE);
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "PNRErrorInReserve").build(), guid);
        }
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, guid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        TP95CommonVerification.tp95ErrorAnalysisReserveVerify(testData, verificationInput);

    }

    //on-airport round-trip standalone, US POS, US pickup location.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidORS()throws Exception
    {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);

        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "1007601", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testData.setSpecificSupplySubset("WorldSpanHertzPrepaid");//Supplysubset for [CarsInventory_STT05].[dbo].[SupplySubSetToWorldSpanSupplierItemMapLog] where PrepaidBool = 1

        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        hertzPrePaidTest(testData, spooferTransport, false);
    }

    //on-airport round-trip standalone, US POS, UK pickup location.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidORK()throws Exception
    {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);

        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario(),
                "1007602", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testData.setSpecificSupplySubset("WorldSpanHertzPrepaid");//Supplysubset for [CarsInventory_STT05].[dbo].[SupplySubSetToWorldSpanSupplierItemMapLog] where PrepaidBool = 1

        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        hertzPrePaidTest(testData, spooferTransport, false);
    }

    //on-airport round-trip standalone, US POS, US pickup location.
    //GetDetail filter car product if request is prepay, response is not prepay.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidGetDetailFilter ()throws Exception
    {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);

        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "1007603", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testData.setSpecificSupplySubset("WorldSpanHertzPrepaid");//Supplysubset for [CarsInventory_STT05].[dbo].[SupplySubSetToWorldSpanSupplierItemMapLog] where PrepaidBool = 1

        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        hertzPrePaidTest(testData, spooferTransport, true);
    }

    public void hertzPrePaidTest(TestData testData, SpooferTransport spooferTransport, boolean isDetailFilterScenario) throws Exception
    {
        if(isDetailFilterScenario)
        {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                    "RateCategoryCodeDiffWithReq_GetDetail").build(), testData.getGuid());
        }
        final SearchVerificationInput searchVerificationInput = hertzPrePaidSearchTest(testData, spooferTransport);

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        hertzPrePaidGetDetailTest(testData, spooferTransport, requestGenerator, isDetailFilterScenario);

        if(!isDetailFilterScenario)
        {
            hertzPrePaidCostAndAvailTest(testData, spooferTransport, requestGenerator);

            //in 6.53.0.3, not return in reserve anymore.
            hertzPrePaidReserveTest(testData, spooferTransport, requestGenerator);
            cancel(testData, spooferTransport, requestGenerator);
        }
    }

    private SearchVerificationInput hertzPrePaidSearchTest(TestData testData, SpooferTransport spooferTransport) throws
            IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            DataAccessException, ParserConfigurationException, SQLException
    {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());
        logger.info("search request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));

        logger.info("search response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        BasicVerificationContext serchBasicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), testData.getScenarios());
        //uapi GDS search request mapping is add verify in this function. send RateCategory="Prepay" in VSAR request while Prepay.
        UapiMapSearchVerification.uapiMapVerifierWSCSSearch(serchBasicVerificationContext, searchVerificationInput,
                DataSourceHelper.getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient, false);

        UapiMapSearchVerification.verifyIfPrePayBooleanReturnInSearchResponseForHertz(searchVerificationInput,
                DataSourceHelper.getCarInventoryDatasource());

        return searchVerificationInput;
    }

    private void hertzPrePaidGetDetailTest(TestData testData, SpooferTransport spooferTransport,
                                          SCSRequestGenerator requestGenerator, boolean isDetailFilterScenario) throws IOException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            DataAccessException, ParserConfigurationException, SQLException
    {
        //get detail
        final GetDetailsVerificationInput getDetailsVerificationInput =
                ExecutionHelper.getDetailsHertzPrepay(httpClient, requestGenerator, testData.getGuid());

        if (null == getDetailsVerificationInput)
        {
            Assert.fail("Do not have Hertz Car in search response. Please check./n");
        }

        logger.info("detail request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info("detail response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(),
                testData.getScenarios());

        if(isDetailFilterScenario)
        {
            if(null != getDetailsVerificationInput.getResponse().getCarProductList() &&
                    null != getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct() &&
                    !getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct().isEmpty())
            {
                Assert.fail("Should not return Car in GetDetail Response, while Response RateCategory code is different with Request. Please check./n");
            }
        }
        else
        {
            //uapi GDS hertz prepay get detail request mapping is add verify in this function. send RateCategory="Prepay" in VSAR request while Prepay.
            UapiMapGetDetailsVerification.uapiMapVerifierWSCSGetDetails(basicVerificationContext, getDetailsVerificationInput,
                    getWSCSDataSourse(), DataSourceHelper.getCarInventoryDatasource(), httpClient);

            UapiMapGetDetailsVerification.verifyIfPrePayBooleanReturnInGetDetailResponseForHertz(getDetailsVerificationInput,
                    DataSourceHelper.getCarInventoryDatasource());
        }
    }

    private void hertzPrePaidCostAndAvailTest(TestData testData, SpooferTransport spooferTransport,
                                             SCSRequestGenerator requestGenerator) throws IOException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            DataAccessException, ParserConfigurationException, SQLException
    {
        //send cost&Avail request
        testData.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        final GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput =
                ExecutionHelper.getCostAndAvailability(httpClient, requestGenerator, testData.getGuid());

        logger.info("cost&Avail request xml==> " + PojoXmlUtil.toString
                (PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getRequest())));
        logger.info("cost&Avail response xml==> " + PojoXmlUtil.toString
                (PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(),
                testData.getScenarios());

        //uapi GDS get Detail request mapping is add verify in this function. send RateCategory="Prepay" in VSAR request while hertz Prepay.
        UapiMapCostAndAvailVerification.uapiMapVerifierWSCSCostAndAvail(basicVerificationContext,
                costAndAvailabilityVerificationInput, DataSourceHelper.getWSCSDataSourse(),
                DataSourceHelper.getCarInventoryDatasource(), httpClient);

        UapiMapCostAndAvailVerification.verifyIfPrePayBooleanReturnInCostAndAvailResponseForHertz
                (costAndAvailabilityVerificationInput, DataSourceHelper.getCarInventoryDatasource());
    }

    private ReserveVerificationInput hertzPrePaidReserveTest(TestData testData, SpooferTransport spooferTransport,
                                             SCSRequestGenerator requestGenerator) throws Exception
    {
        //2,reserve
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, testData.getGuid());
        requestGenerator.setReserveReq(reserveVerificationInput.getRequest());
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        logger.info("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));

        logger.info("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        logger.info("spoofer xml==> " + PojoXmlUtil.toString(spooferDoc));

        ExecutionHelper.reserveVerify(reserveVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger);

        return reserveVerificationInput;
    }

    //4,Cancel the reserve
    private void cancel(TestData testData, SpooferTransport spooferTransport,
                                               SCSRequestGenerator requestGenerator) throws Exception {
        requestGenerator.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator, testData.getGuid());
        ExecutionHelper.cancelVerify(cancelVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger);
    }
}
