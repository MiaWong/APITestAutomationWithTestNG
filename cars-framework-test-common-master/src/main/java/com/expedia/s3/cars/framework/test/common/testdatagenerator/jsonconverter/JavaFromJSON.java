package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter;

//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CCSRSearchRequestTestData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CarConnectivitySearchCriteria;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.AuditLogTrackingData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarSearchStrategy;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.Language;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.PointOfSaleKey;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarVehicle;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarTransportationSegment;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarLocationKey;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.LoyaltyProgram;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarRate;


//import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
//import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;

//import java.io.File;
import java.io.IOException;
import java.util.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by jiyu on 12/27/16.
 */
public class JavaFromJSON
{
    private String jsonFileResource;
    private String scenario;

    public String getScenario() { return this.scenario; }
    private void setScenario(String scenario) { this.scenario = scenario; }

    public String getJsonFileResource() { return this.jsonFileResource; }
    private void setSJsonFileResource(String jsonFileResource) { this.jsonFileResource = jsonFileResource; }

    public JavaFromJSON(String scenarioName, String jsonFileName)
    {
        setScenario(scenarioName);
        setSJsonFileResource(jsonFileName);
    }

    public String inputStreamToUTF8String(final InputStream input)
    {
        final StringBuilder jsonString = new StringBuilder("");

        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            while (true)
            {
                final String str = in.readLine();
                if  (str == null) {
                    break;
                }
                jsonString.append(str);
            }
            in.close();
        }
        catch (IOException ex) {}

