package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;


import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.basetypes.defn.v4.SpecialRequestListType;
import com.expedia.e3.data.basetypes.defn.v4.SpecialRequestType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonTypeGenerator;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import expedia.om.supply.datatype.defn.v1.ConfiguredOfferDataType;
import expedia.om.supply.datatype.defn.v1.ConfiguredProductDataType;
import expedia.om.supply.messages.defn.v1.*;
import org.eclipse.jetty.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 8/30/2016.
 */
@SuppressWarnings("PMD")
public class CarbsOMReserveReqAndRespGenerator {

    private GetOrderProcessRequest getOrderProcessRequestType;
    private GetOrderProcessResponseType getOrderProcessResponseType;
    private CreateRecordRequest createRecordRequestType;
    private CreateRecordResponseType createRecordResponseType;
    private PreparePurchaseRequest preparePurchaseRequestType;
    private PreparePurchaseResponseType preparePurchaseResponseType;
    private CommitPreparePurchaseRequest commitPreparePurchaseRequestType;
    private CommitPreparePurchaseResponseType commitPreparePurchaseResponseType;
    private RollbackPreparePurchaseRequest rollbackPreparePurchaseRequestType;
    private RollbackPreparePurchaseResponseType rollbackPreparePurchaseResponseType;
    private CarECommerceSearchRequestType searchRequestType;
    private CarECommerceSearchResponseType searchResponseType;
    private CarECommerceSearchResponseType standaloneSearchResponseType;
    private CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType;
    private CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType;
    private CarECommerceGetDetailsRequestType getDetailsRequestType;
    private CarECommerceGetDetailsResponseType getDetailsResponseType;
    private CarProductType selectCarProduct;

    public CarbsOMReserveReqAndRespGenerator(CarECommerceSearchRequestType searchRequestType, CarECommerceSearchResponseType searchResponseType) {
        this.searchRequestType = searchRequestType;
        this.searchResponseType = searchResponseType;
    }


    public CarECommerceSearchResponseType getStandaloneSearchResponseType() {
        return standaloneSearchResponseType;
    }

    public void setStandaloneSearchResponseType(CarECommerceSearchResponseType standaloneSearchResponseType) {
        this.standaloneSearchResponseType = standaloneSearchResponseType;
    }


