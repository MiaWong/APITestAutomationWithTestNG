package com.expedia.s3.cars.framework.test.common.jsontestdata;

/**
 * Created by fehu on 7/14/2017.
 */
public class AuditLogTrackingData {
    private String travelProductID;
    private String partnerID;
    private String jurisdictionCode;
    private String companyCode;
    private String managementUnitCode;

    public String getTravelProductID() {
        return travelProductID;
    }

    public void setTravelProductID(String travelProductID) {
        this.travelProductID = travelProductID;
    }

    public String getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(String partnerID) {
        this.partnerID = partnerID;
    }

    public String getJurisdictionCode() {
        return jurisdictionCode;
    }

    public void setJurisdictionCode(String jurisdictionCode) {
        this.jurisdictionCode = jurisdictionCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getManagementUnitCode() {
        return managementUnitCode;
    }

    public void setManagementUnitCode(String managementUnitCode) {
        this.managementUnitCode = managementUnitCode;
    }
}
