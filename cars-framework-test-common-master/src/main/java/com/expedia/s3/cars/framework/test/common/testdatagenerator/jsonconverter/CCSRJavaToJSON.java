package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CCSRSearchRequestTestData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CarConnectivitySearchCriteria;
//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.AuditLogTrackingData;
//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarLocationKey;
//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarTransportationSegment;
//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarVehicle;
//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.CarRate;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.util.List;

/**
 * Created by jiyu on 1/12/17.
 */
public class CCSRJavaToJSON extends JavaToJSON
{
    private CCSRSearchRequestTestData searchRequestData;

    public CCSRSearchRequestTestData getCCSRRequestData() { return this.searchRequestData; }
    private void setCCSRRequestData(CCSRSearchRequestTestData requestData) { this.searchRequestData = requestData; }


    public CCSRJavaToJSON(String scenarioName, String jsonFileName, CCSRSearchRequestTestData requestData)
    {
        super(scenarioName, jsonFileName);
        setCCSRRequestData(requestData);
    }

    private void exportCarSearchCriteriaList(JsonNode nodeScenarioData, ObjectMapper objectMapper)
    {
        final ArrayNode nodeSearchCriteriaList = ((ObjectNode)nodeScenarioData).putArray("CarSearchCriteriaList");

        final List<CarConnectivitySearchCriteria> carSearchCriteriaList = this.searchRequestData.getCarSearchCriteriaList();
        for (final CarConnectivitySearchCriteria carSearchCriteria : carSearchCriteriaList)
        {
            final JsonNode nodeSearchCriteria = objectMapper.createObjectNode();

            exportSupplySubsetIDEntryList(nodeSearchCriteria, carSearchCriteria);
            exportCarTransportationSegment(nodeSearchCriteria, objectMapper, carSearchCriteria);
            exportCarVehicleList(nodeSearchCriteria, objectMapper, carSearchCriteria);
            exportVendorSupplierIDList(nodeSearchCriteria, carSearchCriteria);
            exportCarRate(nodeSearchCriteria, objectMapper, carSearchCriteria.getCarRate());
            exportCarSearchCriteriaMISC(nodeSearchCriteria, carSearchCriteria);

            nodeSearchCriteriaList.add(nodeSearchCriteria);
        }

    }

    private void exportSupplySubsetIDEntryList(JsonNode nodeSearchCriteria, CarConnectivitySearchCriteria carSearchCriteria)
    {
        final ArrayNode nodeSupplySubsetIDEntryList = ((ObjectNode)nodeSearchCriteria).putArray("SupplySubsetIDEntryList");

        //  read SupplySubsetIDEntryList : array of int
        final List<String> supplySubsetIDEntryList = carSearchCriteria.getSupplySubsetIDEntryList();
        for (final String supplySubsetIDEntry : supplySubsetIDEntryList)
        {
            nodeSupplySubsetIDEntryList.add(supplySubsetIDEntry);
        }
    }

    private void exportVendorSupplierIDList(JsonNode nodeSearchCriteria, CarConnectivitySearchCriteria carSearchCriteria)
    {
        final ArrayNode nodeVendorSupplierIDList = ((ObjectNode)nodeSearchCriteria).putArray("VendorSupplierIDList");

        final List<String> vendorSupplierIDList = carSearchCriteria.getVendorSupplierIDList();
        for (final String vendorSupplierID : vendorSupplierIDList)
        {
            nodeVendorSupplierIDList.add(vendorSupplierID);
        }

    }

    private void exportCarSearchCriteriaMISC(JsonNode nodeSearchCriteria, CarConnectivitySearchCriteria carSearchCriteria)
    {
        ((ObjectNode) nodeSearchCriteria).put("Sequence", carSearchCriteria.getSequence());
        ((ObjectNode) nodeSearchCriteria).put("CurrencyCode", carSearchCriteria.getCurrencyCode());
        //((ObjectNode) nodeSearchCriteria).put("CarRate", carSearchCriteria.getCarRate());
        ((ObjectNode) nodeSearchCriteria).put("SmokingBoolean", carSearchCriteria.isSmokingBoolean());
        ((ObjectNode) nodeSearchCriteria).put("PrePaidFuelBoolean", carSearchCriteria.isPrePaidFuelBoolean());
        ((ObjectNode) nodeSearchCriteria).put("UnlimitedMileageBoolean", carSearchCriteria.isUnlimitedMileageBoolean());
        ((ObjectNode) nodeSearchCriteria).put("PackageBoolean", carSearchCriteria.isPackageBoolean());
    }

    public JsonNode ccsrTestdataHandling(ObjectMapper objectMapper, JsonGenerator jsonGenerator)
    {
        jsonGenerator.useDefaultPrettyPrinter();

        //  export to JSON tree node, Create conceptual tree using rootnode, then adding elements on different levels. Use JsonGenerator to write the content.

        //  root
        final JsonNode nodeRoot = objectMapper.createObjectNode();

        //  top level of individual test data: scenario
        final JsonNode nodeScenarioData = objectMapper.createObjectNode();

        //  2nd level : "MessageInfo":"CarSupplyConnectivitySearchRequest" : skip no need to process

        //  2nd level : "AuditLogTrackingData"
        exportAuditLogTrackingData(nodeScenarioData, objectMapper, this.getCCSRRequestData().getAuditLogTrackingData());

        //  2nd lvel : "PointOfSaleKey":
        exportPointOfSaleKey(nodeScenarioData, objectMapper, this.getCCSRRequestData().getPointOfSaleKey());

        //  2nd level : "Language"
        exportLanguageKey(nodeScenarioData, objectMapper, this.getCCSRRequestData().getLanguage());

        //  2nd level : "CarSearchStrategy"
        exportCarSearchStrategy(nodeScenarioData, objectMapper, this.getCCSRRequestData().getCarSearchStrategy());

        //  2nd level : "CarSearchCriteriaList":[
        exportCarSearchCriteriaList(nodeScenarioData, objectMapper);

        //  add scenario node into root : root->"scenario" : {} : 2nd  level
        ((ObjectNode)nodeRoot).put(this.getScenario(), nodeScenarioData);

        return nodeRoot;
    }


    //  test hook : export json file
    public void exportJSONFile() throws JsonParseException, JsonGenerationException, JsonMappingException, IOException, Exception
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        //  export to file
        setJsonFile(new File(this.getJsonFileResource()));
        final OutputStream outputStream = new FileOutputStream(getJsonFile());

        //  export to JSON tree node, Create conceptual tree using rootnode, then adding elements on different levels. Use JsonGenerator to write the content.
        final JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
        //  root
        final JsonNode nodeRoot = ccsrTestdataHandling(objectMapper, jsonGenerator);
        //  write to JSON
        objectMapper.writeTree(jsonGenerator, nodeRoot);
        //  close the json generator
        jsonGenerator.close();
        outputStream.close();

    }

    public String exportJSONString() throws JsonParseException, JsonGenerationException, JsonMappingException, IOException, Exception
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        //  export to string write
        final StringWriter strWriter = new StringWriter();
        //  export to JSON tree node, Create conceptual tree using rootnode, then adding elements on different levels. Use JsonGenerator to write the content.
        final JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(strWriter);
        //  root
        final JsonNode nodeRoot = ccsrTestdataHandling(objectMapper, jsonGenerator);
        //  write to JSON
        objectMapper.writeTree(jsonGenerator, nodeRoot);
        //  close the json generator
        jsonGenerator.close();

        return (strWriter == null) ? null : strWriter.toString();
    }


}
