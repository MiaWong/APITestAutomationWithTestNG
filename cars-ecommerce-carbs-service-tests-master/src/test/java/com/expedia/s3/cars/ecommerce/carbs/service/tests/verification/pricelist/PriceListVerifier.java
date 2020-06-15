package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist;

import com.expedia.e3.data.cartypes.defn.v5.CarMarkupRuleInfoType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.PriceListType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarCommission;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by v-mechen on 12/26/2017.
 */
@SuppressWarnings("PMD")
public class PriceListVerifier {

    private PriceListVerifier(){}

    //Verify price is correct for Search
    public static void verifyPriceListForAgencySearch(Map<String, Double> costItemsMap, CarProductType selectedCarProduct, String requestCurrency,
            String vendorCurrency, List remarks)
    {
        final double priceBaseRateTotal = costItemsMap.get("BasePOS")*costItemsMap.get("BaseRateCount");
        costItemsMap.put("BaseRateTotalPOS", priceBaseRateTotal);

        //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
        verifyPriceItem(costItemsMap, "BasePOS", selectedCarProduct.getPriceList(), "Base", requestCurrency, remarks, null, false);

        //BaseRateTotal
        verifyPriceItem(costItemsMap, "BaseRateTotalPOS", selectedCarProduct.getPriceList(), "BaseRateTotal", requestCurrency, remarks, null, false);

        //DropOffCharge
        verifyPriceItem(costItemsMap, "FeePOS", selectedCarProduct.getPriceList(), "DropOffCharge", requestCurrency, remarks, null, false);

        //OneWayFeeCharge
        verifyPriceItem(costItemsMap, "FeePOS", selectedCarProduct.getPriceList(), "Fee", requestCurrency, remarks, "ONE WAY CHARGE", false);

        //MandatoryCharges in Fee
        verifyPriceItem(costItemsMap, "FeePOS", selectedCarProduct.getPriceList(), "TotalMandatoryCharges", requestCurrency, remarks, null, false);

        //Total
        verifyPriceItem(costItemsMap, "TotalPOS", selectedCarProduct.getPriceList(), "Total", requestCurrency, remarks, null, false);
        verifyPriceItem(costItemsMap, "TotalPOS", selectedCarProduct.getPriceList(), "EstimatedTotalCostToCustomer", requestCurrency, remarks, null, false);

        //BaseRateBreakDown
        verifyPriceItem(costItemsMap, "BasePOS", selectedCarProduct.getPriceList(), "BaseRateBreakDown", requestCurrency, remarks, null, false);

        //Base and total is not negative
        verifyBaseAndTotalPriceInPriceListAreNonNegative(selectedCarProduct.getPriceList(), requestCurrency, remarks);

        if(!requestCurrency.equals(vendorCurrency))
        {
            final double priceBaseRateTotalu = costItemsMap.get("BasePOSu")*costItemsMap.get("BaseRateCount");
            costItemsMap.put("BaseRateTotalPOSu", priceBaseRateTotalu);

            verifyPriceItem(costItemsMap, "BasePOSu", selectedCarProduct.getPriceList(), "Base", vendorCurrency, remarks, null, false);

            verifyPriceItem(costItemsMap, "BaseRateTotalPOSu", selectedCarProduct.getPriceList(), "BaseRateTotal", vendorCurrency, remarks, null, false);

            //DropOffCharge
            verifyPriceItem(costItemsMap, "FeePOSu", selectedCarProduct.getPriceList(), "DropOffCharge", vendorCurrency, remarks, null, false);

            //OneWayFeeCharge
            verifyPriceItem(costItemsMap, "FeePOSu", selectedCarProduct.getPriceList(), "Fee", vendorCurrency, remarks, "ONE WAY CHARGE", false);

            //MandatoryCharges in Fee
            verifyPriceItem(costItemsMap, "FeePOSu", selectedCarProduct.getPriceList(), "TotalMandatoryCharges", vendorCurrency, remarks, null, false);

            //Total
            verifyPriceItem(costItemsMap, "TotalPOSu", selectedCarProduct.getPriceList(), "Total", vendorCurrency, remarks, null, false);
            verifyPriceItem(costItemsMap, "TotalPOSu", selectedCarProduct.getPriceList(), "EstimatedTotalCostToCustomer", vendorCurrency, remarks, null, false);

            //BaseRateBreakDown
            verifyPriceItem(costItemsMap, "BasePOSu", selectedCarProduct.getPriceList(), "BaseRateBreakDown", vendorCurrency, remarks, null, false);

            //Base and total is not negative
            verifyBaseAndTotalPriceInPriceListAreNonNegative(selectedCarProduct.getPriceList(), vendorCurrency, remarks);

        }
    }

