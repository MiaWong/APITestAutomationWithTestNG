package com.expedia.s3.cars.supply.service.requestgenerators;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.placetypes.defn.v4.LanguageType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.supply.messages.cancel.defn.v4.CarSupplyCancelRequestType;
import com.expedia.s3.cars.supply.messages.cancel.defn.v4.CarSupplyCancelResponseType;
import com.expedia.s3.cars.supply.messages.getcostandavailability.defn.v4.CarSupplyGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supply.messages.getcostandavailability.defn.v4.CarSupplyGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsRequestType;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsResponseType;
import com.expedia.s3.cars.supply.messages.getreservation.defn.v4.CarSupplyGetReservationRequestType;
import com.expedia.s3.cars.supply.messages.getreservation.defn.v4.CarSupplyGetReservationResponseType;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveRequestType;
import com.expedia.s3.cars.supply.messages.reserve.defn.v4.CarSupplyReserveResponseType;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchRequestType;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchResponseType;
import com.expedia.s3.cars.supply.service.common.CarCommonEnumManager;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;
import com.expedia.s3.cars.supply.service.utils.ExecutionHelper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by yyang4 on 8/22/2016.
 */
@SuppressWarnings("PMD")
public class SSRequestGenerator {
    private static final Random S_RAND = new Random(System.nanoTime());
    private CarSupplySearchRequestType searchReq;
    private CarSupplySearchResponseType searchResp;
    private CarSupplyGetDetailsRequestType detailsReq;
    private CarSupplyGetDetailsResponseType detailsResp;
    private CarSupplyReserveRequestType reserveReq;
    private CarSupplyReserveResponseType reserveResp;
    private CarSupplyGetCostAndAvailabilityRequestType costAndAvailReq;
    private CarSupplyGetCostAndAvailabilityResponseType costAndAvailResp;
    private CarSupplyGetReservationRequestType getReservationReq;
    private CarSupplyGetReservationResponseType getReservationResp;
    private CarSupplyCancelRequestType cancelReq;
    private CarSupplyCancelResponseType cancelResp;
    private CarProductType selectedCarProduct;
    private TestScenario scenario;
    private boolean bvtTest;

    public SSRequestGenerator(CarSupplySearchRequestType searchReq, CarSupplySearchResponseType searchResp) {
        this.searchReq = searchReq;
        this.searchResp = searchResp;
        this.bvtTest = false;
    }

