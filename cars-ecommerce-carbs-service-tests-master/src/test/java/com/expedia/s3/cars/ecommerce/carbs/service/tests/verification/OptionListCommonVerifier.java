package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.framework.test.common.utils.CurrencyConvertUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 9/19/2016.
 */
@SuppressWarnings("PMD")
public class OptionListCommonVerifier {
    public void verifyOptionList(String posCurrency, CarProductType reqCar, CarProductType rspCar,
                                 ArrayList remarks, String action) {
        this.verifyOptionListItemsBasicVerify(reqCar, rspCar, remarks, action);
        this.verifyOptionListItemsCurrencyConvertVerify(posCurrency, rspCar, remarks, action);
    }

    public void verifyOptionListInSearchResult(String posCurrency, CarProductType rspCar, ArrayList remarks, String action) {
        {
            if (null != rspCar.getCarVehicleOptionList() && null != rspCar.getCarVehicleOptionList().getCarVehicleOption()
                    && !rspCar.getCarVehicleOptionList().getCarVehicleOption().isEmpty()) {
                this.verifyOptionListItemsCurrencyConvertVerify(posCurrency, rspCar, remarks, action);
            }
        }
    }

    public void verifyOptionListItemsBasicVerify(CarProductType reqCar,
                                                 CarProductType rspCar, List remarks, String action) {
        if (null == rspCar.getCarVehicleOptionList()) {
            remarks.add("CarVehicleOptionList is null in " + action +
                    " should not be null, VendorSupplierID : " + rspCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() + ".");
        } else {
            boolean isPassed = false;
            //TODO should compare with GDSMsg
            if (reqCar != null && reqCar.getCarVehicleOptionList() != null)
            {
               for (CarVehicleOptionType reqCVOT : reqCar.getCarVehicleOptionList().getCarVehicleOption()) {
                    for (CarVehicleOptionType rspCVOT : rspCar.getCarVehicleOptionList().getCarVehicleOption()) {
                        if (reqCVOT.getCarVehicleOptionCategoryCode().equals(rspCVOT.getCarVehicleOptionCategoryCode())
                                && reqCVOT.getCarSpecialEquipmentCode().equals(rspCVOT.getCarSpecialEquipmentCode())
                                && reqCVOT.getCost().getFinanceApplicationCode().equals(rspCVOT.getCost().getFinanceApplicationCode())
                                && reqCVOT.getCost().getFinanceApplicationUnitCount().equals(rspCVOT.getCost().getFinanceApplicationUnitCount())
                                && reqCVOT.getCarVehicleOptionMaxCount().equals(rspCVOT.getCarVehicleOptionMaxCount())) {
                            isPassed = true;
                            break;
                        }
                    }

                    if (!isPassed) {
                        remarks.add("CarVehicleOption exist in " + action + " Request: ( CarVehicleOptionCategoryCode: " + reqCVOT.getCarVehicleOptionCategoryCode() +
                                " CarSpecialEquipmentCode : " + reqCVOT.getCarSpecialEquipmentCode() + ") is not find in " + action + " Response.");
                    } else {
                        isPassed = false;
                    }
                }
            }

            for (CarVehicleOptionType rspCVOT : rspCar.getCarVehicleOptionList().getCarVehicleOption()) {
                if (rspCVOT.getCost().getFinanceApplicationUnitCount() != 1) {
                    remarks.add("FinanceApplicationUnitCount of CarVehicleOption in " + action + " Request is:" +
                            rspCVOT.getCost().getFinanceApplicationUnitCount() + " should be 1.");
                    break;
                }
            }
        }
    }

