package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//Define a class VehAvails to parse the info for every "VehAvail" node in VAR response
public class VehAvails {
    //PickupLocationCode
    private String pickupLocationCode;
    //DropOffLocationCode
    private String dropOffLocationCode;
    //VehMakeModel Code
    private String vehMakeModelCode;
    //VendorRateID
    private String vendorRateID;
    //CarRateQualifierCode
    private String carRateQualifierCode;
    //VendorCode
    private String vendorCode;
    //FreeDistanceRatePeriodCode
    private String freeDistanceRatePeriodCode;
    //Preferred Charge
    private BigDecimal preferredCharge;
    //Preferred Charge CurrencyCode
    private String preferredChargeCurrencyCode;
    //Original Charge
    private BigDecimal originalCharge;
    //Original Charge CurrencyCode
    private String originalChargeCurrencyCode;
    //BaseRate(net rate)
    private BigDecimal baseRate;
    //Total Charge
    private BigDecimal totalCharge;
    //Total Charge CurrencyCode
    private String totalChargeCurrencyCode;
   //Fee
    private Fee fee;
    //List of PricedCoverages get from VAR message
    private List<PricedCoverages> pricedCoverages;
    //One-Way Charge
    private BigDecimal oneWayCharge;
    //One-Way Charge CurrencyCode
    private String oneWayChargeCurrencyCode;

