package com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.GDSPCarType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.ResultFilter;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetCostAndAvail extends SuiteCommon {
    final Logger logger = Logger.getLogger(getClass());
    final private DataSource carsInventoryDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    final private DataSource  titaniumDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_TITANIUMSCS_DATABASE_SERVER, SettingsProvider.DB_TITANIUMSCS_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_SCS_USER_NAME, SettingsProvider.DB_SCS_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInGetCostAndAvailability() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInGetCostAndAvailabilityVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2125248501" ,spooferTransport);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInGetCostAndAvailabilityWithCurrencyConvert() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);

        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        this.carRateDetailInGetCostAndAvailabilityVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.
                getTestScenario(), "2125248502", spooferTransport);
    }

    //    CASSS-2125    ConditionalCostList to be returned in xml format
    //    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
    private void carRateDetailInGetCostAndAvailabilityVerification(String guid, TestScenario scenarios, String tuid, SpooferTransport spooferTransport) throws Exception {
        final  TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), guid);
        //send search + getCostAndAvailability Request
        final CarbsRequestGenerator requestGernerator = ExecutionHelper.getCostAndAvailabilityByBusinessModelIDAndServiceProviderID
                (testData, spooferTransport, carsInventoryDatasource, logger);

        //GetCostAndAvailability Verification
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                new GetCostAndAvailabilityVerificationInput(requestGernerator.getGetCostAndAvailabilityRequestType(),
                        requestGernerator.getGetCostAndAvailabilityResponseType());
        VerificationHelper.carRateDetailInGetCostAndAvailabilityVerification(getCostAndAvailabilityVerificationInput,
                spooferTransport, carsInventoryDatasource, titaniumDatasource, scenarios, guid, true, logger);
    }

    //1077600 - Shopping without TPID in ASCS: ASCS Search/CostAvail/Details without TPID
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1077605ShoppingWithoutTPIDCarBS() throws IOException, DataAccessException
    {
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "1077605", PojoXmlUtil.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient)));
        shoppingWithoutTPID(testData);
    }

    public void shoppingWithoutTPID(TestData testData) throws IOException, DataAccessException
    {
        //search
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.getAuditLogTrackingData().setAuditLogTPID(null);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        final CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getDetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
         carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        getDetailsRequestType.getAuditLogTrackingData().setAuditLogTPID(null);
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);
        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);


        //cost&avail
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput(getCostAndAvailabilityRequestType, getCostAndAvailabilityResponseType);
        VerificationHelper.getCostAndAvailabilityBasicVerification(getCostAndAvailabilityVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);

    }


    //region User Story 1064876: MICKO - Consistent Pricing between Search and GetDetails
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1065153testCarBSCostAndAvailMICKO() throws IOException, DataAccessException
    {
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "1065153", PojoXmlUtil.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient)));

        testCarBSGetCostAndAvailMICKO(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs126812testCarBSCostAndAvailMICKO() throws IOException, DataAccessException
    {
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "1065153", PojoXmlUtil.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient)));

        testCarBSGetCostAndAvailMICKO(testData);
    }

    public void testCarBSGetCostAndAvailMICKO(TestData testData) throws IOException, DataAccessException
    {
        //search
        final ResultFilter resultFilter = new ResultFilter();
        resultFilter.setCarType(GDSPCarType.GDSPNetRate);
        testData.setResultFilter(resultFilter);
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.getAuditLogTrackingData().setAuditLogTPID(null);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        final CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);


        //cost&avail
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        getCostAndAvailabilityRequestType.setPurchaseTypeMask(null);
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput(getCostAndAvailabilityRequestType, getCostAndAvailabilityResponseType);
        VerificationHelper.getCostAndAvailabilityBasicVerification(getCostAndAvailabilityVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);
        final List remark = new ArrayList<>();
        TotalPriceVerifier.verifyTotalPriceEqual(carProductType, getCostAndAvailabilityResponseType.getCarProductList().getCarProduct().get(0), testData.getScenarios().getSupplierCurrencyCode(), remark, false);


        //getDetails
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        getDetailsRequestType.setPurchaseTypeMask(null);
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);
        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);
        TotalPriceVerifier.verifyTotalPriceEqual(carProductType, getDetailsResponseType.getCarProductList().getCarProduct().get(0), testData.getScenarios().getSupplierCurrencyCode(), remark, false);

        if (CollectionUtils.isNotEmpty(remark))
        {
            Assert.fail(remark.toString());
        }
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void  tfs181431CarBSPIIDNoCSARMessageSentWhenSendingSameCarBSGetCostAvail() throws IOException, DataAccessException
    {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final TestData testData = new TestData(httpClient, testScenario, "181431", PojoXmlUtil.getRandomGuid());

        testCarBSGetCostAvailNoSSRequestSent(testData);
    }

    @SuppressWarnings("PMD")
    public void testCarBSGetCostAvailNoSSRequestSent(TestData testData) throws IOException, DataAccessException
    {
        //search
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        final CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);


        //1.cost&avail
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType firstGetCostAndAvailabilityResponseType = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        final GetCostAndAvailabilityVerificationInput firstgetCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput(getCostAndAvailabilityRequestType, firstGetCostAndAvailabilityResponseType);
        VerificationHelper.getCostAndAvailabilityBasicVerification(firstgetCostAndAvailabilityVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);

        //2.cost&avail
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        testData.setGuid(PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        final CarECommerceGetCostAndAvailabilityResponseType secondGetCostAndAvailabilityResponseType = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        final GetCostAndAvailabilityVerificationInput secondGetCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput(getCostAndAvailabilityRequestType, secondGetCostAndAvailabilityResponseType);
        VerificationHelper.getCostAndAvailabilityBasicVerification(secondGetCostAndAvailabilityVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);

        try
        {
             Document spooferTransactions = spooferTransport.retrieveRecords(testData.getGuid());
        }
        catch (Exception e)
        {
            //if not send CSAR down , there is also not GDS request send down, retrieveRecords method will get Exception
            return;
        }
        //if get the spooferTransactions, will assert fail.
       Assert.fail("CSAR message should not be sent out when sending the CarBS GetDetails again");

    }

}
