package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;

import com.expedia.e3.data.basetypes.defn.v4.VendorSupplierIDListType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarTransportationSegmentType;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarClassificationIDListType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaListType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchStrategyType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 8/24/2016.
 */
@SuppressWarnings("PMD")
public class CarbsSearchRequestGenerator {
    private static final String MESSAGE_VERSION = "4.1.0";
    private static final String MESSAGE_NAME = "CarECommerceSearchRequest";


    public static CarECommerceSearchStrategyType createCarECommerceSearchStrategy(TestData param) {
        CarECommerceSearchStrategyType carECommerceSearchStrategy = new CarECommerceSearchStrategyType();
        carECommerceSearchStrategy.setNeedReferencePricesBoolean(false);
        carECommerceSearchStrategy.setNeedUpgradeMapBoolean(true);
        carECommerceSearchStrategy.setHotelPurchaseOption(1l);
        carECommerceSearchStrategy.setPurchaseTypeMask(Long.valueOf(param.getScenarios().getPurchaseType().getPurchaseTypeMask()));
        carECommerceSearchStrategy.setNeedPublishedBoolean(false);
        switch (param.getScenarios().getBusinessModel()) {
            case 1: // Agency case
                carECommerceSearchStrategy.setNeedMerchantBoolean(false);
                break;
            default: // for Merchant and GDSP value should be true
                carECommerceSearchStrategy.setNeedMerchantBoolean(true);
        }
        return carECommerceSearchStrategy;
    }

    public static CarTransportationSegmentType createCarTransportationSegment(boolean isSearchByLocation, TestScenario testScenario,
                                                                              SegmentDateTimeRangeType segmentDateTimeRange) {
        CarTransportationSegmentType carTransportationSegment = new CarTransportationSegmentType();
        CommonRequestGenerator commongenerator = new CommonRequestGenerator();
        //If search is by location, build the StartCarLocationKey and EndCarLocationKey, else build the StartCarLocation
        if (isSearchByLocation) {
            carTransportationSegment.setStartCarLocationKey(commongenerator.createCarLocationKeyType(testScenario.getPickupLocationCode()));
            carTransportationSegment.setEndCarLocationKey(commongenerator.createCarLocationKeyType(testScenario.getDropOffLocationCode()));
        } else {
            carTransportationSegment.setStartCarLocation(commongenerator.createCarLocationSearchType(testScenario));
        }

        carTransportationSegment.setSegmentDateTimeRange(segmentDateTimeRange);
        // Not support yet, left it for furture
        carTransportationSegment.setDuration(null);
        return carTransportationSegment;
    }

    public static CarTransportationSegmentType createCarTransportationSegment
            (boolean isSearchByLocation, boolean isOnAirport, SegmentDateTimeRangeType segmentDateTimeRange, CarVendorLocation carStartVendorLocationInfo,
             CarVendorLocation carEndVendorLocationInfo, CarCommonEnumManager.CollectionSet collectionBoolean,
             CarCommonEnumManager.DeliverySet deliveryBoolean, CarCommonEnumManager.OutOfOfficeHourBooleanSet outOfOfficeHourBooleanSet) {

        CarTransportationSegmentType carTransportationSegment = new CarTransportationSegmentType();
        CommonRequestGenerator commongenerator = new CommonRequestGenerator();
        //If search is by location, build the StartCarLocationKey and EndCarLocationKey, else build the StartCarLocation
        if (isSearchByLocation) {
            carTransportationSegment.setStartCarLocationKey(createCarLocationKey(isOnAirport, carStartVendorLocationInfo, collectionBoolean, deliveryBoolean, outOfOfficeHourBooleanSet));
            carTransportationSegment.setEndCarLocationKey(createCarLocationKey(isOnAirport, carEndVendorLocationInfo, collectionBoolean, deliveryBoolean, outOfOfficeHourBooleanSet));
        } else
            carTransportationSegment.setStartCarLocationKey(commongenerator.createCarLocationKeyType(carStartVendorLocationInfo.getLocationCode()));

        carTransportationSegment.setSegmentDateTimeRange(segmentDateTimeRange);
        // Not support yet, left it for furture
        carTransportationSegment.setDuration(null);
        return carTransportationSegment;
    }

