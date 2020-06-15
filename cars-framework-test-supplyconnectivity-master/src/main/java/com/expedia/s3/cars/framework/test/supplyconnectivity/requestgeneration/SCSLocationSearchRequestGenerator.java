package com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration;

import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.*;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

/**
 * Created by v-mechen on 9/28/2018.
 */
@SuppressWarnings("PMD")
public class SCSLocationSearchRequestGenerator {

    private static final String MESSAGE_VERSION = "1.0.0";
    private static final String MESSAGE_NAME = "CarSupplyConnectivityLocationSearchRequest";
    private CarSupplyConnectivityLocationSearchRequestType request;
    private CarSupplyConnectivityLocationSearchResponseType response;
    private Document spooferDoc;

    public CarSupplyConnectivityLocationSearchRequestType createLocationSearchRequest(TestData parameters) throws DataAccessException {
        final CarSupplyConnectivityLocationSearchRequestType request = new CarSupplyConnectivityLocationSearchRequestType();

        final LocationSearchTestScenario scenario = parameters.getLocationSearchTestScenario();
        //Common nodes - MessageInfo/AuditLogTrackingData/PointOfSaleKey
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        request.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
        request.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(parameters));
        final PointOfSaleKeyType pointOfSaleKeyType = commonRequestGenerator.createPointOfSaleKeyType(scenario.getJurisdictionCountryCode(),
                scenario.getCompanyCode(), scenario.getManagementUnitCode());
        final PointOfSaleKeyType pointOfSaleKey = new PointOfSaleKeyType();
        pointOfSaleKey.setCompanyCode(pointOfSaleKeyType.getCompanyCode());
        pointOfSaleKey.setJurisdictionCountryCode(pointOfSaleKeyType.getJurisdictionCountryCode());
        pointOfSaleKey.setManagementUnitCode(pointOfSaleKeyType.getManagementUnitCode());
        request.setPointOfSaleKey(pointOfSaleKey);

        //SearchCriteria
        final CarSupplyConnectivityLocationSearchRequestTypeSearchCriteria searchCriteria = new CarSupplyConnectivityLocationSearchRequestTypeSearchCriteria();
        request.setSearchCriteria(searchCriteria);
        //Lat/Long/Radious
        if(StringUtils.isEmpty(scenario.getIataCode()))
        {
            //covert Latitude and Longitude to string
            final String latString = scenario.getLatitude() + "";
            final String longString = scenario.getLongitude() + "";
            //lat
            final CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaLatitude latitude = new CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaLatitude();
            latitude.setValue(Long.valueOf(latString.split("\\.")[0] + latString.split("\\.")[1]));
            latitude.setDecimalPlaceCount(Long.valueOf(latString.split("\\.")[1].length() + ""));
            //long
            final CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaLongitude longitude = new CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaLongitude();
            longitude.setValue(Long.valueOf(longString.split("\\.")[0] + longString.split("\\.")[1]));
            longitude.setDecimalPlaceCount(Long.valueOf(longString.split("\\.")[1].length() + ""));

            //Radius
            if(scenario.getRadius() > 0) {
                final CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaRadius radius = new CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaRadius();
                radius.setDistanceUnit(scenario.getDistanceUnit());
                radius.setDistanceUnitCount(scenario.getRadius());
                searchCriteria.setRadius(radius);
            }
            else
            {
                searchCriteria.setRadius(null);
            }
            searchCriteria.setLatitude(latitude);
            searchCriteria.setLongitude(longitude);

        }
        //IATA
        else
        {
            searchCriteria.setIata(scenario.getIataCode());
        }

        if (CompareUtil.isObjEmpty(scenario.getCollectionBoolean()) && CompareUtil.isObjEmpty(scenario.getDeliveryBoolean()) && CompareUtil.isObjEmpty(scenario.getOutOfOfficeHourBoolean()) && CompareUtil.isObjEmpty(scenario.getAirportVicinityBoolean())) {
            if (scenario.isNullSearchFilter()) {
                searchCriteria.setFilter(null);
            } else {
                searchCriteria.setFilter(new CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaFilter());
            }
        } else {
            CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaFilter filter = new CarSupplyConnectivityLocationSearchRequestTypeSearchCriteriaFilter();
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
            searchCriteria.setFilter(filter);
        }

        if (!scenario.isNullIncludeLocation()) {
            searchCriteria.setIncludeLocationDetails(scenario.isIncludeLocationDetails());
        }

        if (scenario.isNullAuditLogTracking()) {
            request.setAuditLogTrackingData(null);
        }

        this.setRequest(request);
        return request;
    }

    public CarSupplyConnectivityLocationSearchRequestType getRequest() {
        return request;
    }

    public void setRequest(CarSupplyConnectivityLocationSearchRequestType request) {
        this.request = request;
    }


    public CarSupplyConnectivityLocationSearchResponseType getResponse() {
        return response;
    }

    public void setResponse(CarSupplyConnectivityLocationSearchResponseType response) {
        this.response = response;
    }

    public Document getSpooferDoc() {
        return spooferDoc;
    }

    public void setSpooferDoc(Document spooferDoc) {
        this.spooferDoc = spooferDoc;
    }
}
