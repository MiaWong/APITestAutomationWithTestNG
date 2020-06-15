package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by asharma1 on 10/3/2016.
 */
@SuppressWarnings("PMD")
public class CarsInventoryDataSource
{
    final private DataSource dataSource;

    public CarsInventoryDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("CPD-START")
    public List<Airport> getAirports(String airportCode) throws DataAccessException
    {
        final ParametrizedQuery<Airport> tsql = new ParametrizedQuery<Airport>("Select AirportCode, CountryCode, Latitude, Longitude from Airport where AirportCode = :pAirportCode", dataSource, Airport.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pAirportCode", airportCode);

        return tsql.execute(paramMap);
    }

    public List<PoSToCarProductCatalogMap> getCarProductCatalogByPOS(String pJurisdictionCode, String pCompanyCode, String pManagementUnitCode) throws DataAccessException
    {
        final String sqlQuery = "Select JurisdictionCode, CompanyCode, ManagementUnitCode, CarProductCatalogID from PoSToCarProductCatalogMap \n" +
                "where JurisdictionCode = :pJurisdictionCode and CompanyCode = :pCompanyCode and ManagementUnitCode = :pManagementUnitCode";
        final ParametrizedQuery<PoSToCarProductCatalogMap> tsql = new ParametrizedQuery<PoSToCarProductCatalogMap>(sqlQuery, dataSource, PoSToCarProductCatalogMap.class);

        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("pJurisdictionCode", pJurisdictionCode);
        paramMap.put("pCompanyCode", pCompanyCode);
        paramMap.put("pManagementUnitCode", pManagementUnitCode);

        return tsql.execute(paramMap);
    }

    public List<CarItem> getCarItems(TestScenario testScenario, int pCarProductCatalogID, String countryCode) throws DataAccessException
    {
        final String sqlQuery = "SELECT distinct ci.CarItemID,SupplierID FROM CarProductCatalogChildCarItem cpccci \n" +
                "join CarItem ci on ci.CarItemID=cpccci.CarItemID \n" +
                "join CarProductCatalogChild cpcc on cpccci.CarProductCatalogChildID=cpcc.CarProductCatalogChildID \n" +
                "where ci.ActiveBool=1 and ci.StandaloneBool = :pStandaloneBool \n" +
                "and cpccci.ActiveBool=1 \n" +
                "and cpcc.ActiveBool=1 and cpcc.FlightOption = :pFlightOption and cpcc.HotelOption = :pHotelOption and cpcc.OneWayBool = :pOneWayBool and cpcc.OnAirBool = :pOnAirBool \n" +
                "and cpcc.CarProductCatalogID= :pCarProductCatalogID and cpcc.PickupCountryCode in (:pPickupCountryCode) \n" +
                "order by ci.CarItemID desc";

        final ParametrizedQuery<CarItem> tsql = new ParametrizedQuery<CarItem>(sqlQuery, dataSource, CarItem.class);

        final Map<String, Object> paramMap = new HashMap<>(7);
        final List<String> countryCodes = new ArrayList<String>();
        countryCodes.add(countryCode);
        //countryCode is null when businessModel is agency
        if (testScenario.getBusinessModel() == BusinessModel.Agency.getValue())
        {
            countryCodes.add("");
        }
        paramMap.put("pStandaloneBool", testScenario.isStandalone());
        paramMap.put("pFlightOption", testScenario.getFlightOption());
        paramMap.put("pHotelOption", testScenario.getHotelOption());
        paramMap.put("pOneWayBool", testScenario.isOneWay());
        paramMap.put("pOnAirBool", testScenario.isOnAirPort());
        paramMap.put("pPickupCountryCode", countryCodes.stream().map(i -> i).collect(Collectors.toList()));
        paramMap.put("pCarProductCatalogID", pCarProductCatalogID);

        //Get child CarItemIDs
        return getChildCarItemIDs(tsql.execute(paramMap), testScenario);
    }

    public List<SupplySubset> getSupplierSubsets(List<CarItem> carItems, int supplyConnectivityServiceID, boolean isGetDetail) throws DataAccessException
    {
        return getSupplierSubsets(carItems, supplyConnectivityServiceID, 0, isGetDetail);
    }

    public List<CarItem> getChildCarItemIDs(List<CarItem> carItems, TestScenario testScenario) throws DataAccessException
    {
        // Get child carItemIDs for package CarItemID
        if (testScenario.isStandalone())
        {
            return carItems;
        } else
        {
            String sqlQuery = "";
            // string.Format("select ChildCarItemID as CarItemID from {0}..CarItemParentChild where ParentCarItemID in ({1}) " +
            //"and ChildCarItemID not in (1,2) ", GetDatabaseFromConnectionString(connectionString), carItemIDString);
            sqlQuery = "Select ChildCarItemID as CarItemID \n" +
                    "from CarItemParentChild \n" +
                    "where ParentCarItemID in (:pCarItemIDs) \n" +
                    "and ChildCarItemID not in (1,2)  \n";

            final ParametrizedQuery<CarItem> tsql = new ParametrizedQuery<CarItem>(sqlQuery, dataSource, CarItem.class);

            final Map<String, Object> paramMap = new HashMap<>(2);
            paramMap.put("pCarItemIDs", carItems.stream()
                    .map(i -> i.getCarItemID())
                    .collect(Collectors.toList()));

            return tsql.execute(paramMap);
        }
    }

    public List<SupplySubset> getSupplierSubsets(List<CarItem> carItems, int supplyConnectivityServiceID, long supplierID, boolean isGetDetail) throws DataAccessException
    {
        // TODO any change needed for SupplyRoutingCategoryID?
        String sqlQuery = "";
        if (isGetDetail)
        {
            sqlQuery = "Select cissr.SupplySubsetID,ci.CarBusinessModelID, ci.SupplierID \n" +
                    "from CarItemSupplySubsetRank cissr \n" +
                    "join caritem ci on cissr.CarItemID=ci.CarItemID \n" +
                    "join SupplySubsetToSupplyConnectivityServiceMap ss on cissr.SupplySubsetID = ss.SupplySubsetID \n" +
                    "where cissr.CarItemID in (:pCarItemIDs) \n" +
                    "and ss.SupplyConnectivityServiceID = :pSupplyConnectivityServiceID and ss.SupplyRoutingCategoryID = 1 \n";

            if (supplierID > 0)
            {
                sqlQuery = sqlQuery + "and ci.SupplierID = :pSupplierID \n";
            }
            sqlQuery = sqlQuery + "order by ci.SupplierID asc";
        } else
        {
            sqlQuery = "Select distinct cissr.SupplySubsetID \n" +
                    "from CarItemSupplySubsetRank cissr \n" +
                    "join caritem ci on cissr.CarItemID=ci.CarItemID \n" +
                    "join SupplySubsetToSupplyConnectivityServiceMap ss on cissr.SupplySubsetID = ss.SupplySubsetID \n" +
                    "where cissr.CarItemID in (:pCarItemIDs) \n" +
                    "and ss.SupplyConnectivityServiceID = :pSupplyConnectivityServiceID \n";
            if (supplierID > 0)
            {
                sqlQuery = sqlQuery + "and ci.SupplierID = :pSupplierID ";
            }
            sqlQuery = sqlQuery + "and ss.SupplyRoutingCategoryID = 1 ";
        }
        final ParametrizedQuery<SupplySubset> tsql = new ParametrizedQuery<SupplySubset>(sqlQuery, dataSource, SupplySubset.class);

        final Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("pCarItemIDs", carItems.stream()
                .map(i -> i.getCarItemID())
                .collect(Collectors.toList()));
        paramMap.put("pSupplyConnectivityServiceID", supplyConnectivityServiceID);
        paramMap.put("pSupplierID", supplierID);

        return tsql.execute(paramMap);
    }

    public List<SupplySubset> getDistinctSupplierIds(List<CarItem> carItems, int supplyConnectivityServiceID) throws DataAccessException
    {
        // TODO any change needed for SupplyRoutingCategoryID?
        String sqlQuery = "Select distinct ci.SupplierID \n" +
                "from CarItemSupplySubsetRank cissr \n" +
                "join caritem ci on cissr.CarItemID=ci.CarItemID \n" +
                "join SupplySubsetToSupplyConnectivityServiceMap ss on cissr.SupplySubsetID = ss.SupplySubsetID \n" +
                "where cissr.CarItemID in (:pCarItemIDs) \n" +
                "and ss.SupplyConnectivityServiceID = :pSupplyConnectivityServiceID and ss.SupplyRoutingCategoryID = 1 ";
        final ParametrizedQuery<SupplySubset> tsql = new ParametrizedQuery<SupplySubset>(sqlQuery, dataSource, SupplySubset.class);

        final Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("pCarItemIDs", carItems.stream()
                .map(i -> i.getCarItemID())
                .collect(Collectors.toList()));
        paramMap.put("pSupplyConnectivityServiceID", supplyConnectivityServiceID);

        return tsql.execute(paramMap);
    }

    public List<AuditLogTrackingDataType> getAuditLogTPID(String pJurisdictionCode, String pCompanyCode, String pManagementUnitCode) throws DataAccessException
    {
        final String sqlQuery = "Select TravelProductID as AuditLogTPID, PartnerID as auditLogEAPID from TPIDToPoSAttributeMap  " +
                "where JurisdictionCode = :pJurisdictionCode and CompanyCode = :pCompanyCode and ManagementUnitCode = :pManagementUnitCode ";
        final ParametrizedQuery<AuditLogTrackingDataType> tsql = new ParametrizedQuery<AuditLogTrackingDataType>(sqlQuery, dataSource, AuditLogTrackingDataType.class);
        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("pJurisdictionCode", pJurisdictionCode);
        paramMap.put("pCompanyCode", pCompanyCode);
        paramMap.put("pManagementUnitCode", pManagementUnitCode);
        return tsql.execute(paramMap);
    }

    public List<CarVendorLocation> getCarLocationList(String airPortCode, List<SupplySubset> supplySubsets) throws DataAccessException
    {
        String sqlQuery = " Select CarVendorLocationID, AirportCode as locationCode, substring(CarVendorLocationCode,1,1) as carLocationCategoryCode,substring(CarVendorLocationCode,2,len(CarVendorLocationCode)-1) as supplierRawText, SupplierID as vendorSupplierID, Latitude, Longitude from CarVendorLocation " +
                "where AirportCode = :airPortCode and (StatusCode = 'A' or StatusCode = '5') ";
        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("airPortCode", airPortCode);
        if (!CompareUtil.isObjEmpty(supplySubsets))
        {
            sqlQuery = sqlQuery + " and SupplierID in (:supplierIDs) ";
            paramMap.put("supplierIDs", supplySubsets.stream()
                    .map(i -> i.getSupplierID())
                    .collect(Collectors.toList()));
        }
        final ParametrizedQuery<CarVendorLocation> tsql = new ParametrizedQuery<CarVendorLocation>(sqlQuery, dataSource, CarVendorLocation.class);
        return tsql.execute(paramMap);
    }

    public List<CarVendorLocation> getCarLocationList(String supplierID, String locationCode, String carLocationCategoryCode, String supplierRawText) throws DataAccessException
    {
        final String sqlQuery = " Select CarVendorLocationID, AirportCode as locationCode, substring(CarVendorLocationCode,1,1) as carLocationCategoryCode,substring(CarVendorLocationCode,2,len(CarVendorLocationCode)-1) as supplierRawText, SupplierID as vendorSupplierID, Latitude, Longitude, ISOCountryCode from CarVendorLocation " +
                "where AirportCode = :airPortCode and SupplierID = :supplierID and CarVendorLocationCode = :carVendorLocationCode";
        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("airPortCode", locationCode);
        paramMap.put("supplierID", supplierID);
        paramMap.put("carVendorLocationCode", carLocationCategoryCode + supplierRawText);
        final ParametrizedQuery<CarVendorLocation> tsql = new ParametrizedQuery<>(sqlQuery, dataSource, CarVendorLocation.class);
        return tsql.execute(paramMap);
    }

    public List<CarVendorLocation> getCarLocationList(String airPortCode, String carVendorLocationCode,
                                                      List<SupplySubset> supplySubsets) throws DataAccessException
    {
        final String sqlQuery = " Select CarVendorLocationID, AirportCode as locationCode, substring(CarVendorLocationCode,1,1) as carLocationCategoryCode,substring(CarVendorLocationCode,2,len(CarVendorLocationCode)-1) as supplierRawText, " +
                "SupplierID as vendorSupplierID, Latitude, Longitude from CarVendorLocation " +
                "where AirportCode = :airPortCode and CarVendorLocationCode = :sCarVendorLocationCode " +
                "and (StatusCode = 'A' or StatusCode = '5') and SupplierID in (:supplierIDs)";
        final ParametrizedQuery<CarVendorLocation> tsql = new ParametrizedQuery<CarVendorLocation>(sqlQuery, dataSource, CarVendorLocation.class);
        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("airPortCode", airPortCode);
        if (carVendorLocationCode.length() < 4)
        {
            carVendorLocationCode = carVendorLocationCode.substring(0, 1) + "0" + carVendorLocationCode.substring(1);
        }
        paramMap.put("sCarVendorLocationCode", carVendorLocationCode);
        paramMap.put("supplierIDs", supplySubsets.stream()
                .map(i -> i.getSupplierID())
                .collect(Collectors.toList()));
        return tsql.execute(paramMap);
    }

    public List<CarVendorLocation> getCarLocationList(Long carVendorLocationID) throws DataAccessException
    {
        final String sqlQuery = " Select CarVendorLocationID,AirportCode as locationCode, substring(CarVendorLocationCode,1,1) as carLocationCategoryCode,substring(CarVendorLocationCode,2,len(CarVendorLocationCode)-1) as supplierRawText, SupplierID as vendorSupplierID, Latitude, Longitude, CarShuttleCategoryID from CarVendorLocation " +
                "where CarVendorLocationID = :carVendorLocationID and (StatusCode = 'A' or StatusCode = '5') ";
        final ParametrizedQuery<CarVendorLocation> tsql = new ParametrizedQuery<CarVendorLocation>(sqlQuery, dataSource, CarVendorLocation.class);
        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("carVendorLocationID", carVendorLocationID);
        return tsql.execute(paramMap);
    }

    public List<CarVendorLocation> getCarLocationList(String airportCode) throws DataAccessException
    {

        final String sqlQuery = " Select CarVendorLocationID,AirportCode as locationCode, substring(CarVendorLocationCode,1,1) as carLocationCategoryCode,substring(CarVendorLocationCode,2,len(CarVendorLocationCode)-1) as supplierRawText, SupplierID as vendorSupplierID,Latitude, Longitude from CarVendorLocation " +
                "where AirportCode = :airportCode and (StatusCode = 'A' or StatusCode = '5') ";

        final ParametrizedQuery<CarVendorLocation> tsql = new ParametrizedQuery<CarVendorLocation>(sqlQuery, dataSource, CarVendorLocation.class);
        final Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("airportCode", airportCode);
        return tsql.execute(paramMap);
    }

    public List<CarVendor> getCarVendorList(String carVendorCode) throws DataAccessException
    {
        final StringBuffer sqlQuery = new StringBuffer(" Select CarVendorCode,CarVendorID,SupplierID from CarVendor ");
        final Map<String, Object> paramMap = new HashMap<>(1);

        if (StringUtil.isNotBlank(carVendorCode))
        {
            sqlQuery.append("where CarVendorCode = :carVendorCode");
            paramMap.put("carVendorCode", carVendorCode);
        }


        final ParametrizedQuery<CarVendor> tsql = new ParametrizedQuery<CarVendor>(sqlQuery.toString(), dataSource, CarVendor.class);

        return tsql.execute(paramMap);
    }

    public List<CarVendor> getCarVendorList(long supplierID) throws DataAccessException
    {
        final StringBuffer sqlQuery = new StringBuffer(" Select CarVendorCode,CarVendorID,SupplierID from CarVendor ");
        final Map<String, Object> paramMap = new HashMap<>(1);

        sqlQuery.append("where SupplierID = :supplierID");
        paramMap.put("supplierID", supplierID);

        final ParametrizedQuery<CarVendor> tsql = new ParametrizedQuery<CarVendor>(sqlQuery.toString(), dataSource, CarVendor.class);

        return tsql.execute(paramMap);
    }

    public long getSupplySubSetIDByCarItemID(int carItemID) throws DataAccessException
    {
        long supplySubsetID = 0;

        final String sqlQuery = "select cissr.SupplySubsetID " +
                "from CarItemSupplySubsetRank cissr join caritem ci on cissr.CarItemID=ci.CarItemID " +
                " and cissr.CarItemID = (:pCarItemID) ";

        final ParametrizedQuery<SupplySubset> tsql = new ParametrizedQuery<SupplySubset>(sqlQuery, dataSource, SupplySubset.class);

        final Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("pCarItemID", carItemID);

        if (tsql.execute(paramMap).size() > 0)
        {
            supplySubsetID = tsql.execute(paramMap).get(0).getSupplySubsetID();
        }

        return supplySubsetID;
    }

    public List<CarCommission> getCommissionByCarItemIDAndAirportCode(Long carItemID, String airportCode) throws DataAccessException
    {
        final String sqlQuery = "select CommissionPct,CarCommissionLogID from CarCommission where CarItemID = (:pCarItemID) and AirportCode = (:pAirportCode)";

        final ParametrizedQuery<CarCommission> tsql = new ParametrizedQuery<CarCommission>(sqlQuery, dataSource, CarCommission.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pCarItemID", carItemID);
        paramMap.put("pAirportCode", airportCode);

        return tsql.execute(paramMap);
    }

    public List<CarCommission> getCommissionByCarItemIDAndCountryCode(Long carItemID, String countryCode) throws DataAccessException
    {
        final String sqlQuery = "select CommissionPct,CarCommissionLogID from CarCommission where CarItemID = (:pCarItemID) and airportcode = '' and CountryCode = (:pCountryCode)";

        final ParametrizedQuery<CarCommission> tsql = new ParametrizedQuery<CarCommission>(sqlQuery, dataSource, CarCommission.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pCarItemID", carItemID);
        paramMap.put("pCountryCode", countryCode);

        return tsql.execute(paramMap);
    }

    public String getVendorCollectsFlagGDSPOneWayDropOffCharge(Long vendorSupplierID) throws DataAccessException
    {
        String vendorCollectsFlag = "";

        final String sqlQuery = "select VendorCollectsFlag from carRentalAmountCalculation where ChargeTypeID=4 and OneWayBool =1 "
            + "and SupplierID = (:pVendorSupplierID) and CarBusinessModelID = 3";

        final ParametrizedQuery<CarRentalAmountCalculation> tsql = new ParametrizedQuery<CarRentalAmountCalculation>(sqlQuery, dataSource, CarRentalAmountCalculation.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pVendorSupplierID", vendorSupplierID);

        if (tsql.execute(paramMap).size() > 0)
        {
            vendorCollectsFlag = tsql.execute(paramMap).get(0).getVendorCollectsFlag();
        }

        return vendorCollectsFlag;
    }

    //Get the serviceID for Cost from SupplySubsetToSupplyConnectivityServiceMap based on SupplySubsetID
    //select SupplyConnectivityServiceID from SupplySubsetToSupplyConnectivityServiceMap
    //where SupplySubsetID = 9270 and SupplyRoutingCategoryID = 1 (Cost)
    //ServiceID 1: WSCS 2. ESCS 3. MNSCS
    public int getServiceIDForSupplySubsetIDCost(long supplySubsetID) throws DataAccessException
    {
        int serviceID = 0;

        final String sqlQuery = "select SupplyConnectivityServiceID from SupplySubsetToSupplyConnectivityServiceMap " +
                "where SupplySubsetID = (:pSupplySubsetID) and SupplyRoutingCategoryID = 1 ";

        final ParametrizedQuery<ServiceProvider> tsql = new ParametrizedQuery<ServiceProvider>(sqlQuery, dataSource, ServiceProvider.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pSupplySubsetID", supplySubsetID);

        if (tsql.execute(paramMap).size() > 0)
        {
            serviceID = tsql.execute(paramMap).get(0).getSupplyConnectivityServiceID();
        }

        return serviceID;
    }

    //Get the serviceID for Avail from SupplySubsetToSupplyConnectivityServiceMap based on SupplySubsetID
    //select SupplyConnectivityServiceID from SupplySubsetToSupplyConnectivityServiceMap
    //where SupplySubsetID = 9270 and SupplyRoutingCategoryID = 2 (Avail)
    //ServiceID 1: WSCS 2. ESCS 3. MNSCS
    public int getServiceIDForSupplySubsetIDAvail(long supplySubsetID) throws DataAccessException
    {
        int serviceID = 0;

        final String sqlQuery = "select SupplyConnectivityServiceID from SupplySubsetToSupplyConnectivityServiceMap " +
                "where SupplySubsetID = (:pSupplySubsetID) and SupplyRoutingCategoryID = 2 ";

        final ParametrizedQuery<ServiceProvider> tsql = new ParametrizedQuery<ServiceProvider>(sqlQuery, dataSource, ServiceProvider.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pSupplySubsetID", supplySubsetID);

        if (tsql.execute(paramMap).size() > 0)
        {
            serviceID = tsql.execute(paramMap).get(0).getSupplyConnectivityServiceID();
        }

        return serviceID;
    }

    public Map<Long, Long> getServiceIDFromSupplySubsetIDCost(Set<Long> supplySubsetIDs) throws DataAccessException
    {
        return getServiceIDFromSupplySubsetID(supplySubsetIDs,1);
    }

    public Map<Long, Long> getServiceIDFromSupplySubsetIDAvail(Set<Long> supplySubsetIDs) throws DataAccessException
    {
        return getServiceIDFromSupplySubsetID(supplySubsetIDs,2);
    }

    private Map<Long, Long> getServiceIDFromSupplySubsetID(Set<Long> supplySubsetIDs, int supplyRoutingCategoryID) throws DataAccessException {
        if(supplySubsetIDs.isEmpty()){
            return new HashMap<>();
        }

        final String sqlQuery = "select SupplySubsetID,SupplyConnectivityServiceID from SupplySubsetToSupplyConnectivityServiceMap " +
                "where SupplySubsetID in (:pSupplySubsetID) and SupplyRoutingCategoryID = "+supplyRoutingCategoryID;

        final ParametrizedQuery<SupplyIdVsServiceId> tsql = new ParametrizedQuery<SupplyIdVsServiceId>(sqlQuery, dataSource, SupplyIdVsServiceId.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pSupplySubsetID", supplySubsetIDs);

        List<SupplyIdVsServiceId> execute = tsql.execute(paramMap);
        Map<Long, Long> supplyIdVsServiceIdMap=new HashMap<>();
        for(SupplyIdVsServiceId item:execute){
            supplyIdVsServiceIdMap.put(item.supplySubsetID,item.supplyConnectivityServiceID);
        }
        return supplyIdVsServiceIdMap;
    }


    public int getCarBusinessModelIDFromCarItem(long carItemID) throws DataAccessException
    {
        int carBusinessModelID = 0;

        final String sqlQuery = "select CarBusinessModelID from CarItem where CarItemID = (:pCarItem)";

        final ParametrizedQuery<SupplySubset> tsql = new ParametrizedQuery<SupplySubset>(sqlQuery, dataSource, SupplySubset.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pCarItem", carItemID);

        if (tsql.execute(paramMap).size() > 0)
        {
            carBusinessModelID = tsql.execute(paramMap).get(0).getCarBusinessModelID();
        }

        return carBusinessModelID;
    }

    public Map<Long, Integer> getCarBusinessModelIDFromCarItem(Set<Long> carItemIDList) throws DataAccessException
    {

        final String sqlQuery = "select CarBusinessModelID,CarItemId from CarItem where CarItemID in (:pCarItem)";

        final ParametrizedQuery<SupplySubset> tsql = new ParametrizedQuery<SupplySubset>(sqlQuery, dataSource, SupplySubset.class);

        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pCarItem", carItemIDList);
        List<SupplySubset> execute = tsql.execute(paramMap);

        Map<Long, Integer> carItemVsBusinessIdMap=new HashMap<>();
        if (execute.size() > 0)
        {
            execute.forEach( item -> carItemVsBusinessIdMap.put(item.getCarItemID(),item.getCarBusinessModelID()));
            return carItemVsBusinessIdMap;
        }

        return new HashMap<>();
    }

    public List<SupplySubSetToWorldSpanSupplierItemMap> getWorldSpanSupplierItemMap(List<SupplySubsetIDEntryType> subsetIDEntryTypeList) throws DataAccessException
    {
        final String sqlQuery = "select distinct SupplySubsetID, ITNumber, CorporateDiscountCodeRequiredInShopping," +
                "CorporateDiscountCodeRequiredInBooking,RateCode,IATAAgencyCode,CorporateDiscountCode,IATAOverrideBooking, PrepaidBool " +
                "from SupplySubSetToWorldSpanSupplierItemMap where SupplySubsetID in (:supplyIds)";
        final ParametrizedQuery<SupplySubSetToWorldSpanSupplierItemMap> tsql = new ParametrizedQuery<SupplySubSetToWorldSpanSupplierItemMap>(sqlQuery, dataSource, SupplySubSetToWorldSpanSupplierItemMap.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplyIds", subsetIDEntryTypeList.stream()
                .map(i -> i.getSupplySubsetID()).collect(Collectors.toList()));
        return tsql.execute(paramMap);
    }

    public List<SupplySubSetToWorldSpanSupplierItemMap> getWorldSpanSupplierItemMap(Long subsetID) throws DataAccessException
    {
        final String sqlQuery = "select distinct SupplySubsetID, ITNumber, CorporateDiscountCodeRequiredInShopping," +
                "CorporateDiscountCodeRequiredInBooking,RateCode,IATAAgencyCode,CorporateDiscountCode,IATAOverrideBooking, PrepaidBool " +
                "from SupplySubSetToWorldSpanSupplierItemMap where SupplySubsetID in (:supplyIds)";
        final ParametrizedQuery<SupplySubSetToWorldSpanSupplierItemMap> tsql = new ParametrizedQuery<SupplySubSetToWorldSpanSupplierItemMap>(sqlQuery, dataSource, SupplySubSetToWorldSpanSupplierItemMap.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplyIds", subsetID);
        return tsql.execute(paramMap);
    }

    public String getCarBehaviorAttributValue(Long supplierID, Long supplySubsetID, Long carBehaviorAttID) throws DataAccessException
    {
        final String sqlQuery = "select cba.AttributeValue as carVendorCode from CarBehaviorAttributeValue as cba where SupplierID = :supplierID and SupplySubsetID in (:supplySubsetID,0) and CarBehaviorAttributeID = :carBehaviorAttID";
        final ParametrizedQuery<CarVendor> tsql = new ParametrizedQuery<CarVendor>(sqlQuery, dataSource, CarVendor.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplierID", supplierID);
        paramMap.put("supplySubsetID", supplySubsetID);
        paramMap.put("carBehaviorAttID", carBehaviorAttID);
        final List<CarVendor> resultList = tsql.execute(paramMap);
        return CompareUtil.isObjEmpty(resultList) ? "" : resultList.get(0).getCarVendorCode();
    }

    public List<CarConfigurationFormat> getCarConfigurationFormatList(long supplySubSetID) throws DataAccessException
    {
        final String sqlQuery = "select CarRateCodeFormat,CarRateCodeMaxSize,CarCorpDiscFormat,CarCorpDiscMaxSize,CarTourIDFormat,CarTourIDMaxSize," +
                " CarVoucherNumberFormat,CarVoucherNumberMaxSize,CarSupplementalInfoFormat,CarSupplementalInfoMaxSize from CarConfigurationFormat " +
                " ccf join SupplySubsetToCarConfigurationFormatMap scf on ccf.CarConfigurationFormatID = scf.CarConfigurationFormatID " +
                " and scf.SupplySubsetID =:supplySubSetID ";
        final ParametrizedQuery<CarConfigurationFormat> tsql = new ParametrizedQuery<CarConfigurationFormat>(sqlQuery, dataSource, CarConfigurationFormat.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplySubSetID", supplySubSetID);
        return tsql.execute(paramMap);
    }

    public List<CarItem> getCarItemListById(long carItemId) throws DataAccessException
    {
        final String sqlQuery = " select AccountingVendorID,SupplierID,CarBusinessModelID,PrepaidBool from CarItem where CarItemID =:carItemId";
        final ParametrizedQuery<CarItem> tsql = new ParametrizedQuery<CarItem>(sqlQuery, dataSource, CarItem.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("carItemId", carItemId);
        return tsql.execute(paramMap);
    }

    public List<CarItem> getCarItemListBySubsetID(List<SupplySubset> subsetIDList) throws DataAccessException
    {
        final String sqlQuery = " select CaritemID from caritem where CarItemID " +
                " in( select CarItemID from CarItemSupplySubsetRank where SupplySubsetID in (:supplyIds) ) ";
        final ParametrizedQuery<CarItem> tsql = new ParametrizedQuery<CarItem>(sqlQuery, dataSource, CarItem.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("supplyIds", subsetIDList.stream()
                .map(i -> i.getSupplySubsetID())
                .collect(Collectors.toList()));
        return tsql.execute(paramMap);
    }

    public String getEnv()
    {
        String env = "";
        try
        {
            final String url = dataSource.getConnection().getMetaData().getURL();
            env = url.substring(url.indexOf('_') + 1, url.indexOf(';'));
        } catch (SQLException e)
        {
        }
        return env;
    }

    public List<CarCancelFee> getCancelFeeByTPID(Long tpid) throws DataAccessException
    {
        final String sqlQuery = "select TravelProductID, ExpediaFeeAmt, CurrencyCodeExpediaFeeAmt from CarCancelFee " +
                "where TravelProductID = :pTPID ";
        final ParametrizedQuery<CarCancelFee> tsql = new ParametrizedQuery<CarCancelFee>(sqlQuery, dataSource, CarCancelFee.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("pTPID", tpid);
        return tsql.execute(paramMap);
    }
    @SuppressWarnings("CPD-END")
    public List<CarShareTarget> getShareByMarketPct(Long tpid, String airportCode) throws DataAccessException
    {
        final String sqlQuery = "select TravelProductID, AirportCode, ShareByMarketPct, SupplierID from CarShareTarget " +
                "where TravelProductID = :pTPID and AirportCode = :pAirportCode order by ShareByMarketPct desc";
        final ParametrizedQuery<CarShareTarget> tsql = new ParametrizedQuery<CarShareTarget>(sqlQuery, dataSource, CarShareTarget.class);
        final Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("pTPID", tpid);
        paramMap.put("pAirportCode", airportCode);
        return tsql.execute(paramMap);
    }

    public List<CarShareTarget> isEgenciaPOSByTPID(Long tpid) throws DataAccessException {
        final String sqlQuery = "select TravelProductID from TPIDToPoSAttributeMap where TravelProductID >= 60000 and TravelProductID = :tpid";
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("tpid", tpid);
        final ParametrizedQuery<CarShareTarget> tsql = new ParametrizedQuery<CarShareTarget>(sqlQuery, dataSource, CarShareTarget.class);
        return tsql.execute(paramMap);
    }

    public List<CarVendorLocationLatLong> excuteCarVendorLocationProc(String procName,List<String> paramList) throws DataAccessException {
        final String sqlQuery = "exec " + procName + " "+StringUtils.join(paramList.toArray(),",");
        final ParametrizedQuery<CarVendorLocationLatLong> tsql = new ParametrizedQuery<CarVendorLocationLatLong>(sqlQuery, dataSource, CarVendorLocationLatLong.class);
        return tsql.execute(null);
    }

    public List<CarCatalogMakeModelType> getMakeModelListByMediaID(long mediaID) throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select  a.CarMediaID as mediaID, a.NumberOfPassengersAdult as carCapacityAdultCount, ")
                .append(" a.NumberOfPassengersChild as carCapacityChildCount,a.NumberOfDoorsMin as carMinDoorCount, ")
                .append(" a.NumberOfDoorsMax as carMaxDoorCount,a.NumberOfSuitcasesSmall as carCapacitySmallLuggageCount, ")
                .append(" a.NumberOfSuitcasesLarge as carCapacityLargeLuggageCount,b.MakeModel as carMakeString, ")
                .append(" c.ImageFilenameString as imageFilenameString,c.ImageThumbnailFilenameString as imageThumbnailFilenameString ")
                .append(" from CarMediaData a ,CarMediaDataLoc b,(select CarMediaID,max(case c.MediaSizeTypeID when '1' then c.FullMediaFileName end) ImageThumbnailFilenameString, ")
                .append(" max(case c.MediaSizeTypeID when '2' then c.FullMediaFileName end) ImageFilenameString ")
                .append(" from CarMediaSize c group by c.CarMediaID) c where a.CarMediaID = b.CarMediaID ")
                .append(" and a.CarMediaID = c.CarMediaID and a.CarMediaID = :mediaID ");
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("mediaID", mediaID);
        final ParametrizedQuery<CarCatalogMakeModelType> tsql = new ParametrizedQuery<CarCatalogMakeModelType>(sqlQuery.toString(), dataSource, CarCatalogMakeModelType.class);
        return tsql.execute(paramMap);
    }

    public List<CarCatalogMakeModelType> getCarMediaListBySupplierCarTypeLocation(long supplierID, long carCategoryID, long carTypeID, String locationCode) throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select * from CarMedia where CarMediaID in (select CarMediaID from ")
                .append("  CarMediaConfiguration  where CarCategoryID=:carCategoryID and CarTypeID=:carTypeID and SupplierID=:supplierID and CountryCode= ")
                .append("(select CountryCode from Airport where AirportCode=:locationCode)) ");
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("carCategoryID", carCategoryID);
        paramMap.put("carTypeID", carTypeID);
        paramMap.put("supplierID", supplierID);
        paramMap.put("locationCode", locationCode);
        final ParametrizedQuery<CarCatalogMakeModelType> tsql = new ParametrizedQuery<CarCatalogMakeModelType>(sqlQuery.toString(), dataSource, CarCatalogMakeModelType.class);
        return tsql.execute(paramMap);
    }

    public List<CarCatalogMakeModelType> getACRISSInfo(Long carCategoryID,Long carTypeID,Long carTransmissionDriveID,Long carFuelAirConditionID) throws DataAccessException {
        final StringBuffer sqlQuery = new StringBuffer();
        sqlQuery.append(" select max(case ftype when 1 then fvalue end) ACRISSCategoryCode, ")
                .append(" max(case ftype when 2 then fvalue end) ACRISSTypeCode, ")
                .append(" max(case ftype when 3 then fvalue end) ACRISSTransmissionDriveCode, ")
                .append(" max(case ftype when 4 then fvalue end) ACRISSFuelACCode ")
                .append(" from ( select 1 as ftype,a.CarCategoryExpeAbbr as fvalue from CarCategory a where a.CarCategoryID  = :carCategoryID union ")
                .append(" select 2 as ftype,b.CarTypeExpeAbbr as fvalue from CarType b where b.CarTypeID = :carTypeID union ")
                .append(" select 3 as ftype,c.CarTransmissionDriveExpeAbbr as fvalue from CarTransmissionDrive c where c.CarTransmissionDriveID = :carTransmissionDriveID union ")
                .append(" select 4 as ftype,d.CarFuelAirConditionExpeAbbr as fvalue from CarFuelAirCondition d where d.CarFuelAirConditionID = :carFuelAirConditionID) c ");
        final Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("carCategoryID", carCategoryID);
        paramMap.put("carTypeID", carTypeID);
        paramMap.put("carTransmissionDriveID", carTransmissionDriveID);
        paramMap.put("carFuelAirConditionID", carFuelAirConditionID);
        final ParametrizedQuery<CarCatalogMakeModelType> tsql = new ParametrizedQuery<CarCatalogMakeModelType>(sqlQuery.toString(), dataSource, CarCatalogMakeModelType.class);
        return tsql.execute(paramMap);
    }
    public String getMediaInfoByCarMediaID(long carMediaID) throws DataAccessException
    {
        final String sqlQuery = "select MediaFileName from CarMedia where CarMediaID = :carMediaID";
        final ParametrizedQuery<CarMedia> tsql = new ParametrizedQuery<>(sqlQuery, dataSource, CarMedia.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("carMediaID", carMediaID);
        return tsql.execute(paramMap).get(0).getMediaFileName();
    }

    public boolean hasNagativeRuleMarkup(List<Long> carRuleList) throws DataAccessException
    {
        final String sqlQuery =  "select  * from CarRuleMatrix where CarRuleID in :carRuleID and PurchaseTypeSetID is not null";
        final ParametrizedQuery<CarRuleMatrix> tsql = new ParametrizedQuery<>(sqlQuery, dataSource, CarRuleMatrix.class);
        final Map<String, Object> paramMap = new HashMap<>(1);
        paramMap.put("carRuleID", String.join(",", (String[])carRuleList.toArray()));
        final List<CarRuleMatrix> carRuleMatrixs = tsql.execute(paramMap);
        return CollectionUtils.isEmpty(carRuleMatrixs)? false : true;
    }

//TODO Mia Correct it after table is out.
    public List<CarPromotion> getCarsPromotionsConfigured(String jurisdictionCountryCode, String companyCode, String managementUnitCode,
                                                          String posuCountryCode, String supplierID, boolean isPackage, int carBusinessModel) throws DataAccessException {
        //CarPromotion
        //CarPromotionCriteria
        //CarPromotionToCriteriaMap

        String sqlQuery = "select distinct cp.CarPromotionName, cp.CarPromotionDescription, cp.CarPromotionType, cpcm.CarOfferType, cpc.SearchStartDate," +
                "cpc.SearchEndDate, cpc.TravelStartDate, cpc.TravelEndDate from dbo.CarPromotionCriteria cpc join dbo.CarPromotionToCriteriaMap cpcm on " +
                "cpcm.CarPromotionCriteriaID = cpc.CarPromotionCriteriaID join dbo.CarPromotion cp on cp.CarPromotionID = cpcm.CarPromotionID where " +
                "cpc.SupplierID = :pSupplierID and cpc.JurisdictionCode = :pJurisdictionCode and cpc.CompanyCode = :pCompanyCode and " +
                "cpc.ManagementUnitCode = :pManagementUnitCode and cpc.POSuCountryCode = :pPOSuCountryCode and cpc.CarBusinessModel = :pCarBusinessModel and cpc.IsPackage = :pIsPackage";

        final ParametrizedQuery<CarPromotion> tsql = new ParametrizedQuery<CarPromotion>(sqlQuery, dataSource, CarPromotion.class);

        final Map<String, Object> paramMap = new HashMap<>(7);
        paramMap.put("pSupplierID", supplierID);
        paramMap.put("pJurisdictionCode", jurisdictionCountryCode);
        paramMap.put("pCompanyCode", companyCode);
        paramMap.put("pManagementUnitCode", managementUnitCode);
        paramMap.put("pPOSuCountryCode", posuCountryCode);
        paramMap.put("pCarBusinessModel", Integer.toString(carBusinessModel));
        paramMap.put("pIsPackage", isPackage ? Integer.toString(1) : Integer.toString(0));
        return tsql.execute(paramMap);
    }
}
