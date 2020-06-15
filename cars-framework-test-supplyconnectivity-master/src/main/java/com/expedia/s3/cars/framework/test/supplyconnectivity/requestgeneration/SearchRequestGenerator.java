package com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration;

import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.Calendar;

/**
 * Created by mpaudel on 5/18/16.
 */
@SuppressWarnings("PMD")
public class SearchRequestGenerator {

    private static final String SAMPLE_SEARCH_XML = "/SampleSearch.xml";
    private static final String SAMPLE_OFF_AIRPORT_SEARCH_XML = "/SampleOffAirportSearch.xml";
    private static final String XPATH_TUID = "//CarSupplyConnectivitySearchRequest/AuditLogTrackingData/TravelerUserKey/UserID";
    private static final String XPATH_LOGON_USERID = "//CarSupplyConnectivitySearchRequest/AuditLogTrackingData/LogonUserKey/UserID";
    private static final String XPATH_JURISDICTION = "//CarSupplyConnectivitySearchRequest/PointOfSaleKey/JurisdictionCountryCode";

    private static final String XPATH_COMPANYCODE = "//CarSupplyConnectivitySearchRequest/PointOfSaleKey/CompanyCode";
    private static final String XPATH_MANAGEMENTUNITCODE = "//CarSupplyConnectivitySearchRequest/PointOfSaleKey/ManagementUnitCode";

    private static final String XPATH_STARTLOCATIONCODE = "//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/" +
            "CarSearchCriteria/CarTransportationSegment/StartCarLocationKey/LocationCode";
    private static final String XPATH_ENDLOCATIONCODE = "//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/" +
            "CarSearchCriteria/CarTransportationSegment/EndCarLocationKey/LocationCode";

    //start date time
    private static final String XPATH_STARTDATETIMERANGE_MIN = "//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/" +
            "CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/StartDateTimeRange/MinDateTime";
    private static final String XPATH_STARTDATETIMERANGE_MAX = "//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/" +
            "CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/StartDateTimeRange/MaxDateTime";

    //end date time
    private static final String XPATH_ENDDATETIMERANGE_MIN = "//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/" +
            "CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/EndDateTimeRange/MinDateTime";
    private static final String XPATH_ENDDATETIMERANGE_MAX = "//CarSupplyConnectivitySearchRequest/CarSearchCriteriaList/" +
            "CarSearchCriteria/CarTransportationSegment/SegmentDateTimeRange/EndDateTimeRange/MaxDateTime";

    public static CarSupplyConnectivitySearchRequestType createSearchRequest(TestScenario scenarios, String tuid)
    {
        try {

            String jurisdictionCountryCode =scenarios.getJurisdictionCountryCode();
            String companyCode=scenarios.getCompanyCode();
            String managementUnitCode = scenarios.getManagementUnitCode();
            String pickupLocationCode=scenarios.getPickupLocationCode();
            String dropOffLocationCode=scenarios.getDropOffLocationCode();
            boolean isOnAirPort=scenarios.isOnAirPort();
            int purchaseTypeMask= scenarios.getPurchaseType().getPurchaseTypeMask();

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document xml = null;
            if (isOnAirPort)
            {
                xml = dBuilder.parse(SearchRequestGenerator.class.getResourceAsStream(SAMPLE_SEARCH_XML));
            }
            else
            {
                xml = dBuilder.parse(SearchRequestGenerator.class.getResourceAsStream(SAMPLE_OFF_AIRPORT_SEARCH_XML));
            }

            //TODO: don't use xpaths... just unmarshal and use the pojo to set stuff

            XPath xpath = XPathFactory.newInstance().newXPath();
            Node traveller_tuid = (Node) xpath.compile
                    (XPATH_TUID).evaluate(xml, XPathConstants.NODE);

            traveller_tuid.setTextContent(tuid);
            Node logon_tuid = (Node) xpath.compile
                    (XPATH_LOGON_USERID).evaluate(xml, XPathConstants.NODE);
            logon_tuid.setTextContent(tuid);

            Node JurisdictionCountryCode = (Node) xpath.compile
                    (XPATH_JURISDICTION).evaluate(xml, XPathConstants.NODE);
            JurisdictionCountryCode.setTextContent(jurisdictionCountryCode);

            Node CompanyCode = (Node) xpath.compile
                    (XPATH_COMPANYCODE).evaluate(xml, XPathConstants.NODE);
            CompanyCode.setTextContent(companyCode);

            Node ManagementUnitCode = (Node) xpath.compile
                    (XPATH_MANAGEMENTUNITCODE).evaluate(xml, XPathConstants.NODE);
            ManagementUnitCode.setTextContent(managementUnitCode);

            Node PickupLocationCode = (Node) xpath.compile
                    (XPATH_STARTLOCATIONCODE).evaluate(xml, XPathConstants.NODE);
            PickupLocationCode.setTextContent(pickupLocationCode);

            Node DropOffLocationCode = (Node) xpath.compile
                    (XPATH_ENDLOCATIONCODE).evaluate(xml, XPathConstants.NODE);
            DropOffLocationCode.setTextContent(dropOffLocationCode);


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 180);
            Node startDatetimerangeMin = (Node) xpath.compile
                    (XPATH_STARTDATETIMERANGE_MIN).evaluate(xml, XPathConstants.NODE);
            startDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());
            Node startDatetimerangeMax = (Node) xpath.compile
                    (XPATH_STARTDATETIMERANGE_MAX).evaluate(xml, XPathConstants.NODE);
            startDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());

            cal.add(Calendar.DATE, 5);
            Node endDatetimerangeMin = (Node) xpath.compile
                    (XPATH_ENDDATETIMERANGE_MIN).evaluate(xml, XPathConstants.NODE);
            endDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());
            Node endDatetimerangeMax = (Node) xpath.compile
                    (XPATH_ENDDATETIMERANGE_MAX).evaluate(xml, XPathConstants.NODE);
            endDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());

            CarSupplyConnectivitySearchRequestType requestType= PojoXmlUtil.docToPojo(xml, CarSupplyConnectivitySearchRequestType.class);
            return requestType;
        }
        catch (Exception e)
        {
            System.out.print("Exception occured: "+e.getMessage());
        }
        return null;
    }
}
