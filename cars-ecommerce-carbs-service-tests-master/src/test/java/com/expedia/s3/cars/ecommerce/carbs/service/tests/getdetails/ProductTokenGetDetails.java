package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.CarsInventoryKeyComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProductTokenGetDetails extends SuiteCommon {

    Logger logger = Logger.getLogger(getClass());

    //test: GetDetails with token and CarInventoryKey - Agency
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs507037ProductTokenAndInventoryKeyForAgency() throws IOException, DataAccessException {
        validateCarInventoryKeyForGetDetails(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "USAgencyStandaloneLatLong",
                "507037", CommonEnumManager.TimeDuration.Daily, false, false);
    }

    //test: GetDetails with token and CarInventoryKey - GDSP
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs507306ProductTokenAndInventoryKeyForGDSP() throws IOException, DataAccessException {
        validateCarInventoryKeyForGetDetails(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "USAgencyStandaloneLatLong",
                "507306", CommonEnumManager.TimeDuration.Days3, false, false);
    }

    //test: GetDetails with token and CarInventoryKey - loyalty program
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs989824ProductTokenAndInventoryKeyWithLoyaltyProgram() throws IOException, DataAccessException {
        validateCarInventoryKeyForGetDetails(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "USAgencyStandaloneLatLong",
                "989824", CommonEnumManager.TimeDuration.Weekend3dayextraHours, false, true);
    }

    public void validateCarInventoryKeyForGetDetails(TestScenario testScenario, String scenarioName, String tuid, CommonEnumManager.TimeDuration timeDuration,
                                                     boolean extraHours, boolean ifLoyaltyAndVendorCode) throws IOException, DataAccessException {
        final StringBuilder errorMsg = new StringBuilder();

        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);
        final TestData testData = new TestData(httpClient, timeDuration, testScenario, tuid, guid, extraHours);

        if (ifLoyaltyAndVendorCode) {
            ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, DatasourceHelper.getCarInventoryDatasource(), true, RequestDefaultValues.VENDOR_CODE_AL);
            ExecutionHelper.setCarRateOfTestData(testData, true, RequestDefaultValues.LOYALTYNUMBER_ALAMO, "");
        }
        final CarbsRequestGenerator carbsRequestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData,
                spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);
        final CarProductType selectedCarProduct = carbsRequestGenerator.getCarProduct(carbsRequestGenerator.getSearchResponseType(), testData);

        final boolean result = CarsInventoryKeyComparator.isCarInventoryKeyEqual(selectedCarProduct.getCarInventoryKey(), carbsRequestGenerator.getGetDetailsResponseType()
                .getCarProductList().getCarProduct().get(0).getCarInventoryKey(), errorMsg, new ArrayList<>());
        if (!result) {
            Assert.fail("CarInventoryKey of selected carProduct and getDetails are not equal");
        }
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs52675CarBSGetdetailsRequestOnlySendInvalidPIID() throws Exception {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "81554", PojoXmlUtil.getRandomGuid());

        testCarBSGetdetailsInvalidPIID(testData, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs52675CarBSGetdetailsRequestSuccessfullOnlySendPIID() throws Exception {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "81554", PojoXmlUtil.getRandomGuid());

        testCarBSGetdetailsInvalidPIID(testData,  true);
    }

    public void testCarBSGetdetailsInvalidPIID(TestData testData, boolean isSuccessRespVerify) throws IOException, DataAccessException {

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = requestGenerator.createCarbsDetailsRequest();
        //just send a CarproductToken down stream.
        final String carProductToken = getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarProductToken();
        final CarProductType carProductType = new CarProductType();
        if(isSuccessRespVerify)
        {
            carProductType.setCarProductToken(carProductToken);
        }
        else
        {
            carProductType.setCarProductToken("InvalidProductToken");
        }
        final List<CarProductType> carProductTypeList = new ArrayList<>();
        carProductTypeList.add(carProductType);
        getDetailsRequestType.getCarProductList().setCarProduct(carProductTypeList);

        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        if (isSuccessRespVerify)
        {
            final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);
            VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);

        }
        else
        {
            if (!(null != getDetailsResponseType.getDetailsErrorCollection() && null != getDetailsResponseType.getDetailsErrorCollection().getCarProductTokenInvalidErrorList() && CollectionUtils.isNotEmpty(getDetailsResponseType.getDetailsErrorCollection().getCarProductTokenInvalidErrorList().getCarProductTokenInvalidError()) && getDetailsResponseType.getDetailsErrorCollection().getCarProductTokenInvalidErrorList().getCarProductTokenInvalidError().get(0).getDescriptionRawText().contains("Invalid CarProductToken")))
            {
                Assert.fail("Expected error not exist in CarBS response: Invalid CarProductToken - unable to retrieve CarInventoryKey!");
            }
        }
    }
}
