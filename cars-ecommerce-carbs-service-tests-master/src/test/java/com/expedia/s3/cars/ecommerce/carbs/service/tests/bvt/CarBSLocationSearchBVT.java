package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.locationsearch.CarBSLocationSearchVerify;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchRequest;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by fehu on 3/21/2017.
 */
public class CarBSLocationSearchBVT extends SuiteCommon {

    //TFS_238769_Egencia location search - POR LatLong - Verify locations returned in response correctly for Lat/long/Radius and DEL-T/COL-T/OHR-T
    @Test(groups = {"bvt"})
    public void casss238769CarBSLocationLatLongDELTCOLTOHRT() throws ParserConfigurationException, SAXException, IOException {
        testCarBSLocationSearch("CarBS_Location_LatLong_DELT_COLT_OHRT", 238769);
    }

    //TFS_238763_Egencia location search - IATA Airport - Verify locations returned in response correctly for DEL-T/COL-T/OHR-T
    @Test(groups = {"bvt"})
    public void casss238763CarBSLocationIATAAirportDELTCOLTOHRT() throws ParserConfigurationException, SAXException, IOException {
        testCarBSLocationSearch("CarBS_Location_IATA_Airport_DELT_COLT_OHRT", 238763);
    }


    ///Basic location search method for CarBS
    public void testCarBSLocationSearch(String testCaseName, int tuid) throws IOException, SAXException, ParserConfigurationException {
        final CarLocationIataSearchRequest request = CarbsRequestGenaratorFromSample.createCarbsLocationSearchRequest(testCaseName);
        final  CarLocationIataSearchResponse response = CarbsRequestSender.getCarbsLocationSearchResponse(PojoXmlUtil.getRandomGuid(), httpClient, request);
        //verification
        CarBSLocationSearchVerify.verifyBSLocationSearchResponse(response);
    }
}
