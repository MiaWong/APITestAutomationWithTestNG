package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common;

import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.basetypes.defn.v4.UserKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLegacyBookingDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarOfferDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardFormOfPaymentType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardType;
import com.expedia.e3.data.messagetypes.defn.v4.SiteMessageInfoType;
import com.expedia.e3.data.persontypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.*;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.LegacyReservationDataType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 8/24/2016.
 */
@SuppressWarnings("PMD")
public class CarCommonRequestGenerator {

    //PurchaseTypeMask: 4-HC package, 8-FC package, 16-FHC package, 128-standalone car, 256-TC package, 512-FC bundle
    // 1024-HC bundle 4096-THC package 8192-FHC bundle
    public static boolean getStandaloneBoolByPurchaseTypeMask(long purchaseTypeMask) {
        if (CarCommonEnumManager.PurchaseTypeMask.CarOnly.getPurchaseType() == purchaseTypeMask) {
            System.out.println("****getStandaloneBoolByPurchaseTypeMask: Standalone");
            return true;
        } else if (purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.HCPackage.getPurchaseType() || purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.FCPackage.getPurchaseType() ||
                purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.FHCPackage.getPurchaseType() || purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.TCPackage.getPurchaseType()
                || purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.THCPackage.getPurchaseType()) {
            System.out.println("****getStandaloneBoolByPurchaseTypeMask: Package");
            return false;
        }

        return true;
    }
    /*
    * Create reservemsg:LegacyReservationData
    * */
    public static LegacyReservationDataType createLegacyReservationData(boolean needCC, boolean isOnAirport, boolean isAdminArrange)
    {
        // add LegacyReservationData in the request. These data need to be logged by carbs
        LegacyReservationDataType legacyReservationData = new LegacyReservationDataType();
        legacyReservationData.setTravelPackageID(4L);
        legacyReservationData.setWizardID(3L);
        legacyReservationData.setMarketingProgramID(0L);
        legacyReservationData.setPartnerID(0L);
        legacyReservationData.setReferralTrackingNumer("123");
        legacyReservationData.setReferralTrackingServiceID(76L);
        legacyReservationData.setAbTestGroupID(92L);
        legacyReservationData.setItineraryPurposeMask(0);

        legacyReservationData.setBookingDescription("19ba780e-b330-481e-a618-1b4704a83ae1");
        legacyReservationData.setAgentAssistedBoolean(false);
        legacyReservationData.setGroupAccountID(0l);
        legacyReservationData.setGroupAccountDepartmentID(0l);
        legacyReservationData.setAffiliateID(0l);
        legacyReservationData.setOperatingUnitID(0l);
        legacyReservationData.setSearchTypeIDPickup(2l);
        legacyReservationData.setResultTypeIDPickUp(5l);
        legacyReservationData.setCarSpecialEquipmentMask(0);
        legacyReservationData.setCarInsuranceAndWaiverMask(0);
        legacyReservationData.setCarRateOptionTypeIDTraveler(0l);
        legacyReservationData.setReservationGuaranteeMethodID(0l);

        if (needCC)
        {
            legacyReservationData.setReservationGuaranteeMethodID(1l);
            legacyReservationData.setItineraryBookingDirectoryParentID(0l);
        }

        if (isOnAirport)
        {
            //default is 2 for offairport , 0 is for on airport
            legacyReservationData.setSearchTypeIDPickup(0l);
            legacyReservationData.setResultTypeIDPickUp(3l);
        }

        if (isAdminArrange)
        {
            legacyReservationData.setAbTestGroupID(46l);
            legacyReservationData.setAgentAssistedBoolean(true);
        }

        return legacyReservationData;
    }

    public static ReferenceType createTRLReference(Long tpid, long tuid)
    {
        ReferenceType reference = new ReferenceType();
        reference.setReferenceCategoryCode(CarCommonEnumManager.ReferenceCategoryCode.TRL.toString());
        reference.setReferenceCode(createTRLForReserve(tpid, tuid).toString());
        return reference;
    }

    public static ReferenceListType createReferenceList(ReferenceType referenceType)
    {
        ReferenceListType referenceListType = new ReferenceListType();
        List<ReferenceType> referenceTypeList = new ArrayList<ReferenceType>();
        referenceTypeList.add(referenceType);
        referenceListType.setReference(referenceTypeList);
        return referenceListType;

    }

    //// TODO: 10/13/2016 should get from database
    @SuppressWarnings("PMD")
    private static String createTRLForReserve(Long tpid, long tuid)
    {

        return "89438242";
    }

