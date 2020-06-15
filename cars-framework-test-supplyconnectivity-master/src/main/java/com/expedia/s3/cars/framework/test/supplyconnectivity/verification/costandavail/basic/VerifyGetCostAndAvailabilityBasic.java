package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.basic;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;


/**
 * Created by jiyu on 8/24/16.
 */
public class VerifyGetCostAndAvailabilityBasic implements IGetCostAndAvailabilityVerification
{
    private static final String MESSAGR_NO_COSTAVAIL_RESULT_IN_REQUEST = "No valid car product in GetCostAndAvailability request.";
    private static final String MESSAGR_NO_COSTAVAIL_RESULT_IN_RESPONSE = "No valid car product in GetCostAndAvailability Result response.";

    private static final String MESSAGE_SUCCESS = "Success";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";


    private String verifyBasicCarCateloguCompare(CarSupplyConnectivityGetCostAndAvailabilityRequestType request, CarSupplyConnectivityGetCostAndAvailabilityResponseType response)
    {
        final List<CarProductType> carListInRequest = request.getCarProductList().getCarProduct();
        final List<CarProductType> carListInResponse = response.getCarProductList().getCarProduct();
        return VerificationHelper.verifyBasicCarCateloguCompare(
                carListInRequest.get(0).getCarInventoryKey().getCarCatalogKey(),
                carListInResponse.get(0).getCarInventoryKey().getCarCatalogKey());
    }

    private String verifyBasic(CarSupplyConnectivityGetCostAndAvailabilityRequestType request, CarSupplyConnectivityGetCostAndAvailabilityResponseType response)
    {
        if (!Optional.ofNullable(request)
                .map(CarSupplyConnectivityGetCostAndAvailabilityRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent()) {
            return MESSAGR_NO_COSTAVAIL_RESULT_IN_REQUEST;
        }

        if (!Optional.ofNullable(response)
                .map(CarSupplyConnectivityGetCostAndAvailabilityResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent()) {
            return MESSAGR_NO_COSTAVAIL_RESULT_IN_RESPONSE;
        }

        return verifyBasicCarCateloguCompare(request, response);
    }

    private String verifyExtra(CarSupplyConnectivityGetCostAndAvailabilityResponseType response)
    {
        if (null != response.getErrorCollection()) {
            return VerificationHelper.verifyExtra(getXmlFieldValue(response.getErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }

        return null;
    }

    @Override
    public boolean shouldVerify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext)
    {
        return (Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetCostAndAvailabilityRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent() &&
                Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetCostAndAvailabilityResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .isPresent());
    }

    @Override
    public IVerification.VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext)
    {
        String errorMessage = "";

        errorMessage = verifyBasic(input.getRequest(), input.getResponse());
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = verifyExtra(input.getResponse());
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }
}