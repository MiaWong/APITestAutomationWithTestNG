package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;

import java.util.List;

/**
 * Created by v-mechen on 12/28/2017.
 */
public class TotalPriceVerifier {
    private  TotalPriceVerifier(){}
    // Verify ToalPrice
    public static void verifyTotalPriceEqual(CarProductType expCarProduct, CarProductType actCarProduct, String posCurrencyCode,
                                             List remarks, boolean applyFinanceApplicationUnitCount)
    {
        final double expPOS = CostPriceCalculator.getPosTotalPrice(expCarProduct.getPriceList(), posCurrencyCode, applyFinanceApplicationUnitCount);
        final double expPOSu = CostPriceCalculator.getPosuTotalPrice(expCarProduct.getPriceList(), posCurrencyCode, applyFinanceApplicationUnitCount);
        final double actPOS = CostPriceCalculator.getPosTotalPrice(actCarProduct.getPriceList(), posCurrencyCode, applyFinanceApplicationUnitCount);
        final double actPOSu = CostPriceCalculator.getPosuTotalPrice(actCarProduct.getPriceList(), posCurrencyCode, applyFinanceApplicationUnitCount);

        //Compare two total price, test case will fail if they are not identical
        if (expPOS != actPOS || expPOSu != actPOSu)
        {
            remarks.add("Total price is different, expected Total(POS): " + expPOS + "; actual Total(POS): " + actPOS +
                    "; expected Total(POSu): " + expPOSu + ", actual Total(POSu): "+ actPOSu + ". POS currency code:" + posCurrencyCode);
        }
    }

    public static boolean isPriceListTotalAndCostListTotalEqual(CarProductType carProductType) {
        for (final PriceType priceList : carProductType.getPriceList().getPrice()) {
            if (priceList.getFinanceCategoryCode().equals("Total")) {
                for (final CostType costType : carProductType.getCostList().getCost()) {
                    if (costType.getFinanceCategoryCode().equals("Total") && priceList.getMultiplierOrAmount().getCurrencyAmount().getAmount()
                            .getDecimal() == costType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() && priceList.getMultiplierOrAmount()
                            .getCurrencyAmount().getCurrencyCode().equals(costType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

