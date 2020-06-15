package com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.*;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by asharma1 on 9/26/2016.
 */
public class CarsInventoryHelper
{
    final private DataSource dataSource;
    final private static double EARTH_RADIUS = 6378.137;

    public CarsInventoryHelper(DataSource datasource)
    {
        this.dataSource = datasource;
    }

    public List<SupplySubset> getSupplierSubsets(TestScenario testScenario, boolean isGetDetail) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        // TODO add support for OffAirport
        final List<CarItem> carItems = getCarItems(testScenario);
        // TODO add check for carItems > 0
        if (!CollectionUtils.isEmpty(carItems))
        {
            return carsInventoryDataSource.getSupplierSubsets(carItems, testScenario.getServiceProviderID(), isGetDetail);
        }
        return null;
    }

    public List<SupplySubset> getDistinctSupplierIds(TestScenario testScenario) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        // TODO add support for OffAirport
        final List<CarItem> carItems = getCarItems(testScenario);
        // TODO add check for carItems > 0
        if (!CollectionUtils.isEmpty(carItems))
        {
            return carsInventoryDataSource.getDistinctSupplierIds(carItems, testScenario.getServiceProviderID());
        }
        return null;
    }

    public List<CarItem> getCarItems(TestScenario testScenario) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);

        final List<PoSToCarProductCatalogMap> poSToCarProductCatalogMap = carsInventoryDataSource.getCarProductCatalogByPOS(testScenario.getJurisdictionCountryCode(), testScenario.getCompanyCode(), testScenario.getManagementUnitCode());
        final int carProductCatalogIdByPOS = poSToCarProductCatalogMap.get(0).getCarProductCatalogID();   // TODO add check for null

        final List<Airport> airports = carsInventoryDataSource.getAirports(testScenario.getPickupLocationCode());
        final String countryCode = airports.get(0).getCountryCode();    // TODO add check for null

        return carsInventoryDataSource.getCarItems(testScenario, carProductCatalogIdByPOS, countryCode);
    }

    public List<SupplySubset> getSupplierSubsets(TestScenario testScenario, long supplierID, boolean isGetDetail) throws DataAccessException
    {
        // TODO add support for OffAirport
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarItem> carItems = getCarItems(carsInventoryDataSource, testScenario);
        if (!CollectionUtils.isEmpty(carItems))
        {
            return carsInventoryDataSource.getSupplierSubsets(carItems, testScenario.getServiceProviderID(), supplierID, isGetDetail);
        }
        return null;
    }

    private List<CarItem> getCarItems(CarsInventoryDataSource carsInventoryDataSource, TestScenario testScenario) throws DataAccessException
    {
        final List<PoSToCarProductCatalogMap> poSToCarProductCatalogMap = carsInventoryDataSource.getCarProductCatalogByPOS
                (testScenario.getJurisdictionCountryCode(), testScenario.getCompanyCode(), testScenario.getManagementUnitCode());
        final List<Airport> airports = carsInventoryDataSource.getAirports(testScenario.getPickupLocationCode());
        if (!(poSToCarProductCatalogMap.isEmpty() && airports.isEmpty()))
        {
            final int carProductCatalogIdByPOS = poSToCarProductCatalogMap.get(0).getCarProductCatalogID();
            final String countryCode = airports.get(0).getCountryCode();

            return carsInventoryDataSource.getCarItems(testScenario, carProductCatalogIdByPOS, countryCode);
        }
        return null;
    }

    public Long getAuditLogTPID(TestScenario testScenario) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<AuditLogTrackingDataType> auditLogTrackingDataTypes = carsInventoryDataSource.getAuditLogTPID(testScenario.getJurisdictionCountryCode(), testScenario.getCompanyCode(), testScenario.getManagementUnitCode());
        return CollectionUtils.isEmpty(auditLogTrackingDataTypes) ? 0L : auditLogTrackingDataTypes.get(0).getAuditLogTPID();
    }

    public List<CarVendor> getCarVendorList(String carVendorCode) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getCarVendorList(carVendorCode);
    }

    public String getCarVendorCodeBySupplierID(long supplierID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarVendor> vendorList = carsInventoryDataSource.getCarVendorList(supplierID);
        return CollectionUtils.isEmpty(vendorList) ? null : vendorList.get(0).getCarVendorCode();
    }

    public CarVendorLocation getCarLocation(Long carVendorLocationID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarVendorLocation> carVendorLocations = carsInventoryDataSource.getCarLocationList(carVendorLocationID);
        return CollectionUtils.isEmpty(carVendorLocations) ? null : carVendorLocations.get(0);
    }

    public CarVendorLocation getCarLocation(String supplierID, String locationCode, String carLocationCategoryCode, String supplierRawText) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarVendorLocation> carVendorLocations = carsInventoryDataSource.getCarLocationList(supplierID, locationCode, carLocationCategoryCode, supplierRawText);
        return CollectionUtils.isEmpty(carVendorLocations) ? null : carVendorLocations.get(0);
    }

    public List<CarVendorLocation> getCarLocationList(String airPortCode, List<SupplySubset> supplySubsets) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getCarLocationList(airPortCode, supplySubsets);
    }

    public List<CarVendorLocation> getCarLocationList(String airPortCode, String carVendorLocationCode,
                                                      List<SupplySubset> supplySubsets) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return CollectionUtils.isEmpty(supplySubsets) ? null : carsInventoryDataSource.getCarLocationList(airPortCode, carVendorLocationCode, supplySubsets);
    }

    /*
     * Select the CarVendorLocationCode, Latitude and Longitude from table CarVendorLocation to query all the CarVendorLocation information based on AirportCode
     */
    public List<CarVendorLocation> getCarLocationList(String airportCode, int radiusDistanceUnitCount) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        //Get the Latitude and Longitude for Airport
        final Airport airport = getAirport(airportCode);
        final double airportLat = airport.getLatitude();
        final double airportLon = airport.getLongitude();

        final List<CarVendorLocation> carVendorLocations = carsInventoryDataSource.getCarLocationList(airportCode);
        final List<CarVendorLocation> returnCarVendorLocations = new ArrayList<>();
        for (final CarVendorLocation carVendorLocation : carVendorLocations)
        {
            final double locationLat = carVendorLocation.getLatitude();
            final double locationLon = carVendorLocation.getLongitude();
            //Filter the location code based on distance
            if (calculateDistanceLatLon(airportLat, airportLon, locationLat, locationLon) < radiusDistanceUnitCount)
            {
                final CarVendorLocation carVendorLocationInfo = new CarVendorLocation();

                final long supplierID = carVendorLocation.getVendorSupplierID();
                carVendorLocationInfo.setVendorSupplierID(supplierID);

                carVendorLocationInfo.setLocationCode(airportCode);
                carVendorLocationInfo.setCarLocationCategoryCode(carVendorLocation.getCarLocationCategoryCode());
                carVendorLocationInfo.setSupplierRawText(carVendorLocation.getSupplierRawText());

                carVendorLocationInfo.setDistance(calculateDistanceLatLon(airportLat, airportLon, locationLat, locationLon));
                carVendorLocationInfo.setCarVendorLocationID(carVendorLocation.getCarVendorLocationID());
                returnCarVendorLocations.add(carVendorLocationInfo);
            }
        }
        return returnCarVendorLocations;
    }

    //Calcaulate the distance according two point with Latitude and Longitude
    public double calculateDistanceLatLon(double airportLat, double airportLon, double locationLat, double locationLon)
    {
        final double radLat1 = rad(airportLat);
        final double radLat2 = rad(locationLat);
        final double a = radLat1 - radLat2;
        final double b = rad(airportLon) - rad(locationLon);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (Math.round(s * 10000) / 1000) / 1.6; //Convert the unit to MI: 1MI is about 1.6KM
        return s;
    }

    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }

    public Airport getAirport(String airportCode) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<Airport> airports = carsInventoryDataSource.getAirports(airportCode);
        return CollectionUtils.isEmpty(airports) ? null : airports.get(0);
    }

    @SuppressWarnings("PMD")
    public boolean isSpecificProviderCar(CarProductType car, int serviceProviderID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);

        final int myServiceID_Cost = carsInventoryDataSource.getServiceIDForSupplySubsetIDCost(car.getCarInventoryKey().getSupplySubsetID());
        final int myServiceID_Avail = carsInventoryDataSource.getServiceIDForSupplySubsetIDAvail(car.getCarInventoryKey().getSupplySubsetID());

        if (serviceProviderID == myServiceID_Cost || serviceProviderID == myServiceID_Avail)
        {
            return true;
        }

        return false;
    }

    public int getProviderId(CarProductType car) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);

        return carsInventoryDataSource.getServiceIDForSupplySubsetIDCost(car.getCarInventoryKey().getSupplySubsetID());

    }

    public int getBusinessModelID(CarProductType car) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getCarBusinessModelIDFromCarItem(car.getCarInventoryKey().getCarItemID());
    }

    public boolean checkForSpecificBusinessModelAndServiceProviderCar(CarProductType carProduct, int expectedBusinessModelID, int expectedServiceProviderID) throws DataAccessException
    {
        final int businessModelID = getBusinessModelID(carProduct);

        return ((isSpecificProviderCar(carProduct, expectedServiceProviderID)) && businessModelID == expectedBusinessModelID);
    }

    /**
     * @param carSearchResultList
     * @param expectedBusinessModelID
     * @param expectedServiceProviderID
     * @param rateCodeExistInDBFilter   true: will filter the rate code exist in database, false will do not do filter.
     * @return
     * @throws DataAccessException
     */
    @SuppressWarnings("PMD")
    public List<CarProductType> carSearchResultListFilterByBusinessModelIDAndServiceProviderID
    (CarSearchResultListType carSearchResultList, int expectedBusinessModelID, int expectedServiceProviderID, boolean rateCodeExistInDBFilter) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarProductType> carProductList = new ArrayList<CarProductType>();

        //filter by serviceProviderID and businessModelID
        for (final CarSearchResultType carSearchResult : carSearchResultList.getCarSearchResult())
        {
            for (final CarProductType car : carSearchResult.getCarProductList().getCarProduct())
            {
                if (checkForSpecificBusinessModelAndServiceProviderCar(car, expectedBusinessModelID, expectedServiceProviderID))
                {
                    carProductList.add(car);

                    // if rateCodeExistInDBFilter is not required then return the first selected product itself
                    //comment it cause always select first car.
//                    if (!rateCodeExistInDBFilter)
//                    {
//                        break;
//                    }
                }
            }
        }

        //filter by rate code if is exist in database.
        if (!carProductList.isEmpty())
        {
            if (rateCodeExistInDBFilter)
            {
                final List<CarProductType> carProductSatisfiedProviderList = new ArrayList<CarProductType>();
                carProductSatisfiedProviderList.addAll(carProductList);
                for (final CarProductType car : carProductSatisfiedProviderList)
                {
                    if (StringUtils.isEmpty(car.getCarInventoryKey().getCarRate().getRateCode()))
                    {
                        carProductList.remove(car);
                    } else
                    {
                        //Get rate code from SupplySubSetToWorldSpanSupplierItemMap
                        final List<SupplySubsetIDEntryType> paramList = new ArrayList<SupplySubsetIDEntryType>();
                        final SupplySubsetIDEntryType subsetIDEntryType = new SupplySubsetIDEntryType();
                        subsetIDEntryType.setSupplySubsetID(car.getCarInventoryKey().getSupplySubsetID());
                        paramList.add(subsetIDEntryType);

                        if (carsInventoryDataSource.getWorldSpanSupplierItemMap(paramList).isEmpty())
                        {
                            carProductList.remove(car);
                        }
                    }
                }
            }
            if (!carProductList.isEmpty())
            {
                return carProductList;
            }
        }
        return null;
    }

    public CarProductType selectCarByBusinessModelIDAndServiceProviderIDFromCarSearchResultList
            (CarSearchResultListType carSearchResultList, TestData param,
             boolean rateCodeExistInDBFilter) throws DataAccessException
    {
        final List<CarProductType> carProductList = carSearchResultListFilterByBusinessModelIDAndServiceProviderID
                (carSearchResultList, param.getScenarios().getBusinessModel(), param.getScenarios().getServiceProviderID(), rateCodeExistInDBFilter);

        final List<CarProductType> carProductList4Loop = new ArrayList<>();
        carProductList4Loop.addAll(carProductList);

        if (!carProductList.isEmpty())
        {
            if (param.getTestScenarioSpecialHandleParam().isHertzPrepayTestCase())
            {
                for (final CarProductType car : carProductList4Loop)
                {
                    if (!car.getPrePayBoolean() || car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() != 40)
                    {
                        carProductList.remove(car);
                    }
                }
            }

            if (!carProductList.isEmpty())
            {
                return carProductList.get(getRandomIndex(0, carProductList.size() - 1));
            }
        }
        return null;
    }

    public CarProductType selectCarByBusinessModelIDAndServiceProviderIDFromCarSearchResultList
            (CarSearchResultListType carSearchResultList, int expectedBusinessModelID, int expectedServiceProviderID,
             boolean rateCodeExistInDBFilter) throws DataAccessException
    {
        final List<CarProductType> carProductList = carSearchResultListFilterByBusinessModelIDAndServiceProviderID
                (carSearchResultList, expectedBusinessModelID, expectedServiceProviderID, rateCodeExistInDBFilter);
        if (!carProductList.isEmpty())
        {
            return carProductList.get(getRandomIndex(0, carProductList.size() - 1));
        }
        return null;
    }

    public List<SupplySubSetToWorldSpanSupplierItemMap> getWorldSpanSupplierItemMap(List<SupplySubsetIDEntryType> subsetIDEntryTypeList) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getWorldSpanSupplierItemMap(subsetIDEntryTypeList);
    }

    public SupplySubSetToWorldSpanSupplierItemMap getWorldSpanSupplierItemMap(Long subsetID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<SupplySubSetToWorldSpanSupplierItemMap> ss2wscsMap = carsInventoryDataSource.getWorldSpanSupplierItemMap(subsetID);

        if (null != ss2wscsMap && !ss2wscsMap.isEmpty())
        {
            return ss2wscsMap.get(0);
        }
        return null;
    }

    public String getCarBehaviorAttributValue(Long supplierID, Long supplySubsetID, Long carBehaviorAttID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getCarBehaviorAttributValue(supplierID, supplySubsetID, carBehaviorAttID);
    }

    public CarConfigurationFormat getCarConfigurationFormat(long supplySubSetID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarConfigurationFormat> configurationFormatList = carsInventoryDataSource.getCarConfigurationFormatList(supplySubSetID);
        return CompareUtil.isObjEmpty(configurationFormatList) ? null : configurationFormatList.get(0);
    }

    public CarItem getCarItemById(long carItemId) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarItem> carItemList = carsInventoryDataSource.getCarItemListById(carItemId);
        return CompareUtil.isObjEmpty(carItemList) ? null : carItemList.get(0);
    }

    public List<CarItem> getCarItemListBySubsetID(List<SupplySubset> subsetIDList) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getCarItemListBySubsetID(subsetIDList);
    }

    protected int getRandomIndex(int min, int max)
    {
        final Random s_rand = new Random(System.nanoTime());
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return s_rand.nextInt(max - min + 1) + min;

    }

    public CarCancelFee getCancelFeeByTPID(Long tpid) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarCancelFee> cancelFees = carsInventoryDataSource.getCancelFeeByTPID(tpid);
        return CompareUtil.isObjEmpty(cancelFees) ? null : cancelFees.get(0);
    }

    public List<CarShareTarget> getShareByMarketPct(long tpid, String airportCode) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarShareTarget> shareTargetsList = carsInventoryDataSource.getShareByMarketPct(tpid, airportCode);
        return CompareUtil.isObjEmpty(shareTargetsList) ? null : shareTargetsList;
    }

    public boolean isEgenciaPOSByTPID(Long tpid) throws DataAccessException
    {

        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarShareTarget> shareTargetsList = carsInventoryDataSource.isEgenciaPOSByTPID(tpid);
        return CompareUtil.isObjEmpty(shareTargetsList) ? false : true;
    }

    public List<CarVendorLocationLatLong> excuteCarVendorLocationProc(String procName, List<String> paramList) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.excuteCarVendorLocationProc(procName, paramList);
    }

    public CarCatalogMakeModelType getMakeModelInfo(long mediaID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarCatalogMakeModelType> makeModelList = carsInventoryDataSource.getMakeModelListByMediaID(mediaID);
        return CompareUtil.isObjEmpty(makeModelList) ? null : makeModelList.get(0);
    }

    public boolean existMediaInfoForRequestedCar(long supplierID, long carCategoryID, long carTypeID, String locationCode) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        List<CarCatalogMakeModelType> makeModelList = carsInventoryDataSource.getCarMediaListBySupplierCarTypeLocation(supplierID,
                carCategoryID, carTypeID, locationCode);
        if(CompareUtil.isObjEmpty(makeModelList)){
            makeModelList = carsInventoryDataSource.getCarMediaListBySupplierCarTypeLocation(0,
                    carCategoryID, carTypeID, locationCode);
        }
        return CompareUtil.isObjEmpty(makeModelList) ? false : true;
    }

    public CarCatalogMakeModelType getACRISSInfo(CarVehicleType vehicle) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarCatalogMakeModelType> acrissInfoList = carsInventoryDataSource.getACRISSInfo(vehicle.getCarCategoryCode(),
                vehicle.getCarTypeCode(), vehicle.getCarTransmissionDriveCode(), vehicle.getCarFuelACCode());
        return CompareUtil.isObjEmpty(acrissInfoList) ? null : acrissInfoList.get(0);
    }

    public BigDecimal getCommissionDetailsByCarItemIDAndAirportCode(Long carItemID, String airportCode) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        List<CarCommission> carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndAirportCode(carItemID, airportCode);
        if (CompareUtil.isObjEmpty(carCommissionList))
        {
            carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndAirportCode(carItemID, "");
        }
        return CompareUtil.isObjEmpty(carCommissionList) ? null : carCommissionList.get(0).getCommissionPct();
    }

    public CarCommission getCommissionInfoByCarItemIDAndAirportCode(Long carItemID, String airportCode) throws DataAccessException {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        List<CarCommission> carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndAirportCode(carItemID, airportCode);
        //get countrycode from pickupLocation
        final List<Airport> airportInfo = carsInventoryDataSource.getAirports(airportCode);
        //if no country found for the pickuplocation, set countryCode as global(3 space)
        String countryCode = "   ";

        if (!airportInfo.isEmpty())
        {
            countryCode = airportInfo.get(0).getCountryCode();
        }
        if(CompareUtil.isObjEmpty(carCommissionList))
        {
            carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndCountryCode(carItemID, countryCode);
        }
        if(CompareUtil.isObjEmpty(carCommissionList))
        {
            carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndCountryCode(carItemID, "");
        }
        return CompareUtil.isObjEmpty(carCommissionList) ? null : carCommissionList.get(0);
    }

    public CarCommission getCommissionInfoByCarCatalogKey(CarInventoryKeyType key) throws DataAccessException {
        final CarCatalogKeyType carCatalogKey = key.getCarCatalogKey();
        final CarLocationKeyType pickupLocation = carCatalogKey.getCarPickupLocationKey();
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        List<CarCommission> carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndAirportCode(key.getCarItemID(), pickupLocation.getLocationCode());
        //get countrycode from pickupLocation
        final List<CarVendorLocation> locationList = carsInventoryDataSource.getCarLocationList(String.valueOf(carCatalogKey.getVendorSupplierID()), pickupLocation.getLocationCode(), pickupLocation.getCarLocationCategoryCode(),
                pickupLocation.getSupplierRawText());
        //if no country found for the pickuplocation, set countryCode as global(3 space)
        String countryCode = "   ";

        if (locationList.isEmpty())
        {
            final List<Airport> airportInfo = carsInventoryDataSource.getAirports(pickupLocation.getLocationCode());
            if (!airportInfo.isEmpty())
            {
                countryCode = airportInfo.get(0).getCountryCode();
            }
        }
        else
        {
            countryCode = locationList.get(0).getiSOCountryCode();
        }
        if(CompareUtil.isObjEmpty(carCommissionList))
        {
            carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndCountryCode(key.getCarItemID(), countryCode);
        }
        if(CompareUtil.isObjEmpty(carCommissionList))
        {
            carCommissionList = carsInventoryDataSource.getCommissionByCarItemIDAndCountryCode(key.getCarItemID(), "");
        }
        return CompareUtil.isObjEmpty(carCommissionList) ? null : carCommissionList.get(0);
    }

    public String getVendorCollectsFlagGDSPOneWayDropOffCharge(Long vendorSupplierID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        return carsInventoryDataSource.getVendorCollectsFlagGDSPOneWayDropOffCharge(vendorSupplierID);
    }

    public String getCarShuttleCategoryCode(Long carVendorLocationID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarVendorLocation> carVendorLocations = carsInventoryDataSource.getCarLocationList(carVendorLocationID);
        String carShuttleCategoryID = "";
        if(!CollectionUtils.isEmpty(carVendorLocations))
        {
            carShuttleCategoryID = carVendorLocations.get(0).getCarShuttleCategoryID();
        }

        final String sqlQuery = " Select CarShuttleCategoryID, CarShuttleCategoryCode from CarShuttleCategory " +
                "where  CarShuttleCategoryID = :carShuttleCategoryID";
        final ParametrizedQuery<CarShuttleCategory> tsql = new ParametrizedQuery<>(sqlQuery, dataSource, CarShuttleCategory.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("carShuttleCategoryID", carShuttleCategoryID);
        final List<CarShuttleCategory> carShuttleCategoryCodes = tsql.execute(paramMap);
        return CollectionUtils.isEmpty(carShuttleCategoryCodes)? null : carShuttleCategoryCodes.get(0).getCarShuttleCategoryCode();
    }

    public List<CarPromotion> getCarsPromotionsConfigured(TestScenario testScenario, String supplierID) throws DataAccessException
    {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(dataSource);
        final List<CarPromotion> carPromotionList = carsInventoryDataSource.getCarsPromotionsConfigured(
                testScenario.getJurisdictionCountryCode(), testScenario.getCompanyCode(), testScenario.getManagementUnitCode(),
                testScenario.getPickupLocationCode(), supplierID, !testScenario.isStandalone(), testScenario.getBusinessModel());

        return CompareUtil.isObjEmpty(carPromotionList) ? null : carPromotionList;
    }
}