    public static void verifyPriceListForGDSPSearch(Map<String, Double> costItemsMap, CarProductType selectedCarProduct, String requestCurrency,
            String vendorCurrency, List remarks) throws DataAccessException
    {
        double markup = 0;
        final long vendorSupplierID = selectedCarProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

        //Get the VendorCollectsFlag for EU GDSP one way Dropoff charge from DB
        final String vendorCollectsFlag = carsInventoryHelper.getVendorCollectsFlagGDSPOneWayDropOffCharge(vendorSupplierID);
        CarCommission carCommission = carsInventoryHelper.getCommissionInfoByCarCatalogKey(selectedCarProduct.getCarInventoryKey());

       //final boolean commissionApplied = isCarCommissionApplied(selectedCarProduct);

        if (null == carCommission || carCommission.getCommissionPct().compareTo(new BigDecimal(0)) == 0)
        {
            //If it's a GDSP car with markup, get the markup from CarProduct
            final int amount = selectedCarProduct.getCarMarkupInfo().getAppliedMarkupRate().getDecimal();
            final long decimalCount = selectedCarProduct.getCarMarkupInfo().getAppliedMarkupRate().getDecimalPlaceCount();
            markup = amount / Math.pow(10, decimalCount);
        }

        if (requestCurrency.equals(vendorCurrency))
        {
            //Calculate expected price value basic on cost items from costList
            double priceBasePOS = costItemsMap.get("TotalPOS");
            double priceTotalPOS = costItemsMap.get("TotalPOS");

//            if ((costItemsMap.get("FeePOS") != null) && ("1".equals(vendorCollectsFlag) || "True".equalsIgnoreCase(vendorCollectsFlag)))
//            {
//                final double priceDropOffChargePOS = costItemsMap.get("FeePOS");
//
//                priceBasePOS = (priceBasePOS - priceDropOffChargePOS) * (1 + markup);
//                priceTotalPOS = (priceTotalPOS - priceDropOffChargePOS) * (1 + markup);
//            }
//            else
//            {
//                priceBasePOS = priceBasePOS * (1 + markup);
//                priceTotalPOS = priceTotalPOS * (1 + markup);
//            }

            if ((costItemsMap.get("FeePOS") != null) && ("1".equals(vendorCollectsFlag) || "True".equalsIgnoreCase(vendorCollectsFlag)))
            {
                final double priceDropOffChargePOS = costItemsMap.get("FeePOS");

                priceBasePOS = (priceBasePOS - priceDropOffChargePOS);
                priceTotalPOS = (priceTotalPOS - priceDropOffChargePOS);
            }

            if (null == carCommission || carCommission.getCommissionPct().compareTo(new BigDecimal(0)) == 0)
            {
                //Add markup for GDS net rate cars
                priceBasePOS = priceBasePOS * (1 + markup);
                priceTotalPOS = priceTotalPOS * (1 + markup);
            }

            costItemsMap.put("BasePOS", priceBasePOS);
            costItemsMap.put("TotalPOS", priceTotalPOS);
        }
        else
        {
            //Calculate expected price value basic on cost items from costList
            double priceBasePOS = costItemsMap.get("TotalPOS");
            double priceTotalPOS = costItemsMap.get("TotalPOS");
            double priceBasePOSu = costItemsMap.get("TotalPOSu");
            double priceTotalPOSu = costItemsMap.get("TotalPOSu");

            if (costItemsMap.get("FeePOS") != null)
            {
                final double priceDropOffChargePOS = costItemsMap.get("FeePOS");
                //if VendorCollectsFlag=1, priceBase and priceTotal should be: TotalPOS - FeePOS
                if ("1".equals(vendorCollectsFlag) || "True".equalsIgnoreCase(vendorCollectsFlag))
                {
                    priceBasePOS = priceBasePOS - priceDropOffChargePOS;
                    priceTotalPOS = priceTotalPOS - priceDropOffChargePOS;
                }
            }

            if (costItemsMap.get("FeePOSu") != null)
            {
                final double priceDropOffChargePOSu = costItemsMap.get("FeePOSu");
                if ("1".equals(vendorCollectsFlag)|| "True".equalsIgnoreCase(vendorCollectsFlag))
                {
                    priceBasePOSu = priceBasePOSu - priceDropOffChargePOSu;
                    priceTotalPOSu = priceTotalPOSu - priceDropOffChargePOSu;
                }
            }

            if (null == carCommission || carCommission.getCommissionPct().compareTo(new BigDecimal(0)) == 0)
            {
                //Add markup for GDS net rate cars
                priceBasePOS = priceBasePOS * (1 + markup);
                priceTotalPOS = priceTotalPOS * (1 + markup);
                priceBasePOSu = priceBasePOSu * (1 + markup);
                priceTotalPOSu = priceTotalPOSu * (1 + markup);
            }

            costItemsMap.put("BasePOS", priceBasePOS);
            costItemsMap.put("TotalPOS", priceTotalPOS);
            costItemsMap.put("BasePOSu", priceBasePOSu);
            costItemsMap.put("TotalPOSu", priceTotalPOSu);
        }

        //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
        verifyPriceItem(costItemsMap, "BasePOS", selectedCarProduct.getPriceList(), "Base", requestCurrency, remarks, null, false);

        //Total
        verifyPriceItem(costItemsMap, "TotalPOS", selectedCarProduct.getPriceList(), "Total", requestCurrency, remarks, null, false);

        //DropOffCharge
        verifyPriceItem(costItemsMap, "FeePOS", selectedCarProduct.getPriceList(), "DropOffCharge", requestCurrency, remarks, null, false);

        //DropOffChargeNotAddedToTotal
        verifyPriceItem(costItemsMap, "FeePOS", selectedCarProduct.getPriceList(), "DropOffChargeNotAddedToTotal", requestCurrency, remarks, null, false);

        //TotalMandatoryCharges
        verifyPriceItem(costItemsMap, "FeePOS", selectedCarProduct.getPriceList(), "TotalMandatoryCharges", requestCurrency, remarks, null, false);

//        //Total
//        verifyPriceItem(costItemsMap, "TotalPOS", selectedCarProduct.getPriceList(), "EstimatedTotalCostToCustomer", requestCurrency, remarks, null, false);

        //Base and total is not negative
        verifyBaseAndTotalPriceInPriceListAreNonNegative(selectedCarProduct.getPriceList(), requestCurrency, remarks);

        //At least 2 items(Base and Total) exist in PriceList, if Fee in costList is zero
        if ( costItemsMap.get("FeePOS") == null  && selectedCarProduct.getPriceList().getPrice().size() < 2)
        {
            remarks.add("At least 2 items Base and Total should exist in PriceList when Fee in CostList is zero!");
        }
        //At least 3 items(Base and Total, DropOffCharge or TotalMandatoryCharges or MandatoryCharge) exist in PriceList,
        //if Fee in costList is not zero
        else if (costItemsMap.get("FeePOS") != null && selectedCarProduct.getPriceList().getPrice().size() < 3)
        {
            remarks.add("At least 3 items Base and Total, DropOffCharge or TotalMandatoryCharges or MandatoryCharge should exist in PriceList when Fee in CostList is not zero!");
        }

        if(!requestCurrency.equals(vendorCurrency))
        {
            verifyPriceItem(costItemsMap, "BasePOSu", selectedCarProduct.getPriceList(), "Base", vendorCurrency, remarks, null, false);

            //Total
            verifyPriceItem(costItemsMap, "TotalPOSu", selectedCarProduct.getPriceList(), "Total", vendorCurrency, remarks, null, false);

            //DropOffCharge
            verifyPriceItem(costItemsMap, "FeePOSu", selectedCarProduct.getPriceList(), "DropOffCharge", vendorCurrency, remarks, null, false);

            //DropOffChargeNotAddedToTotal
            verifyPriceItem(costItemsMap, "FeePOSu", selectedCarProduct.getPriceList(), "DropOffChargeNotAddedToTotal", vendorCurrency, remarks, null, false);

            //TotalMandatoryCharges
            verifyPriceItem(costItemsMap, "FeePOSu", selectedCarProduct.getPriceList(), "TotalMandatoryCharges", vendorCurrency, remarks, null, false);

//            //Total
//            verifyPriceItem(costItemsMap, "TotalPOSu", selectedCarProduct.getPriceList(), "EstimatedTotalCostToCustomer", vendorCurrency, remarks, null, false);

            //Base and total is not negative
            verifyBaseAndTotalPriceInPriceListAreNonNegative(selectedCarProduct.getPriceList(), vendorCurrency, remarks);

            //At least 4 items(Base and Total) exist in PriceList, if Fee in costList is zero
            if ( costItemsMap.get("FeePOSu") == null  && selectedCarProduct.getPriceList().getPrice().size() < 4)
            {
                remarks.add("At least 4 items POS and POSu for Base and Total should exist in PriceList when Fee in CostList is zero!");
            }
            //At least 6 items(Base and Total, DropOffCharge or TotalMandatoryCharges or MandatoryCharge) exist in PriceList,
            //if Fee in costList is not zero
            else if (costItemsMap.get("FeePOSu") != null && selectedCarProduct.getPriceList().getPrice().size() < 6)
            {
                remarks.add("At least 6 items POS and POSu for Base and Total, DropOffCharge or TotalMandatoryCharges or MandatoryCharge should exist in PriceList when Fee in CostList is not zero!");
            }
        }
    }

