package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.cancel;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail
        .GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation
        .GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getreservation.GetReservationHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.RetryAnalyzer;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.Utils;

import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by vmohan on 12/07/16.
 */
public class Cancel extends SuiteContext
{

    @Test(retryAnalyzer = RetryAnalyzer.class, groups={TestGroup.BOOKING_REGRESSION})
    public void casss4462CANOffAirportCancel() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        testCancel(scenario, "4462", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.BOOKING_REGRESSION})
    public void casss4462CANOnAirportCancel() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        testCancel(scenario, "4462", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.BOOKING_REGRESSION})
    public void casss4462USAOffAirportCancel() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OffAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        testCancel(scenario, "4462", randomGuid);
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, groups = {TestGroup.BOOKING_REGRESSION})
    public void casss4462USAOnAirportCancel() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Sabre_USA_Standalone_RoundTrip_OnAirport_LAS.getTestScenario();
        final String randomGuid = Utils.setSpooferOverride(spooferTransport, scenario.getScenarionName());
        testCancel(scenario, "4462", randomGuid);
    }

    private void testCancel(TestScenario scenarios,
                            String tuid,
                            String guid) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //1,search
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSabreDatasource);
        final CarSupplyConnectivitySearchRequestType request = scsSearchRequestGenerator.createSpecialSearchRequest(testData, 50);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, request, guid);

        //2. getDetails
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        String newguid = null;

        newguid = Utils.setSpooferOverride(spooferTransport, scenarios.getScenarionName());
        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createDetailsRequest(), newguid);
        GetDetailsHelper.getDetailsVerification(getDetailsVerificationInput, spooferTransport, scenarios, newguid, logger, true);

        //3.getCostAndAvail
        final Document spooferTransactions = spooferTransport.retrieveRecords(newguid);
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, newguid, scenarios);

        newguid = Utils.setSpooferOverride(spooferTransport, scenarios.getScenarionName());
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCostAndAvailRequest(), newguid);
        CostAndAvailHelper.getCostAndAvailabilityVerification(getCostAndAvailabilityVerificationInput, spooferTransport, scenarios, newguid, logger, true);

        //4.Reserve
        final List<CarProductType> carProductType = getCostAndAvailabilityVerificationInput.getRequest().getCarProductList().getCarProduct();
        final long vendorSupplierId = carProductType.get(0).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
        final String carVehicleType = Utils.getVehTypeFromSpooferTransactions(getDetailsVerificationContext);
        final String splEquipment1 = PropertyResetHelper.STRING_SPECIAL_EQUIPMENT_TODDLER_SEAT;
        final String splEquipment2 = PropertyResetHelper.STRING_SPECIAL_EQUIPMENT_GPS;
        newguid = Utils.setReserveSpooferOverride(spooferTransport, scenarios.getScenarionName(), String.valueOf(vendorSupplierId), String.valueOf(carVehicleType),splEquipment1,splEquipment2);
        final ReserveVerificationInput reserveVerificationInput = ReserveHelper.reserve(httpClient,requestGenerator,newguid,false);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        ReserveHelper.reserveVerify(reserveVerificationInput, spooferTransport, scenarios, newguid, false, logger, true);

        //4.getReservation
        newguid = Utils.setGetReserveSpooferOverride(spooferTransport, scenarios.getScenarionName(), String.valueOf(vendorSupplierId), String.valueOf(carVehicleType));
        final GetReservationVerificationInput getReservationVerificationInput = GetReservationHelper.retrieveReservation( httpClient,requestGenerator,newguid,false);
        GetReservationHelper.getreservationVerify(getReservationVerificationInput, spooferTransport, scenarios, newguid, false, logger, true);

        //5.cancel
        newguid = Utils.setSpooferOverride(spooferTransport, scenarios.getScenarionName());
        final CancelVerificationInput cancelVerificationInput = CancelHelper.cancel(httpClient,requestGenerator,newguid,false);
        CancelHelper.cancelVerify(cancelVerificationInput, spooferTransport, scenarios, newguid, false, logger, true);

    }
}
