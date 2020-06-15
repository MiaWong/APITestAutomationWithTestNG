package com.expedia.s3.cars.framework.test.common.execution.requestgeneration;


import com.expedia.e3.data.basetypes.defn.v4.LegacySiteKeyType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardFormOfPaymentType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.messagetypes.defn.v4.MessageInfoType;
import com.expedia.e3.data.messagetypes.defn.v4.SiteMessageInfoType;
import com.expedia.e3.data.persontypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.*;
import com.expedia.e3.data.traveltypes.defn.v4.CustomerType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@SuppressWarnings("PMD")
public class CommonDataTypesGenerator
{
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private CommonDataTypesGenerator()
    {

    }

    public static MessageInfoType createMessageInfo(String msgName, String msgVersion)
    {
        final MessageInfoType msgInfo = new MessageInfoType();
        msgInfo.setMessageNameString(msgName);
        msgInfo.setMessageVersion(msgVersion);
        msgInfo.setMessageGUID(UUID.randomUUID().toString());
        msgInfo.setTransactionGUID(UUID.randomUUID().toString());
        msgInfo.setCreateDateTime(DateTime.getInstanceByDateTime(new Date()));

        return msgInfo;
    }

    public static MessageInfoType buildMessageInfo(String msgName, MessageInfoType searchRequestMessageInfo)
    {
        final MessageInfoType msgInfo = new MessageInfoType();
        msgInfo.setMessageNameString(msgName);
        msgInfo.setMessageVersion(searchRequestMessageInfo.getMessageVersion());
        msgInfo.setMessageGUID(UUID.randomUUID().toString());
        msgInfo.setTransactionGUID(UUID.randomUUID().toString());
        msgInfo.setCreateDateTime(DateTime.getInstanceByDateTime(new Date()));

        if(null != searchRequestMessageInfo)
        {
            if (!StringUtils.isEmpty(searchRequestMessageInfo.getClientHostnameString()))
            {
                msgInfo.setClientHostnameString(searchRequestMessageInfo.getClientHostnameString());
            }

            if (!StringUtils.isEmpty(searchRequestMessageInfo.getClientName()))
            {
                msgInfo.setClientName(searchRequestMessageInfo.getClientName());
            }

            if (!StringUtils.isEmpty(searchRequestMessageInfo.getEndUserIPAddress()))
            {
                msgInfo.setEndUserIPAddress(searchRequestMessageInfo.getEndUserIPAddress());
            }
        }
        return msgInfo;
    }

    public static SiteMessageInfoType createV4SiteMessageInfo(
            PointOfSaleKeyType posKey,
            LegacySiteKeyType legacySiteKey,
            AuditLogTrackingDataType auditLogData)
    {
        LegacySiteKeyType newLegacySiteKey = legacySiteKey;
        final SiteMessageInfoType siteInfo = new SiteMessageInfoType();
        siteInfo.setPointOfSaleKey(posKey);
        if( null == newLegacySiteKey && auditLogData != null)
        {
             newLegacySiteKey = new LegacySiteKeyType();
            if( null != auditLogData.getAuditLogTPID())
            {
                newLegacySiteKey.setTPID(auditLogData.getAuditLogTPID());
            }
            if( null != auditLogData.getAuditLogGPID())
            {
                newLegacySiteKey.setGPID(auditLogData.getAuditLogGPID());
            }
            if( null != auditLogData.getAuditLogEAPID())
            {
                newLegacySiteKey.setEAPID(auditLogData.getAuditLogEAPID());
            }
        }

        siteInfo.setLegacySiteKey(newLegacySiteKey);

        return siteInfo;

    }

    public static ReferenceListType createTRLReference(int trl)
    {
        final ReferenceListType refList = new ReferenceListType();
        final List<ReferenceType> listOfRefs = new ArrayList<ReferenceType>();
        final ReferenceType ref = new ReferenceType();
        ref.setReferenceCategoryCode("TRL");
        ref.setReferenceCode(String.valueOf(trl));
        listOfRefs.add(ref);
        refList.setReference(listOfRefs);
        return refList;

    }


    public static CarReservationType createReservation(CarProductType carProduct, String clientCode)
    {
        final CarReservationType carResv = new CarReservationType();
        carResv.setCarProduct(carProduct);
        carResv.setTravelerList(createTravelerList(false));
        carResv.setCustomer(createCustomer());
        carResv.setClientCode(clientCode);
        return carResv;

    }

    public static CustomerType createCustomer()
    {
        final CustomerType customer = new CustomerType();
        customer.setPerson(createPerson());
        customer.setContactInformation(createContactInformation());
        return customer;


    }

    public static TravelerListType createTravelerList(boolean isMultiTraveler)
    {
        final TravelerListType travelerList = new TravelerListType();
        travelerList.setTraveler(createListOfTraveler(isMultiTraveler));
        return travelerList;
    }

