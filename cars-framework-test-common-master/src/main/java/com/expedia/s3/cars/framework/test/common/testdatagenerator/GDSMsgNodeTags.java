package com.expedia.s3.cars.framework.test.common.testdatagenerator;

/**
 * Created by miawang on 11/29/2016.
 */
public class GDSMsgNodeTags {
    public static final String REQUEST = "Request";
    public static final String RESPONSE = "Response";
    //---------------------------------------titanium GDS Msg Response Request type -----------------------------------------
    public class TitaniumNodeTags {
        //request & response type tags:
        public static final String TVAR_RESPONSE_TYPE = "OTA_VehAvailRateRS";
        public static final String TVRR_REQUEST_TYPE = "OTA_VehRateRuleRQ";
        public static final String TVRR_RESPONSE_TYPE = "OTA_VehRateRuleRS";

        public static final String TVBR_RESPONSE_TYPE = "OTA_VehResRS";

        //Nodes
        public static final String VEHAVAIL = "VehAvail";
        public static final String VEHMAKEMODEL = "VehMakeModel";

        public static final String REFERENCE = "Reference";
        public static final String ID = "ID";
        public static final String CODE = "Code";

        public static final String RATE_REFERENCE = "RateReference";
    }

    //---------------------------------------amadeus GDS Msg Response Request type -----------------------------------------
    public class AmadeusNodeTags {
        public static final String ACAQ_RESPONSE_TYPE = "Car_AvailabilityReply";

        //request & response type tags:
        public static final String ARIA_CAR_GET_DETAIL_REQUEST_TYPE = "Car_RateInformationFromAvailability";
        public static final String ARIA_CAR_GET_DETAIL_RESPONSE_TYPE ="Car_RateInformationFromAvailabilityReply";
        //    public const String CAR_SELL = "ACSQ";
        public static final String ACSQ_CAR_SELL_REQUEST_TYPE = "Car_Sell";

        public static final String APRQ_PNR_REPLY_TYPE = "PNR_Reply";

        public static final String ACSQ_CAR_SELL_RESPONSE_TYPE = "Car_SellReply";

        public static final String APAM_PNR_AME1_RESPONSE_TYPE = "PNR_Reply";

        //    public const String PNR_AME2 = "APCM";
        public static final String APCM_PNR_AME2_RESPONSE_TYPE = "PNR_Reply";

        //    public const String RIFCS = "ARIS";
        public static final String ARIS_RIFCS_RESPONSE_TYPE = "Car_RateInformationFromCarSegmentReply";

        //APRQ
        public static final String APRQ_PNR_REQUEST_TYPE = "PNR_Retrieve";
        public static final String APRQ_PNR_RESPONSE_TYPE = "PNR_Reply";

        //Nodes
        //ACLQ
        public static final String ACLQ_LOCATION_LIST_REQUEST_TYPE = "Car_LocationList";
        public static final String ACLQ_LOCATION_LIST_RESPONSE_TYPE = "Car_LocationListReply";

        //ADBI
        public static final String ADBI_PAY_MANAGEDBIDATA_REQUEST = "PAY_ManageDBIData";
        public static final String ADBI_PAY_MANAGEDBIDATA_RESPONSE = "PAY_ManageDBIDataReply";

        //ACCM
        public static final String PNR_ADDMULTIELEMENTS_REQUEST = "PNR_AddMultiElements";

    }



    //---------------------------------------worldspan GDS Msg Response Request type -----------------------------------------
    public class WorldSpanNodeTags {
        //GDS Msg
        public static final String CRS_MESSGE_TYPE = "Transaction";
        //Response Request type
        public static final String VSAR_REQUEST_TYPE = "VehicleSearchAvailabilityReq";
        public static final String VSAR_RESPONSE_TYPE = "VehicleSearchAvailabilityRsp";
        public static final String VRUR_REQUEST_TYPE = "VehicleRulesReq";
        public static final String VRUR_RESPONSE_TYPE = "VehicleRulesRsp";
        public static final String VCRR_REQUEST_TYPE = "VehicleCreateReservationReq";
        public static final String VCRR_RESPONSE_TYPE = "VehicleCreateReservationRsp";
        public static final String URRR_REQUEST_TYPE = "UniversalRecordRetrieveReq";
        public static final String URRR_RESPONSE_TYPE = "UniversalRecordRetrieveRsp";
        public static final String VCRQ_REQUEST_TYPE = "VehicleCancelReq";
        public static final String VCRQ_RESPONSE_TYPE = "VehicleCancelRsp";
    }

}