    //Verify price is correct with no exchange rates change for Agency GetDetail
    public static void verifyPriceListForAgencyGetDetail(Map<String, Double> costItemsMap, CarProductType getDetailCarProduct, String requestCurrency,
                                                     String vendorCurrency, List remarks)
    {
        if (costItemsMap.size() != getDetailCarProduct.getPriceList().getPrice().size())
        {
            remarks.add("Price list count is not expected, expected " + costItemsMap.size() + ", actual " +
                    getDetailCarProduct.getPriceList().getPrice().size());
        }

        //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
        verifyPriceItem(costItemsMap, "BasePOS", getDetailCarProduct.getPriceList(), "Base", requestCurrency, remarks, null, false);

        //BaseRateBreakDown
        verifyPriceItemListByFinanceApplicationCode(costItemsMap, "BasePOS", getDetailCarProduct.getPriceList(), "BaseRateBreakDown", requestCurrency, remarks, false);

        //MandatoryCharge
        verifyPriceItemListByDescriptionRawText(costItemsMap, "TaxesPOS", getDetailCarProduct.getPriceList(), "MandatoryCharge", requestCurrency, remarks, false);

        //DropOffCharge
        verifyPriceItem(costItemsMap, "DropOffChargePOS", getDetailCarProduct.getPriceList(), "DropOffCharge", requestCurrency, remarks, null, false);

        //DropOffCharge in Fee
        verifyPriceItem(costItemsMap, "DropOffChargePOS", getDetailCarProduct.getPriceList(), "MandatoryCharge", requestCurrency, remarks, "ONE WAY CHARGE", false);

        //Misc - We don't care Misc in PriceList - bug 395697
        //verifyPriceItem(costItemsMap, "MiscPOS", getDetailCarProduct.getPriceList(), "Misc", requestCurrency, remarks, null, false);

        //Total
        verifyPriceItem(costItemsMap, "TotalPOS", getDetailCarProduct.getPriceList(), "Total", requestCurrency, remarks, null, false);
        verifyPriceItem(costItemsMap, "TotalPOS", getDetailCarProduct.getPriceList(), "EstimatedTotalCostToCustomer", requestCurrency, remarks, null, false);

        //BaseRateTotal
        verifyPriceItem(costItemsMap, "BaseRateTotalPOS", getDetailCarProduct.getPriceList(), "BaseRateTotal", requestCurrency, remarks, null, false);

        //Base and total is not negative
        verifyBaseAndTotalPriceInPriceListAreNonNegative(getDetailCarProduct.getPriceList(), requestCurrency, remarks);

        if(!requestCurrency.equals(vendorCurrency))
        {
            //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
            verifyPriceItem(costItemsMap, "BasePOSu", getDetailCarProduct.getPriceList(), "Base", vendorCurrency, remarks, null, false);

            //BaseRateBreakDown
            verifyPriceItemListByFinanceApplicationCode(costItemsMap, "BasePOSu", getDetailCarProduct.getPriceList(), "BaseRateBreakDown", vendorCurrency, remarks, false);

            //MandatoryCharge
            verifyPriceItemListByDescriptionRawText(costItemsMap, "TaxesPOSu", getDetailCarProduct.getPriceList(), "MandatoryCharge", vendorCurrency, remarks, false);

            //DropOffCharge
            verifyPriceItem(costItemsMap, "DropOffChargePOSu", getDetailCarProduct.getPriceList(), "DropOffCharge", vendorCurrency, remarks, null, false);

            //DropOffCharge in Fee
            verifyPriceItem(costItemsMap, "DropOffChargePOSu", getDetailCarProduct.getPriceList(), "Fee", vendorCurrency, remarks, "ONE WAY CHARGE", false);

            //Misc - We don't care Misc in PriceList - bug 395697
            //verifyPriceItem(costItemsMap, "MiscPOSu", getDetailCarProduct.getPriceList(), "Misc", vendorCurrency, remarks, null, false);

            //Total
            verifyPriceItem(costItemsMap, "TotalPOSu", getDetailCarProduct.getPriceList(), "Total", vendorCurrency, remarks, null, false);
            verifyPriceItem(costItemsMap, "TotalPOSu", getDetailCarProduct.getPriceList(), "EstimatedTotalCostToCustomer", vendorCurrency, remarks, null, false);

            //BaseRateTotal
            verifyPriceItem(costItemsMap, "BaseRateTotalPOSu", getDetailCarProduct.getPriceList(), "BaseRateTotal", vendorCurrency, remarks, null, false);

            verifyBaseAndTotalPriceInPriceListAreNonNegative(getDetailCarProduct.getPriceList(), vendorCurrency, remarks);
        }
    }

