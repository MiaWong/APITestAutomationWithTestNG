package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorInputAndExpectOutput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorValues;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 3/27/2018.
 */
public class GetReservationErrorHandling extends SuiteContext
{
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs774100ASCSGetRservationReturnCurrencyNotAvailableErrors() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "774100", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSGetReservationErrorHandling(ErrorValues.Error_GetReservation_CurrencyNotAvailableError.getErrorInputAndExpectOutput(), parameters,
                "Amadeus_CurrencyNotAvailableError_FR_getReservation");
    }

    private void testASCSGetReservationErrorHandling(ErrorInputAndExpectOutput errInAndOut, TestData parameters, String templateOverride) throws Exception {
        //Search and basic verify
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //GetReservation and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));

        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", templateOverride).build(), parameters.getGuid());

        SCSRequestGenerator scsRequestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        //Reserve and basic verify
        final ReserveVerificationInput reserveVerificationInput = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);

//        ReserveVerificationHelper.reserveBasicVerification(reserveVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", templateOverride).build(), parameters.getGuid());
        final CarSupplyConnectivityGetReservationRequestType getReservationRequest = scsRequestGenerator.createGetReservationRequest();

        //error handling
        replaceRequestValues4ErrorHandling(getReservationRequest, errInAndOut);

        //Send Request
        GetReservationVerificationInput getReservationVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, getReservationRequest, parameters.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));
        }

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
        CommonVerificationHelper.errorHandlingVerification(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse()),
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT,
                errInAndOut.getExpectErrorMessage(), logger);
    }

    private void replaceRequestValues4ErrorHandling(CarSupplyConnectivityGetReservationRequestType getReservationRequest, ErrorInputAndExpectOutput errInAndOut)
    {
        for (int i = 0; i < errInAndOut.getInvalidFieldsAndValues().length; i++)
        {
            String invalidField = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[0];
            String invalidValue = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[1];

            if (invalidField.equals(ErrorInputAndExpectOutput.invalidFields.CurrencyCode))
            {
                getReservationRequest.getCarReservationList().getCarReservation().get(0).getCarProduct().setPointOfSupplyCurrencyCode(invalidValue);
            }
        }
    }
}
