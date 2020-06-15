package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs;

import java.sql.Timestamp;

/**
 * Created by v-mechen on 9/19/2018.
 */
public class BookingRecordLocator {
    private Long bookingRecordLocatorID;
    private String transactionStateCode;
    private String jurisdictionCode;
    private String companyCode;
    private String managementUnitCode;
    private String bookingID;
    private String bookingItemID;
    private String changeContextGUID;
    private Timestamp createDate;
    private Timestamp updateDate;
    private String lastUpdatedBy;

    public Long getBookingRecordLocatorID() {
        return bookingRecordLocatorID;
    }

    public void setBookingRecordLocatorID(Long bookingRecordLocatorID) {
        this.bookingRecordLocatorID = bookingRecordLocatorID;
    }

    public String getTransactionStateCode() {
        return transactionStateCode;
    }

    public void setTransactionStateCode(String transactionStateCode) {
        this.transactionStateCode = transactionStateCode;
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

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getBookingItemID() {
        return bookingItemID;
    }

    public void setBookingItemID(String bookingItemID) {
        this.bookingItemID = bookingItemID;
    }

    public String getChangeContextGUID() {
        return changeContextGUID;
    }

    public void setChangeContextGUID(String changeContextGUID) {
        this.changeContextGUID = changeContextGUID;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }
}
