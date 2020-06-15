package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorInputAndExpectOutput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorValues;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 2/28/2018.
 */
public class GetDetailsErrorHandling extends SuiteContext
{
    //292201 - Amadues SCS GetDetail ErrorHandling - Invalid RateCode length
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292201AmadeusSCSOnRoundTripInvalidRateCodeLength() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "292201", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSGetDetailErrorHandling(ErrorValues.Error_GetDetails_InvalidRateCodeLength.getErrorInputAndExpectOutput(), parameters,
                "Amadues_GBR_Standalone_Roundtrip_OnAirport_NCE_WeeklyExtraDay_181587");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs774105AmadeusSCSGetGetDetailsCurrencyNotAvailableErrors() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "774105", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSGetDetailErrorHandling(ErrorValues.Error_GetDetails_CurrencyNotAvailableError.getErrorInputAndExpectOutput(), parameters,
                "Amadeus_CurrencyNotAvailableError_FR_RoundTrip_Shopping");
    }

    private void testASCSGetDetailErrorHandling(ErrorInputAndExpectOutput errInAndOut, TestData parameters, String templateOverride) throws Exception {
        //Search and basic verify
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //GetDetails and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));

        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", templateOverride).build(), parameters.getGuid());
        CarSupplyConnectivityGetDetailsRequestType detailsRequest = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput).createDetailsRequest();

        //error handling
        replaceRequestValues4ErrorHandling(detailsRequest, errInAndOut);

        //Send Request
        GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, parameters.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        }

        //verify
        CommonVerificationHelper.errorHandlingVerification(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse()),
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT,
                errInAndOut.getExpectErrorMessage(), logger);
    }

    private void replaceRequestValues4ErrorHandling(CarSupplyConnectivityGetDetailsRequestType getDetailsRequest, ErrorInputAndExpectOutput errInAndOut)
    {
        for (int i = 0; i < errInAndOut.getInvalidFieldsAndValues().length; i++)
        {
            String invalidField = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[0];
            String invalidValue = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[1];

            switch (invalidField)
            {
                case "CurrencyCode":
                    getDetailsRequest.setCurrencyCode(invalidValue);
                    break;
                case "RateCode":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setRateCode(invalidValue);
                    break;
                case "RateQuaCode":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setCarRateQualifierCode(invalidValue);
                    break;
                case "RateCategory":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setRateCategoryCode(invalidValue);
                    break;
                case "LocationCode":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarPickupLocation().getCarLocationKey().setLocationCode(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode(invalidValue);

                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarDropOffLocation().getCarLocationKey().setLocationCode(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode(invalidValue);
                    break;
                case "StartLocationCode":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarPickupLocation().getCarLocationKey().setLocationCode(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode(invalidValue);
                    break;
                case "EndLocationCode":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarDropOffLocation().getCarLocationKey().setLocationCode(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode(invalidValue);
                    break;
                case "CarLocationCategoryCode":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarPickupLocation().getCarLocationKey().setCarLocationCategoryCode(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setCarLocationCategoryCode(invalidValue);

                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarDropOffLocation().getCarLocationKey().setCarLocationCategoryCode(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setCarLocationCategoryCode(invalidValue);
                    break;
                case "SupplierRawText":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarPickupLocation().getCarLocationKey().setSupplierRawText(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setSupplierRawText(invalidValue);

                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarDropOffLocation().getCarLocationKey().setSupplierRawText(invalidValue);
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setSupplierRawText(invalidValue);
                    break;
                case "Vendor":
                    getDetailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(Long.parseLong(invalidValue));
                    break;
            }
        }
    }
}
