package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 12/15/2016.
 */
public class VRSReq {
   final private Map<String, String> specialEquipmentCodes;

    public Map<String, String> getSpecialEquipmentCodes() {
        return specialEquipmentCodes;
    }

    public VRSReq(CarsSCSDataSource dataSource, Node requst) throws XPathExpressionException, DataAccessException {
        this.specialEquipmentCodes = getSpecialEquipmentCodes(requst, dataSource);
    }

    @SuppressWarnings("PMD")
    public Map<String, String> getSpecialEquipmentCodes(Node requst, CarsSCSDataSource dataSource) throws XPathExpressionException, DataAccessException {

        Map<String, String> specialEquipmentCodes = new HashMap<>();
        final List<Node> specialEquipPrefs = PojoXmlUtil.getNodesByTagName(requst, "SpecialEquipPref");
        if (CollectionUtils.isEmpty(specialEquipPrefs))
        {
          return null;
        }
         for (int i = 0; i < specialEquipPrefs.size(); i++) {
                final NamedNodeMap attributesMap = specialEquipPrefs.get(i).getAttributes();
                if (null != attributesMap) {
                    final Node equipTypeNode = attributesMap.getNamedItem("EquipType");
                    if (null != equipTypeNode) {
                        final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMapList = dataSource.getExternalSupplyServiceDomainValueMap(0l, 0l, "CarSpecialEquipment", null, equipTypeNode.getNodeValue());
                        if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMapList)) {
                            final StringBuffer ret = new StringBuffer();
                            for (final ExternalSupplyServiceDomainValueMap tbl : externalSupplyServiceDomainValueMapList) {
                                ret.append(tbl.getDomainValue());
                            }
                            specialEquipmentCodes.put(ret.toString(), equipTypeNode.getNodeValue());
                        }
                    }
                }
            }




        return specialEquipmentCodes;
    }
}

