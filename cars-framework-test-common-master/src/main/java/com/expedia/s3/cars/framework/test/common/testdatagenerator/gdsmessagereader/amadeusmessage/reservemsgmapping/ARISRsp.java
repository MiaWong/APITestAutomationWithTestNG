package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSReaderUtil;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miawang on 1/23/2017.
 */
@SuppressWarnings("PMD")
public class ARISRsp
{
    /**
     * If you get 'NN'/SS in sell response, this is normal not to have the RateInformationFromSegment available
     *
     * @param carproduct
     * @param nodeArisRifcsRsp
     * @param statusCode
     */
    public void buildPickupAndDropoffLocation(CarProductType carproduct, Node nodeArisRifcsRsp, String statusCode)
    {
        final List<Node> pickupDropoffLocationsNodeList = PojoXmlUtil.getNodesByTagName(nodeArisRifcsRsp, "pickupDropoffLocation");

       for(Node locationNode: pickupDropoffLocationsNodeList)
       {
           if (null != PojoXmlUtil.getNodeByTagName(locationNode, "locationType") && "176".equals(PojoXmlUtil.getNodeByTagName(locationNode, "locationType").getTextContent()))
           {
               buildPickupOrDropoffLocation(carproduct, locationNode, statusCode, false);
           }

           if (null != PojoXmlUtil.getNodeByTagName(locationNode, "locationType") && "DOL".equals(PojoXmlUtil.getNodeByTagName(locationNode, "locationType").getTextContent()))
           {
               buildPickupOrDropoffLocation(carproduct, locationNode, statusCode, true);
           }
       }
       if(null != carproduct.getCarDropOffLocation() && null == carproduct.getCarDropOffLocation().getAddress()
               && CarNodeComparator.isCarLocationKeyEqual(carproduct.getCarPickupLocation().getCarLocationKey(), carproduct.getCarDropOffLocation().getCarLocationKey(),
               new StringBuilder(), Arrays.asList(CarTags.CAR_VENDOR_LOCATION_ID)))
       {
           carproduct.getCarDropOffLocation().setAddress(carproduct.getCarPickupLocation().getAddress());
       }


           final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.buildPickupAndDropOffLocationPhoneList(carproduct, nodeArisRifcsRsp);
    }

    private void buildPickupOrDropoffLocation(CarProductType carproduct, Node locationNode, String statusCode, boolean isDropOffLocation)
    {
        // Booking status
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        if (null != statusCode && !statusCode.equals("HK"))
        {
            // same as the carinventoryKey/../carPickuplocationkey
            // same as the carinventoryKey/../carDropOffLocationkey
            commonNodeReader.buildCarPickupAndDropOffLocationKey(carproduct, isDropOffLocation);
            return;
        }

        commonNodeReader.buildCarPickupAndDropOffLocationKey(carproduct, isDropOffLocation);

        CarLocationType carLocation = carproduct.getCarPickupLocation();
        if (isDropOffLocation)
        {
            carLocation = carproduct.getCarDropOffLocation();
        }

        /// address
        commonNodeReader.buildPickUpDropOffAddress(carLocation, locationNode);


        ///build Open Time schedules
        commonNodeReader.buildPickUpDropOffRecurringPeriod(carLocation, carproduct.getCarInventoryKey(), locationNode, isDropOffLocation);
    }

