package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.e3.data.basetypes.defn.v4.ProductCategoryCodeListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CASSS-4783
 CMA: Pkgs: Return ReferenceEstimatedTotalCostToCustomer Price in GetDetails
 * Created by fehu on 1/22/2017.
 */
@SuppressWarnings("PMD")
public class PackageReferencePriceForGetdetails extends SuiteCommon {

    private static final String setPosConfigUrl = AppConfig.resolveStringValue("${setPosConfig.server.address}");
    private static final String REFERENCE_SETTINGNAME = "GetDetails.referencePricing/enable";
    private SpooferTransport spooferTransport;
    private CarProductType selectCarProduct;
    private String searchGuid;
    private String getDetailsGuid;
    private CarECommerceGetDetailsRequestType getDetailsRequestType;


   /* @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10097PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        final TestScenario scenarioPackage = CommonScenarios.GBR_Package_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        final TestScenario scenarioStandAlone = CommonScenarios.GBR_StandAlone_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1 for POS 10111/1050/GBR");
        }
        referenceEstimatedTotalCostToCustomerVerify(scenarioPackage, scenarioStandAlone, "10097", "ContainedEstimatedTotalAndTotalReferencePrice");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10098PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);


        final TestScenario scenarioPackage = CommonScenarios.GBR_Package_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        final TestScenario scenarioStandAlone = CommonScenarios.GBR_StandAlone_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1 for POS 10111/1050/GBR");
        }
        referenceEstimatedTotalCostToCustomerVerify(scenarioPackage, scenarioStandAlone, "10098", "OnlyContainedEstimatedTotal");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10099PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);


        final TestScenario scenarioPackage = CommonScenarios.GBR_Package_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        final TestScenario scenarioStandAlone = CommonScenarios.GBR_StandAlone_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1 for POS 10111/1050/GBR");
        }
        referenceEstimatedTotalCostToCustomerVerify(scenarioPackage, scenarioStandAlone, "10099", "OnlyContainedTotalReferencePrice");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10100PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);


        final TestScenario scenarioPackage = CommonScenarios.GBR_Package_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        final TestScenario scenarioStandAlone = CommonScenarios.GBR_StandAlone_OneWay_OnAirport_YOUNGDRIVER.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1 for POS 10111/1050/GBR");
        }
        referenceEstimatedTotalCostToCustomerVerify(scenarioPackage, scenarioStandAlone, "10100", "ContainedNull");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10093PackageGetDetails() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        TestScenario scenarioPackage = CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "0", REFERENCE_SETTINGNAME)) {
            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 0 for POS 10111/1060/FRA");

        }
        verifyReferenceEstimatedTotalCostToCustomerIsNotReturn(scenarioPackage, "10093", "ContainedEstimatedTotalAndTotalReferencePrice");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10094PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        TestScenario scenarioPackage = CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "0", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 0 for POS 10111/1060/FRA");
        }
        verifyReferenceEstimatedTotalCostToCustomerIsNotReturn(scenarioPackage, "10094", "OnlyContainedEstimatedTotal");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10095PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);


        TestScenario scenarioPackage = CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "0", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 0 for POS 10111/1060/FRA");
        }
        verifyReferenceEstimatedTotalCostToCustomerIsNotReturn(scenarioPackage, "10095", "OnlyContainedTotalReferencePrice");

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10096PackageGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        TestScenario scenarioPackage = CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "0", REFERENCE_SETTINGNAME)) {

            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 0 for POS 10111/1060/FRA");
        }
        verifyReferenceEstimatedTotalCostToCustomerIsNotReturn(scenarioPackage, "10096", "ContainedNull");

    }*/

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void TFS1026140GetDetailsReferencePricingEnableWithReferencePrice() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        final TestScenario scenarioPackage = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {
            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1 ");
        }
        verifyReturnReferencePrice(scenarioPackage, "1026142", "ContainedEstimatedTotalAndTotalReferencePrice", true, false, false, true,  false);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void TFS1026135GetDetailsReferencePricingEnableWithNoReferencePrice() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        final TestScenario scenarioPackage = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {
            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1");
        }
        verifyReturnReferencePrice(scenarioPackage, "10261351", "ContainedNull", false, false, false, true, false);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void TFS1067354GetDetailsReferencePricingEnableWithMIPCar() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        final TestScenario scenarioPackage = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {
            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1");
        }
        verifyReturnReferencePrice(scenarioPackage, "10673541", "OnlyContainedEstimatedTotal", true, true, false, true, false);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void TFS1026144GetDetailsReferencePricingEnableAndSenTTwice() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), setPosConfigUrl);

        final TestScenario scenarioPackage = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenarioPackage, "1", REFERENCE_SETTINGNAME)) {
            Assert.fail("The pos config GetDetails.referencePricing/enable is not expect 1");
        }
        verifyReturnReferencePrice(scenarioPackage, "10261441", "OnlyContainedEstimatedTotal", true, false, false, true,  true);

    }

    private void referenceEstimatedTotalCostToCustomerVerify(TestScenario scenarioPackage, TestScenario scenarioStandalone, String tuid, String estimatedTotalFeature) throws Exception {
        final CarECommerceGetDetailsResponseType responseTypeForPackage = carbsGetDetailPackage(true, scenarioPackage, tuid, estimatedTotalFeature);
        final CarECommerceGetDetailsResponseType responseTypeForStandalone = carbsGetDetailStandAlone(true, scenarioStandalone, tuid);

        final IVerification.VerificationResult result = verifyResultWhileFeatureEnable(responseTypeForPackage, responseTypeForStandalone);
        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }
    }

    private void verifyReferenceEstimatedTotalCostToCustomerIsNotReturn(TestScenario scenarioPackage, String tuid, String estimatedTotalFeature)
            throws Exception {
        final CarECommerceGetDetailsResponseType responseTypeForPackage = carbsGetDetailPackage(false, scenarioPackage, tuid, estimatedTotalFeature);

        final IVerification.VerificationResult result = verifyResultForPackageWhileFeatureDisable(responseTypeForPackage);
        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }
    }

    private IVerification.VerificationResult verifyResultForPackageWhileFeatureDisable(CarECommerceGetDetailsResponseType responseTypeForPackage) throws IOException, DataAccessException {
        boolean isPassed = false;
        final List remarks = new ArrayList();
        //get pacakge carProduct
        CarProductType packageCarProduct = responseTypeForPackage.getCarProductList().getCarProduct().get(0);

        //verify if exist the ReferenceEstimatedTotalCostToCustomer node
        if (null != packageCarProduct.getReferenceEstimatedTotalCostToCustomer()) {
            remarks.add("ReferenceEstimatedTotalCostToCustomer should not return for Details.referencePricing/enable is 0");
        }
        if (null != packageCarProduct.getReferencePriceList()) {
            remarks.add("ReferencePriceList should not return for Details.referencePricing/enable is 0");
        }
        if (null != packageCarProduct.getReferenceCarProductToken()) {
            remarks.add("ReferenceCarProductToken should not return for Details.referencePricing/enable is 0");
        }
        if (CollectionUtils.isEmpty(remarks)) {
            isPassed = true;
        }
        return new IVerification.VerificationResult("ReferencePriceForPackage", isPassed, remarks);
    }

    /**
     * our template for the price of EstimatedTotalCostToCustomer for standalone  and package car are same, so we can use the same car for standalone
     * and package to compare  and verify if  CarAdditionalFees is added to ReferenceEstimatedTotalCostToCustomer
     */
    private IVerification.VerificationResult verifyResultWhileFeatureEnable(CarECommerceGetDetailsResponseType responseTypeForPackage, CarECommerceGetDetailsResponseType responseTypeForStandalone) throws IOException, DataAccessException {
        boolean isPassed = false;
        final List remarks = new ArrayList();

        //get pacakge carProduct
        final CarProductType packageCarProduct = responseTypeForPackage.getCarProductList().getCarProduct().get(0);
        //get standalone carProduct
        final CarProductType standaloneCarProduct = responseTypeForStandalone.getCarProductList().getCarProduct().get(0);

        //verify if exist the ReferenceEstimatedTotalCostToCustomer node
        if (null == packageCarProduct.getReferenceEstimatedTotalCostToCustomer()) {
            remarks.add("ReferenceEstimatedTotalCostToCustomer is null.");
        }
        if (null == packageCarProduct.getTotalReferencePrice()) {
            remarks.add("TotalReferencePrice is null.");
        }
        if (null == packageCarProduct.getReferenceCarProductToken()) {
            remarks.add("ReferenceCarProductToken is null.");
        }
        // the EstimatedTotalCostToCustomer of package car
        final String estimatedTotalCostToCustomercurrencyCodeForPkg =
                packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
        final long estimatedTotalCostToCustomerDecimalPlaceCountForPkg =
                packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
        final int estimatedTotalCostToCustomerDecimalForPkg =
                packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();

        double estimatedTotalCostToCustomerAmountValueForPkg = estimatedTotalCostToCustomerDecimalForPkg / Math.pow(10, estimatedTotalCostToCustomerDecimalPlaceCountForPkg);

        //totalReferenceBasePrice of package car
        final String totalReferenceBasePriceCurrencyCodeForPkg = packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
        final long totalReferenceBasePriceDecimalPlaceCountForPkg = packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
        final int totalReferenceBasePriceDecimalForPkg = packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();

        double totalReferenceBasePriceAmountValueForPkg = totalReferenceBasePriceDecimalForPkg / Math.pow(10, totalReferenceBasePriceDecimalPlaceCountForPkg);

        //compare the EstimatedTotalCostToCustomer/TotalReferenceBasePrice/ReferenceCarProductToken  price of package and standalone car:
        List<PriceType> standaloneCarPriceList = standaloneCarProduct.getPriceList().getPrice();
        boolean existEReferencePriceFlag = false;
        boolean existBasePriceFlag = false;
        for (PriceType price : standaloneCarPriceList) {
            if ("EstimatedTotalCostToCustomer".equals(price.getFinanceCategoryCode()) &&
                    estimatedTotalCostToCustomercurrencyCodeForPkg.equals(price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())) {
                long decimalPlaceCountForStandalone = price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                double amountValueForStandalone = price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / Math.pow(10, decimalPlaceCountForStandalone);
                if (amountValueForStandalone != estimatedTotalCostToCustomerAmountValueForPkg) {
                    remarks.add("The ReferenceEstimatedTotalCostToCustomer price of Package is not equal the Fee of StandAlone!");
                }
                existEReferencePriceFlag = true;
            } else if ("Base".equals(price.getFinanceCategoryCode()) &&
                    totalReferenceBasePriceCurrencyCodeForPkg.equals(price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())) {
                long decimalPlaceCountForStandalone = price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                double amountValueForStandalone = price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / Math.pow(10, decimalPlaceCountForStandalone);
                if (amountValueForStandalone != totalReferenceBasePriceAmountValueForPkg) {
                    remarks.add("The Base price of Package is not equal the Fee of StandAlone!");
                }
                existBasePriceFlag = true;
            }
        }
        if (!existEReferencePriceFlag) {
            remarks.add("There is no EstimatedTotalCostToCustomer node in standAlone car or CurrencyCode not equal, please check!");
        }
        if (!existBasePriceFlag) {
            remarks.add("There is no Base node in standAlone car or CurrencyCode not equal, please check!");
        }
        if (CollectionUtils.isEmpty(remarks)) {
            isPassed = true;
        }
        return new IVerification.VerificationResult("ReferencePriceForPackage", isPassed, remarks);
    }

    private CarECommerceGetDetailsResponseType carbsGetDetailPackage(boolean needReferencePricesBoolean, TestScenario scenarios, String tuid, String estimatedTotalFeature) throws Exception {
        TestData testData = new TestData(httpClient, scenarios, tuid, ExecutionHelper.generateNewOrigGUID(spooferTransport));
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.getAuditLogTrackingData().setAuditLogForceLogging(true);
        CarECommerceSearchResponseType response = carbsSearch(needReferencePricesBoolean, request, testData);

        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        List<CarProductType> carProductTypeList = carbsSearchRequestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(response.getCarSearchResultList(), testData);

        if(CollectionUtils.isEmpty(carProductTypeList))
        {
            Assert.fail("no expect car return.");
        }
        for (CarProductType carProductType : carProductTypeList)
        {
            if (null != carProductType.getReferenceEstimatedTotalCostToCustomer() && !scenarios.getPurchaseType().equals(PurchaseType.CarOnly))
            {
                this.selectCarProduct = carProductType;
                carbsSearchRequestGenerator.setSelectedCarProduct(this.selectCarProduct);
                break;
            }
        }


        //getdetails
        if(null == this.selectCarProduct)
        {
            Assert.fail("no expect car return.");
        }
        CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        updateReferencePriceOptionOfSelectedCar(estimatedTotalFeature);
        String getDetailsGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(getDetailsGuid, httpClient, getDetailsRequestType);

        //verification
        CarBSGetDetailVerifier.retrySearchForGetDetailsVerifier(estimatedTotalFeature, spooferTransport, getDetailsGuid, scenarios, getDetailsRequestType, getDetailsResponseType);

        return getDetailsResponseType;

    }

    private CarECommerceSearchResponseType carbsSearch(boolean needReferencePricesBoolean, CarECommerceSearchRequestType request, TestData testData) throws IOException, DataAccessException {
        //set the driver age less than 25 years old to get the young driver fee
        request.getCarSearchStrategy().setDriverAgeYearCount(24l);
        request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(needReferencePricesBoolean);
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);

        return response;
    }

    private CarECommerceGetDetailsResponseType carbsGetDetailStandAlone(boolean needReferencePricesBoolean, TestScenario scenarios, String tuid) throws IOException, DataAccessException {
        //search
        String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.getAuditLogTrackingData().setAuditLogForceLogging(true);
        CarECommerceSearchResponseType response = carbsSearch(needReferencePricesBoolean, request, testData);

        //send the car same with package selectCar for standalone getDetail
        CarECommerceGetDetailsResponseType getDetailsResponseType = null;
        CarECommerceGetDetailsRequestType getDetailsRequestType = null;
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);

        for (CarSearchResultType carSearchResultType : response.getCarSearchResultList().getCarSearchResult())
        {
            for (CarProductType carProductType : carSearchResultType.getCarProductList().getCarProduct())
            {
                //find the same car as package's
                if (CarProductComparator.isCorrespondingCar(carProductType, this.selectCarProduct, false, false)) {
                    //getdetails
                    carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
                    getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
                    getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid, httpClient, getDetailsRequestType);
                    break;

                }
            }
        }

        //verification
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, scenarios, getDetailsRequestType, getDetailsResponseType);

        return getDetailsResponseType;
    }

    private void verifyReturnReferencePrice(TestScenario scenarioPackage, String tuid, String estimatedTotalFeature, boolean needReferencePricesBoolean, boolean forMIPCar, boolean needProductToken, boolean needReferencePriceReturned,  boolean sendTwice) throws IOException, DataAccessException, SQLException {
        final CarECommerceGetDetailsResponseType responseTypeForPackage = carbsGetDetailPackageForReferencePrice(scenarioPackage, needReferencePricesBoolean, forMIPCar, tuid, estimatedTotalFeature, needProductToken);

        final IVerification.VerificationResult result = verifyReturnReferenceResultWhileFeatureEnable(responseTypeForPackage,  needReferencePriceReturned,  sendTwice);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
    }

    private CarECommerceGetDetailsResponseType carbsGetDetailPackageForReferencePrice(TestScenario scenarios, boolean needReferencePricesBoolean, boolean forMIPCar, String tuid, String estimatedTotalFeature, boolean needProductToken) throws IOException, DataAccessException {
        searchGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "PriceListAgencyDailyPrice").build(), searchGuid);
        TestData testData = new TestData(httpClient, scenarios, tuid, searchGuid);
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.getAuditLogTrackingData().setAuditLogForceLogging(true);

        if (forMIPCar) {
            request.getCarECommerceSearchStrategy().setPostPurchaseBoolean(true);
            request.getCarSearchStrategy().setPackageBoolean(false);
            List<String> productCategoryList = new ArrayList<>();
            productCategoryList.add("Hotel");
            productCategoryList.add("Air");
            productCategoryList.add("Car");
            ProductCategoryCodeListType productCategorys = new ProductCategoryCodeListType();
            productCategorys.setProductCategoryCode(productCategoryList);
            request.getCarECommerceSearchStrategy().setProductCategoryCodeList(productCategorys);
            request.getCarECommerceSearchStrategy().setPurchaseTypeMask(null);
        }
        CarECommerceSearchResponseType response = carbsSearch(needReferencePricesBoolean, request, testData);
        this.selectCarProduct = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        if (null != selectCarProduct && (null == selectCarProduct.getTotalReferencePrice() || (null != selectCarProduct.getTotalReferencePrice() && selectCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0))) {
            //select the carProduct that contains TotalReferencePrice node
            List<CarSearchResultType> carSearchResultTypes = response.getCarSearchResultList().getCarSearchResult();
            for (CarSearchResultType carSearchResultType : carSearchResultTypes) {
                List<CarProductType> carProductTypeTemps = carSearchResultType.getCarProductList().getCarProduct();
                for (CarProductType carProductType : carProductTypeTemps) {
                    if ((null != carProductType.getTotalReferencePrice() && carProductType.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() > 0) && !scenarios.getPurchaseType().equals(PurchaseType.CarOnly)) {
                        this.selectCarProduct = carProductType;
                        break;
                    }
                }
                break;
            }
        }
        //getdetails
        getDetailsGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "PriceListAgencyDailyPrice").build(), getDetailsGuid);
        testData = new TestData(httpClient, scenarios, tuid, getDetailsGuid);
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        if (forMIPCar) {
            getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID(testData);
        } else {
            getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        }
        updateReferencePriceOptionOfSelectedCar(estimatedTotalFeature);
        getDetailsRequestType.getCarProductList().getCarProduct().set(0, this.selectCarProduct);

        if (!needProductToken) {
            getDetailsRequestType.getCarProductList().getCarProduct().get(0).setReferenceCarProductToken(null);
        }
        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(getDetailsGuid, httpClient, getDetailsRequestType);
        //verification
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(getDetailsGuid, scenarios, getDetailsRequestType, getDetailsResponseType);
        return getDetailsResponseType;
    }

    private IVerification.VerificationResult verifyReturnReferenceResultWhileFeatureEnable(CarECommerceGetDetailsResponseType responseTypeForPackage, boolean needReferencePriceReturned,  boolean sendTwice) throws IOException, DataAccessException {

        if(sendTwice){
            Document spooferDoc = spooferTransport.retrieveRecords(getDetailsGuid);
            final NodeList vehicleSearchAvailabilityReqBefore = spooferDoc.getElementsByTagNameNS("*", "VehicleSearchAvailabilityReq");
            CarbsRequestSender.getCarbsDetailsResponse(getDetailsGuid, httpClient, getDetailsRequestType);
            spooferDoc = spooferTransport.retrieveRecords(getDetailsGuid);
            final NodeList vehicleSearchAvailabilityReqAfter = spooferDoc.getElementsByTagNameNS("*", "VehicleSearchAvailabilityReq");
            if (vehicleSearchAvailabilityReqBefore.getLength() != vehicleSearchAvailabilityReqAfter.getLength()) {
                return new IVerification.VerificationResult("ReferencePriceForPackage", false, Arrays.asList("There is search message sent out as part of second getDetials call"));
            }
        }
        //get package carProduct
        CarProductType packageCarProduct = responseTypeForPackage.getCarProductList().getCarProduct().get(0);
        if (needReferencePriceReturned) {
            if (packageCarProduct.getTotalReferencePrice() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0)
                return new IVerification.VerificationResult("ReferencePriceForPackage", false, Arrays.asList("No ReferencePrice returned."));
        } else {
            if (!(packageCarProduct.getTotalReferencePrice() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount() == null
                    || packageCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0))
                return new IVerification.VerificationResult("ReferencePriceForPackage", false, Arrays.asList("ReferencePrice was returned."));
        }
        return new IVerification.VerificationResult("ReferencePriceForPackage", true, Arrays.asList("ReferencePrice works as expected"));
    }

    private void updateReferencePriceOptionOfSelectedCar(String estimatedTotalFeature)
    {
        //update ReferenceEstimatedTotalCostToCustomer and TotalReferencePrice based on requirement
        if ("OnlyContainedTotalReferencePrice".equalsIgnoreCase(estimatedTotalFeature))
        {
            selectCarProduct.setReferenceEstimatedTotalCostToCustomer(null);
        } else if ("OnlyContainedEstimatedTotal".equalsIgnoreCase(estimatedTotalFeature))
        {
            selectCarProduct.setTotalReferencePrice(null);
        } else if ("ContainedNull".equalsIgnoreCase(estimatedTotalFeature))
        {
            selectCarProduct.setReferenceEstimatedTotalCostToCustomer(null);
            selectCarProduct.setTotalReferencePrice(null);
        }

    }
}

