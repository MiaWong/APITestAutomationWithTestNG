package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by miawang on 12/20/2016.
 */
public class CommonErrorMsgs {
    public class ErrorMsgHeaders
    {
        public final static String MESSAGE_SEARCH_VERIFICATION_PROMPT = " Search Verification run: ";
        public final static String MESSAGE_GETDETAILS_VERIFICATION_PROMPT = " GetDetails Verification run: ";
        public final static String MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT = " GetCostAndAvailability Verification run: ";
        public final static String MESSAGE_RESERVE_VERIFICATION_PROMPT = " Reserve Verification run: ";
        public final static String MESSAGE_GETRESERVATION_VERIFICATION_PROMPT = " GetReservation Verification run: ";
        public final static String MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT = " GetOrderProcess Verification run: ";
        public final static String MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT = " PreparePurshase Verification run: ";
        public final static String MESSAGE_RETRIEVE_VERIFICATION_PROMPT = "Retrieve Verification run: ";
        public final static String MESSAGE_CANCEL_VERIFICATION_PROMPT = " Cancel verification run: ";

        public final static String MESSAGE_GDS = " <<GDS Message>> ";
        public final static String MESSAGE_REQUEST = " <<Request>> ";
        public final static String MESSAGE_RESPONSE = " <<Response>> ";
    }

    public static final String MESSAGE_ERROR_COLLECTION_IN_RESPONSE = "ErrorCollection is present in response.";
    public static final String MESSAGE_NO_MATCHED_CAR = "Car Type is not the same! Please check the request and response";

    public static final String MESSGAGE_NO_CAR_CATALOG_KEY_IN_REQUEST = "No car catalog inventory key in request";
    public static final String MESSGAGE_NO_CAR_CATALOG_KEY_IN_RESPONSE = "No car catalog inventory key in response";
}
