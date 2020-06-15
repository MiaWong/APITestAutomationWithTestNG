package com.expedia.s3.cars.supply.service.requestsender;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.errorhandling.ErrorHandling;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.supply.messages.cancel.defn.v4.CarSupplyCancelRequestType;
import com.expedia.s3.cars.supply.messages.cancel.defn.v4.CarSupplyCancelResponseType;
import com.expedia.s3.cars.supply.messages.getcostandavailability.defn.v4.CarSupplyGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supply.messages.getcostandavailability.defn.v4.CarSupplyGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsRequestType;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsResponseType;
import com.expedia.s3.cars.supply.messages.getreservation.defn.v4.CarSupplyGetReservationRequestType;
import com.expedia.s3.cars.supply.messages.getreservation.defn.v4.CarSupplyGetReservationResponseType;
import com.expedia.s3.cars.supply.messages.location.search.defn.v1.CarSupplyLocationSearchRequest;
import com.expedia.s3.cars.supply.messages.location.search.defn.v1.CarSupplyLocationSearchResponse;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveRequestType;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveResponseType;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchRequestType;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchResponseType;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;
import com.expedia.s3.cars.supply.service.requestgenerators.LocationSearchRequestGenerator;
import com.expedia.s3.cars.supply.service.requestgenerators.SSRequestGenerator;
import com.expedia.s3.cars.supply.service.requestgenerators.SearchRequestGenerator;
import com.expedia.s3.cars.supply.service.verification.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yyang4 on 8/22/2016.
 */
public class SSRequestSender {
    public static DataSource carsInventoryDatasource= DatabaseSetting.createDataSource( SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER,
            SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN,
            SettingsProvider.DB_USER_NAME,
            SettingsProvider.DB_PASSWORD,
            SettingsProvider.DB_USE_PASSWORD);

    private SSRequestSender() {
    }


    public static SSRequestGenerator bvtSearch(TestScenario scenarios, String tuid, HttpClient httpClient,
                                            String guid) throws IOException {

        //  Create search Request
        final SearchRequestGenerator searchRequestGenerator = new SearchRequestGenerator();
        final CarSupplySearchRequestType request = searchRequestGenerator.createSearchRequest(scenarios, tuid);
        // Send request
        final SSRequestGenerator requestGenerator = sendSearchReq(scenarios, httpClient, tuid, request);

        requestGenerator.setBvtTest(true);

        //BVT verification TODO: Use common verification method once the common method is complete
        SearchResponseVerifier.verifyReturn(requestGenerator.getSearchResp(), scenarios);

        return requestGenerator;
    }

    public static SSRequestGenerator search(TestScenario scenarios, String tuid, HttpClient httpClient,
                                            String guid) throws IOException {

        //  Create search Request
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SearchRequestGenerator searchRequestGenerator = new SearchRequestGenerator(carsInventoryDatasource);
        final CarSupplySearchRequestType request = searchRequestGenerator.createSearchRequestD(testData);
        // Send request
        final SSRequestGenerator requestGenerator = sendSearchReq(scenarios, httpClient, tuid, request);
        //BVT verification TODO: Use common verification method once the common method is complete
        SearchResponseVerifier.verifyReturn(requestGenerator.getSearchResp(), scenarios);

        return requestGenerator;
    }

