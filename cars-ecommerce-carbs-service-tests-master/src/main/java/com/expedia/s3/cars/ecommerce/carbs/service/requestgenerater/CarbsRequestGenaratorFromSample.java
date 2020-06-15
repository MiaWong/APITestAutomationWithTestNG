package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;

import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by fehu on 3/15/2017.
 */
public class CarbsRequestGenaratorFromSample {

    private CarbsRequestGenaratorFromSample() {
    }

    public static CarECommerceGetDetailsRequestType createCarbsGetDetailsRequest() throws ParserConfigurationException, IOException, SAXException {

        final ClassLoader classLoader = CarbsRequestGenaratorFromSample.class.getClassLoader();
        final File  file = new File( classLoader.getResource("sampleGetDetail.xml").getPath());
        final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document xml = dBuilder.parse(file);
        return PojoXmlUtil.docToPojo(xml, CarECommerceGetDetailsRequestType.class);

    }



    public static CarLocationIataSearchRequest createCarbsLocationSearchRequest(String testCaseName) throws ParserConfigurationException, IOException, SAXException {
        File file = null;
        final ClassLoader classLoader = CarbsRequestGenaratorFromSample.class.getClassLoader();
        if ("CarBS_Location_LatLong_DELT_COLT_OHRT".equalsIgnoreCase(testCaseName)) {
            file = new File( classLoader.getResource("sampleCarbsLocationLatlongSearch.xml").getPath());
        }
       else if ("CarBS_Location_IATA_Airport_DELT_COLT_OHRT".equalsIgnoreCase(testCaseName))
       {
           file = new File( classLoader.getResource("sampleCarbsLocationIataSearch.xml").getPath());
       }
        final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document xml = dBuilder.parse(file);
        return PojoXmlUtil.docToPojo(xml, CarLocationIataSearchRequest.class);

    }

    @SuppressWarnings("PMD")
    public static CarECommerceSearchRequestType createCarbsSearchRequest(TestScenario scenarios, String tuid) {
        try {
            final String jurisdictionCountryCode = scenarios.getJurisdictionCountryCode();
            final  String companyCode = scenarios.getCompanyCode();
            final String managementUnitCode = scenarios.getManagementUnitCode();
            final String supplierCurrencyCode = scenarios.getSupplierCurrencyCode();
            final  String pickupLocationCode = scenarios.getPickupLocationCode();
            final String dropOffLocationCode = scenarios.getDropOffLocationCode();

            final ClassLoader classLoader = CarbsRequestGenaratorFromSample.class.getClassLoader();
            final File  file = new File( classLoader.getResource("sampleCarbsSearch.xml").getPath());
            final DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final  Document xml = dBuilder.parse(file);
            final XPath xpath = XPathFactory.newInstance().newXPath();
            final Node traveller_tuid = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/AuditLogTrackingData/TravelerUserKey/UserID")
                    .evaluate(xml, XPathConstants.NODE);

            traveller_tuid.setTextContent(tuid);
            final Node logon_tuid = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/AuditLogTrackingData/LogonUserKey/UserID")
                    .evaluate(xml, XPathConstants.NODE);
            logon_tuid.setTextContent(tuid);

            final  Node JurisdictionCountryCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/SiteMessageInfo/PointOfSaleKey/JurisdictionCountryCode")
                    .evaluate(xml, XPathConstants.NODE);
            JurisdictionCountryCode.setTextContent(jurisdictionCountryCode);

            final Node CompanyCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/SiteMessageInfo/PointOfSaleKey/CompanyCode")
                    .evaluate(xml, XPathConstants.NODE);
            CompanyCode.setTextContent(companyCode);

            final Node ManagementUnitCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/SiteMessageInfo/PointOfSaleKey/ManagementUnitCode")
                    .evaluate(xml, XPathConstants.NODE);
            ManagementUnitCode.setTextContent(managementUnitCode);

            final Node PickupLocationCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CarTransportationSegment/StartCarLocationKey/LocationCode")
                    .evaluate(xml, XPathConstants.NODE);
            PickupLocationCode.setTextContent(pickupLocationCode);

            final Node DropOffLocationCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CarTransportationSegment/EndCarLocationKey/LocationCode")
                    .evaluate(xml, XPathConstants.NODE);
            DropOffLocationCode.setTextContent(dropOffLocationCode);

            //currency code
            final Node currencyCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CurrencyCode")
                    .evaluate(xml, XPathConstants.NODE);
            currencyCode.setTextContent(supplierCurrencyCode);

            //startDatetime and endDateTime
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 180);
            final  Node startDatetimerangeMin = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CarTransportationSegment/SegmentDateTimeRange/StartDateTimeRange/MinDateTime").evaluate(xml, XPathConstants.NODE);
            startDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());
            final  Node startDatetimerangeMax = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CarTransportationSegment/SegmentDateTimeRange/StartDateTimeRange/MaxDateTime").evaluate(xml, XPathConstants.NODE);
            startDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());

            cal.add(Calendar.DATE, 5);
            final Node endDatetimerangeMin = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CarTransportationSegment/SegmentDateTimeRange/EndDateTimeRange/MinDateTime").evaluate(xml, XPathConstants.NODE);
            endDatetimerangeMin.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());
            final Node endDatetimerangeMax = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/CarECommerceSearchCriteriaList/CarECommerceSearchCriteria/" +
                            "CarTransportationSegment/SegmentDateTimeRange/EndDateTimeRange/MaxDateTime").evaluate(xml, XPathConstants.NODE);
            endDatetimerangeMax.setTextContent(DateTime.getInstanceByDateTime(cal.getTime()).toString());


            //Laguage
            final Node LanguageCode = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/SiteMessageInfo/Language/" +
                            "LanguageCode").evaluate(xml, XPathConstants.NODE);

            final  Node CountryAlpha2Code = (Node) xpath.compile
                    ("//CarECommerceSearchRequest/SiteMessageInfo/Language/" +
                            "CountryAlpha2Code").evaluate(xml, XPathConstants.NODE);

            if ("USA".equals(jurisdictionCountryCode))
            {
                LanguageCode.setTextContent("en");
                CountryAlpha2Code.setTextContent("US");

            }
             else if ("GBR".equals(jurisdictionCountryCode))
            {
                LanguageCode.setTextContent("en");
                CountryAlpha2Code.setTextContent("GB");
            }
            else if ("FRA".equals(jurisdictionCountryCode))
            {
                LanguageCode.setTextContent("fr");
                CountryAlpha2Code.setTextContent("FR");
            }
            else if("CAN".equals(jurisdictionCountryCode))
            {
                LanguageCode.setTextContent("en");
                CountryAlpha2Code.setTextContent("CA");
            }

            return  PojoXmlUtil.docToPojo(xml, CarECommerceSearchRequestType.class);
            //return Utils.toString(xml);
        } catch (Exception e) {
            System.out.print("Exception occured: " + e.getMessage());
        }
        return null;
    }

    public static CarECommerceGetDetailsRequestType createCarbsGetDetailsRequest(TestScenario scenarios, String tuid) {


        return null;
    }
}