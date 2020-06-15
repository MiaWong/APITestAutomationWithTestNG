package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by miawang on 9/26/2017.
 */
public class CarShareTarget
{
    private Long travelProductID;
    private String airportCode;
    private double shareByMarketPct;
    private int supplierID;

    public Long getTravelProductID()
    {
        return travelProductID;
    }

    public void setTravelProductID(Long travelProductID)
    {
        this.travelProductID = travelProductID;
    }

    public String getAirportCode()
    {
        return airportCode;
    }

    public void setAirportCode(String airportCode)
    {
        this.airportCode = airportCode;
    }

    public double getShareByMarketPct()
    {
        return shareByMarketPct;
    }

    public void setShareByMarketPct(double shareByMarketPct)
    {
        this.shareByMarketPct = shareByMarketPct;
    }

    public int getSupplierID()
    {
        return supplierID;
    }

    public void setSupplierID(int supplierID)
    {
        this.supplierID = supplierID;
    }
}