    public static void verifyPriceListForGDSPGetDetail(Map<String, Double> costItemsMap, CarProductType getDetailCarProduct, String requestCurrency,
            String vendorCurrency, List remarks) throws DataAccessException
    {
        final long vendorSupplierID = getDetailCarProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

        //Get the VendorCollectsFlag for EU GDSP one way Dropoff charge from DB
        final String vendorCollectsFlag = carsInventoryHelper.getVendorCollectsFlagGDSPOneWayDropOffCharge(vendorSupplierID);
        final boolean commissionApplied = isCarCommissionApplied(getDetailCarProduct);

        if (!commissionApplied)
        {
            double markupMin = 0;
            double markupMax = 0;

            //If it's a GDSP car with markup, get the markup from CarProduct
            for(final CarMarkupRuleInfoType carMarkupRuleInfo : getDetailCarProduct.getCarMarkupInfo().getAppliedMarkupRuleList().getCarMarkupRuleInfo())
            {
                final long amount = carMarkupRuleInfo.getCarMarkupRate().getDecimal();
                final long decimalCount = carMarkupRuleInfo.getCarMarkupRate().getDecimalPlaceCount();

                if(carMarkupRuleInfo.getCarMarkupRuleCategoryID() == 1 || carMarkupRuleInfo.getCarMarkupRuleCategoryID() == 2)
                {
                    markupMax = markupMax + amount / Math.pow(10, decimalCount);
                } else if (carMarkupRuleInfo.getCarMarkupRuleCategoryID() == 3)
                {
                    markupMin = amount / Math.pow(10, decimalCount);
                }
            }

            final int appliedMarkupAmount = getDetailCarProduct.getCarMarkupInfo().getAppliedMarkupRate().getDecimal();
            final long appliedMarkupDecimalCount = getDetailCarProduct.getCarMarkupInfo().getAppliedMarkupRate().getDecimalPlaceCount();
            final double appliedMarkupPct = appliedMarkupAmount / Math.pow(10, appliedMarkupDecimalCount);

            if (markupMax == 0 )
            {
                markupMax = markupMin;
            }

            if (markupMin > markupMax)
            {
                final double markup = markupMax;
                markupMax = markupMin;
                markupMin = markup;
            }

            if (costItemsMap.get("POSDropOffChargeNotAddedToTotal") != null)
            {
                handleCostItemsMap(costItemsMap, requestCurrency, vendorCurrency, vendorCollectsFlag);
            }

            costItemsMap.put("POSMaxMarginAmt", costItemsMap.get("POSTotal") * appliedMarkupPct);
            costItemsMap.put("POSMinMarginAmt", costItemsMap.get("POSTotal") * markupMin + costItemsMap.get("POSTotal") * (appliedMarkupPct - markupMax));

            costItemsMap.put("POSTotal", costItemsMap.get("POSTotal") * (1 + markupMax));
            costItemsMap.put("POSBase", costItemsMap.get("POSTotal"));
            costItemsMap.put("POSEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSTotal"));

            if(costItemsMap.get("POSDropOffChargeNotAddedToTotal") != null)
            {
                costItemsMap.put("POSEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSTotal") + costItemsMap.get("POSDropOffChargeNotAddedToTotal"));
            }