    /*
    <ns9:Cost>
						<ns9:MultiplierOrAmount>
							<ns9:CurrencyAmount>
								<ns9:CurrencyCode>USD</ns9:CurrencyCode>
								<ns1:Amount ns1:DecimalPlaceCount="2">
									<ns1:Decimal>1300</ns1:Decimal>
								</ns1:Amount>
							</ns9:CurrencyAmount>
						</ns9:MultiplierOrAmount>
						<ns9:FinanceCategoryCode>SpecialEquipment</ns9:FinanceCategoryCode>
						<ns9:FinanceApplicationCode>Daily</ns9:FinanceApplicationCode>
						<ns9:FinanceApplicationUnitCount>1</ns9:FinanceApplicationUnitCount>
						<ns9:LegacyFinanceKey>
							<ns9:LegacyMonetaryClassID>8</ns9:LegacyMonetaryClassID>
							<ns9:LegacyMonetaryCalculationSystemID>1</ns9:LegacyMonetaryCalculationSystemID>
							<ns9:LegacyMonetaryCalculationID>21</ns9:LegacyMonetaryCalculationID>
						</ns9:LegacyFinanceKey>
						<ns1:DescriptionRawText>PerDay, CHILD SEAT/INFANT</ns1:DescriptionRawText>
					</ns9:Cost>

					<ns9:Price>
						<ns9:MultiplierOrAmount>
							<ns9:CurrencyAmount>
								<ns9:CurrencyCode>GBP</ns9:CurrencyCode>
								<ns1:Amount ns1:DecimalPlaceCount="7">
									<ns1:Decimal>104078000</ns1:Decimal>
								</ns1:Amount>
							</ns9:CurrencyAmount>
						</ns9:MultiplierOrAmount>
						<ns9:FinanceCategoryCode>SpecialEquipment</ns9:FinanceCategoryCode>
						<ns9:FinanceApplicationCode>Daily</ns9:FinanceApplicationCode>
						<ns9:FinanceApplicationUnitCount>1</ns9:FinanceApplicationUnitCount>
						<ns9:LegacyFinanceKey>
							<ns9:LegacyMonetaryClassID>8</ns9:LegacyMonetaryClassID>
							<ns9:LegacyMonetaryCalculationSystemID>1</ns9:LegacyMonetaryCalculationSystemID>
							<ns9:LegacyMonetaryCalculationID>21</ns9:LegacyMonetaryCalculationID>
						</ns9:LegacyFinanceKey>
						<ns1:DescriptionRawText>PerDay, CHILD SEAT/INFANT</ns1:DescriptionRawText>
					</ns9:Price>
     */

    private void verifyOptionListItemsCurrencyConvertVerify(String posCurrency, CarProductType rspCar, ArrayList remarks, String action) {
        if (null != rspCar.getCarVehicleOptionList() && !rspCar.getCarVehicleOptionList().getCarVehicleOption().isEmpty()) {
            for (CarVehicleOptionType rspCVOT : rspCar.getCarVehicleOptionList().getCarVehicleOption()) {
                CostType cost = rspCVOT.getCost();
                PriceType price = rspCVOT.getPrice();
                if (cost == null) {
                    remarks.add(action + " Cost in CarVehicleOption is Null in CarVehicleOption : " + rspCVOT.getDescriptionRawText());
                } else if (price == null) {
                    remarks.add(action + " Price in CarVehicleOption is Null in CarVehicleOption : " + rspCVOT.getDescriptionRawText());
                } else {
                    boolean infoEqual = cost.getFinanceCategoryCode().equals(price.getFinanceCategoryCode());
                    infoEqual = infoEqual && cost.getFinanceApplicationCode().equals(price.getFinanceApplicationCode());
                    infoEqual = infoEqual && cost.getFinanceApplicationUnitCount().equals(price.getFinanceApplicationUnitCount());
                    infoEqual = infoEqual && cost.getFinanceApplicationUnitCount().equals(price.getFinanceApplicationUnitCount());

                    infoEqual = infoEqual && cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID() == price.getLegacyFinanceKey().getLegacyMonetaryCalculationID()
                            && cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() == price.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID()
                            && cost.getLegacyFinanceKey().getLegacyMonetaryClassID() == price.getLegacyFinanceKey().getLegacyMonetaryClassID();

                    infoEqual = infoEqual && cost.getDescriptionRawText().equals(price.getDescriptionRawText());

                    if (infoEqual) {
                        if (price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(posCurrency)) {
                            try {
                                double expectedCurrencyRate = Double.parseDouble(CurrencyConvertUtil.getExchangeRate(
                                        rspCar.getCarVehicleOptionList().getCarVehicleOption().get(0).getCost().getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()
                                        , posCurrency));

                                double costAmout = cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                        Math.pow(10, cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
                                double priceAmout = price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() /
                                        Math.pow(10, price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());

                                if (priceAmout - costAmout * expectedCurrencyRate > 0.01 || costAmout * expectedCurrencyRate - priceAmout > 0.01) {
                                    remarks.add(action + " Price in CarVehicleOption " + rspCVOT.getDescriptionRawText() + " is Wrong, Actual : " + priceAmout + posCurrency +
                                            " Expected: " + costAmout * expectedCurrencyRate + posCurrency);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            remarks.add(action + " Price Currency in CarVehicleOption is Wrong, Actual : " + price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode() +
                                    " Expected: " + posCurrency);
                        }
                    } else {
                        remarks.add(action + " CarVehicleOption Info in Price is different with Cost : " + cost.getDescriptionRawText());
                    }
                }
            }
        }
    }
}