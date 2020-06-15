package com.expedia.s3.cars.supply.service.verification;

import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorListType;
import com.expedia.e3.data.errortypes.defn.v4.ReferenceInvalidErrorListType;
import com.expedia.e3.data.errortypes.defn.v4.ReferenceUnavailableErrorListType;
import com.expedia.s3.cars.data.carerrortypes.defn.v2.CurrencyNotAvailableErrorType;
import com.expedia.s3.cars.supply.messages.cancel.defn.v4.CarSupplyCancelResponseType;
import com.expedia.s3.cars.supply.messages.getcostandavailability.defn.v4.CarSupplyGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsResponseType;
import com.expedia.s3.cars.supply.messages.getreservation.defn.v4.CarSupplyGetReservationResponseType;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveResponseType;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

/**
 * Created by v-mechen on 12/5/2017.
 */
public class ErrorVerifier {
    private ErrorVerifier()
    {}

    public static void verifySearchErrorXpath(CarSupplySearchResponseType response, String errorXpath, String errorDesRawText) {
        //Get errorList
        final FieldInvalidErrorListType invalidErrorList= response.getErrorCollectionList().getErrorCollection().get(0).getFieldInvalidErrorList();

        //Verify expected error returned
        verifyFieldInvalidError(invalidErrorList, errorXpath, errorDesRawText);

    }

    public static void verifyGetDetailsErrorXpath(CarSupplyGetDetailsResponseType response, String errorXpath, String errorDesRawText) {
        //Get errorList
        final FieldInvalidErrorListType invalidErrorList= response.getErrorCollection().getFieldInvalidErrorList();

        //Verify expected error returned
        verifyFieldInvalidError(invalidErrorList, errorXpath, errorDesRawText);

    }

    public static void verifyCostAvailErrorXpath(CarSupplyGetCostAndAvailabilityResponseType response, String errorXpath, String errorDesRawText) {
        //Get errorList
        final FieldInvalidErrorListType invalidErrorList= response.getErrorCollection().getFieldInvalidErrorList();

        //Verify expected error returned
        verifyFieldInvalidError(invalidErrorList, errorXpath, errorDesRawText);

    }

    public static void verifyReserveErrorXpath(CarSupplyReserveResponseType response, String errorXpath, String errorDesRawText) {
        //Get errorList
        final FieldInvalidErrorListType invalidErrorList= response.getErrorCollection().getFieldInvalidErrorList();

        //Verify expected error returned
        verifyFieldInvalidError(invalidErrorList, errorXpath, errorDesRawText);

    }

    public static void verifyGetReservationError(CarSupplyGetReservationResponseType response, String errorType, String errorXpath, String errorDesRawText) {
        if("ReferenceUnavailableError".equals(errorType)) {
            //Get errorList
            final ReferenceUnavailableErrorListType invalidErrorList = response.getErrorCollection().getReferenceUnavailableErrorList();

            //Verify expected error returned
            verifyReferenceUnavailableError(invalidErrorList, errorXpath, errorDesRawText);
        }

        if("FieldInvalidError".equals(errorType)) {
            //Get errorList
            final FieldInvalidErrorListType invalidErrorList= response.getErrorCollection().getFieldInvalidErrorList();

            //Verify expected error returned
            verifyFieldInvalidError(invalidErrorList, errorXpath, errorDesRawText);
        }

        if("CurrencyNotAvailableError".equals(errorType)){
            //Get errorList
            final CurrencyNotAvailableErrorType error= response.getErrorCollection().getCurrencyNotAvailableError();

            //Verify expected error returned
            verifyCurrencyNotAvailableError(error, errorDesRawText);
        }

    }

    public static void verifyCancelErrorXpath(CarSupplyCancelResponseType response, String errorType, String errorXpath, String errorDesRawText) {
        if("ReferenceUnavailableError".equals(errorType)) {
            //Get errorList
            final ReferenceUnavailableErrorListType invalidErrorList = response.getErrorCollection().getReferenceUnavailableErrorList();

            //Verify expected error returned
            verifyReferenceUnavailableError(invalidErrorList, errorXpath, errorDesRawText);
        }
        //Invalid
        if("ReferenceInvalidError".equals(errorType)) {
            //Get errorList
            final ReferenceInvalidErrorListType invalidErrorList = response.getErrorCollection().getReferenceInvalidErrorList();

            //Verify expected error returned
            verifyReferenceInvalidError(invalidErrorList, errorXpath, errorDesRawText);
        }

    }

