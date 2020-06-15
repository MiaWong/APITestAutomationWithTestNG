package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * Created by mpaudel on 5/18/16.
 */
public class SearchRequestGenerator {

    public static CarSupplyConnectivitySearchRequestType createSearchRequest(TestScenarios scenarios, String tuid)
    {
        try {

            String jurisdictionCountryCode =scenarios.getJurisdictionCountryCode();
            String companyCode=scenarios.getCompanyCode();
            String managementUnitCode = scenarios.getManagementUnitCode();
            String pickupLocationCode=scenarios.getPickupLocationCode();
            String dropOffLocationCode=scenarios.getDropOffLocationCode();

            File file = new File("SampleSearch.xml");
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml = dBuilder.parse(file);
            XPath xpath = XPathFactory.newInstance().newXPath();
            Node traveller_tuid = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/AuditLogTrackingData/TravelerUserKey/UserID")
                    .evaluate(xml, XPathConstants.NODE);

            //Node current_tuid = (Node) xpath.evaluate
            //       ("/CarSupplyConnectivitySearchRequest/AuditLogTrackingData/TravelerUserKey/UserID",
            // xml, XPathConstants.NODE);
            traveller_tuid.setTextContent(tuid);
            Node logon_tuid = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/AuditLogTrackingData/LogonUserKey/UserID")
                    .evaluate(xml, XPathConstants.NODE);
            logon_tuid.setTextContent(tuid);

            Node JurisdictionCountryCode = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/PointOfSaleKey/JurisdictionCountryCode")
                    .evaluate(xml, XPathConstants.NODE);
            JurisdictionCountryCode.setTextContent(jurisdictionCountryCode);

            Node CompanyCode = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/PointOfSaleKey/CompanyCode")
                    .evaluate(xml, XPathConstants.NODE);
            CompanyCode.setTextContent(companyCode);

            Node ManagementUnitCode = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/PointOfSaleKey/ManagementUnitCode")
                    .evaluate(xml, XPathConstants.NODE);
            ManagementUnitCode.setTextContent(managementUnitCode);

            Node PickupLocationCode = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/CarSearchCriteria/" +
                            "CarTransportationSegment/StartCarLocationKey/LocationCode")
                    .evaluate(xml, XPathConstants.NODE);
            PickupLocationCode.setTextContent(pickupLocationCode);

            Node DropOffLocationCode = (Node) xpath.compile
                    ("//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/CarSearchCriteria/" +
                            "CarTransportationSegment/EndCarLocationKey/LocationCode")
                    .evaluate(xml, XPathConstants.NODE);
            DropOffLocationCode.setTextContent(dropOffLocationCode);
            CarSupplyConnectivitySearchRequestType requestType= PojoXmlUtil.docToPojo(xml, CarSupplyConnectivitySearchRequestType.class);
            return requestType;
            //return Utils.toString(xml);
        }
        catch (Exception e)
        {
            System.out.print("Exception occured: "+e.getMessage());
        }
        return null;
    }
}
