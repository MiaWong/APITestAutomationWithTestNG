package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;


/**
 * Created by jiyu on 8/24/16.
 */
public abstract class DriverAgeCaseInGetDetails implements IGetDetailsVerification
{
    public static final String DRIVER_AGE_XPATH = "//SpoofedTransactions/Transaction/Request/*[local-name()='OTA_VehAvailRateRQ']/*[local-name()='VehAvailRQCore']/*[local-name()='DriverType']/@Age";

    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return isValidDriverAgePresent(input);
    }

    public static boolean isValidDriverAgePresent(GetDetailsVerificationInput input)
    {

        if (!java.util.Optional.ofNullable(input.getRequest())
                    .map(CarSupplyConnectivityGetDetailsRequestType::getCarProductList)
                    .map(CarProductListType::getCarProduct)
                    .map(car -> car == null ? null : car.get(0))
                    .map(CarProductType::getCarInventoryKey)
                    .map(CarInventoryKeyType::getDriverAgeYearCount)
                    .isPresent()) {
            return false;
        }

        return (input.getRequest().getCarProductList().getCarProduct().get(0).getCarInventoryKey().getDriverAgeYearCount() >= 18);
    }
}
