package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search;

//import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.TestData;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.DriverAgeVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.SearchResponsesNotEmptyVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.errorhandlingverification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.gdsmapverification.GDSMapVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.PropertyResetHelper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;

//import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;


/**
 * Created by jiyu on 10/20/16.
 */
public class SearchHelper
{
    private final static String MESSAGE_SEARCH_VERIFICATION_PROMPT = "Search Verification run: ";

    private SearchHelper() {}

    //------- Search ------------------
    //  serach basic verification : return new GUID for next if required, otherwise return null
    public static String searchVerification(SearchVerificationInput searchVerificationInput,
                                            SpooferTransport spooferTransport,
                                            TestScenario scenarios,
                                            String guid,
                                            Logger logger,
                                            boolean isrequiredsRetrieveRecord) throws Exception {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new SearchResponsesNotEmptyVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }



    //  search driver age verification
    public static String searchVerificationDriverAge(   SearchVerificationInput searchVerificationInput,
                                                        SpooferTransport spooferTransport,
                                                        TestScenario scenarios,
                                                        String guid,
                                                        Logger logger,
                                                        boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new DriverAgeVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //  search GDS map verification
    public static String searchGDSMapVerification(SearchVerificationInput searchVerificationInput,
                                                        SpooferTransport spooferTransport,
                                                        TestScenario scenarios,
                                                        String guid,
                                                        Logger logger) throws Exception
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        //System.out.println(PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new GDSMapVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String errorMapVerification(SearchVerificationInput searchVerificationInput,
                                                  SpooferTransport spooferTransport,
                                                  TestScenario scenarios,
                                                  String guid,
                                                  Logger logger) throws Exception
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        //System.out.println(PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new ErrorHandlingVerification().verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //  SCS search
/*  comment off uesless function for now, will be enabled once non SQL for BVT is supported
    public static SearchVerificationInput search(HttpClient httpClient,
                                                 TestScenario scenarios,
                                                 String tuid,
                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivitySearchRequestType searchRequest = SearchRequestGenerator.createSearchRequest(scenarios, tuid);
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
    }
*/
    //  SCS search with driver age
    public static SearchVerificationInput search(HttpClient httpClient,
                                                 TestScenario scenarios,
                                                 String tuid,
                                                 String guid,
                                                 long driverAge,
                                                 DataSource carsSCSDatasource) throws  Exception
    {

        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsSCSDatasource);
        CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);

       // CarSupplyConnectivitySearchRequestType searchRequest = SuiteContext.createSearchRequest(scenarios, tuid, guid);
        if (driverAge >= 0L) {
            searchRequest = PropertyResetHelper.setDriverAge(searchRequest, driverAge);
        }
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
    }

    //  SCS search with car vendor/car catalog etc parameters
    public static SearchVerificationInput search(HttpClient httpClient,
                                                 TestScenario scenarios,
                                                 String tuid,
                                                 String guid,
                                                 long driverAge,
                                                 long vendorSupplierID,
                                                 long carCategoryCode,
                                                 long carTypeCode,
                                                 long carTransmissionDriveCode,
                                                 long carFuelACCode,
                                                 DataSource carsInventoryDatasource) throws Exception
    {
/*
    //  CarSupplyConnectivitySearchRequestType searchRequest = SearchRequestGenerator.createSearchRequest(scenarios,tuid);

        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(carsInventoryDatasource);
        CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
*/
        CarSupplyConnectivitySearchRequestType searchRequest = SuiteContext.createSearchRequest(scenarios, tuid, guid);

        if (driverAge >= 0L) {
            searchRequest = PropertyResetHelper.setDriverAge(searchRequest, driverAge);
        }
        //  currently it fails with filter on Search level
        searchRequest =  PropertyResetHelper.setCarSearchFilter(searchRequest, vendorSupplierID, carCategoryCode, carTypeCode, carTransmissionDriveCode, carFuelACCode);
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, guid);
    }

    //  SCS search with car vendor/car catalog etc parameters
    public static SearchVerificationInput bvtSearch(HttpClient httpClient,
                                                 TestScenario scenarios,
                                                 String tuid) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final CarSupplyConnectivitySearchRequestType request = SearchRequestGenerator.createSearchRequest(scenarios,tuid);
        return TransportHelper.sendReceive(httpClient,
                SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION,
                request, PojoXmlUtil.getRandomGuid());

    }

}
