package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by fehu on 8/22/2018.
 */
public class SearchForTitaniumBOT extends SuiteCommon {


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1085245CarBSSearchBOTEmptyResponse() throws DataAccessException, SQLException, IOException {
        carBSSearchBOT(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "1085245", "1");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs1085255CarBSSearchBOTEmptyResponse() throws DataAccessException, SQLException, IOException {
        carBSSearchBOT(CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_CDG.getTestScenario(), "1085255", "0");
    }

    private void carBSSearchBOT(TestScenario scenarioName, String tuid, String featureEnable) throws DataAccessException, SQLException, IOException {

        final TestData testData = new TestData(httpClient, scenarioName, tuid, PojoXmlUtil.getRandomGuid());
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getTitaniumDatasource(), SettingsProvider.SCS_TITANIUM_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
        if(!posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), featureEnable, ServiceConfigs.BOTTRAFFIC_EMPTYRESPONSE_ENABLE
                , SettingsProvider.ENVIRONMENT_NAME))
        {
            Assert.fail("The pos config " + ServiceConfigs.BOTTRAFFIC_EMPTYRESPONSE_ENABLE +" set not expect " + featureEnable
                    + ", Pos : " + testData.getScenarios().getJurisdictionCountryCode() + "," + testData.getScenarios().getManagementUnitCode()
                    + "," +testData.getScenarios().getCompanyCode());
        }

        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        if ("1".equals(featureEnable))
        {
            testBot(testData, request);
        }
        else
        {
            request.getMessageInfo().setClientHostnameString("BOTVILLE");
            request.getMessageInfo().setEndUserIPAddress("123.258.65.98");
            request.getMessageInfo().setClientName("KnownBot");
            final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
            CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData,request, response);

        }

      }

    private void testBot(TestData testData, CarECommerceSearchRequestType request) throws DataAccessException, IOException {
        request.getMessageInfo().setClientHostnameString("BOTVILLE");
        request.getMessageInfo().setEndUserIPAddress("123.258.65.98");
        request.getMessageInfo().setClientName("KnownBot");
        getSearchRsponseAndverify(testData, request);

        request.getMessageInfo().setClientHostnameString("BOTVILLE");
        request.getMessageInfo().setEndUserIPAddress("123.258.65.98");
        request.getMessageInfo().setClientName("Competitor");
        getSearchRsponseAndverify(testData, request);

        request.getMessageInfo().setClientHostnameString("BOTVILLE");
        request.getMessageInfo().setEndUserIPAddress("123.258.65.98");
        request.getMessageInfo().setClientName("KnownBotCountry");
        getSearchRsponseAndverify(testData, request);


        request.getMessageInfo().setClientHostnameString("BOTVILLE");
        request.getMessageInfo().setEndUserIPAddress("123.258.65.98");
        request.getMessageInfo().setClientName("HostingProvider");
        getSearchRsponseAndverify(testData, request);

        request.getMessageInfo().setClientHostnameString("CAPTCHA");
        request.getMessageInfo().setEndUserIPAddress("123.258.65.98");
        request.getMessageInfo().setClientName("KnownBot");
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData,request, response);

    }

    private void getSearchRsponseAndverify(TestData testData, CarECommerceSearchRequestType request) throws DataAccessException {
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        final List<CarProductType> carProductTypeList = carbsSearchRequestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(response.getCarSearchResultList(), testData);
        if(CollectionUtils.isNotEmpty(carProductTypeList))
        {
            Assert.fail("there should be not titanium car in response. " + " Scenario is " + request.getMessageInfo().getClientHostnameString()
            + "/" + request.getMessageInfo().getEndUserIPAddress() +  "/" + request.getMessageInfo().getClientName());
        }

        //todo perfmetric verify  ClientHostnameString/EndUserIPAddress/ClientName
    }
}