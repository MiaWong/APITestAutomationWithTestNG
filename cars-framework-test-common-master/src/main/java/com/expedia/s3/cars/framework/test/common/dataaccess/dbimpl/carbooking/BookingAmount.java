package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking;

import java.math.BigDecimal;

/**
 * Created by v-mechen on 9/3/2018.
 */
@SuppressWarnings("PMD")
public class BookingAmount {
    private String bookingID;
    private String bookingAmountSeqNbr;
    private String bookingItemID;
    private String bookingItemTypeID;
    private String currencyCodeCost;
    private String currencyCodePrice;
    private String bookingAmountRefCodeCost;
    private String bookingAmountRefCodePrice;
    private String cancelBool;
    private String createDate;
    private String createTUID;
    private String bookingAmountLevelID;
    private String bookingItemInventorySeqNbr;
    private String monetaryClassID;
    private String monetaryCalculationSystemID;
    private String monetaryCalculationID;
    private String bookingAmountDescCost;
    private BigDecimal transactionAmtCost;
    private BigDecimal transactionAmtPrice;
    private String bookingAmountDescPrice;
    private String bookingAmountRowGUID;
    private String orderOperationCorrelationID;
    private String primaryModificationReasonID;
    private String secondaryModificationReasonID;

    public String getPrimaryModificationReasonID() {
        return primaryModificationReasonID;
    }

    public void setPrimaryModificationReasonID(String primaryModificationReasonID) {
        this.primaryModificationReasonID = primaryModificationReasonID;
    }

    public String getOrderOperationCorrelationID() {
        return orderOperationCorrelationID;
    }

    public void setOrderOperationCorrelationID(String orderOperationCorrelationID) {
        this.orderOperationCorrelationID = orderOperationCorrelationID;
    }

    public String getSecondaryModificationReasonID() {
        return secondaryModificationReasonID;
    }

    public void setSecondaryModificationReasonID(String secondaryModificationReasonID) {
        this.secondaryModificationReasonID = secondaryModificationReasonID;
    }


    public String getBookingItemInventorySeqNbr() {
        return bookingItemInventorySeqNbr;
    }

    public void setBookingItemInventorySeqNbr(String bookingItemInventorySeqNbr) {
        this.bookingItemInventorySeqNbr = bookingItemInventorySeqNbr;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getBookingAmountSeqNbr() {
        return bookingAmountSeqNbr;
    }

    public void setBookingAmountSeqNbr(String bookingAmountSeqNbr) {
        this.bookingAmountSeqNbr = bookingAmountSeqNbr;
    }

    public String getBookingItemID() {
        return bookingItemID;
    }

    public void setBookingItemID(String bookingItemID) {
        this.bookingItemID = bookingItemID;
    }

    public String getBookingItemTypeID() {
        return bookingItemTypeID;
    }

    public void setBookingItemTypeID(String bookingItemTypeID) {
        this.bookingItemTypeID = bookingItemTypeID;
    }

    public String getCurrencyCodeCost() {
        return currencyCodeCost;
    }

    public void setCurrencyCodeCost(String currencyCodeCost) {
        this.currencyCodeCost = currencyCodeCost;
    }

    public String getCurrencyCodePrice() {
        return currencyCodePrice;
    }

    public void setCurrencyCodePrice(String currencyCodePrice) {
        this.currencyCodePrice = currencyCodePrice;
    }

    public String getBookingAmountRefCodeCost() {
        return bookingAmountRefCodeCost;
    }

    public void setBookingAmountRefCodeCost(String bookingAmountRefCodeCost) {
        this.bookingAmountRefCodeCost = bookingAmountRefCodeCost;
    }

    public String getBookingAmountRefCodePrice() {
        return bookingAmountRefCodePrice;
    }

    public void setBookingAmountRefCodePrice(String bookingAmountRefCodePrice) {
        this.bookingAmountRefCodePrice = bookingAmountRefCodePrice;
    }

    public String getCancelBool() {
        return cancelBool;
    }

    public void setCancelBool(String cancelBool) {
        this.cancelBool = cancelBool;
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

    public String getBookingAmountLevelID() {
        return bookingAmountLevelID;
    }

    public void setBookingAmountLevelID(String bookingAmountLevelID) {
        this.bookingAmountLevelID = bookingAmountLevelID;
    }

    public String getMonetaryClassID() {
        return monetaryClassID;
    }

    public void setMonetaryClassID(String monetaryClassID) {
        this.monetaryClassID = monetaryClassID;
    }

    public String getMonetaryCalculationSystemID() {
        return monetaryCalculationSystemID;
    }

    public void setMonetaryCalculationSystemID(String monetaryCalculationSystemID) {
        this.monetaryCalculationSystemID = monetaryCalculationSystemID;
    }

    public String getMonetaryCalculationID() {
        return monetaryCalculationID;
    }

    public void setMonetaryCalculationID(String monetaryCalculationID) {
        this.monetaryCalculationID = monetaryCalculationID;
    }

    public String getBookingAmountDescCost() {
        return bookingAmountDescCost;
    }

    public void setBookingAmountDescCost(String bookingAmountDescCost) {
        this.bookingAmountDescCost = bookingAmountDescCost;
    }

    public BigDecimal getTransactionAmtCost() {
        return transactionAmtCost;
    }

    public void setTransactionAmtCost(BigDecimal transactionAmtCost) {
        this.transactionAmtCost = transactionAmtCost;
    }

    public BigDecimal getTransactionAmtPrice() {
        return transactionAmtPrice;
    }

    public void setTransactionAmtPrice(BigDecimal transactionAmtPrice) {
        this.transactionAmtPrice = transactionAmtPrice;
    }

    public String getBookingAmountDescPrice() {
        return bookingAmountDescPrice;
    }

    public void setBookingAmountDescPrice(String bookingAmountDescPrice) {
        this.bookingAmountDescPrice = bookingAmountDescPrice;
    }

    public String getBookingAmountRowGUID() {
        return bookingAmountRowGUID;
    }

    public void setBookingAmountRowGUID(String bookingAmountRowGUID) {
        this.bookingAmountRowGUID = bookingAmountRowGUID;
    }
}