    public static SSRequestGenerator searchWithError(TestScenario scenarios, String tuid, HttpClient httpClient,
                                            String guid, ErrorHandling errorHandling) throws IOException {

        //  Create search Request
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SearchRequestGenerator searchRequestGenerator = new SearchRequestGenerator(carsInventoryDatasource);
        if(scenarios.getServiceProviderID() == 6) {
            final TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
           testScenarioSpecialHandleParam.setVendorSupplierID(41L);
            testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
        }
            final CarSupplySearchRequestType request = searchRequestGenerator.createSearchRequestD(testData);
        //Set invalid value
        request.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarRate().setCorporateDiscountCode(errorHandling.getInvalidValue());
        if(scenarios.getServiceProviderID() == 6)
        {

            final CarRateOverrideListType carRateOverrideListType = new CarRateOverrideListType();
            final CarRateOverrideType carRateOverride = new CarRateOverrideType();
            carRateOverride.setCorporateDiscountCode(errorHandling.getInvalidValue());
            carRateOverride.setVendorSupplierID(41L);
            carRateOverrideListType.setCarRateOverride(new ArrayList<CarRateOverrideType>());
            carRateOverrideListType.getCarRateOverride().add(carRateOverride);
            request.getCarSearchCriteriaList().getCarSearchCriteria().get(0).setCarRateOverrideList(carRateOverrideListType);
        }
        // Send request
        final SSRequestGenerator requestGenerator = sendSearchReq(scenarios, httpClient, guid, request);
        //Verify error xpath
        ErrorVerifier.verifySearchErrorXpath(requestGenerator.getSearchResp(),  errorHandling.getxPath(), errorHandling.getErrormessage());
        return requestGenerator;
    }

    public static SSRequestGenerator sendSearchReq(TestScenario scenarios, HttpClient httpClient, String guid, CarSupplySearchRequestType request)
    {
        final StringBuilder serviceAddress = new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "search" -> http://cars-supply-service.us-west-2.int.expedia.com/search
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/search");
        }
        final SimpleE3FIHttpTransport<CarSupplySearchRequestType, CarSupplySearchResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplySearchResponseType.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplySearchResponseType response = transport4.getServiceRequestContext().getResponse();

