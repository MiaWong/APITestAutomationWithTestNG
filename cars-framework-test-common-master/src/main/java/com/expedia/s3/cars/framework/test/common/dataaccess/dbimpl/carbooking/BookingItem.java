package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking;


import java.sql.Timestamp;

/**
 * Created by v-mechen on 9/11/2018.
 */
@SuppressWarnings("PMD")
public class BookingItem {
    private String bookingItemID;
    private String bookingID;
    private String bookingItemIDOriginal;
    private String bookingItemIDPrior;
    private String bookingItemTypeID;
    private Integer bookingItemStateID;
    private Integer bookingItemStateIDPending;
    private Integer bookingFulfillmentMethodID;
    private Integer bookingFulfillmentStateID;
    private String bookingFulfillmentDate;
    private Timestamp useDateBegin;
    private Timestamp useDateEnd;
    private Integer bookingRecordSystemID;
    private String bookingRecordSystemReferenceCode;
    private String accountingVendorID;
    private String supplierBookingConfirmationCode;
    private String supplierBookingConfirmationDate;
    private String supplierCancelConfirmationCode;
    private String supplierCancelConfirmationDate;
    private String bookingItemDesc;
    private String cancelDate;
    private String cancelTUID;
    private String createDate;
    private String createTUID;
    private String updateDate;
    private String updateTravelProductID;
    private String updateTUID;
    private String cRSReconstructDate;
    private String domaIntegerypeIDSKUDomain;
    private String sKUID;
    private String bookingFulfillmentDateScheduled;
    private Integer revenueReportingTypeID;
    private String reserveDate;
    private String reserveTUID;
    private String bookDate;
    private String bookTUID;
    private String distributorBookingItemReferenceCode;
    private String reserveEndDate;

    public String getBookingItemID() {
        return bookingItemID;
    }