    public GetOrderProcessRequest createGetOrderProcessRequest(TestData testData) throws DataAccessException {
        GetOrderProcessRequest request = new GetOrderProcessRequest();
        //  List<TPIDToPoSAttributeMap> myTPIDToPoSAttributeMapList = CarsInventory.getTPIDToPoSAttributeMapByPOS(jurisdictionCountryCode, companyCode, managementUnitCode);
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType, testData.isSetHyphenInLanguage()));
        // PointOfSaleCustomerIdentifierType value = new PointOfSaleCustomerIdentifierType();
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));


        request.setConfiguredOfferData(getConfigOfferData(testData));
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());

        if (!CompareUtil.isObjEmpty(testData.getCarSpecialEquipmentCode()))
        {
            final CarSpecialEquipmentListType carSpecialEquipmnetListType = new CarSpecialEquipmentListType();
            carSpecialEquipmnetListType.setCarSpecialEquipment(new ArrayList<>());
            request.getConfiguredOfferData().getCarOfferData().getCarReservation().setCarSpecialEquipmentList(carSpecialEquipmnetListType);
            for(String code : testData.getCarSpecialEquipmentCode().split(","))
            {
                final CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                carSpecialEquipment.setCarSpecialEquipmentCode(code);
                carSpecialEquipmnetListType.getCarSpecialEquipment().add(carSpecialEquipment);
            }
        }
        if (!CompareUtil.isObjEmpty(testData.getSpecialEquipmentEnumType()))
        {
            final CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
            carVehicleOptionListType.setCarVehicleOption(new ArrayList<>());
            request.getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct().setCarVehicleOptionList(carVehicleOptionListType);
            for (String code : testData.getSpecialEquipmentEnumType().split(","))
            {
                final CarVehicleOptionType carVehicleOpt = new CarVehicleOptionType();
                carVehicleOpt.setCarVehicleOptionCategoryCode("special equipment");
                carVehicleOpt.setCarSpecialEquipmentCode(code);
                carVehicleOptionListType.getCarVehicleOption().add(carVehicleOpt);
            }
        }

        return request;
    }

    private ConfiguredOfferDataType getConfigOfferData(TestData testData) throws DataAccessException {
        MultiplierType pointOfSaleToPointOfSupplyExchangeRate = null;
        if(getCostAndAvailabilityResponseType.getCurrencyConversionRate() != null && getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate() != null){
            pointOfSaleToPointOfSupplyExchangeRate = getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate();
        }else{
            pointOfSaleToPointOfSupplyExchangeRate = new MultiplierType();
            pointOfSaleToPointOfSupplyExchangeRate.setDecimal(100000);
            pointOfSaleToPointOfSupplyExchangeRate.setDecimalPlaceCount(5);
        }

        CarOfferDataType carOfferDataType = CarCommonRequestGenerator.createCarOfferData(this.getSelectCarProduct(), searchRequestType,
                pointOfSaleToPointOfSupplyExchangeRate, testData);


        ConfiguredOfferDataType configuredOfferDataType = new ConfiguredOfferDataType();
        configuredOfferDataType.setType("CarOfferDataType");
        configuredOfferDataType.setNamespace("urn:expedia:om:supply:datatype:defn:v1");

        configuredOfferDataType.setCarOfferData(carOfferDataType);
        return configuredOfferDataType;
    }

    public CreateRecordRequest createCreateRecordRequest(TestData testData) throws DataAccessException {
        CreateRecordRequest request = new CreateRecordRequest();
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType, testData.isSetHyphenInLanguage()));
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setOrderIdentifier(CarCommonTypeGenerator.createOrderIdentifier(null, null));
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.createRecordRequestType = request;
        return request;
    }

    public PreparePurchaseRequest createPreparePurchaseRequest(TestData testData) throws DataAccessException {
        PreparePurchaseRequest request = new PreparePurchaseRequest();
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType, testData.isSetHyphenInLanguage()));
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setRecordLocator(createRecordResponseType.getRecordLocator());
        request.setOrderOperationCorrelationID(PojoXmlUtil.getRandomGuid());
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());

        //To validate CASSS-4544 CarVehicleOption in COPP is getting back all the special equipment instead of just the selected one
        ConfiguredProductDataType configProductData = getConfigProductData(testData);
        if(testData.getSpecialTestCasesParam().isOptionListSenerio())
        {
            CarVehicleOptionListType optionList = configProductData.getCarOfferData().getCarReservation().getCarProduct().getCarVehicleOptionList();
            if(null != optionList && optionList.getCarVehicleOption().size() > 1)
            {
                configProductData.getCarOfferData().getCarReservation().getCarProduct().getCarVehicleOptionList().
                        getCarVehicleOption().remove(optionList.getCarVehicleOption().size() -1);
            }
        }

        request.setConfiguredProductData(configProductData);


        //set error handling
        if(testData.getErrHandle() != null && String.valueOf(CarCommonEnumManager.InValidFildType.InvalidCDCode).equals(testData.getErrHandle().getErrorType())) {
            request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey().getCarRate().
                    setCorporateDiscountCode(testData.getErrHandle().getInvalidValue());
        }
        if(testData.getErrHandle() != null && String.valueOf(CarCommonEnumManager.InValidFildType.InvalidSpecialRequest).equals(testData.getErrHandle().getErrorType())) {
            final SpecialRequestListType specialRequestList = new SpecialRequestListType();
            specialRequestList.setSpecialRequest(new ArrayList<>());
            SpecialRequestType specialRequest = new SpecialRequestType();
            specialRequest.setSpecialRequestCategoryCode("1663");
            specialRequestList.getSpecialRequest().add(specialRequest);
            request.getConfiguredProductData().getCarOfferData().getCarReservation().setSpecialRequestList(specialRequestList);
        }

        //specail test
        if(!CompareUtil.isObjEmpty(testData.getSpecialTest())){
            if (testData.getSpecialTest().contains("CCCard")){
                //CC card
                //Force CC downstream to SS/SCS/GDS
                request.getConfiguredProductData().getCarOfferData().getCarLegacyBookingData().setReservationGuaranteeMethodID(1L);
                addCreditCardDetailsInPreparePurchaseRequest(request, testData.getNeedSpecialCreditCard(), testData.isNeedDescriptiveBillingInfo(), testData.isNeedEDIAndDBIData());
            }

           //AirFlight special character
            if (testData.getSpecialTest().contains("ProperHandleOfAirlineCode"))
            {
                //AirFlight
                String[] sCode = testData.getSpecialTest().split("\\|");
                AirFlightType airNode = new AirFlightType();
                airNode.setAirCarrierCode(sCode.length > 1 ? sCode[1] : "VT");
                airNode.setFlightNumber(sCode.length > 2 ? sCode[2] : "121");
                request.getConfiguredProductData().getCarOfferData().setAirFlight(airNode);
            }

            if (testData.getSpecialTest().contains("AirFlight")){
                final AirFlightType airNode = new AirFlightType();
                airNode.setAirCarrierCode("VT");
                airNode.setFlightNumber("121");
                request.getConfiguredProductData().getCarOfferData().setAirFlight(airNode);
            }

            if (testData.getSpecialTest().contains("SpecialEquipment")){
                //Special Equipment
                final CarVehicleOptionListType optionList = new CarVehicleOptionListType();
                final List<CarVehicleOptionType> carVehicleOptionList = new ArrayList<CarVehicleOptionType>();
                final CarVehicleOptionType carEquipment = new CarVehicleOptionType();
                optionList.setCarVehicleOption(carVehicleOptionList);
                carEquipment.setCarVehicleOptionCategoryCode("special equipment");
                carEquipment.setCarSpecialEquipmentCode("NVS");
                optionList.getCarVehicleOption().add(carEquipment);
                request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().setCarVehicleOptionList(optionList);
                request.getConfiguredProductData().getCarOfferData().getCarLegacyBookingData().setCarSpecialEquipmentMask(1);

            }
        }
        if (!CompareUtil.isObjEmpty(testData.getCarSpecialEquipmentCode()))
        {
            final CarSpecialEquipmentListType carSpecialEquipmnetListType = new CarSpecialEquipmentListType();
            carSpecialEquipmnetListType.setCarSpecialEquipment(new ArrayList<>());
            request.getConfiguredProductData().getCarOfferData().getCarReservation().setCarSpecialEquipmentList(carSpecialEquipmnetListType);
            for(String code : testData.getCarSpecialEquipmentCode().split(","))
            {
                final CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                carSpecialEquipment.setCarSpecialEquipmentCode(code);
                carSpecialEquipmnetListType.getCarSpecialEquipment().add(carSpecialEquipment);
            }
        }
        if (!CompareUtil.isObjEmpty(testData.getSpecialEquipmentEnumType()))
        {
            final CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
            carVehicleOptionListType.setCarVehicleOption(new ArrayList<>());
            request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().setCarVehicleOptionList(carVehicleOptionListType);
            for (String code : testData.getSpecialEquipmentEnumType().split(","))
            {
                final CarVehicleOptionType carVehicleOpt = new CarVehicleOptionType();
                carVehicleOpt.setCarVehicleOptionCategoryCode("special equipment");
                carVehicleOpt.setCarSpecialEquipmentCode(code);
                carVehicleOptionListType.getCarVehicleOption().add(carVehicleOpt);
            }
        }

        // build the Del and Col node from Location search API
        if ( null != testData.getTestScenarioSpecialHandleParam().getDeliveryAndCollectionAddress())
        {
            Map<String, AddressType> delAndAddMap = testData.getTestScenarioSpecialHandleParam().getDeliveryAndCollectionAddress();

            AddressType delAddress = null;
            AddressType colAddress = null;

            delAddress = delAndAddMap.get(CarCommonEnumManager.PICKLOCATIONKEY);
            colAddress = delAndAddMap.get(CarCommonEnumManager.DROPLOCATIONKEY);

            if (null != delAddress && testData.getTestScenarioSpecialHandleParam().isDeliveryAvailable())
            {
                final CustomerLocationType deliveryLocation = new CustomerLocationType();
                deliveryLocation.setPhone(createPhone());
                deliveryLocation.setAddress(delAddress);

                request.getConfiguredProductData().getCarOfferData().getCarReservation().setDeliveryLocation(deliveryLocation);
            }

            if ( null != colAddress && testData.getTestScenarioSpecialHandleParam().isCollectionAvailable())
            {
                final CustomerLocationType collectionLocation = new CustomerLocationType();
                collectionLocation.setPhone(createPhone());
                collectionLocation.setAddress(colAddress);
                request.getConfiguredProductData().getCarOfferData().getCarReservation().setCollectionLocation(collectionLocation);
            }
        }

        // for PlaceID scenarios, no need to build Address, only PlaceID in DeliveryLocation/CollectionLocation for the associated vendor is ok.
        if (null != testData.getTestScenarioSpecialHandleParam().getDeliveryPlaceID())
        {
            final CustomerLocationType deliveryLocation = new CustomerLocationType();
            deliveryLocation.setPhone(createPhone());
            deliveryLocation.setCustomerLocationCode(testData.getTestScenarioSpecialHandleParam().getDeliveryPlaceID());

            request.getConfiguredProductData().getCarOfferData().getCarReservation().setDeliveryLocation(deliveryLocation);
        }

        if (null != testData.getTestScenarioSpecialHandleParam().getCollectionPlaceID())
        {
            final CustomerLocationType collectionLocation = new CustomerLocationType();
            collectionLocation.setPhone(createPhone());
            collectionLocation.setCustomerLocationCode(testData.getTestScenarioSpecialHandleParam().getCollectionPlaceID());

            request.getConfiguredProductData().getCarOfferData().getCarReservation().setCollectionLocation(collectionLocation);
        }

        // for HomeDelivery scenario
        if (testData.getTestScenarioSpecialHandleParam().isDeliveryAvailable() && null == testData.getTestScenarioSpecialHandleParam().getDeliveryAndCollectionAddress())
        {
            final CustomerLocationType deliveryLocation = new CustomerLocationType();
            deliveryLocation.setPhone(createPhone());
            deliveryLocation.setAddress(createAddress());
            request.getConfiguredProductData().getCarOfferData().getCarReservation().setDeliveryLocation(deliveryLocation);
        }
        if (testData.getTestScenarioSpecialHandleParam().isCollectionAvailable() && null == testData.getTestScenarioSpecialHandleParam().getDeliveryAndCollectionAddress())
        {
            final CustomerLocationType customerLocation = new CustomerLocationType();
            customerLocation.setPhone(createPhone());
            customerLocation.setAddress(createAddress());
            request.getConfiguredProductData().getCarOfferData().getCarReservation().setCollectionLocation(customerLocation);
        }

        if (null != request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getReservationGuaranteeCategory()
                && CarCommonEnumManager.ReservationGuaranteeCategory.Required.name().equals(request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getReservationGuaranteeCategory()))
        {
            addCreditCardDetailsInPreparePurchaseRequest(request, testData.getNeedSpecialCreditCard(), testData.isNeedDescriptiveBillingInfo(), testData.isNeedEDIAndDBIData());
            request.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList().getTraveler().get(0).setPerson(request.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo().getCreditCardFormOfPayment().getPerson());
        }

        //Add loyalty details in Prepare Purchase call, follow loyalty number in LoyaltyProgramCategoryCode-LoyaltyProgramMembershipCode format (Car-43825675)
        if (testData.getNeedTravelerLoyalty()) {
            request.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList().getTraveler().get(0).setLoyaltyProgramList(CommonDataTypesGenerator.createLoyaltyProgramList(testData.getCarRate().getLoyaltyNum().split(",")[0]));
            if (testData.getNeedMultiTraveler()) {
                request.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList().getTraveler().get(1).setLoyaltyProgramList(CommonDataTypesGenerator.createLoyaltyProgramList(testData.getCarRate().getLoyaltyNum().split(",")[1]));
            }
            //Adding this to send different loyalty number if CarRate and Traveler both require loyalty
            if (testData.getNeedLoyaltyCard()) {
                request.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram().get(0).setLoyaltyProgramMembershipCode(testData.getCarRate().getLoyaltyNum().split(",")[0].split("-")[1].concat("abc"));
            }
        }

        if (StringUtil.isNotBlank(testData.getBillingNumber()))
        {
            //Worldspan billing number is sent in reference code
            if(testData.getScenarios().getServiceProviderID() == 1)
            {
                ReferenceType reference = new ReferenceType();
                reference.setReferenceCategoryCode("BillingNumber");
                reference.setReferenceCode(testData.getBillingNumber());
                request.getConfiguredProductData().getCarOfferData().getCarReservation().getReferenceList().getReference().add(reference);
            }
            else {
                PaymentInfoType paymentInfoType = request.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo();
                if (null == paymentInfoType) {
                    paymentInfoType = new PaymentInfoType();
                    request.getConfiguredProductData().getCarOfferData().getCarReservation().setPaymentInfo(paymentInfoType);
                }
                paymentInfoType.setBillingCode(testData.getBillingNumber());
            }
        }

        //PaymentInstrumentToken
        if(testData.isNeedPaymentInstrumentToken()){
            request.setPaymentDetails(new PaymentDetailsType());
            request.getPaymentDetails().setPaymentInstrumentToken("B7916E97-4C3C-4675-8C96-C0B5D9A9EE0E");
        }

        //Add DBI data for non-Airplus request
        if(!testData.getNeedSpecialCreditCard() && testData.isNeedDescriptiveBillingInfo())
        {
            buildDescBillingInfoListForBillingCode(request);
        }
        if(testData.getdifferentCostInPreparePurchased()) {
            for (CostType costType : request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCostList().getCost()) {
                if (costType.getFinanceCategoryCode().equals("Total") && testData.getdifferentCostInPreparePurchased()) {
                    costType.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(costType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / 2);
                    //break; //Meichun: when currency conversation exist, we need to upate both POS and POSu
                }
            }
        }

        if(testData.getdifferentPriceInPreparePurchased()) {
            for (PriceType priceType : request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getPriceList().getPrice()) {
                if (priceType.getFinanceCategoryCode().equals("Total")) {
                    priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / 2);

                }
            }
        }

        this.preparePurchaseRequestType = request;

        return request;
    }

    //	<ns4:DescriptiveBillingInfoList>
    //<ns4:DescriptiveBillingInfo ns4:Key="EDIDATA" ns4:Value="lolo,GRETER Laurent,ft1,"></ns4:DescriptiveBillingInfo>
    //</ns4:DescriptiveBillingInfoList>
    private void buildDescBillingInfoListForBillingCode(PreparePurchaseRequest reserveRequest)
    {
        final DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        addDescBillingInfo(descBillingInfoList, "EDIDATA", "lolo,GRETER Laurent,ft1,");
        if(null == reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo())
        {
            reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().setPaymentInfo(new PaymentInfoType());
        }
        reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo().
                setDescriptiveBillingInfoList(descBillingInfoList);
    }

    private void buildEDIAndDescBillingInfoListForAirPlus(PreparePurchaseRequest reserveRequest)
    {
        buildDescBillingInfoListForBillingCode(reserveRequest);
        reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo().
                getDescriptiveBillingInfoList().getDescriptiveBillingInfo().addAll(getDescBillingInfoListForAirPlus().getDescriptiveBillingInfo());
    }

    private void buildDescBillingInfoListForAirPlus(PreparePurchaseRequest reserveRequest)
    {
        if(null == reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo())
        {
            reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().setPaymentInfo(new PaymentInfoType());
        }
        reserveRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getPaymentInfo().
                setDescriptiveBillingInfoList(getDescBillingInfoListForAirPlus());
    }

    private DescriptiveBillingInfoListType getDescBillingInfoListForAirPlus()
    {
        DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        addDescBillingInfo(descBillingInfoList, "CLS", "CAR");
        addDescBillingInfo(descBillingInfoList, "PK", "val1");
        addDescBillingInfo(descBillingInfoList, "DS", "val2");
        addDescBillingInfo(descBillingInfoList, "KS", "val3");
        addDescBillingInfo(descBillingInfoList, "AE", "val4");
        addDescBillingInfo(descBillingInfoList, "IK", "val5");
        addDescBillingInfo(descBillingInfoList, "BD", "val6");
        addDescBillingInfo(descBillingInfoList, "PR", "val7");
        addDescBillingInfo(descBillingInfoList, "AU","val8");
        addDescBillingInfo(descBillingInfoList, "AK","val9");
        addDescBillingInfo(descBillingInfoList, "RZ","val10");
        return descBillingInfoList;
    }

    private void addDescBillingInfo(DescriptiveBillingInfoListType descBillingInfoList, String key, String value)
    {
        final DescriptiveBillingInfoType descBillingInfo = new DescriptiveBillingInfoType();
        descBillingInfo.setKey(key);
        descBillingInfo.setValue(value);
        descBillingInfoList.getDescriptiveBillingInfo().add(descBillingInfo);
    }


    private void addCreditCardDetailsInPreparePurchaseRequest(PreparePurchaseRequest request, boolean isSpecialCreditCard,
                                                              boolean needDescriptiveBillingInfo, boolean needEDIAndDBIData)
    {
        final PaymentInfoType paymentInfo = new PaymentInfoType();
        CreditCardType creditCard = null;
        if(isSpecialCreditCard){
            creditCard = CarCommonRequestGenerator.createCreditCard(RequestDefaultValues.AIRPLUS_CARD_SUPPLIER_CODE, RequestDefaultValues.AIRPLUS_CARD_NUMBER_ENCRYPTED, RequestDefaultValues.EXPIRATION_DATE, null, null, RequestDefaultValues.MASKED_CREDIT_CARD_NUMBER);
        }
        final CreditCardFormOfPaymentType ccCard = CarCommonRequestGenerator.createCreditCardFormOfPayment(creditCard,null,null);
        paymentInfo.setCreditCardFormOfPayment(ccCard);
        request.getConfiguredProductData().getCarOfferData().getCarReservation().setPaymentInfo(paymentInfo);
        if(isSpecialCreditCard && needDescriptiveBillingInfo){
            buildDescBillingInfoListForAirPlus(request);
        }
        if(isSpecialCreditCard && needEDIAndDBIData){
            buildEDIAndDescBillingInfoListForAirPlus(request);
        }

    }
    private PhoneType createPhone() {
        final PhoneType phone = new PhoneType();
        phone.setPhoneAreaCode("034");
        phone.setPhoneCountryCode("222");
        phone.setPhoneNumber("1234567890");

        return phone;
    }

    private AddressType createAddress() {
        final AddressType address = new AddressType();
        address.setFirstAddressLine("QUAI PIERRE CORNEILLE");
        address.setCityName("ROUEN");
        address.setPostalCode("76000");
        address.setCountryAlpha3Code("FRA");
        return address;
    }

    private ConfiguredProductDataType getConfigProductData(TestData testData) throws DataAccessException {
        MultiplierType pointOfSaleToPointOfSupplyExchangeRate = null;
        if(getCostAndAvailabilityResponseType.getCurrencyConversionRate() != null && getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate() != null){
            pointOfSaleToPointOfSupplyExchangeRate = getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate();
        }else{
            pointOfSaleToPointOfSupplyExchangeRate = new MultiplierType();
            pointOfSaleToPointOfSupplyExchangeRate.setDecimal(100000);
            pointOfSaleToPointOfSupplyExchangeRate.setDecimalPlaceCount(5);
        }

        CarOfferDataType carOfferDataType = CarCommonRequestGenerator.createCarOfferData(this.getSelectCarProduct(), searchRequestType,
                pointOfSaleToPointOfSupplyExchangeRate, testData);

        ConfiguredProductDataType configuredProductDataType = new ConfiguredProductDataType();
        configuredProductDataType.setType("CarOfferDataType");
        configuredProductDataType.setNamespace("urn:expedia:e3:data:cartypes:defn:v5");
        configuredProductDataType.setCarOfferData(carOfferDataType);
        return configuredProductDataType;
    }

    public CommitPreparePurchaseRequest createCommitPreparePurchaseRequest(TestData testData) throws DataAccessException {
        CommitPreparePurchaseRequest request = new CommitPreparePurchaseRequest();
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType, testData.isSetHyphenInLanguage()));
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setRecordLocator(createRecordResponseType.getRecordLocator());
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.commitPreparePurchaseRequestType = request;
        return request;
    }

    public RollbackPreparePurchaseRequest createRollbackPreparePurchaseRequest(TestData testData) throws DataAccessException {
        RollbackPreparePurchaseRequest request = new RollbackPreparePurchaseRequest();
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType, testData.isSetHyphenInLanguage()));
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setRecordLocator(createRecordResponseType.getRecordLocator());
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.rollbackPreparePurchaseRequestType = request;
        return request;
    }


    public GetOrderProcessRequest getGetOrderProcessRequest() {
        return getOrderProcessRequestType;
    }

    public void setGetOrderProcessRequest(GetOrderProcessRequest getOrderProcessRequestType) {
        this.getOrderProcessRequestType = getOrderProcessRequestType;
    }

    public GetOrderProcessResponseType getGetOrderProcessResponseType() {
        return getOrderProcessResponseType;
    }

    public void setGetOrderProcessResponseType(GetOrderProcessResponseType getOrderProcessResponseType) {
        this.getOrderProcessResponseType = getOrderProcessResponseType;
    }

    public GetOrderProcessRequest getGetOrderProcessRequestType() {
        return getOrderProcessRequestType;
    }

    public void setGetOrderProcessRequestType(GetOrderProcessRequest getOrderProcessRequestType) {
        this.getOrderProcessRequestType = getOrderProcessRequestType;
    }

    public CreateRecordRequest getCreateRecordRequestType() {
        return createRecordRequestType;
    }

    public void setCreateRecordRequestType(CreateRecordRequest createRecordRequestType) {
        this.createRecordRequestType = createRecordRequestType;
    }

    public PreparePurchaseRequest getPreparePurchaseRequestType() {
        return preparePurchaseRequestType;
    }

    public void setPreparePurchaseRequestType(PreparePurchaseRequest preparePurchaseRequestType) {
        this.preparePurchaseRequestType = preparePurchaseRequestType;
    }

    public CommitPreparePurchaseRequest getCommitPreparePurchaseRequestType() {
        return commitPreparePurchaseRequestType;
    }

    public void setCommitPreparePurchaseRequestType(CommitPreparePurchaseRequest commitPreparePurchaseRequestType) {
        this.commitPreparePurchaseRequestType = commitPreparePurchaseRequestType;
    }

    public RollbackPreparePurchaseRequest getRollbackPreparePurchaseRequestType() {
        return rollbackPreparePurchaseRequestType;
    }

    public void setRollbackPreparePurchaseRequestType(RollbackPreparePurchaseRequest rollbackPreparePurchaseRequestType) {
        this.rollbackPreparePurchaseRequestType = rollbackPreparePurchaseRequestType;
    }

    public CreateRecordResponseType getCreateRecordResponseType() {
        return createRecordResponseType;
    }

    public void setCreateRecordResponseType(CreateRecordResponseType createRecordResponseType) {
        this.createRecordResponseType = createRecordResponseType;
    }


    public PreparePurchaseResponseType getPreparePurchaseResponseType() {
        return preparePurchaseResponseType;
    }

    public void setPreparePurchaseResponseType(PreparePurchaseResponseType preparePurchaseResponseType) {
        this.preparePurchaseResponseType = preparePurchaseResponseType;
    }


    public CommitPreparePurchaseResponseType getCommitPreparePurchaseResponseType() {
        return commitPreparePurchaseResponseType;
    }

    public void setCommitPreparePurchaseResponseType(CommitPreparePurchaseResponseType commitPreparePurchaseResponseType) {
        this.commitPreparePurchaseResponseType = commitPreparePurchaseResponseType;
    }


    public RollbackPreparePurchaseResponseType getRollbackPreparePurchaseResponseType() {
        return rollbackPreparePurchaseResponseType;
    }

    public void setRollbackPreparePurchaseResponseType(RollbackPreparePurchaseResponseType rollbackPreparePurchaseResponseType) {
        this.rollbackPreparePurchaseResponseType = rollbackPreparePurchaseResponseType;
    }

    public CarECommerceSearchRequestType getSearchRequestType() {
        return searchRequestType;
    }

    public void setSearchRequestType(CarECommerceSearchRequestType searchRequestType) {
        this.searchRequestType = searchRequestType;
    }

    public CarECommerceSearchResponseType getSearchResponseType() {
        return searchResponseType;
    }

    public void setSearchResponseType(CarECommerceSearchResponseType searchResponseType) {
        this.searchResponseType = searchResponseType;
    }

    public CarECommerceGetCostAndAvailabilityRequestType getGetCostAndAvailabilityRequestType() {
        return getCostAndAvailabilityRequestType;
    }

    public void setGetCostAndAvailabilityRequestType(CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType) {
        this.getCostAndAvailabilityRequestType = getCostAndAvailabilityRequestType;
    }

    public CarECommerceGetCostAndAvailabilityResponseType getGetCostAndAvailabilityResponseType() {
        return getCostAndAvailabilityResponseType;
    }

    public void setGetCostAndAvailabilityResponseType(CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType) {
        this.getCostAndAvailabilityResponseType = getCostAndAvailabilityResponseType;
    }

    public CarECommerceGetDetailsRequestType getGetDetailsRequestType()
    {
        return getDetailsRequestType;
    }

    public void setGetDetailsRequestType(CarECommerceGetDetailsRequestType getDetailsRequestType)
    {
        this.getDetailsRequestType = getDetailsRequestType;
    }

    public CarECommerceGetDetailsResponseType getGetDetailsResponseType()
    {
        return getDetailsResponseType;
    }

    public void setGetDetailsResponseType(CarECommerceGetDetailsResponseType getDetailsResponseType)
    {
        this.getDetailsResponseType = getDetailsResponseType;
    }

    public CarProductType getSelectCarProduct() {
        return selectCarProduct;
    }

    public void setSelectCarProduct(CarProductType selectCarProduct) {
        this.selectCarProduct = selectCarProduct;
    }
}
