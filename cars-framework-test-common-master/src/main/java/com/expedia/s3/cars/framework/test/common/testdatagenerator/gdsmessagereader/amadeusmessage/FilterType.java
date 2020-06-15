package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

/**
 * Created by yyang4 on 3/15/2018.
 */
public class FilterType {
    private Boolean collectionBoolean;
    private Boolean deliveryBoolean;
    private Boolean outOfOfficeHourBoolean;
    private Boolean airportVicinityBoolean;

    public Boolean getCollectionBoolean() {
        return collectionBoolean;
    }

    public void setCollectionBoolean(Boolean collectionBoolean) {
        this.collectionBoolean = collectionBoolean;
    }

    public Boolean getDeliveryBoolean() {
        return deliveryBoolean;
    }

    public void setDeliveryBoolean(Boolean deliveryBoolean) {
        this.deliveryBoolean = deliveryBoolean;
    }

    public Boolean getOutOfOfficeHourBoolean() {
        return outOfOfficeHourBoolean;
    }

    public void setOutOfOfficeHourBoolean(Boolean outOfOfficeHourBoolean) {
        this.outOfOfficeHourBoolean = outOfOfficeHourBoolean;
    }

    public Boolean getAirportVicinityBoolean() {
        return airportVicinityBoolean;
    }

    public void setAirportVicinityBoolean(Boolean airportVicinityBoolean) {
        this.airportVicinityBoolean = airportVicinityBoolean;
    }
}
