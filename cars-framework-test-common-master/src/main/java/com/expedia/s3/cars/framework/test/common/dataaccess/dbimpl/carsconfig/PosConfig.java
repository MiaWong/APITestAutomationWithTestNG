package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig;

import java.sql.Date;

/**
 * Created by fehu on 10/24/2016.
 */
public class PosConfig {
    private int poSConfigurationID;
     private String environmentName;
    private String jurisdictionCode;
    private String companyCode;
    private String managementUnitCode;
    private String settingName;
    private String settingValue;
    private Date createDate;
    private String createdBy;
    private Date updateDate;
    private String lastUpdatedBy;
    private String delete;

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public int getPoSConfigurationID() {
        return poSConfigurationID;
    }

    public void setPoSConfigurationID(int poSConfigurationID) {
        this.poSConfigurationID = poSConfigurationID;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
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

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