        return jsonString.toString();
    }


    public AuditLogTrackingData parseAuditLogTrackingData(JsonNode nodeScenarioData)
    {
        final JsonNode nodeAuditLogTrackData = nodeScenarioData.path("AuditLogTrackingData");
        final AuditLogTrackingData auditLogTrackingData = new AuditLogTrackingData();
        auditLogTrackingData.setLogonUserID( nodeAuditLogTrackData.path("LogonUserKey").asText());
        auditLogTrackingData.setTravelerUserID( nodeAuditLogTrackData.path("TravelerUserKey").asText());
        auditLogTrackingData.setAuditLogGUID( nodeAuditLogTrackData.path("AuditLogGUID").asText());
        auditLogTrackingData.setAuditLogTPID( nodeAuditLogTrackData.path("AuditLogTPID").asText());
        auditLogTrackingData.setAuditLogEAPID( nodeAuditLogTrackData.path("AuditLogEAPID").asText());
        auditLogTrackingData.setAuditLogGPID( nodeAuditLogTrackData.path("AuditLogGPID").asText());
        auditLogTrackingData.setAuditLogLanguageId( nodeAuditLogTrackData.path("AuditLogLanguageId").asText());
        auditLogTrackingData.setAuditLogForceLogging( nodeAuditLogTrackData.path("AuditLogForceLogging").asBoolean());
        auditLogTrackingData.setAuditLogForceDownstreamTransaction( nodeAuditLogTrackData.path("AuditLogForceDownstreamTransaction").asBoolean());

        return auditLogTrackingData;
    }

    public PointOfSaleKey parsePointOfSaleKey(JsonNode nodeScenarioData)
    {
        final JsonNode nodePOS = nodeScenarioData.path("PointOfSaleKey");
        final PointOfSaleKey posKey = new PointOfSaleKey();
        posKey.setJurisdictionCountryCode( nodePOS.path("JurisdictionCountryCode").asText());
        posKey.setCompanyCode( nodePOS.path("CompanyCode").asText());
        posKey.setManagementUnitCode( nodePOS.path("ManagementUnitCode").asText());

        return posKey;
    }

    public Language parseLanguageKey(JsonNode nodeScenarioData)
    {
        final JsonNode nodeLanguage = nodeScenarioData.path("Language");
        final Language langKey = new Language();
        langKey.setLanguageCode( nodeLanguage.path("LanguageCode").asText());
        langKey.setCountryAlpha2Code( nodeLanguage.path("CountryAlpha2Code").asText());

        return langKey;
    }

    public CarSearchStrategy parseCarSearchStrategy(JsonNode nodeScenarioData)
    {
        final JsonNode nodeSearchStrategy = nodeScenarioData.path("CarSearchStrategy");
        final CarSearchStrategy carSearchStrategy = new CarSearchStrategy();
        carSearchStrategy.setPricingVisibilityBoolean( nodeSearchStrategy.path("PricingVisibilityBoolean").asBoolean());
        carSearchStrategy.setPackageBoolean(  nodeSearchStrategy.path("PackageBoolean").asBoolean());

        return carSearchStrategy;
    }


    public CarTransportationSegment parseCarTransportationSegment(JsonNode nodeSearchCriteria)
    {
        final CarTransportationSegment carTransportationSegment = new CarTransportationSegment();
        final CarLocationKey startCarLocationKey = new CarLocationKey();
        final CarLocationKey endCarLocationKey = new CarLocationKey();
        final JsonNode nodeCarTransportationSegment = nodeSearchCriteria.path("CarTransportationSegment");

        final JsonNode nodeStartCarLocationKey = nodeCarTransportationSegment.path("StartCarLocationKey");
        startCarLocationKey.setCarVendorLocationID( nodeStartCarLocationKey.path("CarVendorLocationID").asInt(0));
        startCarLocationKey.setCarLocationCategoryCode( nodeStartCarLocationKey.path("CarLocationCategoryCode").asText(""));
        startCarLocationKey.setLocationCode( nodeStartCarLocationKey.path("LocationCode").asText());
        startCarLocationKey.setSupplierRawText( nodeStartCarLocationKey.path("SupplierRawText").asText(""));

        final JsonNode nodeEndCarLocationKey = nodeCarTransportationSegment.path("EndCarLocationKey");
        endCarLocationKey.setCarVendorLocationID( nodeEndCarLocationKey.path("CarVendorLocationID").asInt());
        endCarLocationKey.setCarLocationCategoryCode( nodeEndCarLocationKey.path("CarLocationCategoryCode").asText());
        endCarLocationKey.setLocationCode( nodeEndCarLocationKey.path("LocationCode").asText());
        endCarLocationKey.setSupplierRawText(nodeEndCarLocationKey.path("SupplierRawText").asText());

        final JsonNode nodeSegmentDateTimeRange = nodeCarTransportationSegment.path("SegmentDateTimeRange");
        final String strStartDateTime = nodeSegmentDateTimeRange.path("StartDateTime").asText();
        final String strEndDateTime = nodeSegmentDateTimeRange.path("EndDateTime").asText();

        carTransportationSegment.setStartCarLocationCodeKey( startCarLocationKey);
        carTransportationSegment.setEndCarLocationCodeKey( endCarLocationKey);
        carTransportationSegment.setStartDateTime( strStartDateTime);
        carTransportationSegment.setEndDateTime( strEndDateTime);

        return carTransportationSegment;
    }

    public List<CarVehicle> parseCarVehicleList(JsonNode nodeSearchCriteria)
    {
        final List<CarVehicle> carVehicleList = new ArrayList<CarVehicle>();
        final JsonNode nodeCarVehicleList = nodeSearchCriteria.path("CarVehicleList");
        final Iterator<JsonNode> itrCarVehicleList = nodeCarVehicleList.elements();
        while(itrCarVehicleList.hasNext())
        {
            final JsonNode nodeCarVehicle = itrCarVehicleList.next();
            final CarVehicle carVehicle = new CarVehicle();
            carVehicle.setCarCategoryCode( nodeCarVehicle.path("CarCategoryCode").asInt(0));
            carVehicle.setCarTypeCode( nodeCarVehicle.path("CarTypeCode").asInt(0));
            carVehicle.setCarTransmissionDriveCode( nodeCarVehicle.path("CarTransmissionDriveCode").asInt(0));
            carVehicle.setCarFuelACCode( nodeCarVehicle.path("CarFuelACCode").asInt(0));
            carVehicleList.add(carVehicle);
        }
        return carVehicleList;
    }


    public void parseCarSearchCriteriaMISC(CarConnectivitySearchCriteria carSearchCriteria, JsonNode nodeSearchCriteria)
    {
        //  read sequence #
        carSearchCriteria.setSequence( nodeSearchCriteria.path("Sequence").asInt());
        //  others
        carSearchCriteria.setCurrencyCode( nodeSearchCriteria.path("CurrencyCode").asText("USD"));
        carSearchCriteria.setSmokingBoolean( nodeSearchCriteria.path("SmokingBoolean").asBoolean());
        carSearchCriteria.setPrePaidFuelBoolean( nodeSearchCriteria.path("PrePaidFuelBoolean").asBoolean());
        carSearchCriteria.setUnlimitedMileageBoolean( nodeSearchCriteria.path("UnlimitedMileageBoolean").asBoolean());
        carSearchCriteria.setPackageBoolean(nodeSearchCriteria.path("PackageBoolean").asBoolean());
    }


    private LoyaltyProgram parseLoyaltyProgram(JsonNode nodeLoyaltyProgram)
    {
        LoyaltyProgram loyaltyProgram = null;
        if (nodeLoyaltyProgram == null)
        {
            loyaltyProgram = new LoyaltyProgram();
            loyaltyProgram.setSequence(nodeLoyaltyProgram.path("Sequence").asLong());
            loyaltyProgram.setLoyaltyProgramMembershipCode(nodeLoyaltyProgram.path("LoyaltyProgramMembershipCode").asText());
            loyaltyProgram.setLoyaltyProgramCategoryCode(nodeLoyaltyProgram.path("oyaltyProgramCategoryCode").asText());
            loyaltyProgram.setLoyaltyProgramCode(nodeLoyaltyProgram.path("setLoyaltyProgramCode").asText());
        }

        return loyaltyProgram;
    }


    public CarRate parseCarRate(CarConnectivitySearchCriteria carSearchCriteria, JsonNode nodeSearchCriteria)
    {
        final JsonNode nodeCarRate = nodeSearchCriteria.path("CarRate");

        CarRate carRate = null;
        if (nodeCarRate != null)
        {
            carRate = new CarRate();
            carRate.setCarRateQualifier(nodeCarRate.path("CarRateQualifier").asText());
            carRate.setCarAgreementID(nodeCarRate.path("CarAgreementID").asLong());
            carRate.setCarVendorAgreement(nodeCarRate.path("CarVendorAgreement").asText());
            carRate.setCorporateDiscountCode(nodeCarRate.path("CorporateDiscountCode").asText());
            carRate.setPromoteCode(nodeCarRate.path("PromoteCode(nodeCarRate").asText()) ;
            carRate.setRateCategoryCode(nodeCarRate.path("RateCategoryCode").asText());
            carRate.setRatePeriodCode(nodeCarRate.path("RatePeriodCode").asText());
            //  set loyaltyProgram
            carRate.setLoyaltyProgram(parseLoyaltyProgram(nodeCarRate.path("LoyaltyProgram")));
        }

        return carRate;
    }

}
