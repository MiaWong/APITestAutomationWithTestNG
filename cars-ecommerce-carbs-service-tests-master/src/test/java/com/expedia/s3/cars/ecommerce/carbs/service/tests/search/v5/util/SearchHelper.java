package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.v5.util;

import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.TransportHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.v5.SearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5.SearchResponseNotEmptyVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5.VerifyPricingInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification.VerificationResult;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

public class SearchHelper
{
    private final static String MESSAGE_SEARCH_VERIFICATION_PROMPT = "Search Verification run: ";

    private SearchHelper()
    {
        //NOP
    }

    //------- Search ------------------
    //  serach basic verification : return new GUID for next if required, otherwise return null
    public static void searchVerification(V5SearchVerificationInput searchVerificationInput,
                                            SpooferTransport spooferTransport,
                                            TestScenario scenarios,
                                            String guid,
                                            Logger logger,
                                            boolean isRequiresRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isRequiresRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext =
                new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final VerificationResult result =
                new SearchResponseNotEmptyVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed())
        {
            if (null != logger)
            {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

    }

    public static void priceListVerification(V5SearchVerificationInput searchVerificationInput,
                                             SpooferTransport spooferTransport,
                                             TestData testData,
                                             String guid,
                                             Logger logger,
                                             boolean isRequiresRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isRequiresRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext =
                new BasicVerificationContext(spooferTransactions, guid, testData.getScenarios());

        final VerificationResult result =
                new VerifyPricingInSearchResponse().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed())
        {
            if (null != logger)
            {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }
    }

    /**
     * Basic search.
     *
     * @param httpClient
     * @param scenarios
     * @param userGUID
     * @return
     */
    public static V5SearchVerificationInput search(HttpClient httpClient,
                                                   TestScenario scenarios,
                                                   String userGUID)
    {
        final TestData testData = new TestData(httpClient, scenarios, null, PojoXmlUtil.getRandomGuid());
        final CarSearchRequest request = SearchRequestGenerator.createSearchRequest(testData, userGUID);
        return TransportHelper.sendRecieve(
                httpClient, SettingsProvider.SERVICE_V5_SEARCH_ADDRESS, request, PojoXmlUtil.getRandomGuid());
    }

    public static V5SearchVerificationInput search(HttpClient httpClient, TestData testData, String userGUID)
    {
        final CarSearchRequest request = SearchRequestGenerator.createSearchRequest(testData, userGUID);
        return TransportHelper.sendRecieve(
                httpClient, SettingsProvider.SERVICE_V5_SEARCH_ADDRESS, request, PojoXmlUtil.getRandomGuid());
    }
}
