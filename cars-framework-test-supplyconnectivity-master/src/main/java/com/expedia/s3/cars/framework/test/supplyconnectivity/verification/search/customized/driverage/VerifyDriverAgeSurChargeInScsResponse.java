package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.driverage;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.DriverAgeCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jiyu on 8/24/16.
 */
public class VerifyDriverAgeSurChargeInScsResponse extends DriverAgeCase
{
    public static final String YOUNG_DRIVER = "YoungDriver";
    public static final int SPOOFED_YOUND_DRIVER_FEE = 2500;

    @Override
    public boolean shouldVerify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext)
    {
        final Long driverAgeYearCount = searchVerificationInput.getRequest().getCarSearchStrategy().getDriverAgeYearCount();

        return ((driverAgeYearCount != null) && ((driverAgeYearCount >= 18 && driverAgeYearCount < 30) || (driverAgeYearCount > 70)));
    }

    @Override
    public IVerification.VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        final List<CarProductType> carsToVerify = getCarsToVerify(input);

        if (carsToVerify.isEmpty())
        {
            //is the spoofing not setup right?
            return new IVerification.VerificationResult(getName(), false, true,
                    Arrays.asList("Verification Failed: Didn't get a car with young driver surcharge. Is spoofing done right?"));
        }

        // verify all carproducts have correct young driver surcharge fees.
        final List<CarProductType> carProductList = carsToVerify.stream()
                .filter(car -> car.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().stream()
                        .anyMatch(fee -> fee.getFinanceSubCategoryCode().equals(YOUNG_DRIVER)
                        && fee.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == SPOOFED_YOUND_DRIVER_FEE))
                .collect(Collectors.toList());

        final String message = "Young driver surcharge carproducts matching count, Expected count:" + carsToVerify.size()
                + ", Actual carproduct count: " + carProductList.size();
        return new IVerification.VerificationResult(getName(), (carProductList.size() == carsToVerify.size()), Arrays.asList(message));
    }

    private List<CarProductType> getCarsToVerify(SearchVerificationInput input)
    {
        final List<CarProductType> carsToVerify = new ArrayList<>();

        for (final CarSearchResultType result : input.getResponse().getCarSearchResultList().getCarSearchResult())
        {
            if (null != result.getCarProductList().getCarProduct())
            {
                final List<CarProductType> carsWithYoungDrivers = result.getCarProductList().getCarProduct().stream()
                        .filter(car -> car.getCarRateDetail() != null
                                && car.getCarRateDetail().getCarAdditionalFeesList() != null
                                && car.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() != null
                                && car.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().stream()
                        .anyMatch(fee -> fee.getFinanceSubCategoryCode().equals(YOUNG_DRIVER)))
                        .collect(Collectors.toList());

                carsToVerify.addAll(carsWithYoungDrivers);
            }
        }
        return carsToVerify;
    }

}
