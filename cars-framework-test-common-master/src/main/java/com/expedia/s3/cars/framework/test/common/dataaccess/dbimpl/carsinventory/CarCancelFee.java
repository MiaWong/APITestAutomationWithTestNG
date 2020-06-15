package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by miawang on 9/20/2017.
 */
public class CarCancelFee
{
    private Long travelProductID;
    private double expediaFeeAmt;
    private String currencyCodeExpediaFeeAmt;

    public Long getTravelProductID()
    {
        return travelProductID;
    }

    public void setTravelProductID(Long travelProductID)
    {
        this.travelProductID = travelProductID;
    }

    public double getExpediaFeeAmt()
    {
        return expediaFeeAmt;
    }

    public void setExpediaFeeAmt(double expediaFeeAmt)
    {
        this.expediaFeeAmt = expediaFeeAmt;
    }

    public String getCurrencyCodeExpediaFeeAmt()
    {
        return currencyCodeExpediaFeeAmt;
    }

    public void setCurrencyCodeExpediaFeeAmt(String currencyCodeExpediaFeeAmt)
    {
        this.currencyCodeExpediaFeeAmt = currencyCodeExpediaFeeAmt;
    }
}
