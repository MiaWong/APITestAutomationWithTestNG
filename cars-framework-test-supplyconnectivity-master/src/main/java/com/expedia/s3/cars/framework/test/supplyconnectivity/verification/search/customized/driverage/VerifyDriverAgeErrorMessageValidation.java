package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage;

import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.DriverAgeCase;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionType;

import java.util.Arrays;

/**
 * Created by jiyu on 8/24/16.
 */
public class VerifyDriverAgeErrorMessageValidation extends DriverAgeCase
{

    public static final String ERROR_XPATH_DRIVER_AGE
            = "search:CarSupplyConnectivitySearchRequest/car:CarSearchStrategy/car:DriverAgeYearCount";

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return !isValidDriverAgePresent(input);
    }

    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        boolean isPassed = false;

        for (final ErrorCollectionType errorCollectionType : input.getResponse().getErrorCollectionList().getErrorCollection())
        {
            for (final FieldInvalidErrorType fieldInvalidErrorType : errorCollectionType.getFieldInvalidErrorList().getFieldInvalidError())
            {
                if (fieldInvalidErrorType.getFieldKey().getXPath().equalsIgnoreCase(ERROR_XPATH_DRIVER_AGE))
                {
                    isPassed = true;
                    break;
                }
            }
        }
        return new VerificationResult(getName(), isPassed, Arrays.asList("Expected FieldInvalidError with xpath '"
                + ERROR_XPATH_DRIVER_AGE + "' is not present in Error Collection."));
    }

}