    public static void verifyFieldInvalidError(final FieldInvalidErrorListType invalidErrorList, String errorXpath, String errorDesRawText) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();

        if (null == invalidErrorList || CollectionUtils.isEmpty(invalidErrorList.getFieldInvalidError())) {
            Assert.fail("FieldInvalidError is not returned in response!");
        }

        if(StringUtils.isEmpty(invalidErrorList.getFieldInvalidError().get(0).getFieldKey().getXPath()) ||
                ! errorXpath.equals(invalidErrorList.getFieldInvalidError().get(0).getFieldKey().getXPath()) ) {
            errorMsg.append("FieldInvalidError in respponse ").append(invalidErrorList.getFieldInvalidError().get(0).getFieldKey().getXPath()).append(
                    "is not equal to expected ").append(errorXpath);
        }

        if(StringUtils.isEmpty(invalidErrorList.getFieldInvalidError().get(0).getDescriptionRawText()) ||
                ! errorDesRawText.equals(invalidErrorList.getFieldInvalidError().get(0).getDescriptionRawText()) ) {
            errorMsg.append("DescriptionRawText in respponse ").append(invalidErrorList.getFieldInvalidError().get(0).getDescriptionRawText()).append(
                    "is not equal to expected ").append(errorDesRawText);
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

    }

    public static void verifyReferenceUnavailableError(final ReferenceUnavailableErrorListType invalidErrorList, String errorXpath, String errorDesRawText) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();

        if (null == invalidErrorList || CollectionUtils.isEmpty(invalidErrorList.getReferenceUnavailableError())) {
            Assert.fail("ReferenceUnavailableError is not returned in response!");
        }

        if(StringUtils.isEmpty(invalidErrorList.getReferenceUnavailableError().get(0).getFieldKey().getXPath()) ||
                ! errorXpath.equals(invalidErrorList.getReferenceUnavailableError().get(0).getFieldKey().getXPath()) ) {
            errorMsg.append("FieldInvalidError in respponse ").append(invalidErrorList.getReferenceUnavailableError().get(0).getFieldKey().getXPath()).append(
                    "is not equal to expected ").append(errorXpath);
        }

        if(StringUtils.isEmpty(invalidErrorList.getReferenceUnavailableError().get(0).getDescriptionRawText()) ||
                ! errorDesRawText.equals(invalidErrorList.getReferenceUnavailableError().get(0).getDescriptionRawText()) ) {
            errorMsg.append("DescriptionRawText in respponse ").append(invalidErrorList.getReferenceUnavailableError().get(0).getDescriptionRawText()).append(
                    "is not equal to expected ").append(errorDesRawText);
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

    }

    public static void verifyReferenceInvalidError(final ReferenceInvalidErrorListType invalidErrorList, String errorXpath, String errorDesRawText) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();

        if (null == invalidErrorList || CollectionUtils.isEmpty(invalidErrorList.getReferenceInvalidError())) {
            Assert.fail("ReferenceUnavailableError is not returned in response!");
        }

        if(StringUtils.isEmpty(invalidErrorList.getReferenceInvalidError().get(0).getFieldKey().getXPath()) ||
                ! errorXpath.equals(invalidErrorList.getReferenceInvalidError().get(0).getFieldKey().getXPath()) ) {
            errorMsg.append("FieldInvalidError in respponse ").append(invalidErrorList.getReferenceInvalidError().get(0).getFieldKey().getXPath()).append(
                    "is not equal to expected ").append(errorXpath);
        }

        if(StringUtils.isEmpty(invalidErrorList.getReferenceInvalidError().get(0).getDescriptionRawText()) ||
                ! errorDesRawText.equals(invalidErrorList.getReferenceInvalidError().get(0).getDescriptionRawText()) ) {
            errorMsg.append("DescriptionRawText in respponse ").append(invalidErrorList.getReferenceInvalidError().get(0).getDescriptionRawText()).append(
                    "is not equal to expected ").append(errorDesRawText);
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

    }

    public static void verifyCurrencyNotAvailableError(final CurrencyNotAvailableErrorType error, String errorDesRawText) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();

        if (null == error) {
            Assert.fail("CurrencyNotAvailableError is not returned in response!");
        }

        if(StringUtils.isEmpty(error.getDescriptionRawText()) ||
                ! errorDesRawText.equals(error.getDescriptionRawText()) ) {
            errorMsg.append("DescriptionRawText in respponse ").append(error.getDescriptionRawText()).append(
                    "is not equal to expected ").append(errorDesRawText);
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

    }
}
