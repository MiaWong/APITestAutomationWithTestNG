package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorInputAndExpectOutput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ErrorValues;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.apache.log4j.Priority;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by MiaWang on 12/26/2017.
 */
public class SearchErrorHandling extends SuiteContext
{
    //TFS_175448_AmadeusSCS Search ErrorHandling - Car Company Not At Location
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs774097CarSCSGetCostAndAvailReturnCurrencyNotAvailableErrors() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "774097", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_Invalid_Pickup_Or_Dropoff_Location.getErrorInputAndExpectOutput(), parameters);
    }

    //TFS_175942_AmadeusSCS Search ErrorHandling - Invalid Location Type _ OffAirport_FRA_OneWay_MultipleLocations_bothLocationCodeWrong
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs175942AmadeusSCSOffOnewayFRAInvalidLocationTypeBothWrong() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCE.getTestScenario(),
                "175942", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_multiple_Invalid_Location_Type.getErrorInputAndExpectOutput(), parameters);
    }

    //not working in old framework
    //TFS_175944_AmadeusSCS Search ErrorHandling - Invalid Location Type _ OnAir_GBR_RoundTrip
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs175944AmadeusSCSOn_RoundTripGBRInvalidLocationType() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCE.getTestScenario(),
                "175944", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_multiple_Invalid_Location_Type.getErrorInputAndExpectOutput(), parameters);
    }

    //TFS_175952_AmadeusSCS Search ErrorHandling - No Rates For Required Company-City(InvalidCDcode)_OnAir_GBR_oneway
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs175952AmadeusSCSOnRoundTripGBRNoRatesForRequiredCompanyInvalidCDcode() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "175952", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_NoRates_InvalidCDcode.getErrorInputAndExpectOutput(), parameters);
    }

    //TFS_175954_AmadeusSCS Search ErrorHandling - No Rates For Required Company-City_OffAir_FRA_oneway
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs175954AmadeusSCSOffOnewayFRANoRatesForRequiredCompany() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "175954", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_NoRates.getErrorInputAndExpectOutput(), parameters);
    }

    //TFS_175948_AmadeusSCS Search ErrorHandling - Invalid Or Missing - Company Code_OffAir_FRA_oneway
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs175948AmadeusSCSOffRoundTripFRAInvalidOrMissingSupplierID() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "175948", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_InvalidSupplierID.getErrorInputAndExpectOutput(), parameters);
    }

    //TFS_175956_AmadeusSCS Search ErrorHandling - UNABLE TO PROCESS(Invalid VendorID)_OnAir_GBR_roundTrip
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs175956AmadeusSCSOnRoundTripGBRUnableToProcessInvalidSupplierID() throws Exception
    {
        TestData parameters = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "175956", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        testASCSSearchErrorHandling(ErrorValues.Error_search_UnableToProcess_InvalidSupplierID.getErrorInputAndExpectOutput(), parameters);
    }

    private void testASCSSearchErrorHandling(ErrorInputAndExpectOutput errInAndOut, TestData testData) throws IOException,
            DataAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);

        CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("request without special handle xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        }

        IVerification.VerificationResult result = null;

        if (null == searchRequest.getCarSearchCriteriaList()
                || (null != searchRequest.getCarSearchCriteriaList() && null == searchRequest.getCarSearchCriteriaList().getCarSearchCriteria())
                || (null != searchRequest.getCarSearchCriteriaList() && null != searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()
                && searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().size() == 0))
        {
            result = new IVerification.VerificationResult("isExpectErrorMessageVerification", false,
                    Arrays.asList("There is no SearchCriteria exist in Search request."));
            Assert.fail(result.toString());
        } else
        {
            replaceRequestValues4ErrorHandling(searchRequest, errInAndOut);
        }

        //Send Request
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        if (logger.isEnabledFor(Priority.INFO))
        {
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
        }

        //verify
        CommonVerificationHelper.errorHandlingVerification(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse()),
                CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RESERVE_VERIFICATION_PROMPT,
                errInAndOut.getExpectErrorMessage(), logger);
    }

    private void replaceRequestValues4ErrorHandling(CarSupplyConnectivitySearchRequestType searchRequest, ErrorInputAndExpectOutput errInAndOut)
    {
        boolean isSearchCriteriaHandled = false;
        for (int i = 0; i < errInAndOut.getInvalidFieldsAndValues().length; i++)
        {
            final int invalidSearchCriteriaIndex = Integer.parseInt(errInAndOut.getInvalidFieldsAndValues()[i].split("-")[0]);
            String invalidField = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[1];
            String invalidValue = errInAndOut.getInvalidFieldsAndValues()[i].split("-")[2];

            switch (invalidField)
            {
                case "CriteriaListSize" :
                    if(!isSearchCriteriaHandled)
                    {
                        List<CarSearchCriteriaType> replacementCriteria = new ArrayList<>();
                        for (int q = 0; q < Integer.parseInt(invalidValue); q++)
                        {
                            replacementCriteria.add(searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(q));
                        }
                        searchRequest.getCarSearchCriteriaList().setCarSearchCriteria(replacementCriteria);
                        isSearchCriteriaHandled = true;
                    }
                    break;

                case "Vendor":
                    List<Long> vendorSupplierIDArrayList = new ArrayList();
                    vendorSupplierIDArrayList.add(Long.parseLong(invalidValue));
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getVendorSupplierIDList().setVendorSupplierID(vendorSupplierIDArrayList);
                    break;
                case "LocationCode":
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getStartCarLocationKey().
                            setLocationCode(invalidValue);
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getEndCarLocationKey().
                            setLocationCode(invalidValue);
                    break;
                case "StartLocationCode":
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getStartCarLocationKey().
                            setLocationCode(invalidValue);
                    break;
                case "EndLocationCode":
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getEndCarLocationKey().
                            setLocationCode(invalidValue);
                    break;
                case "CarLocationCategoryCode":
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getStartCarLocationKey().
                            setCarLocationCategoryCode(invalidValue);
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getEndCarLocationKey().
                            setCarLocationCategoryCode(invalidValue);
                    break;
                case "SupplierRawText":
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarTransportationSegment().getStartCarLocationKey().
                            setSupplierRawText(invalidValue);
                    break;
                case "CDcode":
                    if (null == searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList())
                    {
                        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).setCarRateOverrideList(new CarRateOverrideListType());
                    }
                    if (null == searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList().getCarRateOverride())
                    {
                        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList().setCarRateOverride(new ArrayList<>());
                    }
                    if (searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList().getCarRateOverride().size() == 0)
                    {
                        CarRateOverrideType carRate = new CarRateOverrideType();
                        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList().getCarRateOverride().add(carRate);
                    }
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList().getCarRateOverride().get(0).
                            setCorporateDiscountCode(invalidValue);
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getCarRateOverrideList().getCarRateOverride().get(0)
                            .setVendorSupplierID(searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).getVendorSupplierIDList().getVendorSupplierID().get(0));
                    break;
                case "Date":
//                        lastString = errValueList[i].Replace(firstString + "-", "");
//                        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().
//                                getSegmentDateTimeRange().getStartDateTimeRange().setMinDateTime(Convert.ToDateTime(invalidValue));
//                        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarTransportationSegment().
//                                getSegmentDateTimeRange().getStartDateTimeRange().setMaxDateTime(Convert.ToDateTime(invalidValue));
                    break;
                case "CurrencyCode":
                    searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(invalidSearchCriteriaIndex).setCurrencyCode(invalidValue);
                    break;
            }
        }
    }
}