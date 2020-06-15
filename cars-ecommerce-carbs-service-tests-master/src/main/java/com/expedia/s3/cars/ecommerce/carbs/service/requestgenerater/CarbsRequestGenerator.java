package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;

import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarMarkupRuleInfoType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationRequestType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationResponseType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveRequestType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.GDSPCarType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by fehu on 8/24/2016.
 */

@SuppressWarnings(value={"PMD", "CPD-START"})

public class CarbsRequestGenerator {
    private static final Random s_rand = new Random(System.nanoTime());

    private CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType;
    private CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType;
    private CarECommerceSearchRequestType searchRequestType;
    private CarECommerceSearchResponseType searchResponseType;
    private CarECommerceGetDetailsRequestType getDetailsRequestType;
    private CarECommerceGetDetailsResponseType getDetailsResponseType;
    private CarECommerceGetReservationRequestType getReservationRequestType;
    private CarECommerceGetReservationResponseType getReservationResponseType;
    private CarECommerceReserveRequestType reserveRequestType;
    private CarECommerceReserveResponseType reserveResponseType;
    private CarECommerceCancelRequestType  cancelRequestType;
    private  CarECommerceCancelResponseType cancelResponseType;
    private CarProductType selectedCarProduct;

    private TestData testData;

    public CarbsRequestGenerator(CarECommerceSearchRequestType searchRequestType, CarECommerceSearchResponseType searchResponseType, TestData testData) {
        this.searchRequestType = searchRequestType;
        this.searchResponseType = searchResponseType;
        this.testData = testData;
    }

