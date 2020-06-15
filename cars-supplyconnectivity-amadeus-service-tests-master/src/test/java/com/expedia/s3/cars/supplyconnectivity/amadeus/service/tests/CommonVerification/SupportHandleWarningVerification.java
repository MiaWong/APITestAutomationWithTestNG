package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.errortypes.defn.v4.UnclassifiedErrorListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.ErrorCollectionType;

import java.util.Arrays;

/**
 * Created by MiaWang on 12/26/2017.
 */
public class SupportHandleWarningVerification
{
    public IVerification.VerificationResult isSupportHandleWarningVerification(UnclassifiedErrorListType unclassifiedErrorList,
                                                                               Object errorCollection,
                                                                               CarProductListType carProductList, boolean isGetDetailsAction)
    {
        boolean passed = true;
        if (isGetDetailsAction && !(null != unclassifiedErrorList && null != unclassifiedErrorList.getUnclassifiedError() &&
                unclassifiedErrorList.getUnclassifiedError().size() > 0 && null != carProductList &&
                null != carProductList.getCarProduct() && carProductList.getCarProduct().size() > 0))
        {
            passed = false;
        }

        if (!isGetDetailsAction && !(null != errorCollection && null != carProductList &&
                null != carProductList.getCarProduct() && carProductList.getCarProduct().size() > 0))
        {
            passed = false;
        }

        if (passed)
        {
            return new IVerification.VerificationResult("", passed, null);
        } else
        {
            return new IVerification.VerificationResult("SupportHandleWarning Verification", passed,
                    Arrays.asList("The warning and normal response not return at the same time!"));
        }
    }
}