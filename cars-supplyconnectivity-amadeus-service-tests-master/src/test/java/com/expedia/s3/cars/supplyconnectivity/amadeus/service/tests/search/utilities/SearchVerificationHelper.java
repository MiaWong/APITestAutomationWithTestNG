package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities;

import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification
        .SearchRequestGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification
        .SearchRequestOptimizationVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification
        .SearchResponseGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification
        .SearchResponsesNotEmptyVerification;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by miawang on 2/12/2017.
 */
public class SearchVerificationHelper {
    //-------------------------------------------- Search ------------------
    //  serach basic verification : return new GUID for next if required, otherwise return null
    public static String searchNotEmptyVerification(SearchVerificationInput searchVerificationInput,
                                                    TestScenario scenarios,
                                                    String guid,
                                                    Logger logger) throws Exception {
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(null, guid, scenarios);

        final IVerification.VerificationResult result = new SearchResponsesNotEmptyVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //Search response GDSMsgMap
    public static String searchResponseGDSMsgMappingVerification(SearchVerificationInput searchVerificationInput,
                                                                 SpooferTransport spooferTransport,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        SearchResponseGDSMsgMappingVerification gdsMsgRspVerifier = new SearchResponseGDSMsgMappingVerification();
        final IVerification.VerificationResult rspResult = gdsMsgRspVerifier.verify(searchVerificationInput, searchVerificationContext);

        if (!rspResult.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + rspResult);
            }
            Assert.fail(rspResult.toString());
        }

        return null;
    }

    //Search Request GDSMsgMap
    public static String searchRequestGDSMsgMappingVerification(SearchVerificationInput searchVerificationInput,
                                                                 SpooferTransport spooferTransport,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        SearchRequestGDSMsgMappingVerification gdsMsgReqVerifier = new SearchRequestGDSMsgMappingVerification();
        final IVerification.VerificationResult reqResult = gdsMsgReqVerifier.verify(searchVerificationInput, searchVerificationContext);

        if (!reqResult.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + reqResult);
            }
            Assert.fail(reqResult.toString());
        }

        return null;
    }

    //Search Request Optimization
    public static String searchRequestOptimizationVerification(SearchVerificationInput searchVerificationInput,
                                                                SpooferTransport spooferTransport,
                                                                TestScenario scenarios,
                                                                String guid,
                                                                Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        logger.info("\nspooferxml" + PojoXmlUtil.toString(spooferTransactions));

        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        SearchRequestOptimizationVerification gdsMsgReqVerifier = new SearchRequestOptimizationVerification();
        final IVerification.VerificationResult reqResult = gdsMsgReqVerifier.verify(searchVerificationInput, searchVerificationContext);

        if (!reqResult.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + reqResult);
            }
            Assert.fail(reqResult.toString());
        }

        return null;
    }
}