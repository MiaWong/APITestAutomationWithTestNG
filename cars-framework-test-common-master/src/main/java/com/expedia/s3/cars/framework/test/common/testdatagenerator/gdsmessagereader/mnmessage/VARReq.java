package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

/**
 * Created by fehu on 12/21/2016.
 */
public class VARReq {

    final private String pickUpLocation;
    final private String returnLocation;
    final private String pickUpDateTime;
    final private String returnDateTime;
    final  private String driverAge;
    final private String vendorCode;

    public String getVendorCode() {return vendorCode;  }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public String getPickUpDateTime() {
        return pickUpDateTime;
    }

    public String getReturnDateTime() {
        return returnDateTime;
    }

    public String getDriverAge() {
        return driverAge;
    }

    public VARReq(Node request) throws XPathExpressionException {

        final Node pickUpLocation  = PojoXmlUtil.getNodeByTagName(request, "PickUpLocation");
        this.pickUpLocation = pickUpLocation.getAttributes().getNamedItem("LocationCode").getNodeValue();

        final Node returnLocation = PojoXmlUtil.getNodeByTagName(request,"ReturnLocation");
        this.returnLocation = returnLocation.getAttributes().getNamedItem("LocationCode").getNodeValue();

        final Node driverAge = PojoXmlUtil.getNodeByTagName(request,"DriverType");
        this.driverAge = driverAge.getAttributes().getNamedItem("Age").getNodeValue();

        final Node vehRentalCore  = PojoXmlUtil.getNodeByTagName(request,"VehRentalCore") ;
        this.pickUpDateTime = vehRentalCore.getAttributes().getNamedItem("PickUpDateTime").getNodeValue();
        this.returnDateTime = vehRentalCore.getAttributes().getNamedItem("ReturnDateTime").getNodeValue();

        final Node vendorCodeNode = PojoXmlUtil.getNodeByTagName(request, "VendorPref");
        this.vendorCode = vendorCodeNode.getAttributes().getNamedItem("Code").getNodeValue();
    }
}
