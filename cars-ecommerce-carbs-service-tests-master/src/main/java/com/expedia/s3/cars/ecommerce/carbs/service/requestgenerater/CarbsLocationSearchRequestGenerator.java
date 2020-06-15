package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;

import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.messages.location.search.defn.v1.*;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.*;
import org.w3c.dom.Document;

/**
 * Created by v-mechen on 9/5/2017.
 */
@SuppressWarnings("PMD")
public class CarbsLocationSearchRequestGenerator {
    private CarLocationSearchRequest locationSearchRequest;
    private CarLocationSearchResponse locationSearchResponse;
    private CarLocationIataSearchRequest locationIataSearchRequest;
    private CarLocationIataSearchResponse carLocationIataSearchResponse;
    private Document spooferDoc;

    public CarLocationSearchRequest createCarLocationSearchRequest(TestData parameters) {
        final CarLocationSearchRequest request = new CarLocationSearchRequest();
        final LocationSearchTestScenario scenario = parameters.getLocationSearchTestScenario();
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        request.setMessageInfo(commonRequestGenerator.createMessageInfoType("CarLocationSearchRequest", "1.0.0"));

        final CarLocationSearchRequestSearchCriteria searchCriteria = new CarLocationSearchRequestSearchCriteria();
        //set the lat/long in request with the value got from config file. Remove the dot from lat/long value, e.g: 47.604543 - > 47604543
        final CarLocationSearchRequestSearchCriteriaLatitude latitude = new CarLocationSearchRequestSearchCriteriaLatitude();
        searchCriteria.setLatitude(latitude);
        final String lat = scenario.getLatitude() + "";
        latitude.setValue(Long.valueOf(lat.split("\\.")[0] + lat.split("\\.")[1]));
        latitude.setDecimalPlaceCount(Long.valueOf(lat.split("\\.")[1].length() + ""));

        final CarLocationSearchRequestSearchCriteriaLongitude longitude = new CarLocationSearchRequestSearchCriteriaLongitude();
        searchCriteria.setLongitude(longitude);
        final String lon = scenario.getLongitude() + "";
        longitude.setValue(Long.valueOf(lon.split("\\.")[0] + lon.split("\\.")[1]));
        longitude.setDecimalPlaceCount(Long.valueOf(lon.split("\\.")[1].length() + ""));

        final CarLocationSearchRequestSearchCriteriaRadius radius = new CarLocationSearchRequestSearchCriteriaRadius();
        searchCriteria.setRadius(radius);
        radius.setDistanceUnitCount(scenario.getRadius());
        radius.setDistanceUnit("MI");

        final CarLocationSearchRequestSearchCriteriaFilter filter = new CarLocationSearchRequestSearchCriteriaFilter();
        searchCriteria.setFilter(filter);
        if(!CompareUtil.isObjEmpty(scenario.getCollectionBoolean())) {
            filter.setCollectionBoolean(Boolean.parseBoolean(scenario.getCollectionBoolean()));
        }
        if(!CompareUtil.isObjEmpty(scenario.getDeliveryBoolean())) {
            filter.setDeliveryBoolean(Boolean.parseBoolean(scenario.getDeliveryBoolean()));
        }
        if(!CompareUtil.isObjEmpty(scenario.getOutOfOfficeHourBoolean())) {
            filter.setOutOfOfficeHourBoolean(Boolean.parseBoolean(scenario.getOutOfOfficeHourBoolean()));
        }
        if(!scenario.isNullIncludeLocation()) {
            searchCriteria.setIncludeLocationDetails(scenario.isIncludeLocationDetails());
        }
        if (scenario.isNullSearchFilter()) {
            searchCriteria.setFilter(null);
        }
        request.setSearchCriteria(searchCriteria);
        request.setClientCode(getClientCode(scenario));
        this.setLocationSearchRequest(request);
        return request;
    }

    public CarLocationIataSearchRequest createLocationIataSearchRequest(TestData parameters) throws DataAccessException {
        final CarLocationIataSearchRequest request = new CarLocationIataSearchRequest();
        final LocationSearchTestScenario scenario = parameters.getLocationSearchTestScenario();
        //Common nodes - MessageInfo/AuditLogTrackingData/PointOfSaleKey
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        request.setMessageInfo(commonRequestGenerator.createMessageInfoType("CarLocationIataSearchRequest", "1.0.0"));
        request.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(parameters));
        final PointOfSaleKeyType pointOfSaleKeyType = commonRequestGenerator.createPointOfSaleKeyType(scenario.getJurisdictionCountryCode(),
                scenario.getCompanyCode(), scenario.getManagementUnitCode());
        final PointOfSaleKey pointOfSaleKey = new PointOfSaleKey();
        pointOfSaleKey.setCompanyCode(pointOfSaleKeyType.getCompanyCode());
        pointOfSaleKey.setJurisdictionCountryCode(pointOfSaleKeyType.getJurisdictionCountryCode());
        pointOfSaleKey.setManagementUnitCode(pointOfSaleKeyType.getManagementUnitCode());
        request.setPointOfSaleKey(pointOfSaleKey);
        request.setClientCode(this.getClientCode(scenario));
        //SearchCriteria
        final CarLocationIataSearchRequestSearchCriteria criteria = new CarLocationIataSearchRequestSearchCriteria();
        //Build IATA
        if (!CompareUtil.isObjEmpty(scenario.getIataCode())) {
            criteria.setIata(scenario.getIataCode());
        }
        else{
            //covert Latitude and Longitude to string
            String latString = scenario.getLatitude() + "";
            String longString = scenario.getLongitude() + "";
            //lat
            Latitude latitude = new Latitude();
            latitude.setValue(Long.valueOf(latString.split("\\.")[0] + latString.split("\\.")[1]));
            latitude.setDecimalPlaceCount(Long.valueOf(latString.split("\\.")[1].length() + ""));
            //long
            Longitude longitude = new Longitude();
            longitude.setValue(Long.valueOf(longString.split("\\.")[0] + longString.split("\\.")[1]));
            longitude.setDecimalPlaceCount(Long.valueOf(longString.split("\\.")[1].length() + ""));

            //Radius
            Radius radius = new Radius();
            radius.setDistanceUnit(scenario.getDistanceUnit());
            radius.setDistanceUnitCount(scenario.getRadius());
            criteria.setLatitude(latitude);
            criteria.setLongitude(longitude);
            criteria.setRadius(radius);
        }