    // CarPolicyList
    public void buildCarPolicyList(CarProductType carproduct, Node nodeArisRifcsRsp)
    {
        if (null == carproduct.getCarPolicyList())
        {
            carproduct.setCarPolicyList(new CarPolicyListType());
        }
        if (null == carproduct.getCarPolicyList().getCarPolicy())
        {
            carproduct.getCarPolicyList().setCarPolicy(new ArrayList<>());
        }

        //CarPolicy carPolicyOtherRule = new CarPolicy();
        //Node freeTextOtherRule = arisResponse.SelectSingleNode("//n2:otherRulesGroup"),"otherRules"),"ruleText"),"freeText", xnmPNR);
        //Node typeOtherRule = arisResponse.SelectSingleNode("//n2:otherRulesGroup"),"otherRules"),"ruleDetails"),"type", xnmPNR);
        //if(freeTextOtherRule != null)   carPolicyOtherRule.CarPolicyRawText = freeTextOtherRule.getTextContent();
        //if (typeOtherRule != null)
        //{
        //    carPolicyOtherRule.CarPolicyCategoryCode = typeOtherRule.getTextContent();
        //    carPolicyList.Add(carPolicyOtherRule);
        //}
        ///AmadeusSessionManagerResponse/RawAmadeusXml/Car_RateInformationFromCarSegmentReply/rateDetails/remarks/freeText[2]
        final List<Node> freeTextRemarks = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(nodeArisRifcsRsp, "rateDetails"), "remarks"), "freeText");
        if (freeTextRemarks != null)
        {
            for (final Node freeTextRemark : freeTextRemarks)
            {
                final CarPolicyType remark = new CarPolicyType();
                carproduct.getCarPolicyList().getCarPolicy().add(remark);

                remark.setCarPolicyCategoryCode("Miscellaneous");
                remark.setCarPolicyRawText(freeTextRemark.getTextContent());
            }
        }

        final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(nodeArisRifcsRsp, "rateDetails"), "taxCovSurchargeGroup");
        if (chargeDetails != null)
        {
            for (final Node taxCovSurchargeGroup : chargeDetails)
            {
                final Node chargeDetail = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails").get(0), "type");

                if (null != chargeDetail && chargeDetail.getTextContent().equals("COV"))
                {
                    Node taxSurchargeCoverageInfoNode = PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo");
                    if(null != taxSurchargeCoverageInfoNode)
                    {
                        Node chargeDetailNode = PojoXmlUtil.getNodeByTagName(taxSurchargeCoverageInfoNode, "chargeDetails");
                        if(null != chargeDetailNode)
                        {
                            Node commentNode = PojoXmlUtil.getNodeByTagName( chargeDetailNode, "comment");
                            if(null != commentNode)
                            {
                                final CarPolicyType chargeDetailPolicy = new CarPolicyType();
                                chargeDetailPolicy.setCarPolicyCategoryCode(commentNode.getTextContent());


                                Node additionalInfoNode = PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "additionalInfo");
                                if (null != additionalInfoNode)
                                {
                                    final List<Node> rawTextList = PojoXmlUtil.getNodesByTagName(additionalInfoNode, "freeText");

                                    if(null != rawTextList)
                                    {
                                        StringBuffer rawText = new StringBuffer();
                                        for (final Node freeText : rawTextList)
                                        {
                                            rawText = rawText.append(freeText.getTextContent());
                                        }
                                        chargeDetailPolicy.setCarPolicyRawText(rawText.toString());
                                    }
                                }
                                carproduct.getCarPolicyList().getCarPolicy().add(chargeDetailPolicy);

                            }
                        }
                    }
                }
            }
        }
    }

    public CarCatalogMakeModelType buildCarCatalogMakeModel(Node nodeArisRifcsRsp)
    {
        final CarCatalogMakeModelType carMakeModel = new CarCatalogMakeModelType();
        carMakeModel.setCarMinDoorCount(0);
        carMakeModel.setCarMaxDoorCount(0);
        carMakeModel.setCarCapacityAdultCount(0);
        carMakeModel.setCarCapacityChildCount(0);
        carMakeModel.setCarCapacityLargeLuggageCount(0);
        carMakeModel.setCarCapacitySmallLuggageCount(0);
        carMakeModel.setCarFeatureString("");
        carMakeModel.setMediaID(0);
        carMakeModel.setImageFilenameString("");
        carMakeModel.setImageThumbnailFilenameString("");


        final Node vehicleInformationNode = PojoXmlUtil.getNodeByTagName(nodeArisRifcsRsp, "vehicleInfoGroup");
        // 1.carDoorCount and carCapacityAdultCount
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readCarDoorCount(carMakeModel, vehicleInformationNode);

        // 2.carCapacityLargeLuggageCount and carFeatureString
//            readCarCapacityLargeLuggageCountAndCarFeatureString(carMakeModel, vehicleInformationNode);

        //3. carMakeString
        final List<Node> carMakeStringNodeList = PojoXmlUtil.getNodesByTagName(vehicleInformationNode, "carModel");
        if (!carMakeStringNodeList.isEmpty())
        {
            final String carMakeString = carMakeStringNodeList.get(0).getTextContent();
            carMakeModel.setCarMakeString(carMakeString);
        }
        //Get ImageFilenameString and ImageThumbnailFilenameString according to picture size - 7 is ImageFilenameString, 4 is ImageThumbnailFilenameString
//            readImageFilenameString(carMakeModel, sizedPicturesNodeList);

        return carMakeModel;
    }

    // CarRateDetail
    public void buildCarRateDetail(CarProductType carProduct, Node arisResponse)
    {
        if (null == carProduct.getCarRateDetail())
        {
            carProduct.setCarRateDetail(new CarRateDetailType());
        }

        buildConditionalCostPriceList(carProduct, arisResponse);
    }

    private void buildConditionalCostPriceList(CarProductType carProduct, Node arisResponse)
    {
        if (null == carProduct.getCarRateDetail().getConditionalCostPriceList())
        {
            carProduct.getCarRateDetail().setConditionalCostPriceList(new CostPriceListType());
        }
        if (null == carProduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice())
        {
            carProduct.getCarRateDetail().getConditionalCostPriceList().setCostPrice(new ArrayList<>());
        }
        //(description='OPT') or not included in base rate ('NBR').
        final List<Node> taxCovSurchargeGroupList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(arisResponse, "rateDetails"), "taxCovSurchargeGroup");
        //bool isExits = false;
        if (taxCovSurchargeGroupList != null && !taxCovSurchargeGroupList.isEmpty())
        {
            for (final Node taxCovSurchargeGroup : taxCovSurchargeGroupList)
            {
                String comment = "";
                String currency = "";
                String type = "";
                String description = "";
                String periodType = "";

                ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:taxCovSurchargeGroup[2]/:taxSurchargeCoverageInfo/:tariffInfo/:currency
                ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:taxCovSurchargeGroup[2]/:taxSurchargeCoverageInfo/:chargeDetails
                final List<Node> chargeDetailsList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");

                if (chargeDetailsList != null)
                {
                    for (final Node chargeDetails : chargeDetailsList)
                    {
                        // type = PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"),"chargeDetails"),"type").getTextContent();
                        final Node typeNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "type");
                        if (typeNode != null)
                        {
                            type = typeNode.getTextContent();
                        }

                        final Node descriptionNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "description");
                        final Node periodTypeNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "periodType");
                        final Node commentNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "comment");
                        Node currencyNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "currency");
                        final Node amountNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "amount");

                        if (currencyNode == null)
                        {
                            currencyNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "tariffInfo"), "currency");
                        }

                        if (periodTypeNode != null)
                        {
                            periodType = periodTypeNode.getTextContent();
                        }
                        if (commentNode != null)
                        {
                            comment = commentNode.getTextContent();
                        }
                        if (descriptionNode != null)
                        {
                            final CostPriceType costPrice = new CostPriceType();
                            if (null == costPrice.getCost())
                            {
                                costPrice.setCost(new CostType());
                            }

                            if(type.equals("COV"))
                            {
                                costPrice.getCost().setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Coverage.getFinanceCategoryCode());
                            }
                            else
                            {
                                costPrice.getCost().setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(type));
                            }
                            if (!StringUtils.isEmpty(periodType))
                            {
                                costPrice.getCost().setFinanceApplicationCode(ASCSGDSReaderUtil.getChargeDetailsPeriodType(Integer.parseInt(periodType)));
                            }
                            costPrice.getCost().setFinanceApplicationUnitCount(1L);
                            costPrice.getCost().setDescriptionRawText(comment);
                            description = descriptionNode.getTextContent();
                            currency = currencyNode.getTextContent();
                            if (null == costPrice.getCost().getMultiplierOrAmount())
                            {
                                costPrice.getCost().setMultiplierOrAmount(new MultiplierOrAmountType());
                            }
                            if (null == costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount())
                            {
                                costPrice.getCost().getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
                            }
                            if (currency != null)
                            {
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currency);
                            }

                            if (null == costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount())
                            {
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
                            }
                            if (null != description && ((null != type && description.equals("OPT") && !type.equals("013")) || description.equals("NBR")))
                            {
                                if (null != amountNode)
                                {
                                    //setCurrencyAmount(costPrice.getCost().MultiplierOrAmount.CurrencyAmount, amount, currency);
                                    setAmount(costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount(), amountNode.getTextContent());
                                }

                                //setLegacyFinanceKey(costPrice.Cost);
                                carProduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice().add(costPrice);
                            } else if (type != null && type.equals("COV") && (description.equals("E") || description.equals("LIA")))
                            {
                                // costPrice.getCost().setFinanceCategoryCode( "Coverage";
                                if (comment != null)
                                {
                                    if (null != description && description.equals("E"))
                                    {
                                        costPrice.getCost().setDescriptionRawText(comment + " - Excess");
                                    }
                                    else if (null != description && description.equals("LIA"))
                                    {
                                        costPrice.getCost().setDescriptionRawText(comment + " - Liability");
                                    }
                                }

                                final Node tariffInfo = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "tariffInfo");
                                if (tariffInfo == null)
                                {
                                    costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(0);
                                    costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(0);
                                } else if (amountNode != null && tariffInfo != null)
                                {
                                    setAmount(costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount(), amountNode.getTextContent());
                                }
                                costPrice.getCost().setFinanceApplicationCode(ASCSGDSReaderUtil.getChargeDetailsPeriodType(4));
                                carProduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice().add(costPrice);
                            }
                        }
                    }
                }
            }
        }

        // ChargeDetails = 008 or 009, Extral hour or extral day.
        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:chargeDetails[4]/:type
        final List<Node> rateDetails = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(arisResponse, "rateDetails"), "rateDetail");
        if (rateDetails != null)
        {
            for (final Node rateDetail : rateDetails)
            {
                final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(rateDetail, "chargeDetails");

                if (chargeDetails != null)
                {
                    for (final Node chargeDetail : chargeDetails)
                    {
                        final String type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();

                        if (null != type && (type.equals("008") || type.equals("009")))
                        {
                            final String amount = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount").getTextContent();

                            Node currencyCodeNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "currency");

                            ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:tariffInfo/:currency
                            ////:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:tariffInfo/:currency
                            if (currencyCodeNode == null)
                            {
                                currencyCodeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "currency");
                            }

                            final CostPriceType costPrice = new CostPriceType();
                            costPrice.setCost(new CostType());
                            costPrice.getCost().setMultiplierOrAmount(new MultiplierOrAmountType());
                            costPrice.getCost().getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
                            costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());

                            costPrice.getCost().setFinanceApplicationUnitCount(1L);
                            setCostForChargeDetail(costPrice.getCost(), type);
                            //setCurrencyAmount(costPrice.getCost().MultiplierOrAmount.CurrencyAmount, amount, currencyCodeNode.InnerText.Trim());
                            setAmount(costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount(), amount);
                            costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currencyCodeNode.getTextContent());
                            //setLegacyFinanceKey(costPrice.Cost);
                            carProduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice().add(costPrice);
                        }
                    }
                }

            }
        }
        setLegacyFinanceKey(carProduct.getCarRateDetail().getConditionalCostPriceList());
        /*
        Console.WriteLine("The expected CarRateDetail..............:");
        foreach (CostPrice costPrice in carRateDetail.ConditionalCostPriceList.CostPrice)
        {
            Console.WriteLine("\n\nDescriptionRawText:" + costPrice.getCost().DescriptionRawText);
            Console.WriteLine("MultiplierOrAmount.CurrencyAmount.Amount.Decimal :" + costPrice.getCost().MultiplierOrAmount.CurrencyAmount.Amount.Decimal);
            Console.WriteLine("MultiplierOrAmount.CurrencyAmount.Amount.DecimalPlaceCount :" + costPrice.getCost().MultiplierOrAmount.CurrencyAmount.Amount.DecimalPlaceCount);
            Console.WriteLine("MultiplierOrAmount.CurrencyAmount.CurrencyCode:" + costPrice.getCost().MultiplierOrAmount.CurrencyAmount.CurrencyCode);
            Console.WriteLine("FinanceApplicationCode :" + costPrice.getCost().FinanceApplicationCode);
            Console.WriteLine("FinanceApplicationUnitCount :" + costPrice.getCost().FinanceApplicationUnitCount);
            Console.WriteLine("FinanceCategoryCode:" + costPrice.getCost().FinanceCategoryCode);
            Console.WriteLine("LegacyFinanceKey.LegacyMonetaryClassID :" + costPrice.getCost().LegacyFinanceKey.LegacyMonetaryClassID);
            Console.WriteLine("LegacyFinanceKey.LegacyMonetaryCalculationSystemID :" + costPrice.getCost().LegacyFinanceKey.LegacyMonetaryCalculationSystemID);
            Console.WriteLine("LegacyFinanceKey.LegacyMonetaryCalculationID :" + costPrice.getCost().LegacyFinanceKey.LegacyMonetaryCalculationID);


        }
        */
    }

    // CostList
    public void buildCostList(CarProductType carProduct, Node arisResponse)
    {
        if (null == carProduct.getCostList())
        {
            carProduct.setCostList(new CostListType());
        }
        if (null == carProduct.getCostList().getCost())
        {
            carProduct.getCostList().setCost(new ArrayList<>());
        }
        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:chargeDetails[4]/:type
        final List<Node> rateDetails = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(arisResponse, "rateDetails"), "rateDetail");

        double totalCost = 0;
        double baseCost = 0;
        double taxAndFeeAmount = 0;
        String miscCurrencyCode = "";
        if (rateDetails != null)
        {
            for (final Node rateDetail : rateDetails)
            {
                // ChargeDetails = 008 or 009, Extral hour or extral day.
                final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(rateDetail, "chargeDetails");

                if (chargeDetails != null)
                {
                    for (final Node chargeDetail : chargeDetails)
                    {
                        final String type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();

                        //if (type != "008" && type != "009")
                        if ((null != type) && (type.equals("045") || type.equals("108") || type.equals("113") || type.equals("COV") || type.equals("013"))) //Meichun 2013.10.23: Include special equipment fee - type 013
                        {
                            String amount = "";
                            final Node amountNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount");

                            if (amountNode == null)
                            {
                                continue;
                            } else
                            {
                                amount = amountNode.getTextContent();
                            }
                            taxAndFeeAmount += Double.parseDouble(amount);
                            Node currencyCodeNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "currency");
                            final Node commentNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "comment");

                            ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:tariffInfo/:currency
                            if (currencyCodeNode == null)
                            {
                                currencyCodeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "currency");
                            }
                            final CostType cost = new CostType();
                            cost.setMultiplierOrAmount(new MultiplierOrAmountType());
                            cost.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
                            cost.getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
                            //cost.setFinanceApplicationUnitCount(); 1;
                            setCostForChargeDetail(cost, type);
                            //setCurrencyAmount(costPrice.getCost().MultiplierOrAmount.CurrencyAmount, amount, currencyCodeNode.InnerText.Trim());
                            setAmount(cost.getMultiplierOrAmount().getCurrencyAmount().getAmount(), amount);
                            cost.getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currencyCodeNode.getTextContent());
                            if (commentNode != null)
                            {
                                cost.setDescriptionRawText(commentNode.getTextContent());
                            }
                            if(!StringUtils.isEmpty(amount)){
                                totalCost = Double.parseDouble(amount);
                            }

                            //Meichun 2/16/2014 for one way dropoff charge, FinanceCategoryCode should be "Fee"
                            if (cost.getDescriptionRawText().contains(CommonEnumManager.CostDescriptionRawText.OneWayCharge.getDescriptionRawText()))
                            {
                                cost.setFinanceCategoryCode("Fee");
                            }

                            //setLegacyFinanceKey(cost);
                            carProduct.getCostList().getCost().add(cost);
                        }
                    }
                }

                //TariffInfo.
                ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[2]/:tariffInfo
                ////:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[2]/:tariffInfo/:amount
                String amountTar = "";
                String currencyTar = "";
                String amountTypeTar = "";
                String ratePlanIndicator = "";

                final Node amountTypeTarNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "amountType");

                if (amountTypeTarNode != null)
                {
                    amountTypeTar = amountTypeTarNode.getTextContent();
                }
                if ((null != amountTypeTar) && (amountTypeTar.equals("904") || amountTypeTar.equals("RB")))
                {
                    final Node amountTarNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "amount");
                    final Node currencyTarNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "currency");
                    final Node ratePlanIndicatorNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "ratePlanIndicator");
                    if (amountTarNode != null)
                    {
                        amountTar = amountTarNode.getTextContent();
                    }
                    if (currencyTarNode != null)
                    {
                        currencyTar = currencyTarNode.getTextContent();
                    }
                    if (ratePlanIndicatorNode != null)
                    {
                        ratePlanIndicator = ratePlanIndicatorNode.getTextContent();
                    }

                    final CostType costTar = new CostType();
                    costTar.setMultiplierOrAmount(new MultiplierOrAmountType());
                    costTar.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
                    costTar.getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
                    setFinanceOfCostForTariffInfo(costTar, amountTypeTar, ratePlanIndicator);
                    setAmount(costTar.getMultiplierOrAmount().getCurrencyAmount().getAmount(), amountTar);
                    costTar.getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currencyTar);
                    carProduct.getCostList().getCost().add(costTar);
                    if (!StringUtils.isEmpty(amountTar))
                    {
                        if (null != amountTypeTar && amountTypeTar.equals("904"))
                        {
                            totalCost = Double.parseDouble(amountTar);
                            miscCurrencyCode = currencyTar;
                        } else
                        {
                            baseCost = Double.parseDouble(amountTar);
                        }
                    }
                }
            }
        }

        if (totalCost - baseCost - taxAndFeeAmount > 0)
        {
            final CostType miscCost = new CostType();
            miscCost.setFinanceCategoryCode("Misc");
            miscCost.setFinanceApplicationCode("Trip");
            miscCost.setFinanceApplicationUnitCount(1L);
            miscCost.setDescriptionRawText("Misc Charges");

            miscCost.setMultiplierOrAmount(new MultiplierOrAmountType());
            miscCost.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
            miscCost.getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());

            final double miscAmount = totalCost - baseCost - taxAndFeeAmount;
            final BigDecimal amountB = new BigDecimal(String.valueOf(miscAmount));
            final String miscAmountStr = amountB.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
            setAmount(miscCost.getMultiplierOrAmount().getCurrencyAmount().getAmount(), miscAmountStr);
            miscCost.getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(miscCurrencyCode);
            carProduct.getCostList().getCost().add(miscCost);
        }

        setLegacyFinanceKey(carProduct.getCostList());
