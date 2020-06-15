package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.basic.VerifySearchResponseNotEmpty;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by aaniyath on 04-10-2016.
 */
public class SearchResponseExtraVerification implements ISearchVerification
{
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws Exception
    {
        Assert.assertNotNull(input);

        final ChainedVerification<SearchVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifySearchResponseNotEmpty()
                        , new VerifyCarProductCountWithDownstreamResponse()
                ));

        return verifications.verify(input, verificationContext);
    }

    public VerificationResult verifyDiscount(SearchVerificationInput input, BasicVerificationContext verificationContext) throws Exception
    {
        Assert.assertNotNull(input);

        final ChainedVerification<SearchVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifySearchResponseNotEmpty()
                        , new VerifyCarProductCountWithDownstreamResponse()
                        , new VerifyDiscountCodeInSearchResponse()
                ));

        return verifications.verify(input, verificationContext);
    }

}
