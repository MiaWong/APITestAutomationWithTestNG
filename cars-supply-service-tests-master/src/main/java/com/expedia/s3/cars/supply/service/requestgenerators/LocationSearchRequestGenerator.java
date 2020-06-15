package com.expedia.s3.cars.supply.service.requestgenerators;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.CommonRequestGenerator;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.supply.messages.location.search.defn.v1.*;

import java.math.RoundingMode;

/**
 * Created by v-mechen on 9/5/2017.
 */
public class LocationSearchRequestGenerator {
    private static final String MESSAGE_NAME = "CarSupplyLocationSearchRequest";
    private static final String MESSAGE_VERSION = "1.0.0";
    private CarSupplyLocationSearchRequest request;
    private CarSupplyLocationSearchResponse response;

    public CarSupplyLocationSearchRequest createLocationSearchRequest(TestData parameters) throws DataAccessException {
        final CarSupplyLocationSearchRequest request = new CarSupplyLocationSearchRequest();
        final LocationSearchTestScenario scenario = parameters.getLocationSearchTestScenario();
        //Common nodes - MessageInfo/AuditLogTrackingData/PointOfSaleKey
        final CommonRequestGenerator commonRequestGenerator = new CommonRequestGenerator();
        request.setMessageInfo(commonRequestGenerator.createMessageInfoType(MESSAGE_NAME, MESSAGE_VERSION));
        request.setAuditLogTrackingData(commonRequestGenerator.createAuditLogTrackingDataType(parameters));
        request.setPointOfSaleKey(commonRequestGenerator.createPointOfSaleKeyType(scenario.getJurisdictionCountryCode(),
                scenario.getCompanyCode(), scenario.getManagementUnitCode()));
        //SearchCriteria
        final CarSupplyLocationSearchRequestSearchCriteria criteria = new CarSupplyLocationSearchRequestSearchCriteria();
        //Build IATA
        if(null != scenario.getIataCode())
        {
            criteria.setIata(scenario.getIataCode());
        }
        if(null != scenario.getDistanceUnit())
        {
            //Lat/Long/Radious
            final CarSupplyLocationSearchRequestSearchCriteriaLatitude latitude = new CarSupplyLocationSearchRequestSearchCriteriaLatitude();
            final int scale = 5;
            scenario.getLatitude().setScale(5, RoundingMode.HALF_UP);
            latitude.setDecimalPlaceCount(scale);
            latitude.setValue(scenario.getLatitude().movePointRight(scale).longValue());
            criteria.setLatitude(latitude);
            final CarSupplyLocationSearchRequestSearchCriteriaLongitude longitude = new CarSupplyLocationSearchRequestSearchCriteriaLongitude();
            scenario.getLongitude().setScale(5, RoundingMode.HALF_UP);
            longitude.setDecimalPlaceCount(scale);
            longitude.setValue(scenario.getLongitude().movePointRight(scale).longValue());
            criteria.setLongitude(longitude);
            final CarSupplyLocationSearchRequestSearchCriteriaRadius radious = new CarSupplyLocationSearchRequestSearchCriteriaRadius();
            radious.setDistanceUnitCount(scenario.geRadius());
            radious.setDistanceUnit(scenario.getDistanceUnit());
            criteria.setRadius(radious);
        }

        //SearchCriteriaFilter
        final CarSupplyLocationSearchRequestSearchCriteriaFilter filter = new CarSupplyLocationSearchRequestSearchCriteriaFilter();
        if(null != scenario.getDeliveryBoolean())
        {
            filter.setDeliveryBoolean(Boolean.parseBoolean(scenario.getDeliveryBoolean()));
        }
        if(null != scenario.getCollectionBoolean())
        {
            filter.setCollectionBoolean(Boolean.parseBoolean(scenario.getCollectionBoolean()));
        }
        if(null != scenario.getAirportVicinityBoolean())
        {
            filter.setAirportVicinityBoolean(Boolean.parseBoolean(scenario.getAirportVicinityBoolean()));
        }
        if(null != scenario.getOutOfOfficeHourBoolean())
        {
            filter.setOutOfOfficeHourBoolean(Boolean.parseBoolean(scenario.getOutOfOfficeHourBoolean()));
        }
        criteria.setFilter(filter);

        //IncludeLocationDetails
        criteria.setIncludeLocationDetails(scenario.isIncludeLocationDetails());
        request.setSearchCriteria(criteria);

        setRequest(request);


        return request;
    }

    public CarSupplyLocationSearchRequest getRequest() {
        return request;
    }

    public void setRequest(CarSupplyLocationSearchRequest request) {
        this.request = request;
    }

    public CarSupplyLocationSearchResponse getResponse() {
        return response;
    }

    public void setResponse(CarSupplyLocationSearchResponse response) {
        this.response = response;
    }

}
