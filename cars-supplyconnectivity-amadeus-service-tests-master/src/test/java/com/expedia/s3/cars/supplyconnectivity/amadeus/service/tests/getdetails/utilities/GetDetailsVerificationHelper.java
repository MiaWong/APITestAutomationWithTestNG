package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities;

import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification.GetDetailRequestGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification.GetDetailsCarRateQualifierPassVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification.GetDetailsResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification.GetDetailsResponseGDSMsgMappingVerification;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by miawang on 2/12/2017.
 */
public class GetDetailsVerificationHelper
{
    //-------------------------------------------- getDetails ------------------
    public static String getDetailsBasicVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                     TestScenario scenarios,
                                                     String guid,
                                                     Logger logger) throws Exception
    {
        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, guid, scenarios);

        final IVerification.VerificationResult result = new GetDetailsResponseBasicVerification().verify(getDetailsVerificationInput, verificationContext);

        if (!result.isPassed())
        {
            if (logger != null && logger.isEnabledFor(Priority.INFO))
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //getDetails request GDSMsgMap
    public static String getDetailsRequestGDSMsgMappingVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                                    SpooferTransport spooferTransport,
                                                                    TestScenario scenarios,
                                                                    String guid,
                                                                    Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        GetDetailRequestGDSMsgMappingVerification requestGDSMsgMappingVerification = new GetDetailRequestGDSMsgMappingVerification();
        final IVerification.VerificationResult result = requestGDSMsgMappingVerification.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //getDetails response GDSMsgMap
    public static String getDetailsResponseGDSMsgMappingVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                                     SpooferTransport spooferTransport,
                                                                     TestScenario scenarios,
                                                                     String guid,
                                                                     Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));

        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        GetDetailsResponseGDSMsgMappingVerification gdsMsgVerifier = new GetDetailsResponseGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String getDetailsCarRateQualifierPassingVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                                       SpooferTransport spooferTransport, TestData parameters, String invalidCarRateQualifierCode,
                                                                       Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(parameters.getGuid());

        logger.info("\nspooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, parameters.getGuid(), parameters.getScenarios());

        GetDetailsCarRateQualifierPassVerification carRateQualifierPassVerifier = new GetDetailsCarRateQualifierPassVerification();
        final IVerification.VerificationResult result = carRateQualifierPassVerifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null && logger.isEnabledFor(Priority.INFO))
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}