    public CarSupplyGetDetailsRequestType createDetailsRequest() throws DataAccessException {
        final CarSupplyGetDetailsRequestType req = new CarSupplyGetDetailsRequestType();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(this.getMessageName(req), this.searchReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(this.searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(this.searchReq.getAuditLogTrackingData());
        final LanguageType languageType = new LanguageType();
        languageType.setLanguageCode("en");
        languageType.setCountryAlpha2Code("US");
        req.setLanguage(languageType);
        final CarProductListType carList = new CarProductListType();
        final ArrayList listOfCars = new ArrayList();
        carList.setCarProduct(listOfCars);
        listOfCars.add(this.getRandomCarProduct());
        req.setCarProductList(carList);

        final CarDataCategoryCodeListType carDataCategoryCodeListType = new CarDataCategoryCodeListType();
        final String myCategoryCode = scenario.getServiceProviderID() == CarCommonEnumManager.ServieProvider.Amadeus.getValue() ? "Arrival" : "MerchantRules";
        carDataCategoryCodeListType.setCarDataCategoryCode(new ArrayList<String>());
        carDataCategoryCodeListType.getCarDataCategoryCode().add(myCategoryCode);
        req.setCarDataCategoryCodeList(carDataCategoryCodeListType);

        final CarPolicyCategoryCodeListType carPolicyCategoryCodeListType = new CarPolicyCategoryCodeListType();
        final List<String> carPolicyCategoryCodeArrayList = new ArrayList<String>();
        carPolicyCategoryCodeArrayList.add("MerchantRules");
        carPolicyCategoryCodeArrayList.add("Arrival");
        carPolicyCategoryCodeListType.setCarPolicyCategoryCode(carPolicyCategoryCodeArrayList);
        req.setCarPolicyCategoryCodeList(carPolicyCategoryCodeListType);

        this.detailsReq = req;
        return req;
    }

    public CarSupplyGetReservationRequestType createGetReservationRequest() {
        final CarSupplyGetReservationRequestType req = new CarSupplyGetReservationRequestType();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(this.getMessageName(req), this.searchReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(this.searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(this.searchReq.getAuditLogTrackingData());
        final String currencyCode = ((CarSearchCriteriaType) this.searchReq.getCarSearchCriteriaList().getCarSearchCriteria().get(0)).getCurrencyCode();
        req.setCurrencyCode(currencyCode);
        final ArrayList carReservationList = new ArrayList();
        carReservationList.add(this.reserveResp.getCarReservation());
        final CarReservationListType carReservationListType = new CarReservationListType();
        carReservationListType.setCarReservation(carReservationList);
        req.setCarReservationList(carReservationListType);
        this.getReservationReq = req;
        return req;
    }

    public CarSupplyReserveRequestType createReserveRequest() throws DataAccessException {
        final CarSupplyReserveRequestType req = new CarSupplyReserveRequestType();
        if (this.selectedCarProduct == null) {
            this.selectedCarProduct = this.getRandomCarProduct();
        }

        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(this.getMessageName(req), this.searchReq.getMessageInfo().getMessageVersion()));
        req.setPackageBoolean(Boolean.FALSE);
        req.setPointOfSaleKey(this.searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(this.searchReq.getAuditLogTrackingData());
        req.setCarProduct(this.selectedCarProduct);
        req.setTravelerList(CommonDataTypesGenerator.createTravelerList(false));
        req.setCustomer(CommonDataTypesGenerator.createCustomer());

        this.reserveReq = req;
        return req;
    }

    public CarSupplyGetCostAndAvailabilityRequestType createCostAndAvailRequest() throws DataAccessException {
        final CarSupplyGetCostAndAvailabilityRequestType req = new CarSupplyGetCostAndAvailabilityRequestType();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(this.getMessageName(req), this.searchReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(this.searchReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(this.searchReq.getAuditLogTrackingData());
        final CarProductListType carAvailList = new CarProductListType();
        final ArrayList listOfCars = new ArrayList();
        carAvailList.setCarProduct(listOfCars);
        listOfCars.add(this.getRandomCarProduct());
        req.setCarProductList(carAvailList);
        this.costAndAvailReq = req;
        return req;
    }

    public CarSupplyCancelRequestType createCancelRequest() {
        final CarSupplyCancelRequestType req = new CarSupplyCancelRequestType();
        req.setMessageInfo(CommonDataTypesGenerator.createMessageInfo(this.getMessageName(req), this.reserveReq.getMessageInfo().getMessageVersion()));
        req.setPointOfSaleKey(this.reserveReq.getPointOfSaleKey());
        req.setAuditLogTrackingData(this.reserveReq.getAuditLogTrackingData());
        req.setCarReservation(this.reserveResp.getCarReservation());
        req.setCommitBoolean(true);
        this.cancelReq = req;
        return req;
    }

    private String getMessageName(Object requestMessage) {
        String bindingElementName = requestMessage.getClass().getSimpleName();
        if (bindingElementName.endsWith("Type")) {
            bindingElementName = bindingElementName.substring(0, bindingElementName.lastIndexOf("Type"));
        }

        return bindingElementName;
    }

    protected CarProductType getRandomCarProduct() throws DataAccessException {
        if (isCarSearchResultExist()) {
            final Iterator var1 = this.searchResp.getCarSearchResultList().getCarSearchResult().iterator();
            //Get all cars with expected provideID
            final List<CarProductType> carProductTypeList = new ArrayList<CarProductType>();
            final CarsInventoryDataSource inventoryDataSource = new CarsInventoryDataSource(SettingsProvider.CAR_INVENTORY_DATASOURCE);
            while (var1.hasNext()) {
                final CarSearchResultType result = (CarSearchResultType) var1.next();
                for(final CarProductType car : result.getCarProductList().getCarProduct())
                {
                    final long supplierID = car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
                    final String locationCode = car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode();
                    //final int myBMIDFromRsp = inventoryDataSource.getCarBusinessModelIDFromCarItem(car.getCarInventoryKey().getCarItemID());
                    //Filter out the ZR or ZT car in SEA
                    final boolean filterSea = !((supplierID == 43L &&  "SEA".equals(locationCode))|| (supplierID == 45L && "SEA".equals(locationCode)));
                    final boolean filterVendor = !(supplierID == 42L && 16 == car.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode().intValue());
                    // filter out "GR" vendor
                    if((car.getProviderID() == null || car.getProviderID().intValue() == scenario.getServiceProviderID())
                            && car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() != 1022L
                            && filterSea && filterVendor)
                    {
                            carProductTypeList.add(car);
                    }
                }
            }
            if ( !carProductTypeList.isEmpty()) {
                final byte min = 0;
                final int max = carProductTypeList.size() - 1;
                this.selectedCarProduct = (CarProductType) carProductTypeList.get(this.getRandomIndex(min, max));
                //Fill in location code by CarVendorLocationID because ExpediaSCS need location code for GetDetails
                fillInLocationCodeByLocationID(this.selectedCarProduct);

                return this.selectedCarProduct;
            }
        }

        return null;
    }

    public CarProductType selectCarByVendor(long vendorSupplierID) throws DataAccessException {
        if (isCarSearchResultExist()) {
            final Iterator var1 = this.searchResp.getCarSearchResultList().getCarSearchResult().iterator();
            //Get all cars with expected provideID
            final List<CarProductType> carProductTypeList = new ArrayList<CarProductType>();
            while (var1.hasNext()) {
                final CarSearchResultType result = (CarSearchResultType) var1.next();
                for(final CarProductType car : result.getCarProductList().getCarProduct())
                {
                    if(car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == vendorSupplierID)
                    {
                        carProductTypeList.add(car);
                    }
                }
            }
            if ( !carProductTypeList.isEmpty()) {
                final byte min = 0;
                final int max = carProductTypeList.size() - 1;
                this.selectedCarProduct = (CarProductType) carProductTypeList.get(this.getRandomIndex(min, max));
                //Fill in location code by CarVendorLocationID because ExpediaSCS need location code for GetDetails
                fillInLocationCodeByLocationID(this.selectedCarProduct);

                return this.selectedCarProduct;
            }
        }

        return null;
    }

    public CarProductType selectCarBySupplysubsetID(long supplysubsetID) throws DataAccessException {
        if (isCarSearchResultExist()) {
            final Iterator var1 = this.searchResp.getCarSearchResultList().getCarSearchResult().iterator();
            //Get all cars with expected provideID
            final List<CarProductType> carProductTypeList = new ArrayList<CarProductType>();
            while (var1.hasNext()) {
                final CarSearchResultType result = (CarSearchResultType) var1.next();
                for(final CarProductType car : result.getCarProductList().getCarProduct())
                {
                    if(car.getCarInventoryKey().getSupplySubsetID() == supplysubsetID)
                    {
                        carProductTypeList.add(car);
                    }
                }
            }
            if ( !carProductTypeList.isEmpty()) {
                final byte min = 0;
                final int max = carProductTypeList.size() - 1;
                this.selectedCarProduct = (CarProductType) carProductTypeList.get(this.getRandomIndex(min, max));
                //Fill in location code by CarVendorLocationID because ExpediaSCS need location code for GetDetails
                fillInLocationCodeByLocationID(this.selectedCarProduct);

                return this.selectedCarProduct;
            }
        }

        return null;
    }

    private boolean isCarSearchResultExist()
    {
        boolean exist = false;
        if(null != this.searchResp && null != this.searchResp.getCarSearchResultList() &&
                null != this.searchResp.getCarSearchResultList().getCarSearchResult() &&
                this.searchResp.getCarSearchResultList().getCarSearchResult().size() > 0)
        {
            exist = true;
        }
        return exist;
    }

    //Fill in location code by CarVendorLocationID
    private void fillInLocationCodeByLocationID(CarProductType car) throws DataAccessException {
        if(this.isBvtTest())
        {
            handleBVTLocationCodes(car);
            return;
        }
        final CarsInventoryHelper helper = new CarsInventoryHelper(ExecutionHelper.getCarsInventoryDatasource());
        final Long pickLocationID = this.selectedCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID();
        if(null != pickLocationID && pickLocationID > 0)
        {
            final CarVendorLocation pickLocation = helper.getCarLocation(car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID());
            car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode(null == pickLocation ? "AAA" : pickLocation.locationCode);
            car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setCarLocationCategoryCode(null == pickLocation ? "T" : pickLocation.carLocationCategoryCode);
            car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setSupplierRawText(null == pickLocation ? "001" : pickLocation.getSupplierRawText());
        }

        final Long dropLocationID = car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID();
        if(null != dropLocationID && dropLocationID > 0)
        {
            final CarVendorLocation dropLocation = helper.getCarLocation(car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID());
            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode(null == dropLocation ? "AAA" : dropLocation.locationCode);
            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setCarLocationCategoryCode(null == dropLocation ? "T" : dropLocation.carLocationCategoryCode);
            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setSupplierRawText(null == dropLocation ? "001" : dropLocation.getSupplierRawText());
        }
    }

    private void handleBVTLocationCodes(CarProductType car) {
        final String locationCode = this.selectedCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode();
        if(StringUtils.isEmpty(locationCode))
        {
            car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode(this.scenario.getPickupLocationCode());
            car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setCarLocationCategoryCode("T");
            car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setSupplierRawText("001");
            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode(this.scenario.getDropOffLocationCode());
            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setCarLocationCategoryCode("T");
            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setSupplierRawText("001");
        }

    }

    protected int getRandomIndex(int min, int max) {
        return S_RAND.nextInt(max - min + 1) + min;
    }

    public CarSupplySearchRequestType getSearchReq() {
        return this.searchReq;
    }

    public void setSearchReq(CarSupplySearchRequestType searchReq) {
        this.searchReq = searchReq;
    }

    public CarSupplySearchResponseType getSearchResp() {
        return this.searchResp;
    }

    public void setSearchResp(CarSupplySearchResponseType searchResp) {
        this.searchResp = searchResp;
    }

    public CarSupplyGetDetailsRequestType getDetailsReq() {
        return this.detailsReq;
    }

    public void setDetailsReq(CarSupplyGetDetailsRequestType detailsReq) {
        this.detailsReq = detailsReq;
    }

    public CarSupplyGetDetailsResponseType getDetailsResp() {
        return this.detailsResp;
    }

    public void setDetailsResp(CarSupplyGetDetailsResponseType detailsResp) {
        this.detailsResp = detailsResp;
    }

    public CarSupplyGetCostAndAvailabilityRequestType getCostAndAvailReq() {
        return this.costAndAvailReq;
    }

    public void setCostAndAvailReq(CarSupplyGetCostAndAvailabilityRequestType costAndAvailReq) {
        this.costAndAvailReq = costAndAvailReq;
    }

    public CarSupplyGetCostAndAvailabilityResponseType getCostAndAvailResp() {
        return this.costAndAvailResp;
    }

    public void setCostAndAvailResp(CarSupplyGetCostAndAvailabilityResponseType costAndAvailResp) {
        this.costAndAvailResp = costAndAvailResp;
    }

    public CarSupplyGetReservationRequestType getGetReservationReq() {
        return this.getReservationReq;
    }

    public void setGetReservationReq(CarSupplyGetReservationRequestType getReservationReq) {
        this.getReservationReq = getReservationReq;
    }

    public CarSupplyGetReservationResponseType getGetReservationResp() {
        return this.getReservationResp;
    }

    public void setGetReservationResp(CarSupplyGetReservationResponseType getReservationResp) {
        this.getReservationResp = getReservationResp;
    }

    public CarSupplyCancelRequestType getCancelReq() {
        return this.cancelReq;
    }

    public void setCancelReq(CarSupplyCancelRequestType cancelReq) {
        this.cancelReq = cancelReq;
    }

    public CarSupplyCancelResponseType getCancelResp() {
        return this.cancelResp;
    }

    public void setCancelResp(CarSupplyCancelResponseType cancelResp) {
        this.cancelResp = cancelResp;
    }

    public CarSupplyReserveRequestType getReserveReq() {
        return this.reserveReq;
    }

    public void setReserveReq(CarSupplyReserveRequestType reserveReq) {
        this.reserveReq = reserveReq;
    }

    public CarSupplyReserveResponseType getReserveResp() {
        return this.reserveResp;
    }

    public void setReserveResp(CarSupplyReserveResponseType reserveResp) {
        this.reserveResp = reserveResp;
    }


    public TestScenario getScenario() {
        return this.scenario;
    }

    public void setScenario(TestScenario scenario) {
        this.scenario = scenario;
    }

    public boolean isBvtTest() {
        return bvtTest;
    }

    public void setBvtTest(boolean bvtTest) {
        this.bvtTest = bvtTest;
    }

}
