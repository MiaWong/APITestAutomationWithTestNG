package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.CancelHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

/**
 * Created by jiyu on 8/30/16.
 */
public class GetReservation extends SuiteContext
{
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void casss1896GetReservation() throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testGetReservation( CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                            "1083451",
                            randomGuid,
                            -1L);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896GetReservation(@Optional("15") long vendorSupplierID,
                                        @Optional("10") long carCategoryCode,
                                        @Optional("4") long carTypeCode,
                                        @Optional("1") long carTransmissionDriveCode,
                                        @Optional("1") long carFuelACCode) throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testGetReservation( CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
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
    private void testGetReservation(TestScenario scenarios,
                                    String tuid,
                                    String guid,
                                    long driverAge,
                                    boolean isRandomPick,
                                    long vendorSupplierID,
                                    long carCategoryCode,
                                    long carTypeCode,
                                    long carTransmissionDriveCode,
                                    long carFuelACCode) throws  Exception
    {
        final SearchVerificationInput searchVerificationInput = SearchHelper.search(
                                    httpClient,
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
        final ReserveVerificationInput reserveVerificationInput = ReserveHelper.reserve(
                                    httpClient,
                                    requestGenerator,
                                    newGuid,
                                    false,
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
                                        false,
                                        null,
                                        true);


        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final GetReservationVerificationInput retrieveVerificationInput = GetReservationHelper.retrieveReservation(
                                                    httpClient,
                                                    requestGenerator,
                                                    newGuid,
                                                    false);

        GetReservationHelper.getreservationVerify(   retrieveVerificationInput,
                                                spooferTransport,
                                                scenarios,
                                                newGuid,
                                                false,
                                                null,
                                                true);


        //  do extra miles to cancel the reservation if there is a booking
        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        CancelHelper.cancel(httpClient, requestGenerator, newGuid, false);

    }

    @SuppressWarnings("CPD-END")
    private void testGetReservation(TestScenario scenarios,
                                    String tuid,
                                    String guid,
                                    long driverAge) throws  Exception
    {
        testGetReservation(scenarios, tuid, guid, driverAge, true, 0L, 0L, 0L, 0L, 0L);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    //OnAirport, roundtrip, currency conversation, daily
    public void tfs1077555GetReservationTIMapOnairportRoundtrip() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077555", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTIGetReservationMap(testData, false);
    }

    //OffAirport, oneway, currency conversation, daily
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077547GetReservationTIMapOffairportOneway() throws Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(),
                "1077547", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testTIGetReservationMap(testData, false);
    }

    private void testTIGetReservationMap(TestData testData, boolean needSpecialEquipment) throws  Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), needSpecialEquipment);

        //GetReservation
        final GetReservationVerificationInput input = GetReservationHelper.retrieveReservation(httpClient, requestGenerator, testData.getGuid(), needSpecialEquipment);

        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getRequest())));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getResponse())));

        //cancel
        CancelHelper.cancel(httpClient, requestGenerator, testData.getGuid(), false);

        //Do BVT verification
        GetReservationHelper.getreservationVerify(input, spooferTransport,
                testData.getScenarios(), testData.getGuid(), needSpecialEquipment, logger, false);

        //Verify GDS map
        GetReservationHelper.getReservationGDSMapVerification(input, spooferTransport,
                testData.getScenarios(), testData.getGuid(), logger);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077582GetReservationTIMapError8() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GetReservationError").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077582", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTIGetReservationErrorMap(testData, "EMgetReservation8");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077585GetReservationTIMapError19() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GetReservationError").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077585", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTIGetReservationErrorMap(testData, "EMgetReservation19");
    }

    private void testTIGetReservationErrorMap(TestData testData, String errorCode) throws Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), false);

        //GetReservation
        final GetReservationVerificationInput input = GetReservationHelper.retrieveReservation(httpClient, requestGenerator, testData.getGuid(), errorCode);

        //cancel
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCancelRequest(), testData.getGuid());
        CancelHelper.cancelVerify(cancelVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger, false);

        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getRequest())));
        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getResponse())));
        //Verify error map
        GetReservationHelper.getReservationErrorMapVerification(input, spooferTransport,
                testData.getScenarios(), testData.getGuid(), logger);

    }




}
