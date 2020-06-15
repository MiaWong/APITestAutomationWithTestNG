package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-mechen on 9/4/2018.
 */
/*
# //-----------------------------------------------------------------------------
# // MonetaryCalculationSystemId
# //-----------------------------------------------------------------------------

.    MonetaryCalculationSystemId    kmcaInvalid                    0
.    MonetaryCalculationSystemId    kmcaCommonEvents            1       // This referred to as Generic by the
reporting world
.    MonetaryCalculationSystemId    kmcaLodgInv_OccupancyTax        2
.    MonetaryCalculationSystemId    kmcaLodgInv_TaxRate            3
.    MonetaryCalculationSystemId    kmcaLodgInv_ServiceChargeTaxable    4
.    MonetaryCalculationSystemId    kmcaLodgInv_ExtraPerson        5
.    MonetaryCalculationSystemId    kmcaLodgInv_Adjustment        6
.    MonetaryCalculationSystemId    kmcaLodgInv_CancelPenalty        7
.    MonetaryCalculationSystemId    kmcaLodgInv_SKUCost            8
.    MonetaryCalculationSystemId    kmcaLodgInv_TPIDVariance        9
.    MonetaryCalculationSystemId    kmcaTShop_TicketTypeAmt        10
.    MonetaryCalculationSystemId    kmcaLodgInv_DynamicRateRules    11
.    MonetaryCalculationSystemId    kmcaUniversalCoupon            12
.    MonetaryCalculationSystemId    kmcaMC_CarVendorMarket        13
.    MonetaryCalculationSystemId    kmcaMC_CarPeakPeriod         14
.    MonetaryCalculationSystemId    kmcaMC_CarCancelPenaltyRule    15
.    MonetaryCalculationSystemId    kmcaMC_CarTaxRate            16
.    MonetaryCalculationSystemId    kmcaInsuranceAmount            17
.    MonetaryCalculationSystemId    kmcaTransactionFee            18
.    MonetaryCalculationSystemId    kmcaGoodwill                    19
.    MonetaryCalculationSystemId    kmcaTravelShopsInventoryOfferingTaxLog    20
.    MonetaryCalculationSystemId    kmcaTravelshopsInventorySKUCostLog    21
.    MonetaryCalculationSystemId    kmcaCarAgencyItemPriceLog            22
.    MonetaryCalculationSystemId    kmcaCarAgencyCommissionLog    23
.    MonetaryCalculationSystemId    kmcaAirFareType                24
.    MonetaryCalculationSystemId    kmcaCruiseInv_SailingInventoryPriceLog    25
.    MonetaryCalculationSystemId    kmcaCruiseCatalog_CruiseLineSKULog        26
.    MonetaryCalculationSystemId    kmcaCruiseInv_ProtectionPlanPriceLog        27
.    MonetaryCalculationSystemId    kmcaCruiseInv_ProtectionPlanCommissionLog    28
.    MonetaryCalculationSystemId    kmcaCruiseInv_CruiseLineCommissionLog    29
.    MonetaryCalculationSystemId    kmcaConfiguration_FeeTypeAmount        30
.    MonetaryCalculationSystemId    kmcaLodgPkgTcmAction            31
.    MonetaryCalculationSystemId    kmcaAirCommission            32
.    MonetaryCalculationSystemId    kmcaAirGdsPayment            33
.    MonetaryCalculationSystemId    kmcaAirGdsRebate                34
.    MonetaryCalculationSystemId    kmcaAirBaseMarkup            35
.    MonetaryCalculationSystemId    kmcaLodgInv_ServiceChargeNonTaxable    36
.    MonetaryCalculationSystemId    kmcaLodgInv_VatTax            37
.    MonetaryCalculationSystemId    kmcaCruiseChargeAdjustmentReason    38
.    MonetaryCalculationSystemId    kmcaCruiseCommissionAdjustmentReason 39
.    MonetaryCalculationSystemId    kmcaCruiseAgencyFeeAdjustmentReason 40
.    MonetaryCalculationSystemId    kmcaExternalFinancialTransaction 41
.    MonetaryCalculationSystemId    kmcaCruiseInv_CruiseInventoryContractCruiseAmenityLog 42
.    MonetaryCalculationSystemId    kmcaCruiseInv_CruiseInventoryContractLog              43
.    MonetaryCalculationSystemId    kmcaCanadianVATTaxes            44
.    MonetaryCalculationSystemId    kmcaTravelshopsInventoryWaiverRateLog              45
.    MonetaryCalculationSystemId    kmcaSupplierApprovedCostAdjustment    46
.    MonetaryCalculationSystemId    kmcaLodgingSingleSupplement    47
.    MonetaryCalculationSystemId    kmcaLodgingVariableMargin        48
.    MonetaryCalculationSystemId    kmcaRedeemedLoyaltyIncentive        49
.    MonetaryCalculationSystemId    kmcaEmployeeDiscount            50
.    MonetaryCalculationSystemId    kmcaTravelShopsInventorySKUCostVariablePriceRemittanceLog            51
.    MonetaryCalculationSystemId    kmcaLodgingPropertyRules_ExpediaServiceFeeLog        52
# // IMPORTANT - when adding a new SystemId, please be sure to add a new
# //   row in the MonetaryCalculationSystem table in Configuration

// The following constant is obsolete. It can be deleted after FS12 is merged back into main
// Please do not update
.    MonetaryCalculationSystemId    kmcaLast_R17                    41
.    MonetaryCalculationSystemId    kmcaLast_R22                    42


 */