    public String getPickupLocationCode() {
        return pickupLocationCode;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public void setPickupLocationCode(String pickupLocationCode) {
        this.pickupLocationCode = pickupLocationCode;
    }

    public String getDropOffLocationCode() {
        return dropOffLocationCode;
    }

    public void setDropOffLocationCode(String dropOffLocationCode) {
        this.dropOffLocationCode = dropOffLocationCode;
    }

    public String getVehMakeModelCode() {
        return vehMakeModelCode;
    }

    public void setVehMakeModelCode(String vehMakeModelCode) {
        this.vehMakeModelCode = vehMakeModelCode;
    }

    public String getVendorRateID() {
        return vendorRateID;
    }

    public void setVendorRateID(String vendorRateID) {
        this.vendorRateID = vendorRateID;
    }

    public String getCarRateQualifierCode() {
        return carRateQualifierCode;
    }

    public void setCarRateQualifierCode(String carRateQualifierCode) {
        this.carRateQualifierCode = carRateQualifierCode;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getFreeDistanceRatePeriodCode() {
        return freeDistanceRatePeriodCode;
    }

    public void setFreeDistanceRatePeriodCode(String freeDistanceRatePeriodCode) {
        this.freeDistanceRatePeriodCode = freeDistanceRatePeriodCode;
    }

    public BigDecimal getPreferredCharge() {
        return preferredCharge;
    }

    public void setPreferredCharge(BigDecimal preferredCharge) {
        this.preferredCharge = preferredCharge;
    }

    public String getPreferredChargeCurrencyCode() {
        return preferredChargeCurrencyCode;
    }

    public void setPreferredChargeCurrencyCode(String preferredChargeCurrencyCode) {
        this.preferredChargeCurrencyCode = preferredChargeCurrencyCode;
    }

    public BigDecimal getOriginalCharge() {
        return originalCharge;
    }

    public void setOriginalCharge(BigDecimal originalCharge) {
        this.originalCharge = originalCharge;
    }

    public String getOriginalChargeCurrencyCode() {
        return originalChargeCurrencyCode;
    }

    public void setOriginalChargeCurrencyCode(String originalChargeCurrencyCode) {
        this.originalChargeCurrencyCode = originalChargeCurrencyCode;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public String getTotalChargeCurrencyCode() {
        return totalChargeCurrencyCode;
    }

    public void setTotalChargeCurrencyCode(String totalChargeCurrencyCode) {
        this.totalChargeCurrencyCode = totalChargeCurrencyCode;
    }


    public List<PricedCoverages> getPricedCoverages() {
        return pricedCoverages;
    }

    public void setPricedCoverages(List<PricedCoverages> pricedCoverages) {
        this.pricedCoverages = pricedCoverages;
    }

    public BigDecimal getOneWayCharge() {
        return oneWayCharge;
    }

    public void setOneWayCharge(BigDecimal oneWayCharge) {
        this.oneWayCharge = oneWayCharge;
    }

    public String getOneWayChargeCurrencyCode() {
        return oneWayChargeCurrencyCode;
    }

    public void setOneWayChargeCurrencyCode(String oneWayChargeCurrencyCode) {
        this.oneWayChargeCurrencyCode = oneWayChargeCurrencyCode;
    }

    public VehAvails(Node vehAvail) throws XPathExpressionException {
        final Element vehAvailElement = (Element) vehAvail;
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final Node pickupLocationCodeNode = (Node) xpath.compile
                ("//VehAvailCore/VendorLocation")
                .evaluate(vehAvail, XPathConstants.NODE);
        //Get PickupLocationCode from VendorLocation node
        pickupLocationCode = pickupLocationCodeNode.getAttributes().getNamedItem("LocationCode").getTextContent().substring(0, 3);

        //Get DropOffLocationCode from DropOffLocation node
        final Node dropOffLocationCodeNode = (Node) xpath.compile
                ("//VehAvailCore/DropOffLocation")
                .evaluate(vehAvail, XPathConstants.NODE);
        dropOffLocationCode = dropOffLocationCodeNode.getAttributes().getNamedItem("LocationCode").getTextContent().substring(0, 3);

        //Get VehMakeModelCode from VehMakeModel node
        final Node vehMakeModelCodeNode = (Node) xpath.compile
                ("//VehAvailCore/Vehicle/VehMakeModel")
                .evaluate(vehAvail, XPathConstants.NODE);
        vehMakeModelCode = vehMakeModelCodeNode.getAttributes().getNamedItem("Code").getTextContent();

        //Get VendorRateID from RateQualifier node
        final Node vendorRateIDNode = (Node) xpath.compile
                ("//VehAvailCore/RentalRate/RateQualifier")
                .evaluate(vehAvail, XPathConstants.NODE);
        vendorRateID = vendorRateIDNode.getAttributes().getNamedItem("VendorRateID").getTextContent();

        //Get CarRateQualifierCode from Reference node
        final Node carRateQualifierCodeNode = (Node) xpath.compile
                ("//VehAvailCore/Reference")
                .evaluate(vehAvail, XPathConstants.NODE);
        carRateQualifierCode = carRateQualifierCodeNode.getAttributes().getNamedItem("ID_Context").getTextContent();

        //Get VendorCode from Vendor node
        final Node vendorCodeNode = (Node) xpath.compile
                ("//VehAvailCore/Vendor")
                .evaluate(vehAvail, XPathConstants.NODE);
        vendorCode = vendorCodeNode.getAttributes().getNamedItem("Code").getTextContent();

        //Get preferredCharge from VehicleCharges node
        setVehicleCharge(vehAvailElement);

        //freeDistanceRatePeriodCode
        final Node rateDistanceNode = (Node) xpath.compile
                ("//VehAvailCore/RentalRate/RateDistance")
                .evaluate(vehAvail, XPathConstants.NODE);
        if (null != rateDistanceNode.getAttributes().getNamedItem("VehiclePeriodUnitName")) {
            freeDistanceRatePeriodCode = rateDistanceNode.getAttributes().getNamedItem("VehiclePeriodUnitName").getTextContent().trim();
        }
        //Get TotalCharge from TotalCharge node
        final Node totalChargeNode = (Node) xpath.compile
                ("//VehAvailCore/TotalCharge")
                .evaluate(vehAvail, XPathConstants.NODE);
        totalCharge = new BigDecimal(totalChargeNode.getAttributes().getNamedItem("EstimatedTotalAmount").getTextContent().trim());
        totalChargeCurrencyCode = totalChargeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();

        //Get Fees
        setFee(vehAvailElement);
        //Get List of PricedCoverages
        final NodeList pricedCoverageList = vehAvailElement.getElementsByTagName("PricedCoverage");

        pricedCoverages = new ArrayList<>();
        for (int i = 0; pricedCoverageList.getLength() > i; i++) {
            final PricedCoverages fee = new PricedCoverages(pricedCoverageList.item(i));
            pricedCoverages.add(fee);
        }
    }

    private void setFee(Element vehAvailElement) {
        final NodeList feeNodeList = vehAvailElement.getElementsByTagName("Fee");
        fee = new Fee();
        for (int i = 0; feeNodeList.getLength() > i; i++) {
            if (feeNodeList.item(i).getAttributes().getNamedItem("Description").getTextContent().trim().contains("Location service charge(LSC)")) {
                fee.setLscFee(new BigDecimal(feeNodeList.item(i).getAttributes().getNamedItem("Amount").getTextContent().trim()));
                fee.setLscFeeCurrencyCode(feeNodeList.item(i).getAttributes().getNamedItem("CurrencyCode").getTextContent().trim());
                fee.setLscFeeDescription(feeNodeList.item(i).getAttributes().getNamedItem("Description").getTextContent().trim());
            } else if (feeNodeList.item(i).getAttributes().getNamedItem("Description").getTextContent().trim().contains("TAX(TAX)")) {
                fee.setTaxFee(new BigDecimal(feeNodeList.item(i).getAttributes().getNamedItem("Amount").getTextContent().trim()));
                fee.setTaxFeeCurrencyCode(feeNodeList.item(i).getAttributes().getNamedItem("CurrencyCode").getTextContent().trim());
                fee.setTaxFeeDescription(feeNodeList.item(i).getAttributes().getNamedItem("Description").getTextContent().trim());
            }
        }
    }

    private void setVehicleCharge(Element vehAvailElement) {
        final NodeList vehicleChargesNode = vehAvailElement.getElementsByTagName("VehicleCharge");

        for (int i = 0; vehicleChargesNode.getLength() > i; i++) {
            if ("original".equals(vehicleChargesNode.item(i).getAttributes().getNamedItem("Purpose").getTextContent().trim())) {
                originalCharge = new BigDecimal(vehicleChargesNode.item(i).getAttributes().getNamedItem("Amount").getTextContent().trim());
                originalChargeCurrencyCode = vehicleChargesNode.item(i).getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();
            }
            if ("preferred".equals(vehicleChargesNode.item(i).getAttributes().getNamedItem("Purpose").getTextContent().trim())) {
                preferredCharge = new BigDecimal(vehicleChargesNode.item(i).getAttributes().getNamedItem("Amount").getTextContent().trim());
                preferredChargeCurrencyCode = vehicleChargesNode.item(i).getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();
            }
            if ("baserate".equals(vehicleChargesNode.item(i).getAttributes().getNamedItem("Purpose").getTextContent().trim())) {
                baseRate = new BigDecimal(vehicleChargesNode.item(i).getAttributes().getNamedItem("Amount").getTextContent().trim());
            }
            //Get One-Way Fee
            if ("2".equals(vehicleChargesNode.item(i).getAttributes().getNamedItem("Purpose").getTextContent().trim())) {
                oneWayCharge = new BigDecimal(vehicleChargesNode.item(i).getAttributes().getNamedItem("Amount").getTextContent().trim());
                oneWayChargeCurrencyCode = vehicleChargesNode.item(i).getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();
            }
        }
    }

    //Define a class PricedCoverages to parse the info for every "PricedCoverage" node in VAR response
    public class PricedCoverages {
        //Get CoverageType
        public String coverageType;
        //Get Amount of Fee
        public BigDecimal amount;
        //Get CurrencyCode
        public String currencyCode;


        public PricedCoverages(Node pricedCoverage) {
            final Element pricedCoverageDoc = (Element) pricedCoverage;
            //Get CoverageType from Coverage node
            if (pricedCoverageDoc.getElementsByTagName("Coverage").item(0).getAttributes().getNamedItem("CoverageType") != null) {
                coverageType = pricedCoverageDoc.getElementsByTagName("Coverage").item(0).getAttributes().getNamedItem("CoverageType").getNodeValue();
            }
            // TODO: 12/27/2016 need check  agian
            //Get Amount from Charge node
            if (pricedCoverageDoc.getElementsByTagName("Charge").item(0).getAttributes().getNamedItem("Amount") != null) {
                amount = new BigDecimal(pricedCoverageDoc.getElementsByTagName("Charge").item(0).getAttributes().getNamedItem("Amount").getNodeValue());
            }

            //Get CurrencyCode from Charge node
            if (pricedCoverageDoc.getElementsByTagName("Charge").item(0).getAttributes().getNamedItem("CurrencyCode") != null) {
                currencyCode = pricedCoverageDoc.getElementsByTagName("Charge").item(0).getAttributes().getNamedItem("CurrencyCode").getNodeValue();
            }
        }
    }

}


