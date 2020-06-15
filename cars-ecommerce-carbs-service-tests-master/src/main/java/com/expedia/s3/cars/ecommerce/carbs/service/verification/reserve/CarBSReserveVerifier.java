package com.expedia.s3.cars.ecommerce.carbs.service.verification.reserve;

import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveRequestType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.testng.Assert;

import java.io.IOException;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarBSReserveVerifier {

    private CarBSReserveVerifier() {
    }

    public static void isCarbsReserveWorksVerifier(String guid, TestScenario scenarios, CarECommerceReserveRequestType requestType, CarECommerceReserveResponseType responseType) throws IOException {

        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final ReserveVerificationInput reserveVerificationInput = new ReserveVerificationInput(requestType, responseType);
       /* final ChainedVerification<ReserveVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCarbsReserveWorksVerifier", Arrays.asList(new CarbsReserveResponseBasicVefification()));
*/
        final CarbsReserveResponseBasicVefification vefifications = new CarbsReserveResponseBasicVefification();
        final IVerification.VerificationResult result = vefifications.verify(reserveVerificationInput, reserveVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_RESERVE_VERIFICATION_PROMPT + result.toString());
        }

    }
}
