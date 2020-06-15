package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.commonreader;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.DateLocationReader.readDate;

/**
 * Created by fehu on 1/12/2017.
 */
public class VehAvailNodeHelper {

    final private static Logger LOGGER = Logger.getLogger(VehAvailNodeHelper.class);
    public static final String CDW = "CDW";


    private VehAvailNodeHelper() {
    }

    @SuppressWarnings("PMD")
    public static void costList(Node nodeObject, CarProductType carProductType, CarsSCSDataSource scsDataSource) throws DataAccessException {
        // all PricedCoverage items are already included in Total.
        final List<CostType> costTypes = getCostTypes(carProductType);

        //MN does not return base rate breakdown. We always get Total per trip
        String mnTotalCost = "";
        String localCurrency = "";
        String mnOriginalTotal = "";
        String mnbaseRate = "";
        String supplierCurrency = "";
        String oneWayFeeAmount = "";
        String oneWayFeeCurrency = "";
        final List<Node> vehicleCharges = PojoXmlUtil.getNodesByTagName(nodeObject, "VehicleCharge");
        for (final Node vehicleCharge : vehicleCharges)
        {
            if ("preferred".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnTotalCost = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                localCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();

            }
            if ("original".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnOriginalTotal = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                supplierCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            }
            if ("baserate".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnbaseRate = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
            }
            if ("2".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                oneWayFeeAmount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                oneWayFeeCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            }

        }


        //create total cost Cost element
        totalCost(costTypes, mnTotalCost, localCurrency);

        //create base rate Cost element
        costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(mnTotalCost, localCurrency), "Trip", "Base", 1l, "base rate (vendor currency)", getLegacyFinanceKeyType(1l, 14l, 1l));

