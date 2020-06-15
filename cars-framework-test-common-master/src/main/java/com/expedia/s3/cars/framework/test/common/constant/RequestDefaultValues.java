package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by miawang on 12/9/2016.
 */
public class RequestDefaultValues {
    public static final String INVALID_CORPORATE_DISCOUNT_CODE="aaaaaaaaaaaaaaaaaa";
    public static final String INVALID_CARRATE_QUALIFIERCODE="1234567";

    //----------------------DateTime------------------
    public static final int SEARCH_START_INTERVAL_DAYS = 150;
    public static final int SEARCH_START_INVERVAL_DAYS_MERCHANT = 90;
    public static final int SEARCH_USE_DAYS = 3;

    //---------------------CDCodes------------------
    public static final String WITH_WITHOUT_CD_CODES = "EP-51354174,EP-null,SX-5020105,SX-null,ZI-N865556,ZI-null,ZE-null,ZE-3456789";
    public static final String ONE_CD_CODES = "EP-51354174";
    public static final String MULTIPLE_CD_CODE = "EP-51354174,EP-40823215,SX-5020105,ZI-N865556,ZE-620397,ZE-703771,ZE-709087";

    //----------------------Promocode---------------
    public static final String PROMOCODE_DAILY_ZE ="196114";


    //----------------------LoyaltyNumber---------------
    public static final String LOYALTYNUMBER_ALAMO ="51442848";

    //----------------------vendorCode---------------
    public static final String VENDOR_CODE_AL ="AL";
    public static final String VENDOR_CODE_EP ="EP";
    public static final String VENDOR_CODE_AC ="AC";
    public static final String VENDOR_CODE_ZT ="ZT";
    public static final String VENDOR_CODE_ZI ="ZI";

    //The default value of CreditCard for reserve
    public static final String CREDIT_CARD_SUPPLIER_CODE = "MasterCard";
    public static final String CREDIT_CARD_NUMBER_ENCRYPTED = "AQAQAAEAEAAxMTAxM/e68CKjDVM76j5KUY3VlOVaVs4T5TzQKNkiS4eOuTuu";
    public static final String AIRPLUS_CARD_SUPPLIER_CODE = "Airplus";
    public static final String AIRPLUS_CARD_NUMBER_ENCRYPTED = "AQAQAAEAEAAxMTAxMxJ76+SD6maTX9lMyxA/8vSK6QHWGU/+C86HUtst9k46";
    public static final String EXPIRATION_DATE = "2017-12-28T23:59:00";
    public static final String CARD_PRESENT_BOOLEAN = "true";
    public static final String MASKED_CREDIT_CARD_NUMBER = "5390";
    public static final String ACTUAL_CREDIT_CARD = "5105781454975390";
    public static final String RESERVATION_GUARANTEE_CATEGORY = "Required";
    public static final String NEED_CCCARD = "true";
    public static final String CREDIT_CARD_SECURITY_CODE = "";

    //The default value of Customer for reserve
    public static final String FIRST_NAME = "CARBSRESERVE";
    public static final String LAST_NAME = "STTWO";
    public static final String AGE_CODE = "Adult";

    //The default value of ContactInformation for customer and traveler
    public static final String CONTACTOR_CATEGORY_CODE = "Home";
    public static final String CONTACT_PHONE_COUNTRY_CODE = "1";
    public static final String CONTACT_PHONE_AREA_CODE = "425";
    public static final String CONTACT_PHONE_NUMBER = "555-5555";
    public static final String CONTACT_FIRST_ADDRESSLINE = "82, Boulevard de Clichy";
    public static final String CONTACT_SECOND_ADDRESSLINE = "NewYork 98052, American";
    public static final String CONTACT_CITY_NAME = "NewYork";
    public static final String CONTACT_POSTAL_CODE = "98052";
    public static final String CONTACT_COUNTRY_ALPHA3CODE = "USA";
    public static final String CONTACT_EMAIL_ADDRESS = "testcarssttwo@expedia.com";

    //special equipment
    public static final String CAR_SPECIAL_EQUIPMENT_CODE_CSI = "InfantChildSeat";

    public static final String BILLING_NUMBER = "871131370003";


}