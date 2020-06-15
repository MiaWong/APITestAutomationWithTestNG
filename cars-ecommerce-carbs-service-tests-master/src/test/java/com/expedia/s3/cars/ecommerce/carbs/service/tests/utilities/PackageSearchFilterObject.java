package com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities;

public class PackageSearchFilterObject {

    private long vendorSupplierID;
    private long carCategoryCode;
    private long carTypeCode;
    private long carTransmissionDriveCode;
    private long carFuelACCode;
    private double savings;
    private double margin;
    private double totalPrice;
    private long capacity;

    public PackageSearchFilterObject(long vendorSupplierID, long carCategoryCode, long carTypeCode, long carTransmissionDriveCode, long carFuelACCode) {
        this.vendorSupplierID = vendorSupplierID;
        this.carCategoryCode = carCategoryCode;
        this.carTypeCode = carTypeCode;
        this.carTransmissionDriveCode = carTransmissionDriveCode;
        this.carFuelACCode = carFuelACCode;
    }

    public long getVendorSupplierID() {
        return vendorSupplierID;
    }

    public void setVendorSupplierID(long vendorSupplierID) {
        this.vendorSupplierID = vendorSupplierID;
    }

    public long getCarCategoryCode() {
        return carCategoryCode;
    }

    public void setCarCategoryCode(long carCategoryCode) {
        this.carCategoryCode = carCategoryCode;
    }

    public long getCarTypeCode() {
        return carTypeCode;
    }

    public void setCarTypeCode(long carTypeCode) {
        this.carTypeCode = carTypeCode;
    }

    public long getCarTransmissionDriveCode() {
        return carTransmissionDriveCode;
    }

    public void setCarTransmissionDriveCode(long carTransmissionDriveCode) {
        this.carTransmissionDriveCode = carTransmissionDriveCode;
    }

    public long getCarFuelACCode() {
        return carFuelACCode;
    }

    public void setCarFuelACCode(long carFuelACCode) {
        this.carFuelACCode = carFuelACCode;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }


}
