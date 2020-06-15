package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;

import java.util.Optional;


/**
 * Created by jiyu on 9/16/16.
 */
public abstract class PhoneListCaseInGetDetails implements IGetDetailsVerification
{
    @SuppressWarnings("CPD-START")
    private static boolean isValidPhoneListPresent(GetDetailsVerificationInput input) {
        return Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetDetailsRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(car -> car == null ? null : car.get(0))
                .map(CarProductType::getCarPickupLocation)
                .map(CarLocationType::getPhoneList)
                .isPresent() &&
                Optional.ofNullable(input.getResponse())
                        .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                        .map(CarProductListType::getCarProduct)
                        .map(car -> car == null ? null : car.get(0))
                        .map(CarProductType::getCarPickupLocation)
                        .map(CarLocationType::getPhoneList)
                        .isPresent();
    }

    @SuppressWarnings("CPD-END")
    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return isValidPhoneListPresent(input);
    }
}
