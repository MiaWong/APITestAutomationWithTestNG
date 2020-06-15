package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.customized;

import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.BasicKeys;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues.SCSPerfMetricsExpValues;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by v-mechen on 7/11/2017.
 */
public class VerifyCancelPerfmetricsLogging implements ICancelVerification {
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public boolean shouldVerify(CancelVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;

    }

    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext
           )
    {

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    public void verify(CancelVerificationInput input, BasicVerificationContext verificationContext
            , String gdsMessageName, List<Map> splunkResult)
    {
        VerificationResult result = new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
        //get expected perfMetrics logging value
        final Map<String, String> expValues = SCSPerfMetricsExpValues.getExpTP95SCSCancelValues(input.getRequest(), input.getResponse(),
                verificationContext.getSpooferTransactions(), gdsMessageName);

        //get actual values
        Map<String, String> actValues = null;
        for(final Map splunk : splunkResult)
        {
            if(splunk.containsKey(BasicKeys.ACTIONTYPE) && splunk.get(BasicKeys.ACTIONTYPE).equals(CommonConstantManager.ActionType.CANCEL))
            {
                actValues = splunk;
            }
        }

        //Compare result
        if(actValues == null || actValues.isEmpty())
        {
            result = new VerificationResult(getName(), false, Arrays.asList("PerfMetrics logging for Cancel is not found in actual values!"));
        }
        else
        {
            final String compareResult = CompareUtil.compareSplunkMap(expValues, actValues);
            if(!compareResult.trim().isEmpty())
            {
                result = new VerificationResult(getName(), false, Arrays.asList(compareResult));
            }

        }


        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }


    }

}
