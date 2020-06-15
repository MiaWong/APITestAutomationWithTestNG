package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCostType;
import com.expedia.e3.data.cartypes.defn.v5.CarCoveragesCostType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.utils.CurrencyConvertUtil;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 9/19/2016.
 * <p>
 * We verified CarCoveragesCostList & CarAdditionalFees In these common verifier.
 * <p>
 * CASSS-2125    ConditionalCostList to be returned in xml format
 * CASSS-2485    CMA - return all the fees payable at the counter in both POS
 * and POSu currency and also return a grand total price that represents the total cost to customer
 */
@SuppressWarnings("PMD")
public class CarRateDetailCommonVerifier {
    private final static String MESSAGE_GDS = " GDS Message: ";
    private final static String MESSAGE_Request = " Request ";
    private final static String MESSAGE_Response = " Response ";

    //This flag is use to verify compare Additional fee from GDS with response
    boolean haveCurrencyConvert = false;

    public void verifyCarRateDetail(DataSource carsInventoryDatasource, CarProductType GDSMsgRspCar, CarProductType reqCar,
                                    CarProductType rspCar, List remarks, String action,
                                    boolean shouldVerifyWhileRateDetailIsEmpty) throws Exception {
        if (null == rspCar.getCarRateDetail()) {
            if (shouldVerifyWhileRateDetailIsEmpty) {
                remarks.add("CarRateDetail is null in " + action +
                        " should not be null, VendorSupplierID : " + rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
            }
        } else {
            this.verifyCarCoveragesCostList(reqCar, rspCar, remarks, action);
            this.verifyCarAdditionalFeesList(carsInventoryDatasource, reqCar, rspCar, remarks, action);

            if(null != GDSMsgRspCar)
            {
                verifyCarRateDetailCompareWithGDSMsg(GDSMsgRspCar, rspCar, remarks, action);
            }
        }
    }

    public void verifyCarRateDetailCostNoCompareWithRequest(DataSource carsInventoryDatasource, CarProductType GDSMsgRspCar,
                                                            CarProductType rspCar, List remarks, String action) throws Exception {
        if (null == rspCar.getCarRateDetail()) {
            remarks.add("CarRateDetail is null in " + action +
                    " should not be null, VendorSupplierID : " + rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
        } else {
            OptionListCommonVerifier optionListCommonVerifier = new OptionListCommonVerifier();
            optionListCommonVerifier.verifyOptionListItemsBasicVerify(null, rspCar, remarks, action);

            this.verifyConditionalCostPriceListExist(rspCar, remarks, action);
            this.verifyCarCoveragesCostList(null, rspCar, remarks, action);
            this.verifyCarAdditionalFeesList(carsInventoryDatasource, null, rspCar, remarks, action);

            verifyCarRateDetailCompareWithGDSMsg(GDSMsgRspCar, rspCar, remarks, action);
        }
    }

    private void verifyCarCoveragesCostList(CarProductType reqCar,
                                            CarProductType rspCar, List remarks, String action) {
        carCoveragesCostListBasicVerify(rspCar, remarks, action);
        if (reqCar != null) {
            compareCarCoveragesCost(reqCar, rspCar, remarks, action, MESSAGE_Request, MESSAGE_Response);
        }
    }

    private void carCoveragesCostListBasicVerify(CarProductType rspCar, List remarks, String action) {
        for (CarCoveragesCostType rspCCCL : rspCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost()) {
            if (!rspCCCL.getCarCost().getFinanceCategoryCode().equals("Coverages")) {
                remarks.add("getFinanceCategoryCode in CarProduct/CarRateDetail/CarCoveragesCostList in " + action + " Response is : ("
                        + rspCCCL.getCarCost().getFinanceCategoryCode() + ") it should be Coverages.");
            }

            if (rspCCCL.getCarCost().getFinanceApplicationUnitCount() != 0 && rspCCCL.getCarCost().getFinanceApplicationUnitCount() != 1) {
                remarks.add("FinanceApplicationUnitCount of CarVehicleOption in " + action + " Response is:" +
                        rspCCCL.getCarCost().getFinanceApplicationUnitCount() + " should be 0 or 1.");
            }

            if (!financeSubCategoryList4CoverageType(rspCCCL.getCarCost().getFinanceSubCategoryCode())) {
                remarks.add("FinanceSubCategory is " + rspCCCL.getCarCost().getFinanceSubCategoryCode() +
                        " not in Coverages/Vehicle Coverage Type List should not show in CarCoveragesCostList.");
            }
        }
    }

    public boolean compareCarCoveragesCost(CarProductType compareCar, CarProductType targetCar, List marks, String action,
                                           String whereCompareCarComeFrom, String whereTargetCarComeFrom) {
        boolean isPassed = false;

        ArrayList remarks = new ArrayList();

        int compareCCCLSize = compareCar.getCarRateDetail().getCarCoveragesCostList() == null ||
                compareCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost() == null ? 0 :
                compareCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost().size();

        int targetCCCLSize = targetCar.getCarRateDetail().getCarCoveragesCostList() == null ||
                targetCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost() == null ? 0 :
                targetCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost().size();

        if (compareCCCLSize == 0) {
            remarks.add("CarCoveragesCostList list is empty in " + action + whereCompareCarComeFrom);
        }

        if (compareCCCLSize != targetCCCLSize) {
            remarks.add("CarCoveragesCostList size in " + action + whereTargetCarComeFrom + ": (" + targetCCCLSize + ") is not same as size in "
                    + action + whereCompareCarComeFrom + " : (" + compareCCCLSize + ").");
            marks.addAll(remarks);
            return false;
        }

        for (CarCoveragesCostType compareCCCL : compareCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost()) {
            for (CarCoveragesCostType targetCCCL : targetCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost()) {
                if (compareCCCL.getCarCost().getFinanceApplicationUnitCount() != 0 && compareCCCL.getCarCost().getFinanceApplicationUnitCount() != 1) {
                    remarks.add("FinanceApplicationUnitCount of CarVehicleOption in " + action + whereCompareCarComeFrom + " is:" +
                            compareCCCL.getCarCost().getFinanceApplicationUnitCount() + " should be 0 or 1.");
                }

                if (targetCCCL.getCarCost().getFinanceCategoryCode().equals(compareCCCL.getCarCost().getFinanceCategoryCode())
                        && targetCCCL.getCarCost().getFinanceSubCategoryCode().equals(compareCCCL.getCarCost().getFinanceSubCategoryCode())
                        && targetCCCL.getCarCost().getFinanceApplicationCode().equals(compareCCCL.getCarCost().getFinanceApplicationCode())
                        && targetCCCL.getCarCost().isRequiredCostBoolean() == compareCCCL.getCarCost().isRequiredCostBoolean()
                        && targetCCCL.getCarCost().getFinanceApplicationUnitCount().equals(compareCCCL.getCarCost().getFinanceApplicationUnitCount())) {
                    if (compareCCCL.getCarDeductible() != null && targetCCCL.getCarDeductible() != null
                            && compareCCCL.getCarDeductible().getExcessAmount() != null && targetCCCL.getCarDeductible().getExcessAmount() != null
                            && compareCCCL.getCarDeductible().getExcessAmount().getCurrencyCode().equals(targetCCCL.getCarDeductible().getExcessAmount().getCurrencyCode())
                            && compareCCCL.getCarDeductible().getExcessAmount().getSimpleAmount().equals(targetCCCL.getCarDeductible().getExcessAmount().getSimpleAmount())) {
                        isPassed = true;
                    } else if (compareCCCL.getCarDeductible() == null && targetCCCL.getCarDeductible() == null) {
                        isPassed = true;
                    }
                    if (isPassed == true) {
                        break;
                    }
                }
            }
            if (!isPassed) {
                remarks.add("CarCoveragesCostType exist in " + action + whereCompareCarComeFrom +
                        ": ( FinanceCategoryCode : " + compareCCCL.getCarCost().getFinanceCategoryCode() +
                        " FinanceSubCategoryCode : " + compareCCCL.getCarCost().getFinanceSubCategoryCode() +
                        " FinanceApplicationCode : " + compareCCCL.getCarCost().getFinanceApplicationCode() +
                        " ) is not find in " + action + whereTargetCarComeFrom + ".");
            } else {
                isPassed = false;
            }
        }

        if (remarks.size() > 0) {
            marks.addAll(remarks);
            return false;
        }
        return true;
    }

    private void verifyCarAdditionalFeesList(DataSource carsInventoryDatasource,
                                             CarProductType reqCar, CarProductType rspCar, List remarks, String action) throws Exception {

        isCarAdditionalFeesShouldBeEmpty(carsInventoryDatasource, rspCar, remarks, action);

        carAdditionalFeesListBasicVerify(rspCar, remarks, action);

        if (reqCar != null) {
            compareCarAdditionalFeesList(reqCar, rspCar, remarks, action, MESSAGE_Request, MESSAGE_Response);
        }

        if (!remarks.isEmpty()) {
            return;
        }

        verifyDropOffChargeNotAddedToTotalExistInAdditionalFees(rspCar, remarks, action);

        verifyAdditionalFeesCurrencyConversion(rspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees(),
                remarks, action, rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());

        verifyTotalCostToCustomerFee(carsInventoryDatasource, rspCar, remarks, action);
    }

    private void carAdditionalFeesListBasicVerify(CarProductType rspCar, List remarks, String action) {
        for (CarCostType rspCAFL : rspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {
            if (!rspCAFL.getFinanceCategoryCode().equals("Fee")) {
                remarks.add("getFinanceCategoryCode in CarProduct/CarRateDetail/CarAdditionalFeesList in " + action + " Response is : ("
                        + rspCAFL.getFinanceCategoryCode() + ") it should be Fee.");
            }

            if (rspCAFL.getFinanceApplicationUnitCount() != 0 && rspCAFL.getFinanceApplicationUnitCount() != 1) {
                remarks.add("FinanceApplicationUnitCount of CarVehicleOption in " + action + " Response is:" +
                        rspCAFL.getFinanceApplicationUnitCount() + " should be 0 or 1.");
            }

            if (!financeSubCategoryList4FeeChargePurpose(rspCAFL.getFinanceSubCategoryCode())) {
                remarks.add("FinanceSubCategory is " + rspCAFL.getFinanceSubCategoryCode() +
                        " not in Fee/Vehicle Charge Purpose List should not show in CarAdditionalFeesList.");
            }
        }
    }

    public boolean compareCarAdditionalFeesList(CarProductType compareCar, CarProductType targetCar, List marks,
                                                String action, String whereCompareCarComeFrom, String whereTargetCarComeFrom) {
        boolean isPassed = false;

        ArrayList remarks = new ArrayList();

        int compareCAFLSize = compareCar.getCarRateDetail().getCarAdditionalFeesList() == null ||
                compareCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() == null ? 0 :
                compareCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().size();
        int targetCAFLSize = targetCar.getCarRateDetail().getCarAdditionalFeesList() == null ||
                targetCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() == null ? 0 :
                targetCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().size();

        if (compareCAFLSize == 0) {
            remarks.add("CarAdditionalFeesList list is empty in " + action + whereCompareCarComeFrom + " .");
        }

        //additionalFeeList in Car convert from GDSMsg do not do currency convert, so add this verifier
        if (haveCurrencyConvert && whereCompareCarComeFrom.equals(MESSAGE_GDS) && targetCAFLSize / compareCAFLSize != 2) {
            remarks.add("CarAdditionalFeesList have Currency convert, but size in " + action + whereTargetCarComeFrom +
                    " : (" + targetCAFLSize + ") is not double as size in "
                    + action + whereCompareCarComeFrom + " : (" + compareCAFLSize + ").");
            marks.addAll(remarks);
            return false;
        }

        if (!whereCompareCarComeFrom.equals(MESSAGE_GDS) && compareCAFLSize != targetCAFLSize) {
            remarks.add("CarAdditionalFeesList size in " + action + whereTargetCarComeFrom +
                    " : (" + targetCAFLSize + ") is not same as size in "
                    + action + whereCompareCarComeFrom + " : (" + compareCAFLSize + ").");
            marks.addAll(remarks);
            return false;
        }

        for (CarCostType compareCAFL : compareCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {
            for (CarCostType targetCAFL : targetCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {
                if (compareCAFL.getFinanceApplicationUnitCount() != 0 && compareCAFL.getFinanceApplicationUnitCount() != 1) {
                    remarks.add("FinanceApplicationUnitCount of CarVehicleOption in " + action + whereCompareCarComeFrom
                            + " is:" + compareCAFL.getFinanceApplicationUnitCount() + " should be 0 or 1.");
                }

                if (targetCAFL.getFinanceCategoryCode().equals(compareCAFL.getFinanceCategoryCode())
                        && targetCAFL.getFinanceSubCategoryCode().equals(compareCAFL.getFinanceSubCategoryCode())
                        && targetCAFL.getFinanceApplicationCode().equals(compareCAFL.getFinanceApplicationCode())
                        && targetCAFL.isRequiredCostBoolean() == compareCAFL.isRequiredCostBoolean()
                        && targetCAFL.getFinanceApplicationUnitCount().equals(compareCAFL.getFinanceApplicationUnitCount())) {
                    isPassed = true;
                    break;
                }
            }

            if (!isPassed) {
                remarks.add("CarAdditionalFeesList exist in " + action + whereCompareCarComeFrom +
                        " : ( FinanceCategoryCode :" + compareCAFL.getFinanceCategoryCode() +
                        "FinanceSubCategoryCode : " + compareCAFL.getFinanceSubCategoryCode() +
                        "FinanceApplicationCode : " + compareCAFL.getFinanceApplicationCode() + ") is not find in " +
                        action + whereTargetCarComeFrom + " .");
            } else {
                isPassed = false;
            }
        }

        if (remarks.size() > 0) {
            marks.addAll(remarks);
            return false;
        }
        return true;
    }

    private void isCarAdditionalFeesShouldBeEmpty(DataSource carsInventoryDatasource, CarProductType rspCar,
                                                  List remarks, String action) throws DataAccessException {
        CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDatasource);

        int rspCAFLSize = rspCar.getCarRateDetail().getCarAdditionalFeesList() == null ||
                rspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() == null ? 0 :
                rspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().size();

        if (inventoryHelper.getBusinessModelID(rspCar) == 1 && rspCAFLSize != 0) {
            remarks.add("CarAdditionalFeesList list Should be empty in " + action +
                    " While is Agency car, VendorSupplierID : " + rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
            return;
        }

        if (rspCAFLSize == 0) {
            remarks.add("CarAdditionalFeesList list is empty in " + action + ", VendorSupplierID : " +
                    rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
            return;
        }
    }

    private void verifyConditionalCostPriceListExist(CarProductType rspCar, List remarks, String action) {
        int rspCCCLSize = rspCar.getCarRateDetail().getConditionalCostPriceList() == null ||
                rspCar.getCarRateDetail().getConditionalCostPriceList().getCostPrice() == null ? 0 :
                rspCar.getCarRateDetail().getConditionalCostPriceList().getCostPrice().size();
        if (rspCCCLSize == 0) {
            remarks.add("CarConditionalCostList list is empty in " + action + ", VendorSupplierID : " +
                    rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
        }
    }

    private final String DropOffChargeNotAddedToTotal = "DropOffChargeNotAddedToTotal";

    private void verifyDropOffChargeNotAddedToTotalExistInAdditionalFees(CarProductType rspCar, List remarks, String action) {
        boolean findDropoffFee = false;
        if (rspCar != null && rspCar.getPriceList() != null && !rspCar.getPriceList().getPrice().isEmpty()) {
            for (PriceType price : rspCar.getPriceList().getPrice()) {
                if (price.getDescriptionRawText().equals(DropOffChargeNotAddedToTotal)) {
                    for (CarCostType additionalFee : rspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {
                        if (additionalFee.getFinanceSubCategoryCode().equals("Drop") && additionalFee.getFinanceCategoryCode().equals("Fee")) {
                            findDropoffFee = true;
                            break;
                        }
                    }
                    if (!findDropoffFee) {
                        remarks.add("There is " + DropOffChargeNotAddedToTotal + " Fee in CarProduct/PriceList in " + action +
                                ", but not find AdditionalFee (FinanceCategoryCode = Fee && FinanceSubCategoryCode = Drop) in CarProduct/CarRateDetail/CarAdditionalFees.");
                    }
                    break;
                }
            }
        }
    }

    private void verifyAdditionalFeesCurrencyConversion(List<CarCostType> carAdditionalFees, List remarks, String action, long supplierID) throws Exception {
        if (carAdditionalFees.size() > 0) {
            List<CarCostType> currency1CostList = new ArrayList<>();
            List<CarCostType> currency2CostList = new ArrayList<>();
            String currency1 = carAdditionalFees.get(0).getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
            String currency2 = "";
            for (CarCostType carAddFee : carAdditionalFees) {
                if (currency1.equals(carAddFee.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())) {
                    currency1CostList.add(carAddFee);
                } else {
                    if (currency2.isEmpty())
                        currency2 = carAddFee.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                    currency2CostList.add(carAddFee);
                }
            }
            if (currency1CostList.size() > 0 && currency2CostList.size() > 0 && currency1CostList.size() != currency2CostList.size()) {
                remarks.add("AdditionalFees Currency Conversion is Wrong in " + action + ", VendorSupplierID : " + supplierID +
                        ", " + currency1 + " Item have : " + currency1CostList.size() + ", but " + currency2 + " Item have : " + currency2CostList);
                return;
            }

            if (currency1CostList.size() > 0 && currency2CostList.size() > 0) {
                double expectedCurrencyRate = Double.parseDouble(CurrencyConvertUtil.getExchangeRate(currency1, currency2));
                if (expectedCurrencyRate != 1) {
                    haveCurrencyConvert = true;
                }

                for (CarCostType currency1Cost : currency1CostList) {
                    for (CarCostType currency2Cost : currency2CostList) {
                        if (currency1Cost.getFinanceSubCategoryCode().equals(currency2Cost.getFinanceSubCategoryCode())) {
                            double currency1Amout = currency1Cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                    Math.pow(10, currency1Cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                            double currency2Amout = currency2Cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                    Math.pow(10, currency2Cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());

                            if (currency2Amout - currency1Amout * expectedCurrencyRate > 0.01 || currency1Amout * expectedCurrencyRate - currency2Amout > 0.01) {
                                remarks.add(action + " Price in AdditionalFees " + currency1Cost.getFinanceSubCategoryCode()
                                        + " is Wrong, Actual : " + currency2Amout + currency2 +
                                        " Expected: " + currency1Amout * expectedCurrencyRate + currency2);
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    //decimal totalPrice = Math.Round((decimal)(cost.MultiplierOrAmount.CurrencyAmount.Amount.Decimal /
    // Math.Pow(10, cost.MultiplierOrAmount.CurrencyAmount.Amount.DecimalPlaceCount)), 4, MidpointRounding.AwayFromZero);

    //double newMarkup = car.CarMarkupInfo.AppliedMarkupRate.Decimal / Math.Pow(10, car.CarMarkupInfo.AppliedMarkupRate.DecimalPlaceCount);
    private void verifyTotalCostToCustomerFee(DataSource carsInventoryDatasource, CarProductType rspCar, List remarks, String action) throws DataAccessException {
        if (null != rspCar.getPriceList()) {
            String currency1 = rspCar.getPriceList().getPrice().get(0).getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
            double actualEstimatedTotalCostToCustomerCurrency1 = 0;
            double expectedEstimatedTotalCostToCustomerCurrency1 = 0;

            String currency2 = "";
            double actualEstimatedTotalCostToCustomerCurrency2 = 0;
            double expectedEstimatedTotalCostToCustomerCurrency2 = 0;

            for (PriceType priceType : rspCar.getPriceList().getPrice()) {
                if (priceType.getFinanceCategoryCode().equals("EstimatedTotalCostToCustomer")) {
                    if (priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(currency1))
                        actualEstimatedTotalCostToCustomerCurrency1 = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                Math.pow(10, priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                    else {
                        if (currency2.isEmpty())
                            currency2 = priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                        actualEstimatedTotalCostToCustomerCurrency2 = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                Math.pow(10, priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                    }
                }
                if (priceType.getFinanceCategoryCode().equals("Total")) {
                    if (priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(currency1))
                        expectedEstimatedTotalCostToCustomerCurrency1 = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                Math.pow(10, priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                    else
                        expectedEstimatedTotalCostToCustomerCurrency2 = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                Math.pow(10, priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                }
            }

            CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDatasource);
            if (inventoryHelper.getBusinessModelID(rspCar) != 1) {
                for (CarCostType carAddFee : rspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {
                    if (carAddFee.isRequiredCostBoolean()) {
                        if (carAddFee.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(currency1)) {
                            expectedEstimatedTotalCostToCustomerCurrency1 = expectedEstimatedTotalCostToCustomerCurrency1 +
                                    carAddFee.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                            Math.pow(10, carAddFee.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                        } else {
                            expectedEstimatedTotalCostToCustomerCurrency2 = expectedEstimatedTotalCostToCustomerCurrency2 +
                                    carAddFee.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                            Math.pow(10, carAddFee.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                        }
                    }
                }
            }

            if (actualEstimatedTotalCostToCustomerCurrency1 - expectedEstimatedTotalCostToCustomerCurrency1 > 0.01
                    || expectedEstimatedTotalCostToCustomerCurrency1 - actualEstimatedTotalCostToCustomerCurrency1 > 0.01) {
                remarks.add("EstimatedTotalCostToCustomer is not as expected : " + expectedEstimatedTotalCostToCustomerCurrency1 +
                        " Actual : " + actualEstimatedTotalCostToCustomerCurrency1 + " Currency : " + currency1 +
                        ", VendorSupplierID : " + rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() + " in Action : " + action);
            }

            if (actualEstimatedTotalCostToCustomerCurrency2 - expectedEstimatedTotalCostToCustomerCurrency2 > 0.01
                    || expectedEstimatedTotalCostToCustomerCurrency2 - actualEstimatedTotalCostToCustomerCurrency2 > 0.01) {
                remarks.add("EstimatedTotalCostToCustomer is not as expected : " + expectedEstimatedTotalCostToCustomerCurrency2 +
                        " Actual : " + actualEstimatedTotalCostToCustomerCurrency2 + " Currency : " + currency2 +
                        ", VendorSupplierID : " + rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() + " in Action : " + action);
            }

            if (actualEstimatedTotalCostToCustomerCurrency1 == 0 && actualEstimatedTotalCostToCustomerCurrency2 == 0) {
                remarks.add("EstimatedTotalCostToCustomer is not return in VendorSupplierID : " +
                        rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() + " in Action : " + action);
            }
        }
    }

    //
    private void verifyCarRateDetailCompareWithGDSMsg(CarProductType GDSMsgRspCar, CarProductType rspCar, List remarks, String action) {
        if (GDSMsgRspCar == null || rspCar == null) {
            remarks.add("Car from GDS msg is null or Car in " + action + " is null.");
        } else if (GDSMsgRspCar.getCarRateDetail() == null || rspCar.getCarRateDetail() == null) {
            remarks.add("CarRateDetail in Car from GDS msg is null, or CarRateDetail in Car of " + action + " is null.");
        } else {
//            If priced coverage is Required then the CarCost element will have the RequiredCostBoolean as true.
//            If Priced coverage is included in total then it will have FinanceApplicationUnitCount as 0 else 1.
            if (!compareCarCoveragesCost(GDSMsgRspCar, rspCar, remarks, action, MESSAGE_GDS, MESSAGE_Response)) {
                remarks.add("CarRateDetail/CarCoveragesCost in Car of " + action + MESSAGE_Response +
                        " is different from Car from " + MESSAGE_GDS + ".");
            }
            if (!compareCarAdditionalFeesList(GDSMsgRspCar, rspCar, remarks, action, MESSAGE_GDS, MESSAGE_Response)) {
                remarks.add("CarRateDetail/CarAdditionalFeesList in Car of " + action + MESSAGE_Response +
                        " is different from Car from " + MESSAGE_GDS + ".");
            }
        }
    }

    //TODO read dev code see if there is a table to get or sth else.
    private boolean financeSubCategoryList4CoverageType(String financeSubCategory) {
        StringBuffer Coverages = new StringBuffer();
        Coverages.append("%AdditionalLiabilityInsurance%".trim());
        Coverages.append("%AccidentLiabilityWaiver%".trim());
        Coverages.append("%AccidentProtectionInsurance%".trim());
        Coverages.append("%BaggageCoverage%".trim());
        Coverages.append("%CompleteCoverPackage%".trim());
        Coverages.append("%CollisionDamageInsurance%".trim());
        Coverages.append("%CollisionDamageWaiver%".trim());
        Coverages.append("%CollisionDamageWaiverPlus%".trim());
        Coverages.append("%CollisionDamageWaiverReducedLiability%".trim());
        Coverages.append("%CompulsoryInsurance%".trim());
        Coverages.append("%CollisionDamageWaiverLDWCombo%".trim());
        Coverages.append("%CollisionDamageWaiverLDWComboPlus%".trim());
        Coverages.append("%DamageExcessReduction%".trim());
        Coverages.append("%DamageWaiver%".trim());
        Coverages.append("%DamageWaiverPlus%".trim());
        Coverages.append("%FullCoverage%".trim());
        Coverages.append("%GlassTireWaiver%".trim());
        Coverages.append("%InsuranceDeductibleWaiver%".trim());
        Coverages.append("%InsuranceDeductibleWaiverPlus%".trim());
        Coverages.append("%InsuranceDeductibleWaiverReducedLiability%".trim());
        Coverages.append("%Insurance%".trim());
        Coverages.append("%LiabilityDeductibleCoverage%".trim());
        Coverages.append("%LossDamageInsurance%".trim());
        Coverages.append("%LossDamageWaiver%".trim());
        Coverages.append("%LDWDeductibleWaiver%".trim());
        Coverages.append("%LossDamageWaiverReducedLiability%".trim());
        Coverages.append("%LiabilityInsuranceSupplement%".trim());
        Coverages.append("%MexicanInsurance%".trim());
        Coverages.append("%NonWaiverableResponsibility%".trim());
        Coverages.append("%PartialCoverage%".trim());
        Coverages.append("%artialDamageWaiver%".trim());
        Coverages.append("%PersonalAccidentInsurance%".trim());
        Coverages.append("%PersonalAccidentCoverage%".trim());
        Coverages.append("%PersonalAccidentAndEffectsCoverage%".trim());
        Coverages.append("%PersonalEffectsProtection%".trim());
        Coverages.append("%PersonalEffectsCoverage%".trim());
        Coverages.append("%PersonalPassengerProtection%".trim());
        Coverages.append("%PersonalPropertyInsurance%".trim());
        Coverages.append("%RentalLiabilityProtection%".trim());
        Coverages.append("%SuperCollisionDamageWaiver%".trim());
        Coverages.append("%SpecialCoverage%".trim());
        Coverages.append("%SupplementalLiabilityInsurance%".trim());
        Coverages.append("%SuperPersonalAccidentalAndEffectsCoverage%".trim());
        Coverages.append("%SuperPersonalAccidentInsurance%".trim());
        Coverages.append("%SuperTheftProtection%".trim());
        Coverages.append("%TheftProtectionWaiver%".trim());
        Coverages.append("%TheftInsurance%".trim());
        Coverages.append("%TheftProtection%".trim());
        Coverages.append("%ThirdPartyCoverage%".trim());
        Coverages.append("%ThirdPartyInsurance%".trim());
        Coverages.append("%ThirdPartyPlus%".trim());
        Coverages.append("%UninsuredMotoristCoverage%".trim());
        Coverages.append("%UnlimitedMileageWaiver%".trim());
        Coverages.append("%Waiver%".trim());
        Coverages.append("%YoungDriversInsurance%".trim());
        Coverages.append("%MaxCover%".trim());
        Coverages.append("%AccidentExcessReductionPlus%".trim());
        Coverages.append("%AccidentExcessReduction%".trim());
        Coverages.append("%SuperCover%".trim());
        Coverages.append("%ZeroDeductibleOption%".trim());
        Coverages.append("%ProtectionPackage%".trim());
        Coverages.append("%ExtendedProtection%".trim());
        Coverages.append("%ThirdPartyLiability%".trim());
        Coverages.append("%TireAndWindshieldInsurance%".trim());
        Coverages.append("%RoadsideServicePlan%".trim());
        Coverages.append("%DeductibleDamageWaiver%".trim());
        Coverages.append("%SuperTheftAndDamageWaiver%".trim());
        Coverages.append("%AdditionalProtectonInsurance%".trim());
        Coverages.append("%InsuranceExcess%".trim());
        Coverages.append("%EmergencySicknessProtection%".trim());
        Coverages.append("%TotalProtectionPlus%".trim());
        Coverages.append("%LossDamageWaiverPlus%".trim());
        Coverages.append("%TheftLiabilityWaiver%".trim());
        Coverages.append("%TotalCollisionDamageWaiver%".trim());
        Coverages.append("%LegalLiabilityInsurance%".trim());
        Coverages.append("%SnowCover%".trim());

        if (Coverages.toString().indexOf("%" + financeSubCategory.trim() + "%") < 0)
            return false;
        return true;
    }


    private boolean financeSubCategoryList4FeeChargePurpose(String financeSubCategory) {
        StringBuffer fees = new StringBuffer();
        fees.append("%VehicleRental%".trim());
        fees.append("%Drop%".trim());
        fees.append("%Discount%".trim());
        fees.append("%Coverage%".trim());
        fees.append("%Surcharge%".trim());
        fees.append("%Fee%".trim());
        fees.append("%Tax%".trim());
        fees.append("%AdditionalDistance%".trim());
        fees.append("%AdditionalWeek%".trim());
        fees.append("%AdditionalDay%".trim());
        fees.append("%AdditionalHour%".trim());
        fees.append("%AdditionalDrive%".trim());
        fees.append("%YoungDriver%".trim());
        fees.append("%YoungerDriver%".trim());
        fees.append("%Senior%".trim());
        fees.append("%CustomerPickup%".trim());
        fees.append("%CustomerDropOff%".trim());
        fees.append("%VehicleDelivery%".trim());
        fees.append("%VehicleCollection%".trim());
        fees.append("%Fuel%".trim());
        fees.append("%Equipment%".trim());
        fees.append("%PrepayAmount%".trim());
        fees.append("%PayOnArrivalAmount%".trim());
        fees.append("%PrepaidFuel%".trim());
        fees.append("%Adjustment%".trim());
        fees.append("%MandatoryChargesTotal%".trim());
        fees.append("%Subtotal%".trim());
        fees.append("%Optional%".trim());
        fees.append("%ContractFee%".trim());
        fees.append("%AirportSurcharge%".trim());
        fees.append("%AirConditioningSurcharge%".trim());
        fees.append("%RegistrationFee%".trim());
        fees.append("%VehicleLicenseFee%".trim());
        fees.append("%WinterServiceCharge%".trim());
        fees.append("%BaseRate%".trim());
        fees.append("%Mandatory%".trim());
        fees.append("%RateOverride%".trim());
        fees.append("%Tolls%".trim());
        fees.append("%ExtraPassengers%".trim());
        fees.append("%Stop%".trim());
        fees.append("%ExtraStop%".trim());
        fees.append("%WaitTime%".trim());
        fees.append("%SurfaceTransportationCharge%".trim());
        fees.append("%Tip%".trim());
        fees.append("%Gratuity%".trim());
        fees.append("%StandardGratuity%".trim());
        fees.append("%ExtraGratuity%".trim());
        fees.append("%Parking%".trim());
        fees.append("%AirportFee%".trim());
        fees.append("%FuelSurcharge%".trim());
        fees.append("%MeetAndGreet%".trim());
        fees.append("%Greeter%".trim());
        fees.append("%Representative%".trim());
        fees.append("%Phone%".trim());
        fees.append("%CleaningFee%".trim());
        fees.append("%TravelTimeFee%".trim());
        fees.append("%EarlyAMFee%".trim());
        fees.append("%CarSeatFee%".trim());
        fees.append("%PushCart%".trim());
        fees.append("%LatePMFee%".trim());
        fees.append("%HolidaySurcharge%".trim());
        fees.append("%PetSurcharge%".trim());
        fees.append("%StateSurcharge%".trim());
        fees.append("%AirportAccessFee%".trim());
        fees.append("%CityTax%".trim());
        fees.append("%ServiceCharge%".trim());
        fees.append("%PremiumLocationSurcharge%".trim());
        fees.append("%LicenseRecoupmentFee%".trim());
        fees.append("%TourismCharge%".trim());
        fees.append("%ConcessionFee%".trim());
        fees.append("%CustomerFacilityCharge%".trim());
        fees.append("%MotorVehicleCharge%".trim());
        fees.append("%AirportConcessionFeeRecovery%".trim());
        fees.append("%FacilityFee%".trim());
        fees.append("%GoodsAndServiceFee%".trim());
        fees.append("%ConcessionRecoveryFee%".trim());
        fees.append("%BorderCrossingFee%".trim());
        fees.append("%CountyTax%".trim());
        fees.append("%LocationCustomerFee%".trim());
        fees.append("%InfantChildRestraintDeviceSurcharge%".trim());
        fees.append("%RefuelingSurcharge%".trim());
        fees.append("%OutOfHoursFee%".trim());
        fees.append("%AdministrationFee%".trim());
        fees.append("%MaintenanceFacilityFee%".trim());
        fees.append("%EnergySurcharge%".trim());
        fees.append("%VehicleMaintenanceFee%".trim());
        fees.append("%TireManagementFee%".trim());
        fees.append("%TireAndBatteryFee%".trim());
        fees.append("%LessorTax%".trim());
        fees.append("%AgeDifferential%".trim());
        fees.append("%GovernmentRentalSurcharge%".trim());
        fees.append("%GrossReceiptsFee%".trim());
        fees.append("%ReimbursementFee%".trim());
        fees.append("%UDriveItFee%".trim());
        fees.append("%SecurityFee%".trim());
        fees.append("%GovernmentRateSupplement%".trim());
        fees.append("%RoadSafetyProgramFee%".trim());

        if (fees.toString().indexOf("%" + financeSubCategory.trim() + "%") < 0)
            return false;
        return true;
    }
}