package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.SimpleCurrencyAmountType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CostListReader.buildCost;


/**
 * Created by v-mechen on 12/7/2016.
 */
public class SpecialEquipReader {
    private SpecialEquipReader(){

    }
    public static void readCarVehicleOptionListFromPricedEquips(CarProductType car, CarsSCSDataSource scsDataSource, List<Node> pricedEquips, boolean isCarBS) throws DataAccessException {
        final CarVehicleOptionListType carOptionls = new CarVehicleOptionListType();
        final List<CarVehicleOptionType> carVehicleOptionTypes = new ArrayList<>();
        carOptionls.setCarVehicleOption(carVehicleOptionTypes);
        for (int i = 0; pricedEquips.size() > i; i++) {
            final CarVehicleOptionType vehOpt = new CarVehicleOptionType();
            carVehicleOptionTypes.add(readSpecialEquipmentInfo(vehOpt, pricedEquips.get(i), scsDataSource, isCarBS));
        }
        car.setCarVehicleOptionList(carOptionls);
    }

    public static CarVehicleOptionType readSpecialEquipmentInfo(CarVehicleOptionType vehOpt, Node pricedEq, CarsSCSDataSource scsDataSource,
                                                                boolean isCarBS) throws DataAccessException {
        final Node equip = PojoXmlUtil.getSpecifiedXMLNode(pricedEq.getChildNodes(), "Equipment");
        final String equipType = equip.getAttributes().getNamedItem("EquipType").getTextContent();

        if (!StringUtils.isEmpty(equipType)) {
            vehOpt.setCarSpecialEquipmentCode(scsDataSource.getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.CAR_SPECIAL_EQUIPMENT, null, equipType).get(0).getDomainValue());
        }

        vehOpt.setCarVehicleOptionCategoryCode("special equipment");

        final Node currencyCodeNode = PojoXmlUtil.getSpecifiedXMLNode(pricedEq.getChildNodes(), "Charge").getAttributes().getNamedItem("CurrencyCode");
        String currencyCode = "";
        if (null != currencyCodeNode) {
            currencyCode = currencyCodeNode.getTextContent();
        }

        final StringBuilder maxChargeValue = new StringBuilder();
        if (PojoXmlUtil.getSpecifiedXMLNode(PojoXmlUtil.getSpecifiedXMLNode(pricedEq.getChildNodes(), "Charge").getChildNodes(), "MinMax") != null) {
            maxChargeValue.append(PojoXmlUtil.getSpecifiedXMLNode(PojoXmlUtil.getSpecifiedXMLNode(pricedEq.getChildNodes(), "Charge").getChildNodes(), "MinMax").getAttributes().getNamedItem("MaxCharge").getTextContent());

            final SimpleCurrencyAmountType carVehicleOptionMaxCharge = new SimpleCurrencyAmountType();
            carVehicleOptionMaxCharge.setCurrencyCode(currencyCode);
            carVehicleOptionMaxCharge.setSimpleAmount(maxChargeValue.toString().trim());
            vehOpt.setCarVehicleOptionMaxCharge(carVehicleOptionMaxCharge);

        }
        //<Calculation MaxQuantity="1"
        final Node calculationNode = PojoXmlUtil.getSpecifiedXMLNode(PojoXmlUtil.getSpecifiedXMLNode(pricedEq.getChildNodes(), "Charge").getChildNodes(), "Calculation");
        String maxQuantityValue = "";
        if (null != calculationNode) {
            //<MinMax MaxCharge="52.89"></MinMax>
            maxQuantityValue = calculationNode.getAttributes().getNamedItem("MaxQuantity").getTextContent();
            vehOpt.setCarVehicleOptionMaxCount(Long.parseLong(maxQuantityValue));
        }

        final Node descriptionNode = PojoXmlUtil.getNodeByTagName(pricedEq, "Description");
        final StringBuilder desRawTxt = new StringBuilder();
        if (null != descriptionNode) {
            desRawTxt.append(descriptionNode.getTextContent());
        }

       // CASSS-1551 Any charges at rental desk should be displayed in POSu currency
       /* if(isCarBS) {
            //EquipmentType: ToddlerChildSeat; MaxPrice: 52.89; MaxQuantity: 3; Description: Toddler Seat: For children approx 9 -18 kg/20-40 lbs (approx 1-3 years)
            desRawTxt.append("EquipmentType: ").append(vehOpt.getCarSpecialEquipmentCode());
            if (maxChargeValue.length() > 0) {
                maxChargeValue.append("; MaxPrice: ").append(maxChargeValue);
            }
            desRawTxt.append(maxChargeValue).append(
                    "; MaxQuantity: ").append(maxQuantityValue).append(
                    "; Description: ").append(descriptionNode.getTextContent());
        }*/

        vehOpt.setDescriptionRawText(desRawTxt.toString());

        vehOpt.setCost(readSpecialEquipCostFromPricedEquipment(pricedEq));
        vehOpt.getCost().setDescriptionRawText(vehOpt.getDescriptionRawText());

        return vehOpt;
    }

    public static CostType readSpecialEquipCostFromPricedEquipment(Node pricedEq) {
        final Node charge = PojoXmlUtil.getSpecifiedXMLNode(pricedEq.getChildNodes(), "Charge");
        final Node calculationNode = PojoXmlUtil.getSpecifiedXMLNode(charge.getChildNodes(), "Calculation");
        String chargetype = "";
        String amount = "";
        if (null != calculationNode) {
            chargetype = calculationNode.getAttributes().getNamedItem("UnitName").getTextContent();
            amount = calculationNode.getAttributes().getNamedItem("UnitCharge").getTextContent();
        }

        final Node currencyCodeNode = charge.getAttributes().getNamedItem("CurrencyCode");
        String currencyCode = "";
        if (null != currencyCodeNode) {
            currencyCode = currencyCodeNode.getTextContent();
        }

        final String applicationCode = getSpeEquipAppliationCode(chargetype);

        return buildCost(amount, currencyCode, "SpecialEquipment", applicationCode, 1L, 8, 1, 21, null, true);
    }

    public static String getSpeEquipAppliationCode(String chargetype)
    {
        String applicationCode = null;
        switch (chargetype) {
            case "PerHour":
                applicationCode = "Hourly";
                break;
            case "Day":
                applicationCode = "Daily";
                break;
            case "PerWeek":
                applicationCode = "Weekly";
                break;
            case "PerMonth":
                applicationCode = "Monthly";
                break;
            case "Percent":
                applicationCode = "Percentage";
                break;
            case "NoCharge":
                applicationCode = "Included";
                break;
            default:
                applicationCode = "Trip";
                break;
        }
        return applicationCode;
    }







}
