package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSBvtSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.CancelResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.CostAndAvailResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.GetDetailsBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.GetReservationResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.ReserveResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.SearchResponsesBasicVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Created by jiyu on 8/26/16.
 */
public final class ExecutionHelper {
    //  validation message
    private final static String MESSAGE_SEARCH_VERIFICATION_PROMPT = "Search Verification run: ";
    private final static String MESSAGE_GETDETAILS_VERIFICATION_PROMPT = "GetDetails Verification run: ";
    private final static String MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT = "GetCostAndAvailability Verification run: ";
    private final static String MESSAGE_RESERVE_VERIFICATION_PROMPT = "Reserve Verification run: ";
    private final static String MESSAGE_GETRESERVATION_VERIFICATION_PROMPT = "GetReservation Verification run: ";

    private ExecutionHelper() {
    }


    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        return randomGuid;
    }

    //------- Search ------------------
    public static SCSRequestGenerator createSCSRequestGenerator(SearchVerificationInput searchVerificationInput) {
        return new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
    }

    public static String searchVerification(SearchVerificationInput searchVerificationInput,
                                            SpooferTransport spooferTransport,
                                            TestScenario scenarios,
                                            String guid,
                                            Logger logger) throws IOException {
        //final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        final Document spooferTransactions = null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final SearchResponsesBasicVerification verifier = new SearchResponsesBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    //  SCS search
    //  SCS search with car vendor/car catalog etc parameters
    public static SearchVerificationInput bvtSearch(HttpClient httpClient,
                                                    TestScenario scenarios,
                                                    String tuid, String guid) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        SCSBvtSearchRequestGenerator bvtSearchRequestGenerator = new SCSBvtSearchRequestGenerator();
        CarSupplyConnectivitySearchRequestType request = bvtSearchRequestGenerator.createSearchRequest(scenarios, tuid,
                "5974", SettingsProvider.BVTTEST_OFFAIRPORTLOCATIONLIST, SettingsProvider.BVTTEST_VENDORLIST);

        SimpleE3FIHttpTransport<CarSupplyConnectivitySearchRequestType, CarSupplyConnectivitySearchResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION, SettingsProvider.SERVICE_ADDRESS,
                30000, request, CarSupplyConnectivitySearchResponseType.class);
        RequestSender.sendWithTransport(transport, guid);
        CarSupplyConnectivitySearchResponseType response = transport.getServiceRequestContext().getResponse();

        return new SearchVerificationInput(request, response);
    }

    public static SearchVerificationInput search(TestData testData, DataSource carsInventoryDatasource)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsInventoryDatasource);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        return TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());
    }

    public static SearchVerificationInput search(TestData testData, SpooferTransport spooferTransport, Logger logger, DataSource carsDatasource)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsDatasource);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());
          searchVerification(searchVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger);
        return searchVerificationInput;
    }

    //--------------- GetDetail--------------------
    public static String getDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                SpooferTransport spooferTransport,
                                                TestScenario scenarios,
                                                String guid,
                                                Logger logger) throws Exception {
        final Document spooferTransactions = spooferTransport == null ? null : spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsBasicVerification verifier = new GetDetailsBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    //  SCS getDetails
    public static GetDetailsVerificationInput getDetails(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        requestGenerator.setDetailsReq(detailsRequest);
        final GetDetailsVerificationInput getDetailsVerificationInput =
                TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
        requestGenerator.setDetailsResp(getDetailsVerificationInput.getResponse());

        return getDetailsVerificationInput;
    }

    public static GetDetailsVerificationInput getDetailsHertzPrepay(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid)
            throws IOException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        for(CarSearchResultType searchResult : requestGenerator.getSearchResp().getCarSearchResultList().getCarSearchResult())
        {
            for(CarProductType carProduct : searchResult.getCarProductList().getCarProduct())
            {
                if(carProduct.getPrePayBoolean() && carProduct.getCarInventoryKey().getCarRate().getRateCategoryCode().equals(CommonConstantManager.RateCategory.PREPAY)
                        && carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 40)
                {
                    requestGenerator.setSelectedCarProduct(carProduct);
                    break;
                }
            }
        }

        //if can't find Hertz Car in search response, Exist and throw error.
        if(null == requestGenerator.getSelectedCarProduct())
        {
            return null;
        }

        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        requestGenerator.setDetailsReq(detailsRequest);
        final GetDetailsVerificationInput getDetailsVerificationInput =
                TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
        requestGenerator.setDetailsResp(getDetailsVerificationInput.getResponse());

        return getDetailsVerificationInput;
    }

    public static GetDetailsVerificationInput getDetails(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         SpooferTransport spooferTransport,
                                                         TestScenario scenarios,
                                                         String guid,
                                                         Logger logger) throws Exception {
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        requestGenerator.setDetailsReq(detailsRequest);
        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
        requestGenerator.setDetailsResp(getDetailsVerificationInput.getResponse());

        getDetailsVerification(getDetailsVerificationInput, spooferTransport, scenarios, guid, logger);

        return getDetailsVerificationInput;
    }


    //--------------CostAndAvail-------------------
    public static String getCostAndAvailabilityVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                            SpooferTransport spooferTransport,
                                                            TestScenario scenarios,
                                                            String guid,
                                                            Logger logger) throws Exception {
        final Document spooferTransactions = spooferTransport == null ? null : spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext getCostAndAvailVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final CostAndAvailResponseBasicVerification verifier = new CostAndAvailResponseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getCostAndAvailabilityVerificationInput, getCostAndAvailVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
        }

        return null;
    }


    //  SCS GetCostAndAvailability
    public static GetCostAndAvailabilityVerificationInput getCostAndAvailability(HttpClient httpClient,
                                                                                 SCSRequestGenerator requestGenerator,
                                                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();

        requestGenerator.setCostAndAvailReq(costandavailRequest);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION,
                        costandavailRequest, guid);
        requestGenerator.setCostAndAvailResp(getCostAndAvailabilityVerificationInput.getResponse());

        return getCostAndAvailabilityVerificationInput;
    }

    public static GetCostAndAvailabilityVerificationInput getCostAndAvailability(HttpClient httpClient,
                                                                                 SCSRequestGenerator requestGenerator,
                                                                                 SpooferTransport spooferTransport,
                                                                                 TestScenario scenarios,
                                                                                 String guid,
                                                                                 Logger logger) throws Exception {
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();

        requestGenerator.setCostAndAvailReq(costandavailRequest);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInput = TransportHelper.sendReceive(httpClient,
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, costandavailRequest, guid);
        requestGenerator.setCostAndAvailResp(getCostAndAvailVerificationInput.getResponse());

        getCostAndAvailabilityVerification(getCostAndAvailVerificationInput, spooferTransport, scenarios, guid, logger);

        return getCostAndAvailVerificationInput;
    }


    //---------------Reserve --------------------
    public static String reserveVerify(ReserveVerificationInput reserveVerificationInput,
                                       SpooferTransport spooferTransport,
                                       TestScenario scenarios,
                                       String guid,
                                       Logger logger) throws Exception {

        final Document spooferTransactions = spooferTransport == null ? null : spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final ReserveResponseBasicVerification verifier = new ReserveResponseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(reserveVerificationInput, reserveVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_RESERVE_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        requestGenerator.setReserveReq(reserveRequest);
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        return reserveVerificationInput;
    }

    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   SpooferTransport spooferTransport,
                                                   TestScenario scenarios,
                                                   String guid,
                                                   Logger logger) throws Exception {
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        requestGenerator.setReserveReq(reserveRequest);
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        reserveVerify(reserveVerificationInput, spooferTransport, scenarios, guid, logger);

        return reserveVerificationInput;
    }

    //----------GetReservation-------------------
    public static String getreservationVerify(GetReservationVerificationInput getreservationVerificationInput,
                                              SpooferTransport spooferTransport,
                                              TestScenario scenarios,
                                              String guid,
                                              Logger logger) throws Exception {
        final Document spooferTransactions = spooferTransport == null ? null : spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext getreservationVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetReservationResponseBasicVerification verifier = new GetReservationResponseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getreservationVerificationInput, getreservationVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
        }

        return null;
    }


    public static GetReservationVerificationInput getReservation(HttpClient httpClient,
                                                                 SCSRequestGenerator requestGenerator,
                                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityGetReservationRequestType getReservationRequest = requestGenerator.createGetReservationRequest();

        requestGenerator.setGetReservationReq(getReservationRequest);
        final GetReservationVerificationInput getReservationVerificationInput = TransportHelper.sendReceive(httpClient,
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequest, guid);
        requestGenerator.setGetReservationResp(getReservationVerificationInput.getResponse());

        return getReservationVerificationInput;
    }

    //---------------Cancel----------------------
    public static String cancelVerify(CancelVerificationInput cancelVerificationInput,
                                      SpooferTransport spooferTransport,
                                      TestScenario scenarios,
                                      String guid,
                                      Logger logger) throws Exception {
        final Document spooferTransactions = spooferTransport == null ? null : spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext cancelVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final CancelResponseBasicVerification verifier = new CancelResponseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(cancelVerificationInput, cancelVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    public static CancelVerificationInput cancel(HttpClient httpClient,
                                                 SCSRequestGenerator requestGenerator,
                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityCancelRequestType cancelRequest = requestGenerator.createCancelRequest();

        requestGenerator.setCancelReq(cancelRequest);
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient,
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, cancelRequest, guid);
        requestGenerator.setCancelResp(cancelVerificationInput.getResponse());

        return cancelVerificationInput;
    }
}