package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.SimpleCurrencyAmountType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CostListReader.buildMultiplierOrAmount;

/**
 * Created by v-mechen on 12/7/2016.
 */
public class CarCoveragesCostListReader {
    private CarCoveragesCostListReader(){

    }
    /**
     * Read CarRateDetail/CarCoveragesCostList
     *
     * @param car
     * @param pricedCoverages
     */
    public static void readCarCoveragesCostListFromPricedCoverages(CarProductType car, NodeList pricedCoverages) {
        final CarCoveragesCostListType carCoverCostls = new CarCoveragesCostListType();
        final List<CarCoveragesCostType> carCoveragesCostTypes = new ArrayList<>();
        carCoverCostls.setCarCoveragesCost(carCoveragesCostTypes);
        for (int i = 0; pricedCoverages.getLength() > i; i++) {
            carCoveragesCostTypes.add(readPricedCoveragesInfo(pricedCoverages.item(i)));
        }
        if (car.getCarRateDetail() == null) {
            car.setCarRateDetail(new CarRateDetailType());
        }
        car.getCarRateDetail().setCarCoveragesCostList(carCoverCostls);
    }

    /**
     * @param pricedCoverage
     * @return
     */
    public static CarCoveragesCostType readPricedCoveragesInfo(Node pricedCoverage) {
        final CarCoveragesCostType vehCoverCost = new CarCoveragesCostType();
        final CarCostType coverageCost = new CarCostType();
        coverageCost.setFinanceCategoryCode("Coverages");
        coverageCost.setFinanceApplicationCode("Trip");

        // <ns9:FinanceSubCategoryCode>Insurance</ns9:FinanceSubCategoryCode> get from type maybe
        //<Coverage CoverageType = "21"/>
        //TODO mia may in table or some where
        final Node calculationNode = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverage.getChildNodes(), "Coverage");
        String coverageType = "";
        if (null != calculationNode) {
            coverageType = calculationNode.getAttributes().getNamedItem("CoverageType").getTextContent();
        }
        String financeSubCategoryCode = null;

        switch (coverageType) {
            case "7":
                financeSubCategoryCode = "CollisionDamageWaiver";
                break;
            case "21":
                financeSubCategoryCode = "Insurance";
                break;
            case "33":
                financeSubCategoryCode = "PersonalAccidentCoverage";
                break;
            case "40":
                financeSubCategoryCode = "SuperCollisionDamageWaiver";
                break;
            case "48":
                financeSubCategoryCode = "TheftProtection";
                break;
            default:
                financeSubCategoryCode = "";
                break;
        }
        coverageCost.setFinanceSubCategoryCode(financeSubCategoryCode);

        final boolean requiredBoolean = Boolean.parseBoolean(pricedCoverage.getAttributes().getNamedItem("Required").getTextContent());
        coverageCost.setRequiredCostBoolean(requiredBoolean);

        vehCoverCost.setCarCost(readCoveragesCostFromPricedCoverageCharge(coverageCost, PojoXmlUtil.getSpecifiedXMLNode(pricedCoverage.getChildNodes(), "Charge")));
        vehCoverCost.setCarDeductible(readCarDeductibleFromDeductible(PojoXmlUtil.getSpecifiedXMLNode(pricedCoverage.getChildNodes(), "Deductible")));
        return vehCoverCost;
    }

    /**
     * Read CarRateDetail/CarInsuranceIncluded node
     *
     * @param car
     * @param pricedCoverages
     */
    public static void readCarInsuranceIncludedFromPricedCoverages(CarProductType car, NodeList pricedCoverages) {
        Boolean carInsuranceIncluded = false;
        for (int i = 0; pricedCoverages.getLength() > i; i++) {
            final Node calculationNode = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverages.item(i).getChildNodes(), "Coverage");
            final Node chargeNode = PojoXmlUtil.getSpecifiedXMLNode(pricedCoverages.item(i).getChildNodes(), "Charge");
            String coverageType = "";
            String includedInRate = "";
            if (null != calculationNode) {
                coverageType = calculationNode.getAttributes().getNamedItem("CoverageType").getTextContent();
            }

            if (null != chargeNode) {
                includedInRate = chargeNode.getAttributes().getNamedItem("IncludedInRate").getTextContent();
            }
            if ("7".equals(coverageType)&& "true".equalsIgnoreCase(includedInRate))
            {
                carInsuranceIncluded = true;
            }
        }
        car.getCarRateDetail().setCarInsuranceIncludedInRate(carInsuranceIncluded);

    }

    /**
     * @param coverageCost
     * @param charge
     * @return
     */
    private static CarCostType readCoveragesCostFromPricedCoverageCharge(CarCostType coverageCost, Node charge) {
        final String amount = charge.getAttributes().getNamedItem("Amount").getTextContent().trim();
        coverageCost.setMultiplierOrAmount(buildMultiplierOrAmount(
                charge.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim(), amount));

        if (Boolean.parseBoolean(charge.getAttributes().getNamedItem("GuaranteedInd").getTextContent().trim())
                && Boolean.parseBoolean(charge.getAttributes().getNamedItem("IncludedInRate").getTextContent().trim())) {
            coverageCost.setFinanceApplicationUnitCount(0L);
        } else {
            coverageCost.setFinanceApplicationUnitCount(1L);
        }

        coverageCost.setDescriptionRawText(charge.getAttributes().getNamedItem("Description").getTextContent().trim());

        return coverageCost;
    }


    /**
     * @param deductible
     * @return
     */
    private static CarDeductibleType readCarDeductibleFromDeductible(Node deductible) {
        if (null != deductible) {
            final CarDeductibleType carDeductibleType = new CarDeductibleType();
            final SimpleCurrencyAmountType excessAmout = new SimpleCurrencyAmountType();
            excessAmout.setCurrencyCode(deductible.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim());
            excessAmout.setSimpleAmount(deductible.getAttributes().getNamedItem("ExcessAmount").getTextContent().trim());
            carDeductibleType.setExcessAmount(excessAmout);
            return carDeductibleType;
        }

        return null;
    }

}
