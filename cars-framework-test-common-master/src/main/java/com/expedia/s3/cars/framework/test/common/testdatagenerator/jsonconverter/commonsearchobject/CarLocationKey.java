package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class CarLocationKey //implements Serializable
{
    //@SerializedName("CarVendorLocationID")
    int carVendorLocationID;
    //@SerializedName("LocationCode")
    String locationCode;
    //@SerializedName("CarLocationCategoryCode")
    String carLocationCategoryCode;
    //@SerializedName("SupplierRawText")
    String supplierRawText;

    //  getters
    public int getCarVendorLocationID() { return this.carVendorLocationID; }
    public String getLocationCode() { return this.locationCode; }
    public String  getCarLocationCategoryCode() { return this.carLocationCategoryCode; }
    public String  getSupplierRawText() { return this.supplierRawText; }


    //  setters
    public void setCarVendorLocationID(int carVendorLocationID) { this.carVendorLocationID = carVendorLocationID; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public void setCarLocationCategoryCode(String carLocationCategoryCode) { this.carLocationCategoryCode = carLocationCategoryCode; }
    public void setSupplierRawText(String supplierRawText) { this.supplierRawText = supplierRawText; }
}