package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter;

//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CCSRSearchRequestTestData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CarConnectivitySearchCriteria;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.*;

//import com.fasterxml.jackson.core.JsonEncoding;
//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.util.List;

/**
 * Created by jiyu on 12/27/16.
 */
public class JavaToJSON
{
    private String jsonFileResource;
    private String scenario;
    private File jsonFile;

    public String getScenario() { return this.scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }

    public String getJsonFileResource() { return this.jsonFileResource; }
    public void setJsonFileResource(String jsonFileResource) { this.jsonFileResource = jsonFileResource; }

    public File getJsonFile() { return this.jsonFile; }
    public void setJsonFile(File file)
    {
        this.jsonFile = file;

        try {
            if (!file.exists()) {
                //  the file is created under local Git store project\cars-framework-test-common
                file.createNewFile();
            }
        }
        catch (IOException ex)
        {

        }
    }


    public JavaToJSON(String scenarioName, String jsonFileName)
    {
        setScenario(scenarioName);
        setJsonFileResource(jsonFileName);
    //  setCCSRRequestData(requestData);
    }


    public void exportAuditLogTrackingData(JsonNode nodeScenarioData, ObjectMapper objectMapper, AuditLogTrackingData auditLogTrackingData)
    {
        final JsonNode nodeAuditLogTrackData = objectMapper.createObjectNode();

     // final AuditLogTrackingData auditLogTrackingData = this.getCCSRRequestData().getAuditLogTrackingData();
        ((ObjectNode)nodeAuditLogTrackData).put("LogonUserKey", auditLogTrackingData.getLogonUserID());
        ((ObjectNode)nodeAuditLogTrackData).put("TravelerUserKey", auditLogTrackingData.getTravelerUserID());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogGUID", auditLogTrackingData.getAuditLogGUID());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogTPID", auditLogTrackingData.getAuditLogTPID());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogEAPID", auditLogTrackingData.getAuditLogEAPID());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogGPID", auditLogTrackingData.getAuditLogGPID());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogLanguageId", auditLogTrackingData.getAuditLogLanguageId());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogForceLogging", auditLogTrackingData.isAuditLogForceLogging());
        ((ObjectNode)nodeAuditLogTrackData).put("AuditLogForceDownstreamTransaction", auditLogTrackingData.isAuditLogForceDownstreamTransaction());

        ((ObjectNode)nodeScenarioData).put("AuditLogTrackingData", nodeAuditLogTrackData);
    }

    public void exportPointOfSaleKey(JsonNode nodeScenarioData, ObjectMapper objectMapper, PointOfSaleKey pointOfSaleKey)
    {
        final JsonNode nodePOS = objectMapper.createObjectNode();

    //  final PointOfSaleKey pointOfSaleKey =  this.getCCSRRequestData().getPointOfSaleKey();
        ((ObjectNode)nodePOS).put("JurisdictionCountryCode", pointOfSaleKey.getJurisdictionCountryCode());
        ((ObjectNode)nodePOS).put("CompanyCode", pointOfSaleKey.getCompanyCode());
        ((ObjectNode)nodePOS).put("ManagementUnitCode", pointOfSaleKey.getManagementUnitCode());

        ((ObjectNode)nodeScenarioData).put("PointOfSaleKey", nodePOS);
    }

    public void exportLanguageKey(JsonNode nodeScenarioData, ObjectMapper objectMapper, Language language)
    {
        final JsonNode nodeLanguage = objectMapper.createObjectNode();
    //  final Language language =  this.getCCSRRequestData().getLanguage();
        ((ObjectNode)nodeLanguage).put("LanguageCode", language.getLanguageCode());
        ((ObjectNode)nodeLanguage).put("CountryAlpha2Code", language.getCountryAlpha2Code());

        ((ObjectNode)nodeScenarioData).put("Language", nodeLanguage);
    }


    public void exportCarSearchStrategy(JsonNode nodeScenarioData, ObjectMapper objectMapper, CarSearchStrategy carSearchStrategy)
    {
        final JsonNode nodeSearchStrategy = objectMapper.createObjectNode();

    //  final CarSearchStrategy carSearchStrategy = this.getCCSRRequestData().getCarSearchStrategy();
        ((ObjectNode)nodeSearchStrategy).put("PricingVisibilityBoolean", carSearchStrategy.isPricingVisibilityBoolean());
        ((ObjectNode)nodeSearchStrategy).put("PackageBoolean", carSearchStrategy.isPackageBoolean());

        ((ObjectNode)nodeScenarioData).put("CarSearchStrategy", nodeSearchStrategy);
    }


    public void exportCarTransportationSegment(JsonNode nodeSearchCriteria, ObjectMapper objectMapper, CarConnectivitySearchCriteria carSearchCriteria)
    {
        final JsonNode nodeCarTransportationSegment = objectMapper.createObjectNode();

        final CarTransportationSegment carTransportationSegment = carSearchCriteria.getCarTransportSegment();
        final CarLocationKey startCarLocationKey = carTransportationSegment.getStartCarLocationCodeKey();
        final CarLocationKey endCarLocationKey = carTransportationSegment.getEndCarLocationCodeKey();

        final JsonNode nodeStartCarLocationKey = objectMapper.createObjectNode();
        ((ObjectNode) nodeStartCarLocationKey).put("CarVendorLocationID", startCarLocationKey.getCarVendorLocationID());
        ((ObjectNode) nodeStartCarLocationKey).put("CarLocationCategoryCode", startCarLocationKey.getCarLocationCategoryCode());
        ((ObjectNode) nodeStartCarLocationKey).put("LocationCode", startCarLocationKey.getLocationCode());
        ((ObjectNode) nodeStartCarLocationKey).put("SupplierRawText", startCarLocationKey.getSupplierRawText());

        final JsonNode nodeEndCarLocationKey = objectMapper.createObjectNode();
        ((ObjectNode) nodeEndCarLocationKey).put("CarVendorLocationID", endCarLocationKey.getCarVendorLocationID());
        ((ObjectNode) nodeEndCarLocationKey).put("CarLocationCategoryCode", endCarLocationKey.getCarLocationCategoryCode());
        ((ObjectNode) nodeEndCarLocationKey).put("LocationCode", endCarLocationKey.getLocationCode());
        ((ObjectNode) nodeEndCarLocationKey).put("SupplierRawText", endCarLocationKey.getSupplierRawText());

        final JsonNode nodeSegmentDateTimeRange = objectMapper.createObjectNode();;
        ((ObjectNode) nodeSegmentDateTimeRange).put("StartDateTime", carTransportationSegment.getStartDateTime());
        ((ObjectNode) nodeSegmentDateTimeRange).put("EndDateTime", carTransportationSegment.getEndDateTime());

        ((ObjectNode)nodeCarTransportationSegment).put("StartCarLocationKey", nodeStartCarLocationKey);
        ((ObjectNode)nodeCarTransportationSegment).put("EndCarLocationKey", nodeEndCarLocationKey);
        ((ObjectNode)nodeCarTransportationSegment).put("SegmentDateTimeRange", nodeSegmentDateTimeRange);

        ((ObjectNode)nodeSearchCriteria).put("CarTransportationSegment", nodeCarTransportationSegment);
    }

    public void exportCarVehicleList(JsonNode nodeSearchCriteria, ObjectMapper objectMapper, CarConnectivitySearchCriteria carSearchCriteria)
    {
        final ArrayNode nodeCarVehicleList = ((ObjectNode)nodeSearchCriteria).putArray("CarVehicleList");

        final List<CarVehicle> carVehicleList = carSearchCriteria.getCarVehicleList();
        for (final CarVehicle carVhicle : carVehicleList)
        {
            final JsonNode nodeCarVehicle = objectMapper.createObjectNode();

            ((ObjectNode)nodeCarVehicle).put("CarCategoryCode", carVhicle.getCarCategoryCode());
            ((ObjectNode)nodeCarVehicle).put("CarTypeCode", carVhicle.getCarTypeCode());
            ((ObjectNode)nodeCarVehicle).put("CarTransmissionDriveCode", carVhicle.getCarTransmissionDriveCode());
            ((ObjectNode)nodeCarVehicle).put("CarFuelACCode", carVhicle.getCarFuelACCode());

            nodeCarVehicleList.add(nodeCarVehicle);
        }
    }

    public void exportCarRate(JsonNode nodeSearchCriteria, ObjectMapper objectMapper, CarRate carRate)
    {
        final JsonNode nodeCarRate = objectMapper.createObjectNode();

        if (carRate != null) {
            //  final PointOfSaleKey pointOfSaleKey =  this.getCCSRRequestData().getPointOfSaleKey();
            ((ObjectNode) nodeCarRate).put("CarRateQualifier", carRate.getCarRateQualifier());
            ((ObjectNode) nodeCarRate).put("CarAgreementID", carRate.getCarAgreementID());
            ((ObjectNode) nodeCarRate).put("setCarVendorAgreement", carRate.getCarVendorAgreement());
            ((ObjectNode) nodeCarRate).put("CorporateDiscountCode", carRate.getCorporateDiscountCode());
            ((ObjectNode) nodeCarRate).put("PromoteCode", carRate.getPromoteCode());
            ((ObjectNode) nodeCarRate).put("RateCategoryCode", carRate.getRateCategoryCode());
            ((ObjectNode) nodeCarRate).put("RatePeriodCode", carRate.getRatePeriodCode());
            //  loyaltyProgram
            final JsonNode nodeLoyaltyProgram = objectMapper.createObjectNode();
            if (carRate.getLoyaltyProgram() != null)
            {
                ((ObjectNode) nodeLoyaltyProgram).put("LoyaltyProgramCategoryCode", carRate.getLoyaltyProgram().getLoyaltyProgramCategoryCode());
                ((ObjectNode) nodeLoyaltyProgram).put("LoyaltyProgramCode", carRate.getLoyaltyProgram().getLoyaltyProgramCode());
                ((ObjectNode) nodeLoyaltyProgram).put("LoyaltyProgramMembershipCode", carRate.getLoyaltyProgram().getLoyaltyProgramMembershipCode());
                ((ObjectNode) nodeLoyaltyProgram).put("Sequence", carRate.getLoyaltyProgram().getSequence());

            }
            ((ObjectNode)nodeCarRate).put("LoyaltyProgram", nodeLoyaltyProgram);

        }
        ((ObjectNode)nodeSearchCriteria).put("CarRate", nodeCarRate);
    }

}
