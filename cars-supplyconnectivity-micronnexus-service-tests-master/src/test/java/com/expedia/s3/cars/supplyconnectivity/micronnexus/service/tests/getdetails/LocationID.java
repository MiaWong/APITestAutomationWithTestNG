package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getdetails;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail.GetDetailsVerification;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by yyang4 on 11/3/16.
 */
public class LocationID extends SuiteCommon{

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000201WithOutLactionIdTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        final String tuid = "3000201";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource scsDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,scsDataSource);

        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        logger.info("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        logger.info("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        final CarProductType productType = detailsRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        startCarLocationKeyType.setCarVendorLocationID(null);
        final CarLocationKeyType endCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        endCarLocationKeyType.setCarVendorLocationID(null);
        final GetDetailsVerificationInput getDetailsVerificationInput =ExecutionHelper.getDetails(httpClient,requestGenerator,randomGuid);
        logger.info("detail request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        logger.info("detail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
        logger.info("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput,spooferTransport,testScenario,randomGuid,logger);
        GetDetailsVerification.verifyCarLocationInfo(basicVerificationContext,getDetailsVerificationInput.getResponse(),scsDataSource);
    }

    @Test(groups= {"regression"})
    public void cass3000202WithLactionIdTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        final String tuid = "3000202";
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource scsDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,scsDataSource);
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        logger.info("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        logger.info("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        final CarProductType productType = detailsRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        startCarLocationKeyType.setLocationCode("XXX");
        startCarLocationKeyType.setCarLocationCategoryCode("C");
        startCarLocationKeyType.setSupplierRawText("888");
        final CarLocationKeyType endCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        endCarLocationKeyType.setLocationCode("XXX");
        endCarLocationKeyType.setCarLocationCategoryCode("C");
        endCarLocationKeyType.setSupplierRawText("888");
        final GetDetailsVerificationInput getDetailsVerificationInput =ExecutionHelper.getDetails(httpClient,requestGenerator,randomGuid);
        System.out.println("detail request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        System.out.println("detail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput,spooferTransport,testScenario,randomGuid,logger);
        GetDetailsVerification.verifyCarLocationInfo(basicVerificationContext,getDetailsVerificationInput.getResponse(),scsDataSource);

    }

}
