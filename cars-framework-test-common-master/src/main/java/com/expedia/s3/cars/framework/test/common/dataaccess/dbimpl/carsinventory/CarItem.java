package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by asharma1 on 9/27/2016.
 */
public class CarItem
{
    private Long carItemID;
    private String accountingVendorID;
    private Long supplierID;
    private int carBusinessModelID;
    private boolean prepaidBool;

    public Long getCarItemID()
    {
        return carItemID;
    }

    public void setCarItemID(Long carItemID)
    {
        this.carItemID = carItemID;
    }

    public String getAccountingVendorID() {
        return accountingVendorID;
    }

    public void setAccountingVendorID(String accountingVendorID) {
        this.accountingVendorID = accountingVendorID;
    }

    public Long getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(Long supplierID) {
        this.supplierID = supplierID;
    }

    public int getCarBusinessModelID() {
        return carBusinessModelID;
    }

    public void setCarBusinessModelID(int carBusinessModelID) {
        this.carBusinessModelID = carBusinessModelID;
    }

    public boolean isPrepaidBool() {
        return prepaidBool;
    }

    public void setPrepaidBool(boolean prepaidBool) {
        this.prepaidBool = prepaidBool;
    }
}
