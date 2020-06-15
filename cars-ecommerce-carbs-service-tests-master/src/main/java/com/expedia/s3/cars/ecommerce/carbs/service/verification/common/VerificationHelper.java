package com.expedia.s3.cars.ecommerce.carbs.service.verification.common;

import java.util.List;

/**
 * Created by fehu on 11/10/2016.
 */
public class VerificationHelper {
    private static final String MESSAGE_ERROR_COLLECTION_IN_RESPONSE = "ErrorCollection is present in response.";

    private VerificationHelper() {
    }

    public static String verifyExtra(List<String> descriptionRawTextList) {
        final StringBuilder errorMsg = new StringBuilder();

        if (!descriptionRawTextList.isEmpty()) {
            errorMsg.append(MESSAGE_ERROR_COLLECTION_IN_RESPONSE);
            descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
        }

        if (errorMsg.toString().trim().length() > 0) {
            return errorMsg.toString();
        } else {
            return null;
        }
    }

}
