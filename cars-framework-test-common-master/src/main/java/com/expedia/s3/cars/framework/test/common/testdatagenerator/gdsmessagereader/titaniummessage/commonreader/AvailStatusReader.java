package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import org.w3c.dom.Node;

/**
 * Created by v-mechen on 12/7/2016.
 */
public class AvailStatusReader {
    private AvailStatusReader(){

    }
    /// <summary>
    /// Get avail status code form VehAvailCore Node
    /// </summary>
    /// <param name="carInventoryKey"></param>
    /// <param name="vehAvailCoreNode"></param>
    /// <returns></returns>
    public static String readVehAvailStatus(Node vehAvailCoreNode)
    {
        final String status = vehAvailCoreNode.getAttributes().getNamedItem("Status").getTextContent();
        if (null != status && status.equals("Available")){
            return "A";
        }
        else{
            return "X";
        }
    }
}
