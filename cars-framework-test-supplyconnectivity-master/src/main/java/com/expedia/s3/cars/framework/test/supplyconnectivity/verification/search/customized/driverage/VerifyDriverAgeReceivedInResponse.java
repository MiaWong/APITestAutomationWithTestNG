package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.DriverAgeCase;

import java.util.Arrays;

/**
 * Created by jiyu on 8/24/16.
 */
public class VerifyDriverAgeReceivedInResponse extends DriverAgeCase
{
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        final StringBuilder errorMessage = new StringBuilder();
        // if driver age is not supplied in request, response car product should not have driver age.
        final Long expectedDriverAge = input.getRequest().getCarSearchStrategy().getDriverAgeYearCount();

        for (final CarSearchResultType result : input.getResponse().getCarSearchResultList().getCarSearchResult())
        {
            if (null == result.getCarProductList().getCarProduct()) {
                continue;
            }

            final StringBuilder criteriaLevelMessage = new StringBuilder("Result Sequence ID: ")
                    .append(result.getSequence()).append("; Total Cars: ")
                    .append(result.getCarProductList().getCarProduct().size());
            int failedCarsCount = 0;
            for (final CarProductType car : result.getCarProductList().getCarProduct()) {
                if (car.getCarInventoryKey().getDriverAgeYearCount() != expectedDriverAge) {
                    failedCarsCount++;
                }
            }
            if (failedCarsCount > 0) {
                criteriaLevelMessage.append("; Cars where expected driver age of ")
                        .append(expectedDriverAge).append(" didn't match: ").append(failedCarsCount)
                        .append("\r\n");

                errorMessage.append(criteriaLevelMessage);
            }
        }
        return new VerificationResult(getName(), errorMessage.length() == 0, Arrays.asList(errorMessage.toString()));
    }
}

