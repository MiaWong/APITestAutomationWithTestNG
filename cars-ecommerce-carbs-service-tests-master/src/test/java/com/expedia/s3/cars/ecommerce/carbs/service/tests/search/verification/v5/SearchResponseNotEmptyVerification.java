package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5;

import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5.basic.VerifySearchResponseNotEmpty;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;

import java.util.Arrays;

import static org.testng.Assert.assertNotNull;

public class SearchResponseNotEmptyVerification implements ISearchVerification
{
    @Override
    public VerificationResult verify(V5SearchVerificationInput input, BasicVerificationContext verificationContext)
        throws Exception
    {
        assertNotNull(input);

        final ChainedVerification<V5SearchVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(), Arrays.asList(new VerifySearchResponseNotEmpty()));

        return verifications.verify(input, verificationContext);
    }
}
