package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by v-mechen on 11/30/2016.
 */
public class CommonConstantManager {
    public class DomainType{
        public static final String CAR_VENDOR_EXTENDED = "CarVendorExtended";
        public static final String CAR_VENDOR = "CarVendor";
        public static final String CAR_VENDOR_LOCATTION = "CarVendorLocation";
        public static final String CAR_SPECIAL_EQUIPMENT = "CarSpecialEquipment";

        //SIPP
        public static final String CAR_TYPE = "CarType";
        public static final String CAR_TRANSMISSIOND_DRIVE = "CarTransmissionDrive";
        public static final String CAR_FUEL_AIR_CONDITION = "CarFuelAirCondition";
        public static final String CAR_CATEGORY = "CarCategory";

        public static final String RATE_PERIOD = "RatePeriod";

        public static final String DISTANCE_UNIT = "DistanceUnit";

        public static final String BOOKING_ITEM_STATE = "Booking Item State";
        public static final String LOCATION_TYPE = "LocationType";
        public static final String AVAIL_STATUS = "AvailStatus";
        public static final String CAR_SHUTTLE_CATEGORY = "CarShuttleCategory";
        public static final String CAR_POLICY_CATEGORY = "CarPolicyCategory";
        public static final String CAR_MILEAGE_RATE_PERIOD= "MileageRatePeriod";


        public static final String CREDIT_CARD_TYPE = "CreditCardType";
        public static final String RATE_CATEGORY = "RateCategory";
    }

    public class ReservationGuaranteeCategory
    {
        public static final String REQUIRED = "Required";
        public static final String OPTIONAL = "Optional";
        public static final String NONE = "None";
    }

    public class SupplierConfigurationSettingName
    {
        public static final String COSTANDAVAIL_SUPPRESS_DOWNSTREAM_RATECODE_PROPAGATION_ENABLE = "CostAndAvail.suppressDownstreamRateCodePropagation/enable";
        public static final String RESERVE_DELIVERY_COLLECTION_BILLINGGUARANTEE_ENABLE = "Reserve.deliveryCollectionBillingGuarantee/enable";
        public static final String PROPAGATE_CDCODE_UPSTREAM_ENABLE = "propagateCDCodeUpstream/enable";
    }

    public class PickupOrDropoffType
    {
        public static final String PICKUP_TYPE = "176";
        public static final String DROPOFF_TYPE = "DOL";
    }

    public class EnableKey
    {
        public static final String ENABLE_ON = "1";
        public static final String ENABLE_OFF = "0";
        public static final String ENABLE_NULL = "null";
    }

    public class ActionType
    {
        public static final String SEARCH = "Search";
        public static final String GETDETAILS = "GetDetails";
        public static final String GETCOSTANDAVAILABILITY = "GetCostAndAvailability";
        public static final String RESERVE = "Reserve";
        public static final String GETRESERVATION = "GetReservation";
        public static final String CANCEL = "Cancel";
    }

    public class TitaniumGDSMessageName
    {
        public static final String COSTAVAILREQUEST = "OTA_VehAvailRateRQ";
        public static final String COSTAVAILRESPONSE = "OTA_VehAvailRateRS";
        public static final String DETAILSREQUEST = "OTA_VehRateRuleRQ";
        public static final String DETAILSRESPONSE = "OTA_VehRateRuleRS";
        public static final String RESERVEREQUEST = "OTA_VehResRQ";
        public static final String RESERVERESPONSE = "OTA_VehResRS";
        public static final String GETRESERVATIONREQUEST = "OTA_VehRetResRQ";
        public static final String GETRESERVATIONRESPONSE = "OTA_VehRetResRS";
        public static final String CANCELREQUEST = "OTA_VehCancelRQ";
        public static final String CANCELRESPONSE = "OTA_VehCancelRS";
    }

    public class MNGDSMessageName
    {
        public static final String COSTAVAILREQUEST = "VehAvailRateRQ";
        public static final String COSTAVAILRESPONSE = "VehAvailRateRS";
        public static final String DETAILSREQUEST = "VehRateRuleRQ";
        public static final String DETAILSRESPONSE = "VehRateRuleRS";
        public static final String RESERVEREQUEST = "VehResRQ";
        public static final String RESERVERESPONSE = "VehResRS";
        public static final String GETRESERVATIONREQUEST = "VehRetResRQ";
        public static final String GETRESERVATIONRESPONSE = "VehRetResRS";
        public static final String CANCELREQUEST = "VehCancelResRQ";
        public static final String CANCELRESPONSE = "VehCancelResRS";
    }

    public class SPErrorCode
    {
        public static final String DUPLICATEDRECORD = "duplicate";
    }

    public class RateCategory
    {
        public static final String PREPAY = "Prepay";
    }
}
