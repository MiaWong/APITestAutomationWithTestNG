package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateDetailType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by v-mechen on 7/19/2018.
 */
public class CarInsuranceIncludedInRateReader {
    private CarInsuranceIncludedInRateReader(){}
    /**
     * Read CarInsuranceIncludedInRate
     *
     * @param car
     * @param pricedCoverages
     */
    public static void readCarInsuranceIncludedInRate(CarProductType car, NodeList pricedCoverages) {
        boolean carInsuranceIncludedInRate = false;
        if (car.getCarRateDetail() == null) {
            car.setCarRateDetail(new CarRateDetailType());
        }
        for (int i = 0; pricedCoverages.getLength() > i; i++) {
            final Node coverage = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverages.item(i).getChildNodes(), "Coverage");
            final String coverageType = (null == coverage) ? "" : coverage.getAttributes().getNamedItem("CoverageType").getTextContent();
            final Node charge = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverages.item(i).getChildNodes(), "Charge");
            if("7".equals(coverageType) && null != charge && null != charge.getAttributes().getNamedItem("IncludedInRate"))
            {
                carInsuranceIncludedInRate = Boolean.parseBoolean(charge.getAttributes().getNamedItem("IncludedInRate").getTextContent());
            }
        }
        car.getCarRateDetail().setCarInsuranceIncludedInRate(carInsuranceIncludedInRate);
    }

}
