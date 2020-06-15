package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.v5;

import com.expedia.cars.schema.common.v1.CarOffer;
import com.expedia.cars.schema.common.v1.FinancialDetailsType;
import com.expedia.cars.schema.common.v1.FinancialLineItemListType;
import com.expedia.cars.schema.common.v1.FinancialLineItemType;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import com.expedia.cars.schema.ecommerce.shopping.v1.SearchResult;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARRsp;

import org.testng.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("PMD")
public class VerifyPricingInSearchResponse implements ISearchVerification
{
    public static final String MESSAGE_SUCCESS = "Success";

    @Override
    public VerificationResult verify(V5SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        boolean passed = false;
        final List<String> remarks = new ArrayList<>();

        //TODO: v4 verifier uses CarsInventoryHelper to find CarOffer for given business model
        final CarOffer carOffer = getCarOfferFromResponse(input.getResponse());
        verifyPricingForAgencySearch(remarks, carOffer);

        if (!remarks.isEmpty())
        {
            return new VerificationResult(getName(), false, remarks);
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }

    /**
     * Validate the pricing in the car offer.
     *
     * @param remarks - list of remarks when there is a verification error
     * @param carOffer
     */
    public static void verifyPricingForAgencySearch(List<String> remarks, CarOffer carOffer)
    {
        final FinancialDetailsType pricing = carOffer.getPricing();

        //validate that there is pricing
        if (null == pricing)
        {
            remarks.add("No pricing information found for the car offer.");
            return;
        }

        //validate the grand total is set
        final FinancialLineItemType grandTotal = pricing.getGrandTotal();
        if (null == grandTotal)
        {
            remarks.add("The grand total was not set in pricing.");
        }
        else
        {
            // ensure the grand total is greater than 0
            if (-1 == grandTotal.getAmount().compareTo(BigDecimal.ZERO)
                    || 0 == grandTotal.getAmount().compareTo(BigDecimal.ZERO))
            {
                remarks.add("The grand total must be greater than 0");
            }

            // check that the monetary triplets are set correctly
            if (!grandTotal.getFinanceCategoryCode().equals("Total")
                    || !grandTotal.getFinanceApplicationCode().equals("Trip")
                    || 1 != grandTotal.getFinanceApplicationUnitCount())
            {
                remarks.add("The grand total is expected to have the monetary triplet FinanceCategoryCode:FinanceApplicationCode:FinanceApplicationUnitCount of Total:Trip:1");
            }
        }


    }

    public static void assertSearchResponseForDynamicCommission(CarSearchResponse response, VARRsp varRsp)
        throws Exception
    {
        final List<CarOffer> carOfferList = new ArrayList<>();
        final CarsInventoryDataSource carsInventoryDataSource =
                new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());

        Assert.assertNotNull(response.getSearchResultList(), "The search response is null.");

        for (final SearchResult searchResult : response.getSearchResultList().getSearchResult())
        {
            if (null != searchResult.getCarOfferList())
            {
                for (final CarOffer carOffer : searchResult.getCarOfferList().getCarOffer())
                {
                    if (null != carOffer.getPricing())
                    {
                       if (!isCommissionExistsInPriceList(carOffer.getPricing().getFinancialLineItemList()))
                       {
                           Assert.fail("Pricelist in MN response does not have a commission value.");
                       }
                       //TODO: costlist currently isn't returned; so can't compare total cost to total price
                        // also can't validate the commission cost in the costlist to the scs response costlist
                    }
                }
            }
        }
    }

    private static boolean isCommissionExistsInPriceList(FinancialLineItemListType financialLineItemListType)
    {
        if (null != financialLineItemListType)
        {
            for (final FinancialLineItemType financialLineItem : financialLineItemListType.getFinancialLineItem())
            {

            }
        }

        return true;
    }

    /**
     * Retrieve a CarOffer from the response.  Right now the first offer is retrieved.
     * TODO: retrieve a CarOffer based on some logic.
     *
     * @param response
     * @return
     */
    private CarOffer getCarOfferFromResponse(CarSearchResponse response)
    {
        final CarOffer carOffer =
                response.getSearchResultList().getSearchResult().get(0).getCarOfferList().getCarOffer().get(0);

        return carOffer;
    }
}
