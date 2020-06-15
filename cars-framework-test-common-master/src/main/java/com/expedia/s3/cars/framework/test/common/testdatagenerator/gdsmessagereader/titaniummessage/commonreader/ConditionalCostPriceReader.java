package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateDetailType;
import com.expedia.e3.data.financetypes.defn.v4.CostPriceListType;
import com.expedia.e3.data.financetypes.defn.v4.CostPriceType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CostListReader.buildCost;

/**
 * Created by v-mechen on 12/7/2016.
 */
public class ConditionalCostPriceReader {
    private ConditionalCostPriceReader(){

    }
    //TODO decription is wrong, need correct.
    public static void buildConditionalCostPriceList(CarProductType carproduct, NodeList pricedCoverages) {
        final List<CostPriceType> costPriceList = new ArrayList<>();
        CarRateDetailType carRateDetailType = carproduct.getCarRateDetail();
        if(CompareUtil.isObjEmpty(carRateDetailType)) {
            carRateDetailType = new CarRateDetailType();
        }
        final CostPriceListType costPriceListType = new CostPriceListType();
        carRateDetailType.setConditionalCostPriceList(costPriceListType);
        costPriceListType.setCostPrice(costPriceList);
        carproduct.setCarRateDetail(carRateDetailType);
        for (int i = 0; pricedCoverages.getLength() > i; i++) {
            final NodeList nodeList = pricedCoverages.item(i).getChildNodes();
            String currencyCode = "";
            String amount = "";
            int unitC = 1;
            //final StringBuilder description = new StringBuilder();

            final String description = getConditionalCostPriceDescription(pricedCoverages.item(i));
            for (int j = 0; nodeList.getLength() > j; j++) {
                if ("charge".compareToIgnoreCase(nodeList.item(j).getNodeName()) == 0) {
                    currencyCode = nodeList.item(j).getAttributes().getNamedItem("CurrencyCode").getTextContent();

                    amount = nodeList.item(j).getAttributes().getNamedItem("Amount").getTextContent();

                    if (Boolean.TRUE.toString().compareToIgnoreCase(nodeList.item(j).getAttributes().getNamedItem("IncludedInRate").getTextContent()) == 0) {
                        unitC = 0;
                    }


                }

            }
            final String financeCategoryCode = "Optional";
            //CASSS-5547 on UI(expweb/luna), we are only respecting "Extra" and "Optional" categoryCode in conditionalCostList
            //, and we log a warning on other unknown categoryCode. The other categoryCodes are usually "Coverage, Taxes",
            //which has additional fee like driver surcharges. These kind of fees are already included in additionalFeeList and CoverageCostList.
            if (Boolean.TRUE.toString().compareToIgnoreCase(pricedCoverages.item(i).getAttributes().getNamedItem("Required").getTextContent()) == 0) {
                continue;//financeCategoryCode = "Conditional";
            }

            final CostType covCost = buildCost(amount, currencyCode, financeCategoryCode, "Trip", unitC, 8, 1, 21, description, true);
            covCost.setFinanceApplicationCode("Trip");
            final CostPriceType costPrice = new CostPriceType();
            costPrice.setCost(covCost);
            costPriceList.add(costPrice);
        }
    }

    public  static String getConditionalCostPriceDescription(Node pricedCoverage)
    {
        final StringBuilder descriptionStr = new StringBuilder().append("CoverageType: ");

        switch (PojoXmlUtil.getNodeByTagName(pricedCoverage, "Coverage").getAttributes().getNamedItem("CoverageType").getTextContent())
        {
            case "7":
                descriptionStr.append("CollisionDamageWaiver");
                break;

            case "21":
                descriptionStr.append("Insurance");
                break;

            case "33":
                descriptionStr.append("PersonalAccidentCoverage");
                break;

            case "40":
                descriptionStr.append("SuperCollisionDamageWaiver");
                break;

            case "48":
                descriptionStr.append("TheftProtection");
                break;
            default:
                break;
        }

        if (!PojoXmlUtil.getNodesByTagName(pricedCoverage, "Deductible").isEmpty())
        {
            descriptionStr.append("; ExcessAmount: " + PojoXmlUtil.getNodeByTagName(pricedCoverage, "Deductible").getAttributes().getNamedItem("ExcessAmount").getTextContent() +
                    "; DeductibleCurrency: " + PojoXmlUtil.getNodeByTagName(pricedCoverage, "Deductible").getAttributes().getNamedItem("CurrencyCode").getTextContent());
        }

        final String chargeDescription = PojoXmlUtil.getNodeByTagName(pricedCoverage, "Charge").getAttributes().getNamedItem("Description").getTextContent();
        descriptionStr.append("; Description: ").append(chargeDescription);

        return descriptionStr.toString();

    }