    public static CarLocationKeyType createCarLocationKey(boolean isOnAirport, CarVendorLocation carVendorLocationInfo,
                                                          CarCommonEnumManager.CollectionSet collectionBoolean,
                                                          CarCommonEnumManager.DeliverySet deliveryBoolean, CarCommonEnumManager.OutOfOfficeHourBooleanSet outOfOfficeHourBooleanSet) {
        CarLocationKeyType carLocationKey = new CarLocationKeyType();

        carLocationKey.setLocationCode(carVendorLocationInfo.getLocationCode());

        if (!isOnAirport) {
            //DeliveryBoolean, CollectionBoolean and OurtOfOfficeHourBoolean are only for off airport car
            if (collectionBoolean != CarCommonEnumManager.CollectionSet.NonExist)
                carLocationKey.setCollectionBoolean(collectionBoolean.isCollectionSet());
            if (deliveryBoolean != CarCommonEnumManager.DeliverySet.NonExist)
                carLocationKey.setDeliveryBoolean(deliveryBoolean.isDeliverySet());
            if (outOfOfficeHourBooleanSet != CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist)
                carLocationKey.setOutOfOfficeHourBoolean(outOfOfficeHourBooleanSet.isOutOfOOfficeeHourBooleanSet());

            carLocationKey.setCarLocationCategoryCode(carVendorLocationInfo.getCarLocationCategoryCode());
            carLocationKey.setSupplierRawText(carVendorLocationInfo.getSupplierRawText());
            carLocationKey.setCarVendorLocationID(carVendorLocationInfo.getCarVendorLocationID());
        }
        return carLocationKey;
    }