/* from car carsp\constants.h
enum MonetaryCalculationSystemIds
{
    MONETARY_TYPE_GENERIC = 1,
    MERCHANT_BASE_RATE = 13,
    MERCHANT_PEAK_RATE = 14,
    MERCHANT_CANCEL_PENALTY = 15,
    MERCHANT_TAX_RATE = 16
};
 */
@SuppressWarnings("PMD")
public enum MonetaryCalculationSystemId
{
    INVALID(0),
    MONETARY_TYPE_GENERIC(1),
    MONETARY_TYPE_TAX_HST(98),
    MONETARY_TYPE_TAX_PST(99),
    MONETARY_TYPE_TAX_GST(100),
    MONETARY_TYPE_TAX_QST(101),
    MERCHANT_BASE_RATE(13),
    MERCHANT_PEAK_RATE(14),
    MERCHANT_CANCEL_PENALTY(15),
    MERCHANT_TAX_RATE(16),
    CAR_MERCHANT_COMMISSION_LOG(102),
    CAR_AGENCY_ITEM_PRICE_LOG(22),
    CAR_AGENCY_COMMISSION_LOG(23),
    MICRONNEXUS_TRANSACTION_FEE(92),
    PROVIDER_SPECIFIED_COMMISSION(93);


    private static final Map<Integer, MonetaryCalculationSystemId> S_LOOKUP
            = new HashMap<Integer, MonetaryCalculationSystemId>();
    private int m_amld;

    private MonetaryCalculationSystemId(int amlId)
    {
        m_amld = amlId;
    }

    static
    {
        for (MonetaryCalculationSystemId s : EnumSet.allOf(MonetaryCalculationSystemId.class))
        {
            S_LOOKUP.put(Integer.valueOf(s.intValue()), s);
        }
    }

    public static MonetaryCalculationSystemId asEnum(int calcId)
    {
        MonetaryCalculationSystemId t = S_LOOKUP.get(Integer.valueOf(calcId));
        if (t != null)
        {
            return t;
        }

        return INVALID;
    }

    public int intValue()
    {
        return m_amld;
    }

    public byte byteValue()
    {
        return (byte) m_amld;
    }

    public short shortValue()
    {
        return (short) m_amld;
    }
}

