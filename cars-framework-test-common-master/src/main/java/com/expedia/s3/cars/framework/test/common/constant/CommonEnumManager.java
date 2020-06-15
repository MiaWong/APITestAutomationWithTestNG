package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by v-mechen on 11/30/2016.
 */
public class CommonEnumManager {
    //------------------------------------------------parameters use to build request use days----------------------
    // define for daily,3days, ,weekly(5,6,7),weekly+extradays, mounthly ,mounthyly+extradyas , extraHours
    public enum TimeDuration {
        Random(0),
        Daily(1),
        Days2(2),
        Days3(3),
        Days4(4),

        //weekly
        WeeklyDays5(5),
        WeeklyDays6(6),
        WeeklyDays7(7),
        WeeklyExtDays9(9),
        WeeklyExtDays(10),
        WeeklyExtDays12(12),
        WeeklyExtDays15(15),

        Mounthly(30),
        MounthlyExtDays(32),
        MounthlyExtDays35(35),
        MounthlyExtDays37(37),
        Weekend2day(52),
        Weekend3day(53),
        Weekend1day(51),

        Days3extraHours(54),
        Weekend3dayextraHours(55);

        private int timeDuration;

        TimeDuration(int timeDuration) {
            this.timeDuration = timeDuration;
        }

        public int getTimeDuration() {
            return timeDuration;
        }
    }

    //LegacyMonetaryCalculationID for BaseCost
    public enum BaseCostLegacyMonetaryCalculationID {
        Hourly(7),
        Daily(7),
        Weekly(8),
        Weekend(9),
        WeekendDay(9),
        Monthly(13),
        ExtraDaily(7),
        ExtraHourly(7),
        ExtraWeekly(8),
        Trip(14);

        private int baseCostLegacyMonetaryCalculationID;

        BaseCostLegacyMonetaryCalculationID(int baseCostLegacyMonetaryCalculationID) {
            this.baseCostLegacyMonetaryCalculationID = baseCostLegacyMonetaryCalculationID;
        }

        public int getBaseCostLegacyMonetaryCalculationID() {
            return baseCostLegacyMonetaryCalculationID;
        }

    }

    public  enum DataLogType
    {
        RefPricing,
        PerfMetrics,
        VRDError,
        ErrorAnalysis,
        BusinessIntelligence
    }

    public enum ErrorHandlingType
    {
        None,
        InvalidLocationOneWay,
        InvalidPickupLocation,
        InvalidCarRateQualifierCode,
        InvalidCarRates,
        InvalidCarRateCode,
        InvalidPNR,
        CanceledPNR,
        WithoutPNR,
        InprogressPNR,
        SECUREDPNR,
        WithoutPNRNode,
        InvalidCompanyCode,
        InvalidCorporateDiscountCode,
        InvalidSpecialEquipmentCode,  // in CarVehicleOptionList
        InvalidSpecialEquipmentCodeInCarSpecialEquipmentList,  // in CarSpecialEquipmentList
        FieldInvalidError,
        CarProductNotAvailableError,
        CarLoyaltyNumberNotAppliedError,
        RateCodeNotAppliedError,
        CarTypeNotAvailableError,
        SpecialEquipmentNotAvailableError,
        InvalidPOS,
        NoCarProductCataLogID,
        NotMatchedCarProductCataLogID,
        InvalidPOSAndNoCarProductCataLogID,
        PNRUnavailable,
        InvalidDeliveryCityName,
        InvalidCollectionCityName,
        NoMatchingTraveler,
    }

    public  enum BookStatusCode
    {
        BOOKED("Booked"),
        PENDING("Pending"),
        CONFIRM("Confirm"),
        RESERVED("Reserved"),
        CANCELLED("Cancelled"),
        UNKNOWN("Unknown");

        private final String status;

        private BookStatusCode(String statusCode)
        {
            this.status = statusCode;
        }

        public String getStatusCode()
        {
            return this.status;
        }
    }

    //------------------------------------------Cost & Price-------------------------------------
    public enum FinanceApplicationCode{
        Daily("Daily"),
        Weekly("Weekly"),
        Weekend("Weekend"),
        Monthly("Monthly"),
        ExtraHourly("ExtraHourly"),
        ExtraDaily("ExtraDaily"),
        Total("Total"),
        Trip("Trip");

