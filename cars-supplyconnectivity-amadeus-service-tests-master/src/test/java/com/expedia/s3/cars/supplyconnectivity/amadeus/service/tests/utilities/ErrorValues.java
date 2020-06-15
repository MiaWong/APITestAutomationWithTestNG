package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities;

/**
 * Created by MiaWang on 12/26/2017.
 */
public enum ErrorValues
{
    //-------------------------search error handling input and expect output-------------------------
    //invalidFieldsAndValues format : invalidSearchCriteriaIndex-invalidField-invalidValue
    Error_search_Car_Company_Not_Exist_At_Location(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-14",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.LocationCode + "-LHR",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CarLocationCategoryCode + "-A",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_418"},
            "Car company does not exist at this location")),

    Error_search_Invalid_Pickup_Or_Dropoff_Location(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-14",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.LocationCode + "-LHR",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CarLocationCategoryCode + "-A",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_418"},
            "SearchRequest has invalid pickup or dropoff location")),

    Error_search_Requested_Supplier_Invalid(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-38",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.LocationCode + "-CDG",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_8307"},
            "Requested supplier is invalid")),

    Error_search_One_Invalid_Location_Type(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.StartLocationCode + "-PA",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_10451"},
            "Invalid Location Type")),

    Error_search_multiple_Invalid_Location_Type(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-2",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.StartLocationCode + "-PA",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_10451",

                    "1-" + ErrorInputAndExpectOutput.invalidFields.StartLocationCode + "-NC",
                    "1-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_10451"},
            "Invalid Location Type")),

    Error_search_NoRates_InvalidCDcode(new ErrorInputAndExpectOutput(
        new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-40",
                "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_902"},
        "Requested rate not available")),

    Error_search_NoRates(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-14",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.LocationCode + "-CDG",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_418"},
            "Car company does not exist at this location")),

    Error_search_InvalidSupplierID(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-123",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.LocationCode + "-PAR",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_8307"},
            "Requested supplier is invalid")),

    Error_search_UnableToProcess_InvalidSupplierID(new ErrorInputAndExpectOutput(
            new String[]{"0-" + ErrorInputAndExpectOutput.invalidFields.CriteriaListSize + "-1",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.Vendor + "-4555",
                    "0-" + ErrorInputAndExpectOutput.invalidFields.CDcode + "-EC_8307"},
            "Requested supplier is invalid")),


    //-------------------------getDetail error handling input and expect output-------------------------
    //invalidFieldsAndValues format : invalidField-invalidValue
    Error_GetDetails_InvalidRateCodeLength(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.RateCode + "-aaaaaaa"},
            "RateCode must not be greater than 6 valid characters")),

    Error_GetDetails_CurrencyNotAvailableError(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.CurrencyCode + "-EUR"},
            "Invalid Currency Code")),


    //-------------------------GetCostAndAvailability error handling input and expect output-------------------------
    //invalidFieldsAndValues format : invalidField-invalidValue
    Error_GetCostAndAvailability_CurrencyNotAvailableError(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.CurrencyCode + "-EUR"},
            "Invalid Currency Code")),

    //-------------------------Reserve error handling input and expect output-------------------------
    //invalidFieldsAndValues format : invalidField-invalidValue
    Error_Reserve_InvalidCollectionCityName(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.CollectionLocation_CityName + "-123456"},
            "CityName is empty or contains invalidcharacters (must match pattern [a-zA-Z\\s]*)")),
    Error_Reserve_InvalidDeliveryCityName(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.DeliveryLocation_CityName + "-123456"},
        "CityName is empty or contains invalidcharacters (must match pattern [a-zA-Z\\s]*)")),
    Error_Reserve_InvalidBillingCode(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.BillingCode + "-265127"},
            "NO AUTHORITY TO ISSUE VOUCHERS")),
    Error_Reserve_InvalidLoyaltyCardNumber(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.LoyaltyCardNumber + "-LoyaltyCardNumberErrorMapping"},
            "Invalid loyalty card number")),
    Error_Reserve_InvalidCreditCardFormOfPayment(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.CreditCardFormOfPayment + "-EH_2213"},
            "Invalid form of payment")),
    Error_Reserve_InvalidCorporateDiscount(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.CreditCardFormOfPayment + "-EH_10192"},
            "Invalid loyalty card number")),
    Error_Reserve_OutOfHours(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.LoyaltyCardNumber + "-EH_10193"},
            "Drop-off date-time is out of open hours")),
    Error_Reserve_InvalidFlightInfo(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.FlightNumber + "-EH_10194"},
            "Flight information including flight number is required")),
    Error_Reserve_InvalidLocation(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.LocationCode + "-EH_10197"},
            "Car company does not exist at this location")),

    //-------------------------get reservation error handling input and expect output-------------------------
    //invalidFieldsAndValues format : invalidField-invalidValue
    Error_GetReservation_CurrencyNotAvailableError(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.CurrencyCode + "-EUR"},
            "Invalid Currency Code")),


    //-------------------------Cancel error handling input and expect output-------------------------
    //invalidFieldsAndValues format : invalidField-invalidValue
    Error_Cancel_CanceledPNR(new ErrorInputAndExpectOutput(
            new String[]{ErrorInputAndExpectOutput.invalidFields.PNR + "-5CT6BA"},
            "cancelled")),
    ;

    //Amadeus Errors
    private ErrorInputAndExpectOutput errorInputAndExpectOutput;

    ErrorValues(ErrorInputAndExpectOutput errorInputAndExpectOutput)
    {
        this.errorInputAndExpectOutput = errorInputAndExpectOutput;
    }

    public ErrorInputAndExpectOutput getErrorInputAndExpectOutput()
    {
        return errorInputAndExpectOutput;
    }
}