package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.CancelHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
/**
 * Created by jiyu on 8/29/16.
 */
public class Reserve extends SuiteContext
{
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void casss1896Reserve() throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testReserve(CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                    "1083451",
                    randomGuid,
                    -1L);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896Reserve(@Optional("15") long vendorSupplierID,
                                 @Optional("10") long carCategoryCode,
                                 @Optional("4") long carTypeCode,
                                 @Optional("1") long carTransmissionDriveCode,
                                 @Optional("1") long carFuelACCode) throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testReserve(CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                "1083451",
                randomGuid,
                -1L,
                false,
                vendorSupplierID,
                carCategoryCode,
                carTypeCode,
                carTransmissionDriveCode,
                carFuelACCode);
    }


    @SuppressWarnings("CPD-START")
    private void testReserve(TestScenario scenarios,
                             String tuid,
                             String guid,
                             long driverAge,
                             boolean isRandomPick,
                             long vendorSupplierID,
                             long carCategoryCode,
                             long carTypeCode,
                             long carTransmissionDriveCode,
                             long carFuelACCode) throws Exception
    {
        final SearchVerificationInput searchVerificationInput =
                SearchHelper.search( httpClient,
                                        scenarios,
                                        tuid,
                                        guid,
                                        driverAge,
                        carSCSDatasource);

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        String newGuid = null;
        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        CostAndAvailHelper.getCostAndAvailability( httpClient,
                                                requestGenerator,
                                                newGuid,
                                                isRandomPick,
                                                vendorSupplierID,
                                                carCategoryCode,
                                                carTypeCode,
                                                carTransmissionDriveCode,
                                                carFuelACCode);

        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        GetDetailsHelper.getDetails( httpClient,
                                    requestGenerator,
                                    newGuid,
                                    isRandomPick,
                                    vendorSupplierID,
                                    carCategoryCode,
                                    carTypeCode,
                                    carTransmissionDriveCode,
                                    carFuelACCode);

        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final ReserveVerificationInput reserveVerificationInput =
                ReserveHelper.reserve(httpClient,
                                        requestGenerator,
                                        newGuid,
                                        true,
                                        isRandomPick,
                                        vendorSupplierID,
                                        carCategoryCode,
                                        carTypeCode,
                                        carTransmissionDriveCode,
                                        carFuelACCode);

        ReserveHelper.reserveVerify(  reserveVerificationInput,
                                        spooferTransport,
                                        scenarios,
                                        newGuid,
                                        true,
                                        null,
                                        true);

        //  do extra miles to cancel the reservation if there is a booking
        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        CancelHelper.cancel(httpClient, requestGenerator, newGuid, false);
    }

    @SuppressWarnings("CPD-END")
    private void testReserve(TestScenario scenarios,
                             String tuid,
                             String guid,
                             long driverAge) throws Exception
    {
        testReserve(scenarios, tuid, guid, driverAge, true, 0L, 0L, 0L, 0L, 0L);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    //OnAirport, roundtrip, currency conversation, daily
    public void tfs1077510ReserveTIMapOnairportRoundtrip() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077510", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTiscsReserveMap(testData, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077511ReserveTIMapOffairportOneway() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(),
                "1077511", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTiscsReserveMap(testData, false);
    }

    //TisSCS_FRA_Package_Roundtrip_OnAirport_CDG
    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1083126ReserveTIMapPackage() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario(),
                "1083126", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTiscsReserveMap(testData, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077520ReserveTIMapSpecialEquip() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario(),
                "1077520", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTiscsReserveMap(testData, true);
    }

    //1077521
    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077521ReserveTIMapAirFlight() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario(),
                "1077521", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTiscsReserveMap(testData, false, true);
    }

    private void testTiscsReserveMap(TestData testData, boolean needSpecialEquipment, boolean... isRequiredAirFlight) throws Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final ReserveVerificationInput input = ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), needSpecialEquipment, isRequiredAirFlight);

        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getRequest())));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getResponse())));

        //Do BVT verification
        ReserveHelper.reserveVerify(input, spooferTransport, testData.getScenarios(), testData.getGuid(), needSpecialEquipment, logger, false);

        //cancel
        CancelHelper.cancel(httpClient, requestGenerator, testData.getGuid(), false);

        //Verify GDS map
        ReserveHelper.reserveGDSMapVerification(input, spooferTransport,
                testData.getScenarios(), testData.getGuid(), logger);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077526ReserveTIErrorMap() throws IOException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,DataAccessException, Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_ReserveError").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario(),
                "1077526", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTIReserveErrorMap(testData, "EMreserve8");
    }

    private void testTIReserveErrorMap(TestData testData, String errorCode) throws IOException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,DataAccessException, Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final ReserveVerificationInput input = ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), errorCode);

        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getRequest())));
        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getResponse())));

        //Verify GDS map
        ReserveHelper.reserveErrorGDSMapVerification(input, spooferTransport,
                testData.getScenarios(), testData.getGuid(), logger);

    }


}
