package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.CarPolicyListType;
import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 1/15/2017.
 */
public class CarPolicyReader {
    private CarPolicyReader(){

    }
    /*
    Read CarPolicyList from GDS respponse
     */
    public static void readCarPolicyListFromGDSRsp(CarProductType car, Node response)
    {
        final CarPolicyListType carPolicies = new CarPolicyListType();
        final List<CarPolicyType> carPolicyTypeList = new ArrayList<CarPolicyType>();

        final List<Node> vendorMessages = PojoXmlUtil.getNodesByTagName(response, "VendorMessage");
        for (final Node vendorMessage : vendorMessages)
        {
            final Node subSectionNode = PojoXmlUtil.getNodeByTagName(vendorMessage, "SubSection");
            if (null != subSectionNode)
            {
                final List<Node> paragraphNodes = PojoXmlUtil.getNodesByTagName(subSectionNode, "Paragraph");
                for (final Node paragraphNode : paragraphNodes)
                {
                    if (!StringUtils.isEmpty(paragraphNode.getTextContent()))
                    {
                        final CarPolicyType carPolicy = new CarPolicyType();
                        carPolicy.setCarPolicyRawText(paragraphNode.getTextContent());
                        carPolicy.setCarPolicyCategoryCode(null == paragraphNode.getAttributes().getNamedItem("Name") ? "Arrival" : paragraphNode.getAttributes().getNamedItem("Name").getTextContent());
                        carPolicyTypeList.add(carPolicy);
                    }
                }
            }
        }

        carPolicies.setCarPolicy(carPolicyTypeList);
        car.setCarPolicyList(carPolicies);
    }
}
