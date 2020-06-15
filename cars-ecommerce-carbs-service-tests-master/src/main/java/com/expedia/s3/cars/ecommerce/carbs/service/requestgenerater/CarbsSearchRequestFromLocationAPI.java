package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideListType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateOverrideType;
import com.expedia.e3.data.cartypes.defn.v5.CarTransportationSegmentType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaListType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchCriteriaType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 1/4/2018.
 */
public class CarbsSearchRequestFromLocationAPI {
    private static final String MESSAGE_VERSION = "4.1.0";
    private static final String MESSAGE_NAME = "CarECommerceSearchRequest";

    private CarbsSearchRequestFromLocationAPI()
    {}

    public static CarECommerceSearchRequestType buildCarBSSearchRequestFromLocationAPI(TestData testData, CarLocationIataSearchResponse pickLocationRsp, CarLocationIataSearchResponse dropLocationRsp) throws DataAccessException {
        final CarECommerceSearchRequestType searchRequest = new CarECommerceSearchRequestType();
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        if(!StringUtils.isEmpty(testData.getClientCode())){
            searchRequest.setClientCode(testData.getClientCode());
        }
        searchRequest.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
        searchRequest.setSiteMessageInfo(CarCommonRequestGenerator.createSiteMessageInfoForSearch(testData.getScenarios()));
        searchRequest.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(testData));
        searchRequest.setCarSearchStrategy(CommonDataTypesGenerator.createCarSearchStrategy(testData.getScenarios()));
        searchRequest.setCarECommerceSearchStrategy(CarbsSearchRequestGenerator.createCarECommerceSearchStrategy(testData));
        searchRequest.setCarProductCatalogID(0l);
        searchRequest.setDisableCostListProcessingBoolean(false);

        //-----------------------------------------------------------------------------------------------------
        // Build searchCriteria from Location list  -- single or multiple search criteria ??
        //-----------------------------------------------------------------------------------------------------
        final List<CarLocationType> pickLocationList = pickLocationRsp.getCarLocationList().getCarLocation();
        List<CarLocationType> dropLocationList = null;
        if (dropLocationRsp != null) {
            dropLocationList = dropLocationRsp.getCarLocationList().getCarLocation();
        }

        // if not specify any vendor code in config, just return these two location lists to build search request.
        if ((null == testData.getTestScenarioSpecialHandleParam()) || ( null != testData.getTestScenarioSpecialHandleParam() && testData.getTestScenarioSpecialHandleParam().getVendorCode() == null))
        {
            searchRequest.setCarECommerceSearchCriteriaList(buildCarSearchCriteriaListFromLocationSearch(
                    testData, pickLocationList, dropLocationList, commonRequestGenerator));
        }

        // filter out all the CarLocations which has the vendor that specified in config file.
        else if (null != testData.getTestScenarioSpecialHandleParam().getVendorCode())
        {
            final String expVendor = testData.getTestScenarioSpecialHandleParam().getVendorCode();
            final long expSupplierID = Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(expVendor));

            List<CarLocationType> expLocationListDropoff = null;

            // filter out the carlocations from fist locationlist by specified vendor
            final List<CarLocationType> expLocationListPickup = filterLocationListBySupplier(pickLocationList, expSupplierID);

            // if there is a config(location) for drop off
            if (dropLocationRsp != null)
            {
                expLocationListDropoff = filterLocationListBySupplier(dropLocationList, expSupplierID);
            }

