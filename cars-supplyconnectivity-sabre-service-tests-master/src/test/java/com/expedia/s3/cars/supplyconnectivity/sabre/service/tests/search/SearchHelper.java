package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.requestgeneration.SabreSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search.verification.SearchResponseExtraVerification;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search.verification
        .SearchResponsesNotEmptyVerification;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by vmohan on 24-11-2016.
 */
public class SearchHelper {

    private SearchHelper() {}

    private final static String MESSAGE_SEARCH_VERIFICATION_PROMPT = "Search Verification run: ";

    //  SCS search
    public static SearchVerificationInput search(HttpClient httpClient,
                                                 TestScenario scenarios,
                                                 String tuid,
                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivitySearchRequestType searchRequest = SabreSearchRequestGenerator.createSearchRequest(scenarios, tuid);
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
    }


    //  SCS search with discount code
    public static SearchVerificationInput searchWithDiscountCode(HttpClient httpClient,
                                                                 TestScenario scenarios,
                                                                 String tuid,
                                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivitySearchRequestType searchRequest = SabreSearchRequestGenerator
                .createSearchRequest(scenarios, tuid);
        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(1).getCarRate().setCorporateDiscountCode("CDCode");
        searchRequest.getCarSearchCriteriaList().getCarSearchCriteria().get(1).getCarRate().setPromoCode("PromoCode");
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
    }


    //  search basic verification : return new GUID for next if required, otherwise return null
    public static String searchVerification(SearchVerificationInput searchVerificationInput,
                                            SpooferTransport spooferTransport,
                                            TestScenario scenarios,
                                            String guid,
                                            Logger logger,
                                            boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new SearchResponsesNotEmptyVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String searchRegressionVerification(SearchVerificationInput searchVerificationInput,
                                                      SpooferTransport spooferTransport,
                                                      TestScenario scenarios,
                                                      String guid,
                                                      Logger logger,
                                                      boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final IVerification.VerificationResult result = new SearchResponseExtraVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String discountSearchVerification(SearchVerificationInput searchVerificationInput,
                                                      SpooferTransport spooferTransport,
                                                      TestScenario scenarios,
                                                      String guid,
                                                      Logger logger,
                                                      boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final IVerification.VerificationResult result = new SearchResponseExtraVerification().verifyDiscount(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}
