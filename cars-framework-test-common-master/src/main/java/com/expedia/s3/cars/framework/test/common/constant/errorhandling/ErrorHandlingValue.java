package com.expedia.s3.cars.framework.test.common.constant.errorhandling;

/**
 * Created by fehu on 2/12/2017.
 */
public enum ErrorHandlingValue {

    ErrorHandling_search_CarTypeNotAvailable(new ErrorHandling("PMD", "CarTypeNotAvailableError", "No vehicles are available for the chosen period and criteria.  Please change your parameters.", null)),
    ErrorHandling_search_FieldInvalidError(new ErrorHandling("AAA", "FieldInvalidErrorXML", "Incorrect XML-Request", null)),
    ErrorHandling_search_FieldRequiredError(new ErrorHandling(null, "FieldRequiredError", "Default currency code is required", null)),
    ErrorHandling_search_RentalOutOfRangeError(new ErrorHandling(null, "RentalOutOfRangeError", "Pickup date/time is outside limits", null)),
    ErrorMapping_Detail_8(new ErrorHandling("EMDetail8", "FieldInvalidError", "Requested rate not available", "/car:CarInventoryKey/car:CarRate/car:CarRateQualifierCode")),
    ErrorMapping_Detail_16(new ErrorHandling("EMDetail16", "FieldInvalidError", "Requested rate not available", "/car:CarInventoryKey/car:CarRate/car:CarRateQualifierCode")),
    ErrorMapping_Detail_19(new ErrorHandling("EMDetail19", "FieldInvalidError", "Requested rate not available", "/car:CarInventoryKey/car:CarRate/car:CarRateQualifierCode")),
    ErrorMapping_uAPI_InvalidPNR(new ErrorHandling("", "FieldInvalidError", "The PNR must be 6 alpha numeric characters", "/base:ReferenceList/base:Reference[base:ReferenceCategoryCode = 'PNR']")),
    ErrorMapping_uAPI_EmptyPNRCode_GetReservation(new ErrorHandling("", "ReferenceRequiredError", "PNR locator missing", "/getreservation:CarSupplyConnectivityGetReservationRequest/car:CarReservationList/car:CarReservation[1]/base:ReferenceList/base:Reference[base:ReferenceCategoryCode='PNR']")),
    ErrorMapping_uAPI_EmptyPNRCode(new ErrorHandling("", "ReferenceRequiredError", "PNR locator missing", "/cancel:CarSupplyConnectivityCancelRequest/car:CarReservation/base:ReferenceList/base:Reference[base:ReferenceCategoryCode = 'PNR']")),
    ErrorMapping_uAPI_EmptyPNRValue(new ErrorHandling("", "FieldInvalidError", "Incorrect Format for PNR locator", "/cancel:CarSupplyConnectivityCancelRequest/car:CarReservation/base:ReferenceList/base:Reference[base:ReferenceCategoryCode = 'PNR']/base:ReferenceCode")),
    ErrorMapping_uAPI_InvalidAddress(new ErrorHandling("", "ReferenceUnavailableError", "Invalid or unavailable PNR","/base:ReferenceList/base:Reference[base:ReferenceCategoryCode = 'PNR']")),
    ErrorMapping_RESPONSEWASNULLORNOTINADOM_VAQ(new ErrorHandling("RESPONSEWASNULLORNOTINADOM", "DownstreamServiceUnavailableError","An error occurred while processing this request.",null));


    private ErrorHandling errorHandling;

    ErrorHandlingValue(ErrorHandling errorHandling) {
        this.errorHandling = errorHandling;
    }

    public ErrorHandling getErrorHandling() {
        return errorHandling;
    }
}
