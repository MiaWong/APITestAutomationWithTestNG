package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities;

import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.verification.CancelResponseBasicVerification;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.junit.Assert;

import java.io.IOException;

/**
 * Created by miawang on 2/12/2017.
 */
public class CancelVerificationHelper {
    //-------------------------------------------- Cancel ------------------
    public static String cancelBasicVerification(CancelVerificationInput cancelVerificationInput,
                                                 TestScenario scenarios,
                                                 String guid,
                                                 Logger logger) throws Exception {
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(null, guid, scenarios);

        final IVerification.VerificationResult result = new CancelResponseBasicVerification().verify(cancelVerificationInput, reserveVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                if(logger.isEnabledFor(Priority.DEBUG))
                {
                    logger.debug(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_CANCEL_VERIFICATION_PROMPT + result);
                }
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}
