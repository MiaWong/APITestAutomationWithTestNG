package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.cartypes.defn.v5.CarAdditionalFeesListType;
import com.expedia.e3.data.cartypes.defn.v5.CarCostType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateDetailType;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/6/2016.
 */
public class CostListReader {
    private CostListReader(){

    }
    /**
     * @param car
     * @param fees
     */
    public static void readDetailFees(CarProductType car, List<Node> fees) {
        if (null == car.getCostList()) {
            car.setCostList(new CostListType());
        }
        if(null == car.getCostList().getCost()) {
            car.getCostList().setCost(new ArrayList<>());
        }
        for (final Node fee : fees) {
            if(fee != null) {
                final CostType cost = readDetailCostFromFee(car, fee);
                if (cost != null) {
                    car.getCostList().getCost().add(cost);
                }
            }
        }
    }

    //<Fee Amount = "24.00" CurrencyCode = "USD" Description = "One-Way Fee" GuaranteedInd = "false" IncludedInRate = "false" Purpose = "2" RequiredInd = "true"/>
    private static CostType readDetailCostFromFee(CarProductType car, Node fee) {
        final String amount = fee.getAttributes().getNamedItem("Amount").getTextContent().trim();
        final String currencyCode = fee.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();

        if (fee.getAttributes().getNamedItem("Purpose").getTextContent().trim().equals("2")) {

            int financeApplicationUnitCount = 1;
            if (Boolean.parseBoolean(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent().trim())) {
                financeApplicationUnitCount = 0;
            }
            return buildCost(amount, currencyCode, "Fee", "Trip", financeApplicationUnitCount,
                    18, 1, 27, "drop-off fee (vendor currency)", true);
        }
        //TODO make sure filter with description name or purpose
        else if (Boolean.parseBoolean(fee.getAttributes().getNamedItem("GuaranteedInd").getTextContent().trim()) ||
                fee.getAttributes().getNamedItem("Description").getTextContent().trim().equals("One-Way Fee")) {

            String financeApplicationCode = "Trip";
            int financeApplicationUnitCount = 1;
            final StringBuilder description = new StringBuilder();
            description.append(fee.getAttributes().getNamedItem("Description").getTextContent().trim());
            if (fee.getAttributes().getNamedItem("Description").getTextContent().trim().equals("One-Way Fee")) {
                description.append("drop-off fee (vendor currency)");
                return buildCost(amount, currencyCode, "Fee", financeApplicationCode, financeApplicationUnitCount,
                        18, 1, 27, description.toString(), true);
            }
            if (Boolean.parseBoolean(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent().trim())) {
                financeApplicationCode = "Included";
                financeApplicationUnitCount = 0;
                description.append(". Included in rate. Amount: ").append(amount).append(' ').append(currencyCode);
            }

            return buildCost(amount, currencyCode, "Taxes", financeApplicationCode, financeApplicationUnitCount,
                    3, 1, 10, description.toString(), true);
        } else {
            readAdditionalFeeFromFee(car, fee);
        }

        return null;
    }

    public static void readAdditionalFeeFromFee(CarProductType car, Node fee) {
        if (car.getCarRateDetail() == null) {
            car.setCarRateDetail(new CarRateDetailType());
        }
        if (car.getCarRateDetail().getCarAdditionalFeesList() == null) {
            car.getCarRateDetail().setCarAdditionalFeesList(new CarAdditionalFeesListType());
        }
        if (car.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() == null) {
            car.getCarRateDetail().getCarAdditionalFeesList().setCarAdditionalFees(new ArrayList<CarCostType>());
        }
        final CarCostType carCostType = new CarCostType();

        final boolean requiredBoolean = Boolean.parseBoolean(fee.getAttributes().getNamedItem("RequiredInd").getTextContent());
        carCostType.setRequiredCostBoolean(requiredBoolean);

        final String amount = fee.getAttributes().getNamedItem("Amount").getTextContent().trim();
        final String currencyCode = fee.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();

        carCostType.setMultiplierOrAmount(buildMultiplierOrAmount(currencyCode, amount));

        carCostType.setFinanceCategoryCode("Fee");
        //TODO should get from Purpose, debug code to know where get them, may in table or some where
        final String feePurpose = fee.getAttributes().getNamedItem("Purpose").getTextContent();
        String financeSubCategoryCode = null;

        if(null != feePurpose && feePurpose.equals("7")){
            financeSubCategoryCode = "Tax";
        }
        else if(null != feePurpose && feePurpose.equals("8")){
            financeSubCategoryCode = "AdditionalDistance";
        }

        carCostType.setFinanceSubCategoryCode(financeSubCategoryCode);


        carCostType.setFinanceApplicationCode("Trip");
        if (Boolean.parseBoolean(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent().trim())) {
            carCostType.setFinanceApplicationUnitCount(0L);
        } else {
            carCostType.setFinanceApplicationUnitCount(1L);
        }
        //f the fees is is included in total then it will have FinanceApplicationUnitCount as 0 else 1.
        // //Fees are generally not included in total rate so it should be 1 always.
        carCostType.setDescriptionRawText(fee.getAttributes().getNamedItem("Description").getTextContent().trim());

        car.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().add(carCostType);
    }