        Logger.getLogger("SSRequestSender").warn("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        Logger.getLogger("SSRequestSender").warn("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));

        //  Return the request and response so it can be used for following request, like GetDetails
        final SSRequestGenerator requestGenerator = new SSRequestGenerator(request, response);
        requestGenerator.setScenario(scenarios);


        return requestGenerator;
    }

    public static LocationSearchRequestGenerator locationSearch(LocationSearchTestScenario scenarios, String tuid, HttpClient httpClient,
                                                    String guid) throws IOException, DataAccessException {
        //  Create details request
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final LocationSearchRequestGenerator requestGenerator = new LocationSearchRequestGenerator();
        final CarSupplyLocationSearchRequest request = requestGenerator.createLocationSearchRequest(testData);

        //Set HTPP Transport
        final StringBuilder serviceAddress =new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "getdetails" -> http://cars-supply-service.us-west-2.int.expedia.com/getdetails
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/location/search");
        }
        else
        {
            serviceAddress.replace(serviceAddress.lastIndexOf("/"), serviceAddress.length(), "/location/search");
        }
        //http://chelcarjvafe101:52028/location/search
        final SimpleE3FIHttpTransport<CarSupplyLocationSearchRequest, CarSupplyLocationSearchResponse, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplyLocationSearchResponse.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyLocationSearchResponse response = transport4.getServiceRequestContext().getResponse();

        //  Return response so it can be used for verification
        requestGenerator.setResponse(response);

        //BVT verification TODO: Use common verification method once the common method is complete
        LocationSearchResponseVerifier.verifyReturn(response, scenarios);
        return requestGenerator;
    }

    public static SSRequestGenerator getDetail(TestScenario scenarios, String tuid, HttpClient httpClient,
                                               String guid, final SSRequestGenerator requestGenerator) throws IOException, DataAccessException {
        //  Create details request
        final CarSupplyGetDetailsRequestType request = requestGenerator.createDetailsRequest();

        //Send request
        sendGetDetailsRequest(request, httpClient, guid, requestGenerator);

        //BVT verification TODO: Use common verification method once the common method is complete
        DetailsResponseVerifier.verifyReturn(requestGenerator.getDetailsResp(), scenarios);
        return requestGenerator;
    }

    public static void sendGetDetailsRequest(CarSupplyGetDetailsRequestType request, HttpClient httpClient, String guid
            , final SSRequestGenerator requestGenerator)
    {
        final StringBuilder serviceAddress =new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "getdetails" -> http://cars-supply-service.us-west-2.int.expedia.com/getdetails
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/getdetails");
        }

        final SimpleE3FIHttpTransport<CarSupplyGetDetailsRequestType, CarSupplyGetDetailsResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplyGetDetailsResponseType.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyGetDetailsResponseType response = transport4.getServiceRequestContext().getResponse();

        Logger.getLogger("SSRequestSender").warn("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        Logger.getLogger("SSRequestSender").warn("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));


        //  Return the request and response so it can be used for following request, like GetDetails
        requestGenerator.setDetailsReq(request);
        requestGenerator.setDetailsResp(response);
    }

    public static SSRequestGenerator getDetailWithError(HttpClient httpClient,
                                               String guid, ErrorHandling errorHandling, final SSRequestGenerator requestGenerator) throws IOException, DataAccessException {
        //  Create details request
        final CarSupplyGetDetailsRequestType request = requestGenerator.createDetailsRequest();

        // set invalid value to CD Code
        request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setCorporateDiscountCode(errorHandling.getInvalidValue());

        //Send request
        sendGetDetailsRequest(request, httpClient, guid, requestGenerator);

        //Verify error xpath
        ErrorVerifier.verifyGetDetailsErrorXpath(requestGenerator.getDetailsResp(),  errorHandling.getxPath(), errorHandling.getErrormessage());
        return requestGenerator;
    }

    public static SSRequestGenerator getCostAndAvail(TestScenario scenarios, HttpClient httpClient,
                                                     String guid, final SSRequestGenerator requestGenerator) throws IOException, DataAccessException {
        //  Create CostAndAvail request
        final CarSupplyGetCostAndAvailabilityRequestType request = requestGenerator.createCostAndAvailRequest();

        //Send request and get response
        sendCostAndAvailRequest(request, httpClient, guid, requestGenerator);

        //BVT verification TODO: Use common verification method once the common method is complete
        GetCostAndAvailResponseVerifier.verifyReturn(requestGenerator.getCostAndAvailResp(), scenarios);

        return requestGenerator;
    }

    public static SSRequestGenerator getCostAndAvailWithError(HttpClient httpClient,
                                                     String guid, ErrorHandling errorHandling, final SSRequestGenerator requestGenerator) throws IOException, DataAccessException {
        //  Create CostAndAvail request
        final CarSupplyGetCostAndAvailabilityRequestType request = requestGenerator.createCostAndAvailRequest();

        // set invalid value to CD Code
        request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setCorporateDiscountCode(errorHandling.getInvalidValue());

        //Send request and get response
        sendCostAndAvailRequest(request, httpClient, guid, requestGenerator);

        //Verify error xpath
        ErrorVerifier.verifyCostAvailErrorXpath(requestGenerator.getCostAndAvailResp(),  errorHandling.getxPath(), errorHandling.getErrormessage());

        return requestGenerator;
    }

    public static SSRequestGenerator sendCostAndAvailRequest(final CarSupplyGetCostAndAvailabilityRequestType request, HttpClient httpClient,
                                                     String guid, final SSRequestGenerator requestGenerator) throws IOException, DataAccessException {
        //Set HTPP Transport
        final StringBuilder serviceAddress =new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "getcostandavail" -> http://cars-supply-service.us-west-2.int.expedia.com/getcostandavail
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/getcostandavail");
        }
        final SimpleE3FIHttpTransport<CarSupplyGetCostAndAvailabilityRequestType, CarSupplyGetCostAndAvailabilityResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplyGetCostAndAvailabilityResponseType.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyGetCostAndAvailabilityResponseType response = transport4.getServiceRequestContext().getResponse();

        //  Return the request and response so it can be used for following request, like GetDetails
        requestGenerator.setCostAndAvailReq(request);
        requestGenerator.setCostAndAvailResp(response);

        Logger.getLogger("SSRequestSender").warn("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        Logger.getLogger("SSRequestSender").warn("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));


        return requestGenerator;
    }

    public static SSRequestGenerator reserve(TestScenario scenarios, String tuid, HttpClient httpClient,
                                             String guid, final SSRequestGenerator requestGenerator,
                                             boolean... setVoucher) throws IOException, DataAccessException {
        //  Create reserve request
        final CarSupplyReserveRequestType request = requestGenerator.createReserveRequest();

        //Set voucher
        if(null != setVoucher && setVoucher.length > 0 && setVoucher[0])
        {
            setReserveVoucher(request);
        }

        //Send request
        sendReserveRequest(request, httpClient, guid, requestGenerator);

        //BVT verification TODO: Use common verification method once the common method is complete
        ReserveVerifier.verifyReturn(requestGenerator.getReserveResp(), scenarios);
        return requestGenerator;
    }

    public static SSRequestGenerator reserveWithError(HttpClient httpClient,
                                             String guid, ErrorHandling errorHandling, final SSRequestGenerator requestGenerator)
            throws IOException, DataAccessException {
        //  Create reservation request
        final CarSupplyReserveRequestType request = requestGenerator.createReserveRequest();

        // set invalid value to CD Code
        request.getCarProduct().getCarInventoryKey().getCarRate().setCorporateDiscountCode(errorHandling.getInvalidValue());

        //Send request
        sendReserveRequest(request, httpClient, guid, requestGenerator);

        //Verify error xpath
        ErrorVerifier.verifyReserveErrorXpath(requestGenerator.getReserveResp(),  errorHandling.getxPath(), errorHandling.getErrormessage());
        return requestGenerator;
    }

    public static SSRequestGenerator sendReserveRequest(final CarSupplyReserveRequestType request, HttpClient httpClient,
                                             String guid, final SSRequestGenerator requestGenerator) throws IOException, DataAccessException {
        //Set HTPP Transport
        final StringBuilder serviceAddress =new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "reserve" -> http://cars-supply-service.us-west-2.int.expedia.com/reserve
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/reserve");
        }
        final SimpleE3FIHttpTransport<CarSupplyReserveRequestType, CarSupplyReserveResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplyReserveResponseType.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyReserveResponseType response = transport4.getServiceRequestContext().getResponse();

        //  Return the request and response so it can be used for following request, like GetDetails
        requestGenerator.setReserveReq(request);
        requestGenerator.setReserveResp(response);

        return requestGenerator;
    }


    public static void setReserveVoucher(CarSupplyReserveRequestType request)
    {
        //set voucher
        final ReferenceListType referenceList = new ReferenceListType();
        final ReferenceType reference = new ReferenceType();
        reference.setReferenceCategoryCode("Voucher");
        reference.setReferenceCode("04ERH4C");
        referenceList.setReference(new ArrayList<ReferenceType>());
        referenceList.getReference().add(reference);
        request.setReferenceList(referenceList);
    }

    public static SSRequestGenerator getReservation(HttpClient httpClient,
                                                    String guid, final SSRequestGenerator requestGenerator) throws IOException {
        //  Create reservation request
        final CarSupplyGetReservationRequestType request = requestGenerator.createGetReservationRequest();

        //Send request
        sendGetReservationRequest(request, httpClient, guid, requestGenerator);

        //BVT verification TODO: Use common verification method once the common method is complete
        GetReservationVerifier.verifyReturn(requestGenerator.getGetReservationResp());
        return requestGenerator;
    }

    public static SSRequestGenerator getReservationWithError(HttpClient httpClient,
                                                    String guid, ErrorHandling errorHandling,  final SSRequestGenerator requestGenerator) throws IOException {
        //  Create reservation request
        final CarSupplyGetReservationRequestType request = requestGenerator.createGetReservationRequest();

        //Set invalid PNR
        if(null != errorHandling.getxPath() && errorHandling.getxPath().contains("ReferenceCategoryCode = 'PNR"))
        {
            for (final ReferenceType reference: request.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference()
                    ) {
                if(reference.getReferenceCategoryCode().equals("PNR"))
                {
                    reference.setReferenceCode(errorHandling.getInvalidValue());
                }
            }
        }

        //Set invalid Traveler name
        if(null != errorHandling.getxPath() && errorHandling.getxPath().contains("ReferenceCategoryCode = 'LastName"))
        {
            request.getCarReservationList().getCarReservation().get(0).getTravelerList().getTraveler().get(0)
                    .getPerson().getPersonName().setLastName(errorHandling.getInvalidValue());
        }

        //Send request
        sendGetReservationRequest(request, httpClient, guid, requestGenerator);

        //Verify error
        ErrorVerifier.verifyGetReservationError(requestGenerator.getGetReservationResp(),
                errorHandling.getErrorType(), errorHandling.getxPath(), errorHandling.getErrormessage());

        //for CurrencyNotAvailableError, car should be returned
        if(errorHandling.getErrorType().equals("CurrencyNotAvailableError")){
            GetReservationVerifier.verifyReturn(requestGenerator.getGetReservationResp());
        }

        return requestGenerator;
    }

    public static SSRequestGenerator sendGetReservationRequest(final CarSupplyGetReservationRequestType request, HttpClient httpClient,
                                                    String guid, final SSRequestGenerator requestGenerator) throws IOException {
        //Set HTPP Transport
        final StringBuilder serviceAddress =new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "getreservation" -> http://cars-supply-service.us-west-2.int.expedia.com/getreservation
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/getreservation");
        }

        final SimpleE3FIHttpTransport<CarSupplyGetReservationRequestType, CarSupplyGetReservationResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplyGetReservationResponseType.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyGetReservationResponseType response = transport4.getServiceRequestContext().getResponse();

        //  Return the request and response so it can be used for following request, like GetDetails
        requestGenerator.setGetReservationReq(request);
        requestGenerator.setGetReservationResp(response);

        return requestGenerator;
    }

    public static SSRequestGenerator cancel(TestScenario scenarios, String tuid, HttpClient httpClient,
                                            String guid, final SSRequestGenerator requestGenerator) throws IOException {
        //  Create cancel request
        final CarSupplyCancelRequestType request = requestGenerator.createCancelRequest();

        //Send request
        sendCancelRequest(request, httpClient, guid, requestGenerator);

        //BVT verification TODO: Use common verification method once the common method is complete
        CancelVerifier.verifyReturn(requestGenerator.getCancelResp(), scenarios);
        return requestGenerator;
    }

    public static SSRequestGenerator cancelWithError(HttpClient httpClient, String guid, ErrorHandling errorHandling,
                                                     final SSRequestGenerator requestGenerator) throws IOException {
        //  Create cancel request
        final CarSupplyCancelRequestType request = requestGenerator.createCancelRequest();

        //Set invalid PNR
        if(null != errorHandling.getxPath() && errorHandling.getxPath().contains("ReferenceCategoryCode = 'PNR"))
        {
            for (final ReferenceType reference: request.getCarReservation().getReferenceList().getReference()
                    ) {
                if(reference.getReferenceCategoryCode().equals("PNR"))
                {
                    reference.setReferenceCode(errorHandling.getInvalidValue());
                }
            }
        }

        //Send request
        sendCancelRequest(request, httpClient, guid, requestGenerator);

        //Verify error
        ErrorVerifier.verifyCancelErrorXpath(requestGenerator.getCancelResp(), errorHandling.getErrorType(), errorHandling.getxPath(),
                errorHandling.getErrormessage());
        return requestGenerator;
    }

    public static SSRequestGenerator sendCancelRequest(final CarSupplyCancelRequestType request, HttpClient httpClient,
                                            String guid, final SSRequestGenerator requestGenerator) throws IOException {
        //Set HTPP Transport
        final StringBuilder serviceAddress =new StringBuilder().append(SettingsProvider.SERVICE_ADDRESS);
        //Handle aws adress - append "cancel" -> http://cars-supply-service.us-west-2.int.expedia.com/cancel
        if(serviceAddress.toString().contains("cars-supply-service"))
        {
            serviceAddress.append("/cancel");
        }
        final SimpleE3FIHttpTransport<CarSupplyCancelRequestType, CarSupplyCancelResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                serviceAddress.toString(),
                30000, request, CarSupplyCancelResponseType.class);

        //Send request and get response
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyCancelResponseType response = transport4.getServiceRequestContext().getResponse();

        //  Return the request and response so it can be used for following request, like GetDetails
        requestGenerator.setCancelReq(request);
        requestGenerator.setCancelResp(response);

        return requestGenerator;
    }
}
