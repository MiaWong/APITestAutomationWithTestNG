package com.expedia.s3.cars.framework.test.common.execution.verification;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sswaminathan on 8/4/16.
 */
public class ChainedVerification<Input, Context> implements IVerification<Input, Context>
{
    private final List<IVerification<Input, Context>> verifications;
    private final String name;

    public ChainedVerification(String verificationName, List<IVerification<Input, Context>> verifications)
    {
        this.name = verificationName;
        Assert.notEmpty(verifications);
        this.verifications = verifications;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public VerificationResult verify(Input input, Context verificationContext) throws Exception {
        boolean isPassed = true;
        final List<VerificationResult> results = new ArrayList<>(verifications.size());
        final List<String> remarks = new ArrayList<>();

        for (final IVerification<Input, Context> verification : verifications)
        {
            final VerificationResult result = verify(input, verificationContext, verification);
            isPassed = isPassed && result.isPassed();
            results.add(result);

            if (!result.isPassed() || result.isSkipped())
            {
                remarks.add(result.toString());
            }

        }

        return new ChainedVerificationResult(name, isPassed, remarks, results);
    }

    private VerificationResult verify(Input input, Context verificationContext, IVerification<Input, Context> verification) throws Exception {

            if (verification.shouldVerify(input, verificationContext))
            {
                return verification.verify(input, verificationContext);
            } else
            {
                return new VerificationResult(verification.getName(), true, true, Arrays.asList("Skipped test as shouldVerify returned false."));
            }


    }

    public static class ChainedVerificationResult extends VerificationResult
    {
        private final List<VerificationResult> results;

        public ChainedVerificationResult(String verificationName,
                                         boolean passed, List<String> remarks,
                                         List<VerificationResult> results)
        {
            super(verificationName, passed, remarks);
            this.results = results;
        }
    }
}
