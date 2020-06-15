package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;



/**
 * Created by jiyu on 8/25/16.
 */
public final class VerificationHelper
{
    private static final String MESSAGE_ERROR_COLLECTION_IN_RESPONSE = "ErrorCollection is present in response.";
    private static final String MESSAGE_NO_MATCHED_CAR = "Car Type is not the same! Please check the request and response";

    private static final String MESSGAGE_NO_CAR_CATALOG_KEY_IN_REQUEST = "No car catalog inventory key in request";
    private static final String MESSGAGE_NO_CAR_CATALOG_KEY_IN_RESPONSE = "No car catalog inventory key in response";


    // add a private constructor or make the class abstract to silence PMD warning.
    private VerificationHelper() {}


    public static String verifyBasicCarCateloguCompare(CarCatalogKeyType expectedCarKeyInRequest, CarCatalogKeyType expectedCarKeyInResponse)
    {
        if (null == expectedCarKeyInRequest)
        {
            return MESSGAGE_NO_CAR_CATALOG_KEY_IN_REQUEST;
        }

        if (null == expectedCarKeyInResponse)
        {
            return MESSGAGE_NO_CAR_CATALOG_KEY_IN_RESPONSE;
        }

        //  you may add more basic validation here
        final boolean isPassed = compare(expectedCarKeyInRequest, expectedCarKeyInResponse);
        if (!isPassed) {
            return MESSAGE_NO_MATCHED_CAR;
        }

        return null;
    }

    private static boolean compare(CarCatalogKeyType expectedCarKeyInRequest, CarCatalogKeyType expectedCarKeyInResponse )
    {
        return (expectedCarKeyInRequest.getVendorSupplierID() == expectedCarKeyInResponse.getVendorSupplierID() &&
                expectedCarKeyInRequest.getCarVehicle().getCarCategoryCode() == expectedCarKeyInResponse.getCarVehicle().getCarCategoryCode() &&
                expectedCarKeyInRequest.getCarVehicle().getCarTypeCode() == expectedCarKeyInResponse.getCarVehicle().getCarTypeCode() &&
                expectedCarKeyInRequest.getCarVehicle().getCarTransmissionDriveCode() == expectedCarKeyInResponse.getCarVehicle().getCarTransmissionDriveCode() &&
                expectedCarKeyInRequest.getCarVehicle().getCarFuelACCode() == expectedCarKeyInResponse.getCarVehicle().getCarFuelACCode());
    }

    public static String verifyExtra(List<String> descriptionRawTextList)
    {
        final StringBuilder errorMsg = new StringBuilder();

        if (!descriptionRawTextList.isEmpty()) {
            errorMsg.append(MESSAGE_ERROR_COLLECTION_IN_RESPONSE);
            descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
        }

        if (errorMsg.toString().trim().length() > 0) {
            return errorMsg.toString();
        }
        else {
            return null;
        }
    }

    public static IVerification.VerificationResult getVerificationResult(List remarks, String methodName )
    {
        if(remarks.isEmpty()) {
            return new IVerification.VerificationResult(methodName, true, Arrays.asList(new String[]{"Success"}));
        } else {
            return new IVerification.VerificationResult(methodName, false, remarks);
        }
    }

    public static IVerification.VerificationResult getVerificationResult(String errorMessage, String methodName )
    {
        if(StringUtils.isEmpty(errorMessage)) {
            return new IVerification.VerificationResult(methodName, true, Arrays.asList(new String[]{"Success"}));
        } else {
            return new IVerification.VerificationResult(methodName, false, Arrays.asList(new String[]{errorMessage}));
        }
    }

}
