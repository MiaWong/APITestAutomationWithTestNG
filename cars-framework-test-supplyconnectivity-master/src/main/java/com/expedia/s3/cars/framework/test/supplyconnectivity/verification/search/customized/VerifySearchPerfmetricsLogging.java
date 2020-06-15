package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized;

import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.BasicKeys;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues.SCSPerfMetricsExpValues;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by v-mechen on 7/6/2017.
 */
public class VerifySearchPerfmetricsLogging implements ISearchVerification {
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;

    }

    @Override
    public IVerification.VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext
           )
    {

        return new IVerification.VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    public void verify(SearchVerificationInput input, BasicVerificationContext verificationContext
            , String gdsMessageName, List<Map> splunkResult)
    {
        IVerification.VerificationResult result = new IVerification.VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
        //get expected perfMetrics logging value
        final Map<String, String> expValues = SCSPerfMetricsExpValues.getExpTP95SCSSearchValues(input.getRequest(), input.getResponse(),
                verificationContext.getSpooferTransactions(), gdsMessageName);

        //get actual values
        Map<String, String> actValues = null;
        for(final Map splunk : splunkResult)
        {
            if(splunk.containsKey(BasicKeys.ACTIONTYPE) && splunk.get(BasicKeys.ACTIONTYPE).equals(CommonConstantManager.ActionType.SEARCH))
            {
                actValues = splunk;
            }
        }

        //Compare result
        if(actValues == null || actValues.isEmpty())
        {
            result = new IVerification.VerificationResult(getName(), false, Arrays.asList("PerfMetrics logging for Search is not found in actual values!"));
        }
        else
        {
            final String compareResult = CompareUtil.compareSplunkMap(expValues, actValues);
            if(!compareResult.trim().isEmpty())
            {
                result = new IVerification.VerificationResult(getName(), false, Arrays.asList(compareResult));
            }

        }


        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }


    }

}
