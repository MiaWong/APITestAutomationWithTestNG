package com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import expedia.om.supply.messages.defn.v1.RetrieveRequest;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fehu on 11/10/2016.
 */
public class OmRetrieveVerifier {

    private OmRetrieveVerifier() {
    }

    public static void isOMRetrieveWorksVerifier(String guid, TestScenario scenarios, RetrieveRequest requestType, RetrieveResponseType responseType, boolean retrieveAfterCancel) throws IOException {
        final BasicVerificationContext context = new BasicVerificationContext(null, guid, scenarios);
        final RetrieveVerificationInput retrieveVerificationInput = new RetrieveVerificationInput(requestType, responseType);
        /*final ChainedVerification<RetrieveVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isRetrieveWorksVerifier", Arrays.asList(new OmRetrieveBasicVerification()));
*/

        final OmRetrieveBasicVerification verifications = new OmRetrieveBasicVerification();
        final Map<String, Object> testParam=new HashMap<>();
        testParam.put("retrieveAfterCancel",retrieveAfterCancel);
        final IVerification.VerificationResult result = verifications.verify(retrieveVerificationInput, context, testParam);
        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }


    }
}