    //CASSS-5547 on UI(expweb/luna), we are only respecting "Extra" and "Optional" categoryCode in conditionalCostList
    //, and we log a warning on other unknown categoryCode. The other categoryCodes are usually "Coverage, Taxes",
    //which has additional fee like driver surcharges. These kind of fees are already included in additionalFeeList and CoverageCostList.
    public static void buildConditionalCostPriceListFromFee(CarProductType carproduct, List<Node> fees)
    {
            /*<Fee Amount="24.00" CurrencyCode="EUR" Description="One-Way Fee"                          GuaranteedInd="false" IncludedInRate="false" Purpose="2" RequiredInd="true"></Fee>
					<Fee Amount="25.00" CurrencyCode="EUR" Description="AIRPORT FEE 25.00/EURO/RNTL"    GuaranteedInd="true" IncludedInRate="true" Purpose="5" RequiredInd="true"></Fee>
					<Fee Amount="15.00" CurrencyCode="EUR" Description="LOCAl FEE 15.00/EURO/RNTL"      GuaranteedInd="true" IncludedInRate="false" Purpose="5" RequiredInd="true"></Fee>
					<Fee Amount="82.52" CurrencyCode="EUR" Description="VAT TAX"                        GuaranteedInd="true" IncludedInRate="true" Purpose="7" RequiredInd="true"></Fee>
					<Fee Amount="22.52" CurrencyCode="EUR" Description="VAT TAX"                        GuaranteedInd="false" IncludedInRate="false" Purpose="7" RequiredInd="true"></Fee>
             */
        if(CompareUtil.isObjEmpty(carproduct.getCarRateDetail())) {
            carproduct.setCarRateDetail(new CarRateDetailType());
        }
        if(CompareUtil.isObjEmpty(carproduct.getCarRateDetail().getConditionalCostPriceList())) {
            carproduct.getCarRateDetail().setConditionalCostPriceList(new CostPriceListType());
        }
        if (carproduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice() == null)
        {
            final List<CostPriceType> costPriceList = new ArrayList<CostPriceType>();
            carproduct.getCarRateDetail().getConditionalCostPriceList().setCostPrice(costPriceList);
        }

        for (final Node fee : fees)
        {
            if (feeIsConditional(fee)) {
                final String currencyCode = fee.getAttributes().getNamedItem("CurrencyCode").getTextContent();
                final String amount = fee.getAttributes().getNamedItem("Amount").getTextContent();

                final CostType covCost = CostListReader.buildCost(amount, currencyCode,
                        "Extra", getApplicationCode(fee.getAttributes().getNamedItem("Purpose").getTextContent()),
                        1, 8, 1, getLegacyMonetaryCalculationID(fee.getAttributes().getNamedItem("Purpose").getTextContent()), "", true);


                //ChargePurpose: Tax; Description: VAT TAX
                final String description = "ChargePurpose: " + getChargePurpose(fee.getAttributes().getNamedItem("Purpose").getTextContent())
                        + "; Description: " + fee.getAttributes().getNamedItem("Description").getTextContent();

                covCost.setDescriptionRawText(description);
                covCost.setFinanceApplicationCode(getApplicationCode(fee.getAttributes().getNamedItem("Purpose").getTextContent()));

                final CostPriceType costPrice = new CostPriceType();
                costPrice.setCost(covCost);
                carproduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice().add(costPrice);
            }
        }
    }

    private static boolean feeIsConditional(Node fee)
    {
        boolean isConditional = false;
        if (fee.getAttributes().getNamedItem("Purpose").getTextContent().equals("8")  ||
                fee.getAttributes().getNamedItem("Purpose").getTextContent().equals("9") ||
                fee.getAttributes().getNamedItem("Purpose").getTextContent().equals("10") ||
                fee.getAttributes().getNamedItem("Purpose").getTextContent().equals("11") ||
                fee.getAttributes().getNamedItem("Purpose").getTextContent().equals("12")) {
            return isConditional = true;
        }
        return isConditional;

    }

    private static String getChargePurpose(String purpose)
    {
        if ("8".equals(purpose))
        {
            return "AdditionalDistance";
        }
        if ("9".equals(purpose))
        {
            return "AdditionalWeek";
        }
        if ("10".equals(purpose))
        {
            return "AdditionalDay";
        }
        if ("11".equals(purpose))
        {
            return "AdditionalHour";
        }
        if ("12".equals(purpose))
        {
            return "AdditionalDrive";
        }
        return null;

    }
    private static String getApplicationCode(String purpose)
    {

        if ("9".equals(purpose))
        {
            return "ExtraWeekly";
        }
        if ("10".equals(purpose))
        {
            return "ExtraDaily";
        }
        if ("11".equals(purpose))
        {
            return "ExtraHourly";
        }
        else
        {
            return "Trip";
        }

    }
    private static int getLegacyMonetaryCalculationID(String purpose)
    {

        if ("9".equals(purpose))
        {
            return 8;
        }
        if ("10".equals(purpose))
        {
            return 7;
        }

        else
        {
            return 25;
        }

    }


}
