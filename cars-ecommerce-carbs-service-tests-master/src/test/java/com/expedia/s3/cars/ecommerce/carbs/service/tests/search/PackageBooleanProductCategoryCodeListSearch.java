package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.basetypes.defn.v4.ProductCategoryCodeListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by yyang4 on 6/27/2018.
 */
public class PackageBooleanProductCategoryCodeListSearch extends SuiteCommon
{

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "621741"})
    public void tfs621741PackageBooleanProductCatalogListHC() throws Exception
    {
        doTest(CommonScenarios.MN_GBR_Package_RoundTrip_OnAirport_LHR.getTestScenario(), "621741",
    "5", true, false, "Hotel,Car");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "621752"})
    public void tfs621752PackageBooleanProductCatalogListTHC() throws Exception
    {
        doTest(CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario(), "621752",
                "5", true, false, "Train,Hotel,Car");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "694063"})
    public void tfs694063PackageBooleanProductCatalogListFCBundle() throws Exception
    {
        doTest(CommonScenarios.Worldspan_US_Agency_Bundle_OnAirport.getTestScenario(), "694063",
                "5", false, false, "Air,Car");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "1058595"})
    public void tfs1058595PackageBooleanProductCatalogListMip() throws Exception
    {
        doTest(CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario(), "1058595",
                "5", false, true, "Air,Car");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "621727"})
    public void tfs621727NoProductCatalogList() throws Exception
    {
        doTest(CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario(), "621727",
                "5", false, true, null);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "621728"})
    public void tfs621728NoPackageBoolean() throws Exception
    {
        doTest(CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario(), "621728",
                "5", null, null, "Air,Car");
    }

    public void doTest(TestScenario scenarios, String tuid, String clientID, Boolean packageBoolean, Boolean postPurchaseBoolean, String productCategoryCodes) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        //create search request
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        //Send request with purchase type
        final CarECommerceSearchResponseType responseWithPurchaseType = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        //Override package boolean/postpurchase boolean and productCategoryCodeList
        request.getCarSearchStrategy().setPackageBoolean(null);
        request.getCarECommerceSearchStrategy().setPackageBoolean(packageBoolean);
        request.getCarECommerceSearchStrategy().setPostPurchaseBoolean(postPurchaseBoolean);
        request.getCarECommerceSearchStrategy().setPurchaseTypeMask(null);
        setProductCategoryCodeList(productCategoryCodes, request);
        //Send request with package boolean/postpurchase boolean and productCategoryCodeList
        final CarECommerceSearchResponseType responseWithoutPurchaseType = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);

        //If ProductCategoryCodeList is missing, verify error: CarECommerceSearchStrategy/ProductCategoryCodeList was missing but is required<
        if(productCategoryCodes == null || packageBoolean == null)
        {
            verifyError(productCategoryCodes, responseWithoutPurchaseType);

        }
        else
        {
            //Verify car count in two response is the same
            verifyHappyPath(responseWithPurchaseType, responseWithoutPurchaseType);
        }


    }

    private void setProductCategoryCodeList(String productCategoryCodes, CarECommerceSearchRequestType request)
    {
        if(productCategoryCodes == null)
        {
            request.getCarECommerceSearchStrategy().setProductCategoryCodeList(null);
        }
        else
        {
            final String[] productCategoryCodeList = productCategoryCodes.split(",");
            final ProductCategoryCodeListType productCategoryCodeListType = new ProductCategoryCodeListType();
            productCategoryCodeListType.setProductCategoryCode(new ArrayList<String>());
            for (final String productCategoryCode : productCategoryCodeList)
            {
                productCategoryCodeListType.getProductCategoryCode().add(productCategoryCode);
            }
            request.getCarECommerceSearchStrategy().setProductCategoryCodeList(productCategoryCodeListType);
        }
    }

    private void verifyError(String productCategoryCodes, CarECommerceSearchResponseType responseWithoutPurchaseType)
    {
        if(responseWithoutPurchaseType.getSearchErrorCollection().getFieldRequiredErrorList() == null
                || responseWithoutPurchaseType.getSearchErrorCollection().getFieldRequiredErrorList().getFieldRequiredError() == null
                || responseWithoutPurchaseType.getSearchErrorCollection().getFieldRequiredErrorList().getFieldRequiredError().isEmpty())
        {
            Assert.fail("FieldRequiredError is not returned in response");
        }
        final String expErorMsg = productCategoryCodes == null ? "ProductCategoryCodeList" : "PackageBoolean" + " was missing but is required";
        if(!responseWithoutPurchaseType.getSearchErrorCollection().getFieldRequiredErrorList().getFieldRequiredError().get(0).getDescriptionRawText().contains(expErorMsg))
        {
            Assert.fail(expErorMsg + " is not returned in response");
        }

    }

    private void verifyHappyPath(CarECommerceSearchResponseType responseWithPurchaseType,
                             CarECommerceSearchResponseType responseWithoutPurchaseType)
    {
        if (responseWithPurchaseType.getCarSearchResultList().getCarSearchResult().size() !=
                responseWithoutPurchaseType.getCarSearchResultList().getCarSearchResult().size())
        {
            Assert.fail(String.format(
                    "Search result count is not same between purchaseTypeMask(%S) and packageboolean/productCategoryCodeList(%s)!",
                    responseWithPurchaseType.getCarSearchResultList().getCarSearchResult().size(),
                    responseWithoutPurchaseType.getCarSearchResultList().getCarSearchResult().size()));
        }
        if (responseWithPurchaseType.getCarSearchResultList().getCarSearchResult().get(
                0).getCarProductList().getCarProduct().size() !=
                responseWithoutPurchaseType.getCarSearchResultList().getCarSearchResult().get(
                        0).getCarProductList().getCarProduct().size())
        {
            Assert.fail(String.format(
                    "Car count is not same between purchaseTypeMask(%S) and packageboolean/productCategoryCodeList(%s)!",
                    responseWithPurchaseType.getCarSearchResultList().getCarSearchResult().get(
                            0).getCarProductList().getCarProduct().size(),
                    responseWithoutPurchaseType.getCarSearchResultList().getCarSearchResult().get(
                            0).getCarProductList().getCarProduct().size()));
        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "550378"})
    public void tfsPackageBooleanSearch550378() throws Exception
    {
        doTest(CommonScenarios.Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(), "550378",
                "0Q7XRN", null, "1");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "550378"})
    public void tfs550385PackageBooleanFromPTMFalseCSSP() throws Exception
    {
        doTest(CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "550385"
                ,  "0Q7XRN",false, "1");
    }

    public void doTest(TestScenario scenarios, String tuid, String clientCode, Boolean packageBoolean, String expProductTokenOn) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        testData.setClientCode(clientCode);
        //create search request
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.getCarSearchStrategy().setPackageBoolean(packageBoolean);
        final CarECommerceSearchResponseType responseType = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        final CarProductType selectCarProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, responseType);
        Boolean expSearchPacBool = false;
        if (checkConfig(scenarios, expProductTokenOn))
        {
            if (null!= packageBoolean && !packageBoolean)
            {
                if(!CarbsRequestGenerator.getStandaloneBoolByPurchaseTypeMask(request.getCarECommerceSearchStrategy().getPurchaseTypeMask()))
                {
                    expSearchPacBool = true;
                }
            }
        }
        //Verify search response
        final StringBuffer errorMsg = new StringBuffer();
        for (final CarSearchResultType result : responseType.getCarSearchResultList().getCarSearchResult())
        {
            for (final CarProductType car : result.getCarProductList().getCarProduct())
            {
                if (!car.getCarInventoryKey().getPackageBoolean().equals(expSearchPacBool) && !car.getCarProductToken().equals(selectCarProductType.getCarProductToken()))
                {
                    errorMsg.append(String.format("PackageBoolean for car:%s is not expected in search response, expected: %s, actual: %s\r\n",
                            car.getCarProductToken(), expSearchPacBool, car.getCarInventoryKey().getPackageBoolean()));
                }
            }
        }


    }

    private boolean checkConfig(TestScenario scenarios ,String expectValue) throws DataAccessException, SQLException
    {
        final String setPosConfigUrl = AppConfig.resolveStringValue("${setPosConfig.server.address}");
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(),setPosConfigUrl);
        if(!posConfigHelper.checkPosConfigFeatureEnable(scenarios, expectValue, ServiceConfigs.PRODUCTTOKENENABLE, SettingsProvider.ENVIRONMENT_NAME))
        {
            Assert.fail("Pos config for ProductToken/enable is not set right");
        }
        final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());
        if (!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.PRODUCTTOKENENABLE, 5, expectValue))
        {
            Assert.fail("Client config for ProductToken/enable is not set right");
        }
        return true;
    }
}
