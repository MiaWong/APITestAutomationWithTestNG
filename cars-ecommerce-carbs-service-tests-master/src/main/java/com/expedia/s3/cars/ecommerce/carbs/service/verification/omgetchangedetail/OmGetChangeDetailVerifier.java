package com.expedia.s3.cars.ecommerce.carbs.service.verification.omgetchangedetail;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailRequestType;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailResponseType;
import org.testng.Assert;

import java.io.IOException;

/**
 * Created by fehu on 11/10/2016.
 */
public class OmGetChangeDetailVerifier {

    private OmGetChangeDetailVerifier() {
    }

    public static void isOMGetChangeDetailsWorksVerifier(String guid, TestScenario scenarios, GetChangeDetailRequestType requestType, GetChangeDetailResponseType responseType) throws IOException {
        final BasicVerificationContext context = new BasicVerificationContext(null, guid, scenarios);
        final GetChangeDetailsVerificationInput getChangeDetailsVerificationInput = new GetChangeDetailsVerificationInput(requestType, responseType);
        /*final ChainedVerification<GetChangeDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isOMGetChangeDetailsWorksVerifier", Arrays.asList(new OmGetChangeDetailBasicVerification()));
*/
        final OmGetChangeDetailBasicVerification verifications = new OmGetChangeDetailBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(getChangeDetailsVerificationInput, context);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }
    }
}
