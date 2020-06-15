package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs;

/**
 * Created by yyang4 on 12/19/2016.
 */
public class PoSToWorldspanDefaultSegmentMap {
    private String jurisdictionCode;
    private String companyCode;
    private String managementUnitCode;
    private String iata;
    private String iataPackage;
    private String branchCode;
    private String packageBranchCode;

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

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getPackageBranchCode() {
        return packageBranchCode;
    }

    public void setPackageBranchCode(String packageBranchCode) {
        this.packageBranchCode = packageBranchCode;
    }

    public String getIataPackage() {
        return iataPackage;
    }

    public void setIataPackage(String iataPackage) {
        this.iataPackage = iataPackage;
    }
}
