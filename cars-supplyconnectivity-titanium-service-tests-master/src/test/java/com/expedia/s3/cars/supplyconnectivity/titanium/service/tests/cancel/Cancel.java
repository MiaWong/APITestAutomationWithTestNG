package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.GetReservationHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
/**
 * Created by jiyu on 8/30/16.
 */
public class Cancel extends SuiteContext
{
    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void casss1896Cancel() throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testCancel( CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                    "1083451",
                    randomGuid,
                    -1L);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896Cancel(@Optional("15") long vendorSupplierID,
                                @Optional("10") long carCategoryCode,
                                @Optional("4") long carTypeCode,
                                @Optional("1") long carTransmissionDriveCode,
                                @Optional("1") long carFuelACCode) throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testCancel( CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
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

    private void testCancel(TestScenario scenarios,
                            String tuid,
                            String guid,
                            long driverAge) throws  Exception
    {
        testCancel(scenarios, tuid, guid, driverAge, true, 0L, 0L, 0L, 0L, 0L);
    }

    private void testCancel(TestScenario scenarios,
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
        ReserveHelper.reserve(httpClient,
                                requestGenerator,
                                newGuid,
                                false,
                                isRandomPick,
                                vendorSupplierID,
                                carCategoryCode,
                                carTypeCode,
                                carTransmissionDriveCode,
                                carFuelACCode);

        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        GetReservationHelper.retrieveReservation(httpClient,
                                            requestGenerator,
                                            newGuid,
                                            false);

        newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final CancelVerificationInput cancelVerificationInput = CancelHelper.cancel(httpClient, requestGenerator, newGuid, false);
        CancelHelper.cancelVerify(   cancelVerificationInput,
                                        spooferTransport,
                                        scenarios,
                                        newGuid,
                                        false,
                                        null,
                                        true);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077714CancelTIMapOnairportRoundtrip() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077714", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTICancelMap(testData);
    }

    private void testTICancelMap(TestData testData) throws  Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final ReserveVerificationInput reserveInput = ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), false);

        //cancel
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCancelRequest(), testData.getGuid());

        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getRequest())));
        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getResponse())));
        //Do reserve BVT verification
        ReserveHelper.reserveVerify(reserveInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger, false);

        //Verify GDS map
        CancelHelper.cancelGDSMapVerification(cancelVerificationInput, spooferTransport,
                testData.getScenarios(), testData.getGuid(), logger);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077719CancelTIErrorMap8() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077719", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTICancelErrorMap(testData, "25");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077761CancelTIErrorMap19() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077761", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTICancelErrorMap(testData, "77");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077720CancelTIErrorMapEmptyPNR() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_LHR.getTestScenario(),
                "1077720", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTICancelErrorMap(testData, null);
    }

    private void testTICancelErrorMap(TestData testData, String errorCode) throws  Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final ReserveVerificationInput reserveInput = ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), false);

        //cancel
        final CancelVerificationInput cancelVerificationInput = CancelHelper.cancel(httpClient, requestGenerator, testData.getGuid(), errorCode);

        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getRequest())));
        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getResponse())));
        //Do reserve BVT verification
        ReserveHelper.reserveVerify(reserveInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger, false);

        //Verify GDS map
        CancelHelper.cancelErrorMapVerification(cancelVerificationInput, spooferTransport,
                testData.getScenarios(), testData.getGuid(), logger);

    }




}