    /// <summary>
    /// read vehicleCharges to costList
    /// </summary>
    /// <param name="costList"></param>
    /// <param name="vehicleChargesNode"></param>
    /// LegacyFinanceKey values refer to wiki: https://confluence/display/SSG/Maserati+SCS+Costs
    public static CostListType readBaseCosts(CostListType costList, Node vehicleChargesNode, Node totalChargesNode)
    {
        List<Node> charges = new ArrayList<Node>();
        if(null != vehicleChargesNode){
            charges = PojoXmlUtil.getNodesByTagName(vehicleChargesNode, "VehicleCharge");
        }

        if(costList.getCost() == null)
        {
            costList.setCost(new ArrayList<CostType>());
        }

        //Read base
        final String baseAmount = readBaseCost(costList, charges);

        //Set MiscBase
        setMiscBaseCost(costList, baseAmount, totalChargesNode);

        return costList;
    }

    //read base cost
    public static String readBaseCost(CostListType costList, List<Node> chargeNodeList)
    {
        String currencyCode = "";
        String baseAmount = "";
        if(costList.getCost() == null)
        {
            costList.setCost(new ArrayList<CostType>());
        }

        for (final Node charge : chargeNodeList)
        {
            //Bases
            if (charge.getAttributes().getNamedItem("Purpose").getTextContent().equals("35"))
            {
                baseAmount = charge.getAttributes().getNamedItem("Amount").getTextContent();
                currencyCode = charge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                costList.getCost().add(buildCost(baseAmount, currencyCode, "Base", "Trip", 1, 1, 1,
                        CommonEnumManager.BaseCostLegacyMonetaryCalculationID.Trip.getBaseCostLegacyMonetaryCalculationID(), "Base", false));
            }
        }

        return baseAmount;

    }

    //Calculate and set MiscBase
    public static void setMiscBaseCost(CostListType costList,  String baseAmount, Node totalChargesNode)
    {
        //get Total first
        String total = "";
        String currencyCode = "";
        if(null != totalChargesNode)
        {
            total = totalChargesNode.getAttributes().getNamedItem("RateTotalAmount").getTextContent();
            currencyCode = totalChargesNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
        }

        //Calculte miscBase
        double miscBase = 0;
        if(!StringUtils.isEmpty(total) && !StringUtils.isEmpty(baseAmount)){
            miscBase = Double.parseDouble(total) - Double.parseDouble(baseAmount);
        }

        //Build miscBase if exist
        if (Math.abs(miscBase) > 0.001)
        {
            costList.getCost().add(buildCost(String.valueOf(miscBase), currencyCode, "MiscBase", "Trip", 1, 8, 1, 6,
                    "misc base rate (may include extra days, etc) (vendor currency)", false));
        }
    }

    /// <summary>
    /// Read total charge to costList
    /// </summary>
    /// <param name="costList"></param>
    /// <param name="totalChargesNode"></param>
    public static double readTotalCharge(CostListType costList, Node totalChargesNode)
    {
        final String total = totalChargesNode.getAttributes().getNamedItem("RateTotalAmount").getTextContent();
        final String currencyCode = totalChargesNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
        costList.getCost().add(buildCost(total, currencyCode, "Total", "Total", 1, 0, 0, 0, "Total (vendor currency)", false));
        return Double.parseDouble(total);
    }

