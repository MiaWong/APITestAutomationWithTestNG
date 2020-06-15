package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/4/2018.
 */
/*
# //-----------------------------------------------------------------------------
# // MonetaryClassId
# //-----------------------------------------------------------------------------

# // SQL_DOMAIN_TYPE_SYNC
# //
# // ID: 302 and 350
# // Type: Monetary Class
# // Desc: High level classifications of financial transaction such as base amount, tax, and adjustment.

# // DOMAIN_VALUE_START
.    MonetaryClassId    kmclInvalid                           0       // Should not be logged
.    MonetaryClassId    kmclBaseAmount                       1       // Raw amount used as the basis for all other
MonetaryClass records.
.    MonetaryClassId    kmclExtraPersonAmount               2       // Obsolete -- use BaseAmount
.    MonetaryClassId    kmclTaxOnCost                    3       // Tax on cost, typically paid by supplier. Charges
imposed by a government entity.
.    MonetaryClassId    kmclOccupancyTax                   4       // Obsolete -- use Tax classes
.    MonetaryClassId    kmclServiceChargeTaxable           5       // Obsolete -- use current classes
.    MonetaryClassId    kmclServiceChargeNontaxable           6       // Obsolete -- use current classes
.    MonetaryClassId    kmclExpediaFee                       7       // Obsolete -- use Fee
.    MonetaryClassId    kmclAdjustment                       8       // Amount added or deducted on the basis of a
qualifying circumstance not covered by Base, Tax, Fee or Commission.
.    MonetaryClassId    kmclCancelChangePenalty               9       // Obsolete -- use Fee
.    MonetaryClassId    kmclDynamicRateRulesFeatured       10       // Obsolete -- use Adjustment
.    MonetaryClassId    kmclDynamicRateRulesHidden           11       // Obsolete -- use Adjustment
.    MonetaryClassId    kmclUniversalCoupon                   12       // Obsolete -- use Adjustment
.    MonetaryClassId    kmclVATTax                           13       // Obsolete -- use Tax classes
.    MonetaryClassId    kmclPeakSurcharge                14       // Obsolete -- use Adjustment
.    MonetaryClassId    kmclVariableCost                 15       // Obsolete -- do not use
.    MonetaryClassId    kmclFixedCost                    16       // Obsolete -- do not use
.    MonetaryClassId    kmclFee                            18       // Amount charged for services rendered, always
originate with amount > 0.
.    MonetaryClassId    kmclCommission                     19       // Amount allowed to a sales rep or an agent for
services rendered.
.    MonetaryClassId    kmclTaxOnMargin                    20       // Tax on margin, typically paid by Expedia.
Charges imposed by a government entity.
.    MonetaryClassId    kmclRevenueShare                21       // air compensation data
.    MonetaryClassId    kmclUTTCredit                    22       // Amount credited from an unused ticket
.    MonetaryClassId    kmclFlatCancel                    23       // Supplier approved cost adjustment amount for
cancellations
.    MonetaryClassId    kmclFlatGoodwill                24       // Supplier approved cost adjustment amount for
goodwill adjustments
# // DOMAIN_VALUE_END
.    MonetaryClassId    kmclLast_R17                        22
.    MonetaryClassId    kmclLast_R25                        24

 */
@SuppressWarnings("PMD")
public enum MonetaryClassId
{
    INVALID(0),
    BASE_AMOUNT(1),
    TAX_ON_COST(3),
    ADJUSTMENT(8),
    FEE(18),
    COMMISSION(19),
    TAX_ON_MARGIN(20),
    REVENUE_SHARE(21),
    UTT_CREDIT(22),
    FLAT_CANCEL(23),
    FLAT_GOODWILL(24);

    private int m_mclId;

    private MonetaryClassId(int mclId)
    {
        m_mclId = mclId;
    }

    public int intValue()
    {
        return m_mclId;
    }

    public byte byteValue()
    {
        return (byte) m_mclId;
    }
}

