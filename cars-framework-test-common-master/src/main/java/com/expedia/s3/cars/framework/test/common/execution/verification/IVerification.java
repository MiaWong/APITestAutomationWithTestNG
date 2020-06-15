package com.expedia.s3.cars.framework.test.common.execution.verification;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by sswaminathan on 8/4/16.
 */
public interface IVerification<Input, Context>
{
    default Logger getLogger()
    {
        return Logger.getLogger(getClass());
    }

    String getName();
    VerificationResult verify(Input input, Context verificationContext)throws Exception;

    default VerificationResult verify(Input input, Context verificationContext, Map<String, Object> testParameter) throws Exception {
        return verify(input, verificationContext);
    }
    default boolean shouldVerify(Input input, Context verificationContext)
    {
        return true;
    }

    class VerificationResult
    {
        private final String verificationName;
        private final boolean passed;
        private final boolean skipped;
        private final List<String> remarks;

        public VerificationResult(String verificationName, boolean passed, List<String> remarks)
        {
            this.verificationName = verificationName;
            this.passed = passed;
            this.remarks = remarks;
            skipped = false;
        }

        public VerificationResult(String verificationName, boolean passed, boolean skipped, List<String> remarks)
        {
            this.verificationName = verificationName;
            this.passed = passed;
            this.skipped = skipped;
            this.remarks = remarks;
        }

        /**
         * Indicates whether the overall test failed
         * @return
         */
        public boolean isPassed()
        {
            return passed;
        }

        /**
         * Indicates if the test was skipped (the overall test may be indicated as passed or failed depending on the verification)
         * @return
         */
        public boolean isSkipped()
        {
            return skipped;
        }

        public List<String> getRemarks()
        {
            return remarks;
        }

        public String getVerificationName()
        {
            return verificationName;
        }

        @Override
        public String toString()
        {
            return "VerificationResult{" +
                    "verificationName='" + verificationName + '\'' +
                    ", passed=" + passed +
                    ", remarks=" + remarks +
                    '}';
        }
    }
}
