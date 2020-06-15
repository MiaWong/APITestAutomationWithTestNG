package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CarProductCacheTests extends SuiteCommon {

    Logger logger = Logger.getLogger(getClass());

    //test: CarProductCacheTestForAgency - Agency - currency conversation
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs61836CarProductCacheTestForAgencyCurrencyExchagen() throws IOException, DataAccessException {
        carProductCacheTest(CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway.getTestScenario(),
                "POSuGBP_Weekly",
                "61836", CommonEnumManager.TimeDuration.WeeklyExtDays);
    }

    //test: CarProductCacheTestForAgency - GDSP - currency conversation
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs61842CarProductCacheTestForGDSPCurrencyExchagen() throws IOException, DataAccessException {
        carProductCacheTest(CommonScenarios.Worldspan_UK_GDSP_FHCPackage_nonUKLocation_OnAirport.getTestScenario(),
                "POSuUSD_Weekly",
                "61842", CommonEnumManager.TimeDuration.WeeklyExtDays);
    }

    //test: CarProductCacheTestForAgency - Agency - currency conversation
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs61886CarProductCacheTestForAgency() throws IOException, DataAccessException {
        carProductCacheTest(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(),
                "POSuUSD_Daily",
                "61886", CommonEnumManager.TimeDuration.Days3);
    }

    //test: CarProductCacheTestForAgency - GDSP - currency conversation
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs61888CarProductCacheTestForGDSP() throws IOException, DataAccessException {
        carProductCacheTest(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "POSuGBP_Weekly",
                "61888", CommonEnumManager.TimeDuration.WeeklyDays6);
    }




    public void carProductCacheTest(TestScenario testScenario, String scenarioName, String tuid, CommonEnumManager.TimeDuration timeDuration)
            throws IOException, DataAccessException {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);
        final TestData testData = new TestData(httpClient, timeDuration, testScenario, tuid, guid, false);

        //search
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);

        //CostAndAvailability
        final CarECommerceGetCostAndAvailabilityRequestType costAvailReq =
                requestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType costAvailRsp =
                CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), costAvailReq);

        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                new GetCostAndAvailabilityVerificationInput(costAvailReq, costAvailRsp);

        requestGenerator.setGetCostAndAvailabilityRequestType(costAvailReq);
        requestGenerator.setGetCostAndAvailabilityResponseType(costAvailRsp);

        VerificationHelper.getCostAndAvailabilityBasicVerification(getCostAndAvailabilityVerificationInput, spooferTransport,
                testData.getScenarios(), testData.getGuid(), false, logger);

        //getDetails
        final CarECommerceGetDetailsRequestType detailsReq =
                requestGenerator.createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID(testData);
        final CarECommerceGetDetailsResponseType detailsRsp =
                CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), detailsReq);

        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(detailsReq, detailsRsp);
        requestGenerator.setGetDetailsRequestType(detailsReq);
        requestGenerator.setGetDetailsResponseType(detailsRsp);

        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger);

        //generate with new GUID for second costAvail request(only PIID) and getdetails request(only PIID)
        guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);

        //set costAvail request CarInventoryKey to null and send to CarBS
        costAvailReq.getCarProductList().getCarProduct().get(0).setCarInventoryKey(null);
        final CarECommerceGetCostAndAvailabilityResponseType secondCostAvailRsp =
                CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), costAvailReq);


        //set details requestCarInventoryKey to null and send to CarBS
        detailsReq.getCarProductList().getCarProduct().get(0).setCarInventoryKey(null);
        final CarECommerceGetDetailsResponseType secondDetailsRsp =
                CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), detailsReq);

        //Verify total price for each costAvail and details response
        final String posCurrencyCode = requestGenerator.getSearchRequestType().getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
        final List remarks = new ArrayList<>();
        TotalPriceVerifier.verifyTotalPriceEqual(costAvailRsp.getCarProductList().getCarProduct().get(0),
                detailsRsp.getCarProductList().getCarProduct().get(0),
                posCurrencyCode, remarks, true);
        TotalPriceVerifier.verifyTotalPriceEqual(secondCostAvailRsp.getCarProductList().getCarProduct().get(0),
                detailsRsp.getCarProductList().getCarProduct().get(0),
                posCurrencyCode, remarks, true);
        TotalPriceVerifier.verifyTotalPriceEqual(secondCostAvailRsp.getCarProductList().getCarProduct().get(0),
                secondDetailsRsp.getCarProductList().getCarProduct().get(0),
                posCurrencyCode, remarks, true);

        //Verify no VSAR/VRUR sent for second costAvail/details
        String exception = "";
        try {
            spooferTransport.retrieveRecords(guid);
        }
        catch (Exception e)
        {
            exception = e.getMessage();
        }
        if(exception.isEmpty())
        {
            remarks.add("There should have no GDS message for second costAvail and details");
        }

        if (!remarks.isEmpty()) {
            Assert.fail(remarks.toString());
        }
    }
}
