package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorInputAndExpectOutput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorValues;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 3/12/2018.
 */
public class CostAndAvailErrorHandling extends SuiteContext
{
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs774097AmadeusSCSGetCostAndAvailCurrencyNotAvailableErrors() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "774097", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSCostAndAvailErrorHandling(ErrorValues.Error_GetCostAndAvailability_CurrencyNotAvailableError.getErrorInputAndExpectOutput(), parameters,
                "Amadeus_CurrencyNotAvailableError_FR_RoundTrip_Shopping");
    }

    private void testASCSCostAndAvailErrorHandling(ErrorInputAndExpectOutput errInAndOut, TestData parameters, String templateOverride) throws Exception {
        //Search and basic verify
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //GetCostAndAvailability and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));

        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", templateOverride).build(), parameters.getGuid());
        CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput).createCostAndAvailRequest();

        //error handling
        replaceRequestValues4ErrorHandling(costAndAvailRequest, errInAndOut);

        //Send Request
        GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, costAndAvailRequest, parameters.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getResponse())));
        }

        //verify
        CommonVerificationHelper.errorHandlingVerification(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getResponse()),
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT,
                errInAndOut.getExpectErrorMessage(), logger);
    }

    private void replaceRequestValues4ErrorHandling(CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailabilityRequest, ErrorInputAndExpectOutput errInAndOut)
    {
        for (int i = 0; i < errInAndOut.getInvalidFieldsAndValues().length; i++)
        {
            String invalidField = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[0];
            String invalidValue = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[1];

            switch (invalidField)
            {
                case "CurrencyCode":
                    costAndAvailabilityRequest.setCurrencyCode(invalidValue);
                    break;
            }
        }
    }

}
