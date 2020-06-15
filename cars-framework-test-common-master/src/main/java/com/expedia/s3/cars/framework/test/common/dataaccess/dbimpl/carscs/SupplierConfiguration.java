package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs;

/**
 * Created by yyang4 on 12/27/2016.
 */
public class SupplierConfiguration {
    private long supplierConfigurationID;
    private String environmentName;
    private String supplierID;
    private String settingName;
    private String settingValue;

    public long getSupplierConfigurationID() {
        return supplierConfigurationID;
    }

    public void setSupplierConfigurationID(long supplierConfigurationID) {
        this.supplierConfigurationID = supplierConfigurationID;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
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
}