    /// <summary>
    /// Calculate total from other Cost and build the cost
    /// </summary>
    /// <param name="costList"></param>
    public static void calAndBuildTotal(CostListType costList)
    {
        double total = 0;
        String currencyCode = null;
        for (final CostType cost : costList.getCost()) {
            total += CostPriceCalculator.calculateCostAmount(cost, 0, true);
            currencyCode = cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
        }

        costList.getCost().add(buildCost(String.valueOf(total), currencyCode, "Total", "Total", 1, 0, 0, 0, "Total (vendor currency)", false));
    }

    /// <summary>
    /// Read fees from costList for Search and CostAvail
    /// </summary>
    /// <param name="costList"></param>
    /// <param name="feeNodeList"></param>
    public static void readCostAvailFees(CarProductType car, List<Node> feeNodeList)
    {
        double totalMisc = 0;
        String currencyCode = null;

        if(CompareUtil.isObjEmpty(car.getCostList().getCost()))
        {
            car.getCostList().setCost(new ArrayList<CostType>());
        }
        for (final Node feeNode : feeNodeList)
        {
            if (feeNode.getAttributes().getNamedItem("Description").getTextContent().equals("Mileage Charge"))
            {
                readAdditionalFeeFromFee(car, feeNode);
            }
            else
            {
                final double feeMisc = buildCostAvailFee(car, feeNode);
                totalMisc += feeMisc;
                if (feeMisc > 0.01) {
                    currencyCode = feeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                }
            }

        }
        //Build Misc in CostList
        if (totalMisc > 0.01)
        {
            car.getCostList().getCost().add(buildCost(String.valueOf(totalMisc), currencyCode,"Misc", "Trip", 1, 8, 1, 6,
                    "misc charges (taxes, fees, etc) (vendor currency)", false));
        }


    }

    public static double buildCostAvailFee(CarProductType car, Node feeNode)
    {
        double misc = 0;
        if (feeNode.getAttributes().getNamedItem("Purpose").getTextContent().equals("2") && !Boolean.parseBoolean(feeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent()))
        {
            final String amount = feeNode.getAttributes().getNamedItem("Amount").getTextContent();
            final String currencyCode = feeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            car.getCostList().getCost().add(buildCost(amount, currencyCode,"Fee", "Trip", 1, 18, 1, 27,
                    "drop-off fee (vendor currency)", false));
        }
        else if (!Boolean.parseBoolean(feeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent()) && Boolean.parseBoolean(feeNode.getAttributes().getNamedItem("GuaranteedInd").getTextContent()))
        {
            misc = Double.parseDouble(feeNode.getAttributes().getNamedItem("Amount").getTextContent());
        }
        else if (Boolean.parseBoolean(feeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent()) && Boolean.parseBoolean(feeNode.getAttributes().getNamedItem("GuaranteedInd").getTextContent()))
        {
            final String amount = feeNode.getAttributes().getNamedItem("Amount").getTextContent();
            final String currencyCode = feeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            String description = feeNode.getAttributes().getNamedItem("Description").getTextContent();
            description = String.format("%s. Included in rate. Amount: %s %s", description, amount, currencyCode);
            car.getCostList().getCost().add(buildCost(amount, currencyCode,"Taxes", "Included", 0, 3, 1, 10,
                    description, true));
        }
        else {
            readAdditionalFeeFromFee(car, feeNode);
        }
        return misc;
    }

    public static CostType buildCost(String amount, String currencyCode, String financeCategoryCode,
                                     String financeApplicationCode, long financeApplicationUnitCount, int legacyMonetaryClassID,
                                     int legacyMonetaryCalculationSystemID, int legacyMonetaryCalculationID, String description,
                                     boolean buildZeroUnitCount) {
        final CostType cost = new CostType();
        cost.setMultiplierOrAmount(buildMultiplierOrAmount(currencyCode, amount));

        cost.setFinanceCategoryCode(financeCategoryCode);
        cost.setFinanceApplicationCode(financeApplicationCode);
        if (financeApplicationUnitCount != 0 || buildZeroUnitCount) {
            cost.setFinanceApplicationUnitCount(financeApplicationUnitCount);
        }
        if (buildZeroUnitCount && financeApplicationUnitCount == 0) {
            cost.setFinanceApplicationCode("Included");
        }

        if (null == cost.getLegacyFinanceKey()) {
            cost.setLegacyFinanceKey(new LegacyFinanceKeyType());
        }
        cost.getLegacyFinanceKey().setLegacyMonetaryClassID(legacyMonetaryClassID);
        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(legacyMonetaryCalculationSystemID);
        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(legacyMonetaryCalculationID);
        if (description != null) {
            cost.setDescriptionRawText(description);
        }
        return cost;
    }

