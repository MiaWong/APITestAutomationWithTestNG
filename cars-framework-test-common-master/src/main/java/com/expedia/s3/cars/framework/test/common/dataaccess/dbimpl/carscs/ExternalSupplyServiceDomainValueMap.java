package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs;

/**
 * Created by yyang4 on 11/2/2016.
 */
public class ExternalSupplyServiceDomainValueMap {
    private long supplierID;
    private long messageSystemID;
    private String domainType;
    private String domainValue;
    private String externalDomainValue;
    private String createDate;
    private String createdBy;
    private String updateDate;
    private String lastUpdatedBy;

    public long getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(long supplierID) {
        this.supplierID = supplierID;
    }

    public long getMessageSystemID() {
        return messageSystemID;
    }

    public void setMessageSystemID(long messageSystemID) {
        this.messageSystemID = messageSystemID;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDomainValue() {
        return domainValue;
    }

    public void setDomainValue(String domainValue) {
        this.domainValue = domainValue;
    }

    public String getExternalDomainValue() {
        return externalDomainValue;
    }

    public void setExternalDomainValue(String externalDomainValue) {
        this.externalDomainValue = externalDomainValue;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
