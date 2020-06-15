package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by A-6281 on 04-10-2016.
 */
public class Utils
{
    private Utils(){}

    public static String setSpooferOverride(SpooferTransport spooferTransport, String scenarioName) throws IOException
    {
        final String randomGuid = generateRandomGuid();
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("ScenarioName", scenarioName);
        overrides.put("storeTransactions", "true");
        spooferTransport.setOverrides(overrides, randomGuid);
        return randomGuid;
    }

    public static String setReserveSpooferOverride(SpooferTransport spooferTransport, String scenarioName ,String vendorSupplierId,String carVehType, String splEquipment1,String splEquipment2) throws IOException
    {
        final String randomGuid = generateRandomGuid();
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("ScenarioName", scenarioName);
        overrides.put("vendorId", vendorSupplierId);
        overrides.put("storeTransactions", "true");
        overrides.put("vehType", carVehType);
        overrides.put("splEquipment1", splEquipment1);
        overrides.put("splEquipment2", splEquipment2);
        spooferTransport.setOverrides(overrides, randomGuid);
        return randomGuid;
    }
    public static String setGetReserveSpooferOverride(SpooferTransport spooferTransport, String scenarioName ,String vendorSupplierId,String carVehType) throws IOException
    {
        final String randomGuid = generateRandomGuid();
        final Map<String, String> overrides = new HashMap<>();
        overrides.put("ScenarioName", scenarioName);
        overrides.put("vendorId", vendorSupplierId);
        overrides.put("storeTransactions", "true");
        overrides.put("vehType", carVehType);
        spooferTransport.setOverrides(overrides, randomGuid);
        return randomGuid;
    }

    public static String getVehTypeFromSpooferTransactions(BasicVerificationContext verificationContext)
    {
        String vehType = "";
        final NodeList nodeList  = verificationContext.getSpooferTransactions().getElementsByTagName("VehRateRulesRS");
        final Element eElement = (Element) nodeList.item(0);
        vehType=   eElement.getElementsByTagName("VehType").item(0).getFirstChild().toString().substring(8,12);
        return vehType;

    }

    public static String generateRandomGuid()
    {
        return UUID.randomUUID().toString();
    }

}