    public static CarECommerceSearchCriteriaListType createCarECommerceSearchCriteriaList
            (TestData testData, CommonRequestGenerator commonRequestGenerator, boolean isSearchByLocation) throws DataAccessException {
        CarECommerceSearchCriteriaListType carECommerceSearchCriteriaList = new CarECommerceSearchCriteriaListType();
        List<CarECommerceSearchCriteriaType> carECommerceSearchCriteriaTypes = new ArrayList<>();
        CarRateType carRateType = new CarRateType();
        CarRateOverrideListType carRateOverrideList = new CarRateOverrideListType();
        carECommerceSearchCriteriaList.setCarECommerceSearchCriteria(carECommerceSearchCriteriaTypes);
        SegmentDateTimeRangeType segmentDateTimeRange = commonRequestGenerator.createSegmentDateTimeRangeType(testData);
        String currencyCode = testData.getScenarios().getSupplierCurrencyCode();
        boolean oneWayBool = true;
        if (testData.getScenarios().getPickupLocationCode() == testData.getScenarios().getDropOffLocationCode())
            oneWayBool = false;

        //For adding Loyalty part, condition will check multi traveler and traveler loyalty flag to decide if CarRate loyalty is required or not
        if (!StringUtils.isEmpty(testData.getCarRate().getLoyaltyNum())) {
            carRateType = commonRequestGenerator.createCarRate(testData);
        }
        if (!StringUtils.isEmpty(testData.getCarRate().getPromoCode())) {
            carRateType = commonRequestGenerator.createCarRate(testData);
        }
        //for adding CD code in CarRateOverrideListType
        if (!StringUtils.isEmpty(testData.getCarRate().getCdCode())) {
            List<CarRateOverrideType> carRateOverride = commonRequestGenerator.createCDCodes(testData.getCarRate());
            carRateOverrideList.setCarRateOverride(carRateOverride);
        }

        //For on airport car request
        if (testData.getScenarios().isOnAirPort()) {
            CarTransportationSegmentType carTransportationSegment =
                    commonRequestGenerator.createCarTransportationSegmentType(testData);
            CarECommerceSearchCriteriaType carECommerceSearchCriteria = createCarECommerceSearchCriteria(carTransportationSegment, currencyCode, testData, carRateType, carRateOverrideList);
            carECommerceSearchCriteriaTypes.add(carECommerceSearchCriteria);
        }

        //For Lat/Lon off airport request
        else if (!isSearchByLocation && !testData.getScenarios().isOnAirPort()) {
            CarTransportationSegmentType carTransportationSegment = createCarTransportationSegment(isSearchByLocation, testData.getScenarios(), segmentDateTimeRange);
            CarECommerceSearchCriteriaType carECommerceSearchCriteria = createCarECommerceSearchCriteria(carTransportationSegment, currencyCode, testData, carRateType, carRateOverrideList);
            carECommerceSearchCriteriaTypes.add(carECommerceSearchCriteria);
        }

        //For non Lat/Lon(LocationCode) off airport roundtrip request
        else if (isSearchByLocation && !testData.getScenarios().isOnAirPort() && !oneWayBool) {
            //Get the CarLocationKeyList queried from DB
            CarsInventoryHelper carVenderHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
            List<CarVendorLocation> carLocationKeyList = carVenderHelper.getCarLocationList(testData.getScenarios().getPickupLocationCode(), 10);
            int sequence = 1;

            for (CarVendorLocation carVendorLocationInfo : carLocationKeyList) {
                CarTransportationSegmentType carTransportationSegment = createCarTransportationSegment(isSearchByLocation, testData.getScenarios().isOnAirPort(), segmentDateTimeRange, carVendorLocationInfo, carVendorLocationInfo,
                        CarCommonEnumManager.CollectionSet.NonExist, CarCommonEnumManager.DeliverySet.NonExist, CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist);

                CarECommerceSearchCriteriaType carECommerceSearchCriteria = createCarECommerceSearchCriteria(carTransportationSegment, currencyCode, carVendorLocationInfo.getVendorSupplierID(), sequence);
                carECommerceSearchCriteriaTypes.add(carECommerceSearchCriteria);
                sequence++;
            }
        }

        //For non Lat/Lon(LocationCode) off airport one way request
        else if (isSearchByLocation && !testData.getScenarios().isOnAirPort() && oneWayBool) {
            //Get the CarLocationKeyList queried from DB
            CarsInventoryHelper carVenderHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

            List<CarVendorLocation> carStartLocationKeyList = carVenderHelper.getCarLocationList(testData.getScenarios().getPickupLocationCode(), 10);
            List<CarVendorLocation> carEndLocationKeyList = carVenderHelper.getCarLocationList(testData.getScenarios().getDropOffLocationCode(), 10);

            //Filter out the CarVendorLocation which have the same VendorSupplierID
            List<CarVendorLocation> filteredCarStartLocationKeyList = new ArrayList<>();
            List<CarVendorLocation> filteredCarEndLocationKeyList = new ArrayList<>();
            List<CarVendorLocation> uniquePickupLocationKeyList = new ArrayList<>();
            List<CarVendorLocation> uniqueDropOffLocationKeyList = new ArrayList<>();
            for (int a = 0; a < carStartLocationKeyList.size(); a++) {
                for (int b = 0; b < carEndLocationKeyList.size(); b++) {
                    if (carStartLocationKeyList.get(a).getVendorSupplierID() == carEndLocationKeyList.get(b).getVendorSupplierID()
                            && carStartLocationKeyList.get(a).carLocationCategoryCode.equals(carEndLocationKeyList.get(b).carLocationCategoryCode)
                            && carStartLocationKeyList.get(a).supplierRawText.equals(carEndLocationKeyList.get(b).supplierRawText)) {
                        filteredCarStartLocationKeyList.add(carStartLocationKeyList.get(a));
                        filteredCarEndLocationKeyList.add(carEndLocationKeyList.get(b));
                    }
                }
            }
            int sequence = 1;
            int sameVendorCount_1 = 0;
            CarECommerceSearchCriteriaType carECommerceSearchCriteria;
            for (int i = 0; i < filteredCarStartLocationKeyList.size(); i++) {
                CarTransportationSegmentType carTransportationSegment = createCarTransportationSegment(isSearchByLocation, testData.getScenarios().isOnAirPort(),
                        segmentDateTimeRange, filteredCarStartLocationKeyList.get(i), filteredCarEndLocationKeyList.get(i), CarCommonEnumManager.CollectionSet.NonExist,
                        CarCommonEnumManager.DeliverySet.NonExist, CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist);
                carECommerceSearchCriteria = createCarECommerceSearchCriteria(carTransportationSegment, currencyCode, filteredCarStartLocationKeyList.get(i).getVendorSupplierID(), sequence);
                addCarECommerceSearchCriteriaForVendor(carECommerceSearchCriteriaTypes, carECommerceSearchCriteria, filteredCarStartLocationKeyList.get(i), sameVendorCount_1, 5);
                sequence++;
            }

            for (CarVendorLocation filteredCarStartLocationKey : filteredCarStartLocationKeyList) {
                if (carStartLocationKeyList.contains(filteredCarStartLocationKey)) {
                    carStartLocationKeyList.remove(filteredCarStartLocationKey);
                }
            }
            for (CarVendorLocation filteredCarEndLocationKey : filteredCarEndLocationKeyList) {
                if (carEndLocationKeyList.contains(filteredCarEndLocationKey)) {
                    carEndLocationKeyList.remove(filteredCarEndLocationKey);
                }
            }
            for (int a = 0; a < carStartLocationKeyList.size(); a++) {
                for (int b = 0; b < carEndLocationKeyList.size(); b++) {
                    if (carStartLocationKeyList.get(a).vendorSupplierID == carEndLocationKeyList.get(b).vendorSupplierID) {
                        uniquePickupLocationKeyList.add(carStartLocationKeyList.get(a));
                        uniqueDropOffLocationKeyList.add(carEndLocationKeyList.get(b));
                    }
                }
            }
            if (uniquePickupLocationKeyList.size() != 0 && uniqueDropOffLocationKeyList.size() != 0) {
                int minCount = uniquePickupLocationKeyList.size() > uniqueDropOffLocationKeyList.size() ? uniqueDropOffLocationKeyList.size() : uniquePickupLocationKeyList.size();
                int sameVendorCount_2 = 0;
                for (int k = 0; k < minCount; k++) {
                    CarTransportationSegmentType carTransportationSegment = createCarTransportationSegment(isSearchByLocation, testData.getScenarios().isOnAirPort(), segmentDateTimeRange,
                            uniquePickupLocationKeyList.get(k), uniqueDropOffLocationKeyList.get(k), CarCommonEnumManager.CollectionSet.NonExist, CarCommonEnumManager.DeliverySet.NonExist,
                            CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist);
                    carECommerceSearchCriteria = createCarECommerceSearchCriteria(carTransportationSegment, currencyCode, uniquePickupLocationKeyList.get(k).getVendorSupplierID(), sequence);
                    addCarECommerceSearchCriteriaForVendor(carECommerceSearchCriteriaTypes, carECommerceSearchCriteria, uniquePickupLocationKeyList.get(k), sameVendorCount_2, 5);
                    sequence++;
                }
            }
            carECommerceSearchCriteriaTypes = filterForMultipleVendorInSearchCriteria(carECommerceSearchCriteriaTypes);
            carECommerceSearchCriteriaList.setCarECommerceSearchCriteria(carECommerceSearchCriteriaTypes);
        }

        if (null != testData.getTestScenarioSpecialHandleParam() && StringUtil.isNotBlank(testData.getTestScenarioSpecialHandleParam()
        .getVendorCode()) )
        {
            String vendorCode = testData.getTestScenarioSpecialHandleParam().getVendorCode();
            if(testData.getScenarios().isOnAirPort())
            {
                Long supplierId = Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(vendorCode));
                VendorSupplierIDListType vendorSupplierIDListType = new VendorSupplierIDListType();
                vendorSupplierIDListType.setVendorSupplierID(Arrays.asList(supplierId));
                carECommerceSearchCriteriaList.getCarECommerceSearchCriteria().get(0).setVendorSupplierIDList(vendorSupplierIDListType);
            }
            else
            {
                String[] vendors = vendorCode.split(",");
                List<Long> supplierIDList = new ArrayList<>();
                for (String vendor : vendors)
                {
                    //Query the VendorSupplierID from DB based on VendorCode
                    Long supplierID = Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(vendor));
                    supplierIDList.add(supplierID);

                }

                List<CarECommerceSearchCriteriaType> returnedCriteriaList = new ArrayList<>();
                // filter
                for (CarECommerceSearchCriteriaType criteria : carECommerceSearchCriteriaList.getCarECommerceSearchCriteria())
                {
                    // location code for Off-airport search per search criteria only has one vendor
                     Long supplierID = criteria.getVendorSupplierIDList().getVendorSupplierID().get(0);
                        if (supplierIDList.contains(supplierID))
                        {
                            returnedCriteriaList.add(criteria);
                        }

                }
                if (CollectionUtils.isEmpty(returnedCriteriaList))
                {
                    Assert.fail("There is no mached vendor in search criteria for vendor " + vendorCode);
                }
                carECommerceSearchCriteriaList.setCarECommerceSearchCriteria(returnedCriteriaList);
            }
        }


        return carECommerceSearchCriteriaList;
    }

    public static void addCarECommerceSearchCriteriaForVendor(List<CarECommerceSearchCriteriaType> carECommerceSearchCriteriaTypes, CarECommerceSearchCriteriaType
            carECommerceSearchCriteria, CarVendorLocation pickupLocationKey, int sequence, int searchCriteriaForVendor) {
        int sameVendorCount = 0;
        if (carECommerceSearchCriteriaTypes.size() == 0) {
            carECommerceSearchCriteriaTypes.add(carECommerceSearchCriteria);
            sequence++;
        } else {
            for (int j = 0; j < carECommerceSearchCriteriaTypes.size(); j++) {
                // loop the search criteria and match the same vendor
                if (pickupLocationKey.vendorSupplierID == carECommerceSearchCriteriaTypes.get(j).getVendorSupplierIDList().getVendorSupplierID().get(0))
                    sameVendorCount++;
            }
            // default value is 5 = searchCriteriaForVendor
            if (sameVendorCount < searchCriteriaForVendor) {
                carECommerceSearchCriteriaTypes.add(carECommerceSearchCriteria);
                sequence++;
            }
        }
    }

    public static List<CarECommerceSearchCriteriaType> filterForMultipleVendorInSearchCriteria(List<CarECommerceSearchCriteriaType> criteriaList) {
        List<CarECommerceSearchCriteriaType> criteriaList_returned = new ArrayList<>();
        for (CarECommerceSearchCriteriaType criteria : criteriaList) {
            if (criteriaList_returned.size() == 0)
                criteriaList_returned.add(criteria);
            else {
                int machedSameIndex = 0;
                boolean machedBool = false;
                List<Long> returnedSupplierIDs = null;
                CarTransportationSegmentType segment_added = criteria.getCarTransportationSegment();
                for (int i = 0; i < criteriaList_returned.size(); i++) {
                    CarTransportationSegmentType segment_filter = criteriaList_returned.get(i).getCarTransportationSegment();
                    // CarTransportationSegment
                    if (segment_filter.getStartCarLocationKey().getLocationCode().equals(segment_added.getStartCarLocationKey().getLocationCode())
                            && segment_filter.getStartCarLocationKey().getCarLocationCategoryCode().equals(segment_added.getStartCarLocationKey().getCarLocationCategoryCode())
                            && segment_filter.getStartCarLocationKey().getSupplierRawText().equals(segment_added.getStartCarLocationKey().getSupplierRawText())
                            && segment_filter.getEndCarLocationKey().getLocationCode().equals(segment_added.getEndCarLocationKey().getLocationCode())
                            && segment_filter.getEndCarLocationKey().getCarLocationCategoryCode().equals(segment_added.getEndCarLocationKey().getCarLocationCategoryCode())
                            && segment_filter.getEndCarLocationKey().getSupplierRawText().equals(segment_added.getEndCarLocationKey().getSupplierRawText())) {
                        machedSameIndex = i;
                        machedBool = true;
                        returnedSupplierIDs = criteriaList_returned.get(i).getVendorSupplierIDList().getVendorSupplierID();
                        break;
                    }
                }
                if (machedBool) {
                    List<Long> addedSupplierIDs = criteria.getVendorSupplierIDList().getVendorSupplierID();
                    for (Long addedSupplierID : addedSupplierIDs) {
                        if (!returnedSupplierIDs.contains(addedSupplierID)) {
                            returnedSupplierIDs.add(addedSupplierID);
                        }
                    }
                    criteriaList_returned.get(machedSameIndex).getVendorSupplierIDList().setVendorSupplierID(null);
                    criteriaList_returned.get(machedSameIndex).getVendorSupplierIDList().setVendorSupplierID(returnedSupplierIDs);
                } else {
                    criteriaList_returned.add(criteria);
                }
            }
        }
        return criteriaList_returned;
    }

    private static CarECommerceSearchCriteriaType createCarECommerceSearchCriteria(CarTransportationSegmentType carTransportationSegment, String currencyCode,
                                                                                   TestData testData, CarRateType carRateType, CarRateOverrideListType carRateOverrideList) {
        CarECommerceSearchCriteriaType carECommerceSearchCriteria = new CarECommerceSearchCriteriaType();
        carECommerceSearchCriteria.setCarTransportationSegment(carTransportationSegment);
        ///use the config value
        carECommerceSearchCriteria.setCurrencyCode(currencyCode);
        carECommerceSearchCriteria.setCarClassificationIDList(createCarClassificationIDList("1"));
        carECommerceSearchCriteria.setSmokingBoolean(false);
        carECommerceSearchCriteria.setPrePaidFuelBoolean(false);
        carECommerceSearchCriteria.setUnlimitedMileageBoolean(false);
        carECommerceSearchCriteria.setCarRate(carRateType);
        if (testData.getTestScenarioSpecialHandleParam() != null && testData.getTestScenarioSpecialHandleParam().getVendorSupplierID() > 0) {
            setVendorSupplierIDs(testData.getTestScenarioSpecialHandleParam().getVendorSupplierID(), carECommerceSearchCriteria);
        }
        carECommerceSearchCriteria.setSequence(1l);
        carECommerceSearchCriteria.setCarRateOverrideList(carRateOverrideList);
        return carECommerceSearchCriteria;
    }

    public static CarECommerceSearchCriteriaType createCarECommerceSearchCriteria(CarTransportationSegmentType carTransportationSegment,
                                                                                   String currencyCode, long vendorSupplierID, int sequence) {
        CarECommerceSearchCriteriaType carECommerceSearchCriteria = new CarECommerceSearchCriteriaType();
        carECommerceSearchCriteria.setCarTransportationSegment(carTransportationSegment);
        ///use the config value
        carECommerceSearchCriteria.setCurrencyCode(currencyCode);
        carECommerceSearchCriteria.setCarClassificationIDList(createCarClassificationIDList("1"));
        carECommerceSearchCriteria.setCarRate(new CarRateType());
        carECommerceSearchCriteria.setSmokingBoolean(false);
        carECommerceSearchCriteria.setPrePaidFuelBoolean(false);
        carECommerceSearchCriteria.setUnlimitedMileageBoolean(false);
        setVendorSupplierIDs(vendorSupplierID, carECommerceSearchCriteria);
        carECommerceSearchCriteria.setSequence(sequence);
        return carECommerceSearchCriteria;
    }

    private static void setVendorSupplierIDs(long vendorSupplierID, CarECommerceSearchCriteriaType carECommerceSearchCriteria) {
        VendorSupplierIDListType vendorListType = new VendorSupplierIDListType();
        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(vendorSupplierID);
        vendorListType.setVendorSupplierID(vendorSupplierIDs);
        if (vendorSupplierID > 0) carECommerceSearchCriteria.setVendorSupplierIDList(vendorListType);
    }


    private static CarClassificationIDListType createCarClassificationIDList(String carClassificationIDListString) {
        CarClassificationIDListType carClassificationIDList = new CarClassificationIDListType();
        String[] carClassificationIDArray = carClassificationIDListString.split(",");
        for (String carClassificationID : carClassificationIDArray) {
            List<Long> classificationID = new ArrayList<Long>();
            classificationID.add(Long.valueOf(carClassificationID));
            carClassificationIDList.setCarClassificationID(classificationID);
        }
        return carClassificationIDList;
    }

    public static CarECommerceSearchRequestType createCarbsSearchRequest(TestData testData) throws DataAccessException {

        final CarECommerceSearchRequestType searchRequestType = new CarECommerceSearchRequestType();
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        if (StringUtil.isNotBlank(testData.getClientCode())) {
            searchRequestType.setClientCode(testData.getClientCode());
        }
        searchRequestType.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
        searchRequestType.setSiteMessageInfo(CarCommonRequestGenerator.createSiteMessageInfoForSearch(testData.getScenarios()));
        searchRequestType.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(testData));
        searchRequestType.setCarSearchStrategy(CommonDataTypesGenerator.createCarSearchStrategy(testData.getScenarios()));
        searchRequestType.setCarECommerceSearchStrategy(createCarECommerceSearchStrategy(testData));
        searchRequestType.setCarProductCatalogID(1l);
        searchRequestType.setDisableCostListProcessingBoolean(false);
        if (testData.getScenarios().getLatitude() != null && testData.getScenarios().getLongitude() != null) {
            searchRequestType.setCarECommerceSearchCriteriaList(createCarECommerceSearchCriteriaList(testData, commonRequestGenerator, false));
        } else {
            searchRequestType.setCarECommerceSearchCriteriaList(createCarECommerceSearchCriteriaList(testData, commonRequestGenerator, true));
        }

        if (!testData.getScenarios().isOnAirPort() && (testData.getScenarios().getLatitude() == null && testData.getScenarios().getLongitude() == null)) {
            filterSearchCriteriaPerVendorForAmadeus(searchRequestType);
        }
        return searchRequestType;
    }

    public static void setPickupLocationAsIATA(CarECommerceSearchRequestType request)
    {
        for(final CarECommerceSearchCriteriaType carECommerceSearchCriteria: request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria())
        {
            carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().setCarLocationCategoryCode(null);
            carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().setSupplierRawText(null);
            carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().setCarVendorLocationID(0l);
        }
    }

    public static void setDropoffLocationAsIATA(CarECommerceSearchRequestType request) {
        for (final CarECommerceSearchCriteriaType carECommerceSearchCriteria : request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria()) {
            carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().setCarLocationCategoryCode(null);
            carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().setSupplierRawText(null);
            carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().setCarVendorLocationID(0l);
        }
    }

    public static void filterSearchCriteriaPerVendorForAmadeus(CarECommerceSearchRequestType request)
    {
        final Map searchCriteriaCountByVendor = new HashMap();
        final List<CarECommerceSearchCriteriaType> filteredList = new ArrayList<>();
        for(final CarECommerceSearchCriteriaType carECommerceSearchCriteria: request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria())
        {
            final long supplierID = carECommerceSearchCriteria.getVendorSupplierIDList().getVendorSupplierID().get(0);
            if(searchCriteriaCountByVendor.containsKey(supplierID))
            {
                final int currentValue = Integer.parseInt(searchCriteriaCountByVendor.get(supplierID).toString());
                //max SearchCriteria count is 5 Per Vendor
                if(currentValue == 5)
                {
                    continue;
                }
                searchCriteriaCountByVendor.replace(supplierID, currentValue, currentValue + 1);
            }
            else
            {
                searchCriteriaCountByVendor.put(supplierID, 1);
            }
            filteredList.add(carECommerceSearchCriteria);
        }
        request.getCarECommerceSearchCriteriaList().setCarECommerceSearchCriteria(filteredList);

    }

    public static void setDeliveryCollectionOutOfOffice(CarECommerceSearchRequestType request, CarCommonEnumManager.CollectionSet collection, CarCommonEnumManager.DeliverySet delivery,
                                                                                                                         CarCommonEnumManager.OutOfOfficeHourBooleanSet outOfOfficeHourBoolean)
    {
        for(final CarECommerceSearchCriteriaType carECommerceSearchCriteria: request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria())
        {
            if (collection != CarCommonEnumManager.CollectionSet.NonExist) {
                carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().setCollectionBoolean(collection.isCollectionSet());
            }
            if (delivery != CarCommonEnumManager.DeliverySet.NonExist) {
                carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().setDeliveryBoolean(delivery.isDeliverySet());
            }
            if (outOfOfficeHourBoolean != CarCommonEnumManager.OutOfOfficeHourBooleanSet.NonExist)
            {
                carECommerceSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().setOutOfOfficeHourBoolean(outOfOfficeHourBoolean.isOutOfOOfficeeHourBooleanSet());
                carECommerceSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().setOutOfOfficeHourBoolean(outOfOfficeHourBoolean.isOutOfOOfficeeHourBooleanSet());
            }
        }
    }
}

