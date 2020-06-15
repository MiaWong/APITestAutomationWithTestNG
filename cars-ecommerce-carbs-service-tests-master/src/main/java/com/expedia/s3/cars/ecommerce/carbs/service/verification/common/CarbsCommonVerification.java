package com.expedia.s3.cars.ecommerce.carbs.service.verification.common;

import com.expedia.e3.data.cartypes.defn.v5.CarCostType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 11/9/2016.
 */
public class CarbsCommonVerification
{
    @SuppressWarnings("PMD")
    public String verifyHertzPrepayCarIsCorrect(CarProductType carProduct, CarsInventoryHelper inventoryHelper) throws DataAccessException
    {
        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder();

        final SupplySubSetToWorldSpanSupplierItemMap supplySubsetMap = inventoryHelper
                .getWorldSpanSupplierItemMap(carProduct.getCarInventoryKey().getSupplySubsetID());

        if (null != supplySubsetMap.getPrepaidBool() && supplySubsetMap.getPrepaidBool().equals("1"))
        {
            if (!carProduct.getPrePayBoolean())
            {
                errorMsg.append("PrePayBoolean Should be true but get false for Hertz, SupplySubsetID = " +
                        carProduct.getCarInventoryKey().getSupplySubsetID() +" ; CarItemID = "+
                        carProduct.getCarInventoryKey().getCarItemID());
            }

            if (carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 40)
            {
                if (null == carProduct.getCarInventoryKey().getCarRate() ||
                        null == carProduct.getCarInventoryKey().getCarRate().getRateCategoryCode())
                {
                    errorMsg.append("Hertz prepay car RateCategoryCode should be Prepay, but get Null. for car SupplySubsetID: "
                            + carProduct.getCarInventoryKey().getSupplySubsetID()
                            + " ; CarItemID = "+ carProduct.getCarInventoryKey().getCarItemID()
                            + "SIPP : " + PojoXmlUtil.pojoToDoc(carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()));
                } else if (!carProduct.getCarInventoryKey().getCarRate().getRateCategoryCode().equalsIgnoreCase
                        (CommonConstantManager.RateCategory.PREPAY))
                {
                    errorMsg.append("Hertz prepay car RateCategoryCode should be Prepay, but get "
                            + carProduct.getCarInventoryKey().getCarRate().getRateCategoryCode()
                            + " for Car SupplySubsetID: " + carProduct.getCarInventoryKey().getSupplySubsetID()
                            + " ; CarItemID = "+ carProduct.getCarInventoryKey().getCarItemID()
                            + "SIPP : " + PojoXmlUtil.pojoToDoc(carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()));
                }
            }
        }

        return errorMsg.toString();
    }

    @SuppressWarnings("PMD")
    public String verifyIfPrePayCarReferencePriceReturn(CarProductType carProduct, boolean shouldReferencePriceReturned) throws DataAccessException
    {
        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder();

        final String carSIPP = "VendorSupplierID : " + carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()
                + ", CarTypeCode : " + carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode()
                + ", CarTransmissionDriveCode : " + carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode()
                + ", CarFuelACCode : " + carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()
                + ", CarCategoryCode : " + carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode();

        if (shouldReferencePriceReturned)
        {
            //TODO CASSS-11910 Price filter is not done to prepaid agency car:
            // because of this bug, duplicate car is not filtered by carbs to prepaid car, so not filtered duplicate car will not have reference price
            // If bug CASSS-11910 is fixed, we can remove this code: (note: only CarCategoryCode=7 or 6 has duplicate car, for oneway, has CarCategoryCode=4)
            /*if (carProduct.getPrePayBoolean().booleanValue() && !((carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == 7
                    || carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == 6
                    || carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == 4) &&
                    carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode() == 1)) {
                return "";
            }*/
            if (carProduct.getPrePayBoolean().booleanValue() && (carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == 7
                    || carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == 6
                    || carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() == 4) &&
                    carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode() == 1) {
                return "";
            }
            if (carProduct.getTotalReferencePrice() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0)
            {
                errorMsg.append("\nTotalReferencePrice of car : ").append(carSIPP).append(" should return but not return.");
            }

            if (carProduct.getReferenceEstimatedTotalCostToCustomer() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0)
            {
                errorMsg.append("\nReferenceEstimatedTotalCostToCustomer of car : ").append(carSIPP)
                        .append(" should return but not return.");
            }
        } else
        {
            if (!(carProduct.getTotalReferencePrice() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount() == null
                    || carProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0))
            {
                errorMsg.append("\nTotalReferencePrice of car : ").append(carSIPP).append(" should not return but returned.");
            }

            if (!(carProduct.getReferenceEstimatedTotalCostToCustomer() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount() == null
                    || carProduct.getReferenceEstimatedTotalCostToCustomer().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0))
            {
                errorMsg.append("\nReferenceEstimatedTotalCostToCustomer of car : " ).append( carSIPP)
                        .append(" should not return but returned.");
            }
        }

        return errorMsg.toString();
    }


    public static void handleCurrency(boolean currencyConversion, TestData testData, CarProductType actualCarproduct)
    {
        if(currencyConversion)
        {
            final List<CarCostType> tempadditionalFee = new ArrayList<>();
            if(null != actualCarproduct.getCarRateDetail() && null != actualCarproduct.getCarRateDetail().getCarAdditionalFeesList())
            {
                for (final CarCostType carCostType : actualCarproduct.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees())
                {
                    if (!carCostType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(testData.getScenarios().getSupplierCurrencyCode()))
                    {
                        tempadditionalFee.add(carCostType);
                    }
                }
                actualCarproduct.getCarRateDetail().getCarAdditionalFeesList().setCarAdditionalFees(tempadditionalFee);
            }
            final List<CostType> tempCostList = new ArrayList<>();

            for (final CostType carCost : actualCarproduct.getCostList().getCost())
            {
                if (!carCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(testData.getScenarios().getSupplierCurrencyCode()))
                {
                    tempCostList.add(carCost);
                }
            }
            actualCarproduct.getCostList().setCost(tempCostList);
        }
    }


}