package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by yyang4 on 3/15/2018.
 */
@SuppressWarnings("PMD")
public class ACLQReq {
    private FilterType filter;

    public ACLQReq(Node reqNode) {
        filter = new FilterType();
        filter.setCollectionBoolean(null);
        filter.setDeliveryBoolean(null);
        filter.setOutOfOfficeHourBoolean(null);
        filter.setAirportVicinityBoolean(null);
        final List<Node> selectionOptionsList = PojoXmlUtil.getNodesByTagName(reqNode, "selectionOptions");
        for (final Node catNode : selectionOptionsList) {
            final String companyAccessIndicator = PojoXmlUtil.getNodeByTagName(catNode, "companyAccessIndicator").getTextContent();
            if ("CAT".equals(companyAccessIndicator)) {
                final List<Node> vicinityInfoList = PojoXmlUtil.getNodesByTagName(catNode, "vicinityInfo");
                if (!CompareUtil.isObjEmpty(vicinityInfoList)) {
                    for (final Node vicNode : vicinityInfoList) {
                        final String vicinityIndicator = PojoXmlUtil.getNodeByTagName(vicNode, "vicinityIndicator").getTextContent();
                        switch (vicinityIndicator) {
                            case "COL":
                                filter.setCollectionBoolean(true);
                                break;
                            case "DEL":
                                filter.setDeliveryBoolean(true);
                                break;
                            case "OHR":
                                filter.setOutOfOfficeHourBoolean(true);
                                break;
                            case "VIC":
                                filter.setAirportVicinityBoolean(true);
                                break;
                        }
                    }
                }
                break;
            }

        }
    }

    public FilterType getFilter() {
        return filter;
    }

    public void setFilter(FilterType filter) {
        this.filter = filter;
    }
}
