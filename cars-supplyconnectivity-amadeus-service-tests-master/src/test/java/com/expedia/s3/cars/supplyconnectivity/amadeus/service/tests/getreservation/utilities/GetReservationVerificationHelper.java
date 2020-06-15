package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities;

import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.verification.GetReservationRequestGDSMsgMappingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.verification.GetReservationResponseBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.verification.GetReservationResponseGDSMsgMappingVerification;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by miawang on 2/12/2017.
 */
public class GetReservationVerificationHelper {
    //----------GetReservation-------------------
    public static String getReservationBasicVerification(GetReservationVerificationInput getreservationVerificationInput,
                                              TestScenario scenarios,
                                              String guid,
                                              Logger logger) throws Exception {
        final BasicVerificationContext getreservationVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final GetReservationResponseBasicVerification verifier = new GetReservationResponseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getreservationVerificationInput, getreservationVerificationContext);

        if (logger != null) {
            logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    //get reservation response GDSMsgMap
    public static String getReservationResponseGDSMsgMappingVerification(GetReservationVerificationInput getReservationVerificationInput,
                                                                         SpooferTransport spooferTransport,
                                                                         TestScenario scenarios,
                                                                         String guid,
                                                                         Logger logger) throws IOException {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        GetReservationResponseGDSMsgMappingVerification gdsMsgVerifier = new GetReservationResponseGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(getReservationVerificationInput, reserveVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //get reservation request GDSMsgMap
    public static String getReservationRequestGDSMsgMappingVerification(GetReservationVerificationInput getReservationVerificationInput,
                                                                         SpooferTransport spooferTransport,
                                                                         TestScenario scenarios,
                                                                         String guid,
                                                                         Logger logger) throws IOException {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        logger.info("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        GetReservationRequestGDSMsgMappingVerification gdsMsgVerifier = new GetReservationRequestGDSMsgMappingVerification();
        final IVerification.VerificationResult result = gdsMsgVerifier.verify(getReservationVerificationInput, reserveVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}