        if (CompareUtil.isObjEmpty(scenario.getCollectionBoolean()) && CompareUtil.isObjEmpty(scenario.getDeliveryBoolean()) && CompareUtil.isObjEmpty(scenario.getOutOfOfficeHourBoolean()) && CompareUtil.isObjEmpty(scenario.getAirportVicinityBoolean())) {
            if (scenario.isNullSearchFilter()) {
                criteria.setFilter(null);
            } else {
                criteria.setFilter(new Filter());
            }
        } else {
            Filter filter = new Filter();
            if (!CompareUtil.isObjEmpty(scenario.getCollectionBoolean())) {
                filter.setCollectionBoolean(Boolean.parseBoolean(scenario.getCollectionBoolean()));
            }
            if (!CompareUtil.isObjEmpty(scenario.getDeliveryBoolean())) {
                filter.setDeliveryBoolean(Boolean.parseBoolean(scenario.getDeliveryBoolean()));
            }
            if (!CompareUtil.isObjEmpty(scenario.getOutOfOfficeHourBoolean())) {
                filter.setOutOfOfficeHourBoolean(Boolean.parseBoolean(scenario.getOutOfOfficeHourBoolean()));
            }
            if (!CompareUtil.isObjEmpty(scenario.getAirportVicinityBoolean())) {
                filter.setAirportVicinityBoolean(Boolean.parseBoolean(scenario.getAirportVicinityBoolean()));
            }
            criteria.setFilter(filter);
        }

        if (!scenario.isNullIncludeLocation()) {
            criteria.setIncludeLocationDetails(scenario.isIncludeLocationDetails());
        }

        if (scenario.isNullAuditLogTracking()) {
            request.setAuditLogTrackingData(null);
        }
        this.setLocationIataSearchRequest(request);
        request.setSearchCriteria(criteria);
        return request;
    }

    public String getClientCode(LocationSearchTestScenario scenario) {
        String clientCode = "";
        try {
            CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
            CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
            if (!CompareUtil.isObjEmpty(scenario.getClientCode())) {
                clientCode = scenario.getClientCode();
            } else if (inventoryHelper.isEgenciaPOSByTPID(scenario.getTpid())) {
                clientCode = CompareUtil.isObjEmpty(carBSHelper.getClientListById("3")) ? "" : carBSHelper.getClientListById("3").get(0).getClientCode();
            } else {
                clientCode = CompareUtil.isObjEmpty(carBSHelper.getClientListById("7")) ? "" : carBSHelper.getClientListById("7").get(0).getClientCode();
            }
        } catch (Exception e) {
        }
        return clientCode;
    }

    public CarLocationSearchRequest getLocationSearchRequest() {
        return locationSearchRequest;
    }

    public void setLocationSearchRequest(CarLocationSearchRequest locationSearchRequest) {
        this.locationSearchRequest = locationSearchRequest;
    }

    public CarLocationSearchResponse getLocationSearchResponse() {
        return locationSearchResponse;
    }

    public void setLocationSearchResponse(CarLocationSearchResponse locationSearchResponse) {
        this.locationSearchResponse = locationSearchResponse;
    }

    public CarLocationIataSearchRequest getLocationIataSearchRequest() {
        return locationIataSearchRequest;
    }

    public void setLocationIataSearchRequest(CarLocationIataSearchRequest locationIataSearchRequest) {
        this.locationIataSearchRequest = locationIataSearchRequest;
    }

    public CarLocationIataSearchResponse getCarLocationIataSearchResponse() {
        return carLocationIataSearchResponse;
    }

    public void setCarLocationIataSearchResponse(CarLocationIataSearchResponse carLocationIataSearchResponse) {
        this.carLocationIataSearchResponse = carLocationIataSearchResponse;
    }

    public Document getSpooferDoc() {
        return spooferDoc;
    }

    public void setSpooferDoc(Document spooferDoc) {
        this.spooferDoc = spooferDoc;
    }
}
