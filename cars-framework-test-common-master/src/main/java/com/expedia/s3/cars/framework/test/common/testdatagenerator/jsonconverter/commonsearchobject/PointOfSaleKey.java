package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class PointOfSaleKey //implements Serializable
{
    //@SerializedName("JurisdictionCountryCode")
    String jurisdictionCountryCode;
    //@SerializedName("CompanyCode")
    String companyCode;
    //@SerializedName("ManagementUnitCode")
    String managementUnitCode;

    //  getters
    public String getJurisdictionCountryCode() { return this.jurisdictionCountryCode; }
    public String getCompanyCode() { return this.companyCode; }
    public String getManagementUnitCode() { return this.managementUnitCode; }

    //  setters
    public void setJurisdictionCountryCode(String jurisdictionCountryCode) { this.jurisdictionCountryCode = jurisdictionCountryCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
    public void setManagementUnitCode(String managementUnitCode) { this.managementUnitCode = managementUnitCode; }
}
