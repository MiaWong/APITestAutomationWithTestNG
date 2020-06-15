package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.locationsearch.utilities;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSLocationSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchResponseType;
import org.eclipse.jetty.client.HttpClient;

/**
 * Created by v-mechen on 9/28/2018.
 */
public class LocationSearchExecutionHelper {

    public static SCSLocationSearchRequestGenerator locationSearch(LocationSearchTestScenario scenarios, String tuid, HttpClient httpClient,
                                                                   String guid) throws DataAccessException {
        //Create test data bsed on parameters
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //Create location search request
        final SCSLocationSearchRequestGenerator scsLocationSearchRequestGenerator = new SCSLocationSearchRequestGenerator();
        //Send request and get response
        final CarSupplyConnectivityLocationSearchRequestType request = scsLocationSearchRequestGenerator.createLocationSearchRequest(testData);
        final SimpleE3FIHttpTransport<CarSupplyConnectivityLocationSearchRequestType, CarSupplyConnectivityLocationSearchResponseType, Object> transport4
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION, SettingsProvider.SERVICE_ADDRESS,
                30000, request, CarSupplyConnectivityLocationSearchResponseType.class);
        RequestSender.sendWithTransport(transport4, guid);
        final CarSupplyConnectivityLocationSearchResponseType response = transport4.getServiceRequestContext().getResponse();

        //Set response to SCSLocationSearchRequestGenerator
        scsLocationSearchRequestGenerator.setResponse(response);

        System.out.println("request xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        System.out.println("response xml==> " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));

        return scsLocationSearchRequestGenerator;

    }
}
