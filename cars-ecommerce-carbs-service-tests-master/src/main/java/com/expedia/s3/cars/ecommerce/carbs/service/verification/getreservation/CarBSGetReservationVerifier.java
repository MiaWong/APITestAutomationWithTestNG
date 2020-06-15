package com.expedia.s3.cars.ecommerce.carbs.service.verification.getreservation;

import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationRequestType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationResponseType;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.testng.Assert;

import java.io.IOException;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarBSGetReservationVerifier {

    private CarBSGetReservationVerifier() {
    }

    public static void isCarbsGetReservationWorksVerifier(String guid, TestScenario scenarios, CarECommerceGetReservationRequestType requestType, CarECommerceGetReservationResponseType responseType) throws IOException {

        final BasicVerificationContext getReservationVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final GetReservationVerificationInput getReservationVerificationInput = new GetReservationVerificationInput(requestType, responseType);
      /*  final ChainedVerification<GetReservationVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCarbsGetReservationWorksVerifier", Arrays.asList(new CarbsGetReservationResponseBasicVerification()));
*/
        final CarbsGetReservationResponseBasicVerification verifications = new CarbsGetReservationResponseBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(getReservationVerificationInput, getReservationVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result.toString());
        }

    }
}
