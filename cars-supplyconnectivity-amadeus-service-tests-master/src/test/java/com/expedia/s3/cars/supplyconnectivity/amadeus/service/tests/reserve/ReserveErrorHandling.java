package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve;

import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.cartypes.defn.v5.CustomerLocationType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorInputAndExpectOutput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorValues;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by miawang on 3/12/2018.
 */
public class ReserveErrorHandling extends SuiteContext
{
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs514146ASCSErrorMappingInvalidCollectionCityName() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "514146", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidCollectionCityName.getErrorInputAndExpectOutput(), parameters,
                "Amadues_GBR_Standalone_Roundtrip_OnAirport_NCE_WeeklyExtraDay_181587");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs514149ASCSErrorMappingInvalidDeliveryCityName() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "514149", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidDeliveryCityName.getErrorInputAndExpectOutput(), parameters,
                "Amadues_GBR_Standalone_Roundtrip_OnAirport_NCE_WeeklyExtraDay_181587");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs287905ASCSInvalidBillingNumber() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "287905", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidBillingCode.getErrorInputAndExpectOutput(), parameters,
                "TFS_287905_InvalidBillingNumber");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs503350ASCSErrorMappingInvalidLoyaltyCardNumber() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "503350", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidLoyaltyCardNumber.getErrorInputAndExpectOutput(), parameters,
                "Dynamic_ErrorMapping");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs486357ASCSErrorMappingInvalidFormOfPayment() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "486357", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidCreditCardFormOfPayment.getErrorInputAndExpectOutput(), parameters,
                "testCarAmaduesSCSReserve_ErrorMapping_2213");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss999610192ASCSErrorMappingUnknownDiscountNo() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "999610192", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidCorporateDiscount.getErrorInputAndExpectOutput(), parameters,
                "TestCarAmadeusSCSReserve_ErrorMapping_10192");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss999610193ASCSErrorMappingOutOfHours() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "999610193", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_OutOfHours.getErrorInputAndExpectOutput(), parameters,
                "TestCarAmadeusSCSReserve_ErrorMapping_10193");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss999610194ASCSErrorMappingInvalidFlightInfo() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "999610194", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidFlightInfo.getErrorInputAndExpectOutput(), parameters,
                "TestCarAmadeusSCSReserve_ErrorMapping_10194");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss999610197ASCSErrorMappingInvalidLocation() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "999610197", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSReserveErrorHandling(ErrorValues.Error_Reserve_InvalidLocation.getErrorInputAndExpectOutput(), parameters,
                "TestCarAmadeusSCSReserve_ErrorMapping_10197");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss11311ASCSErrorMappingDuplicateBooking() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "999610185", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        testASCSReserveErrorHandling(parameters, "TestCarAmadeusSCSReserve_ErrorMapping_Duplicate_Booking_10192", null, "Booking failed as vendor identified duplicate booking", false );
    }

    private void testASCSReserveErrorHandling(TestData parameters, String templateOverride, ErrorInputAndExpectOutput errInAndOut, String expectedMessage, boolean invalidFieldType) throws Exception
    {
        //Search and basic verify
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);
        logger.info("search request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.info("search response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //Reserve and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));

        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", templateOverride).build(), parameters.getGuid());
        SCSRequestGenerator scsRequestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        CarSupplyConnectivityReserveRequestType reserveRequest = scsRequestGenerator.createReserveRequest();

        logger.info("request before error handling xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));

        //error handling for invalid field type
        if (invalidFieldType)
        {
            replaceRequestValues4ErrorHandling(reserveRequest, errInAndOut);
        }

        //Send Request
        ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, parameters.getGuid());
        scsRequestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        //cancel and basic verify
        if (null != reserveVerificationInput.getResponse().getCarReservation() && null != reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode())
        {
            if (reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.BOOKED.getStatusCode())
                    || reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode()))
            {
                CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(parameters, scsRequestGenerator);

                CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
            }
        }

        //verify
        CommonVerificationHelper.errorHandlingVerification(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse()),
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RESERVE_VERIFICATION_PROMPT, expectedMessage, logger);

    }

    private void testASCSReserveErrorHandling(ErrorInputAndExpectOutput errInAndOut, TestData parameters, String templateOverride) throws Exception
    {
        testASCSReserveErrorHandling(parameters, templateOverride, errInAndOut, errInAndOut.getExpectErrorMessage(), true);
    }

    private void replaceRequestValues4ErrorHandling(CarSupplyConnectivityReserveRequestType reserveRequest, ErrorInputAndExpectOutput errInAndOut)
    {
        for (int i = 0; i < errInAndOut.getInvalidFieldsAndValues().length; i++)
        {
            String invalidField = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[0];
            String invalidValue = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[1];

            switch (invalidField)
            {
                case "CollectionLocation_CityName":
                    if (null == reserveRequest.getCollectionLocation())
                    {
                        reserveRequest.setCollectionLocation(new CustomerLocationType());
                    }
                    if (null == reserveRequest.getCollectionLocation().getAddress())
                    {
                        reserveRequest.getCollectionLocation().setAddress(new AddressType());
                    }
                    reserveRequest.getCollectionLocation().getAddress().setCityName(invalidValue);
                    break;
                case "DeliveryLocation_CityName":
                    if (null == reserveRequest.getDeliveryLocation())
                    {
                        reserveRequest.setDeliveryLocation(new CustomerLocationType());
                    }
                    if (null == reserveRequest.getDeliveryLocation().getAddress())
                    {
                        reserveRequest.getDeliveryLocation().setAddress(new AddressType());
                    }
                    reserveRequest.getDeliveryLocation().getAddress().setCityName(invalidValue);
                    break;
                case "BillingCode":
                    reserveRequest.setBillingCode(invalidValue);
                    break;
                case "LoyaltyCardNumber":
                case "CreditCardFormOfPayment":
                    //below code is use to mark use which template, use for template.
                    if (null == reserveRequest.getCarSpecialEquipmentList())
                    {
                        reserveRequest.setCarSpecialEquipmentList(new CarSpecialEquipmentListType());
                    }
                    if (null == reserveRequest.getCarSpecialEquipmentList().getCarSpecialEquipment())
                    {
                        reserveRequest.getCarSpecialEquipmentList().setCarSpecialEquipment(new ArrayList<>());
                    }
                    CarSpecialEquipmentType specialTemp = new CarSpecialEquipmentType();
                    specialTemp.setCarSpecialEquipmentCode(invalidValue);
                    reserveRequest.getCarSpecialEquipmentList().getCarSpecialEquipment().add(specialTemp);
                    break;
            }
        }
    }
}