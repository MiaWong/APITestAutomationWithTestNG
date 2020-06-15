package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

/**
 * Created by asharma1 on 9/23/2016.
 */
public class PoSToCarProductCatalogMap
{
    private String jurisdictionCode;
    private String companyCode;
    private String managementUnitCode;
    private int carProductCatalogID;

    public String getJurisdictionCode()
    {
        return jurisdictionCode;
    }

    public void setJurisdictionCode(String jurisdictionCode)
    {
        this.jurisdictionCode = jurisdictionCode;
    }

    public String getCompanyCode()
    {
        return companyCode;
    }

    public void setCompanyCode(String companyCode)
    {
        this.companyCode = companyCode;
    }

    public String getManagementUnitCode()
    {
        return managementUnitCode;
    }

    public void setManagementUnitCode(String managementUnitCode)
    {
        this.managementUnitCode = managementUnitCode;
    }

    public int getCarProductCatalogID()
    {
        return carProductCatalogID;
    }

    public void setCarProductCatalogID(int carProductCatalogID)
    {
        this.carProductCatalogID = carProductCatalogID;
    }
}