    public static CarOfferDataType createCarOfferData(CarProductType carProduct, CarECommerceSearchRequestType searchRequestType, MultiplierType
            pointOfSaleToPointOfSupplyExchangeRate, TestData testData) throws DataAccessException {
        CarOfferDataType carOfferData = new CarOfferDataType();
        Long tpid = null;
        if(null != searchRequestType.getAuditLogTrackingData().getAuditLogTPID())
        {
            tpid = searchRequestType.getAuditLogTrackingData().getAuditLogTPID();
        }
        long tuid = searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID();
        carOfferData.setCarLegacyBookingData(createCarLegacyBookingData(tuid,searchRequestType));
        CarReservationType carReservationType = getCarReservationType(carProduct, searchRequestType, pointOfSaleToPointOfSupplyExchangeRate, tpid, tuid, testData);
        carOfferData.setCarReservation(carReservationType);
        return carOfferData;
    }


    private static CarReservationType getCarReservationType(CarProductType carProduct, CarECommerceSearchRequestType searchRequestType,
                                                            MultiplierType pointOfSaleToPointOfSupplyExchangeRate, Long tpid, long tuid, TestData testData) throws DataAccessException {
        CarReservationType carReservationType = new CarReservationType();
        carReservationType.setCustomer(CommonDataTypesGenerator.createCustomer());
        carReservationType.setTravelerList(CommonDataTypesGenerator.createTravelerList(testData.getNeedMultiTraveler()));
        carReservationType.setReferenceList(createReferenceList(createTRLReference(tpid,tuid)));
        carReservationType.setClientCode(searchRequestType.getClientCode());
        carReservationType.setPointOfSaleToPointOfSupplyExchangeRate(pointOfSaleToPointOfSupplyExchangeRate);
        carReservationType.setCarProduct(carProduct);
        if(testData.getNeedLoyaltyCard()){
            final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
            final String vendorCode = carsInventoryHelper.getCarVendorCodeBySupplierID(carReservationType.getCarProduct().getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
            carReservationType.getCarProduct().getCarInventoryKey().getCarRate().setLoyaltyProgram(CommonDataTypesGenerator.createCarLoyalty(testData.getCarRate().getLoyaltyNum(), vendorCode));
        }
        return carReservationType;
    }

    public static MultiplierType createPointOfSaleToPointOfSupplyExchangeRate()
    {
        MultiplierType pointOfSaleToPointOfSupplyExchangeRate = new MultiplierType();
        pointOfSaleToPointOfSupplyExchangeRate.setDecimal(100000);
        pointOfSaleToPointOfSupplyExchangeRate.setDecimalPlaceCount(5);
        return pointOfSaleToPointOfSupplyExchangeRate;
    }


    public static CarLegacyBookingDataType createCarLegacyBookingData(long tuid, CarECommerceSearchRequestType searchRequestType)
    {
        CarLegacyBookingDataType carLegacyBookingData = new CarLegacyBookingDataType();
        UserKeyType userKeyType = new UserKeyType();
        userKeyType.setUserID(tuid);
        carLegacyBookingData.setLogonUserKey(userKeyType);
        carLegacyBookingData.setTravelerUserKey(userKeyType);
        carLegacyBookingData.setAuditLogGUID(searchRequestType.getAuditLogTrackingData().getAuditLogGUID());
        carLegacyBookingData.setAuditLogEAPID(searchRequestType.getAuditLogTrackingData().getAuditLogEAPID());
        carLegacyBookingData.setAuditLogGPID(searchRequestType.getAuditLogTrackingData().getAuditLogGPID());
        carLegacyBookingData.setAuditLogLanguageId(searchRequestType.getAuditLogTrackingData().getAuditLogLanguageId());
        carLegacyBookingData.setSendingServerName("expedia");
        carLegacyBookingData.setCurrencyCode(searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode());
        carLegacyBookingData.setTravelPackageID(4l);
        carLegacyBookingData.setMarketingProgramID(0l);
        carLegacyBookingData.setReferralTrackingNumer(null);
        carLegacyBookingData.setReferralTrackingServiceID(0l);
        carLegacyBookingData.setAbTestGroupID(15l);
        carLegacyBookingData.setItineraryPurposeMask(0);
        carLegacyBookingData.setBookingDescription("Car_BS_OM_Reserve");
        carLegacyBookingData.setAgentAssistedBoolean(false);
        carLegacyBookingData.setGroupAccountID(0l);
        carLegacyBookingData.setGroupAccountDepartmentID(0l);
        carLegacyBookingData.setAffiliateID(0l);
        carLegacyBookingData.setOperatingUnitID(0l);
        carLegacyBookingData.setSearchTypeIDPickup(0l);
        carLegacyBookingData.setResultTypeIDPickUp(5l);
        carLegacyBookingData.setCarSpecialEquipmentMask(0);
        carLegacyBookingData.setCarInsuranceAndWaiverMask(0);
        carLegacyBookingData.setCarRateOptionTypeIDTraveler(0l);
        carLegacyBookingData.setReservationGuaranteeMethodID(0l);
        if (null == searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask())
        {
            carLegacyBookingData.setPurchaseTypeMask(128l);
        }
        else {
            carLegacyBookingData.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());
        }
        return carLegacyBookingData;
    }

