package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by fehu on 10/25/2016.
 */
public class PosConfigSettingName {

    //-------------------Shopping
    public static final String SHOPPING_PROPAGATELOYALTYINFO_ENABLE_CARBS="Shopping.propagateLoyaltyInfo/enable";
    public static final String SHOPPING_PROPAGATELOYALTYINFO_ENABLE_WSCS="Shopping.propagateLoyaltyInfo/enable";
    public static final String SHOPPING_PREPAIDAGENCYCARS_ENABLE = "Shopping.prepaidAgencyCars/enable";

    //Cars Promotions configurable
    public static final String SHOPPING_CARVENDORPROMOTIONS_ENABLE = "Shopping.carVendorPromotions/enable";
    public static final String SHOPPING_CARINCLUSIONDETAILS_ENABLE = "Shopping.carInclusionDetails/enable";
    public static final String SHOPPING_CAREXCLUSIONDETAILS_ENABLE = "Shopping.carExclusionDetails/enable";
    public static final String SHOPPING_CARMISCELLANEOUSINFO_ENABLE = "Shopping.carMiscellaneousInfo/enable";


    public static final String BOOKING_PREPAIDAGENCY_SETREVENUEREPORTINGTYPEID_ENABLE= "Booking.prepaidAgency.setRevenueReportingTypeId/enable";

    //Search:
    public static final String SEARCH_REQUESTEXTENDEDSIPPCARCATEGORIES_ENABLE = "Search.requestExtendedSIPPCarCategories/enable";
    public static final String SEARCH_FIXUNCLASSIFIEDERRORMAPPING_ENABLE = "Search.fixUnclassifiedErrorMapping/enable";
    public static final String SEARCH_FILTERINVALIDLOCATIONCODE_ENABLE = "Search.FilterInvalidLocationCode/enable";
    public static final String SEARCH_FILTER_COMPARE_INVENTORYKEY_ENABLE = "Search.filter.compareInventoryKeys/enable";


    //Cost And Avail
    public static final String COSTANDAVAIL_FIXCARPRODUCTNOTAVAILABLEMAPPING_ENABLE = "CostAndAvail.fixCarProductNotAvailableMapping/enable";

    //Get Detail:
    public static final String DETAILS_REFERENCEPRICING_ENABLE = "GetDetails.referencePricing/enable";
    public static final String GETDETAILSDROPCHARGE_SETTINGNAME_POS = "GetDetails.useGetDetailsDropCharge/enable";
    public static final String GETDETAILSDROPCHARGE_SETTINGNAME_CLIENT = "GetDetails.useGetDetailsDropCharge/enable";

    //-----------------Booking
    public static final String BOOKING_LOG_PREPAID_AGENCY_PUBLISHED_PRICE_AMT = "Booking.prepaidAgency.setPublishedPriceAmt/enable";
    public static final String BOOKING_LOG_BOOKING_TYPE_IDENTIFIER = "Booking.setBookingTypeIdentifier/enable";
    public static final String BOOKING_SETADDITIONALBOOKINGATTRIBUTES_ENABLE= "Booking.setAdditionalBookingAttributes/enable";
    public static final String BOOKING_ENHANCEDBOOKINGLOGGING_ENABLE = "Booking.enhancedBookingLogging/enable";
    public static final String BOOKING_MARKUPLOGGING_ENABLE = "Booking.markupLogging/enable";
    public static final String BOOKING_PROPAGATEBILLINGNUMBER_ENABLE = "Booking.propagateBillingNumber/enable";
    public static final String BOOKING_REFERENCEPRICELOG_OVERRIDEPUBLISHEDPRICE = "Booking.referencePriceLog.overridePublishedPrice/enable";
    public static final String BOOKING_SETBOOKINGRECORDSYSTEMID_ENABLE = "Booking.setBookingRecordSystemID/enable";
    public static final String BOOKING_SETBOOKINGRECORDSYSTEMID_TITANIUM_ENABLE = "Booking.setBookingRecordSystemID.titanium/enable";

