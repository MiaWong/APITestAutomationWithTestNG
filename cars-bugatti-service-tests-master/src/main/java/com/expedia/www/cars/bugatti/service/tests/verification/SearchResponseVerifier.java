package com.expedia.www.cars.bugatti.service.tests.verification;

import com.expedia.cars.schema.common.v1.AdditionalFeesList.AdditionalFees;
import com.expedia.cars.schema.common.v1.CarOffer;
import com.expedia.cars.schema.common.v1.CoveragesCostList.CoveragesCost;
import com.expedia.cars.schema.common.v1.ErrorType;
import com.expedia.cars.schema.common.v1.ProductCategoryCodeList;
import com.expedia.cars.schema.common.v1.RateDetail;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchResult;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;

import java.util.List;


/**
 * Created by miawang on 3/21/2017.
 */
@SuppressWarnings("PMD")
public class SearchResponseVerifier
{
    final static long DEFAULT_DRIVER_AGE = 35l;
    private TestScenario scenarios;

    public SearchResponseVerifier(TestScenario scenarios) {
        this.scenarios = scenarios;
    }

    public void verifyCarProductReturned(CarSearchResponse response) {
        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder("");
        boolean matchedCarReturned = false;
        if (null == response) {
            errorMsg.append("search Response is null.");
        } else {
            if (null == response.getSearchResultList() || CollectionUtils.isEmpty(response.getSearchResultList().getSearchResult())) {
                errorMsg.append("No SearchResult return in response.");
            } else {
                for (final SearchResult result : response.getSearchResultList().getSearchResult()) {
                    if (null != result.getCarOfferList()
                            && null != result.getCarOfferList().getCarOffer()
                            && result.getCarOfferList().getCarOffer().size() > 0) {
                        matchedCarReturned = true;
                        break;
                    }
                }
            }
            if (!matchedCarReturned) {
                errorMsg.append("No Car returned in CarSCS response.");
            }

            if (null != response.getErrorList() && null != response.getErrorList().getError()) {
                final List<ErrorType> errors = response.getErrorList().getError();
                if (!CollectionUtils.isEmpty(errors)) {
                    errorMsg.append("ErrorCollection is present in response");
                    errors.parallelStream().forEach((ErrorType s) -> errorMsg.append(s.getValue()));
                }
            }
        }
        if (!StringUtils.isEmpty(String.valueOf(errorMsg))) {
            Assert.fail(errorMsg.toString());
        }
    }

    public void verifyPackageInfoAndCDCodesInCarProduct(CarSearchResponse response) {
        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder("");
        boolean packageInfoCorrectReturn = true;
        boolean returnRulesEngineCDcodesInPackegeCar = false;
        if (null != response && null != response.getSearchResultList() && !CollectionUtils.isEmpty(response.getSearchResultList().getSearchResult())) {
            for (final SearchResult result : response.getSearchResultList().getSearchResult()) {
                if (null != result.getCarOfferList()
                        && null != result.getCarOfferList().getCarOffer()
                        && result.getCarOfferList().getCarOffer().size() > 0) {
                    for (final CarOffer carOffer : result.getCarOfferList().getCarOffer()) {
                        final ProductCategoryCodeList expectProductCategoryCodeList = buildProductCategoryCodeListWithTestScenarios(scenarios);
                        final ProductCategoryCodeList actualProductCategoryCodeList = carOffer.getCarOfferContext().getBundlingInfo().getProductCategoryCodeList();
                        if (carOffer.getCarOfferContext().getBundlingInfo().isPackage() != !scenarios.isStandalone()
                                || !isProductCategoryCodeListEqual(expectProductCategoryCodeList, actualProductCategoryCodeList)) {
                            packageInfoCorrectReturn = false;
                        }

                        if (!scenarios.isStandalone()) {
                            if (null != carOffer.getCarProduct().getCarInventoryKey() && null != carOffer.getCarProduct().getCarInventoryKey().getCarRate()
                                    && !StringUtils.isEmpty(carOffer.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode())
                                    && (
                                    (carOffer.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals("D103900"))
                                            || (carOffer.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals("D778300"))
                                            || (carOffer.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals("393940"))
                                            || (carOffer.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals("393494993"))
                                            || (carOffer.getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals("393494993"))
                            )) {
                                returnRulesEngineCDcodesInPackegeCar = true;
                            }
                        }
                    }
                }
            }
        }
        if (!packageInfoCorrectReturn) {
            errorMsg.append("PackageBoolean or ProductCategoryCodeList is not Return correctly in Response, please check.");
        }

        if (returnRulesEngineCDcodesInPackegeCar) {
            errorMsg.append("Return CD code in package Car, should not apply CD code in Package car, please check.");
        }

        if (!StringUtils.isEmpty(String.valueOf(errorMsg))) {
            Assert.fail(errorMsg.toString());
        }
    }