    private static List<TravelerType> createListOfTraveler(boolean isMultiTraveler) {
        final List<TravelerType> listOfTravelers = new ArrayList<TravelerType>();
        listOfTravelers.add(createTraveler());
        if (isMultiTraveler) {
            listOfTravelers.add(createTraveler());
        }
        return listOfTravelers;
    }

    private static TravelerType createTraveler()
    {
        final TravelerType traveler = new TravelerType();
        traveler.setPerson(createPerson());
        traveler.setContactInformation(createContactInformation());
        return traveler;

    }

    private static ContactInformationType createContactInformation()
    {
        final ContactInformationType contactInfo = new ContactInformationType();
        contactInfo.setPhoneList(createPhoneList());
        contactInfo.setEmailAddressEntryList(createEmailAddressEntryList());
        contactInfo.setAddressList(createAddressList());
        return contactInfo;

    }

    private static EmailAddressEntryListType createEmailAddressEntryList()
    {
        final EmailAddressEntryListType emailList = new EmailAddressEntryListType();
        emailList.setEmailAddressEntry(createListOfEmails());
        return emailList;

    }

    private static List<EmailAddressEntryType> createListOfEmails()
    {
        final List<EmailAddressEntryType> listOfEmails = new ArrayList<EmailAddressEntryType>();
        listOfEmails.add(createEmail());
        return listOfEmails;

    }

    private static EmailAddressEntryType createEmail()
    {
        final EmailAddressEntryType email = new EmailAddressEntryType();
        email.setEmailAddress(genRandomAlphaString()+"@expedia.com");
        return email;
    }

    private static AddressListType createAddressList()
    {
        final AddressListType addressList = new AddressListType();
        addressList.setAddress(createListOfAddress());
        return addressList;

    }

    private static List<AddressType> createListOfAddress()
    {
        final List<AddressType> listOfAddress = new ArrayList<AddressType>();
        listOfAddress.add(createAddress());
        return listOfAddress;

    }

    private static AddressType createAddress()
    {
        final AddressType address = new AddressType();
        address.setAddressCategoryCode("Home");
        address.setFirstAddressLine("82, Boulevard de Clichy");
        address.setCityName("New York");
        address.setPostalCode("98052");
        address.setProvinceName("NY");
        address.setCountryAlpha3Code("USA");
        return address;
    }

    private static PhoneListType createPhoneList()
    {
        final PhoneListType phoneList = new PhoneListType();
        phoneList.setPhone(createListOfPhones());
        return phoneList;

    }

    private static List<PhoneType> createListOfPhones()
    {
        final List<PhoneType> listOfPhones = new ArrayList<PhoneType>();
        listOfPhones.add(createPhone());
        return listOfPhones;

    }

    private static PhoneType createPhone()
    {
        final PhoneType phone = new PhoneType();
        phone.setPhoneCountryCode("1");
        phone.setPhoneAreaCode("425");
        phone.setPhoneNumber("555-5555");
        return phone;

    }

    private static PersonType createPerson()
    {
        final PersonType person = new PersonType();
        person.setPersonName(createPersonName());
        person.setAge(createAge());
        return person;

    }

    private static PersonNameType createPersonName()
    {
        final PersonNameType personName = new PersonNameType();
        personName.setFirstName("CARS");
        personName.setLastName("STT" + genRandomAlphaString());
        return personName;

    }

    private static AgeType createAge()
    {
        final AgeType age = new AgeType();
        age.setAgeCode("Adult");
        return age;
    }

