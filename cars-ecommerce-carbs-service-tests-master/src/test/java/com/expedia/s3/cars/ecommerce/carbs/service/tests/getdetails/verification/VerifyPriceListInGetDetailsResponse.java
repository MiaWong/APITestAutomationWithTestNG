package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.CostItemsRetriever;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.PriceListVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerifyPriceListInGetDetailsResponse implements IVerification<GetDetailsVerificationInput, BasicVerificationContext>
{
    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarProductType> carProductList = input.getRequest().getCarProductList().getCarProduct();
        if (carProductList.isEmpty()) {
            return false;
        } else {
            return carProductList.get(0).getPriceList() != null &&
                    !carProductList.get(0).getPriceList().getPrice().isEmpty();
        }
    }

    @Override
    public VerificationResult verify(GetDetailsVerificationInput getDetailsVerificationInput, BasicVerificationContext verificationContext) {

        boolean isPassed = false;
        final ArrayList remarks = new ArrayList();
        Map<String, Double> costItemsMap = null;
        try {
            final List<CarProductType> carProductList = getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct();
            final CarProductType carProductInGetDetailResponse = carProductList.get(0);

            final String requestCurrency = getDetailsVerificationInput.getRequest().getCurrencyCode();
            final String vendorCurrency = CostPriceCalculator.getCostPosuCurrencyCode(carProductInGetDetailResponse.getCostList(), requestCurrency);

            if(verificationContext.getScenario().getBusinessModel() == BusinessModel.Agency.getValue())
            {
                costItemsMap = CostItemsRetriever.getCostItemsFromCostListForAgencyGetDetail(requestCurrency, vendorCurrency, carProductInGetDetailResponse.getCostList(), false, remarks);
                PriceListVerifier.verifyPriceListForAgencyGetDetail(costItemsMap, carProductInGetDetailResponse, requestCurrency, vendorCurrency, remarks);

            } else if (verificationContext.getScenario().getBusinessModel() == BusinessModel.GDSP.getValue())
            {
                costItemsMap = CostItemsRetriever.getCostItemsFromCostListForGDSPGetDetail(requestCurrency, vendorCurrency, carProductInGetDetailResponse.getCostList());
                PriceListVerifier.verifyPriceListForGDSPGetDetail(costItemsMap, carProductInGetDetailResponse, requestCurrency, vendorCurrency, remarks);
            }

            if (costItemsMap.isEmpty())
            {
                remarks.add("There is no Car cost exit in car product for price list verification.");
            }
        } catch (DataAccessException e)
        {
            remarks.add(e);
        }

        if (CollectionUtils.isEmpty(remarks)) {
            isPassed = true;
        }

        return new VerificationResult(getName(), isPassed, remarks);
    }

    public VerificationResult verifyTitaniumPrice(GetDetailsVerificationInput getDetailsVerificationInput, TestData testData) throws DataAccessException
    {

            final List<String> remarks =  new ArrayList<>();
            Map<String, Double> costItemsMap;

            final List<CarProductType> carProductList = getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct();
            final CarProductType carProductInGetDetailResponse = carProductList.get(0);

            final String requestCurrency = getDetailsVerificationInput.getRequest().getCurrencyCode();
            final String vendorCurrency = CostPriceCalculator.getCostPosuCurrencyCode(carProductInGetDetailResponse.getCostList(), requestCurrency);

            //GDSP Markup car
            if (testData.getScenarios().getBusinessModel() == 3 && CollectionUtils.isNotEmpty(CostPriceCalculator.getPriceListByFinanceCategoryCode(
                    carProductInGetDetailResponse.getPriceList(), requestCurrency,"MaxMarginAmt","")))
            {
                costItemsMap = CostItemsRetriever.getCostItemsFromCostListForTitaniumGDSPGetDetail(requestCurrency, vendorCurrency, carProductInGetDetailResponse.getCostList());
                PriceListVerifier.gdspMarkupCarPriceListVerifier(costItemsMap, carProductInGetDetailResponse, requestCurrency, vendorCurrency, remarks);
            }
            //GDSP commission car
            else if (testData.getScenarios().getBusinessModel() == 3)
            {
                costItemsMap = CostItemsRetriever.getCostItemsFromCostListForTitaniumGDSPGetDetail(requestCurrency, vendorCurrency, carProductInGetDetailResponse.getCostList());
                PriceListVerifier.gdspCommissionCarPriceListVerifier(costItemsMap, carProductInGetDetailResponse, requestCurrency, vendorCurrency, remarks);
            }

        if (CollectionUtils.isNotEmpty(remarks))
        {
            return new VerificationResult(getName(), false, remarks);
        }

        return new VerificationResult(getName(), true, remarks);
    }

    public static void verifyTotalOfPriceListAndCostList(CarbsRequestGenerator requestGenerator) {
        String currencyCode = null;
        double actualAmount = 0;
        for (final CostType cost : requestGenerator.getSelectedCarProduct().getCostList().getCost()) {
            if (cost.getDescriptionRawText().contains("vendor currency") && cost.getDescriptionRawText().contains("Total") && cost.getFinanceCategoryCode().equals("Total")) {
                currencyCode = cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                actualAmount = CostPriceCalculator.calculateCostAmount(cost, 4, false);
                break;
            }
        }
        final Double priceAmount = CostPriceCalculator.getPriceAmountByFinanceCategoryCode(requestGenerator.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0).getPriceList(), currencyCode, "Total", 4, null, false);
        final Double costAmount = CostPriceCalculator.getCostAmountByFinanceCategoryCode(requestGenerator.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0).getCostList(), currencyCode, "Total", 4, null, null, null, false);
        if ((actualAmount != priceAmount) || (actualAmount != costAmount)) {
            Assert.fail("PriceList or CostList total is not equal to the expected value");
        }
    }

    public static void verifyDynamicCommissionInGetDetails(CarECommerceGetDetailsResponseType getDetailsResponse, VRRRsp vrrRsp) {
        final CarProductType carProduct = getDetailsResponse.getCarProductList().getCarProduct().get(0);
        if (CarProductComparator.isCorrespondingCar(carProduct, vrrRsp.getCarProduct(), false, false)) {
            if (!PriceListVerifier.isCommissionExistsInPriceList(carProduct)) {
                Assert.fail("PriceList in MN response is having Commission value");
            }
            if (!TotalPriceVerifier.isPriceListTotalAndCostListTotalEqual(carProduct)) {
                Assert.fail("PriceList total and CostList total are not equal in MN response");
            }
            if (!CostItemsRetriever.isCommissionValueEqual(carProduct, vrrRsp.getCarProduct())) {
                Assert.fail("Commission of MN detail response is not identical with Commission of MNSCS detail response: ");
            }
        }
    }

        @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