        //MN Commission
        //Calculate Commission when Total and BaseRate are provided by MN and the difference is greater than 0
        final BigDecimal mnCommission = new BigDecimal(mnOriginalTotal).subtract(new BigDecimal(mnbaseRate));
        if (mnCommission.compareTo(BigDecimal.ZERO) > 0)
        {
            costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(String.valueOf(mnCommission),supplierCurrency), "Included", "Commission", 0l, "", getLegacyFinanceKeyType(19l, 0, 93l));
        }

        //MN Transaction Fees
        transactionFees(costTypes, mnTotalCost, localCurrency);

        //one-way fee (drop charge)
        if (StringUtil.isNotBlank(oneWayFeeAmount) && StringUtil.isNotBlank(oneWayFeeAmount)) {
            costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(oneWayFeeAmount, oneWayFeeCurrency), "Trip", "Fee", 1l, "one-way fee", getLegacyFinanceKeyType(18l, 27l, 1l));
        }

        //get all included fees and priced coverages
        feeCostList(nodeObject, costTypes);
    }

    public static void transactionFees(List<CostType> costTypes, String mnTotalCost, String localCurrency) throws DataAccessException {
     /*   final List<SupplierConfiguration> supplierConfigurationList = scsDataSource.getSupplierSetting("Cost.TransactionFeesPercent","stt05", carProductType.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
        if(CollectionUtils.isNotEmpty(supplierConfigurationList) && Integer.parseInt(supplierConfigurationList.get(0).getSettingValue()) >0
                || CollectionUtils.isEmpty(supplierConfigurationList))
        {*/
            costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(String.valueOf((float) Math.round((Double.parseDouble(mnTotalCost) * 3 * 100)) / 10000), localCurrency), "Trip", "ProviderTransactionFees", 1l, "MN Transaction Fees", getLegacyFinanceKeyType(18l, 0l, 92l));
       // }
    }


    @SuppressWarnings("PMD")
    public static void detailsCostList(Node nodeObject, CarProductType carProductType, CarsSCSDataSource scsDataSource) throws DataAccessException {

        final List<CostType> costTypes = getCostTypes(carProductType);

        String mnTotalCost = "";
        String localCurrency = "";
        String mnOriginalTotal = "";
        String mnbaseRate = "";
        String supplierCurrency = "";

        final List<Node> vehicleCharges = PojoXmlUtil.getNodesByTagName(nodeObject, "VehicleCharge");
        for (final Node vehicleCharge : vehicleCharges)
        {
            if ("preferred".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnTotalCost = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                localCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                //create total cost Cost element
                totalCost(costTypes, mnTotalCost, localCurrency);
            }
           else if ("original".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnOriginalTotal = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                supplierCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            }

            //baseRate will be add later
           else if ("baserate".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnbaseRate = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();

            }
            //drop-off fee (drop charge)
            else if ("2".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                final String oneWayFeeAmount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final String oneWayFeeCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(oneWayFeeAmount, oneWayFeeCurrency), "Trip", "Fee", 1l, "drop-off fee", getLegacyFinanceKeyType(18l, 27l, 1l));

            }
            //ExtraWeekly fee (drop charge)
            else if ("9".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                final String extraFeeAmount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final  String extraFeeCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
               costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(extraFeeAmount, extraFeeCurrency), "ExtraWeekly", "base", 1l, "base rate extra week (vendor currency)", getLegacyFinanceKeyType(1l, 8l, 1l));


            }
            else if ("10".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                final String amount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), "ExtraDaily", "base", 1l, "base rate extra day (vendor currency)", getLegacyFinanceKeyType(1l, 7l, 1l));

            }
            else if ("11".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                final String amount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), "ExtraHourly", "base", 1l, "base rate extra hour (vendor currency)", getLegacyFinanceKeyType(1l, 7l, 1l));

            }
            else  if ("35".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                final String amount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), "Trip", "base", 1l, "base rate", getLegacyFinanceKeyType(1l, 14l, 1l));
            }
            else if ("7".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                final String amount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), "Trip", "Taxes", 1l, "Tax", getLegacyFinanceKeyType(3l, 10l, 1l));
            }
            else
            {
                final String amount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                final String description = "Fee - Purpose: " + vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), "Trip", "Fees", 1l, description, getLegacyFinanceKeyType(18l, 27l, 1l));

            }

        }
        // priceCoverages
        float accNonIncludedFeesAndTaxes = feeDetailsCostList(nodeObject, costTypes);

        //Misc Fees
        accNonIncludedFeesAndTaxes = accNonIncludedFeesAndTaxes +  priceCoveragesCostList(nodeObject, costTypes);

       //create base rate Cost element
        final String baseRate = String.valueOf(Float.parseFloat(mnTotalCost) - accNonIncludedFeesAndTaxes);
        costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(baseRate, localCurrency), "Trip", "Base", 1l, "base rate (vendor currency)", getLegacyFinanceKeyType(1l, 14l, 1l));


         //MN Transaction Fees
        transactionFees(costTypes, mnTotalCost, localCurrency);
        //MN Commission
        //Calculate Commission when Total and BaseRate are provided by MN and the difference is greater than 0
        final float mnCommission = Float.parseFloat(mnOriginalTotal) - Float.parseFloat(mnbaseRate);
        if (mnCommission > 0)
        {
            costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(String.valueOf(mnCommission),supplierCurrency), "Included", "Commission", 0l, "MN Specified Commission", getLegacyFinanceKeyType(19l, 0, 93l));
        }
    }

    @SuppressWarnings("PMD")
    private static float priceCoveragesCostList(Node nodeObject, List<CostType> costTypes) {
        float accNonIncludedFeesAndTaxes = 0;
        final List<Node> priceCoverages = PojoXmlUtil.getNodesByTagName(nodeObject, "PricedCoverage");
        for (final Node priceCoverage : priceCoverages)
        {
            final Node chargeNode = getSpecifiedChildNode(priceCoverage, "Charge");
            if (null != chargeNode && null != chargeNode.getAttributes().getNamedItem("IncludedInRate")
                    && "false".equalsIgnoreCase(chargeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent()))
            {
                final String amount = chargeNode.getAttributes().getNamedItem("Amount").getTextContent();
                accNonIncludedFeesAndTaxes = Float.parseFloat(amount);
                return accNonIncludedFeesAndTaxes;
            }

            if (null != chargeNode && null != chargeNode.getAttributes().getNamedItem("IncludedInRate")
                    && "true".equalsIgnoreCase(chargeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent()))
            {
                final  Node coverageNode = getSpecifiedChildNode(priceCoverage, "Coverage");
                final String amount = chargeNode.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = chargeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                final String coverageCode = coverageNode.getAttributes().getNamedItem("Code").getTextContent();

                boolean containedCode = false;
                for(final CostType costType : costTypes)
               {
                  if (costType.getDescriptionRawText().contains(coverageCode) )
                   {
                       containedCode = true;
                       break;
                   }

               }
                if (!containedCode && StringUtil.isNotBlank(amount)) {
                    Node coverageTypeNode = coverageNode.getAttributes().getNamedItem("CoverageType");
                    String descrtiption = null != coverageTypeNode ? coverageTypeNode.getTextContent() : chargeNode.getAttributes().getNamedItem("Description").getTextContent();
                    costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), "Included", "Taxes", 0l
                            , descrtiption, getLegacyFinanceKeyType(3l, 10l, 1l));
                }
            }
        }
        return accNonIncludedFeesAndTaxes;
    }

    public static Node  getSpecifiedChildNode(Node priceCoverage, String  specifiedNodeName) {

        final NodeList childNodes = priceCoverage.getChildNodes();
        for (int i=0; i < childNodes.getLength();i++)
        {
            if (childNodes.item(i).getNodeName().contains(specifiedNodeName))
            {
                return childNodes.item(i);
            }
        }
        LOGGER.info("getSpecifiedNode  method: reutn null.");
        return null;
    }

    public static float feeDetailsCostList(Node vehAvail, List<CostType> costTypes) {
        float accNonIncludedFeesAndTaxes = 0;
        final List<Node> fees = PojoXmlUtil.getNodesByTagName(vehAvail, "Fee");

        for(final Node fee : fees)
        {
            if ("false".equalsIgnoreCase(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent()))
            {
                final String feeRate = fee.getAttributes().getNamedItem("Amount").getTextContent();
                accNonIncludedFeesAndTaxes = Float.parseFloat(feeRate);
                return accNonIncludedFeesAndTaxes;
     }
            if ("true".equalsIgnoreCase(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent()))
            {
                final String mscFee = fee.getAttributes().getNamedItem("Amount").getTextContent();
                final String mscCurrency = fee.getAttributes().getNamedItem("CurrencyCode").getTextContent();
               final String description = fee.getAttributes().getNamedItem("Description").getTextContent();
                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(mscFee, mscCurrency), "Included", "Taxes", 0l, description, getLegacyFinanceKeyType(3l, 10l, 1l));
            }

        }
        return accNonIncludedFeesAndTaxes;
    }

    public static void feeCostList(Node vehAvail, List<CostType> costTypes) {
        final List<Node> fees = PojoXmlUtil.getNodesByTagName(vehAvail, "Fee");
        for(final Node fee : fees)
        {
            if ("true".equalsIgnoreCase(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent()))
            {
                final String mscFee = fee.getAttributes().getNamedItem("Amount").getTextContent();
                final String mscCurrency = fee.getAttributes().getNamedItem("CurrencyCode").getTextContent();

                costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(mscFee, mscCurrency), "Included", "Misc", 0l, "misc charges (taxes, fees, extra days, etc) (vendor currency)", getLegacyFinanceKeyType(8l, 6l, 1l));
            }

        }
    }

    public static List<CostType> getCostTypes(CarProductType carProductType) {
        final CostListType costListType = new CostListType();
        final List<CostType> costTypes = new ArrayList<>();
        costListType.setCost(costTypes);
        carProductType.setCostList(costListType);
        return costTypes;
    }

    public static void costList(List<CostType> costTypes, CurrencyAmountType currencyAmountType, String trip, String base, long financeApplicationUnitCount, String descriptionRawText, LegacyFinanceKeyType legacyFinanceKeyType) {
        final CostType baseCostType = new CostType();
        final MultiplierOrAmountType baseMultiplierOrAmountType = new MultiplierOrAmountType();
        baseMultiplierOrAmountType.setCurrencyAmount(currencyAmountType);
        baseCostType.setMultiplierOrAmount(baseMultiplierOrAmountType);
        baseCostType.setFinanceApplicationCode(trip);
        baseCostType.setFinanceCategoryCode(base);
        baseCostType.setFinanceApplicationUnitCount(financeApplicationUnitCount);
        baseCostType.setDescriptionRawText(descriptionRawText);

        if (legacyFinanceKeyType != null)
        {
           baseCostType.setLegacyFinanceKey(legacyFinanceKeyType);
        }
        costTypes.add(baseCostType);
    }

    public static void totalCost(List<CostType> costTypes, String mnTotalCost, String localCurrency) {
        final CostType totalCostType = new CostType();
        final MultiplierOrAmountType totalMultiplierOrAmountType = new MultiplierOrAmountType();
        totalMultiplierOrAmountType.setCurrencyAmount(VehAvailNodeHelper.getCurrencyAmountType(mnTotalCost,localCurrency));
        totalCostType.setMultiplierOrAmount(totalMultiplierOrAmountType);
        totalCostType.setFinanceApplicationCode("Total");
        totalCostType.setFinanceCategoryCode("Total");
        totalCostType.setFinanceApplicationUnitCount(1l);
        totalCostType.setDescriptionRawText("Total (vendor currency)");
        costTypes.add(totalCostType);
    }

    public static LegacyFinanceKeyType getLegacyFinanceKeyType(long legacyMonetaryClassID, long legacyMonetaryCalculationID, long legacyMonetaryCalculationSystemID) {
        final LegacyFinanceKeyType commissionLegacyFinanceKeyType = new LegacyFinanceKeyType();
        commissionLegacyFinanceKeyType.setLegacyMonetaryClassID(legacyMonetaryClassID);
        commissionLegacyFinanceKeyType.setLegacyMonetaryCalculationID(legacyMonetaryCalculationID);
        commissionLegacyFinanceKeyType.setLegacyMonetaryCalculationSystemID(legacyMonetaryCalculationSystemID);
        return commissionLegacyFinanceKeyType;
    }
    public static void carRateDetail(Node nodeObject, CarProductType carProductType, CarsSCSDataSource scsDataSource) throws DataAccessException {

        final CarRateDetailType carRateDetailType = new CarRateDetailType();
        carProductType.setCarRateDetail(carRateDetailType);

        //AdditionalFee list
        final CarAdditionalFeesListType carAdditionalFeesListType = new CarAdditionalFeesListType();
        carRateDetailType.setCarAdditionalFeesList(carAdditionalFeesListType);
        final List<CarCostType> carCostTypes = new ArrayList<>();
        carAdditionalFeesListType.setCarAdditionalFees(carCostTypes);

        //GetAdditionalFeesFromPricedCoveragesList
        // If a MN PricedCoverage is not included in rate, it is an additional fee to be paid at the counter
        getAdditionalFeesFromPricedCoveragesList(nodeObject, carCostTypes, scsDataSource);

        //GetAdditionalFeesFromFeesList
        getAdditionalFeesFromFeesList(nodeObject, carCostTypes, scsDataSource);

        //Priced Coverages Cost List
        getCoveragesCostList(nodeObject, carRateDetailType);

        //Adding Test Case For CASSS-10716 : Insurance included for GDSP Prepaid inventory
        getCarInsuranceIncludedInRateFromPricedCoveragesList(carProductType,
                PojoXmlUtil.getNodesByTagName(nodeObject, "PricedCoverages").get(0).getChildNodes());
    }
    public static void carVehicleOption(Node response, CarsSCSDataSource scsDataSource, CarProductType carProduct) throws DataAccessException {
        final CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
        final List<CarVehicleOptionType> carVehicleOptionTypes = new ArrayList<>();
        carVehicleOptionListType.setCarVehicleOption(carVehicleOptionTypes);
        carProduct.setCarVehicleOptionList(carVehicleOptionListType);

        final List<Node> priceEquipNodes = PojoXmlUtil.getNodesByTagName(response, "PricedEquip");
        if (CollectionUtils.isNotEmpty(priceEquipNodes)) {
            for (final Node priceEquipNode : priceEquipNodes) {
                final String description = PojoXmlUtil.getNodeByTagName(priceEquipNode, "Description").getTextContent();
                String desc = "";
               if(null != PojoXmlUtil.getNodeByTagName(priceEquipNode, "MinMax"))
               {
                   desc = PojoXmlUtil.getNodeByTagName(priceEquipNode, "MinMax").getAttributes().getNamedItem("MaxCharge").getTextContent();
               }
                final String descriptionRawText = description + "\r\nMaxPrice: " + desc;
                final String equipMentCode = PojoXmlUtil.getNodeByTagName(priceEquipNode, "Equipment").getAttributes().getNamedItem("EquipType").getTextContent();
                final CarVehicleOptionType carVehicleOptionType = new CarVehicleOptionType();

                final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap("CarSpecialEquipment", equipMentCode);
                if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMaps)) {
                    carVehicleOptionType.setCarSpecialEquipmentCode(externalSupplyServiceDomainValueMaps.get(0).getDomainValue());
                }

                carVehicleOptionType.setCarVehicleOptionCategoryCode("special equipment");
                carVehicleOptionType.setDescriptionRawText(descriptionRawText);
                final List<CostType> costTypes = getCostTypes(priceEquipNode, description);
                carVehicleOptionType.setCost(costTypes.get(0));

                carVehicleOptionTypes.add(carVehicleOptionType);
            }
        }
    }

    public static List<CostType> getCostTypes(Node priceEquipNode, String description) {
        final Node chargeNode = PojoXmlUtil.getNodeByTagName(priceEquipNode, "Charge");
        final String amount = chargeNode.getAttributes().getNamedItem("Amount").getTextContent();
        final String currency = chargeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
        final  String unitName = PojoXmlUtil.getNodeByTagName(priceEquipNode, "Calculation").getAttributes().getNamedItem("UnitName").getTextContent();
        String financeApplicationCode = "";
        if ("1-per day".equalsIgnoreCase(unitName) || "PreferedCurrencyPrice-per day".equalsIgnoreCase(unitName))
        {
            financeApplicationCode = "Daily";
        }
        else
        {
            financeApplicationCode = "Trip";
        }

        final List<CostType> costTypes = new ArrayList<>();
        VehAvailNodeHelper.costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(amount, currency), financeApplicationCode, "SpecialEquipment", 1l, description, null);
        return costTypes;
    }

    public static void getCoveragesCostList(Node nodeObeject, CarRateDetailType carRateDetailType) {
        final CarCoveragesCostListType carCoveragesCostListType = new CarCoveragesCostListType();
        final List<CarCoveragesCostType> carCoveragesCostTypes = new ArrayList<>();
        carCoveragesCostListType.setCarCoveragesCost(carCoveragesCostTypes);

        carRateDetailType.setCarCoveragesCostList(carCoveragesCostListType);

        final List<Node> pricedCoverages = PojoXmlUtil.getNodesByTagName(nodeObeject, "PricedCoverage");

        for (final Node pricedCoverage : pricedCoverages) {
            final String coverageCode = PojoXmlUtil.getNodeByTagName(pricedCoverage, "Coverage").getAttributes().getNamedItem("Code").getTextContent();

            if ("CDW".equalsIgnoreCase(coverageCode) || "ExcessPrice".equalsIgnoreCase(coverageCode)) {
                final Node chargeNode = getChargeNode(pricedCoverage);
                long unitAcount;
                if ("true".equalsIgnoreCase(chargeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent())) {
                    unitAcount = 0;
                } else {
                    unitAcount = 1;
                }
                final String amount = chargeNode.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = chargeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                final String description = chargeNode.getAttributes().getNamedItem("Description").getTextContent();


                final String financeSubCategoryCode = getFinanceSubCategoryCode(coverageCode);
                if (StringUtil.isNotBlank(currency)) {
                    final CarCoveragesCostType carCoveragesCostType = new CarCoveragesCostType();
                    final CarCostType carCostType = VehAvailNodeHelper.createCarCostType(unitAcount, amount, currency, description, financeSubCategoryCode, "Coverages");

                    carCoveragesCostType.setCarCost(carCostType);
                    if ("CDW".equalsIgnoreCase(coverageCode))
                    {
                        final CarDeductibleType carDeductibleType = new CarDeductibleType();
                        final SimpleCurrencyAmountType simpleCurrencyAmountType = new SimpleCurrencyAmountType();
                        simpleCurrencyAmountType.setCurrencyCode("EUR");
                        simpleCurrencyAmountType.setSimpleAmount("950");
                        carDeductibleType.setExcessAmount(simpleCurrencyAmountType);
                        carCoveragesCostType.setCarDeductible(carDeductibleType);
                    }
                    carCoveragesCostTypes.add(carCoveragesCostType);
                }

            }

        }
    }
    public static void getAdditionalFeesFromFeesList(Node nodeObject, List<CarCostType> carCostTypes, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final List<Node> fees = PojoXmlUtil.getNodesByTagName(nodeObject, "Fee");
        for (final Node fee : fees) {
            if ("false".equalsIgnoreCase(fee.getAttributes().getNamedItem("IncludedInRate").getTextContent()) || "false".equalsIgnoreCase(fee.getAttributes().getNamedItem("IncludedInEstTotalInd").getTextContent())) {
                final String feeDescription = fee.getAttributes().getNamedItem("Description").getTextContent();
                final String feeAmount = fee.getAttributes().getNamedItem("Amount").getTextContent();
                final String feeCurrency = fee.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                final String financeCategoryCode = "Fee";
                final String unitCount = "1";
                final String feeCode = StringUtil.isNotBlank(feeDescription) ? feeDescription.substring(feeDescription.indexOf('('), feeDescription.indexOf(')') + 1) : null;
                String financeSubCategoryCode = null;

                final List<ExternalSupplyServiceDomainValueMap> extMaps =  scsDataSource.getExternalSupplyServiceDomainValueMap("CarVehicleChargePurpose" ,feeCode);
                   if (CollectionUtils.isNotEmpty(extMaps))
                   {
                       financeSubCategoryCode = extMaps.get(0).getDomainValue();
                   }

                if (StringUtil.isNotBlank(feeAmount) && StringUtil.isNotBlank(feeCurrency)) {
                    final CarCostType carCostType = VehAvailNodeHelper.createCarCostType(Long.parseLong(unitCount), feeAmount, feeCurrency, feeDescription, financeSubCategoryCode, financeCategoryCode);
                    carCostTypes.add(carCostType);
                }

            }
        }
    }
    public static void getAdditionalFeesFromPricedCoveragesList(Node nodeObeject, List<CarCostType> carCostTypes,CarsSCSDataSource scsDataSource) throws DataAccessException {
        final List<Node> pricedCoverageList = PojoXmlUtil.getNodesByTagName(nodeObeject, "PricedCoverage");
        for (final Node pricedCoverage : pricedCoverageList) {
            final Node chargeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pricedCoverage, "Coverage"), "Charge");
            if (null != chargeNode.getAttributes().getNamedItem("IncludedInEstTotalInd") && "false".equalsIgnoreCase(chargeNode.getAttributes().getNamedItem("IncludedInEstTotalInd").getTextContent())
                    || null != chargeNode.getAttributes().getNamedItem("IncludedInRate") && "false".equalsIgnoreCase(chargeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent())) {
                final String amount = chargeNode.getAttributes().getNamedItem("Amount").getTextContent();
                final String currency = chargeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                final String coverageCode = PojoXmlUtil.getNodeByTagName(pricedCoverage, "Coverage").getAttributes().getNamedItem("Code").getTextContent();
                final String description = chargeNode.getAttributes().getNamedItem("Description").getTextContent();
                final String financeCategoryCode = "Fee";
                final long unitAcount = 1;
                String financeSubCategoryCode = null;
                //If this additional fee is CMA related, we get the Expedia OTA mapping for the given Carnect fee code form ExternalDomainData
                final List<ExternalSupplyServiceDomainValueMap> extMaps =  scsDataSource.getExternalSupplyServiceDomainValueMap("CarVehicleChargePurpose" ,coverageCode);
                if (CollectionUtils.isNotEmpty(extMaps))
                {
                    financeSubCategoryCode = extMaps.get(0).getDomainValue();
                }
                if (StringUtil.isNotBlank(amount) && StringUtil.isNotBlank(currency)) {
                    final CarCostType carCostType = VehAvailNodeHelper.createCarCostType(unitAcount, amount, currency, description, financeSubCategoryCode, financeCategoryCode);
                    carCostTypes.add(carCostType);
                }

            }

        }
    }

    /**
     * Read CarRateDetail/CarInsuranceIncludedInRate node
     *
     * @param car
     * @param pricedCoverages
     */
    public static void getCarInsuranceIncludedInRateFromPricedCoveragesList(CarProductType car, NodeList pricedCoverages)
    {
        Boolean carInsuranceIncludedInRate = false;
        for(int i=0; pricedCoverages.getLength() > i; i++)
        {
            final Node coverageNode = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverages.item(i).getChildNodes(), "Coverage");
            final Node chargeNode = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverages.item(i).getChildNodes(), "Charge");

            if(null != coverageNode && CDW.equals(coverageNode.getAttributes().getNamedItem("Code").getTextContent()))
            {
                if(null != chargeNode)
                {
                    carInsuranceIncludedInRate = Boolean.valueOf(chargeNode.getAttributes()
                            .getNamedItem("IncludedInRate").getTextContent());

                    break;

                }
            }
        }
        car.getCarRateDetail().setCarInsuranceIncludedInRate(carInsuranceIncludedInRate);
    }


    public static void setLocationCodes(CarLocationKeyType key, String locationNodeValue) {
        if (Pattern.matches("[a-zA-Z]{4}[0-9]{2,3}$", locationNodeValue))
        {
            key.setLocationCode(locationNodeValue.substring(0, 3));
            key.setCarLocationCategoryCode(locationNodeValue.substring(3, 4));
            key.setSupplierRawText(locationNodeValue.substring(4));
            if (key.getSupplierRawText().length() == 2){
                key.setSupplierRawText("0" + key.getSupplierRawText());
            }
        }
    }

    public static String getDistanceUnit(Node response) {

        final String distUnitName = PojoXmlUtil.getNodeByTagName(response, "RateDistance").
                getAttributes().getNamedItem("DistUnitName").getTextContent();
        if ("Km".equalsIgnoreCase(distUnitName)) {
            return "KM";
        } else if ("Mile".equalsIgnoreCase(distUnitName)) {
            return "MI";
        }
        return null;
    }



    public static  CarCostType createCarCostType(long unitAcount, String amount, String currency, String description, String financeSubCategoryCode, String financeCategoryCode) {
        final CarCostType carCostType = new CarCostType();
        carCostType.setRequiredCostBoolean(true);

        final MultiplierOrAmountType multiplierOrAmountType = new MultiplierOrAmountType();
        multiplierOrAmountType.setCurrencyAmount(getCurrencyAmountType(amount, currency));
        carCostType.setMultiplierOrAmount(multiplierOrAmountType);

        carCostType.setFinanceCategoryCode(financeCategoryCode);
        carCostType.setFinanceSubCategoryCode(financeSubCategoryCode);
        carCostType.setFinanceApplicationCode("Trip");
        carCostType.setFinanceApplicationUnitCount(unitAcount);
        carCostType.setDescriptionRawText(description);
        return carCostType;
    }

    public static CurrencyAmountType getCurrencyAmountType(String amount, String currencyCode) {
        final CurrencyAmountType currencyAmountType = new CurrencyAmountType();
        final AmountType amountType = new AmountType();
        final String tempAmount = getAmount(amount);
        amountType.setDecimal(Integer.parseInt(tempAmount.replace(".", "")));
        amountType.setDecimalPlaceCount(tempAmount.contains(".") ? tempAmount.split("\\.")[1].length() : 0);

        currencyAmountType.setAmount(amountType);
        currencyAmountType.setCurrencyCode(currencyCode);
        return currencyAmountType;
    }
    //eg 1.980000000000000000004 change to 1.9
    public static String getAmount(String amount)
    {
        try {
            Integer.parseInt(amount.replace(".", ""));
            return amount;
        }
        catch(Exception e)
        {
            return amount.substring(0, amount.indexOf('.')) + amount.substring(amount.indexOf('.'), amount.indexOf('.') + 2);
        }
    }

    public static Node getChargeNode( Node pricedCoverage) {
        Node chargeNode = null;
        final NodeList nodeList = pricedCoverage.getChildNodes();
        for(int i=0; nodeList.getLength()>i; i++)
        {
            if ("Charge".equalsIgnoreCase(nodeList.item(i).getNodeName()))
            {
                chargeNode = nodeList.item(i);
                break;
            }
        }
        return chargeNode;
    }

    public static String getFinanceSubCategoryCode(String coverageCode) {
        String financeSubCategoryCode = null;
        if ("CDW".equalsIgnoreCase(coverageCode)) {
            financeSubCategoryCode = "CollisionDamageWaiver";

        }
        if ("ExcessPrice".equalsIgnoreCase(coverageCode)) {
            financeSubCategoryCode = "InsuranceExcess";
        }
        return financeSubCategoryCode;
    }

    public static void setLocationCode(Node nodeObject, CarsSCSDataSource scsDataSource, CarCatalogKeyType carCatalogKeyType) throws Exception {
        final String pickupLocationCode = PojoXmlUtil.getNodeByTagName(nodeObject, "PickUpLocation").
                getAttributes().getNamedItem("LocationCode").getTextContent();

        //get dropoffLoctionCode
        final String dropoffLocationCode = PojoXmlUtil.getNodeByTagName(nodeObject, "ReturnLocation").
                getAttributes().getNamedItem("LocationCode").getTextContent();


       // final String vendorCode = PojoXmlUtil.getNodeByTagName(nodeObject, "Vendor").
      //          getAttributes().getNamedItem("Code").getTextContent();

        //setSupplierId
       // carCatalogKeyType.setVendorSupplierID(pickupExtendedVendorList.get(0).getSupplierID());

        final CarLocationKeyType carPickupLocationKey = new CarLocationKeyType();
        final CarLocationKeyType carDropoffLocationKey = new CarLocationKeyType();
        carCatalogKeyType.setCarDropOffLocationKey(carDropoffLocationKey);
        carCatalogKeyType.setCarPickupLocationKey(carPickupLocationKey);

        VehAvailNodeHelper.setLocationCodes(carPickupLocationKey, pickupLocationCode);
        VehAvailNodeHelper.setLocationCodes(carDropoffLocationKey, dropoffLocationCode);
    }


    public static void setCarInventoryKey(Node nodeObject, CarsSCSDataSource scsDataSource, CarInventoryKeyType carInventoryKey) throws Exception {
        final Node vehRentalCoreNode = PojoXmlUtil.getNodeByTagName(nodeObject, "VehRentalCore");
        readDate(carInventoryKey, vehRentalCoreNode);

        final CarCatalogKeyType carCatalogKeyType = new CarCatalogKeyType();
        carInventoryKey.setCarCatalogKey(carCatalogKeyType);

        //get PickupLocationCode
        VehAvailNodeHelper.setLocationCode(nodeObject, scsDataSource, carCatalogKeyType);

        //car vehicle
        final String carModel = PojoXmlUtil.getNodeByTagName(nodeObject, "VehMakeModel").
                getAttributes().getNamedItem("Code").getTextContent();
        final VehMakeModel vehMakeModel = new VehMakeModel(carModel.substring(0, 1), carModel.substring(1, 2), carModel.substring(2, 3), carModel.substring(3, 4), true);
        GDSMsgReadHelper.readVehMakeModel(carInventoryKey, scsDataSource, vehMakeModel);
    }

    public static void setCarRate(Node nodeObject, CarInventoryKeyType carInventoryKey) {
        final CarRateType carRateType = new CarRateType();
        carInventoryKey.setCarRate(carRateType);
        carRateType.setRatePeriodCode("Trip");
        final Node corpDiscountNmbr = PojoXmlUtil.getNodeByTagName(nodeObject, "RateQualifier").
                getAttributes().getNamedItem("CorpDiscountNmbr");
        if (null != corpDiscountNmbr) {
            carRateType.setCorporateDiscountCode(corpDiscountNmbr.getTextContent());
        }
        final Node promoDesc = PojoXmlUtil.getNodeByTagName(nodeObject, "RateQualifier").
                getAttributes().getNamedItem("PromoDesc");
        if (null != promoDesc) {
            carRateType.setPromoCode(promoDesc.getTextContent());
        }

        final Node rateCode = PojoXmlUtil.getNodeByTagName(nodeObject, "RateQualifier").
                getAttributes().getNamedItem("RateCategory");
        if (null != rateCode && StringUtil.isNotBlank(rateCode.getTextContent())) {
            carRateType.setRateCode(rateCode.getTextContent());
        }
    }
}