            // to build CarBS search request based on these two location lists.
            searchRequest.setCarECommerceSearchCriteriaList(buildCarSearchCriteriaListFromLocationSearch(
                    testData, expLocationListPickup, expLocationListDropoff, commonRequestGenerator));
        }

        return searchRequest;
    }

    public static List<CarLocationType> filterLocationListBySupplier(List<CarLocationType> locationList, long expSupplierID)
    {
        final List<CarLocationType> filteredLocationList = new ArrayList<>();
        for (final CarLocationType carLocation : locationList)
        {
            if (carLocation.getSupplierID() == expSupplierID) {
                filteredLocationList.add(carLocation);
            }
        }
        // if no any carlocation has the vendor, then failed this test case.
        if (filteredLocationList.isEmpty())
        {
            Assert.fail("No expected supplier " + expSupplierID + " returned in location search reponse, please try to change the location for pickup");
        }

        return filteredLocationList;
    }

    public static CarECommerceSearchCriteriaListType buildCarSearchCriteriaListFromLocationSearch(
            TestData testData, List<CarLocationType> pickLocationList, List<CarLocationType> dropLocationList,
            CommonRequestGenerator commonRequestGenerator) throws DataAccessException {
        final CarECommerceSearchCriteriaListType searchCriteriaList = new CarECommerceSearchCriteriaListType();

        // Build roundtrip search criteria if LocationList_drop == null
        if (dropLocationList == null)
        {
            int sequence = 0;
            //build searchCriteria from one Location
            for (final CarLocationType pickLocation : pickLocationList)
            {
                sequence = buildCarECommerceSearchCriteriaFromLocation(pickLocation, pickLocation, searchCriteriaList, testData, sequence, commonRequestGenerator);
            }
        }
        // build OneWay in search criteria if LocationList_drop is not null.
        else
        {
            buildOneWaySearchCriteriaList(pickLocationList, dropLocationList, searchCriteriaList,
                    testData, commonRequestGenerator);

        }
        return searchCriteriaList;
    }

    public static void buildOneWaySearchCriteriaList(List<CarLocationType> pickLocationList, List<CarLocationType> dropLocationList,
                                                     CarECommerceSearchCriteriaListType searchCriteriaList,
                                                     TestData testData, CommonRequestGenerator commonRequestGenerator) throws DataAccessException {
        // build with the same location and vendor at top pririty
        final List<CarLocationType> pickLocationListWithNoSameDropLocation = new ArrayList<>();
        int sequence = 0;
        for(final CarLocationType pickLocation : pickLocationList)
        {
            boolean haveSameKey = false;
            for (final CarLocationType dropLocation : dropLocationList)
            {
                if (isLocationSame(pickLocation, dropLocation))
                {
                    sequence = buildCarECommerceSearchCriteriaFromLocation(pickLocation, dropLocation, searchCriteriaList, testData, sequence, commonRequestGenerator);
                    haveSameKey = true;
                }
            }
            if (!haveSameKey) {
                pickLocationListWithNoSameDropLocation.add(pickLocation);
            }
        }

        // buld searchCriteria from different location with same supplier
        for (final CarLocationType pickLocation : pickLocationListWithNoSameDropLocation)
        {
            for (final CarLocationType dropLocation : dropLocationList)
            {
                if (pickLocation.getSupplierID() == dropLocation.getSupplierID())
                {
                    sequence = buildCarECommerceSearchCriteriaFromLocation(pickLocation, dropLocation, searchCriteriaList, testData, sequence, commonRequestGenerator);
                }
            }
        }
    }

    public static boolean isLocationSame(CarLocationType pickLocation, CarLocationType dropLocation)
    {
        boolean isSameLocation = false;
        if (pickLocation.getSupplierID() == dropLocation.getSupplierID()
                && pickLocation.getCarLocationKey().getSupplierRawText() == dropLocation.getCarLocationKey().getSupplierRawText()
                && pickLocation.getCarLocationKey().getCarLocationCategoryCode() == dropLocation.getCarLocationKey().getCarLocationCategoryCode())
        {
            isSameLocation = true;
        }
        return isSameLocation;
    }

    public static int buildCarECommerceSearchCriteriaFromLocation(CarLocationType pickLocation, CarLocationType dropLocation, CarECommerceSearchCriteriaListType searchCriteriaList,
                             TestData testData, int sequence, CommonRequestGenerator commonRequestGenerator) throws DataAccessException {
        final int vendorCriteriaLimit = 5;
        final String currencyCode = testData.getScenarios().getSupplierCurrencyCode();
        final CarVendorLocation pickCarVendorLocation = buildCarVendorLocationFromLocationAPI(pickLocation);
        final CarVendorLocation dropoffCarVendorLocation = buildCarVendorLocationFromLocationAPI(dropLocation);
        // no more than 5 criterias per vendor
        boolean supplierLimitExceed = false;
        if (null != searchCriteriaList.getCarECommerceSearchCriteria()) {
            supplierLimitExceed = supplierLimitExceed(searchCriteriaList, pickCarVendorLocation.getVendorSupplierID(), vendorCriteriaLimit);
        }
        if (!supplierLimitExceed) {
            final CarTransportationSegmentType carTransportationSegment = commonRequestGenerator.createCarTransportationSegmentType(pickCarVendorLocation, dropoffCarVendorLocation, testData);
            final CarECommerceSearchCriteriaType carECommerceSearchCriteria = CarbsSearchRequestGenerator.createCarECommerceSearchCriteria(carTransportationSegment, currencyCode, pickCarVendorLocation.getVendorSupplierID(), sequence);

            // support for CD code
            final List<CarRateOverrideType> carRateOverrideTypeList = commonRequestGenerator.createCDCodes(testData.getCarRate());
            if (null != carRateOverrideTypeList) {
                carECommerceSearchCriteria.setCarRateOverrideList(new CarRateOverrideListType());
                carECommerceSearchCriteria.getCarRateOverrideList().setCarRateOverride(carRateOverrideTypeList);
            }
            if (null == searchCriteriaList.getCarECommerceSearchCriteria())
            {
                final ArrayList<CarECommerceSearchCriteriaType> carECommerceSearchCriteriaTypes = new ArrayList<>();
                carECommerceSearchCriteriaTypes.add(carECommerceSearchCriteria);
                searchCriteriaList.setCarECommerceSearchCriteria(carECommerceSearchCriteriaTypes);
            }
            else {
                searchCriteriaList.getCarECommerceSearchCriteria().add(carECommerceSearchCriteria);
            }
        }
        return sequence + 1;
    }

    //-----------------------------------------------------------------------------------------------------
    // Build the offAiprportLocation key with the location Search result node.
    //-----------------------------------------------------------------------------------------------------
    public static CarVendorLocation buildCarVendorLocationFromLocationAPI(CarLocationType location)
    {
        final CarVendorLocation carVendorLocation = new CarVendorLocation();
        carVendorLocation.vendorSupplierID = location.getSupplierID();
        carVendorLocation.locationCode = location.getCarLocationKey().getLocationCode();
        carVendorLocation.supplierRawText = location.getCarLocationKey().getSupplierRawText();
        carVendorLocation.carLocationCategoryCode = location.getCarLocationKey().getCarLocationCategoryCode();
        return carVendorLocation;
    }

    public static boolean supplierLimitExceed(CarECommerceSearchCriteriaListType carECommerceSearchCriteriaList,
                                                   long vendorSupplierID,int searchCriteriaLimitBySupplier)
    {
        boolean supplierLimitExceed = false;
        //more than 5 cearch criteria per vendor.
        int sameVendorCriteriaCount = 0;
        for (final CarECommerceSearchCriteriaType criteria : carECommerceSearchCriteriaList.getCarECommerceSearchCriteria())
        {

            if (vendorSupplierID == criteria.getVendorSupplierIDList().getVendorSupplierID().get(0)) {
                sameVendorCriteriaCount++;
            }
        }
        // default value is 5 = searchCriteriaForVendor
        if (sameVendorCriteriaCount >= searchCriteriaLimitBySupplier)
        {
            supplierLimitExceed = true;
        }

        return supplierLimitExceed;
    }




}