    public CarECommerceReserveRequestType createCarbsReserveRequest() throws DataAccessException {

        CarECommerceReserveRequestType req = new CarECommerceReserveRequestType();
        if (selectedCarProduct == null)
            selectedCarProduct = getCarProduct(searchResponseType, testData);
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchRequestType.getMessageInfo().getMessageVersion()));
        req.setPackageBoolean(false);
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());
        Boolean packageBoolean = !CarCommonRequestGenerator.getStandaloneBoolByPurchaseTypeMask(
                searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());
        req.setPackageBoolean(packageBoolean);
        selectedCarProduct.getCarInventoryKey().setPackageBoolean(packageBoolean);
        req.setCarProduct(selectedCarProduct);
        req.setCommitBoolean(false);
        req.setLegacyReservationData(CarCommonRequestGenerator.createLegacyReservationData(false, true, false));
        //// TODO: 8/25/2016
        req.setPointOfSaleToPointOfSupplyExchangeRate(null);
        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());

        req.setTravelerList(CommonDataTypesGenerator.createTravelerList(false));
        req.setCustomer(CommonDataTypesGenerator.createCustomer());

        req.setReferenceList(CarCommonRequestGenerator.createReferenceList(CarCommonRequestGenerator.createTRLReference(searchRequestType.getAuditLogTrackingData().getAuditLogTPID(),
                searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID())));

        req.setSmokingBoolean(false);

        this.reserveRequestType = req;
        return req;
    }
    public CarECommerceGetDetailsRequestType createCarbsDetailsRequest() throws DataAccessException {
        CarECommerceGetDetailsRequestType req = new CarECommerceGetDetailsRequestType();
        req.setClientCode(searchRequestType.getClientCode());

        req.setMessageInfo(CommonDataTypesGenerator.buildMessageInfo(getMessageName(req), searchRequestType.getMessageInfo()));

        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());

        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setDisableCostListProcessingBoolean(false);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());

        req.setCarProductList(getCarProductList());
        this.getDetailsRequestType = req;
        return req;
    }

    public CarECommerceGetDetailsRequestType createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID (TestData testData) throws DataAccessException
    {
        CarECommerceGetDetailsRequestType req = new CarECommerceGetDetailsRequestType();
        req.setClientCode(searchRequestType.getClientCode());
        req.setMessageInfo(CommonDataTypesGenerator.buildMessageInfo(getMessageName(req), searchRequestType.getMessageInfo()));
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());

        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setDisableCostListProcessingBoolean(false);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());

        if(null != this.selectedCarProduct)
        {
            req.setCarProductList(new CarProductListType());
            req.getCarProductList().setCarProduct(new ArrayList<>());
            req.getCarProductList().getCarProduct().add(this.selectedCarProduct);
        }
        else {
            req.setCarProductList(selectCarByBusinessModelIDAndServiceProviderID(testData, false));
        }

        if (null != req.getCarProductList() && null != req.getCarProductList().getCarProduct() && !req.getCarProductList().getCarProduct().isEmpty())
        {
            this.setSelectedCarProduct(req.getCarProductList().getCarProduct().get(0));
        }

        this.getDetailsRequestType = req;
        return req;
    }

    /**
     *
     * @param param
     * @param rateCodeExistInDBFilter true: will filter the rate code exist in database, false will do not do filter.
     * @return
     * @throws DataAccessException
     */
    public CarECommerceGetDetailsRequestType createCarbsDetailsRequestByBusinessModelIDAndServiceProviderIDAndRateCodeExistInDBFilter
    (TestData param, boolean rateCodeExistInDBFilter) throws DataAccessException {
        CarECommerceGetDetailsRequestType req = new CarECommerceGetDetailsRequestType();
        req.setClientCode(searchRequestType.getClientCode());
        req.setMessageInfo(CommonDataTypesGenerator.buildMessageInfo(getMessageName(req), searchRequestType.getMessageInfo()));
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());

        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setDisableCostListProcessingBoolean(false);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());

        if(null != this.selectedCarProduct)
        {
            req.setCarProductList(new CarProductListType());
            req.getCarProductList().setCarProduct(new ArrayList<>());
            req.getCarProductList().getCarProduct().add(this.selectedCarProduct);
        }
        else {
            req.setCarProductList(selectCarByBusinessModelIDAndServiceProviderID(param, rateCodeExistInDBFilter));
        }
        this.getDetailsRequestType = req;
        return req;
    }

    public CarECommerceGetReservationRequestType  createCarbsGetReservationRequest() {
        CarECommerceGetReservationRequestType  req = new CarECommerceGetReservationRequestType ();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchRequestType.getMessageInfo().getMessageVersion()));
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());

        req.setReferenceList(reserveRequestType.getReferenceList());
        req.setClientCode(reserveRequestType.getClientCode());

        this.getReservationRequestType = req;
        return req;
    }

    public CarECommerceReserveRequestType  createCarbsFirstReserveRequest() throws DataAccessException {

        CarECommerceReserveRequestType  req = new CarECommerceReserveRequestType ();
        if (selectedCarProduct == null)
            selectedCarProduct = getCarProduct(searchResponseType, testData);
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                searchRequestType.getMessageInfo().getMessageVersion()));
        req.setPackageBoolean(false);
        req.setClientCode(searchRequestType.getClientCode());
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());
        Boolean packageBoolean = !CarCommonRequestGenerator.getStandaloneBoolByPurchaseTypeMask(
                searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());
        req.setPackageBoolean(packageBoolean);
        selectedCarProduct.getCarInventoryKey().setPackageBoolean(packageBoolean);
        req.setCarProduct(selectedCarProduct);
        req.setCommitBoolean(false);
        req.setLegacyReservationData(CarCommonRequestGenerator.createLegacyReservationData(false,true,false));

        // If there isn't PointOfSaleToPointOfSupplyExchangeRate return in response, set decimal and decimalPlaceCount as null.
        setExchangeRate(req);
        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());

        req.setTravelerList(CommonDataTypesGenerator.createTravelerList(false));
        req.setCustomer(CommonDataTypesGenerator.createCustomer());

        req.setReferenceList(CarCommonRequestGenerator.createReferenceList(CarCommonRequestGenerator.createTRLReference(searchRequestType.getAuditLogTrackingData().getAuditLogTPID(),
                searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID())));

        req.setSmokingBoolean(false);

        this.reserveRequestType = req;
        return req;
    }

    private void setExchangeRate(CarECommerceReserveRequestType req) {
        if (null != getCostAndAvailabilityResponseType && null != getCostAndAvailabilityResponseType.getCurrencyConversionRate()
               )
        {
            Integer supplyExchangeRateDecimal = getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate().getDecimal();
            Long supplyExchangeRateDecimalPlaceCount = getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate().getDecimalPlaceCount();

            if (("0").equals(supplyExchangeRateDecimal))
            {
                supplyExchangeRateDecimal = null;
            }
            if (0l == supplyExchangeRateDecimalPlaceCount)
            {
                supplyExchangeRateDecimalPlaceCount = null;
            }

            req.setPointOfSaleToPointOfSupplyExchangeRate(createPointOfSaleToPointOfSupplyExchangeRateForReserve(supplyExchangeRateDecimal, supplyExchangeRateDecimalPlaceCount));

        }
        else {
            req.setPointOfSaleToPointOfSupplyExchangeRate(null);
        }
    }

    private static MultiplierType createPointOfSaleToPointOfSupplyExchangeRateForReserve(Integer supplyExchangeRateDecimal, Long supplyExchangeRateDecimalPlaceCount)
    {
        MultiplierType pointOfSaleToPointOfSupplyExchangeRate = new MultiplierType();
        pointOfSaleToPointOfSupplyExchangeRate.setDecimal(null != supplyExchangeRateDecimal ? supplyExchangeRateDecimal : 100000);
        pointOfSaleToPointOfSupplyExchangeRate.setDecimalPlaceCount(null != supplyExchangeRateDecimalPlaceCount ? supplyExchangeRateDecimalPlaceCount : 5);

        return pointOfSaleToPointOfSupplyExchangeRate;
    }
    public  CarECommerceReserveRequestType createCarbsSecondReserveRequest(CarECommerceReserveResponseType reserveResponseType) {

        CarECommerceReserveRequestType carBSSecondReserveRequest = this.getReserveRequestType();
        carBSSecondReserveRequest.setCommitBoolean(true);
        carBSSecondReserveRequest.setClientCode(searchRequestType.getClientCode());
        List<ReferenceType> referenceArrayList = reserveResponseType.getCarReservation().getReferenceList().getReference();

        for (int i = 0; i < referenceArrayList.size(); i++) {
            if ("Vendor".equals(referenceArrayList.get(i).getReferenceCategoryCode())) {
                carBSSecondReserveRequest.getReferenceList().getReference().add(referenceArrayList.get(0));
                break;
            }
        }
        if (carBSSecondReserveRequest.getCarReservation() == null)
            carBSSecondReserveRequest.setCarReservation(new CarReservationType());
        carBSSecondReserveRequest.getCarReservation().setCarProduct(reserveResponseType.getCarReservation().getCarProduct());
        carBSSecondReserveRequest.getCarReservation().setTravelerList(reserveResponseType.getCarReservation().getTravelerList());
        carBSSecondReserveRequest.getCarReservation().setCarRateDetail(reserveResponseType.getCarReservation().getCarRateDetail());
        if(carBSSecondReserveRequest.getCarReservation().getReferenceList() == null)
            carBSSecondReserveRequest.getCarReservation().setReferenceList(new ReferenceListType());
        carBSSecondReserveRequest.getCarReservation().getReferenceList().setReference(reserveResponseType.getCarReservation().getReferenceList().getReference());
        carBSSecondReserveRequest.getCarReservation().setBookingStateCode(null);

        //firstPhaseRsp.CarReservation not returned pricelist, so we get the value from firstPhaseReq.CarProduct
        carBSSecondReserveRequest.getCarReservation().getCarProduct().setPriceList(this.getReserveRequestType().getCarProduct().getPriceList());

        carBSSecondReserveRequest.setDisableCostListProcessingBoolean(false);

        return carBSSecondReserveRequest;
    }

    public CarECommerceGetCostAndAvailabilityRequestType  createCarbsCostAndAvailRequest() throws DataAccessException {
        CarECommerceGetCostAndAvailabilityRequestType  req = new CarECommerceGetCostAndAvailabilityRequestType ();
        req.setClientCode(searchRequestType.getClientCode());

        req.setMessageInfo(CommonDataTypesGenerator.buildMessageInfo(getMessageName(req), searchRequestType.getMessageInfo()));

        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());
        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());
        req.setDisableCostListProcessingBoolean(false);
        req.setCarProductList(getCarProductList());
        this.getCostAndAvailabilityRequestType = req;
        return req;
    }

    public CarECommerceGetCostAndAvailabilityRequestType createCarbsCostAndAvailRequestByBusinessModelIDAndServiceProviderID(TestData param) throws DataAccessException {
        CarECommerceGetCostAndAvailabilityRequestType req = new CarECommerceGetCostAndAvailabilityRequestType();
        req.setClientCode(searchRequestType.getClientCode());
        req.setMessageInfo(CommonDataTypesGenerator.buildMessageInfo(getMessageName(req), searchRequestType.getMessageInfo()));
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setAuditLogTrackingData(searchRequestType.getAuditLogTrackingData());
        String m_currencyCode = searchRequestType.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        req.setCurrencyCode(m_currencyCode);
        req.setPurchaseTypeMask(searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask());
        req.setDisableCostListProcessingBoolean(false);

        //make sure use same car in whole action.
        if(null != this.getSelectedCarProduct())
        {
            CarProductListType carList = new CarProductListType();
            List<CarProductType> listOfCars = new ArrayList<>();
            carList.setCarProduct(listOfCars);
            listOfCars.add(this.getSelectedCarProduct());
            req.setCarProductList(carList);
        }
        else
        {
            req.setCarProductList(selectCarByBusinessModelIDAndServiceProviderID(param, false));

        }
        this.getCostAndAvailabilityRequestType = req;
        return req;
    }

    public CarECommerceCancelRequestType  createCarbsCancelRequest() {

        CarECommerceCancelRequestType  req = new CarECommerceCancelRequestType ();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(getMessageName(req),
                reserveRequestType.getMessageInfo().getMessageVersion()));
        req.setSiteMessageInfo(searchRequestType.getSiteMessageInfo());
        req.setClientCode(searchRequestType.getClientCode());

        req.setAuditLogTrackingData(reserveRequestType.getAuditLogTrackingData());
        req.setTravelPurposeCode("");
        CarReservationType carReservation = reserveResponseType.getCarReservation();

        carReservation.getCarProduct().getCarPickupLocation().getCarLocationKey().setCarLocationCategoryCode(
                reserveRequestType.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarLocationCategoryCode());
        carReservation.getCarProduct().getCarPickupLocation().getCarLocationKey().setLocationCode(
                reserveRequestType.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode());
        carReservation.getCarProduct().getCarPickupLocation().getCarLocationKey().setSupplierRawText(
                reserveRequestType.getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getSupplierRawText());

        req.setCarReservation(carReservation);
        req.setCommitBoolean(true);
        req.setRollbackBoolean(false);
        req.setProcessRefundBoolean(true);
        this.cancelRequestType = req;
        return req;
    }


    private CarProductListType getCarProductList() throws DataAccessException {
        CarProductListType carList = new CarProductListType();
        List<CarProductType> listOfCars = new ArrayList<CarProductType>();
        carList.setCarProduct(listOfCars);
        CarProductType carProductType = getCarProduct(searchResponseType, testData);
        if (null != carProductType.getCarInventoryKey() && searchRequestType.getCarECommerceSearchStrategy().getPackageBoolean() != null) {
            carProductType.getCarInventoryKey().setPackageBoolean(searchRequestType.getCarECommerceSearchStrategy().getPackageBoolean());
        }
        listOfCars.add(carProductType);
        return carList;
    }

    /**
     *
     * @param testData
     * @param rateCodeExistInDBFilter true: will filter the rate code exist in database, false will do not do filter.
     * @return
     * @throws DataAccessException
     */
    private CarProductListType selectCarByBusinessModelIDAndServiceProviderID(TestData testData, boolean rateCodeExistInDBFilter) throws DataAccessException {
        CarProductListType carList = new CarProductListType();
        List<CarProductType> listOfCars = new ArrayList<>();
        carList.setCarProduct(listOfCars);
        CarProductType carProductType = getCarProductByBusinessModelIDAndServiceProviderID(testData, rateCodeExistInDBFilter);
        if(carProductType != null) {
            if(carProductType.getCarInventoryKey() == null)
                carProductType.setCarInventoryKey(new CarInventoryKeyType());
            if(null == searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask())
            {
                carProductType.getCarInventoryKey().setPackageBoolean(searchRequestType.getCarECommerceSearchStrategy().getPackageBoolean());
            }
           else {
                carProductType.getCarInventoryKey().setPackageBoolean(!CarCommonRequestGenerator.getStandaloneBoolByPurchaseTypeMask(
                        searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask()));
            }
        }
        else
        {
            Assert.fail("Not find expect car!");
        }
        listOfCars.add(carProductType);
        return carList;
    }

    public static boolean getStandaloneBoolByPurchaseTypeMask(long purchaseTypeMask)
    {

        if (purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.HCPackage.getPurchaseType() || purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.FCPackage.getPurchaseType()||
                purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.FHCPackage.getPurchaseType() || purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.TCPackage.getPurchaseType()
                || purchaseTypeMask == CarCommonEnumManager.PurchaseTypeMask.THCPackage.getPurchaseType())
        {
           return false;
        }

        return true;
    }