    public static MultiplierOrAmountType buildMultiplierOrAmount(String currencyCode, String amount) {
        final MultiplierOrAmountType multiplierOrAmountType = new MultiplierOrAmountType();

        final int amountDecimal = Integer.parseInt(StringUtils.isEmpty(amount) ? "0" : amount.replace(".", ""));
        final int decimalPlaceCount = !StringUtils.isEmpty(amount) && amount.contains(".") ? amount.split("\\.")[1].length() : 0;

        if (!StringUtils.isEmpty(currencyCode)) {
            setCurrencyAmount(multiplierOrAmountType, currencyCode, amountDecimal, decimalPlaceCount);
        }
        if (StringUtils.isEmpty(currencyCode)) {
            setMultiplier(multiplierOrAmountType, amountDecimal, decimalPlaceCount);
        }

        return multiplierOrAmountType;
    }

    public static void setCurrencyAmount(MultiplierOrAmountType multiplierOrAmountType, String currencyCode, int amountDecimal, int decimalPlaceCount ){
        if (multiplierOrAmountType.getCurrencyAmount() == null) {
            multiplierOrAmountType.setCurrencyAmount(new CurrencyAmountType());
        }
        if (multiplierOrAmountType.getCurrencyAmount().getAmount() == null) {
            multiplierOrAmountType.getCurrencyAmount().setAmount(new AmountType());
        }
        multiplierOrAmountType.getCurrencyAmount().setCurrencyCode(currencyCode);
        multiplierOrAmountType.getCurrencyAmount().getAmount().setDecimal(amountDecimal);
        multiplierOrAmountType.getCurrencyAmount().getAmount().setDecimalPlaceCount(decimalPlaceCount);
    }

    public static void setMultiplier(MultiplierOrAmountType multiplierOrAmountType, int amountDecimal, int decimalPlaceCount ){
        if (multiplierOrAmountType.getMultiplier() == null) {
            multiplierOrAmountType.setMultiplier(new MultiplierType());
        }
        multiplierOrAmountType.getMultiplier().setDecimal(amountDecimal);
        multiplierOrAmountType.getMultiplier().setDecimalPlaceCount(decimalPlaceCount);
    }

    /// <summary>
    /// Read car Cost List from response - for getDetails/Reserve
    /// </summary>
    /// <param name="car"></param>
    /// <param name="xmlDoc"></param>
    public static void readCostListFromXmlDoc(CarProductType car, Node response) {
        final Node totalChargeNode = (PojoXmlUtil.getNodesByTagName(response, "TotalCharge").size() > 0) ? PojoXmlUtil.getNodeByTagName(response, "TotalCharge") : null;
        if(car.getCostList() == null)
        {
            car.setCostList(new CostListType());
        }
        readBaseCosts(car.getCostList(), PojoXmlUtil.getNodeByTagName(response, "VehicleCharges"), totalChargeNode);
        readDetailFees(car, PojoXmlUtil.getNodesByTagName(response, "Fee"));//Read DC and Misc from Fee

        calAndBuildTotal(car.getCostList());//Calculate total from costList - other Cost
    }

    /// <summary>
    /// Bild costList
    /// </summary>
    /// <param name="costList"></param>
    /// <param name="VehAvailNode"></param>
    public static void buildCostAvailCostList(CarProductType car, Node vehAvailNode, boolean forDetail)
    {
        if(CompareUtil.isObjEmpty(car.getCostList()))
        {
            car.setCostList(new CostListType());
        }
        CostListType costList = readBaseCosts(car.getCostList(), PojoXmlUtil.getNodeByTagName(vehAvailNode, "VehicleCharges"), PojoXmlUtil.getNodeByTagName(vehAvailNode, "TotalCharge"));//Base rate
        if (!forDetail){
            readCostAvailFees(car, PojoXmlUtil.getNodesByTagName(vehAvailNode, "Fee"));//Read DC and Misc from Fee
        }
        if(forDetail) {
            readDetailFees(car, PojoXmlUtil.getNodesByTagName(vehAvailNode, "Fee"));
            costList = car.getCostList();
        }
        calAndBuildTotal(costList);
        car.setCostList(costList);
    }



}
