package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by yyang4 on 11/15/2016.
 */
public class CarVendor {
    private String carVendorCode;
    private String carVendorID;
    private String supplierID;

    public String getCarVendorCode() {
        return carVendorCode;
    }

    public void setCarVendorCode(String carVendorCode) {
        this.carVendorCode = carVendorCode;
    }

    public String getCarVendorID() {
        return carVendorID;
    }

    public void setCarVendorID(String carVendorID) {
        this.carVendorID = carVendorID;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
}