    public  static String getMessageName(Object requestMessage)
    {
        String bindingElementName = requestMessage.getClass().getSimpleName();
        // trim "Type" from the end of the name
        if (bindingElementName.endsWith("Type"))
        {
            bindingElementName = bindingElementName.substring(0, bindingElementName
                    .lastIndexOf("Type"));
        }
        return bindingElementName;
    }

    public static SiteMessageInfoType createSiteMessageInfoForSearch(TestScenario scenario) throws DataAccessException {
        SiteMessageInfoType siteMessageInfo = new SiteMessageInfoType();
        PointOfSaleKeyType pointOfSaleKey = new PointOfSaleKeyType();
        pointOfSaleKey.setJurisdictionCountryCode(scenario.getJurisdictionCountryCode());
        pointOfSaleKey.setCompanyCode(scenario.getCompanyCode());
        pointOfSaleKey.setManagementUnitCode(scenario.getManagementUnitCode());
        siteMessageInfo.setPointOfSaleKey(pointOfSaleKey);
        //language set
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        siteMessageInfo.setLanguage(commonRequestGenerator.createLanguage(scenario));

        return siteMessageInfo;
    }

    public static CreditCardFormOfPaymentType createCreditCardFormOfPayment(CreditCardType creditCard, PersonType person, ContactInformationType contactInformation) {
        final CreditCardFormOfPaymentType creditCardFormOfPayment = new CreditCardFormOfPaymentType();
        creditCardFormOfPayment.setCreditCard((null == creditCard) ? createCreditCard(null,null,null,null,null,null) : creditCard);
        creditCardFormOfPayment.setPerson((null == person) ? createPerson(null,null,null,"") : person);
        creditCardFormOfPayment.setContactInformation((null == contactInformation) ? createContactInformation(null,null,null) : contactInformation);

        return creditCardFormOfPayment;
    }



    public static CreditCardType createCreditCard(String creditCardSupplierCode, String creditCardNumberEncrypted, String expirationDate,
                                                  String creditCardSecurityCode, String cardPresentBoolean, String maskedCreditCardNumber) {
        final CreditCardType creditCard = new CreditCardType();

        creditCard.setCreditCardSupplierCode((null != creditCardSupplierCode) ? creditCardSupplierCode : RequestDefaultValues.CREDIT_CARD_SUPPLIER_CODE);
        creditCard.setCreditCardNumberEncrypted((null != creditCardNumberEncrypted) ? creditCardNumberEncrypted : RequestDefaultValues.CREDIT_CARD_NUMBER_ENCRYPTED);
        creditCard.setExpirationDate(DateTime.decode((null != expirationDate) ? expirationDate : RequestDefaultValues.EXPIRATION_DATE));
        //Edit by Qiuhua
        if (creditCardSecurityCode == null) {
            creditCardSecurityCode = RequestDefaultValues.CREDIT_CARD_SECURITY_CODE;
        }
        if ((!CompareUtil.isObjEmpty(creditCardSecurityCode) && creditCardSecurityCode.length() > 0)) {
            creditCard.setCreditCardSecurityCode(creditCardSecurityCode);
        }
        creditCard.setCardPresentBoolean(Boolean.parseBoolean((null != cardPresentBoolean) ? cardPresentBoolean : RequestDefaultValues.CARD_PRESENT_BOOLEAN));
        creditCard.setMaskedCreditCardNumber((null != maskedCreditCardNumber) ? maskedCreditCardNumber : RequestDefaultValues.MASKED_CREDIT_CARD_NUMBER);

        return creditCard;
    }

