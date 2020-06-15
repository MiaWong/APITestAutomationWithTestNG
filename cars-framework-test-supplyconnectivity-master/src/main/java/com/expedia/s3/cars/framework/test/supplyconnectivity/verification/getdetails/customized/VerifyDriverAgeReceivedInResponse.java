package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by jiyu on 8/24/16.
 */
public class VerifyDriverAgeReceivedInResponse extends DriverAgeCaseInGetDetails
{
    private final static String TAG_VALID_EXPECTED_DRIVERAGE_PROMPT = "Expected Driver age = ";
    private final static String TAG_VALID_ACTUAL_DRIVERAGE_PROMPT = "; actual driver age in GetDetails response = ";
    private final static String TAG_INVALID_EXPECTED_DRIVERAGE_PROMPT = "Invalid expected driver age (";
    private final static String TAG_INVALID_ACTUAL_DRIVERAGE_PROMPT = "Invalid actual driver age (";
    private final static String TAG_IN_GETDETAIL_REQUEST_PROMPT = ") in TiSCS GetDetails request.";
    private final static String TAG_IN_GETDETAIL_RESPONSE_PROMPT = ") in TiSCS GetDetails request.";

    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(cars -> cars == null ? null : cars.get(0))
                .map(CarProductType::getCarInventoryKey)
             // .map(CarInventoryKeyType::getDriverAgeYearCount)    //  not needed in precheck
                .isPresent();
    }

    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        // get expected driver age from request
        final Long expectedDriverAge = Optional.of(input.getRequest())
                .map(CarSupplyConnectivityGetDetailsRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(cars -> cars == null ? null : cars.get(0))
                .map(CarProductType::getCarInventoryKey)
                .map(CarInventoryKeyType::getDriverAgeYearCount)
                .orElseGet(() -> 0L);

        if (expectedDriverAge == 0L)
        {
            return new VerificationResult(getName(), false,
                    Arrays.asList(TAG_INVALID_EXPECTED_DRIVERAGE_PROMPT + expectedDriverAge + TAG_IN_GETDETAIL_REQUEST_PROMPT));
        }

        // get actual driver age from response
        final Long actualDriverAge = Optional.of(input.getResponse())
                .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(cars -> cars == null ? null : cars.get(0))
                .map(CarProductType::getCarInventoryKey)
                .map(CarInventoryKeyType::getDriverAgeYearCount)
                .orElseGet(() -> 0L);

        if (actualDriverAge == 0L)
        {
            return new VerificationResult(getName(), false,
                    Arrays.asList(TAG_INVALID_ACTUAL_DRIVERAGE_PROMPT + actualDriverAge + TAG_IN_GETDETAIL_RESPONSE_PROMPT));
        }

        return new VerificationResult(getName(), actualDriverAge == expectedDriverAge,
                Arrays.asList(TAG_VALID_EXPECTED_DRIVERAGE_PROMPT + expectedDriverAge + TAG_VALID_ACTUAL_DRIVERAGE_PROMPT + actualDriverAge));
    }
}