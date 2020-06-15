package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getcostandavail;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail.CostAndAvailVerification;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;

/**
 * Created by yyang4 on 11/3/16.
 */
public class LocationID extends SuiteCommon{

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000301WithOutLactionIdTest() throws Exception {
        final String tuid = "3000301";
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

        //2. Generate GetCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest  = requestGenerator3.createCostAndAvailRequest();
        final CarProductType productTypeCost = costAndAvailRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        startCarLocationKeyTypeCost.setCarVendorLocationID(null);
        final CarLocationKeyType endCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        endCarLocationKeyTypeCost.setCarVendorLocationID(null);
        final GetCostAndAvailabilityVerificationInput verificationInput = ExecutionHelper.getCostAndAvail(httpClient,requestGenerator3,randomGuid);
        logger.info("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        logger.info("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
        logger.info("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getCostAndAvailCarRateDetailVerification(verificationInput,spooferTransport,testScenario,randomGuid,logger);
        CostAndAvailVerification.verifyCarLocationInfo(basicVerificationContext,verificationInput.getResponse(),scsDataSource);

    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000302WithLactionIdTest() throws Exception {
        final String tuid = "3000302";
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


        //2. Generate GetCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest  = requestGenerator3.createCostAndAvailRequest();
        final CarProductType productTypeCost = costAndAvailRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        startCarLocationKeyTypeCost.setLocationCode("XXX");
        startCarLocationKeyTypeCost.setCarLocationCategoryCode("C");
        startCarLocationKeyTypeCost.setSupplierRawText("888");
        final CarLocationKeyType endCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        endCarLocationKeyTypeCost.setLocationCode("XXX");
        endCarLocationKeyTypeCost.setCarLocationCategoryCode("C");
        endCarLocationKeyTypeCost.setSupplierRawText("888");
        final GetCostAndAvailabilityVerificationInput verificationInput = ExecutionHelper.getCostAndAvail(httpClient,requestGenerator3,randomGuid);
        logger.info("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        logger.info("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
        logger.info("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getCostAndAvailCarRateDetailVerification(verificationInput,spooferTransport,testScenario,randomGuid,logger);
        CostAndAvailVerification.verifyCarLocationInfo(basicVerificationContext,verificationInput.getResponse(),scsDataSource);

    }


}
