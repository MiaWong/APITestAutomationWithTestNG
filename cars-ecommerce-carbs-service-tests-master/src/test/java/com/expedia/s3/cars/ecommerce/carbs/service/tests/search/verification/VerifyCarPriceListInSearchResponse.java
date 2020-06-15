package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.CostItemsRetriever;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.PriceListVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARRsp;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VerifyCarPriceListInSearchResponse implements IVerification<SearchVerificationInput, BasicVerificationContext>
{

    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext basicVerificationContext)
    {
        boolean isPassed = false;
        final List remarks = new ArrayList();
        try
        {
            final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
            final CarProductType selectedCarProduct = inventoryHelper.selectCarByBusinessModelIDAndServiceProviderIDFromCarSearchResultList(
                    searchVerificationInput.getResponse().getCarSearchResultList(), basicVerificationContext.getScenario().getBusinessModel(),
                    basicVerificationContext.getScenario().getServiceProviderID(), false);

            final String requestCurrency = searchVerificationInput.getRequest().getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode();
            final String vendorCurrency = CostPriceCalculator.getCostPosuCurrencyCode(selectedCarProduct.getCostList(), requestCurrency);

            final Map<String, Double> costItems = CostItemsRetriever.getCostItemsFromCostListForAgencyAndGDSPCarBSSearch(requestCurrency, vendorCurrency, selectedCarProduct);
            if(basicVerificationContext.getScenario().getBusinessModel() == BusinessModel.Agency.getValue())
            {
                PriceListVerifier.verifyPriceListForAgencySearch(costItems, selectedCarProduct, requestCurrency, vendorCurrency, remarks);
            } else if (basicVerificationContext.getScenario().getBusinessModel() == BusinessModel.GDSP.getValue())
            {
                PriceListVerifier.verifyPriceListForGDSPSearch(costItems, selectedCarProduct, requestCurrency, vendorCurrency, remarks);
            }

            if (costItems.isEmpty()) {
                remarks.add("There is no Car cost exit in car product for price list verification.");
            }

        } catch (DataAccessException e)
        {
            remarks.add(e);
        }

        if (CollectionUtils.isEmpty(remarks))
        {
            isPassed = true;
        }

        return new VerificationResult(getName(), isPassed, remarks);
    }

    public static void assertSearchResponseForDynamicCommission(CarECommerceSearchResponseType responseType, VARRsp varRsp) throws Exception {
        final ArrayList<CarProductType> carProductList = new ArrayList<>();
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        for (final CarSearchResultType carSearchResultType : responseType.getCarSearchResultList().getCarSearchResult()) {
            carProductList.addAll(carSearchResultType.getCarProductList().getCarProduct());
        }
        for (final CarProductType carProduct : carProductList) {
            if (carsInventoryDataSource.getServiceIDForSupplySubsetIDCost(carProduct.getCarInventoryKey().getSupplySubsetID()) == 3) {
                if (!PriceListVerifier.isCommissionExistsInPriceList(carProduct)) {
                    Assert.fail("PriceList in MN response is having Commission value");
                }
                if (!TotalPriceVerifier.isPriceListTotalAndCostListTotalEqual(carProduct)) {
                    Assert.fail("PriceList total and CostList total are not equal in MN response");
                }
                if (CostItemsRetriever.verifyCommissionInCostList(carProduct, varRsp.getCarProductList())) {
                    break;
                }
            }
        }
    }

    @Override
    public String getName()
    {
        return getClass().getSimpleName();
    }
}