package com.expedia.s3.cars.supply.service.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by fehu on 7/11/2017.
 */
public class TestDataUtil {

    private final static Logger LOGGER = Logger.getLogger(com.expedia.s3.cars.framework.test.common.utils.TestDataUtil.class);

    private TestDataUtil() {
    }

    public static String getSupplysubsetTestData(long supplysbusetID, String testDataKey)
    {
        try
        {
            final StringBuffer jsonString = getJsonString(TestDataUtil.class.getClassLoader().getResourceAsStream("supplysubsetTestData.json"));
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode nodeRoot = objectMapper.readTree(jsonString.toString().getBytes());
            final JsonNode jsonNode = nodeRoot.path(String.valueOf(supplysbusetID));

            return jsonNode.path(testDataKey).asText();

        }
        catch (IOException ex) {
            LOGGER.error(ex.getStackTrace(),ex.getCause());
        }

        return "";
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




}
