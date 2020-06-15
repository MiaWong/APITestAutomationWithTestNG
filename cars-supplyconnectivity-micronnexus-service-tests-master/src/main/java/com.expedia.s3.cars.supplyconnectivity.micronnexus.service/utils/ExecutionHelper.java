package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils;

import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.basic.VerifyGetCostAndAvailabilityBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.basic.VerifySearchResponseNotEmpty;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail.GetCostAndAvailCarRateDetailVerification;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail.RetrySearchForGetdetailAndCostandAvailVerify;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail.GetDetailsCarRateDetailVerification;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail.GetDetailsPhoneListVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fehu on 9/29/2016.
 */
public final class ExecutionHelper {
    //  validation message
    private final static String MESSAGE_SEARCH_VERIFICATION_PROMPT = "Search Verification run: ";
    private final static String MESSAGE_GETDETAILS_VERIFICATION_PROMPT = "GetDetails Verification run: ";
    private final static String MESSAGE_GETCOSTANDAVAIL_VERIFICATION_PROMPT = "GetCostAndAvail Verification run: ";


    private ExecutionHelper() {
    }


    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        if (SettingsProvider.SPOOFER_ENABLE) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        }
        return randomGuid;
    }

    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport, String templateName) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        if (SettingsProvider.SPOOFER_ENABLE) {
            final SpooferTransport.OverridesBuilder overridesBuilder = SpooferTransport.OverridesBuilder.newBuilder();
            overridesBuilder.withOverride("ScenarioName", templateName);

            spooferTransport.setOverrides(overridesBuilder.build(), randomGuid);
        }
        return randomGuid;
    }
    /*
    ***********************************************************************
    *                                Search                               *
    ***********************************************************************
    */

    //  generator for non-SCS-Search request
    public static SCSRequestGenerator createSCSRequestGenerator(SearchVerificationInput searchVerificationInput)
    {
        return new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
    }


    public static String searchVerification(SearchVerificationInput searchVerificationInput,
                                            SpooferTransport spooferTransport,
                                            TestScenario scenarios,
                                            String guid,
                                            Logger logger) throws IOException
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final VerifySearchResponseNotEmpty verifier = new VerifySearchResponseNotEmpty();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

        if (logger != null)
        {
            logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    //  SCS search
    public static SearchVerificationInput search(TestData testData,
                                                 SpooferTransport spooferTransport,
                                                 Logger logger,
                                                 DataSource dataSource) throws IOException, InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException,DataAccessException
    {
        final SCSSearchRequestGenerator requestGenerator = new SCSSearchRequestGenerator(dataSource);
        final CarSupplyConnectivitySearchRequestType searchRequest = requestGenerator.createSearchRequest(testData);
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(),
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());
        searchVerification(searchVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), logger);
        return searchVerificationInput;
    }

    /*
    ***********************************************************************
    *                             Get Details                             *
    ***********************************************************************
    */

    //  SCS getDetails
    public static GetDetailsVerificationInput getDetails(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        createPhoneList(detailsRequest);
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
    }

    public static GetDetailsVerificationInput getDetail(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
    }

    //  SCS getDetails
    public static GetReservationVerificationInput getReservation(HttpClient httpClient,
                                                                 SCSRequestGenerator requestGenerator,
                                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetReservationRequestType getReservationRequestType = requestGenerator.createGetReservationRequest();

        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequestType, guid);
    }


    public static  CancelVerificationInput cancel(HttpClient httpClient,
                                                  SCSRequestGenerator requestGenerator,
                                                  String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator.createCancelRequest();

        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, cancelRequestType, guid);
    }


    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityReserveRequestType reserveRequestType = requestGenerator.createReserveRequest();

        requestGenerator.setReserveReq(reserveRequestType);
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequestType, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        return reserveVerificationInput;

    }


    public static String getDetailsCarRateDetailVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                             SpooferTransport spooferTransport,
                                                             TestScenario scenarios,
                                                             String guid,
                                                             Logger logger) throws Exception
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsCarRateDetailVerification verifier = new GetDetailsCarRateDetailVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null)
        {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    public static String getDetailsPhoneListVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                         SpooferTransport spooferTransport,
                                                         TestScenario scenarios,
                                                         String guid,
                                                         Logger logger) throws Exception
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsPhoneListVerification verifier = new GetDetailsPhoneListVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null)
        {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
        }

        return null;
    }
    public static String getDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                            SpooferTransport spooferTransport,
                                            TestScenario scenarios,
                                            String guid,
                                            Logger logger) throws IOException
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final VerifyGetDetailsBasic verifier = new VerifyGetDetailsBasic();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, searchVerificationContext);

        if (logger != null)
        {
            logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
        }

        return null;
    }
    private static void createPhoneList(CarSupplyConnectivityGetDetailsRequestType detailsRequest)
    {
        final PhoneListType phoneListType = new PhoneListType();
        final List<PhoneType> phoneList = new ArrayList<PhoneType>();
        phoneListType.setPhone(phoneList);
        final PhoneType phoneType1 = new PhoneType();
        phoneType1.setPhoneAreaCode("310");
        phoneType1.setPhoneCategoryCode("2");
        phoneType1.setPhoneCountryCode("1");
        phoneType1.setPhoneNumber("649-5400");
        final PhoneType phoneType2 = new PhoneType();
        phoneType2.setPhoneAreaCode("800");
        phoneType2.setPhoneCategoryCode("0");
        phoneType2.setPhoneCountryCode("1");
        phoneType2.setPhoneNumber("736-8222");
        phoneList.add(phoneType1);
        phoneList.add(phoneType2);
        detailsRequest.getCarProductList().getCarProduct().get(0).getCarPickupLocation().setPhoneList(phoneListType);
    }

        /*
    ***********************************************************************
    *                      Get Cost and Availability                      *
    ***********************************************************************
    */

    public static GetCostAndAvailabilityVerificationInput getCostAndAvail(HttpClient httpClient,
                                                                     SCSRequestGenerator requestGenerator,
                                                                     String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest = requestGenerator.createCostAndAvailRequest();

        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, costAndAvailRequest, guid);
    }

    public static String getCostAndAvailCarRateDetailVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInput,
                                                             SpooferTransport spooferTransport,
                                                             TestScenario scenarios,
                                                             String guid,
                                                             Logger logger) throws Exception
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getCostAndAvailVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetCostAndAvailCarRateDetailVerification verifier = new GetCostAndAvailCarRateDetailVerification();
        final IVerification.VerificationResult result = verifier.verify(getCostAndAvailVerificationInput, getCostAndAvailVerificationContext);

        if (logger != null)
        {
            logger.info(MESSAGE_GETCOSTANDAVAIL_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    public static IVerification.VerificationResult retrySearchVerify(BasicVerificationInput verificationInput,
                                                                     SpooferTransport spooferTransport,
                                                                     TestScenario scenarios,
                                                                     String guid,
                                                                     Logger logger) throws IOException
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions,guid, scenarios);

        final RetrySearchForGetdetailAndCostandAvailVerify verifier = new RetrySearchForGetdetailAndCostandAvailVerify();
        final IVerification.VerificationResult result = verifier.verify(verificationInput,  verificationContext);
        if (logger != null)
        {
            logger.info(MESSAGE_GETCOSTANDAVAIL_VERIFICATION_PROMPT + result);
        }

        return result;
    }

    public static String getCostAndAvailVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInput,
                                                                  SpooferTransport spooferTransport,
                                                                  TestScenario scenarios,
                                                                  String guid,
                                                                  Logger logger) throws IOException
    {
        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getCostAndAvailVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final VerifyGetCostAndAvailabilityBasic verifier = new VerifyGetCostAndAvailabilityBasic();
        final IVerification.VerificationResult result = verifier.verify(getCostAndAvailVerificationInput, getCostAndAvailVerificationContext);

        if (logger != null)
        {
            logger.info(MESSAGE_GETCOSTANDAVAIL_VERIFICATION_PROMPT + result);
        }

        return null;
    }

}