/*
        Console.WriteLine("\n\nThe expected CostList..............:");
        foreach (Cost cost in costList)
        {
            Console.WriteLine("\nDescriptionRawText:" + cost.DescriptionRawText);
            Console.WriteLine("MultiplierOrAmount.CurrencyAmount.Amount.Decimal :" + cost.MultiplierOrAmount.CurrencyAmount.Amount.Decimal);
            Console.WriteLine("MultiplierOrAmount.CurrencyAmount.Amount.DecimalPlaceCount :" + cost.MultiplierOrAmount.CurrencyAmount.Amount.DecimalPlaceCount);
            Console.WriteLine("MultiplierOrAmount.CurrencyAmount.CurrencyCode:" + cost.MultiplierOrAmount.CurrencyAmount.CurrencyCode);
            Console.WriteLine("FinanceApplicationCode :" + cost.FinanceApplicationCode);
            Console.WriteLine("FinanceApplicationUnitCount :" + cost.FinanceApplicationUnitCount);
            Console.WriteLine("FinanceCategoryCode:" + cost.FinanceCategoryCode);
            Console.WriteLine("LegacyFinanceKey.LegacyMonetaryClassID :" + cost.LegacyFinanceKey.LegacyMonetaryClassID);
            Console.WriteLine("LegacyFinanceKey.LegacyMonetaryCalculationSystemID :" + cost.LegacyFinanceKey.LegacyMonetaryCalculationSystemID);
            Console.WriteLine("LegacyFinanceKey.LegacyMonetaryCalculationID :" + cost.LegacyFinanceKey.LegacyMonetaryCalculationID);


        }
        */
    }

    private static void setCostForChargeDetail(CostType cost, String type)
    {
        cost.setFinanceApplicationCode("Trip");
        cost.setFinanceApplicationUnitCount(1L);
        if (null != type && type.equals("008"))
        {
            cost.setFinanceApplicationCode("ExtraDaily");
            cost.setFinanceCategoryCode("Extra");
            cost.setDescriptionRawText("extra day charge");
        } else if (null != type && type.equals("009"))
        {
            cost.setFinanceApplicationCode("ExtraHourly");
            cost.setFinanceCategoryCode("Extra");
            cost.setDescriptionRawText("extra hour charge");
        } else if (null != type && type.equals("045"))
        {
            cost.setFinanceCategoryCode("Taxes");
        } else if (null != type && type.equals("108"))
        {
            cost.setFinanceCategoryCode("Surcharge");
        } else if (null != type && type.equals("113"))
        {
            cost.setFinanceCategoryCode("Prepayment");
        } else if (null != type && (type.equals("COV") || type.equals("013"))) //Meichun 2013.10.23: For special equipment, it should be fee
        {
            cost.setFinanceCategoryCode("Fee");//"Coverage";
        }
    }

    private static void setLegacyFinanceKey(CostListType costList)
    {
        for (final CostType cost : costList.getCost())
        {
            if (null == cost.getLegacyFinanceKey())
            {
                cost.setLegacyFinanceKey(new LegacyFinanceKeyType());
            }
            if (cost.getFinanceCategoryCode().equals("Base"))
            {
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(14);
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
                cost.getLegacyFinanceKey().setLegacyMonetaryClassID(1);
            } else if (cost.getFinanceCategoryCode().equals("Total"))
            {
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(0);
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(0);
                cost.getLegacyFinanceKey().setLegacyMonetaryClassID(0);
            } else if (cost.getFinanceCategoryCode().equals("Surcharge") || cost.getFinanceCategoryCode().equals("Taxes") || cost.getFinanceCategoryCode().equals("Fee"))
            {
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(10);
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
                cost.getLegacyFinanceKey().setLegacyMonetaryClassID(3);

                //Meichun 2/16/2014 for one way dropoff charge, LegacyFinanceKey should be 18, 1, 27
                if (cost.getDescriptionRawText().contains(CommonEnumManager.CostDescriptionRawText.OneWayCharge.getDescriptionRawText()))
                {
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(27);
                    cost.getLegacyFinanceKey().setLegacyMonetaryClassID(18);
                }
            } else if (cost.getFinanceCategoryCode().equals("Misc") || cost.getFinanceCategoryCode().equals("MiscBase"))
            {
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(6);
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
                cost.getLegacyFinanceKey().setLegacyMonetaryClassID(8);
            }
        }
    }

    private static void setLegacyFinanceKey(CostPriceListType costPriceList)
    {
        for (final CostPriceType costPrice : costPriceList.getCostPrice())
        {
            if(null == costPrice.getCost())
            {
                costPrice.setCost(new CostType());
            }
            if(null == costPrice.getCost().getLegacyFinanceKey())
            {
                costPrice.getCost().setLegacyFinanceKey(new LegacyFinanceKeyType());
            }
            if (costPrice.getCost().getFinanceApplicationCode().equals("ExtraDaily"))
            {
                costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(7);
            } else if (costPrice.getCost().getFinanceApplicationCode().equals("ExtraHourly"))
            {
                costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(25);
            } else
            {
                costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(21);
            }
            costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
            costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryClassID(8);
        }
    }

    private static void setCurrencyAmount(CurrencyAmountType currencyAmount, String amount, String currencyCode)
    {
        final int len = amount.length() - amount.indexOf('.') - 1;
        currencyAmount.getAmount().setDecimalPlaceCount(len);
        currencyAmount.getAmount().setDecimal(new Double(Double.parseDouble(amount) * Math.pow(10, len)).intValue());

        currencyAmount.setCurrencyCode(currencyCode);
    }

    private static void setAmount(AmountType amount, String amountStr)
    {
        final int len = amountStr.length() - amountStr.indexOf('.') - 1;
        amount.setDecimalPlaceCount(len);

        if(StringUtils.isEmpty(amountStr)){
            amount.setDecimal(0);
        }
        else {
            amount.setDecimal(new Double(Double.parseDouble(amountStr) * Math.pow(10, len)).intValue());
        }
    }

    private static void setFinanceOfCostForTariffInfo(CostType cost, String amountType, String ratePlanIndicator)
    {
        cost.setFinanceApplicationUnitCount(1L);

        if (null != amountType && amountType.equals("RB"))
        {
            cost.setFinanceCategoryCode("Base");
            cost.setFinanceApplicationCode("Trip");
            cost.setDescriptionRawText("Base Rate Total");
        } else if (null != amountType && amountType.equals("904"))
        {
            cost.setFinanceCategoryCode("Total");
            cost.setFinanceApplicationCode("Total");
            cost.setDescriptionRawText("Estimated total amount");
        } else if (null != amountType && amountType.equals("RP"))
        {
            cost.setFinanceCategoryCode("Base");
            cost.setDescriptionRawText("Base");
            if (null != ratePlanIndicator && ratePlanIndicator.equals("DY"))
            {
                cost.setFinanceApplicationCode("Daily");
            } else if (null != ratePlanIndicator && ratePlanIndicator.equals("MY"))
            {
                cost.setFinanceApplicationCode("Monthly");
            } else if (null != ratePlanIndicator && ratePlanIndicator.equals("WD"))
            {
                cost.setFinanceApplicationCode("Weekend");
            } else if (null != ratePlanIndicator && ratePlanIndicator.equals("WY"))
            {
                cost.setFinanceApplicationCode("Weekly");
            }
            //else if (ratePlanIndicator.equals("")
            //{
            //    cost.setFinanceApplicationCode("";
            //}
            else
            {
                cost.setFinanceApplicationCode("Trip");
            }
        }
    }

    public void buildCarMileage(CarProductType carProduct, Node arisResponse)
    {
        if(null == carProduct.getCarMileage())
        {
            carProduct.setCarMileage(new CarMileageType());
        }

        // /:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:chargeDetails[1]/:type
        // /:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:chargeDetails[4]/:type
        final List<Node> rateDetails = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(arisResponse, "rateDetails"), "rateDetail");

        if (rateDetails != null)
        {
            String distanceUnit = "";
            String amount = "";
            String distanceUnitCount = "";
            for (final Node rateDetail : rateDetails)
            {
                final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(rateDetail, "chargeDetails");
                if (chargeDetails != null)
                {
                    for (final Node chargeDetail : chargeDetails)
                    {
                        final String type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();
                        final Node amountNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount");
                        final Node distanceUnitCountNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "numberInParty");

                        //if (distanceUnitCountNode != null) distanceUnitCount = distanceUnitCountNode.InnerText.Trim();
                        if (null != type && type.equals("032"))
                        {
                            if (amountNode != null)
                            {
                                amount = amountNode.getTextContent();
                            }
                            //amount = arisResponse.SelectSingleNode("//n2:rateDetails/n2:rateDetail[" + i + "]/n2:chargeDetails[" + j + "]/n2:amount", xnmPNR).InnerText.Trim();
                            distanceUnit = "KM";
                        } else if (null != type && type.equals("031"))
                        {
                            if (amountNode != null)
                            {
                                amount = amountNode.getTextContent();
                            }
                            //amount = arisResponse.SelectSingleNode("//n2:rateDetails/n2:rateDetail[" + i + "]/n2:chargeDetails[" + j + "]/n2:amount", xnmPNR).InnerText.Trim();
                            distanceUnit = "MI";
                        } else if (null != type && type.equals("033"))
                        {
                            if (distanceUnitCountNode != null)
                            {
                                distanceUnitCount = distanceUnitCountNode.getTextContent();
                            }
                            //distanceUnitCount = arisResponse.SelectSingleNode("//n2:rateDetails/n2:rateDetail[" + i + "]/n2:chargeDetails[" + j + "]/n2:numberInParty", xnmPNR).InnerText.Trim();
                            distanceUnit = "MI";
                        } else if (null != type && type.equals("034"))
                        {
                            if (distanceUnitCountNode != null)
                            {
                                distanceUnitCount = distanceUnitCountNode.getTextContent();
                            }
                            //distanceUnitCount = arisResponse.SelectSingleNode("//n2:rateDetails/n2:rateDetail[" + i + "]/n2:chargeDetails[" + j + "]/n2:numberInParty", xnmPNR).InnerText.Trim();
                            distanceUnit = "KM";
                        }
                    }
                }
            }
            if(null == carProduct.getCarMileage().getFreeDistance())
            {
                carProduct.getCarMileage().setFreeDistance(new DistanceType());
            }
            if (StringUtils.isEmpty(distanceUnitCount) && StringUtils.isEmpty(amount))
            {
                carProduct.getCarMileage().getFreeDistance().setDistanceUnitCount(-1);
            }
            else
            {
                if(null == carProduct.getCarMileage().getExtraCostPerDistance())
                {
                    carProduct.getCarMileage().setExtraCostPerDistance(new CostPerDistanceType());
                }
                if(null ==carProduct.getCarMileage().getExtraCostPerDistance().getDistance())
                {
                    carProduct.getCarMileage().getExtraCostPerDistance().setDistance(new DistanceType());
                }
                carProduct.getCarMileage().getExtraCostPerDistance().getDistance().setDistanceUnit(distanceUnit);

                if(null == carProduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount())
                {
                    carProduct.getCarMileage().getExtraCostPerDistance().setCostCurrencyAmount(new CurrencyAmountType());
                }
                if(null == carProduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().getAmount())
                {
                    carProduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().setAmount(new AmountType());
                }
                setAmount(carProduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().getAmount(), amount);

                //carVehicleOption.Cost.MultiplierOrAmount.CurrencyAmount.CurrencyCode = currency;
                if(StringUtils.isEmpty(distanceUnitCount))
                {
                    carProduct.getCarMileage().getFreeDistance().setDistanceUnitCount(0);
                }
                else {
                    carProduct.getCarMileage().getFreeDistance().setDistanceUnitCount(Integer.parseInt(distanceUnitCount));
                }
            }
        }
    }

    // CarVehicleOptionList
    public void buildCarVehicleOptionList(CarProductType carProduct, Node arisResponse)
    {
        if (null == carProduct.getCarVehicleOptionList())
        {
            carProduct.setCarVehicleOptionList(new CarVehicleOptionListType());
        }
        if (null == carProduct.getCarVehicleOptionList().getCarVehicleOption())
        {
            carProduct.getCarVehicleOptionList().setCarVehicleOption(new ArrayList<>());
        }

        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:taxCovSurchargeGroup[3]/:taxSurchargeCoverageInfo/:chargeDetails[1]/:type
        final List<Node> taxCovSurchargeGroups = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(arisResponse, "rateDetails"), "taxCovSurchargeGroup");

        if (taxCovSurchargeGroups != null)
        {
            for (final Node taxCovSurchargeGroup : taxCovSurchargeGroups)
            {
                buildCarVehicleOptionListBigLoop(carProduct, taxCovSurchargeGroup);
            }
        }
    }

    private void buildCarVehicleOptionListBigLoop(CarProductType carProduct, Node taxCovSurchargeGroup)
    {
        final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");

        if (chargeDetails != null)
        {
            for (final Node chargeDetail : chargeDetails)
            {
                buildCarVehicleOption(carProduct, chargeDetail);
            }
        }
    }

    @SuppressWarnings("PMD")
    private void buildCarVehicleOption(CarProductType carProduct, Node chargeDetail)
    {
        String comment = "";
        String amount = "";
        String currency = "";
        String periodType = "";
        String description = "";
        boolean isExits = false;
        final Node type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type");
        //Node description = arisResponse.SelectSingleNode("//n2:rateDetails/n2:taxCovSurchargeGroup[" + i + "]/n2:chargeDetails[" + j +"]/n2:description", xnmPNR);
        if (type != null && type.getTextContent().equals("013"))
        {
            isExits = true;
            //Node description = arisResponse.SelectSingleNode("//n2:rateDetails/n2:taxCovSurchargeGroup[" + i + "]/n2:chargeDetails[" + j +"]/n2:description", xnmPNR);
            final Node commentNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "comment");
            if (commentNode != null)
            {
                comment = commentNode.getTextContent();
            }

            final Node amountNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount");
            if (amountNode != null)
            {
                amount = amountNode.getTextContent();
            }

            final Node periodTypeNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "periodType");
            if (periodTypeNode != null)
            {
                periodType = periodTypeNode.getTextContent();
            }

            final Node currencyNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "currency");
            if (currencyNode != null)
            {
                currency = currencyNode.getTextContent();
            }

            //get decription node for booking status
            final Node descriptionNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "description");
            if (descriptionNode != null)
            {
                description = descriptionNode.getTextContent();
            }
        }
        if (isExits)
        {
            if ((null != description) && (description.equals("IES") || description.equals("IBR") || description.equals("NBR") || description.equals("CNF")))
            {
                final CarVehicleOptionType carVehicleOption = new CarVehicleOptionType();
                carVehicleOption.setCarVehicleOptionCategoryCode("special equipment");
                carVehicleOption.setCarSpecialEquipmentCode(comment.substring(0, 3));
                carVehicleOption.setAvailStatusCode("A");
                carVehicleOption.setDescriptionRawText(comment);
                setAmount(carVehicleOption.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount(), amount);
                carVehicleOption.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currency);
                carVehicleOption.getCost().setFinanceApplicationCode(getFinanceApplicationCodeForTaxSuchargeCoverageInfo(periodType));//periodType;
                carVehicleOption.getCost().setFinanceApplicationUnitCount(1L);
                carVehicleOption.getCost().setFinanceCategoryCode("Optional");
                carVehicleOption.getCost().setDescriptionRawText(comment);

                carProduct.getCarVehicleOptionList().getCarVehicleOption().add(carVehicleOption);
            }
        }
    }

    public void buildSpecialEquipmentList(CarReservationType carReservation, Node arisResponse)
    {
        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:taxCovSurchargeGroup[3]/:taxSurchargeCoverageInfo/:chargeDetails[1]/:type
        final List<Node> taxCovSurchargeGroups = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(arisResponse, "rateDetails"), "taxCovSurchargeGroup");

        if (taxCovSurchargeGroups != null)
        {
            for (final Node taxCovSurchargeGroup : taxCovSurchargeGroups)
            {
                final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");
                String comment = "";
                String description = "";
                for(final Node chargeDetail : chargeDetails) {
                    final Node type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type");
                    //Node description = arisResponse.SelectSingleNode("//n2:rateDetails/n2:taxCovSurchargeGroup[" + i + "]/n2:chargeDetails[" + j +"]/n2:description", xnmPNR);
                    if (type != null && type.getTextContent().equals("013")) {
                        if(null == carReservation.getCarSpecialEquipmentList())
                        {
                            carReservation.setCarSpecialEquipmentList(new CarSpecialEquipmentListType());
                            carReservation.getCarSpecialEquipmentList().setCarSpecialEquipment(new ArrayList<>());
                        }
                        //Node description = arisResponse.SelectSingleNode("//n2:rateDetails/n2:taxCovSurchargeGroup[" + i + "]/n2:chargeDetails[" + j +"]/n2:description", xnmPNR);
                        CarSpecialEquipmentType specialEquip = new CarSpecialEquipmentType();
                        final Node commentNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "comment");
                        if (commentNode != null) {
                            comment = commentNode.getTextContent();
                            specialEquip.setCarSpecialEquipmentCode(getInternalSpecialEquipmentCode(comment.split("-")[0].trim(),
                                    carReservation.getCarProduct().getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()));
                        }

                        //get decription node for booking status
                        final Node descriptionNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "description");
                        if (descriptionNode != null) {
                            description = descriptionNode.getTextContent();
                            if(description.equals("IES") || description.equals("IBR") || description.equals("CNF") || description.equals("NBR"))
                            {
                                specialEquip.setBookingStateCode("Booked");
                            }
                            else
                            {
                                specialEquip.setBookingStateCode("Unconfirmed");
                            }
                        }

                        if(!(StringUtils.isEmpty(specialEquip.getCarSpecialEquipmentCode()) && StringUtils.isEmpty(specialEquip.getBookingStateCode()))) {
                            carReservation.getCarSpecialEquipmentList().getCarSpecialEquipment().add(specialEquip);
                        }


                    }
                }

            }
        }

    }

    //SELECT *  FROM [CarAmadeusSCS_STT05].[dbo].[ExternalSupplyServiceDomainValueMap] where domaintype like '%special%'
    private String getInternalSpecialEquipmentCode(String externalCode, long vendorSupplierID)
    {
        String internalCode = "";
        if(externalCode.equals("NAV") && (vendorSupplierID == 39l || vendorSupplierID == 41l))
        {
            internalCode = "NavigationalSystem";
        }
        else if(externalCode.equals("CST"))
        {
            internalCode = "ToddlerChildSeat";
        }
        else if(externalCode.equals("CSI"))
        {
            internalCode = "InfantChildSeat";
        }
        else if(externalCode.equals("NVS"))
        {
            internalCode = "NavigationalSystem";
        }

        return internalCode;
    }

    private static String getFinanceApplicationCodeForTaxSuchargeCoverageInfo(String periodType)
    {
        String financeApplicationCode = "";
        final int typeInt = Integer.parseInt(periodType);
        if (typeInt == 1)
        {
            financeApplicationCode = "Daily";
        }
        else if (typeInt == 2)
        {
            financeApplicationCode = "Weekly";
        }
        else if (typeInt == 3)
        {
            financeApplicationCode = "Monthly";
        }
        else if (typeInt == 4)
        {
            financeApplicationCode = "Trip";
        }
        else if (typeInt == 12)
        {
            financeApplicationCode = "Base";
        }
        else if (typeInt == 13)
        {
            financeApplicationCode = "";
        }

        return financeApplicationCode;
    }
}