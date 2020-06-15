package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 2/12/2017.
 */
public class SearchExecutionHelper {
    //SCS search with specific handler
    public static SearchVerificationInput search(TestData parameters, DataSource scsDataSource) throws
            IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(scsDataSource);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(parameters);

        return TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION,
                searchRequest, parameters.getGuid());
    }
}