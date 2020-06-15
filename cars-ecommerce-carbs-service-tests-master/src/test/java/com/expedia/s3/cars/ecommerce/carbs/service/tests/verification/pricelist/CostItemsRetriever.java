package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.testng.Assert;

import java.util.*;

/**
 * Created by v-mechen on 12/28/2017.
 */
public class CostItemsRetriever {
    private CostItemsRetriever()
    {}
    //Get cost items from costList for Agency CarBS search
    public static Map<String, Double> getCostItemsFromCostListForAgencyAndGDSPCarBSSearch(String requestCurrency,
                                                                                                 String vendorCurrency, CarProductType selectedCar)
    {
        final Map<String, Double> costValueMap = new HashMap<>();

        //Base
        costValueMap.put("BasePOS", CostPriceCalculator.getCostAmountByFinanceCategoryCode(selectedCar.getCostList(),
                requestCurrency, "Base", 4, null, null, null, false ));
        costValueMap.put("BaseRateCount", (double)CostPriceCalculator.getCostFinanceApplicationUnitCountByFinanceCategoryCode(
                selectedCar.getCostList(), requestCurrency, "Base", null, null, null ));
        //Fee
        handleFeeForAgencyGDSPSearch(selectedCar.getCostList(),requestCurrency, costValueMap, "FeePOS");

        //Total
        costValueMap.put("TotalPOS", CostPriceCalculator.getCostAmountByFinanceCategoryCode(selectedCar.getCostList(),
                requestCurrency, "Total", 4, null, null, null, false ));

        //Get POSu items exchange rates change
        if (!requestCurrency.equals(vendorCurrency))
        {
            //Base
            costValueMap.put("BasePOSu", CostPriceCalculator.getCostAmountByFinanceCategoryCode(selectedCar.getCostList(),
                    vendorCurrency, "Base", 4, null, null, null, false ));
            //Fee
            handleFeeForAgencyGDSPSearch(selectedCar.getCostList(), vendorCurrency, costValueMap, "FeePOSu");

            //Total
            costValueMap.put("TotalPOSu", CostPriceCalculator.getCostAmountByFinanceCategoryCode(selectedCar.getCostList(),
                    vendorCurrency, "Total", 4, null, null, null, false ));
        }
        return costValueMap;
    }