    public void setBookingItemID(String bookingItemID) {
        this.bookingItemID = bookingItemID;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getBookingItemIDOriginal() {
        return bookingItemIDOriginal;
    }

    public void setBookingItemIDOriginal(String bookingItemIDOriginal) {
        this.bookingItemIDOriginal = bookingItemIDOriginal;
    }

    public String getBookingItemIDPrior() {
        return bookingItemIDPrior;
    }

    public void setBookingItemIDPrior(String bookingItemIDPrior) {
        this.bookingItemIDPrior = bookingItemIDPrior;
    }

    public String getBookingItemTypeID() {
        return bookingItemTypeID;
    }

    public void setBookingItemTypeID(String bookingItemTypeID) {
        this.bookingItemTypeID = bookingItemTypeID;
    }

    public Integer getBookingItemStateID() {
        return bookingItemStateID;
    }

    public void setBookingItemStateID(Integer bookingItemStateID) {
        this.bookingItemStateID = bookingItemStateID;
    }

    public Integer getBookingItemStateIDPending() {
        return bookingItemStateIDPending;
    }

    public void setBookingItemStateIDPending(Integer bookingItemStateIDPending) {
        this.bookingItemStateIDPending = bookingItemStateIDPending;
    }

    public Integer getBookingFulfillmentMethodID() {
        return bookingFulfillmentMethodID;
    }

    public void setBookingFulfillmentMethodID(Integer bookingFulfillmentMethodID) {
        this.bookingFulfillmentMethodID = bookingFulfillmentMethodID;
    }

    public Integer getBookingFulfillmentStateID() {
        return bookingFulfillmentStateID;
    }

    public void setBookingFulfillmentStateID(Integer bookingFulfillmentStateID) {
        this.bookingFulfillmentStateID = bookingFulfillmentStateID;
    }

    public String getBookingFulfillmentDate() {
        return bookingFulfillmentDate;
    }

    public void setBookingFulfillmentDate(String bookingFulfillmentDate) {
        this.bookingFulfillmentDate = bookingFulfillmentDate;
    }

    public Timestamp getUseDateBegin() {
        return useDateBegin;
    }

    public void setUseDateBegin(Timestamp useDateBegin) {
        this.useDateBegin = useDateBegin;
    }

    public Timestamp getUseDateEnd() {
        return useDateEnd;
    }

    public void setUseDateEnd(Timestamp useDateEnd) {
        this.useDateEnd = useDateEnd;
    }

    public Integer getBookingRecordSystemID() {
        return bookingRecordSystemID;
    }

    public void setBookingRecordSystemID(Integer bookingRecordSystemID) {
        this.bookingRecordSystemID = bookingRecordSystemID;
    }

    public String getBookingRecordSystemReferenceCode() {
        return bookingRecordSystemReferenceCode;
    }

    public void setBookingRecordSystemReferenceCode(String bookingRecordSystemReferenceCode) {
        this.bookingRecordSystemReferenceCode = bookingRecordSystemReferenceCode;
    }

    public String getAccountingVendorID() {
        return accountingVendorID;
    }

    public void setAccountingVendorID(String accountingVendorID) {
        this.accountingVendorID = accountingVendorID;
    }

    public String getSupplierBookingConfirmationCode() {
        return supplierBookingConfirmationCode;
    }

    public void setSupplierBookingConfirmationCode(String supplierBookingConfirmationCode) {
        this.supplierBookingConfirmationCode = supplierBookingConfirmationCode;
    }

    public String getSupplierBookingConfirmationDate() {
        return supplierBookingConfirmationDate;
    }

    public void setSupplierBookingConfirmationDate(String supplierBookingConfirmationDate) {
        this.supplierBookingConfirmationDate = supplierBookingConfirmationDate;
    }

    public String getSupplierCancelConfirmationCode() {
        return supplierCancelConfirmationCode;
    }

    public void setSupplierCancelConfirmationCode(String supplierCancelConfirmationCode) {
        this.supplierCancelConfirmationCode = supplierCancelConfirmationCode;
    }

    public String getSupplierCancelConfirmationDate() {
        return supplierCancelConfirmationDate;
    }

    public void setSupplierCancelConfirmationDate(String supplierCancelConfirmationDate) {
        this.supplierCancelConfirmationDate = supplierCancelConfirmationDate;
    }

    public String getBookingItemDesc() {
        return bookingItemDesc;
    }

    public void setBookingItemDesc(String bookingItemDesc) {
        this.bookingItemDesc = bookingItemDesc;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }

    public String getCancelTUID() {
        return cancelTUID;
    }

    public void setCancelTUID(String cancelTUID) {
        this.cancelTUID = cancelTUID;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateTUID() {
        return createTUID;
    }

    public void setCreateTUID(String createTUID) {
        this.createTUID = createTUID;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateTravelProductID() {
        return updateTravelProductID;
    }

    public void setUpdateTravelProductID(String updateTravelProductID) {
        this.updateTravelProductID = updateTravelProductID;
    }

    public String getUpdateTUID() {
        return updateTUID;
    }

    public void setUpdateTUID(String updateTUID) {
        this.updateTUID = updateTUID;
    }

    public String getcRSReconstructDate() {
        return cRSReconstructDate;
    }

    public void setcRSReconstructDate(String cRSReconstructDate) {
        this.cRSReconstructDate = cRSReconstructDate;
    }

    public String getDomaIntegerypeIDSKUDomain() {
        return domaIntegerypeIDSKUDomain;
    }

    public void setDomaIntegerypeIDSKUDomain(String domaIntegerypeIDSKUDomain) {
        this.domaIntegerypeIDSKUDomain = domaIntegerypeIDSKUDomain;
    }

    public String getsKUID() {
        return sKUID;
    }

    public void setsKUID(String sKUID) {
        this.sKUID = sKUID;
    }

    public String getBookingFulfillmentDateScheduled() {
        return bookingFulfillmentDateScheduled;
    }

    public void setBookingFulfillmentDateScheduled(String bookingFulfillmentDateScheduled) {
        this.bookingFulfillmentDateScheduled = bookingFulfillmentDateScheduled;
    }

    public Integer getRevenueReportingTypeID() {
        return revenueReportingTypeID;
    }

    public void setRevenueReportingTypeID(Integer revenueReportingTypeID) {
        this.revenueReportingTypeID = revenueReportingTypeID;
    }

    public String getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(String reserveDate) {
        this.reserveDate = reserveDate;
    }

    public String getReserveTUID() {
        return reserveTUID;
    }

    public void setReserveTUID(String reserveTUID) {
        this.reserveTUID = reserveTUID;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getBookTUID() {
        return bookTUID;
    }

    public void setBookTUID(String bookTUID) {
        this.bookTUID = bookTUID;
    }

    public String getDistributorBookingItemReferenceCode() {
        return distributorBookingItemReferenceCode;
    }

    public void setDistributorBookingItemReferenceCode(String distributorBookingItemReferenceCode) {
        this.distributorBookingItemReferenceCode = distributorBookingItemReferenceCode;
    }

    public String getReserveEndDate() {
        return reserveEndDate;
    }

    public void setReserveEndDate(String reserveEndDate) {
        this.reserveEndDate = reserveEndDate;
    }


}