    public void verifyAdditionalFeesInCarProduct(CarSearchResponse response)
    {
        final StringBuilder errorMessage = new StringBuilder();
        if ( null != response && null != response.getSearchResultList() && !CollectionUtils.isEmpty(response.getSearchResultList().getSearchResult()) )
        {
            for ( final SearchResult result : response.getSearchResultList().getSearchResult() )
            {
                if ( null != result.getCarOfferList().getCarOffer()
                        && result.getCarOfferList().getCarOffer().size() > 0 )
                {
                    for ( final CarOffer carOffer : result.getCarOfferList().getCarOffer() )
                    {
                        if (null != carOffer.getCarProduct()  )
                        {
                            final RateDetail rateDetail = carOffer.getCarProduct().getRateDetail();
                            if ( null == rateDetail || null == rateDetail.getAdditionalFeesList()
                                    || null == rateDetail.getAdditionalFeesList()
                                    || rateDetail.getAdditionalFeesList().getAdditionalFees().isEmpty() )
                            {
                                errorMessage.append("No additional fees returned.");
                            }
                            else
                            {
                                // validate addtional fees
                                final List<AdditionalFees> fees = rateDetail.getAdditionalFeesList().getAdditionalFees();
                                for ( final AdditionalFees fee : fees )
                                {
                                    if ( null == fee.getFeeLineItem() )
                                    {
                                        errorMessage.append("Invalid additional fee returned.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if ( !StringUtils.isEmpty(String.valueOf(errorMessage)) )
        {
            Assert.fail(errorMessage.toString());
        }
    }

    public void verifyCoveragesCostInCarProduct(CarSearchResponse response)
    {
        final StringBuilder errorMessage = new StringBuilder();
        if ( null != response && null != response.getSearchResultList() && !CollectionUtils.isEmpty(response.getSearchResultList().getSearchResult()) )
        {
            for ( final SearchResult result : response.getSearchResultList().getSearchResult() )
            {
                if ( null != result.getCarOfferList().getCarOffer()
                        && result.getCarOfferList().getCarOffer().size() > 0 )
                {
                    for ( final CarOffer carOffer : result.getCarOfferList().getCarOffer() )
                    {
                        if (null != carOffer.getCarProduct()  )
                        {
                            final RateDetail rateDetail = carOffer.getCarProduct().getRateDetail();
                            if ( null == rateDetail || null == rateDetail.getCoveragesCostList()
                                    || null == rateDetail.getCoveragesCostList()
                                    || rateDetail.getCoveragesCostList().getCoveragesCost().isEmpty() )
                            {
                                errorMessage.append("No coverages costs returned.");
                            }
                            else
                            {
                                // validate coverages costs
                                final List<CoveragesCost> costs = rateDetail.getCoveragesCostList().getCoveragesCost();
                                for ( final CoveragesCost cost : costs )
                                {
                                    if ( null == cost.getCostLineItem() )
                                    {
                                        errorMessage.append("Invalid coverages cost returned.");
                                    }

                                    if ( null == cost.getCarDeductible() )
                                    {
                                        errorMessage.append("Invalid coverages cost deductible returned.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if ( !StringUtils.isEmpty(String.valueOf(errorMessage)) )
        {
            Assert.fail(errorMessage.toString());
        }
    }

    public void verifyDriverAgeInCarProduct(CarSearchRequest request, CarSearchResponse response)
    {
        final StringBuilder errorMessage = new StringBuilder();
        if ( null != response && null != response.getSearchResultList() && !CollectionUtils.isEmpty(response.getSearchResultList().getSearchResult()) )
        {
            for ( final SearchResult result : response.getSearchResultList().getSearchResult() )
            {
                if ( null != result.getCarOfferList().getCarOffer()
                        && result.getCarOfferList().getCarOffer().size() > 0 )
                {
                    for ( final CarOffer carOffer : result.getCarOfferList().getCarOffer() )
                    {
                        if (null != carOffer.getCarProduct() && null != carOffer.getCarProduct().getCarInventoryKey()
                                && null != carOffer.getCarProduct().getCarInventoryKey().getDriverAgeYearCount() )
                        {
                            long driverAge = carOffer.getCarProduct().getCarInventoryKey().getDriverAgeYearCount();
                            if (null != request.getSearchStrategy().getDriverAgeYearCount())
                            {
                                if (driverAge != request.getSearchStrategy().getDriverAgeYearCount().longValue())
                                {
                                    errorMessage.append("The response driver age does not equal the requested driverAge.");
                                }
                            }
                            else
                            {
                                if (driverAge != DEFAULT_DRIVER_AGE)
                                {
                                    errorMessage.append("The response driver age does not equal the default of 35");
                                }
                            }
                        }
                    }
                }
            }
        }

        if ( !StringUtils.isEmpty(String.valueOf(errorMessage)) )
        {
            Assert.fail(errorMessage.toString());
        }
    }

    public ProductCategoryCodeList buildProductCategoryCodeListWithTestScenarios(TestScenario scenarios) {
        final ProductCategoryCodeList productCategoryCodeList = new ProductCategoryCodeList();
        if (scenarios.getPurchaseType() == PurchaseType.CarOnly
                || scenarios.getPurchaseType() == PurchaseType.FCBundle || scenarios.getPurchaseType() == PurchaseType.FCPackage
                || scenarios.getPurchaseType() == PurchaseType.FHCBundle || scenarios.getPurchaseType() == PurchaseType.FHCPackage
                || scenarios.getPurchaseType() == PurchaseType.HCBundle || scenarios.getPurchaseType() == PurchaseType.HCPackage) {
            productCategoryCodeList.getProductCategoryCode().add("Car");
        }

        if (scenarios.getPurchaseType() == PurchaseType.HCBundle || scenarios.getPurchaseType() == PurchaseType.HCPackage
                || scenarios.getPurchaseType() == PurchaseType.FHCBundle || scenarios.getPurchaseType() == PurchaseType.FHCPackage) {
            productCategoryCodeList.getProductCategoryCode().add("Hotel");
        }

        if (scenarios.getPurchaseType() == PurchaseType.FCBundle || scenarios.getPurchaseType() == PurchaseType.FCPackage
                || scenarios.getPurchaseType() == PurchaseType.FHCBundle || scenarios.getPurchaseType() == PurchaseType.FHCPackage) {
            productCategoryCodeList.getProductCategoryCode().add("Air");
        }
        return productCategoryCodeList;
    }

    private boolean isProductCategoryCodeListEqual(ProductCategoryCodeList expectProductCategoryCodeList, ProductCategoryCodeList actualProductCategoryCodeList) {
        boolean isEqual = true;
        if (expectProductCategoryCodeList == null && actualProductCategoryCodeList == null) {
            return isEqual;
        } else if ((expectProductCategoryCodeList == null && actualProductCategoryCodeList != null)
                || (expectProductCategoryCodeList != null && actualProductCategoryCodeList == null)) {
            isEqual = false;
        } else if (expectProductCategoryCodeList.getProductCategoryCode().size() != actualProductCategoryCodeList.getProductCategoryCode().size()) {
            isEqual = false;
        } else {
            for (final String expectProductCategoryCode : expectProductCategoryCodeList.getProductCategoryCode()) {
                boolean ifFindExpectProductCategoryCode = false;
                for (final String actualProductCategoryCode : actualProductCategoryCodeList.getProductCategoryCode()) {
                    if (expectProductCategoryCode.equals(actualProductCategoryCode)) {
                        ifFindExpectProductCategoryCode = true;
                    }
                }
                if (!ifFindExpectProductCategoryCode) {
                    isEqual = false;
                    break;
                }
            }
        }

        return isEqual;
    }
}