            if(!requestCurrency.equals(vendorCurrency))
            {
                costItemsMap.put("POSuMaxMarginAmt", costItemsMap.get("POSuTotal") * appliedMarkupPct);
                costItemsMap.put("POSuMinMarginAmt", costItemsMap.get("POSuTotal") * markupMin + costItemsMap.get("POSuTotal") * (appliedMarkupPct - markupMax));

                costItemsMap.put("POSuTotal", costItemsMap.get("POSuTotal") * (1 + appliedMarkupPct));
                costItemsMap.put("POSuBase", costItemsMap.get("POSuTotal"));
                costItemsMap.put("POSuEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSuTotal") );

                if(costItemsMap.get("POSuDropOffChargeNotAddedToTotal") != null)
                {
                    costItemsMap.put("POSuEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSuTotal") + costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                }
            }
        }

        if(costItemsMap.get("POSDropOffChargeNotAddedToTotal") != null && commissionApplied)
        {
            final double dropOffChargeNotAddedToTotal = costItemsMap.get("POSDropOffChargeNotAddedToTotal");

            if("1".equals(vendorCollectsFlag.trim()) || "True".equalsIgnoreCase(vendorCollectsFlag.trim()))
            {
                costItemsMap.put("POSTotal", costItemsMap.get("POSTotal") - dropOffChargeNotAddedToTotal);
                costItemsMap.put("POSBase", costItemsMap.get("POSBase") - dropOffChargeNotAddedToTotal);
                if (costItemsMap.get("POSMisc") != null)
                {
                    costItemsMap.put("POSMisc", costItemsMap.get("POSMisc") - dropOffChargeNotAddedToTotal);
                }

                if (!requestCurrency.equals(vendorCurrency))
                {
                    costItemsMap.put("POSuTotal", costItemsMap.get("POSuTotal") - costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                    costItemsMap.put("POSuBase", costItemsMap.get("POSuBase") - costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));

                    if (costItemsMap.get("POSuMisc") != null)
                    {
                        costItemsMap.put("POSuMisc", costItemsMap.get("POSuMisc") - costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                    }
                }
            } else {
                costItemsMap.put("POSDropOffCharge", dropOffChargeNotAddedToTotal);
                costItemsMap.put("POSDROP CHARGETrip", dropOffChargeNotAddedToTotal);
                costItemsMap.remove("POSDropOffChargeNotAddedToTotal");

                if (!requestCurrency.equals(vendorCurrency))
                {
                    costItemsMap.put("POSuDropOffCharge", costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                    costItemsMap.put("POSuDROP CHARGETrip", costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                    costItemsMap.remove("POSuDropOffChargeNotAddedToTotal");
                }
            }
        }

        //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
        verifyPriceItem(costItemsMap, "POSBase", getDetailCarProduct.getPriceList(), "Base", requestCurrency, remarks, null, false);

        //BaseRateBreakDown
        verifyPriceItemListByDescriptionRawTextAndFinanceApplicationCode(costItemsMap, "POS", getDetailCarProduct.getPriceList(), "MandatoryCharge", requestCurrency, remarks);

        //DropOffCharge
       // verifyPriceItem(costItemsMap, "POSDropOffCharge", getDetailCarProduct.getPriceList(), "DropOffChargeNotAddedToTotal", requestCurrency, remarks, null, false);

        //Total
        verifyPriceItem(costItemsMap, "POSTotal", getDetailCarProduct.getPriceList(), "Total", requestCurrency, remarks, null, false);

        verifyPriceItem(costItemsMap, "POSMaxMarginAmt", getDetailCarProduct.getPriceList(), "MaxMarginAmt", requestCurrency, remarks, null, false);

        verifyPriceItem(costItemsMap, "POSMinMarginAmt", getDetailCarProduct.getPriceList(), "MinMarginAmt", requestCurrency, remarks, null, false);

        verifyPriceItem(costItemsMap, "POSMisc", getDetailCarProduct.getPriceList(), "Misc", requestCurrency, remarks, null, false);

        verifyBaseAndTotalPriceInPriceListAreNonNegative(getDetailCarProduct.getPriceList(), requestCurrency, remarks);

        if(!requestCurrency.equals(vendorCurrency))
        {
            //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
            verifyPriceItem(costItemsMap, "POSuBase", getDetailCarProduct.getPriceList(), "Base", vendorCurrency, remarks, null, false);

            //BaseRateBreakDown
            verifyPriceItemListByDescriptionRawTextAndFinanceApplicationCode(costItemsMap, "POSu", getDetailCarProduct.getPriceList(), "MandatoryCharge", vendorCurrency, remarks);

            //DropOffCharge
            //verifyPriceItem(costItemsMap, "POSuDropOffCharge", getDetailCarProduct.getPriceList(), "DropOffChargeNotAddedToTotal", vendorCurrency, remarks, null, false);

            //Total
            verifyPriceItem(costItemsMap, "POSuTotal", getDetailCarProduct.getPriceList(), "Total", vendorCurrency, remarks, null, false);

            verifyPriceItem(costItemsMap, "POSuMaxMarginAmt", getDetailCarProduct.getPriceList(), "MaxMarginAmt", vendorCurrency, remarks, null, false);

            verifyPriceItem(costItemsMap, "POSuMinMarginAmt", getDetailCarProduct.getPriceList(), "MinMarginAmt", vendorCurrency, remarks, null, false);

            verifyPriceItem(costItemsMap, "POSuMisc", getDetailCarProduct.getPriceList(), "Misc", vendorCurrency, remarks, null, false);

            verifyBaseAndTotalPriceInPriceListAreNonNegative(getDetailCarProduct.getPriceList(), vendorCurrency, remarks);
        }
    }

    private static void handleCostItemsMap(Map<String, Double> costItemsMap, String requestCurrency, String vendorCurrency, String vendorCollectsFlag)
    {
        if ("1".equals(vendorCollectsFlag.trim()) || "True".equalsIgnoreCase(vendorCollectsFlag.trim()))
        {
            oneWayHandle(costItemsMap, requestCurrency, vendorCurrency);
        } else
        {
            costItemsMap.put("POSDropOffCharge", costItemsMap.get("POSDropOffChargeNotAddedToTotal"));
            costItemsMap.put("POSDROP CHARGETrip", costItemsMap.get("POSDropOffChargeNotAddedToTotal"));
            costItemsMap.remove("POSDropOffChargeNotAddedToTotal");
            if (!requestCurrency.equals(vendorCurrency))
            {
                costItemsMap.put("POSuDropOffCharge", costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                costItemsMap.put("POSuDROP CHARGETrip", costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                costItemsMap.remove("POSuDropOffChargeNotAddedToTotal");
            }
        }
    }

    private static void oneWayHandle(Map<String, Double> costItemsMap, String requestCurrency, String vendorCurrency)
    {
        costItemsMap.put("POSTotal", costItemsMap.get("POSTotal") - costItemsMap.get("POSDropOffChargeNotAddedToTotal") );
        costItemsMap.put("POSMisc", costItemsMap.get("POSMisc") - costItemsMap.get("POSDropOffChargeNotAddedToTotal") );
        if (!requestCurrency.equals(vendorCurrency))
        {
            costItemsMap.put("POSuTotal", costItemsMap.get("POSuTotal") - costItemsMap.get("POSuDropOffChargeNotAddedToTotal") );
            costItemsMap.put("POSuMisc", costItemsMap.get("POSuMisc") - costItemsMap.get("POSuDropOffChargeNotAddedToTotal") );

        }
    }


    public static void gdspCommissionCarPriceListVerifier(Map<String, Double> costItemsMap, CarProductType getDetailCarProduct, String requestCurrency,
                                                               String vendorCurrency, List remarks) throws DataAccessException
    {
        costItemsMap.put("POSBase", costItemsMap.get("POSTotal"));
        if (requestCurrency != vendorCurrency)
        {
            costItemsMap.put("POSuBase", costItemsMap.get("POSuTotal"));
        }
        //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
        verifyPriceItem(costItemsMap, "POSBase", getDetailCarProduct.getPriceList(), "Base", requestCurrency, remarks, null, false);

        //Total
        verifyPriceItem(costItemsMap, "POSTotal", getDetailCarProduct.getPriceList(), "Total", requestCurrency, remarks, null, false);

        if(!requestCurrency.equals(vendorCurrency))
        {
            //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
            verifyPriceItem(costItemsMap, "POSuBase", getDetailCarProduct.getPriceList(), "Base", vendorCurrency, remarks, null, false);

             //Total
            verifyPriceItem(costItemsMap, "POSuTotal", getDetailCarProduct.getPriceList(), "Total", vendorCurrency, remarks, null, false);
 }
    }

    public static void gdspMarkupCarPriceListVerifier(Map<String, Double> costItemsMap, CarProductType getDetailCarProduct, String requestCurrency,
                                                          String vendorCurrency, List remarks) throws DataAccessException
    {
        final long vendorSupplierID = getDetailCarProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
         final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

        //Get the VendorCollectsFlag for EU GDSP one way Dropoff charge from DB
        final String vendorCollectsFlag = carsInventoryHelper.getVendorCollectsFlagGDSPOneWayDropOffCharge(vendorSupplierID);
         double markupMins = 0;
         double markupMaxs = 0;

            //If it's a GDSP car with markup, get the markup from CarProduct
            for(final CarMarkupRuleInfoType carMarkupRuleInfo : getDetailCarProduct.getCarMarkupInfo().getAppliedMarkupRuleList().getCarMarkupRuleInfo())
            {
                final long amount = carMarkupRuleInfo.getCarMarkupRate().getDecimal();
                final long decimalCount = carMarkupRuleInfo.getCarMarkupRate().getDecimalPlaceCount();

                if(carMarkupRuleInfo.getCarMarkupRuleCategoryID() == 1 || carMarkupRuleInfo.getCarMarkupRuleCategoryID() == 2)
                {
                    markupMaxs = markupMaxs + amount / Math.pow(10, decimalCount);
                } else if (carMarkupRuleInfo.getCarMarkupRuleCategoryID() == 3)
                {
                    markupMins = amount / Math.pow(10, decimalCount);
                }
            }

            final int appliedMarkupAmount = getDetailCarProduct.getCarMarkupInfo().getAppliedMarkupRate().getDecimal();
            final long appliedMarkupDecimalCount = getDetailCarProduct.getCarMarkupInfo().getAppliedMarkupRate().getDecimalPlaceCount();
            final double appliedMarkupPct = appliedMarkupAmount / Math.pow(10, appliedMarkupDecimalCount);

            if (markupMaxs == 0 )
            {
                markupMaxs = markupMins;
            }

            if (markupMins > markupMaxs)
            {
                final double markup = markupMaxs;
                markupMaxs = markupMins;
                markupMins = markup;
            }

            if (costItemsMap.get("POSDropOffChargeNotAddedToTotal") != null)
            {
                if ("1".equals(vendorCollectsFlag.trim()) || "True".equalsIgnoreCase(vendorCollectsFlag.trim()))
                {
                    oneWayHandle(costItemsMap, requestCurrency, vendorCurrency);
                }
            }

            costItemsMap.put("POSMaxMarginAmt", costItemsMap.get("POSTotal") * appliedMarkupPct);
            costItemsMap.put("POSMinMarginAmt", costItemsMap.get("POSTotal") * markupMins + costItemsMap.get("POSTotal") * (appliedMarkupPct - markupMaxs));

            costItemsMap.put("POSTotal", costItemsMap.get("POSTotal") * (1 + markupMaxs));
            costItemsMap.put("POSBase", costItemsMap.get("POSTotal"));

            if(costItemsMap.get("POSDropOffChargeNotAddedToTotal") != null)
            {
                costItemsMap.put("POSEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSTotal") + costItemsMap.get("POSDropOffChargeNotAddedToTotal"));
            }

            if(!requestCurrency.equals(vendorCurrency))
            {
                costItemsMap.put("POSuMaxMarginAmt", costItemsMap.get("POSuTotal") * appliedMarkupPct);
                costItemsMap.put("POSuMinMarginAmt", costItemsMap.get("POSuTotal") * markupMins + costItemsMap.get("POSuTotal") * (appliedMarkupPct - markupMaxs));

                costItemsMap.put("POSuTotal", costItemsMap.get("POSuTotal") * (1 + appliedMarkupPct));
                costItemsMap.put("POSuBase", costItemsMap.get("POSuTotal"));
                costItemsMap.put("POSuEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSuTotal") );

                if(costItemsMap.get("POSuDropOffChargeNotAddedToTotal") != null)
                {
                    costItemsMap.put("POSuEstimated total cost to customer including mandatory charges payable at the counter.", costItemsMap.get("POSuTotal") + costItemsMap.get("POSuDropOffChargeNotAddedToTotal"));
                }
            }



        //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
        verifyPriceItem(costItemsMap, "POSBase", getDetailCarProduct.getPriceList(), "Base", requestCurrency, remarks, null, false);


        //Total
        verifyPriceItem(costItemsMap, "POSTotal", getDetailCarProduct.getPriceList(), "Total", requestCurrency, remarks, null, false);

        verifyPriceItem(costItemsMap, "POSMaxMarginAmt", getDetailCarProduct.getPriceList(), "MaxMarginAmt", requestCurrency, remarks, null, false);

        verifyPriceItem(costItemsMap, "POSMinMarginAmt", getDetailCarProduct.getPriceList(), "MinMarginAmt", requestCurrency, remarks, null, false);


        if(!requestCurrency.equals(vendorCurrency))
        {
            //Verify Base item, if priceBase is not equal with priceAmount, then test case Fail
            verifyPriceItem(costItemsMap, "POSuBase", getDetailCarProduct.getPriceList(), "Base", vendorCurrency, remarks, null, false);

            //Total
            verifyPriceItem(costItemsMap, "POSuTotal", getDetailCarProduct.getPriceList(), "Total", vendorCurrency, remarks, null, false);

            verifyPriceItem(costItemsMap, "POSuMaxMarginAmt", getDetailCarProduct.getPriceList(), "MaxMarginAmt", vendorCurrency, remarks, null, false);

            verifyPriceItem(costItemsMap, "POSuMinMarginAmt", getDetailCarProduct.getPriceList(), "MinMarginAmt", vendorCurrency, remarks, null, false);
        }
    }

    //Compare the two prices, verify if the compared prices is within the allowed deviation
    private static void verifyPriceItem(Map<String, Double> costItemsMap, String costItemKey, PriceListType priceList, String finaceCategoryCode,
            String currencyCode, List remarks, String description, boolean applyFinanceApplicationUnitCount)
    {
        final Double priceAmount = CostPriceCalculator.getPriceAmountByFinanceCategoryCode(priceList, currencyCode, finaceCategoryCode, 4, description, applyFinanceApplicationUnitCount);
        final Double expAmount = (null == costItemsMap.get(costItemKey)) ? 0d: costItemsMap.get(costItemKey);
        final Double allowedvalue = 0.01;

        if (Math.abs(priceAmount - expAmount) > allowedvalue)
        {
            remarks.add(finaceCategoryCode + "/" + costItemKey + " is not expected, actual: "+ priceAmount + ", expected: " + expAmount);
        }
    }

    //Compare pricelist, key in costItemsMap which include FinanceApplicationCode
    private static void verifyPriceItemListByFinanceApplicationCode(Map<String, Double> costItemsMap, String costItemKey, PriceListType priceList, String finaceCategoryCode,
                                       String currencyCode, List remarks, boolean applyFinanceApplicationUnitCount)
    {
        final Double allowedValue = 0.01;
        final List<PriceType> priceListToVerify = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList, currencyCode, finaceCategoryCode, null );

        for(final PriceType price : priceListToVerify)
        {
            final Double priceAmount = CostPriceCalculator.calculatePriceAmount(price, 4, applyFinanceApplicationUnitCount);
            final Double expAmount = (null == costItemsMap.get(costItemKey + price.getFinanceApplicationCode())) ? 0d: costItemsMap.get(costItemKey + price.getFinanceApplicationCode());
            if (Math.abs(priceAmount - expAmount) > allowedValue)
            {
                remarks.add(costItemKey + price.getFinanceApplicationCode() + " is not expected, actual: "+ priceAmount + ", expected: " + expAmount);
            }
        }
    }

    //Compare pricelist, key in costItemsMap which include DescriptionRawText
    private static void verifyPriceItemListByDescriptionRawText(Map<String, Double> costItemsMap, String costItemKey, PriceListType priceList, String financeCategoryCode,
            String currencyCode, List remarks, boolean applyFinanceApplicationUnitCount)
    {
        final Double allowedValue = 0.01;
        final List<PriceType> priceListToVerify = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList, currencyCode, financeCategoryCode, null );

        for(final PriceType price : priceListToVerify)
        {
            final Double priceAmount = CostPriceCalculator.calculatePriceAmount(price, 4, applyFinanceApplicationUnitCount);
            final Double expAmount = (null == costItemsMap.get(costItemKey + price.getDescriptionRawText())) ? 0d: costItemsMap.get(costItemKey + price.getDescriptionRawText());
            if (Math.abs(priceAmount - expAmount) > allowedValue)
            {
                remarks.add(costItemKey + price.getDescriptionRawText() + " is not expected, actual: "+ priceAmount + ", expected: " + expAmount);
            }
        }
    }

    private static void verifyPriceItemListByDescriptionRawTextAndFinanceApplicationCode(Map<String, Double> costItemsMap, String costItemKey, PriceListType priceList,
            String financeCategoryCode, String currencyCode, List remarks)
    {
        final List<PriceType> priceListToVerify = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList, currencyCode, financeCategoryCode, null );

        for (final PriceType price : priceListToVerify)
        {
            verifyPriceItemByDescriptionRawTextAndFinanceApplicationCode(costItemsMap, costItemKey, price, remarks);
        }
    }

    private static void verifyPriceItemByDescriptionRawTextAndFinanceApplicationCode(Map<String, Double> costItemsMap, String costItemKey,  PriceType price, List remarks)
    {
        final Double allowedValue = 0.01;
        final Double priceAmount = CostPriceCalculator.calculatePriceAmount(price, 4, false);
        final Double expAmount = (null == costItemsMap.get(costItemKey + price.getDescriptionRawText() + price.getFinanceApplicationCode())) ? 0d: costItemsMap.get(costItemKey + price.getDescriptionRawText() + price.getFinanceApplicationCode());

        if (Math.abs(priceAmount - expAmount) > allowedValue)
        {
            remarks.add(costItemKey + price.getDescriptionRawText() + price.getFinanceApplicationCode() + " is not expected, actual: "+ priceAmount + ", expected: " + expAmount);
        }
    }

    private static void verifyBaseAndTotalPriceInPriceListAreNonNegative(PriceListType priceList, String currency, List remarks)
    {
        if(CostPriceCalculator.getPriceAmountByFinanceCategoryCode(priceList, currency, "Base", 4, null, false) < 0)
        {
            remarks.add("Test case failed due to Base Price in " + currency + " negative, Base price is : " +
                    CostPriceCalculator.getPriceAmountByFinanceCategoryCode(priceList, currency, "Base", 4, null, false));
        }
        if(CostPriceCalculator.getPriceAmountByFinanceCategoryCode(priceList, currency, "Total", 4, null, false) < 0)
        {
            remarks.add("Test case failed due to Total Price in " + currency + " negative, Total price is : " +
                    CostPriceCalculator.getPriceAmountByFinanceCategoryCode(priceList, currency, "Total", 4, null, false));
        }
    }

    private static boolean isCarCommissionApplied(CarProductType carProduct) throws DataAccessException
    {
        Boolean commissionApplied = false;
        final BigDecimal commissionRate = getCarCommissionRate(carProduct);

        if (commissionRate != null && commissionRate.compareTo(BigDecimal.ZERO) > 0)
        {
            commissionApplied = true;
        }

        return commissionApplied;
    }

    private static BigDecimal getCarCommissionRate(CarProductType carProduct) throws DataAccessException
    {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

        return inventoryHelper.getCommissionDetailsByCarItemIDAndAirportCode(carProduct.getCarInventoryKey().getCarItemID(), carProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode());
    }

    public static boolean isCommissionExistsInPriceList(CarProductType carProductType) {
        for (final PriceType priceList : carProductType.getPriceList().getPrice()) {
            if (priceList.getFinanceCategoryCode().equals("Commission")) {
                return false;
            }
        }
        return true;
    }
}