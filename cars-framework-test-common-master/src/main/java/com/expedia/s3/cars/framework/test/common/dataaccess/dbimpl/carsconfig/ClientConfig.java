package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig;


import java.sql.Date;

/**
 * Created by fehu on 2/20/2017.
 */
public class ClientConfig {
    private Integer clientConfigurationID;
    private String environmentName;
    private Integer clientId;
    private String settingName;
    private String settingValue;
    private Date createDate;
    private String createBy;
    private Date updateDate;
    private String lastUpdateBy;


    public Integer getClientConfigurationID() {
        return clientConfigurationID;
    }

    public void setClientConfigurationID(Integer clientConfigurationID) {
        this.clientConfigurationID = clientConfigurationID;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
}
