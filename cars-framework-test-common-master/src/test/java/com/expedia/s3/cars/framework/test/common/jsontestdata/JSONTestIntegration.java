package com.expedia.s3.cars.framework.test.common.jsontestdata;

import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.ParametrizedQuery;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.CCSRJavaFromJSON;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.CCSRJavaToJSON;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.ccsrobject.CCSRSearchRequestTestData;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.testng.Assert;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import java.io.IOException;

/**
 * Created by jiyu on 12/28/16.
 */
public class JSONTestIntegration
{

    //
    //  scenario : will depend on the common scenarion logic and test data will be defined in json file per SCS or CarBS/CarSS
    //  jsonFile : each car service will create service-sepcific json which contain the key info for building the search request
    //
    private CCSRSearchRequestTestData testJsonTestDataReaderCCSR(String scenario, String jsonFile)
    {
        final CCSRJavaFromJSON jsonReader = new CCSRJavaFromJSON(scenario, jsonFile);
        final CCSRSearchRequestTestData searchRequestTestData = jsonReader.parseJSONFileAsResource();
        if (searchRequestTestData != null)
        {
            searchRequestTestData.logjsonTestData();
        }
        return searchRequestTestData;
    }

    private String testJsonTestDataWriterCCSR(String scenario, String jsonFile, CCSRSearchRequestTestData searchRequestTestData) throws Exception
    {
        final CCSRJavaToJSON jsonWriter = new CCSRJavaToJSON(scenario, jsonFile, searchRequestTestData);

        //  export to file
        jsonWriter.exportJSONFile();

        //  export to string
        final String jsonString = jsonWriter.exportJSONString();
        Assert.assertNotNull(jsonString);
        return jsonString;
    }

    @Ignore
    @Test
    public void testJsonTestDataReaderCCSR() throws Exception
    {
       CCSRSearchRequestTestData requestData = null;

        requestData = testJsonTestDataReaderCCSR("CCSR-Search-OnAirport", "jsonTestDataCCSR.json");
        Assert.assertNotNull(requestData);
        testJsonTestDataWriterCCSR("CCSR-Search-OnAirport-Test", "CCSR-Search-OnAirport-Test.json", requestData);

        requestData = testJsonTestDataReaderCCSR("CCSR-Search-OffAirport", "jsonTestDataCCSR.json");
        Assert.assertNotNull(requestData);
        testJsonTestDataWriterCCSR("CCSR-Search-OffAirport-Test", "CCSR-Search-OffAirport-Test.json", requestData);
       TestDataUtil.getAuditTestData("1011","USA","1010");
    }

    @Test
    public void exportvendorMap() throws Exception
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode nodeRoot = objectMapper.createObjectNode();
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource( DatabaseSetting.createDataSource("carsinventory.db.stt05.sb.karmalab.net", "CarsInventory_stt05",
                "", "maserati", "maserati", true));

        final List<CarVendor> carVendors = carsInventoryDataSource.getCarVendorList("");
        for (final CarVendor carVendor : carVendors)
        {
            nodeRoot.put(carVendor.getCarVendorCode(), carVendor.getSupplierID());
        }
        final OutputStream outputStream = new FileOutputStream(AppConfig.getPath()+ "\\src\\main\\resources\\vendorMap.json");
         //  export to JSON tree node, Create conceptual tree using rootnode, then adding elements on different levels. Use JsonGenerator to write the content.
        final JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
          //  write to JSON
        objectMapper.writeTree(jsonGenerator, nodeRoot);
        //  close the json generator
        jsonGenerator.close();

        TestDataUtil.getSupplierIDByVendorCode("ZT");
        TestDataUtil.getSupplySubIDs("WorldspanStandaloneOnAirport");


          }

    @Test
    public void exportTpidAndLanguageId() throws Exception
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode nodeAuditLogTrackData = objectMapper.createObjectNode();
        final DataSource inventoryDataSource = DatabaseSetting.createDataSource("carsinventory.db.stt05.sb.karmalab.net", "CarsInventory_stt05",
                "", "maserati", "maserati", true);
        final DataSource configrationDataSource = DatabaseSetting.createDataSource("configurationmaster.db.stt05.sb.karmalab.net", "ConfigurationMaster_stt05",
                "", "CarSupply", "Expedia!123", true);

        final ParametrizedQuery<AuditLogTrackingData> tsql = new ParametrizedQuery<>("Select * from TPIDToPoSAttributeMap", inventoryDataSource, AuditLogTrackingData.class);
        final Map<String, Object> paramMap = new HashMap<>();
        final List<AuditLogTrackingData> auditLogTrackingDataTypes = tsql.execute(paramMap);


        for (final AuditLogTrackingData auditLogTrackingData : auditLogTrackingDataTypes)
        {
            final String key = auditLogTrackingData.getCompanyCode()+"," + auditLogTrackingData.getJurisdictionCode()
                    + "," + auditLogTrackingData.getManagementUnitCode();
            if(null != nodeAuditLogTrackData.findValue(key))
            {
                continue;
            }
            final ParametrizedQuery<AuditLogTrackingDataType> tsqla = new ParametrizedQuery<>("Select LangID as auditLogLanguageId from LocaleProduct where TravelProductID =:travelProductID", configrationDataSource, AuditLogTrackingDataType.class);
            final Map<String, Object> paramMap1 = new HashMap<>();
            paramMap1.put("travelProductID", auditLogTrackingData.getTravelProductID());

            final List<AuditLogTrackingDataType> langID = tsqla.execute(paramMap1);

            final ObjectNode nodeChild = objectMapper.createObjectNode();
            nodeChild.put("TPID", auditLogTrackingData.getTravelProductID());
            nodeChild.put("EAPID",auditLogTrackingData.getPartnerID());
            nodeChild.put("LanguageId", CollectionUtils.isNotEmpty(langID) ? langID.get(0).getAuditLogLanguageId() : 1033l);
            nodeAuditLogTrackData.put(auditLogTrackingData.getCompanyCode()+"," + auditLogTrackingData.getJurisdictionCode()
                    + "," + auditLogTrackingData.getManagementUnitCode(), nodeChild);
        }

        final OutputStream outputStream = new FileOutputStream(AppConfig.getPath()+ "\\src\\main\\resources\\tpidAndLanguageId.json");
        //  export to JSON tree node, Create conceptual tree using rootnode, then adding elements on different levels. Use JsonGenerator to write the content.
        final JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
        //  write to JSON
        objectMapper.writeTree(jsonGenerator, nodeAuditLogTrackData);
        //  close the json generator
        jsonGenerator.close();

    }

}
