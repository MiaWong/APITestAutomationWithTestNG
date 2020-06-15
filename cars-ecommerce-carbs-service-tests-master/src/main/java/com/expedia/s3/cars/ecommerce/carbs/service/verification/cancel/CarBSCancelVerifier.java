package com.expedia.s3.cars.ecommerce.carbs.service.verification.cancel;

import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.testng.Assert;

import java.io.IOException;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarBSCancelVerifier {

    private CarBSCancelVerifier() {
    }

    public static void isCarbsCancelWorksVerifier(String guid, TestScenario scenarios, CarECommerceCancelRequestType requestType, CarECommerceCancelResponseType responseType) throws IOException {

        final BasicVerificationContext cancelVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final CancelVerificationInput cancelVerificationInput = new CancelVerificationInput(requestType, responseType);
      /*  final ChainedVerification<CancelVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCarbsGetCostAndAvailWorksVerifier", Arrays.asList(new CarbsCancelResponseBasicVerification()));
*/
        final CarbsCancelResponseBasicVerification verifications = new CarbsCancelResponseBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(cancelVerificationInput, cancelVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_CANCEL_VERIFICATION_PROMPT + result.toString());
        }

    }
}
