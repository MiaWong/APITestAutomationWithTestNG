package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized;

import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.BasicKeys;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues.SCSErrorAnalysisExpValues;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 8/23/2017.
 */
public class VerifyGetDetailsErrorAnalysisLogging implements IGetDetailsVerification{
    @Override
    public VerificationResult verify(GetDetailsVerificationInput getDetailsVerificationInput, BasicVerificationContext verificationContext) {
        return null;
    }

    public void verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext
            , List<Map> splunkResult)
    {
        VerificationResult result = new VerificationResult(getName(), true, Arrays.asList("Suceess"));
        //get expected perfMetrics logging value
        final List<Map<String, String>> expValues = SCSErrorAnalysisExpValues.genExpeErrorAnalysisMNSCSDetail(input.getRequest(), input.getResponse(),
                verificationContext.getSpooferTransactions());



        //Compare result
        if(splunkResult == null || splunkResult.isEmpty())
        {
            result = new VerificationResult(getName(), false, Arrays.asList("ErrorAnalysis logging for GetDetails is not found in actual values!"));
        }
        else {
            final List<Map<String, String>> actValues = new ArrayList<>();
            for (final Map<String, String> actValue : splunkResult) {
                if (actValue.get(BasicKeys.ACTIONTYPE).equalsIgnoreCase("getDetails")) {
                    actValues.add(actValue);
                }
            }

            final StringBuffer compareResults = new StringBuffer();
            for (int i = 0; expValues.size() > i; i++) {
                final String compareResult = CompareUtil.compareSplunkMap(expValues.get(i), actValues.get(i));
                compareResults.append(compareResult);
                compareResults.append('\n');
            }

            if (StringUtil.isNotBlank(compareResults.toString())) {
                result = new IVerification.VerificationResult(getName(), false, Arrays.asList(compareResults.toString()));
            }


        }
        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

    }
}
