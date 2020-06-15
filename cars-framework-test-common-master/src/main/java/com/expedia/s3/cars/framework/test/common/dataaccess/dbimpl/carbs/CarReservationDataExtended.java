package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs;

/**
 * Created by fehu on 11/22/2018.
 */
@SuppressWarnings("PMD")
public class CarReservationDataExtended {
    public String pJurisdictionCode;
    public String pCompanyCode;
    public String pManagementUnitCode;
    public String pBookingItemID;
    public String pSeqNbr;
    public String pMonetaryClassID;
    public String pMonetaryCalculationSystemID;
    public String pMonetaryCalculationID;
    public String pCurrencyCodeCost;
    public String pTransactionAmtCost;
    public String pDescCost;
    public String pFinanceCategoryCodeCost;
    public String pFinanceApplicationCodeCost;
    public String pCurrencyCodePrice;
    public String pTransactionAmtPrice;
    public String pDescPrice;
    public String pFinanceCategoryCodePrice;
    public String pFinanceApplicationCodePrice;
    public String pCreateDate;
    public String pFinanceApplicationUnitCntCost;
    public String pFinanceApplicationUnitCntPrice;
    public String pMultiplierPctCost;
    public String pMultiplierPctPrice;

    public String getpJurisdictionCode()
    {
        return pJurisdictionCode;
    }

    public void setpJurisdictionCode(String pJurisdictionCode)
    {
        this.pJurisdictionCode = pJurisdictionCode;
    }
}
