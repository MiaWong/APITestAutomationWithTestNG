package com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter;

/**
 * Created by fehu on 9/9/2018.
 */
public class ResultFilter {
    private GDSPCarType carType;
    private boolean needFilterMarkup;

    public GDSPCarType getCarType()
    {
        return carType;
    }

    public void setCarType(GDSPCarType carType)
    {
        this.carType = carType;
    }

    public boolean isNeedFilterMarkup()
    {
        return needFilterMarkup;
    }

    public void setNeedFilterMarkup(boolean needFilterMarkup)
    {
        this.needFilterMarkup = needFilterMarkup;
    }
}