        private String financeApplicationCode;

        FinanceApplicationCode(String financeApplicationCode) {
            this.financeApplicationCode = financeApplicationCode;
        }

        public String getFinanceApplicationCode() {
            return financeApplicationCode;
        }
    }

    public enum FinanceCategoryCode{
        Base("Base"),
        Total("Total"),
        Misc("Misc"),
        MiscBase("MiscBase"),
        Extra("Extra"),
        Taxes("Taxes"),
        Surcharge("Surcharge"),
        Prepayment("Prepayment"),
        Fee("Fee"),
        Coverage("Coverage"),
        SpecialEquipment("SpecialEquipment"),
        ExpediaCancelFee("ExpediaCancelFee"),
        Commission("Commission"),
        ProviderTransactionFees("ProviderTransactionFees"),
        MaxMarginAmt("MaxMarginAmt"),
        MinMarginAmt("MinMarginAmt"),
        TotalDiscount("TotalDiscount"),
        MandatoryCharge("MandatoryCharge"),
        EstimatedTotalTaxesAndFees("EstimatedTotalTaxesAndFees");

        private String financeCategoryCode;

        FinanceCategoryCode(String financeCategoryCode) {
            this.financeCategoryCode = financeCategoryCode;
        }

        public String getFinanceCategoryCode() {
            return financeCategoryCode;
        }
    }

    public enum CostDescriptionRawText{
        ExtraDayCharge("extra day charge"),
        ExtraHourCharge("extra hour charge"),
        BaseRateTotal("Base Rate Total"),
        Base("Base"),
        EstimatedTotalAmount("Estimated total amount"),
        OneWayCharge("ONE WAY CHARGE");

        private String descriptionRawText;

        CostDescriptionRawText(String descriptionRawText) {
            this.descriptionRawText = descriptionRawText;
        }

        public String getDescriptionRawText() {
            return descriptionRawText;
        }
    }

    public enum DistanceUnit{
        MI("MI"),
        KM("KM"),
        UNL("UNL");

        private String distanceUnit;

        DistanceUnit(String distanceUnit) {
            this.distanceUnit = distanceUnit;
        }

        public String getDistanceUnit() {
            return distanceUnit;
        }
    }

    public enum BusinessModel
    {
        Agency(1),
        Merchant(2),
        GDSP(3);

        private int businessModel;

        BusinessModel(int businessModel) {
            this.businessModel = businessModel;
        }

        public int getBusinessModel() {
            return businessModel;
        }
    }


    public enum ServieProvider
    {
        worldSpanSCS(1),
        expediaSCS(2),
        MNSCS(3),
        Amadeus(6),
        TitaniumSCS(7);


        private int serviceProvider;

        ServieProvider(int serviceProvider) {
            this.serviceProvider = serviceProvider;
        }

        public int getServiceProvider() {
            return serviceProvider;
        }
    }

    public enum ReferenceCategoryCode
    {
        TRL,
        Voucher,
        PNR,
        BookingID,
        BookingItemID,
        ItineraryBookingDirectoryID,
        Segment,
        Vendor,
        BillingNumber,
    }

    public enum DomainType  // for ExternalSupplyServiceDomainValueMap DB
    {
        CarSpecialEquipment("146");
        //CarSpecialEquipment
        private String domainType;

        DomainType(String domainType) {
            this.domainType = domainType;
        }

        public String getDomainType() {
            return domainType;
        }
    }

    public  enum VendorCDCodeType
    {
        MulCDSingleVendor,
        MulVendorHaveSingleCD,
        VendorsWithAndWithoutCD,
        SingleCD,
        NULL
    }

    public enum VerifyType
    {
        CarInventoryKey,
        CarPickupDropOff,
        CostAndCalculate,
        TravelerList,
        Customer,
        BookingStateCode,
        ReferenceList,
        CCCard,
        DCLocation,
        SpecialEquipment,
        DirectBilling,
        CarReservationRemark,
        LoyaltyNumber,
        FrequentFlyerNumber,
        AnalyticalCode,
        EVoucherFail
    }
}
