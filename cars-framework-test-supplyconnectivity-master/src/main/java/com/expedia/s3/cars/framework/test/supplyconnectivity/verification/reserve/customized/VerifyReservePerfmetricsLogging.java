package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized;

import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.BasicKeys;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues.SCSPerfMetricsExpValues;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by v-mechen on 7/11/2017.
 */
public class VerifyReservePerfmetricsLogging implements IReserveVerification {
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public boolean shouldVerify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;

    }

    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext
           )
    {

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    public void verify(ReserveVerificationInput input, BasicVerificationContext verificationContext
            , String gdsMessageName, List<Map> splunkResult)
    {
        VerificationResult result = new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
        //get expected perfMetrics logging value
        final Map<String, String> expValues = SCSPerfMetricsExpValues.getExpTP95SCSReserveValues(input.getRequest(), input.getResponse(),
                verificationContext.getSpooferTransactions(), gdsMessageName);

        //get actual values
        Map<String, String> actValues = null;
        for(final Map splunk : splunkResult)
        {
            if(splunk.containsKey(BasicKeys.ACTIONTYPE) && splunk.get(BasicKeys.ACTIONTYPE).equals(CommonConstantManager.ActionType.RESERVE))
            {
                actValues = splunk;
            }
        }

        //Compare result
        if(actValues == null || actValues.isEmpty())
        {
            result = new VerificationResult(getName(), false, Arrays.asList("PerfMetrics logging for Reserve is not found in actual values!"));
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
