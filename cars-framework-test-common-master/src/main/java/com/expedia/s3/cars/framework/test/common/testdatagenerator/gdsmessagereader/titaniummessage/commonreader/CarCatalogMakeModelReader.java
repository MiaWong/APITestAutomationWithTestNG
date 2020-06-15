package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by v-mechen on 5/3/2018.
 */
@SuppressWarnings("PMD")
public class CarCatalogMakeModelReader {
    private CarCatalogMakeModelReader() {
    }

    public static CarCatalogMakeModelType readCarCatalogMakeModel(Node vehAvailNode)
    {
        final CarCatalogMakeModelType makeModel = new CarCatalogMakeModelType();

       // <Vehicle AirConditionInd="true" BaggageQuantity="1" Code="ICAR" CodeContext="SUPPLIER_CODE" FuelType="Unspecified" PassengerQuantity="5" TransmissionType="Automatic">
       // <VehType DoorCount="2/4" VehicleCategory="1"></VehType>
       // <VehMakeModel Code="ICAR" Name="Chevrolet Cruze"></VehMakeModel>
      //  <VehIdentity VehicleAssetNumber="24829319"></VehIdentity>
       // </Vehicle>
        //Luggage count
        final String baggageQuantity = null == PojoXmlUtil.getNodeByTagName(vehAvailNode, "Vehicle").getAttributes().getNamedItem("BaggageQuantity") ? "0"
         : PojoXmlUtil.getNodeByTagName(vehAvailNode, "Vehicle").getAttributes().getNamedItem("BaggageQuantity").getTextContent();
        makeModel.setCarCapacityLargeLuggageCount(Long.parseLong(baggageQuantity));

        //passengerQuantity will be "5+4" or just "5" - first number represents a Adult Passenger count
        final String passengerQuantity = PojoXmlUtil.getNodeByTagName(vehAvailNode, "Vehicle").getAttributes().getNamedItem("PassengerQuantity").getTextContent();
        final String[] passengerList = passengerQuantity.split("\\+");
        makeModel.setCarCapacityAdultCount(Long.parseLong(passengerList[0]));
        if(passengerList.length > 1) {
            makeModel.setCarCapacityChildCount(Long.parseLong(passengerList[1]));
        }

        //Door count - "2/4" or "2-4" or just "5"
        final String doorCountString = PojoXmlUtil.getNodeByTagName(vehAvailNode, "VehType").getAttributes().getNamedItem("DoorCount").getTextContent();
        Long minDoorC = 0L;
        Long maxDoorC;
        if(doorCountString.contains("/"))
        {
            minDoorC = Long.parseLong(doorCountString.split("/")[0]);
            maxDoorC = Long.parseLong(doorCountString.split("/")[1]);
        }
        else if(doorCountString.contains("-"))
        {
            minDoorC = Long.parseLong(doorCountString.split("-")[0]);
            maxDoorC = Long.parseLong(doorCountString.split("-")[1]);
        }
        else {
            maxDoorC = Long.parseLong(doorCountString);
        }
        makeModel.setCarMaxDoorCount(maxDoorC);
        makeModel.setCarMinDoorCount(minDoorC);

        //CarModelGuaranteedBoolean: VehAvailCore/TPA_Extensions/Vehicle/VehMakeModel/@Guaranteed="true"
        final List<Node> tpaExtensions = PojoXmlUtil.getNodesByTagName(vehAvailNode, "TPA_Extensions");
        for(final Node tpaExtension : tpaExtensions)
        {
            final Node extVehMakeModel = PojoXmlUtil.getNodeByTagName(tpaExtension, "VehMakeModel");
            if(null != extVehMakeModel)
            {
                makeModel.setCarModelGuaranteedBoolean(Boolean.parseBoolean(extVehMakeModel.getAttributes().getNamedItem("Guaranteed").getTextContent()));
            }
        }


        //Image
        final Node imgURLNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(vehAvailNode, "Vehicle"),"PictureURL");
        if(makeModel.isCarModelGuaranteedBoolean() && null != imgURLNode)
        {
            makeModel.setImageFilenameString(imgURLNode.getTextContent());
            makeModel.setImageThumbnailFilenameString(imgURLNode.getTextContent());
        }

        //CarMakeString <VehMakeModel Code="EDAR" Name="Mitsubishi Mirage"></VehMakeModel>
        final Node makeModelgNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(vehAvailNode, "Vehicle"), "VehMakeModel");
        if(makeModel.isCarModelGuaranteedBoolean() && null != makeModelgNode)
        {
            makeModel.setCarMakeString(makeModelgNode.getAttributes().getNamedItem("Name").getTextContent());

        }

        return makeModel;

    }



}
