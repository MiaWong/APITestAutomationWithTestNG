package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.CarMileageType;
import com.expedia.e3.data.financetypes.defn.v4.CostPerDistanceType;
import com.expedia.e3.data.financetypes.defn.v4.CurrencyAmountType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */
public class CarMileageReader {
    private CarMileageReader(){

    }
    /// <summary>
    /// read CarMileage from rateDistanceNode and fee(limited)
    /// </summary>
    /// <param name="rateDistanceNode"></param>
    /// <param name="feeNodeList"></param>
    /// <param name="CarsSCSDataSource"></param>
    /// <returns></returns>
    public static CarMileageType readCarMileage(Node rateDistanceNode, List<Node> feeNodeList, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final CarMileageType carMileage = new CarMileageType();
        carMileage.setFreeDistance(new DistanceType());
        carMileage.setFreeDistanceRatePeriodCode(rateDistanceNode.getAttributes().getNamedItem("VehiclePeriodUnitName").getTextContent());
        if (null != rateDistanceNode.getAttributes().getNamedItem("Unlimited") && Boolean.parseBoolean(rateDistanceNode.getAttributes().getNamedItem("Unlimited").getTextContent()))
        {
            carMileage.getFreeDistance().setDistanceUnitCount(-1);
        }
        else
        {
            //DistanceUnit is from rateDistanceNode node
            carMileage.getFreeDistance().setDistanceUnit(scsDataSource.getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.DISTANCE_UNIT, null, rateDistanceNode.getAttributes().getNamedItem("DistUnitName").getTextContent()).get(0).getDomainValue());
            carMileage.getFreeDistance().setDistanceUnitCount(Integer.parseInt(rateDistanceNode.getAttributes().getNamedItem("Quantity").getTextContent()));//Quantity="600"
            for (final Node feeNode : feeNodeList)
            {
                //Amount and currencyCode is from Fee
                if (feeNode.getAttributes().getNamedItem("Purpose").getTextContent().equals("8"))
                {
                    carMileage.setExtraCostPerDistance(new CostPerDistanceType());
                    carMileage.getExtraCostPerDistance().setDistance(new DistanceType());
                    carMileage.getExtraCostPerDistance().setCostCurrencyAmount(new CurrencyAmountType());
                    carMileage.getExtraCostPerDistance().getCostCurrencyAmount().setAmount(new AmountType());
                    carMileage.getExtraCostPerDistance().getDistance().setDistanceUnitCount(1);
                    carMileage.getExtraCostPerDistance().getDistance().setDistanceUnit(carMileage.getFreeDistance().getDistanceUnit());
                    carMileage.getExtraCostPerDistance().getCostCurrencyAmount().setCurrencyCode(feeNode.getAttributes().getNamedItem("CurrencyCode").getTextContent());
                    final String extraDisAmount = feeNode.getAttributes().getNamedItem("Amount").getTextContent();
                    carMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimal(Integer.parseInt(extraDisAmount.replace(".", "")));
                    carMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimalPlaceCount(extraDisAmount.contains(".") ? extraDisAmount.split("\\." )[1].length() : 0);
                }
            }
        }
        return carMileage;
    }

}
