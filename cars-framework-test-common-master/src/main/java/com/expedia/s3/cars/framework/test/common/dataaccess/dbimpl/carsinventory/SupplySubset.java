package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by asharma1 on 9/27/2016.
 */
public class SupplySubset
{
    private long supplySubsetID;
    private int carBusinessModelID;
    private int supplierID;
    private long carItemID;

    public long getSupplySubsetID()
    {
        return supplySubsetID;
    }

    public void setSupplySubsetID(long supplySubsetID)
    {
        this.supplySubsetID = supplySubsetID;
    }

    public int getCarBusinessModelID()
    {
        return carBusinessModelID;
    }

    public void setCarBusinessModelID(int carBussinessModelID)
    {
        carBusinessModelID = carBussinessModelID;
    }

    public int getSupplierID()
    {
        return supplierID;
    }

    public void setSupplierID(int supplierID)
    {
        this.supplierID = supplierID;
    }

    public long getCarItemID() {
        return carItemID;
    }

    public void setCarItemID(long carItemID) {
        this.carItemID = carItemID;
    }
}
