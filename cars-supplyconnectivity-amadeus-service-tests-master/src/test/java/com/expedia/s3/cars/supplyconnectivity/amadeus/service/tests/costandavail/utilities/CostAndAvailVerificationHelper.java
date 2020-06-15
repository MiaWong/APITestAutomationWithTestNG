package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.utilities;

import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.verification.CostAndAvailResponseVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.verification.GetCostAndAvailRequestGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.verification.GetCostAndAvailResponseGDSMsgMappingVerification;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by miawang on 2/12/2017.
 */
public class CostAndAvailVerificationHelper {
    //-------------------------------------------- getDetails ------------------
    public static String getCostAndAvailBasicVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                     TestScenario scenarios,
                                                     String guid,
                                                     Logger logger) throws Exception
    {
        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, guid, scenarios);

        final IVerification.VerificationResult result = new CostAndAvailResponseVerification().verify(getCostAndAvailabilityVerificationInput, verificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.debug(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //getCostAndAvail response GDSMsgMap
    public static String getCostAndAvailResponseGDSMsgMappingVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                                     SpooferTransport spooferTransport,
                                                                     TestScenario scenarios,
                                                                     String guid,
                                                                     Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext getCostAndAvailabilityVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        GetCostAndAvailResponseGDSMsgMappingVerification gdsMsgVerifier = new GetCostAndAvailResponseGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(getCostAndAvailabilityVerificationInput, getCostAndAvailabilityVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //getCostAndAvail request GDSMsgMap
    public static String getCostAndAvailRequestGDSMsgMappingVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                                          SpooferTransport spooferTransport,
                                                                          TestScenario scenarios,
                                                                          String guid,
                                                                          Logger logger) throws IOException
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        final BasicVerificationContext getCostAndAvailabilityVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        GetCostAndAvailRequestGDSMsgMappingVerification gdsMsgVerifier = new GetCostAndAvailRequestGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(getCostAndAvailabilityVerificationInput, getCostAndAvailabilityVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.debug(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}