    public static final String BOOKING_MERCHANTCOMMISSIONMARKUP_SETREVENUEREPORTINGTYPEID_ENABLE = "Booking.merchantCommissionMarkup.setRevenueReportingTypeId/enable";

    public static final String BOOKING_FALLBACKCATALOGRETRY= "Booking.fallbackCatalogRetry/enable";
    public static final String BOOKING_FALLBACKCATALOGRETRY_VALUE = "Booking.fallbackCatalogRetry/1.value";
    public static final String BOOKING_FALLBACKCATALOGRETRY_MAXELAPSEDTIMEBEFOREFALLBACK = "Booking.fallbackCatalogRetry/maxElapsedTimeBeforeFallback";
    public static final String BOOKING_FALLBACKCATALOGRETRY_SUPPRESSSPECIALEQUIPMENTREQUEST= "Booking.fallbackCatalogRetry.suppressSpecialEquipmentRequest/enable";

    public static final String GETORDERPROCESS_MAKEDETAILSCALL_ENABLE = "GetOrderProcess.makeDetailsCall/enable";
    public static final String GETORDERPROCES_CUSTOMERPAYMENTINSTRUMENTREQUIREDBYSUPPLIER = "GetOrderProcess.customerPaymentInstrumentRequiredBySupplier/enable";

    //-------------------shopping & booking
    public static final String BOOKINGSHOPPING_SUPPRESSLOYALTYINFO_NONAGENCY_ENABLE="BookingShopping.suppressLoyaltyInfo.nonAgency/enable";


    //-------------------error:
    public static final String ERROR_XPATHMAPPING_ENABLE = "Error.xpathMapping/enable";
    public static final String ERRORANALYSIS_LOGGING_ENABLE = "ErrorAnalysis.Logging/enable";



    public static final String RECONSTRUCT_SUPPRESSCOSTANDPRICELIST_ENABLE = "Reconstruct.suppressCostAndPriceList/enable";
    public static final String RECONSTRUCT_SUPPRESSDOWNSTREAMRESPONSEERRORS_ENABLE = "Reconstruct.suppressDownstreamResponseErrors/enable";
    public static final String POPULATECARVENDORLOCATIONINFO_USECVLTABLEASMASTERDATA_ENABLE = "PopulateCarVendorLocationInfo.useCVLTableAsMasterData/enable";
    public static final String POPULATEEMPTYDROPOFFLOCATION_ENABLE = "PopulateEmptyDropOffLocation/enable";

    public static final String PRICECHANGE_TOTALPRICETOLERANCE = "PriceChange.totalPriceTolerance";
    public static final String DATALOG_LOGCARRATEINFO_ENABLE = "Datalog.logCarRateInfo/enable";

    public static final String AIRPORTTIMEZONETRANSPORTURL = "airportTimeZoneTransport.url";

    public static final String PRODUCTTOKEN_ENABLE = "ProductToken/enable";

    //worldspan SCS
    public static final String GETCOSTANDAVAILABILITYUAPI_ENABLE = "GetCostAndAvailability.uAPI/enable";

    public static final String GETDETAILS_DOCOSTANDAVAILREQUEST_ENABLE = "GetDetails.doCostAndAvailRequest/enable";

    public static final String RESERVE_SUPPRESSSPECIALEQUIPMENT= "Reserve.suppressSpecialEquipment/enable";
    public static final String RESERVE_SPECIALEQUIPMENTWHITELIST_ENABLE = "Reserve.specialEquipmentWhitelist/enable";
    public static final String RESERVE_SPECIALEQUIPMENTWHITELIST = "Reserve.specialEquipmentWhitelist/list";

    //TSCS
    public static final String ENABLEDYNAMICCONTENTFROMGDS_ENABLE = "enableDynamicContentFromGDS/enable";
}
