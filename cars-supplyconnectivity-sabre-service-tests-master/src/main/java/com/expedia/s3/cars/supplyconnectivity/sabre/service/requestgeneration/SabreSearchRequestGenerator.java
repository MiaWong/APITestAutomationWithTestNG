package com.expedia.s3.cars.supplyconnectivity.sabre.service.requestgeneration;

import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.EventLogBaseID;
import com.expedia.s3.cars.framework.core.logging.LogHelper;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Created by srajendran on 11-10-2018.
 */
public class SabreSearchRequestGenerator
{

    private static final String SAMPLE_SEARCH_XML = "/SabreOnAirportSearch.xml";
    private static final String SAMPLE_OFF_AIRPORT_SEARCH_XML = "/SabreOffAirportSearch.xml";
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

    private static final Logger LOGGER =Logger.getLogger(SabreSearchRequestGenerator.class);

    private SabreSearchRequestGenerator(){}

    public static CarSupplyConnectivitySearchRequestType createSearchRequest(TestScenario scenarios, String tuid)
    {
        try {

            final String jurisdictionCountryCode = scenarios.getJurisdictionCountryCode();
            final String companyCode = scenarios.getCompanyCode();
            final String managementUnitCode = scenarios.getManagementUnitCode();
            final String pickupLocationCode = scenarios.getPickupLocationCode();
            final String dropOffLocationCode = scenarios.getDropOffLocationCode();
            final boolean isOnAirPort = scenarios.isOnAirPort();

            final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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

            final XPath xpath = XPathFactory.newInstance().newXPath();
            final Node travellerTuid = (Node) xpath.compile
                    (XPATH_TUID).evaluate(xml, XPathConstants.NODE);

            travellerTuid.setTextContent(tuid);
            final Node logonTuid = (Node) xpath.compile
                    (XPATH_LOGON_USERID).evaluate(xml, XPathConstants.NODE);
            logonTuid.setTextContent(tuid);

            final Node jurisdictionCountryCodeNode = (Node) xpath.compile
                    (XPATH_JURISDICTION).evaluate(xml, XPathConstants.NODE);
            jurisdictionCountryCodeNode.setTextContent(jurisdictionCountryCode);

            final Node companyCodeNode = (Node) xpath.compile
                    (XPATH_COMPANYCODE).evaluate(xml, XPathConstants.NODE);
            companyCodeNode.setTextContent(companyCode);

            final Node managementUnitCodeNode = (Node) xpath.compile
                    (XPATH_MANAGEMENTUNITCODE).evaluate(xml, XPathConstants.NODE);
            managementUnitCodeNode.setTextContent(managementUnitCode);

            final Node pickupLocationCodeNode = (Node) xpath.compile
                    (XPATH_STARTLOCATIONCODE).evaluate(xml, XPathConstants.NODE);
            pickupLocationCodeNode.setTextContent(pickupLocationCode);

            final Node dropOffLocationCodeNode = (Node) xpath.compile
                    (XPATH_ENDLOCATIONCODE).evaluate(xml, XPathConstants.NODE);
            dropOffLocationCodeNode.setTextContent(dropOffLocationCode);


            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 180);

            final NodeList startDatetimerangeMinNodes = (NodeList) xpath.evaluate(XPATH_STARTDATETIMERANGE_MIN,xml,XPathConstants.NODESET);
            for(int i = 0; i < startDatetimerangeMinNodes.getLength(); i++) {
                final Node startDatetimerangeMin = startDatetimerangeMinNodes.item(i);
                startDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(calendar.getTime()).toString());
            }

            final NodeList startDatetimerangeMaxNodes = (NodeList) xpath.evaluate(XPATH_STARTDATETIMERANGE_MAX,xml,XPathConstants.NODESET);
            for(int i = 0; i < startDatetimerangeMaxNodes.getLength(); i++) {
                final Node startDatetimerangeMax = startDatetimerangeMaxNodes.item(i);
                startDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(calendar.getTime()).toString());
            }

            calendar.add(Calendar.DATE, 5);
            final NodeList endDatetimerangeMinNodes = (NodeList) xpath.evaluate(XPATH_ENDDATETIMERANGE_MIN,xml,XPathConstants.NODESET);
            for(int i = 0; i < endDatetimerangeMinNodes.getLength(); i++) {
                final Node endDatetimerangeMin = endDatetimerangeMinNodes.item(i);
                endDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(calendar.getTime()).toString());
            }

            final NodeList endDatetimerangeMaxNodes = (NodeList) xpath.evaluate(XPATH_ENDDATETIMERANGE_MAX,xml,XPathConstants.NODESET);
            for(int i = 0; i < endDatetimerangeMaxNodes.getLength(); i++) {
                final Node endDatetimerangeMax = endDatetimerangeMaxNodes.item(i);
                endDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(calendar.getTime()).toString());
            }
            return PojoXmlUtil.docToPojo(xml, CarSupplyConnectivitySearchRequestType.class);
        }
        catch (Exception e)
        {
            LogHelper.log(LOGGER, Level.ERROR, EventLogBaseID.ERROR+1, "Exception occured: "+e.getMessage());
        }
        return null;
    }
}
