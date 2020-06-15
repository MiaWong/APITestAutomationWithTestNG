package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.AirFlightType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by v-mechen on 2/28/2017.
 */
public class AirFlightReader {
    private AirFlightReader(){

    }

    public static AirFlightType readAirFlight(Node gdsNode)
    {
        AirFlightType airFlight = new AirFlightType();
        final List<Node> arrivalNodeList = PojoXmlUtil.getNodesByTagName(gdsNode, "ArrivalDetails");
        if (arrivalNodeList.isEmpty())
        {
            airFlight = null;
        }
        else
        {
            airFlight.setFlightNumber(arrivalNodeList.get(0).getAttributes().getNamedItem("Number").getTextContent());
            airFlight.setAirCarrierCode(arrivalNodeList.get(0).getChildNodes().item(0).getAttributes().getNamedItem("Code").getTextContent());
        }
        return airFlight;
    }
}
