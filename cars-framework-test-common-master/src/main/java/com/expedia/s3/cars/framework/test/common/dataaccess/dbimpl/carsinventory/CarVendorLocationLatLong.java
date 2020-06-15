package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by asharma1 on 9/27/2016.
 */
@SuppressWarnings("PMD")
public class CarVendorLocationLatLong
{
    @SuppressWarnings("CPD-START")
    private long carVendorLocationID;
    public String locationCode;
    public String carLocationCategoryCode;
    public String supplierRawText;
    public String latitude;
    public String longitude;
    public String distance;

    public String supplierID;
    public String carVendorLocationCode;
    public String airportCode;
    public String iSOCountryCode;
    public String streetAddress;
    public String cityName;
    public String stateProvinceName;
    public String postalCode;
    public String phoneNumber;
    public String faxNumber;
    public String locationTypeID;
    public String deliveryBool;
    public String collectionBool;
    public String outOfOfficeHoursBool;
    public String carShuttleCategoryID;


    public long getCarVendorLocationID() {
        return carVendorLocationID;
    }

    public void setCarVendorLocationID(long carVendorLocationID) {
        this.carVendorLocationID = carVendorLocationID;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getCarLocationCategoryCode() {
        return carLocationCategoryCode;
    }

    public void setCarLocationCategoryCode(String carLocationCategoryCode) {
        this.carLocationCategoryCode = carLocationCategoryCode;
    }

    public String getSupplierRawText() {
        return supplierRawText;
    }

    public void setSupplierRawText(String supplierRawText) {
        this.supplierRawText = supplierRawText;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    public String getCarVendorLocationCode() {
        return carVendorLocationCode;
    }

    public void setCarVendorLocationCode(String carVendorLocationCode) {
        this.carVendorLocationCode = carVendorLocationCode;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getiSOCountryCode() {
        return iSOCountryCode;
    }

    public void setiSOCountryCode(String iSOCountryCode) {
        this.iSOCountryCode = iSOCountryCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateProvinceName() {
        return stateProvinceName;
    }

    public void setStateProvinceName(String stateProvinceName) {
        this.stateProvinceName = stateProvinceName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getDeliveryBool() {
        return deliveryBool;
    }

    public void setDeliveryBool(String deliveryBool) {
        this.deliveryBool = deliveryBool;
    }

    public String getCollectionBool() {
        return collectionBool;
    }

    public void setCollectionBool(String collectionBool) {
        this.collectionBool = collectionBool;
    }

    public String getOutOfOfficeHoursBool() {
        return outOfOfficeHoursBool;
    }

    public void setOutOfOfficeHoursBool(String outOfOfficeHoursBool) {
        this.outOfOfficeHoursBool = outOfOfficeHoursBool;
    }

    public String getLocationTypeID() {
        return locationTypeID;
    }

    public void setLocationTypeID(String locationTypeID) {
        this.locationTypeID = locationTypeID;
    }

    public String getCarShuttleCategoryID() {
        return carShuttleCategoryID;
    }

    @SuppressWarnings("CPD-END")
    public void setCarShuttleCategoryID(String carShuttleCategoryID) {
        this.carShuttleCategoryID = carShuttleCategoryID;
    }
}
