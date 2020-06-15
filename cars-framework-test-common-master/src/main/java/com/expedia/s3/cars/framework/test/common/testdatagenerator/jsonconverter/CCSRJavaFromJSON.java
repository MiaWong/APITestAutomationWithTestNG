package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter;

import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CCSRSearchRequestTestData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CarConnectivitySearchCriteria;
//import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jiyu on 1/12/17.
 */
public class CCSRJavaFromJSON extends JavaFromJSON
{
    public CCSRJavaFromJSON(String scenarioName, String jsonFileName)
    {
        super(scenarioName, jsonFileName);
    }

    //  read json file as resource
    public CCSRSearchRequestTestData parseJSONFileAsResource()
    {
        CCSRSearchRequestTestData searchRQ = null;

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final File jsonFile = new File(classLoader.getResource(getJsonFileResource()).getFile());
        try (final InputStream input = new FileInputStream(jsonFile)) {
            final String jsonString = inputStreamToUTF8String(input);
            searchRQ = parseJSONStream(getScenario(), jsonString);
        }
        catch (IOException ex) { searchRQ = null; }
        catch (Exception ex) { searchRQ = null; }

        return searchRQ;
    }

    //  read json file as byte stream
    public CCSRSearchRequestTestData parseJSONFile()
    {
        CCSRSearchRequestTestData searchRQ = null;
        try {
            // Convert JSON string from file to Object
            final byte[] jsonData = Files.readAllBytes(Paths.get(getJsonFileResource()));
            searchRQ = parseJSONStream(getScenario(), jsonData);
        }
        catch (IOException ex) { searchRQ = null; }
        catch (Exception ex) { searchRQ = null; }

        return searchRQ;
    }

    public CCSRSearchRequestTestData parseJSONStream(String scenario, String jsonStream)
    {
        return parseJSONStream(scenario, jsonStream.getBytes());
    }


    private List<CarConnectivitySearchCriteria> parseCarSearchCriteriaList(JsonNode nodeScenarioData)
    {
        //  read CarSearchCriteriaList
        final List<CarConnectivitySearchCriteria> carSearchCriteriaList = new ArrayList<CarConnectivitySearchCriteria>();
        final JsonNode nodeSearchCriteriaList = nodeScenarioData.path("CarSearchCriteriaList");
        final Iterator<JsonNode> elements = nodeSearchCriteriaList.elements();
        while(elements.hasNext()){
            final CarConnectivitySearchCriteria carSearchCriteria = new CarConnectivitySearchCriteria();
            final JsonNode nodeSearchCriteria = elements.next();
            //  read SupplySubsetIDEntryList : array of int
            carSearchCriteria.setSupplySubsetIDEntryList( parseSupplySubsetIDEntryList(nodeSearchCriteria));
            //  read CarTransportationSegment
            carSearchCriteria.setCarTransportSegment( parseCarTransportationSegment(nodeSearchCriteria));
            //  CarVehicleList : array of object
            carSearchCriteria.setCarVehicleList( parseCarVehicleList(nodeSearchCriteria));
            //  VendorSupplierIDList : array of int
            carSearchCriteria.setVendorSupplierIDList( parseVendorSupplierIDList(nodeSearchCriteria));
            //  carrate
            carSearchCriteria.setCarRate(parseCarRate(carSearchCriteria, nodeSearchCriteria));
            //  dequence # & other MISC
            parseCarSearchCriteriaMISC(carSearchCriteria, nodeSearchCriteria);
            //  add into SearchCriteriaList
            carSearchCriteriaList.add(carSearchCriteria);

        }

        return carSearchCriteriaList;
    }

    private List<String> parseSupplySubsetIDEntryList(JsonNode nodeSearchCriteria)
    {
        //  read SupplySubsetIDEntryList : array of int
        final List<String> supplySubsetIDEntryList = new ArrayList<String>();
        final JsonNode nodeSupplySubsetIDEntryList = nodeSearchCriteria.path("SupplySubsetIDEntryList");
        final Iterator<JsonNode> itrSupplySubsetIDEntryList = nodeSupplySubsetIDEntryList.elements();
        while(itrSupplySubsetIDEntryList.hasNext())
        {
            final JsonNode nodeSupplySubsetIDEntry = itrSupplySubsetIDEntryList.next();
            supplySubsetIDEntryList.add(nodeSupplySubsetIDEntry.asText());
        }
        return supplySubsetIDEntryList;
    }

    private List<String> parseVendorSupplierIDList(JsonNode nodeSearchCriteria)
    {
        final List<String> vendorSupplierIDList = new ArrayList<String>();
        final JsonNode nodeVendorSupplierIDList = nodeSearchCriteria.path("VendorSupplierIDList");
        final Iterator<JsonNode> itrVendorSupplierIDList = nodeVendorSupplierIDList.elements();
        while(itrVendorSupplierIDList.hasNext())
        {
            final JsonNode nodeVendorSupplierID = itrVendorSupplierIDList.next();
            vendorSupplierIDList.add(nodeVendorSupplierID.asText());
        }

        return vendorSupplierIDList;
    }

    //  parse the json object via byte stream
    public CCSRSearchRequestTestData parseJSONStream(String scenario, byte [] byteStream)
    {
        CCSRSearchRequestTestData searchRQ = null;

        try {
            //convert json string to object
            searchRQ = new CCSRSearchRequestTestData();

            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode nodeRoot = objectMapper.readTree(byteStream);
            final JsonNode nodeScenarioData = nodeRoot.path(scenario);

            searchRQ.setScenarioName(scenario);
        /*
            searchRQ.setIsOnAirport(true);
            searchRQ.setPurchaseType(PurchaseType.CarOnly);
            searchRQ.setBusinessModel(BusinessModel.GDSP);
            searchRQ.setServiceID(7);
        */
            searchRQ.setAuditLogTrackingData(parseAuditLogTrackingData(nodeScenarioData));
            searchRQ.setPointOfSaleKey(parsePointOfSaleKey(nodeScenarioData));
            searchRQ.setLanguage(parseLanguageKey(nodeScenarioData));
            searchRQ.setCarSearchStrategy(parseCarSearchStrategy(nodeScenarioData));
            searchRQ.setCarSearchCriteriaList(parseCarSearchCriteriaList(nodeScenarioData));
        }
        catch (IOException ex) { searchRQ = null; }
        catch (Exception ex) { searchRQ = null; }

        return searchRQ;
    }

}
