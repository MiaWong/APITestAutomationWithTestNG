package com.expedia.s3.cars.supply.service.requestgenerators;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.Airport;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarItem;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubset;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonDataTypesGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supply.messages.search.defn.v4.CarSupplySearchRequestType;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yyang4 on 8/22/2016.
 */
@SuppressWarnings("PMD")
public class SearchRequestGenerator {
    private static final String MESSAGE_NAME = "CarSupplySearchRequest";
    private static final String MESSAGE_VERSION = "4.1.0";
    private final DataSource dataSource;

    public SearchRequestGenerator(DataSource dataSource) {
        this.dataSource = dataSource;

    }

    public SearchRequestGenerator() {
        this.dataSource = null;

    }

    public CarSupplySearchRequestType createSearchRequestD(TestData parameters) {
        final CarSupplySearchRequestType searchRequestType = new CarSupplySearchRequestType();
        try {
                final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
                searchRequestType.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
                searchRequestType.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(parameters));
                searchRequestType.setPointOfSaleKey(commonRequestGenerator.createPointOfSaleKeyType(parameters.getScenarios()));
                searchRequestType.setCarSearchStrategy(CommonDataTypesGenerator.createCarSearchStrategy(parameters.getScenarios()));

                if(parameters.getScenarios().isOnAirPort()) {
                    searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaList(parameters));
                }else{
                    searchRequestType.setCarSearchCriteriaList(createCarSearchCriteriaListOffAirPort(parameters,0));
                }
            } catch (DataAccessException e) {
                Assert.fail(e.getMessage());
            }
        return searchRequestType;
    }

    private CarSearchCriteriaListType createCarSearchCriteriaList(TestData testData) throws DataAccessException {
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarTransportationSegmentType carTransportationSegmentType = commonRequestGenerator.
                createCarTransportationSegmentType(testData);
        final CarVehicleListType carVehicleListType = commonRequestGenerator.createCarVehicleListType(10,0L,0L,0L);
        final List<CarRateOverrideType> cdCodes = commonRequestGenerator.createCDCodes(dataSource, testData.getCarRate());

        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(dataSource);
        final List<CarItem> carItems = carsInventoryHelper.getCarItems(testData.getScenarios());
       final List<CarItem> tempCarItems = new ArrayList<>();
        tempCarItems.addAll(carItems);
        if(null!= testData.getTestScenarioSpecialHandleParam() && 0 != testData.getTestScenarioSpecialHandleParam().getVendorSupplierID())
        {
            for(final CarItem carItem : tempCarItems){
                if (carItem.getSupplierID() != testData.getTestScenarioSpecialHandleParam().getVendorSupplierID())
                carItems.remove(carItem);
            }
        }
        final CarItemIDListType carItemIDListType= new CarItemIDListType();
        final List<Long> carItemID = new ArrayList<Long>();
        for(final CarItem carItem : carItems){
            carItemID.add(carItem.getCarItemID());
        }

        carItemIDListType.setCarItemID(carItemID);
        final String currencyCode = testData.getScenarios().getSupplierCurrencyCode();

        final List<CarSearchCriteriaType> carSearchCriteriaTypes = new ArrayList<CarSearchCriteriaType>();
        final long sequence = 101L;

        final CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();
        carSearchCriteriaType.setSequence(sequence);
        carSearchCriteriaType.setCarItemIDList(carItemIDListType);
        carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);
        carSearchCriteriaType.setCarVehicleList(carVehicleListType);
        if(null != cdCodes) {
            if(null == carSearchCriteriaType.getCarRateOverrideList())
            {
                carSearchCriteriaType.setCarRateOverrideList(new CarRateOverrideListType());
            }
            carSearchCriteriaType.getCarRateOverrideList().setCarRateOverride(cdCodes);
        }
        carSearchCriteriaType.setCarRate(new CarRateType());


        //set currencyCode
        carSearchCriteriaType.setCurrencyCode(currencyCode);
        carSearchCriteriaType.setSmokingBoolean(false);
        carSearchCriteriaType.setPrePaidFuelBoolean(false);
        carSearchCriteriaType.setUnlimitedMileageBoolean(false);
        carSearchCriteriaType.setPackageBoolean(!testData.getScenarios().isStandalone());
        carSearchCriteriaTypes.add(carSearchCriteriaType);
        final CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypes);
        return carSearchCriteriaListType;
    }
    private CarSearchCriteriaListType createCarSearchCriteriaListOffAirPort(TestData testData, double radius) throws DataAccessException {
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        final CarVehicleListType carVehicleListType = commonRequestGenerator.createCarVehicleListType(10,0L,0L,0L);
        final List<CarRateOverrideType> cdCodes = commonRequestGenerator.createCDCodes(dataSource, testData.getCarRate());

        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(dataSource);
        final List<CarItem> carItemTotalList = carsInventoryHelper.getCarItems(testData.getScenarios());
        final List<CarItem> tempCarItems = new ArrayList<>();
        tempCarItems.addAll(carItemTotalList);
        if(null!= testData.getTestScenarioSpecialHandleParam() && 0 != testData.getTestScenarioSpecialHandleParam().getVendorSupplierID())
        {
            for(final CarItem carItem : tempCarItems){
                if (carItem.getSupplierID() != testData.getTestScenarioSpecialHandleParam().getVendorSupplierID())
                    carItemTotalList.remove(carItem);
            }
        }
        final String currencyCode = testData.getScenarios().getSupplierCurrencyCode();
        final List<List<CarVendorLocation>> filterList = filterVendorLocationListWithOnwayBool(testData.getScenarios(),null,radius);
        final List<CarVendorLocation> carStratLocationListAll = filterList.get(0);
        final List<CarVendorLocation> carEndLocationListAll = filterList.get(1);
        long sequence = 1;
        final CarSearchCriteriaListType carSearchCriteriaListType = new CarSearchCriteriaListType();
        final List<CarSearchCriteriaType> carSearchCriteriaTypes = new ArrayList<CarSearchCriteriaType>();
        for(int i=0;i<carStratLocationListAll.size();i++){
            final CarVendorLocation carStartVendor = carStratLocationListAll.get(i);
            final CarVendorLocation carEndVendor = carEndLocationListAll.get(i);
            final CarSearchCriteriaType carSearchCriteriaType = new CarSearchCriteriaType();
            final List<Long> carItemIdList = new ArrayList<Long>();
            for(final CarItem carItem : carItemTotalList){
                if(carItem.getSupplierID().longValue() == carStartVendor.getVendorSupplierID()){
                    carItemIdList.add(carItem.getCarItemID());
                }
            }
            if(!CompareUtil.isObjEmpty(carItemIdList)) {
                final CarItemIDListType carItemIDListType = new CarItemIDListType();
                carItemIDListType.setCarItemID(new ArrayList<Long>(Arrays.asList(carItemIdList.get(0))));
                //set carItemId
                carSearchCriteriaType.setCarItemIDList(carItemIDListType);
                //set carTransportationSegment
                final CarTransportationSegmentType carTransportationSegmentType = commonRequestGenerator.
                        createCarTransportationSegmentType(carStartVendor, carEndVendor, testData);
                carSearchCriteriaType.setCarTransportationSegment(carTransportationSegmentType);

                if (null != cdCodes) {
                    if (null == carSearchCriteriaType.getCarRateOverrideList()) {
                        carSearchCriteriaType.setCarRateOverrideList(new CarRateOverrideListType());
                    }
                    carSearchCriteriaType.getCarRateOverrideList().setCarRateOverride(cdCodes);
                }

                //set other items
                carSearchCriteriaType.setSequence(sequence);
                carSearchCriteriaType.setCarVehicleList(carVehicleListType);
                carSearchCriteriaType.setCurrencyCode(currencyCode);
                carSearchCriteriaType.setSmokingBoolean(false);
                carSearchCriteriaType.setPrePaidFuelBoolean(false);
                carSearchCriteriaType.setUnlimitedMileageBoolean(false);
                carSearchCriteriaType.setPackageBoolean(!testData.getScenarios().isStandalone());
                carSearchCriteriaTypes.add(carSearchCriteriaType);
                sequence++;
            }
        }
        carSearchCriteriaListType.setCarSearchCriteria(carSearchCriteriaTypes);
        return carSearchCriteriaListType;
    }

    private List<List<CarVendorLocation>>  filterVendorLocationListWithOnwayBool(TestScenario testScenario,
                                                                                 List<SupplySubset> supplySubsets, double radius)throws DataAccessException {
        final List<List<CarVendorLocation>> resultList = new ArrayList<List<CarVendorLocation>>();
        final List<CarVendorLocation> carStartLocationList = getFilterCarLocationList(testScenario.getPickupLocationCode(),supplySubsets,radius);
        final List<CarVendorLocation> carEndLocationList = getFilterCarLocationList(testScenario.getDropOffLocationCode(),supplySubsets,radius);
        List<CarVendorLocation> carStratLocationListAll = new ArrayList<CarVendorLocation>();
        List<CarVendorLocation> carEndLocationListAll = new ArrayList<CarVendorLocation>();
        //trip way
        if(testScenario.getPickupLocationCode().equals(testScenario.getDropOffLocationCode())){
            carStratLocationListAll = carStartLocationList;
            carEndLocationListAll = carEndLocationList;
        }else{//one way
            for(final CarVendorLocation carStartVendor : carStartLocationList) {
                for (final CarVendorLocation carEndVendor : carEndLocationList) {
                    if (carStartVendor.getVendorSupplierID() == carEndVendor.getVendorSupplierID()) {
                        carStratLocationListAll.add(carStartVendor);
                        carEndLocationListAll.add(carEndVendor);
                    }
                }
            }
        }
        resultList.add(carStratLocationListAll);
        resultList.add(carEndLocationListAll);
        return resultList;
    }

    private List<CarVendorLocation> getFilterCarLocationList(String airPortCode, List<SupplySubset> supplySubsets, double radius)throws DataAccessException{
        final List<CarVendorLocation> filterCarLocationKeyList = new ArrayList<CarVendorLocation>();
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(dataSource);
        final Airport airport = carsInventoryHelper.getAirport(airPortCode);
        final List<CarVendorLocation> carVendorLocations = carsInventoryHelper.getCarLocationList(airPortCode,supplySubsets);
        radius = radius > 0? radius : 10;
        if(!CollectionUtils.isEmpty(carVendorLocations)){
            for(final CarVendorLocation carVendorLocation : carVendorLocations){
                final double calcRadius = calculateDistanceLatLon(airport.getLatitude(),airport.getLongitude(),carVendorLocation.getLatitude(),carVendorLocation.getLongitude());
                if(calcRadius < radius){
                    carVendorLocation.setDistance(calcRadius);
                    filterCarLocationKeyList.add(carVendorLocation);
                }
            }
        }
        return filterCarLocationKeyList;
    }

    private double  calculateDistanceLatLon(double airportLat, double airportLon, double locationLat, double locationLon){
        final double radLat1 = airportLat * Math.PI / 180.0;
        final double radLat2 = locationLat * Math.PI / 180.0;
        final double radLon1 = airportLon * Math.PI / 180.0;
        final double radLon2 = locationLon * Math.PI / 180.0;
        final double a = radLat1 - radLat2;
        final double b = radLon1 - radLon2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        s = (Math.round(s * 10000) / 1000) / 1.6; //Convert the unit to MI: 1MI is about 1.6KM
        return s;

    }

    public static CarSupplySearchRequestType createSearchRequest(TestScenario scenarios, String tuid) {
        try {
            final String e = scenarios.getJurisdictionCountryCode();
            final String companyCode = scenarios.getCompanyCode();
            final String managementUnitCode = scenarios.getManagementUnitCode();
            final String pickupLocationCode = scenarios.getPickupLocationCode();
            final String dropOffLocationCode = scenarios.getDropOffLocationCode();
            final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml = scenarios.getBusinessModel() == 1 ? dBuilder.parse(SearchRequestGenerator.class.getResourceAsStream("/AgencySampleSearch.xml")) :
                    dBuilder.parse(SearchRequestGenerator.class.getResourceAsStream("/GDSPSampleSearch.xml"));
            if(!scenarios.getPurchaseType().equals(PurchaseType.CarOnly))
            {
                xml = dBuilder.parse(SearchRequestGenerator.class.getResourceAsStream("/PackageSampleSearch.xml"));
            }
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final Node traveller_tuid = (Node) xpath.compile("//CarSupplySearchRequest/AuditLogTrackingData/TravelerUserKey/UserID").evaluate(xml, XPathConstants.NODE);
            traveller_tuid.setTextContent(tuid);
            final Node logon_tuid = (Node) xpath.compile("//CarSupplySearchRequest/AuditLogTrackingData/LogonUserKey/UserID").evaluate(xml, XPathConstants.NODE);
            logon_tuid.setTextContent(tuid);
            final Node JurisdictionCountryCode = (Node) xpath.compile("//CarSupplySearchRequest/PointOfSaleKey/JurisdictionCountryCode").evaluate(xml, XPathConstants.NODE);
            JurisdictionCountryCode.setTextContent(e);
            final Node CompanyCode = (Node) xpath.compile("//CarSupplySearchRequest/PointOfSaleKey/CompanyCode").evaluate(xml, XPathConstants.NODE);
            CompanyCode.setTextContent(companyCode);
            final Node ManagementUnitCode = (Node) xpath.compile("//CarSupplySearchRequest/PointOfSaleKey/ManagementUnitCode").evaluate(xml, XPathConstants.NODE);
            ManagementUnitCode.setTextContent(managementUnitCode);
            final Node PickupLocationCode = (Node) xpath.compile("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CarTransportationSegment/StartCarLocationKey/LocationCode").evaluate(xml, XPathConstants.NODE);
            PickupLocationCode.setTextContent(pickupLocationCode);
            final Node DropOffLocationCode = (Node) xpath.compile("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CarTransportationSegment/EndCarLocationKey/LocationCode").evaluate(xml, XPathConstants.NODE);
            DropOffLocationCode.setTextContent(dropOffLocationCode);
            final Node CurrencyCode = (Node) xpath.compile("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CurrencyCode").evaluate(xml, XPathConstants.NODE);
            CurrencyCode.setTextContent(scenarios.getSupplierCurrencyCode());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 180);
            Node startDatetimerangeMin = (Node) xpath.compile
                    ("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/StartDateTimeRange/MinDateTime").evaluate(xml, XPathConstants.NODE);
            startDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());
            Node startDatetimerangeMax = (Node) xpath.compile
                    ("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/StartDateTimeRange/MaxDateTime").evaluate(xml, XPathConstants.NODE);
            startDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());

            cal.add(Calendar.DATE, 5);
            NodeList endDatetimerangeMin = (NodeList) xpath.compile
                    ("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/EndDateTimeRange/MinDateTime").evaluate(xml, XPathConstants.NODESET);

            for(int i = 0; i < endDatetimerangeMin.getLength(); i++)
            {
                endDatetimerangeMin.item(i).setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());
            }
            Node endDatetimerangeMax = (Node) xpath.compile
                    ("//CarSupplySearchRequest/CarSearchCriteriaList/CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/EndDateTimeRange/MaxDateTime").evaluate(xml, XPathConstants.NODE);
            endDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());

            return PojoXmlUtil.docToPojo(xml, CarSupplySearchRequestType.class);
        } catch (Exception var22) {
            return null;
        }
    }
}
