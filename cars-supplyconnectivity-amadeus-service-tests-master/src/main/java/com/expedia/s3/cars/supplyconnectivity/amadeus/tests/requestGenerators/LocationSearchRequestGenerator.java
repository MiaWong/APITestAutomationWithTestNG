package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchRequestType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * Created by alibadisos on 10/7/16.
 */
public class LocationSearchRequestGenerator {
    public CarSupplyConnectivityLocationSearchRequestType createLocationSearchRequest(TestScenarios scenarios, String tuid, Long numberOfItems) {
        return createLocationSearchRequest(scenarios, tuid, numberOfItems, null, null, 0, null);
    }

    public CarSupplyConnectivityLocationSearchRequestType createLocationLatLongSearchRequest(TestScenarios scenarios,
                                                                                             String tuid, Double latitude, Double longitude, int radius, String distanceUnit) {
        return createLocationSearchRequest(scenarios, tuid, null, latitude, longitude, radius, distanceUnit);
    }

    public CarSupplyConnectivityLocationSearchRequestType createLocationSearchRequest(
            TestScenarios scenarios, String tuid, Long numberOfItems, Double latitude, Double longitude, int radius, String distanceUnit) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {

            String jurisdictionCountryCode = scenarios.getJurisdictionCountryCode();
            String companyCode = scenarios.getCompanyCode();
            String managementUnitCode = scenarios.getManagementUnitCode();
            String pickupLocationCode = scenarios.getPickupLocationCode();
            int supplierID = scenarios.getSupplierID();

            InputStream is = classLoader.getResourceAsStream("SampleLocationLatLongSearch.xml");
            if (null == latitude && null == longitude) {
                is = classLoader.getResourceAsStream("SampleLocationSearch.xml");
            }
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml = dBuilder.parse(is);

            XPath xpath = XPathFactory.newInstance().newXPath();
            Node traveller_tuid = (Node) xpath.compile
                    ("//CarSupplyConnectivityLocationSearchRequest/AuditLogTrackingData/TravelerUserKey/UserID")
                    .evaluate(xml, XPathConstants.NODE);

            //Node current_tuid = (Node) xpath.evaluate
            //       ("/CarSupplyConnectivitySearchRequest/AuditLogTrackingData/TravelerUserKey/UserID",
            // xml, XPathConstants.NODE);
            traveller_tuid.setTextContent(tuid);
            Node logon_tuid = (Node) xpath.compile
                    ("//CarSupplyConnectivityLocationSearchRequest/AuditLogTrackingData/LogonUserKey/UserID")
                    .evaluate(xml, XPathConstants.NODE);
            logon_tuid.setTextContent(tuid);

            Node JurisdictionCountryCode = (Node) xpath.compile
                    ("//CarSupplyConnectivityLocationSearchRequest/PointOfSaleKey/JurisdictionCountryCode")
                    .evaluate(xml, XPathConstants.NODE);
            JurisdictionCountryCode.setTextContent(jurisdictionCountryCode);

            Node CompanyCode = (Node) xpath.compile
                    ("//CarSupplyConnectivityLocationSearchRequest/PointOfSaleKey/CompanyCode")
                    .evaluate(xml, XPathConstants.NODE);
            CompanyCode.setTextContent(companyCode);

            Node ManagementUnitCode = (Node) xpath.compile
                    ("//CarSupplyConnectivityLocationSearchRequest/PointOfSaleKey/ManagementUnitCode")
                    .evaluate(xml, XPathConstants.NODE);
            ManagementUnitCode.setTextContent(managementUnitCode);

            if (null == latitude && null == longitude) {
                if (null != pickupLocationCode) {
                    Node IataCode = (Node) xpath.compile
                            ("//CarSupplyConnectivityLocationSearchRequest/SearchCriteria/Iata").evaluate(xml, XPathConstants.NODE);
                    IataCode.setTextContent(pickupLocationCode);
                }
            }

            else {
                if (null != latitude) {
                    Node latitudeValueNode = (Node) xpath.compile
                            ("//CarSupplyConnectivityLocationSearchRequest/SearchCriteria/Latitude").evaluate(xml, XPathConstants.NODE);

                    buildValueAndPlaceCountNode(latitude, latitudeValueNode);
                }

                if (null != longitude) {
                    Node longitudeValueNode = (Node) xpath.compile
                            ("//CarSupplyConnectivityLocationSearchRequest/SearchCriteria/Longitude").evaluate(xml, XPathConstants.NODE);

                    buildValueAndPlaceCountNode(longitude, longitudeValueNode);
                }

                if (radius > 0 && null != distanceUnit) {
                    Node radiusNode = (Node) xpath.compile
                            ("//CarSupplyConnectivityLocationSearchRequest/SearchCriteria/Radius").evaluate(xml, XPathConstants.NODE);

                    radiusNode.getAttributes().getNamedItem("p5:DistanceUnitCount").setTextContent(String.valueOf(radius));
                    radiusNode.getAttributes().getNamedItem("p5:DistanceUnit").setTextContent(distanceUnit);
                }
            }

            CarSupplyConnectivityLocationSearchRequestType requestType = PojoXmlUtil.docToPojo(xml, CarSupplyConnectivityLocationSearchRequestType.class);

            if (latitude==null && longitude == null && supplierID > 0) {
                requestType.getSearchCriteria().setSupplierID(Long.valueOf(supplierID));
            }

            if (null == pickupLocationCode) {
                requestType.getSearchCriteria().setIata(null);
            }

            if (null != numberOfItems) {
                requestType.setNumberOfItems(numberOfItems);
                requestType.setLastItemRetrieved(5L);
            }
            return requestType;
        } catch (Exception e) {
            System.out.print("Exception occured: " + e.getMessage());
        }

        return null;
    }

    private void buildValueAndPlaceCountNode(Double latLong, Node valueNode) {
        if (valueNode != null) {
            final int len = latLong.toString().length() - latLong.toString().indexOf('.') - 1;

            valueNode.getAttributes().getNamedItem("p5:DecimalPlaceCount").setTextContent(String.valueOf(len));
            String latLongvalue = String.valueOf(new Double(latLong * Math.pow(10, len)).intValue());

            valueNode.getAttributes().getNamedItem("p5:Value").setTextContent(latLongvalue);
        }
    }
}