    public static PersonType createPerson( String firstName , String lastName ,String ageCode , String suffix )
    {
        final PersonType person = new PersonType();
        final PersonNameType personName = new PersonNameType();
        person.setPersonName(personName);
        final AgeType age = new AgeType();
        person.setAge(age);
        person.getPersonName().setFirstName((null != firstName) ? firstName : RequestDefaultValues.FIRST_NAME);
        person.getPersonName().setLastName((null != lastName) ? lastName : RequestDefaultValues.LAST_NAME + suffix);
        person.getAge().setAgeCode((null != ageCode) ? ageCode : RequestDefaultValues.AGE_CODE);
        return person;
    }

    public static ContactInformationType createContactInformation(PhoneListType phoneList, AddressListType addressList, EmailAddressEntryListType emailAddressEntryList)
    {
        final ContactInformationType contactInformation = new ContactInformationType();
        contactInformation.setPhoneList((null != phoneList) ? phoneList : createPhoneList(null,null,null,null));
        contactInformation.setAddressList((null != addressList) ? addressList : createAddressList(null,null,null,null,null,null,null,null));
        contactInformation.setEmailAddressEntryList((null != emailAddressEntryList) ? emailAddressEntryList : createEmailAddressEntryList(null));
        return contactInformation;
    }

    public static PhoneListType createPhoneList(String phoneAreaCode, String phoneCategoryCode, String phoneCountryCode,
                                                String phoneNumber)
    {
        final PhoneListType phoneListType = new PhoneListType();
        final List<PhoneType> phoneList = new ArrayList<PhoneType>();
        phoneListType.setPhone(phoneList);
        final PhoneType phone = new PhoneType();
        phone.setPhoneCategoryCode((null != phoneCategoryCode) ? phoneCategoryCode : RequestDefaultValues.CONTACTOR_CATEGORY_CODE);
        phone.setPhoneCountryCode((null != phoneCountryCode) ? phoneCountryCode : RequestDefaultValues.CONTACT_PHONE_COUNTRY_CODE);
        phone.setPhoneAreaCode((null != phoneAreaCode) ? phoneAreaCode : RequestDefaultValues.CONTACT_PHONE_AREA_CODE);
        phone.setPhoneNumber((null != phoneNumber) ? phoneNumber : RequestDefaultValues.CONTACT_PHONE_NUMBER);
        phoneList.add(phone);

        return phoneListType;
    }

    public static AddressListType createAddressList(String addressCategoryCode, String firstAddressLine, String secondAddressList,
                                                    String cityName, String postalCode, String countryAlpha2Code, String countryAlpha3Code, String provinceName)
    {
        final AddressListType addressListType = new AddressListType();
        final List<AddressType> addressList = new ArrayList<AddressType>();
        addressListType.setAddress(addressList);
        final AddressType address = new AddressType();
        address.setAddressCategoryCode((null != addressCategoryCode) ? addressCategoryCode : RequestDefaultValues.CONTACTOR_CATEGORY_CODE);
        address.setFirstAddressLine((null != firstAddressLine) ? firstAddressLine : RequestDefaultValues.CONTACT_FIRST_ADDRESSLINE);
        address.setSecondAddressLine((null != secondAddressList) ? secondAddressList : RequestDefaultValues.CONTACT_SECOND_ADDRESSLINE);
        address.setCityName((null != cityName) ? cityName : RequestDefaultValues.CONTACT_CITY_NAME);
        address.setPostalCode((null != postalCode) ? postalCode : RequestDefaultValues.CONTACT_POSTAL_CODE);
        address.setProvinceName((null != provinceName) ? provinceName : RequestDefaultValues.CONTACT_CITY_NAME);
        address.setCountryAlpha3Code((null != countryAlpha3Code) ? countryAlpha3Code : RequestDefaultValues.CONTACT_COUNTRY_ALPHA3CODE);
        addressList.add(address);

        return addressListType;
    }

    public static EmailAddressEntryListType createEmailAddressEntryList(String emailAddress)
    {
        final EmailAddressEntryListType emailAddressEntryListType = new EmailAddressEntryListType();
        final List<EmailAddressEntryType> emailAddressEntryList = new ArrayList<EmailAddressEntryType>();
        final EmailAddressEntryType emailAddressEntry = new EmailAddressEntryType();
        emailAddressEntry.setSequence(1);
        emailAddressEntry.setEmailAddress((null != emailAddress) ? emailAddress : RequestDefaultValues.CONTACT_EMAIL_ADDRESS);
        emailAddressEntryList.add(emailAddressEntry);
        return emailAddressEntryListType;
    }
}
