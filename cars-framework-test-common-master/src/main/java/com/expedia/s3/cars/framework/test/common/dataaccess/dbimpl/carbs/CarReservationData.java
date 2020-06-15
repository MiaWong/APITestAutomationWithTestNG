package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs;

import java.sql.Timestamp;

/**
 * Created by v-mechen on 12/21/2016.
 */
public class CarReservationData {
    private String jurisdictionCode;
    private String companyCode;
    private String managementUnitCode;
    private String bookingItemID;
    private byte[] carReservationNodeData;
    private String carReservationNodeMajorVersion;
    private String carReservationNodeMinorVersion;
    private int carReservationDataExtendedElementCnt;
    private int carReservationDataExtendedPriceListCnt;
    private Timestamp useDateEnd;
    private Timestamp createDate;

    public String getCarReservationNodeMinorVersion() {
        return carReservationNodeMinorVersion;
    }

    public void setCarReservationNodeMinorVersion(String carReservationNodeMinorVersion) {
        this.carReservationNodeMinorVersion = carReservationNodeMinorVersion;
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

    public String getBookingItemID() {
        return bookingItemID;
    }

    public void setBookingItemID(String bookingItemID) {
        this.bookingItemID = bookingItemID;
    }

    public byte[] getCarReservationNodeData() {
        return carReservationNodeData;
    }

    public void setCarReservationNodeData(byte[] carReservationNodeData) {
        this.carReservationNodeData = carReservationNodeData;
    }

    public String getCarReservationNodeMajorVersion() {
        return carReservationNodeMajorVersion;
    }

    public void setCarReservationNodeMajorVersion(String carReservationNodeMajorVersion) {
        this.carReservationNodeMajorVersion = carReservationNodeMajorVersion;
    }

    public int getCarReservationDataExtendedElementCnt() {
        return carReservationDataExtendedElementCnt;
    }

    public void setCarReservationDataExtendedElementCnt(int carReservationDataExtendedElementCnt) {
        this.carReservationDataExtendedElementCnt = carReservationDataExtendedElementCnt;
    }

    public int getCarReservationDataExtendedPriceListCnt() {
        return carReservationDataExtendedPriceListCnt;
    }

    public void setCarReservationDataExtendedPriceListCnt(int carReservationDataExtendedPriceListCnt) {
        this.carReservationDataExtendedPriceListCnt = carReservationDataExtendedPriceListCnt;
    }

    public Timestamp getUseDateEnd() {
        return useDateEnd;
    }

    public void setUseDateEnd(Timestamp useDateEnd) {
        this.useDateEnd = useDateEnd;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }



}