/*
    private SiteMessageInfoType getSiteMessageInfo(PointOfSaleKeyType pointOfSaleKeyType)
    {
        SiteMessageInfoType siteMessageInfoType = new SiteMessageInfoType();
        siteMessageInfoType.setPointOfSaleKey(pointOfSaleKeyType);
        SiteKeyType siteKeyType = new SiteKeyType();
        siteKeyType.setSiteID(1);
        siteMessageInfoType.setSiteKey(siteKeyType);
        return siteMessageInfoType;

    }
    */

    private String getMessageName(Object requestMessage) {
        String bindingElementName = requestMessage.getClass().getSimpleName();
        // trim "Type" from the end of the name
        if (bindingElementName.endsWith("Type")) {
            bindingElementName = bindingElementName.substring(0, bindingElementName
                    .lastIndexOf("Type"));
        }
        return bindingElementName;
    }

    public CarProductType selectCarByBusinessModelAndServiceProviderIDFromCarSearchResultList(CarSearchResultListType carSearchResultList,
                                                                                              TestData testData) throws DataAccessException {
        CarProductType carProductType = null;
        List<CarProductType> carProductList  = selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(carSearchResultList, testData );

        if (CollectionUtils.isNotEmpty(carProductList)) {
            carProductType = carProductList.get(getRandomIndex(0, carProductList.size() - 1));
        }


        return carProductType;
    }


    public List<CarProductType> selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(CarSearchResultListType carSearchResultList,
                                                                                              TestData testData) throws DataAccessException {
        List<CarProductType> carProductList = new ArrayList<>();
        Set<Long> supplySubsetIdList = new HashSet<>();

        //region Filter with GDS service and bussiness
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        //filter by serviceProviderID
        Assert.assertNotNull(carSearchResultList.getCarSearchResult(),"CarSearchResult should not null");
        for (final CarSearchResultType carSearchResult : carSearchResultList.getCarSearchResult()) {
            Assert.assertNotNull(carSearchResult.getCarProductList().getCarProduct(), "CarProductList should not be empty");

            //collect supply subset id to query
            supplySubsetIdList.clear();
            for (final CarProductType car : carSearchResult.getCarProductList().getCarProduct()) {
                supplySubsetIdList.add(car.getCarInventoryKey().getSupplySubsetID());
            }

            //get serviceId from supplyId
            Map<Long,Long> supplyCostVsServiceIdMap=carsInventoryDataSource.getServiceIDFromSupplySubsetIDCost(supplySubsetIdList);
            Map<Long,Long> supplyAvailVsServiceIdMap=carsInventoryDataSource.getServiceIDFromSupplySubsetIDAvail(supplySubsetIdList);

            for (final CarProductType car : carSearchResult.getCarProductList().getCarProduct()) {
                if (testData.getScenarios().getServiceProviderID() == supplyCostVsServiceIdMap.get(car.getCarInventoryKey().getSupplySubsetID())
                        || testData.getScenarios().getServiceProviderID() == supplyAvailVsServiceIdMap.get(car.getCarInventoryKey().getSupplySubsetID()) ) {
                    carProductList.add(car);
                }
            }
        }

        //filter by BusinessModelID
        if (!carProductList.isEmpty()) {
            Set<Long> carItemList = new HashSet<>();
            carProductList.forEach( item -> carItemList.add(item.getCarInventoryKey().getCarItemID()));
            final List<CarProductType> carProductSatisfiedProviderList = new ArrayList<CarProductType>();
            carProductSatisfiedProviderList.addAll(carProductList);

            Map<Long, Integer> carBusinessModelIDFromCarItem = carsInventoryDataSource.getCarBusinessModelIDFromCarItem(carItemList);
            for (final CarProductType car : carProductSatisfiedProviderList) {
                if (carBusinessModelIDFromCarItem.get(car.getCarInventoryKey().getCarItemID()) != testData.getScenarios().getBusinessModel()) {
                    carProductList.remove(car);
                }
            }
        }


        //filter for prepaid car
        if ("N".equalsIgnoreCase(testData.getNeedPrepaidCar())) {
            final List<CarProductType> carProductSatisfiedProviderList = new ArrayList<CarProductType>();
            carProductSatisfiedProviderList.addAll(carProductList);
            for (CarProductType carProductType : carProductSatisfiedProviderList) {
                if (carProductType.getPrePayBoolean()) {
                    carProductList.remove(carProductType);
                }
            }
        }
        if ("Y".equalsIgnoreCase(testData.getNeedPrepaidCar())) {
            final List<CarProductType> carProductSatisfiedProviderList = new ArrayList<CarProductType>();
            carProductSatisfiedProviderList.addAll(carProductList);
            for (CarProductType carProductType : carProductSatisfiedProviderList) {
                if (!carProductType.getPrePayBoolean()) {
                    carProductList.remove(carProductType);
                }
            }
        }

        if (null != testData.getSpecificCarCategoryCode()) {
            final List<CarProductType> carProductSatisfiedProviderList = new ArrayList<CarProductType>();
            carProductSatisfiedProviderList.addAll(carProductList);
            for (CarProductType carProductType : carProductSatisfiedProviderList) {
                if (testData.getSpecificCarCategoryCode() != carProductType.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()) {
                    carProductList.remove(carProductType);
                }
            }
        }

        //filter hertz prepay car
        if (!carProductList.isEmpty())
        {
            final List<CarProductType> carProductSatisfiedProviderList = new ArrayList<CarProductType>();
            carProductSatisfiedProviderList.addAll(carProductList);
            if (testData.getTestScenarioSpecialHandleParam().isHertzPrepayTestCase())
            {
                for (final CarProductType car : carProductSatisfiedProviderList)
                {
                    if (!car.getPrePayBoolean() || car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() != 40)
                    {
                        carProductList.remove(car);
                    }
                }
            }
        }

        final List<CarProductType> returnCarProductList = new ArrayList<CarProductType>();

        //Filter out GDSP commission, GDSP net rate car
        if (null != testData.getResultFilter() && testData.getResultFilter().getCarType() != null && CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == testData.getScenarios().getBusinessModel())
        {
            for (CarProductType car : carProductList)
            {
                if(testData.getResultFilter().getCarType().equals(GDSPCarType.GDSPCommission)) {
                    if (null == car.getCarMarkupInfo() || null == car.getCarMarkupInfo().getAppliedMarkupRuleList() || CollectionUtils.isEmpty(car.getCarMarkupInfo().getAppliedMarkupRuleList().getCarMarkupRuleInfo())) {
                        returnCarProductList.add(car);
                    }
                }
                else
                {
                    if (!(null == car.getCarMarkupInfo() || null == car.getCarMarkupInfo().getAppliedMarkupRuleList() || CollectionUtils.isEmpty(car.getCarMarkupInfo().getAppliedMarkupRuleList().getCarMarkupRuleInfo())))
                    {
                        if (testData.getResultFilter().isNeedFilterMarkup() == true)
                        {
                            List<Long> carRuleID = new ArrayList<>();
                            for (CarMarkupRuleInfoType markupInfo : car.getCarMarkupInfo().getAppliedMarkupRuleList().getCarMarkupRuleInfo())
                            {
                                carRuleID.add(markupInfo.getCarMarkupRuleID());
                            }
                            if (carsInventoryDataSource.hasNagativeRuleMarkup(carRuleID) == true)
                            {
                                returnCarProductList.add(car);
                            }
                        }
                        else
                        {
                            returnCarProductList.add(car);
                        }
                    }
                }
            }
            carProductList = returnCarProductList;
        }


        return carProductList;
    }

    public CarProductType getCarProduct(CarECommerceSearchResponseType searchResponseType, TestData testData) throws DataAccessException {

        if(this.selectedCarProduct != null) {
            return this.selectedCarProduct;
        }
        else if (null != searchResponseType
                && null != searchResponseType.getCarSearchResultList()
                && null != searchResponseType.getCarSearchResultList().getCarSearchResult()
                && !CollectionUtils.isEmpty(searchResponseType.getCarSearchResultList().getCarSearchResult())) {

            //filter
            selectedCarProduct = selectCarByBusinessModelAndServiceProviderIDFromCarSearchResultList(searchResponseType.getCarSearchResultList(), testData);
            if(selectedCarProduct != null) {
                if(selectedCarProduct.getCarInventoryKey() == null)
                    selectedCarProduct.setCarInventoryKey(new CarInventoryKeyType());
                if(null == searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask())
                {
                    selectedCarProduct.getCarInventoryKey().setPackageBoolean(searchRequestType.getCarECommerceSearchStrategy().getPackageBoolean());
                }
                else {
                    selectedCarProduct.getCarInventoryKey().setPackageBoolean(!CarCommonRequestGenerator.getStandaloneBoolByPurchaseTypeMask(
                            searchRequestType.getCarECommerceSearchStrategy().getPurchaseTypeMask()));
                }
            }
            else
            {
                Assert.fail("no expect car select.");
            }

           /* for (CarSearchResultType result : searchResponseType.getCarSearchResultList().getCarSearchResult()) {
                int size = result.getCarProductList().getCarProduct().size();
                if (null != result.getCarProductList()
                        && null != result.getCarProductList().getCarProduct()
                        && size > 0) {
                    int min = 0, max = size - 1;
                    //for cass 874 select a avis car
                    for(CarProductType product : result.getCarProductList().getCarProduct()){
                        //spoofer
                        if(product.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 41L &&
                                product.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()==2L ){
                            //life
                            //if(product.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 45L && product.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()==9L){
                            returnProductCass874 = product;
                        }
                    }
                    randomProduct = result.getCarProductList().getCarProduct().get(getRandomIndex(min, max));
                }
            }*/

        }
        else
        {
            Assert.fail("CarSearchResult is null.");
        }
        return selectedCarProduct;
    }

    public CarProductType getRandomCarProduct(CarECommerceSearchResponseType searchResponseType, TestData testData) {

        if(this.selectedCarProduct != null) {
            return this.selectedCarProduct;
        }
        else if (null != searchResponseType
                && null != searchResponseType.getCarSearchResultList()
                && null != searchResponseType.getCarSearchResultList().getCarSearchResult()
                && !CollectionUtils.isEmpty(searchResponseType.getCarSearchResultList().getCarSearchResult())) {
            CarProductType returnProductCass874 = null;
            CarProductType randomProduct = null;
            for (CarSearchResultType result : searchResponseType.getCarSearchResultList().getCarSearchResult()) {
                int size = result.getCarProductList().getCarProduct().size();
                if (null != result.getCarProductList()
                        && null != result.getCarProductList().getCarProduct()
                        && size > 0) {
                    int min = 0, max = size - 1;
                    //for cass 874 select a avis car
                    for(CarProductType product : result.getCarProductList().getCarProduct()){
                        //spoofer
                        if(product.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 41L &&
                                product.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()==2L ){
                            //life
                            //if(product.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 45L && product.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()==9L){
                            returnProductCass874 = product;
                        }
                    }
                    randomProduct = result.getCarProductList().getCarProduct().get(getRandomIndex(min, max));
                }
            }
            this.selectedCarProduct = returnProductCass874 == null ? randomProduct : returnProductCass874;
        }
        return this.selectedCarProduct;
    }

    protected int getRandomIndex(int min, int max) {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return s_rand.nextInt(max - min + 1) + min;

    }

    /**
     *
     * @param testData
     * @param rateCodeExistInDBFilter
     * @return
     * @throws DataAccessException
     */
    protected CarProductType getCarProductByBusinessModelIDAndServiceProviderID(TestData testData, boolean rateCodeExistInDBFilter) throws DataAccessException {
        if (null == selectedCarProduct && null != searchResponseType
                    && null != searchResponseType.getCarSearchResultList()
                    && null != searchResponseType.getCarSearchResultList().getCarSearchResult()
                    && !CollectionUtils.isEmpty(searchResponseType.getCarSearchResultList().getCarSearchResult())) {
            CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
            selectedCarProduct = inventoryHelper.selectCarByBusinessModelIDAndServiceProviderIDFromCarSearchResultList
                    (searchResponseType.getCarSearchResultList(), testData, rateCodeExistInDBFilter);
        }
        return selectedCarProduct;
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

    public CarProductType getSelectedCarProduct() {
        return selectedCarProduct;
    }

    public void setSelectedCarProduct(CarProductType selectedCarProduct) {
        this.selectedCarProduct = selectedCarProduct;
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

    public CarECommerceGetDetailsRequestType getGetDetailsRequestType() {
        return getDetailsRequestType;
    }

    public void setGetDetailsRequestType(CarECommerceGetDetailsRequestType getDetailsRequestType) {
        this.getDetailsRequestType = getDetailsRequestType;
    }

    public CarECommerceGetDetailsResponseType getGetDetailsResponseType() {
        return getDetailsResponseType;
    }

    public void setGetDetailsResponseType(CarECommerceGetDetailsResponseType getDetailsResponseType) {
        this.getDetailsResponseType = getDetailsResponseType;
    }

    public CarECommerceGetReservationRequestType getGetReservationRequestType() {
        return getReservationRequestType;
    }

    public void setGetReservationRequestType(CarECommerceGetReservationRequestType getReservationRequestType) {
        this.getReservationRequestType = getReservationRequestType;
    }

    public CarECommerceGetReservationResponseType getGetReservationResponseType() {
        return getReservationResponseType;
    }

    public void setGetReservationResponseType(CarECommerceGetReservationResponseType getReservationResponseType) {
        this.getReservationResponseType = getReservationResponseType;
    }

    public CarECommerceReserveRequestType getReserveRequestType() {
        return reserveRequestType;
    }

    public void setReserveRequestType(CarECommerceReserveRequestType reserveRequestType) {
        this.reserveRequestType = reserveRequestType;
    }

    public CarECommerceReserveResponseType getReserveResponseType() {
        return reserveResponseType;
    }

    public void setReserveResponseType(CarECommerceReserveResponseType reserveResponseType) {
        this.reserveResponseType = reserveResponseType;
    }

    public CarECommerceCancelRequestType getCancelRequestType() {
        return cancelRequestType;
    }

    public void setCancelRequestType(CarECommerceCancelRequestType cancelRequestType) {
        this.cancelRequestType = cancelRequestType;
    }

    public CarECommerceCancelResponseType getCancelResponseType() {
        return cancelResponseType;
    }

    public void setCancelResponseType(CarECommerceCancelResponseType cancelResponseType) {
        this.cancelResponseType = cancelResponseType;
    }

}
