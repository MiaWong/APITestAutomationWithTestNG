package com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationListType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionListType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardFormOfPaymentType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardType;
import com.expedia.e3.data.persontypes.defn.v4.AgeType;
import com.expedia.e3.data.persontypes.defn.v4.ContactInformationType;
import com.expedia.e3.data.persontypes.defn.v4.EmailAddressEntryListType;
import com.expedia.e3.data.persontypes.defn.v4.EmailAddressEntryType;
import com.expedia.e3.data.persontypes.defn.v4.PersonNameType;
import com.expedia.e3.data.persontypes.defn.v4.PersonType;
import com.expedia.e3.data.placetypes.defn.v4.AddressListType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.eclipse.jetty.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mpaudel on 6/29/16.
 */
@SuppressWarnings("PMD")
//TODO: extract these common scs stuff into its own module
public class SCSRequestGenerator {
    private static final Random s_rand = new Random(System.nanoTime());

    private CarSupplyConnectivitySearchRequestType searchReq;
    private CarSupplyConnectivitySearchResponseType searchResp;
    private CarSupplyConnectivityGetDetailsRequestType detailsReq;
    private CarSupplyConnectivityGetDetailsResponseType detailsResp;
    private CarSupplyConnectivityReserveRequestType reserveReq;
    private CarSupplyConnectivityReserveResponseType reserveResp;
    private CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailReq;
    private CarSupplyConnectivityGetCostAndAvailabilityResponseType costAndAvailResp;
    private CarSupplyConnectivityGetReservationRequestType getReservationReq;
    private CarSupplyConnectivityGetReservationResponseType getReservationResp;
    private CarSupplyConnectivityCancelRequestType cancelReq;
    private CarSupplyConnectivityCancelResponseType cancelResp;
    private CarProductType selectedCarProduct;
    private TestData testdata;

    public CarProductType getSelectedCarProduct() {
        return selectedCarProduct;
    }

    public SCSRequestGenerator(CarSupplyConnectivitySearchRequestType searchReq, CarSupplyConnectivitySearchResponseType searchResp) {
        this.searchReq = searchReq;
        this.searchResp = searchResp;
    }

    public SCSRequestGenerator(CarSupplyConnectivitySearchRequestType searchReq, CarSupplyConnectivitySearchResponseType searchResp, TestData testData) {
        this.searchReq = searchReq;
        this.searchResp = searchResp;
        this.testdata = testData;
    }

    public void setSelectedCarProduct(CarProductType selectedCarProduct)
    {
        this.selectedCarProduct = selectedCarProduct;
    }

    public CarSupplyConnectivityGetDetailsRequestType createDetailsRequest() {
        CarSupplyConnectivityGetDetailsRequestType req = new CarSupplyConnectivityGetDetailsRequestType();
        if (selectedCarProduct == null) {
            selectedCarProduct = getRandomCarProduct();
        }
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(searchReq.getAuditLogTrackingData());
        String m_currencyCode = searchReq.getCarSearchCriteriaList().
                getCarSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        CarProductListType carList = new CarProductListType();
        List<CarProductType> listOfCars = new ArrayList<CarProductType>();
        listOfCars.add(selectedCarProduct);
        carList.setCarProduct(listOfCars);
        req.setCarProductList(carList);
        this.detailsReq = req;
        return req;
    }

    public CarSupplyConnectivityGetDetailsRequestType createDetailsRequestWithCarRateQualifier() {
        if (selectedCarProduct == null) {
            selectedCarProduct = getCarProductWithCarRateQualifier();
        }
        return createDetailsRequest();
    }

    public CarSupplyConnectivityGetReservationRequestType createGetReservationRequest() {
        CarSupplyConnectivityGetReservationRequestType req = new CarSupplyConnectivityGetReservationRequestType();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(searchReq.getAuditLogTrackingData());
        String m_currencyCode = searchReq.getCarSearchCriteriaList().
                getCarSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        List<CarReservationType> carReservationList = new ArrayList<CarReservationType>();
        carReservationList.add(this.reserveResp.getCarReservation());
        CarReservationListType carReservationListType = new CarReservationListType();
        carReservationListType.setCarReservation(carReservationList);
        req.setCarReservationList(carReservationListType);
        this.getReservationReq = req;
        return req;
    }

