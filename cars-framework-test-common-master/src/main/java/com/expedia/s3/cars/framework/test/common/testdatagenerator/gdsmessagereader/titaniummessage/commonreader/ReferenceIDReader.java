package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by v-mechen on 12/7/2016.
 */
public class ReferenceIDReader {
    private ReferenceIDReader(){

    }
    /// <summary>
    /// Read referenceID for one vehAvail
    /// </summary>
    /// <param name="vehAvailNode"></param>
    /// <returns></returns>
    public static String readReferenceID(Node vehAvailNode)
    {
        //        <Reference ID="0" Type="16">
        //<TPA_Extensions>
        //    <RateReference>52FFAR8090508598090911SEAZZZSEAZZZ</RateReference>
        //</TPA_Extensions>
        //ReferenceId = xmlDoc.GetElementsByTagName("RateReference", uapiNameSpace.all)[0].InnerText;

        return PojoXmlUtil.getNodeByTagName(vehAvailNode, "RateReference").getTextContent();

    }
}
