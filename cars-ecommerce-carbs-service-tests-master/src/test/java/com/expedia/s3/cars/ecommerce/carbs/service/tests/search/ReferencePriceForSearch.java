package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 1/6/2017.
 */
@SuppressWarnings("PMD")
public class ReferencePriceForSearch extends SuiteCommon{

    //CASSS-4380 CMA: Pkgs: make sure Driver Surcharge is included in headline price (Carbs work)
    //the scenario can response cars that contains additional fee and not contains addtional fee,so both can be verified
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS18710PackageSearchFC() throws IOException, DataAccessException
    {
        referencePriceVerify(CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario(), CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),"18710");
    }

    //18711 - Verify Reference Price in CarBS H+C package search Response is correct
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS18711PackageSearchHC() throws IOException, DataAccessException
    {
        referencePriceVerify(CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario(), CommonScenarios.Worldspan_FR_GDSP_Standalone_nonFRLocation_OnAirport.getTestScenario(), "18711");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    //bug CASSS-11910
    public void CASSS10076StandalonePrepaidCar() throws IOException, DataAccessException
    {
        referencePriceVerify(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "10076");
    }

    private  void referencePriceVerify(TestScenario scenarioPackage, TestScenario scenarioStandalone, String tuid) throws IOException, DataAccessException
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final List<CarProductType> carProductTypeListForPackage = carbsSearch(scenarioPackage, tuid, randomGuid);
        List<CarProductType> carProductTypeListForStandalone = carbsSearch(scenarioStandalone, tuid, randomGuid);

        IVerification.VerificationResult result = verifyDuplicateCarPriceFilter(carProductTypeListForPackage, scenarioPackage);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }

        if(scenarioPackage.getPurchaseType() == PurchaseType.CarOnly)
        {
            carProductTypeListForStandalone = filterPrepaidCarForStandlaloneReferencePrice(carProductTypeListForStandalone);
            result = verifyStandalonePrepaidCarHasReferencePrice(carProductTypeListForPackage);
            if (!result.isPassed())
            {
                Assert.fail(result.toString());
            }
        }

        result = verifyResult(carProductTypeListForPackage, carProductTypeListForStandalone);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
    }

    private List<CarProductType> filterPrepaidCarForStandlaloneReferencePrice(List<CarProductType> carProductTypeListForStandalone)
    {
        final List<CarProductType> filterList = new ArrayList<>();
        for (CarProductType car : carProductTypeListForStandalone)
        {
            if (null == car.getPrePayBoolean() || !car.getPrePayBoolean().booleanValue())
            {
                filterList.add(car);
            }
        }
        return filterList;

    }

    private IVerification.VerificationResult verifyDuplicateCarPriceFilter (List<CarProductType> carProductList, TestScenario scenario) throws IOException, DataAccessException
    {
        boolean isPassed = false;
        List<String> remarks = new ArrayList<>();

        Map<String, CarProductType> carMap = new HashMap<>();
        for (CarProductType car : carProductList)
        {
            //TODO CASSS-11910 Price filter is not done to prepaid agency car:
            //After bug fix, we shold remove below code
            /*if(scenario.getBusinessModel() == 1 && car.getPrePayBoolean()) {
                continue;
            }*/
            if(scenario.getBusinessModel() == 1 && car.getPrePayBoolean()) {
                continue;
            }
            final String connectStr = "^";
            final StringBuffer vPrice =new StringBuffer();
            vPrice.append(car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()).append(connectStr)
                    .append("CarCategory")
                    .append(car.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()).append(connectStr)
                    .append(CostPriceCalculator.getPosTotalPrice(car.getPriceList(), scenario.getSupplierCurrencyCode(), true));
            if(null != car.getPrePayBoolean())
            {
                vPrice.append(connectStr).append("prePayBoolean ").append(car.getPrePayBoolean().booleanValue());
            }
            //find the duplicated car.
            if (carMap.containsKey(vPrice.toString()))
            {
                remarks.add("Car " + vPrice + " is not filter duplicate price car in search response.");
            }
            carMap.put(vPrice.toString(), car);
        }

        if (CollectionUtils.isEmpty(remarks)) {
            isPassed = true;
        }
        return new IVerification.VerificationResult("verifyDuplicateCarFilter", isPassed, remarks);
    }

    private IVerification.VerificationResult verifyStandalonePrepaidCarHasReferencePrice (List<CarProductType> carProductList) throws IOException, DataAccessException
    {
        boolean isPassed = false;
        List<String> remarks = new ArrayList<>();
        List<Long> supplierHasRefPrice = new ArrayList<>();
        for (CarProductType car : carProductList)
        {
            if(null != car.getPrePayBoolean() && car.getPrePayBoolean().booleanValue()) {
                if (null == car.getTotalReferencePrice() && !supplierHasRefPrice.contains(car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID())) {
                    remarks.add("ReferencePrice not returned for prepaid agency car");
                }
                else
                {
                    supplierHasRefPrice.add(car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
                }
            }

        }

        if (CollectionUtils.isEmpty(remarks)) {
            isPassed = true;
        }
        return new IVerification.VerificationResult("verifyStandalonePrepaidCarHasReferencePrice", isPassed, remarks);
    }

    /**
     * our template for the price of EstimatedTotalCostToCustomer for standalone  and package car are same, so we can use the same car for standalone
     * and package to compare  and verify if the CarAdditionalFees is added to ReferenceEstimatedTotalCostToCustomer
     */
    private IVerification.VerificationResult verifyResult(List<CarProductType> carProductList, List<CarProductType> standaloneCarProductList) throws IOException, DataAccessException {
        int compareCount = 0;

        for (CarProductType packageCarProduct : carProductList)
        {
            for (CarProductType standaloneCarProduct : standaloneCarProductList)
            {
                //find the same car as package's
                if ( CarProductComparator.isCorrespondingCar(packageCarProduct, standaloneCarProduct, false, false))
                {

                        //verify if exist the ReferenceEstimatedTotalCostToCustomer node
                        if (null == packageCarProduct.getReferenceEstimatedTotalCostToCustomer())
                        {
                            continue;
                        }
                        // get the package car refrenceTotal
                        String curAmountForPackge = packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                        long deciPlaceCountForPackge = packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                        double amountValueForPackge = packageCarProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / Math.pow(10, deciPlaceCountForPackge);


                        //compare the price of EstimatedTotalCostToCustomer
                        List<PriceType> priceTypes = standaloneCarProduct.getPriceList().getPrice();
                        for (PriceType priceType : priceTypes)
                        {
                            if ("EstimatedTotalCostToCustomer".equals(priceType.getFinanceCategoryCode()) && curAmountForPackge.equals(priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())) {
                                long decimalPlaceCountForStandalone = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                                double amountValueForStandalone = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / Math.pow(10, decimalPlaceCountForStandalone);
                                if (amountValueForStandalone != amountValueForPackge)
                                {
                                    Assert.fail("The amount in ReferenceEstimatedTotalCostToCustomer is not right, Expect "+ amountValueForStandalone + " Actual " + amountValueForPackge) ;
                                }
                                compareCount++;
                                break;
                            }
                        }
                        break;

                }
            }
            //added condition to check the Reference price of first 5 carProducts only.
            if (compareCount > 4)
                break;
        }
        return new IVerification.VerificationResult("ReferencePriceVerify", true, Arrays.asList("success"));
    }

    private List<CarProductType> carbsSearch(TestScenario scenarios, String tuid, String guid) throws IOException, DataAccessException {
        TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        //set the driver age less than 25 years old to get the young driver fee
        request.getCarSearchStrategy().setDriverAgeYearCount(24l);

        //to-do: for now test cases might fail as we are not getting GDSP car when NeedReferencePricesBoolean is false
        if (!scenarios.getPurchaseType().equals(PurchaseType.CarOnly)) {
            request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
        }
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, request);
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        List<CarProductType> carProductTypeList = carbsSearchRequestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(response.getCarSearchResultList(), testData);

        return carProductTypeList;
    }
}