    public CarSupplyConnectivityReserveRequestType createReserveRequest() {

        CarSupplyConnectivityReserveRequestType req = new CarSupplyConnectivityReserveRequestType();
        if (selectedCarProduct == null) {
            selectedCarProduct = getRandomCarProduct();
        }
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchReq.getMessageInfo().getMessageVersion()));
        req.setPackageBoolean(false);
        req.setCurrencyCode(searchReq.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCurrencyCode());
        req.setPointOfSaleKey(searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(searchReq.getAuditLogTrackingData());
        req.setCarProduct(selectedCarProduct);
        if (null != testdata && testdata.getNeedMultiTraveler())
        {
            req.setTravelerList(CommonDataTypesGenerator.createTravelerList(true));
        }
        else
        {
            req.setTravelerList(CommonDataTypesGenerator.createTravelerList(false));
        }
        req.setCustomer(CommonDataTypesGenerator.createCustomer());

        if (null != testdata && StringUtil.isNotBlank(testdata.getCarSpecialEquipmentCode()))
        {
            //option 1: if specialEquipmentCode = 'NAV' or 'NAV|CVO', then it will go to CarVehicleOptionList
            //option 2:  if specialEquipmentCode = 'NAV,NVS|SEP', then it will go to CarSpecialEquipmentList
            //option 3:  if specialEquipmentCode = 'NAV|CVO|SEP', then it will go to both
            String specialCode = testdata.getCarSpecialEquipmentCode();
            String[] sepArrays = specialCode.split("\\|");
            String[] codeArrays = sepArrays[0].split(",");

            CarVehicleOptionListType optionList = new CarVehicleOptionListType();
            List<CarVehicleOptionType> carVehicleOptionTypes = new ArrayList<>();
            optionList.setCarVehicleOption(carVehicleOptionTypes);

            CarSpecialEquipmentListType carSpecialEquipmnetList = new CarSpecialEquipmentListType();
            List<CarSpecialEquipmentType> carSpecialEquipmentTypes = new ArrayList<>();
            carSpecialEquipmnetList.setCarSpecialEquipment(carSpecialEquipmentTypes);

            for (int i = 0; i < codeArrays.length; i++ )
            {
                CarVehicleOptionType carEquipment = new CarVehicleOptionType();
                carEquipment.setCarSpecialEquipmentCode(codeArrays[i]);
                carEquipment.setCarVehicleOptionCategoryCode("special equipment");
                carVehicleOptionTypes.add(carEquipment);

                CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                carSpecialEquipment.setCarSpecialEquipmentCode(codeArrays[i]);
                carSpecialEquipmentTypes.add(carSpecialEquipment);
            }

            if (sepArrays.length == 1)
            {
            }
            else if (sepArrays.length == 3)
            {
                req.getCarProduct().setCarVehicleOptionList(optionList);
                req.setCarSpecialEquipmentList(carSpecialEquipmnetList);
            }
            else if (sepArrays.length == 2 && sepArrays[1].equals("SEP"))
            {
                req.setCarSpecialEquipmentList(carSpecialEquipmnetList);
            }
            else if (sepArrays.length == 2 && sepArrays[1].equals("CVO"))
            {
                req.getCarProduct().setCarVehicleOptionList(optionList);
            }
        }

        if(null != testdata && StringUtil.isNotBlank(testdata.getSpecialEquipmentEnumType()))
        {
            CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
            List<CarVehicleOptionType> carVehicleOptionTypes = new ArrayList<>();
            carVehicleOptionListType.setCarVehicleOption(carVehicleOptionTypes);
            CarVehicleOptionType carEquipment = new CarVehicleOptionType();
            carEquipment.setCarSpecialEquipmentCode(testdata.getSpecialEquipmentEnumType());
            carEquipment.setCarVehicleOptionCategoryCode("special equipment");
            carVehicleOptionTypes.add(carEquipment);
            req.getCarProduct().setCarVehicleOptionList(carVehicleOptionListType);
        }

        if(null != testdata && testdata.isNeedCC())
        {

            CreditCardFormOfPaymentType ccCard = new CreditCardFormOfPaymentType();

            //Person
            PersonType personType = new PersonType();
            ccCard.setPerson(personType);
            PersonNameType personNameType = new PersonNameType();
            personNameType.setFirstName("CARBSRESERVE");
            personNameType.setLastName("STTWO");
            personType.setPersonName(personNameType);
            personType.setGenderCode("Unknown");
            AgeType ageType = new AgeType();
            ageType.setAgeCode("Adult");
            ageType.setAgeYearCount(0l);
            personType.setAge(ageType);

             // card
            CreditCardType creditCardType = new CreditCardType();
            ccCard.setCreditCard(creditCardType);
            creditCardType.setCreditCardSupplierCode("MasterCard");
            creditCardType.setCreditCardNumberEncrypted("AQAQAAEAEAAxMTAxM/e68CKjDVM76j5KUY3VlOVaVs4T5TzQKNkiS4eOuTuu");

           DateTime dateTime = new DateTime("2017-12-30T23:59:00");
           creditCardType.setExpirationDate(dateTime);
            creditCardType.setCardPresentBoolean(true);
            creditCardType.setMaskedCreditCardNumber("2005");

            //phone & EmailAddress & address
            PhoneListType phoneListType = new PhoneListType();
            List<PhoneType> phoneTypes = new ArrayList<>();
            phoneListType.setPhone(phoneTypes);
            PhoneType phone = new PhoneType();
            phoneTypes.add(phone);
            phone.setPhoneAreaCode("2");
            phone.setPhoneCountryCode("1");
            phone.setPhoneNumber("94123456");

            EmailAddressEntryListType emailAddressEntryListType = new EmailAddressEntryListType();
            List<EmailAddressEntryType> emailAddressEntryTypes = new ArrayList<>();
            emailAddressEntryListType.setEmailAddressEntry(emailAddressEntryTypes);
            EmailAddressEntryType email = new EmailAddressEntryType();
            email.setEmailAddress("testcarssttwo@expedia.com");
            emailAddressEntryTypes.add(email);


            AddressListType addressListType = new AddressListType();
            List<AddressType> addressTypes = new ArrayList<>();
            addressListType.setAddress(addressTypes);
            AddressType address = new AddressType();
            address.setFirstAddressLine("");
            address.setSecondAddressLine("");
            address.setThirdAddressLine("");
            address.setFourthAddressLine("");
            address.setCityName("GRI");
            address.setProvinceName("ABC");
            address.setPostalCode("123456");
            address.setCountryAlpha3Code("FRA");

            ContactInformationType contactInformationType = new ContactInformationType();
            contactInformationType.setPhoneList(phoneListType);
            contactInformationType.setEmailAddressEntryList(emailAddressEntryListType);
            contactInformationType.setAddressList(addressListType);

            ccCard.setContactInformation(contactInformationType);
            req.setCreditCardFormOfPayment(ccCard);
            }

        if(null != testdata && StringUtil.isNotBlank(testdata.getBillingNumber()))
        {
            req.setBillingCode(testdata.getBillingNumber());
        }
        this.reserveReq = req;
        return req;
    }

    public CarSupplyConnectivityGetCostAndAvailabilityRequestType createCostAndAvailRequest() {
        CarSupplyConnectivityGetCostAndAvailabilityRequestType req = new CarSupplyConnectivityGetCostAndAvailabilityRequestType();
        if (selectedCarProduct == null) {
            selectedCarProduct = getRandomCarProduct();
        }
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(searchReq.getAuditLogTrackingData());
        String m_currencyCode = searchReq.getCarSearchCriteriaList().
                getCarSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        CarProductListType carAvailList = new CarProductListType();
        List<CarProductType> listOfCars = new ArrayList<CarProductType>();
        listOfCars.add(selectedCarProduct);
        carAvailList.setCarProduct(listOfCars);
        req.setCarProductList(carAvailList);
        this.costAndAvailReq = req;
        return req;
    }

    public CarSupplyConnectivityCancelRequestType createCancelRequest() {
        CarSupplyConnectivityCancelRequestType req = new CarSupplyConnectivityCancelRequestType();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                reserveReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(reserveReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(reserveReq.getAuditLogTrackingData());
        req.setCarReservation(reserveResp.getCarReservation());
        req.setCommitBoolean(true);
        this.cancelReq = req;
        return req;
    }

    private String getMessageName(Object requestMessage) {
        String bindingElementName = requestMessage.getClass().getSimpleName();
        // trim "Type" from the end of the name
        if (bindingElementName.endsWith("Type")) {
            bindingElementName = bindingElementName.substring(0, bindingElementName
                    .lastIndexOf("Type"));
        }
        return bindingElementName;
    }

    @SuppressWarnings("CPD-START")
    protected CarProductType getRandomCarProduct() {
        if (null != searchResp
                && null != searchResp.getCarSearchResultList()
                && null != searchResp.getCarSearchResultList().getCarSearchResult()
                && searchResp.getCarSearchResultList().getCarSearchResult().size() > 0) {
            for (CarSearchResultType result : searchResp.getCarSearchResultList().getCarSearchResult()) {
                if (null == result.getCarProductList() ||
                        null == result.getCarProductList().getCarProduct() ||
                        0 == result.getCarProductList().getCarProduct().size() ||
                        null == searchReq.getCarSearchStrategy() || (null != result.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean() &&
                        searchReq.getCarSearchStrategy().getPackageBoolean() != result.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean())) {
                    continue;
                }
                final int size = result.getCarProductList().getCarProduct().size();
                final int min = 0, max = size - 1;
                return result.getCarProductList().getCarProduct().get(getRandomIndex(min, max));
            }
        }
        return null;
    }

    @SuppressWarnings("CPD-END")

    protected CarProductType getCarProductWithCarRateQualifier() {
        if (null != searchResp
                && null != searchResp.getCarSearchResultList()
                && null != searchResp.getCarSearchResultList().getCarSearchResult()
                && searchResp.getCarSearchResultList().getCarSearchResult().size() > 0) {
            for (CarSearchResultType result : searchResp.getCarSearchResultList().getCarSearchResult()) {
                if (null == result.getCarProductList() ||
                        null == result.getCarProductList().getCarProduct() ||
                        0 == result.getCarProductList().getCarProduct().size() ||
                        null == searchReq.getCarSearchStrategy() || (null != result.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean() &&
                        searchReq.getCarSearchStrategy().getPackageBoolean() != result.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean())) {
                    continue;
                }

                for (CarProductType car : result.getCarProductList().getCarProduct()) {
                    if (null != car.getCarInventoryKey() && null != car.getCarInventoryKey().getCarRate() &&
                            !car.getCarInventoryKey().getCarRate().getCarRateQualifierCode().isEmpty()) {
                        return car;
                    }
                }
            }
        }
        return null;
    }


    //InfantChildSeat,NavigationalSystem
    /// <summary>
    /// Build CarSpecialEquipmentList on a CarSupplyConnectivityReserveRequest
    /// </summary>
    /// <param name="req"></param>
    /// <param name="specialEquip"></param>
    public static void buildSpecialEquipmentList(CarSupplyConnectivityReserveRequestType req, String specialEquip) {
        final CarSpecialEquipmentListType carSpecialEquipmentListType = new CarSpecialEquipmentListType();
        final List<CarSpecialEquipmentType> specialEquipmentTypeList = new ArrayList<CarSpecialEquipmentType>();
        carSpecialEquipmentListType.setCarSpecialEquipment(specialEquipmentTypeList);
        String[] codeList = specialEquip.split(",");
        for (int i = 0; i < codeList.length; i++) {
            final CarSpecialEquipmentType carSpecialEquipmentType = new CarSpecialEquipmentType();
            carSpecialEquipmentType.setCarSpecialEquipmentCode(codeList[i]);
            specialEquipmentTypeList.add(carSpecialEquipmentType);
        }
        if (!CompareUtil.isObjEmpty(carSpecialEquipmentListType.getCarSpecialEquipment())) {
            req.setCarSpecialEquipmentList(carSpecialEquipmentListType);
        }
    }

    public static void buildSpecialEquipmentList(CarSupplyConnectivityReserveRequestType req, ReserveDefaultValue defaultValue) {
        if (!CompareUtil.isObjEmpty(defaultValue.getReserveConfigValue().getCarSpecialEquipmentCode())) {
            String[] codeList = defaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(",");
            if (!CompareUtil.isObjEmpty(codeList)) {
                final CarSpecialEquipmentListType carSpecialEquipmentListType = new CarSpecialEquipmentListType();
                final List<CarSpecialEquipmentType> specialEquipmentTypeList = new ArrayList<CarSpecialEquipmentType>();
                carSpecialEquipmentListType.setCarSpecialEquipment(specialEquipmentTypeList);
                for (String code : codeList) {
                    final CarSpecialEquipmentType carSpecialEquipmentType = new CarSpecialEquipmentType();
                    carSpecialEquipmentType.setCarSpecialEquipmentCode(code);
                    specialEquipmentTypeList.add(carSpecialEquipmentType);
                }
                req.setCarSpecialEquipmentList(carSpecialEquipmentListType);
            }
        }
        if (!CompareUtil.isObjEmpty(defaultValue.getReserveConfigValue().getSpecialEquipmentEnumType())) {
            String[] typeList = defaultValue.getReserveConfigValue().getSpecialEquipmentEnumType().split(",");
            if (!CompareUtil.isObjEmpty(typeList)) {
                final CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
                final List<CarVehicleOptionType> vehicleOptionTypeList = new ArrayList<CarVehicleOptionType>();
                carVehicleOptionListType.setCarVehicleOption(vehicleOptionTypeList);
                for (String type : typeList) {
                    final CarVehicleOptionType carVehicleOpt = new CarVehicleOptionType();
                    carVehicleOpt.setCarVehicleOptionCategoryCode("special equipment");
                    carVehicleOpt.setCarSpecialEquipmentCode(type);
                    vehicleOptionTypeList.add(carVehicleOpt);
                }
                req.getCarProduct().setCarVehicleOptionList(carVehicleOptionListType);
            }
        }

    }

    /*
        protected CarProductType getRandomCarProduct()
    {
        final boolean hasSearchResultList =
                (null != searchResp.getCarSearchResultList() &&
                        null != searchResp.getCarSearchResultList().getCarSearchResult() &&
                        searchResp.getCarSearchResultList().getCarSearchResult().size() > 0);
        if (null != searchResp && hasSearchResultList)
        {
            for (CarSearchResultType result : searchResp.getCarSearchResultList().getCarSearchResult())
            {
                final boolean doesNotHaveCarProduct =
                        (null == result.getCarProductList().getCarProduct() ||
                                0 == result.getCarProductList().getCarProduct().size());
                final boolean doesNotHaveMatchingPackageBoolean =
                        (null != result.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean() &&
                                searchReq.getCarSearchStrategy().getPackageBoolean() != result.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean());
                if (null == result.getCarProductList() || doesNotHaveCarProduct || null == searchReq.getCarSearchStrategy() || doesNotHaveMatchingPackageBoolean)
                {
                    continue;
                }

                final int size = result.getCarProductList().getCarProduct().size();
                final int min = 0, max = size - 1;
                return result.getCarProductList().getCarProduct().get(getRandomIndex(min, max));
            }
        }
        return null;
    }
    */


    protected int getRandomIndex(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return s_rand.nextInt(max - min + 1) + min;

    }

    public CarSupplyConnectivitySearchRequestType getSearchReq() {
        return searchReq;
    }

    public void setSearchReq(CarSupplyConnectivitySearchRequestType searchReq) {
        this.searchReq = searchReq;
    }

    public CarSupplyConnectivitySearchResponseType getSearchResp() {
        return searchResp;
    }

    public void setSearchResp(CarSupplyConnectivitySearchResponseType searchResp) {
        this.searchResp = searchResp;
    }

    public CarSupplyConnectivityGetDetailsRequestType getDetailsReq() {
        return detailsReq;
    }

    public void setDetailsReq(CarSupplyConnectivityGetDetailsRequestType detailsReq) {
        this.detailsReq = detailsReq;
    }

    public CarSupplyConnectivityGetDetailsResponseType getDetailsResp() {
        return detailsResp;
    }

    public void setDetailsResp(CarSupplyConnectivityGetDetailsResponseType detailsResp) {
        this.detailsResp = detailsResp;
    }

    public CarSupplyConnectivityGetCostAndAvailabilityRequestType getCostAndAvailReq() {
        return costAndAvailReq;
    }

    public void setCostAndAvailReq(CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailReq) {
        this.costAndAvailReq = costAndAvailReq;
    }

    public CarSupplyConnectivityGetCostAndAvailabilityResponseType getCostAndAvailResp() {
        return costAndAvailResp;
    }

    public void setCostAndAvailResp(CarSupplyConnectivityGetCostAndAvailabilityResponseType costAndAvailResp) {
        this.costAndAvailResp = costAndAvailResp;
    }

    public CarSupplyConnectivityGetReservationRequestType getGetReservationReq() {
        return getReservationReq;
    }

    public void setGetReservationReq(CarSupplyConnectivityGetReservationRequestType getReservationReq) {
        this.getReservationReq = getReservationReq;
    }

    public CarSupplyConnectivityGetReservationResponseType getGetReservationResp() {
        return getReservationResp;
    }

    public void setGetReservationResp(CarSupplyConnectivityGetReservationResponseType getReservationResp) {
        this.getReservationResp = getReservationResp;
    }

    public CarSupplyConnectivityCancelRequestType getCancelReq() {
        return cancelReq;
    }

    public void setCancelReq(CarSupplyConnectivityCancelRequestType cancelReq) {
        this.cancelReq = cancelReq;
    }

    public CarSupplyConnectivityCancelResponseType getCancelResp() {
        return cancelResp;
    }

    public void setCancelResp(CarSupplyConnectivityCancelResponseType cancelResp) {
        this.cancelResp = cancelResp;
    }

    public CarSupplyConnectivityReserveRequestType getReserveReq() {
        return reserveReq;
    }

    public void setReserveReq(CarSupplyConnectivityReserveRequestType reserveReq) {
        this.reserveReq = reserveReq;
    }

    public CarSupplyConnectivityReserveResponseType getReserveResp() {
        return reserveResp;
    }

    public void setReserveResp(CarSupplyConnectivityReserveResponseType reserveResp) {
        this.reserveResp = reserveResp;
    }
}