    private static void handleFeeForAgencyGDSPSearch(CostListType costList, String currencyCode, Map<String, Double> costValueMap, String feeKeyInMap)
    {
        final List<CostType> feeCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList, currencyCode, "Fee", null,
                "Merchant Car Expedia Cancellation Fee");
        for(final CostType feeCost : feeCostList) {
            if (feeCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() > 0) {
                if (feeCost.getDescriptionRawText().toUpperCase(Locale.US).contains("DROP") ||
                        feeCost.getDescriptionRawText().toUpperCase(Locale.US).contains("ONE WAY CHARGE"))
                {
                    costValueMap.put(feeKeyInMap, CostPriceCalculator.calculateCostAmount(feeCost, 4, false));
                }
                else
                {
                    //It will throw a exceptioni when there are multi fee, like:TP - THEFT PROTECTION, CDW - COLLISION DAMAGE WAIVER - Edit by Qiuhua
                    final String des = "_" + feeCost.getDescriptionRawText().split("-")[0].trim();
                    costValueMap.put("FeePOS" + des, CostPriceCalculator.calculateCostAmount(feeCost, 4, false));
                }
            }
        }
    }

    private static double handleBaseForAgencyDetails(CostListType costList, String currencyCode, Map<String, Double> costValueMap, String keyInMap, double sumBasePOS)
    {
        double newSum = sumBasePOS;
        final List<CostType> baseCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Base", null, null);
        for(final CostType cost: baseCostList)
        {
            costValueMap.put(keyInMap + cost.getFinanceApplicationCode(), CostPriceCalculator.calculateCostAmount(cost, 4, false));
            newSum += CostPriceCalculator.calculateCostAmount(cost, 4, true);
            if (!cost.getFinanceApplicationCode().contains("Extra"))
            {
                //Add BasePOS which is not Extra FinanceApplicationCode
                costValueMap.put(keyInMap, CostPriceCalculator.calculateCostAmount(cost, 4, false));
            }
        }
        return newSum;
    }

    //Include all Taxes/Fee/Surcharge
    private static double handleTaxesForAgencyDetails(CostListType costList, String currencyCode, Map<String, Double> costValueMap, String keyInMap,
                                                     double sumTaxesPOS, List remarks)
    {
        //Get Taxes, set DescriptionRawText of the taxes as key
        double newSum = sumTaxesPOS;
        final List<CostType> taxCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Taxes", null,null);
        for(final CostType cost: taxCostList)
        {
            costValueMap.put(keyInMap + cost.getDescriptionRawText(), CostPriceCalculator.calculateCostAmount(cost, 4, false));
            newSum += CostPriceCalculator.calculateCostAmount(cost, 4, false);
        }

        //Fee set TexesPOS + DescriptionRawText of the taxes as key, because Taxes and Fee are all taken as MandatoryCharge in PriceList
        final List<CostType> feeCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Fee", null,null);
        for(final CostType cost: feeCostList)
        {
            newSum += CostPriceCalculator.calculateCostAmount(cost, 4, false);
            if (null == costValueMap.get(keyInMap + cost.getDescriptionRawText()))
            {
                costValueMap.put(keyInMap + cost.getDescriptionRawText(), CostPriceCalculator.calculateCostAmount(cost, 4, false));
                //add for the one way charge in Fee
                if (cost.getDescriptionRawText().contains("DROP CHARGE") ||cost.getDescriptionRawText().contains("ONE WAY CHARGE"))
                {
                    final String key = keyInMap.contains("POSu") ? "DropOffChargePOSu" : "DropOffChargePOS";
                    costValueMap.put(key, CostPriceCalculator.calculateCostAmount(cost, 4, false));
                }
            }
            else
            {
                //Dupliacated Fee returned in Cost List
                if (CostPriceCalculator.calculateCostAmount(cost, 4, false) == 0)
                {
                    // [Bug397465 clodes by design: GDS returned 0 charged and duplicated Other Fee]:
                    // add a cost in map , but don't verify the charge in price list
                    costValueMap.put(keyInMap + cost.getDescriptionRawText() + "1", 0d);
                }
                else
                {
                    remarks.add("Dupliacated Fee returned in cost list and the charge is " + CostPriceCalculator.calculateCostAmount(cost, 4, false));
                }
            }
        }

        //Surcharge
        final List<CostType> surchargeCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Surcharge", null,null);
        for(final CostType cost: surchargeCostList)
        {
            costValueMap.put(keyInMap + cost.getDescriptionRawText(), CostPriceCalculator.calculateCostAmount(cost, 4, false));
            newSum += CostPriceCalculator.calculateCostAmount(cost, 4, false);
        }
        return newSum;
    }

    //Get cost items from costList for Agency CarBS search
    //Egenica preparePurchase doesn't have EstimatedTotalCostToCustomer
    public static Map<String, Double> getCostItemsFromCostListForAgencyGetDetail(String requestCurrency, String vendorCurrency,
                                                                                        CostListType costList, boolean ignoreEstimatedTotalCostToCustomer, List remarks)
    {
        final Map<String, Double> costValueMap = new HashMap<>();
        Double sumTaxesPOS = 0d;
        Double sumTaxesPOSu = 0d;
        Double sumBasePOS = 0d;
        Double sumBasePOSu = 0d;

        //Base
        sumBasePOS = handleBaseForAgencyDetails(costList, requestCurrency, costValueMap, "BasePOS", sumBasePOS);

        //Taxes, include all Taxes/Fee/Surcharge
        sumTaxesPOS = handleTaxesForAgencyDetails(costList, requestCurrency, costValueMap, "TaxesPOS", sumTaxesPOS, remarks);

        //Total
        costValueMap.put("TotalPOS", CostPriceCalculator.getPosTotalCost(costList, requestCurrency));
        if(!ignoreEstimatedTotalCostToCustomer)
        {
            costValueMap.put("EstimatedTotalCostToCustomerPOS", CostPriceCalculator.getPosTotalCost(costList, requestCurrency));
        }

        //Get Misc in costList
        sumTaxesPOS += CostPriceCalculator.getCostAmountByFinanceCategoryCode(costList, requestCurrency, "Misc", 4, null, null, null, false);

        //Set sum of taxesPOS to costValueMap
        if (sumTaxesPOS != 0) {
            costValueMap.put("MiscPOS", sumTaxesPOS);
        }
        //Set sum of baseTotalPOS
        costValueMap.put("BaseRateTotalPOS", sumBasePOS);

        //Do not have exhchange rates change, POSu keys
        if (!requestCurrency.equals(vendorCurrency)) {
            //Base
            sumBasePOSu = handleBaseForAgencyDetails(costList, vendorCurrency, costValueMap, "BasePOSu", sumBasePOSu);

            //Taxes, include all Taxes/Fee/Surcharge
            sumTaxesPOSu = handleTaxesForAgencyDetails(costList, vendorCurrency, costValueMap, "TaxesPOSu", sumTaxesPOSu, remarks);

            //Total
            costValueMap.put("TotalPOSu", CostPriceCalculator.getPosTotalCost(costList, vendorCurrency));
            if(!ignoreEstimatedTotalCostToCustomer)
            {
                costValueMap.put("EstimatedTotalCostToCustomerPOSu", CostPriceCalculator.getPosTotalCost(costList, vendorCurrency));
            }

            //Get Misc in costList
            sumTaxesPOSu += CostPriceCalculator.getCostAmountByFinanceCategoryCode(costList, vendorCurrency, "Misc", 4, null, null, null, false);

            //Set sum of taxesPOSu to costValueMap
            if (sumTaxesPOSu != 0) {
                costValueMap.put("MiscPOSu", sumTaxesPOSu);
            }
            //Set sum of baseTotalPOSu
            costValueMap.put("BaseRateTotalPOSu", sumBasePOSu);
        }

        return costValueMap;
    }

    private static double handleBaseForGDSPDetails(CostListType costList, String currencyCode,  double sumBasePOS)
    {
        double newSum = sumBasePOS;
        final List<CostType> baseCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Base", null, null);
        for(final CostType cost: baseCostList)
        {
            newSum += CostPriceCalculator.calculateCostAmount(cost, 4, true);
        }

        final List<CostType> miscBaseCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "MiscBase", null, null);
        for(final CostType cost: miscBaseCostList)
        {
            newSum += CostPriceCalculator.calculateCostAmount(cost, 4, true);
        }

        return newSum;
    }

    //Include all Taxes/Fee/Surcharge
    private static double handleTaxesForGDSPDetails(CostListType costList, String currencyCode, Map<String, Double> costValueMap, String keyInMap,
                                                   double sumTaxesPOS)
    {
        //Get Taxes, set DescriptionRawText of the taxes as key
        double newTaxes = sumTaxesPOS;
        final List<CostType> taxCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Taxes", null,null);
        for(final CostType cost: taxCostList)
        {
            costValueMap.put(keyInMap + cost.getDescriptionRawText() + cost.getFinanceApplicationCode(), CostPriceCalculator.calculateCostAmount(cost, 4, false));
            newTaxes += CostPriceCalculator.calculateCostAmount(cost, 4, false);
        }

        //Fee
        final List<CostType> feeCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList,currencyCode, "Fee", null,null);
        for(final CostType cost: feeCostList)
        {
            newTaxes += CostPriceCalculator.calculateCostAmount(cost, 4, false);
            if (null == costValueMap.get(keyInMap + cost.getDescriptionRawText()))
            {
                costValueMap.put(keyInMap + cost.getDescriptionRawText(), CostPriceCalculator.calculateCostAmount(cost, 4, false));

                // add for the one way charg in Fee
                if (cost.getDescriptionRawText().contains("DROP CHARGE"))
                {
                    costValueMap.put(keyInMap + "DropOffChargeNotAddedToTotal", CostPriceCalculator.calculateCostAmount(cost, 4, false));

                }
            }
            else if (cost.getDescriptionRawText().contains("Merchant Car Expedia Cancellation Fee") && CostPriceCalculator.calculateCostAmount(cost, 4, false) == 0)
            {
                continue;
            }
            else
            {
                costValueMap.put(keyInMap + cost.getDescriptionRawText() + cost.getFinanceApplicationCode(), CostPriceCalculator.calculateCostAmount(cost, 4, false));
            }
        }

        return newTaxes;
    }

    //Get cost items from costList for GDSP CarBS GetDetail
    public static Map<String, Double> getCostItemsFromCostListForGDSPGetDetail(String requestCurrency,
                                                                                      String vendorCurrency, CostListType costList)
    {
        final Map<String, Double> costValueMap = new HashMap<>();
        Double sumTaxesPOS = 0d;
        Double sumTaxesPOSu = 0d;
        Double sumBasePOS = 0d;
        Double sumBasePOSu = 0d;

        //Base
        sumBasePOS = handleBaseForGDSPDetails(costList, requestCurrency, sumBasePOS);

        //Taxes
        sumTaxesPOS = handleTaxesForGDSPDetails(costList, requestCurrency, costValueMap, "POS", sumTaxesPOS);

        costValueMap.put("POSTotal", CostPriceCalculator.getPosTotalCost(costList, requestCurrency));
        costValueMap.put("POSEstimated total cost to customer including mandatory charges payable at the counter.", CostPriceCalculator.getPosTotalCost(costList, requestCurrency));

        //Get Misc in costList
        sumBasePOS += CostPriceCalculator.getCostAmountByFinanceCategoryCode(costList, requestCurrency, "Misc", 4, null, null, null, false);

        //Set sum of taxesPOS to costValueMap
        if (sumTaxesPOS > 0)
        {
            costValueMap.put("POSMisc", sumTaxesPOS);
        }
        //Set sum of baseTotalPOS
        costValueMap.put("POSBase", sumBasePOS + sumTaxesPOS);

        if(!(requestCurrency.equals(vendorCurrency)))
        {
            //Base
            sumBasePOSu = handleBaseForGDSPDetails(costList, vendorCurrency, sumBasePOSu);

            //Taxes
            sumTaxesPOSu = handleTaxesForGDSPDetails(costList, vendorCurrency, costValueMap, "POSu", sumTaxesPOSu);

            costValueMap.put("POSuTotal", CostPriceCalculator.getPosTotalCost(costList, vendorCurrency));
            costValueMap.put("POSuEstimated total cost to customer including mandatory charges payable at the counter.", CostPriceCalculator.getPosTotalCost(costList, vendorCurrency));

            //Get Misc in costList
            sumBasePOSu += CostPriceCalculator.getCostAmountByFinanceCategoryCode(costList, vendorCurrency, "Misc", 4, null, null, null, false);

            //Set sum of taxesPOSu to costValueMap
            if (sumTaxesPOSu > 0)
            {
                costValueMap.put("POSuMisc", sumTaxesPOSu);
            }
            //Set sum of baseTotalPOSu
            costValueMap.put("POSuBase", sumBasePOSu + sumTaxesPOSu);
        }

        return costValueMap;
    }

    //Get cost items from costList for GDSP CarBS GetDetail
    public static Map<String, Double> getCostItemsFromCostListForTitaniumGDSPGetDetail(String requestCurrency,
                                                                                      String vendorCurrency, CostListType costList)
    {
        final Map<String, Double> costValueMap = new HashMap<>();
        Double sumBasePOS = 0d;
        Double sumBasePOSu = 0d;

        //Base
        sumBasePOS = handleBaseForGDSPDetails(costList, requestCurrency, sumBasePOS);
        costValueMap.put("POSTotal", CostPriceCalculator.getPosTotalCost(costList, requestCurrency));

        //Set sum of baseTotalPOS
        costValueMap.put("POSBase", sumBasePOS);

        //Fee
        dropOffChargeFee(requestCurrency, costList, costValueMap, "POS");


        if(!(requestCurrency.equals(vendorCurrency)))
        {
            //Base
            sumBasePOSu = handleBaseForGDSPDetails(costList, vendorCurrency, sumBasePOSu);
            costValueMap.put("POSuTotal", CostPriceCalculator.getPosTotalCost(costList, vendorCurrency));

            //Set sum of baseTotalPOSu
            costValueMap.put("POSuBase", sumBasePOSu);
            //Fee
            dropOffChargeFee(requestCurrency, costList, costValueMap, "POSu");
        }

        return costValueMap;
    }

    private static void dropOffChargeFee(String requestCurrency, CostListType costList, Map<String, Double> costValueMap, String keyInMap)
    {
        final List<CostType> feeCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(costList, requestCurrency, "Fee", null,null);
        for(final CostType cost: feeCostList)
        {
            final double newTaxes = CostPriceCalculator.calculateCostAmount(cost, 4, false);
            if (null == costValueMap.get(keyInMap + cost.getDescriptionRawText()))
            {
                // add for the one way charg in Fee
                if (cost.getDescriptionRawText().contains("DROP CHARGE"))
                {
                    costValueMap.put(keyInMap + "DropOffChargeNotAddedToTotal", newTaxes);
                }
            }

       }
    }

    public static boolean verifyCommissionInCostList(CarProductType carProduct, CarProductListType gdsProductList) {
        for (final CarProductType carProductGDS : gdsProductList.getCarProduct()) {
            if (CarProductComparator.isCorrespondingCar(carProduct, carProductGDS, false, false)) {
                //verify CostList for MNSCS response and MN response
                if (!isCommissionValueEqual(carProduct, carProductGDS)) {
                    Assert.fail("Commission of MN response is not identical with Commission of MNSCS response: ");
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isCommissionValueEqual(CarProductType actualCarProduct, CarProductType expectedCarProduct) {
        for (final CostType actualCost : actualCarProduct.getCostList().getCost()) {
            if (actualCost.getFinanceCategoryCode().equals("Commission") && CarNodeComparator.isCostListEqualSmallLoop(actualCost, expectedCarProduct.getCostList(), Collections.emptyList())) {
                return true;
            }
        }
        return false;
    }

}