    protected static DateTime createDateTimeFromToday(int daysToAdd)
    {
        final TimeZone tz = TimeZone.getDefault();
        final Calendar cal = Calendar.getInstance(tz);
        cal.add(Calendar.DATE, daysToAdd);
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // do not pass in the time zone info when calling this function.
        return DateTime.getInstanceByDateTime(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,    // Calendar.MONTH starts from 0.
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND));

    }

    protected static String genRandomAlphaString()
    {
        final StringBuilder sb = new StringBuilder();
        for(int i =0; i < 10; i++ )
        {
            sb.append((char)(RANDOM.nextInt(26) + 'a'));
        }

        return sb.toString().toUpperCase();
    }

    public static CarSpecialEquipmentListType createSpecialEquipmentList(List<String> specialEquipmentList)
    {
        final CarSpecialEquipmentListType  specialEquipList = new CarSpecialEquipmentListType();
        specialEquipList.setCarSpecialEquipment(createListOfSepcialEqupment(specialEquipmentList));
        return specialEquipList;
    }

    private static List<CarSpecialEquipmentType> createListOfSepcialEqupment(List<String> specialEquipmentList)
    {
        final List<CarSpecialEquipmentType> listOfSpecialEquipment = new ArrayList<CarSpecialEquipmentType>();
        for (final String specialEquipment : specialEquipmentList) {
            listOfSpecialEquipment.add(createSepcialEqupment(specialEquipment));
        }
        return listOfSpecialEquipment;
    }

    private static CarSpecialEquipmentType createSepcialEqupment(String specialEquip)
    {
        final CarSpecialEquipmentType equipType = new CarSpecialEquipmentType();
        equipType.setCarSpecialEquipmentCode(specialEquip);
        return equipType;
    }

    public static CarSearchStrategyType createCarSearchStrategy(TestScenario testScenario)
    {
        final CarSearchStrategyType carSearchStrategyType = new CarSearchStrategyType();
        carSearchStrategyType.setPricingVisibilityBoolean(true);
        carSearchStrategyType.setPackageBoolean(!testScenario.isStandalone());
        return carSearchStrategyType;
    }

    public static CreditCardFormOfPaymentType createCreditCardFormOfPayment(CreditCardType creditCard, PersonType person, ContactInformationType contactInformation)
    {
        final CreditCardFormOfPaymentType creditCardFormOfPayment = new CreditCardFormOfPaymentType();
        creditCardFormOfPayment.setCreditCard(CompareUtil.isObjEmpty(creditCard) ? createCreditCard(null,null,null,null,null,null) : creditCard);
        creditCardFormOfPayment.setPerson(CompareUtil.isObjEmpty(person) ? createPerson() : person);
        creditCardFormOfPayment.setContactInformation(CompareUtil.isObjEmpty(contactInformation) ? createContactInformation() : contactInformation);

        return creditCardFormOfPayment;
    }

    public static CreditCardType createCreditCard( String creditCardSupplierCode, String creditCardNumberEncrypted , String expirationDate,
                                                   String creditCardSecurityCode, String cardPresentBoolean, String maskedCreditCardNumber)
    {
        final CreditCardType creditCard = new CreditCardType();

        creditCard.setCreditCardSupplierCode(!CompareUtil.isObjEmpty(creditCardSupplierCode) ? creditCardSupplierCode : RequestDefaultValues.CREDIT_CARD_SUPPLIER_CODE);
        creditCard.setCreditCardNumberEncrypted(!CompareUtil.isObjEmpty(creditCardNumberEncrypted) ? creditCardNumberEncrypted : RequestDefaultValues.CREDIT_CARD_NUMBER_ENCRYPTED);
        creditCard.setExpirationDate(DateTime.decode((!CompareUtil.isObjEmpty(expirationDate) ? expirationDate : RequestDefaultValues.EXPIRATION_DATE)));
        //Edit by Qiuhua
        if (CompareUtil.isObjEmpty(creditCardSecurityCode)) {
            creditCardSecurityCode = RequestDefaultValues.CREDIT_CARD_SECURITY_CODE;
        }
        if (!CompareUtil.isObjEmpty(creditCardSecurityCode)) {
            creditCard.setCreditCardSecurityCode(creditCardSecurityCode);
        }
        creditCard.setCardPresentBoolean(Boolean.parseBoolean(!CompareUtil.isObjEmpty(cardPresentBoolean) ? cardPresentBoolean : RequestDefaultValues.CARD_PRESENT_BOOLEAN));
        creditCard.setMaskedCreditCardNumber(!CompareUtil.isObjEmpty(maskedCreditCardNumber) ? maskedCreditCardNumber : RequestDefaultValues.MASKED_CREDIT_CARD_NUMBER);

        return creditCard;
    }

    public static LoyaltyProgramListType createLoyaltyProgramList(String loyaltyCode) {
        final LoyaltyProgramListType loyaltyProgram = new LoyaltyProgramListType();
        loyaltyProgram.setLoyaltyProgram(createLoyaltyProgram(loyaltyCode));
        return loyaltyProgram;
    }

    public static List<LoyaltyProgramType> createLoyaltyProgram(String loyaltyCode) {
        final List<LoyaltyProgramType> loyaltyProgramType = new ArrayList<>();
        loyaltyProgramType.add(createLoyalty(loyaltyCode));
        return loyaltyProgramType;
    }

    public static LoyaltyProgramType createLoyalty(String loyaltyCode) {
        final LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
        if (loyaltyCode.contains("-")) {
            if (!loyaltyCode.contains("NoCategory")) {
                loyaltyProgram.setLoyaltyProgramCategoryCode(loyaltyCode.split("-")[0]);
            }
            loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCode.split("-")[1]);
            if (loyaltyCode.contains("Air")) {
                loyaltyProgram.setLoyaltyProgramCode("AA");
            }
        }
        return loyaltyProgram;
    }

    public static LoyaltyProgramType createCarLoyalty(String loyaltyCard, String vendorCode) {
        LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
        if (!loyaltyCard.contains("Air")) {
            loyaltyProgram.setLoyaltyProgramCategoryCode("Car");
            loyaltyProgram.setLoyaltyProgramCode(vendorCode);
            if (loyaltyCard.contains("-")) {
                loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCard.split("-")[1]);
            } else {
                loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCard);
            }
        }
        return loyaltyProgram;
    }

}
