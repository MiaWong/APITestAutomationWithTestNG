package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.SupplySubsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 7/11/2017.
 */
public class TestDataUtil {

    private final static Logger LOGGER = Logger.getLogger(TestDataUtil.class);

    private TestDataUtil() {
    }

    public static String getSupplierIDByVendorCode(String vendorCode)
    {
        try
        {
            final InputStream input = TestDataUtil.class.getClassLoader().getResourceAsStream("vendorMap.json");
            final StringBuffer jsonString = new StringBuffer();
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

            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode nodeRoot = objectMapper.readTree(jsonString.toString().getBytes());
            return nodeRoot.path(vendorCode).asText();

        }
        catch (IOException ex) {
            LOGGER.error(ex.getStackTrace(),ex.getCause());
        }

        return null;
    }

    public static Map<String, String> getAuditTestData(String companyCode, String jurisdictionCode, String managementUnitCode)
    {
        final Map<String, String> auditTestData = new HashMap<>();
        try
        {
            final StringBuffer jsonString = getJsonString(TestDataUtil.class.getClassLoader().getResourceAsStream("tpidAndLanguageId.json"));
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode nodeRoot = objectMapper.readTree(jsonString.toString().getBytes());
            final JsonNode jsonNode = nodeRoot.path(companyCode + "," + jurisdictionCode + "," + managementUnitCode);

            auditTestData.put("TPID", jsonNode.path("TPID").asText());
            auditTestData.put("EAPID", jsonNode.path("EAPID").asText());
            auditTestData.put("LanguageId", jsonNode.path("LanguageId").asText());


         return auditTestData;
        }
        catch (IOException ex) {
            LOGGER.error(ex.getStackTrace(),ex.getCause());
        }

        return auditTestData;
    }

    private static StringBuffer getJsonString(InputStream input) throws IOException {
        final StringBuffer jsonString = new StringBuffer();
        final BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        while (true)
        {
            final String str = in.readLine();
            if  (str == null)
            {
                break;
            }
            jsonString.append(str);
        }
        in.close();
        return jsonString;
    }


    public static List<SupplySubsets> getSupplySubIDs(String pathName){

        final List<SupplySubsets> supplySubsets = new ArrayList<>();
        try
        {
            final InputStream input = TestDataUtil.class.getClassLoader().getResourceAsStream("scsTestAutomationData.json");
            final StringBuffer jsonString = new StringBuffer();
            final BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            while (true)
            {
                final String str = in.readLine();
                if  (str == null)
                {
                    break;
                }
                jsonString.append(str);
            }
            in.close();

            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode nodeRoot = objectMapper.readTree(jsonString.toString().getBytes());
            final JsonNode jsonNode = nodeRoot.path("SupplySubsetIDsForRegression").path(pathName);

            final String[] supsetID = jsonNode.path("supplysubsetID").asText().split(",");
            final String[] supID = jsonNode.path("supplierID").asText().split(",");
            for(int i=0; supsetID.length > i ; i++)
            {
                final SupplySubsets supplySubset = new SupplySubsets();
                supplySubset.setSupplySubsetID(Long.parseLong(supsetID[i]));
                if (supID.length >= 0 && StringUtil.isNotBlank(supID[0]))
                {
                    final String[] suppliIDs = supID[i].split("/");
                    final List<Long> supplierIDs = new ArrayList<>();
                    for(int j=0 ; suppliIDs.length > j ; j++)
                    {
                        supplierIDs.add(Long.parseLong(suppliIDs[j]));
                    }
                    supplySubset.setSupplierID(supplierIDs);
                }

                supplySubsets.add(supplySubset);
            }

        }
        catch (Exception ex) {
            LOGGER.error(ex.getStackTrace(), ex.getCause());
        }

        return supplySubsets;
    }

}
