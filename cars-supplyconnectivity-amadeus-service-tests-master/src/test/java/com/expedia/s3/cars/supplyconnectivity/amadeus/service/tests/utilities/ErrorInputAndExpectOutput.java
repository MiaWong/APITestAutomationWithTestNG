package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities;

/**
 * Created by miawang on 2/11/2018.
 */
public class ErrorInputAndExpectOutput
{
    //format : invalidSearchCriteriaIndex-invalidField-invalidValue
    private String[] invalidFieldsAndValues;
    private String expectErrorMessage;

    public String[] getInvalidFieldsAndValues()
    {
        return invalidFieldsAndValues;
    }


    public String getExpectErrorMessage()
    {
        return expectErrorMessage;
    }

    public ErrorInputAndExpectOutput(String[] invalidFieldsAndValues, String expectErrorMessage)
    {
        this.invalidFieldsAndValues = invalidFieldsAndValues;
        this.expectErrorMessage = expectErrorMessage;
    }

    public enum invalidFields
    {
        CriteriaListSize,

        Vendor,

        CurrencyCode,

        //this will handle both start and end location code.
        LocationCode, StartLocationCode, EndLocationCode,

        CarLocationCategoryCode,StartCarLocationCategoryCode,EndCarLocationCategoryCode,

        SupplierRawText,

        CollectionLocation_CityName, DeliveryLocation_CityName,

        BillingCode,

        LoyaltyCardNumber,

        CreditCardFormOfPayment,

        CDcode,RateCode,

        FlightNumber,

        PNR;
    }
}
