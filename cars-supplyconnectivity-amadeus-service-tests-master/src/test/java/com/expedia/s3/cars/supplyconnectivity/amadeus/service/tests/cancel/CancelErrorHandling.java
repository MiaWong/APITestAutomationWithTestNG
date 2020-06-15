package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorInputAndExpectOutput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorValues;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by miawang on 3/27/2018.
 */
public class CancelErrorHandling extends SuiteContext
{
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs774100ASCSCanceledPNRErrors() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "774100", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSCancelErrorHandling(ErrorValues.Error_Cancel_CanceledPNR.getErrorInputAndExpectOutput(), parameters,
                "Amadeus_FRA_Standalone_OneWay_OffAirport_CanceledPNR");
    }

    private void testASCSCancelErrorHandling(ErrorInputAndExpectOutput errInAndOut, TestData parameters, String templateOverride) throws Exception {
        SCSRequestGenerator scsRequestGenerator = CancelExecutionHelper.reserveAndCancel(parameters, spooferTransport, templateOverride);

        CarSupplyConnectivityCancelRequestType cancelRequestType = scsRequestGenerator.getCancelReq();

        //Send Request
        CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, cancelRequestType, parameters.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getResponse())));
        }

        //verify
        CommonVerificationHelper.errorHandlingVerification(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getResponse()),
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_CANCEL_VERIFICATION_PROMPT,
                errInAndOut.getExpectErrorMessage(), logger);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs528741ASCSCancelWarnHandling() throws Exception {
        //Search and basic verify
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "528741", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "Amadues_WECWarn").build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);
        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        SCSRequestGenerator scsRequestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        //GetDetails and basic verify
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsExecutionHelper.getDetails(parameters, scsRequestGenerator);
        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //Reserve and basic verify
        final ReserveVerificationInput reserveVerificationInput = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);
        ReserveVerificationHelper.reserveBasicVerification(reserveVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //Cancel and basic verify
        CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(parameters, scsRequestGenerator);
        CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getResponse())));

        //verify
        final CarProductListType carProductList = new CarProductListType();
        if(null != cancelVerificationInput.getResponse().getCarReservation() && null != cancelVerificationInput.getResponse().getCarReservation().getCarProduct())
        {
            carProductList.setCarProduct(new ArrayList<>());
            carProductList.getCarProduct().add(cancelVerificationInput.getResponse().getCarReservation().getCarProduct());
        }
        CommonVerificationHelper.supportHandleWarningVerification(cancelVerificationInput.getResponse().getErrorCollection().getUnclassifiedErrorList(),
                cancelVerificationInput.getResponse().getErrorCollection(),
                carProductList, false,
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_CANCEL_VERIFICATION_PROMPT, logger);
    }
}
