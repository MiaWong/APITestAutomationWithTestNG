package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyPhoneListReceivedInResponse;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by vmohan on 03-11-2016.
 */
public class GetDetailsPhoneListVerification implements IGetDetailsVerification
{
    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception
    {
        Assert.assertTrue(Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetDetailsRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent());

        Assert.assertTrue(Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent());

        //  invoke a chain of verifications and return the result...
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetDetailsBasic(),
                        new VerifyPhoneListReceivedInResponse()
                ));

        return verifications.verify(input, verificationContext);
    }
}
