package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by miawang on 12/6/2016.
 */
public class SearchResponsesNotEmptyVerification implements ISearchVerification {
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);

        final ChainedVerification<SearchVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifySearchResponseNotEmpty()
                ));

        return verifications.verify(input, verificationContext);
    }

}
