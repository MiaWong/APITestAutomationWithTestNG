package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by fehu on 9/3/2018.
 */
public class CarShuttleCategory {
    private  String  carShuttleCategoryID;
    private  String  carShuttleCategoryCode;

    public String getCarShuttleCategoryID()
    {
        return carShuttleCategoryID;
    }

    public void setCarShuttleCategoryID(String carShuttleCategoryID)
    {
        this.carShuttleCategoryID = carShuttleCategoryID;
    }

    public String getCarShuttleCategoryCode()
    {
        return carShuttleCategoryCode;
    }

    public void setCarShuttleCategoryCode(String carShuttleCategoryCode)
    {
        this.carShuttleCategoryCode = carShuttleCategoryCode;
    }
}
