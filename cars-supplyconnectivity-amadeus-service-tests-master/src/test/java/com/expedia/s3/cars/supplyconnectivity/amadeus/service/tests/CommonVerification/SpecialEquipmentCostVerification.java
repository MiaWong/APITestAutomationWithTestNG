package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionListType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by miawang on 4/2/2018.
 */
public class SpecialEquipmentCostVerification
{
    Logger logger = Logger.getLogger(getClass());

    public IVerification.VerificationResult verify(CarProductType carProduct, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        if (remarks.size() > 0)
        {
            return new IVerification.VerificationResult("SpecialEquipmentCostVerification", isPassed, remarks);
        } else
        {
            verifySpecialEquipmentCostFinanceCategoryCode(carProduct.getCarVehicleOptionList(), remarks);

            if (remarks.size() > 0)
            {
                return new IVerification.VerificationResult("SpecialEquipmentCostVerification", isPassed, remarks);
            }
        }
        return new IVerification.VerificationResult("SpecialEquipmentCostVerification", true, Arrays.asList(new String[]{"Success"}));
    }

    private void verifySpecialEquipmentCostFinanceCategoryCode(CarVehicleOptionListType specialEquipment, ArrayList remarks)
    {
        if (null != specialEquipment && null != specialEquipment.getCarVehicleOption() && specialEquipment.getCarVehicleOption().size() > 0)
        {
            for (CarVehicleOptionType carVehicleOption : specialEquipment.getCarVehicleOption())
            {
                if (!carVehicleOption.getCost().getFinanceCategoryCode().equals(CommonEnumManager.FinanceCategoryCode.Fee.getFinanceCategoryCode()))
                {
                    remarks.add("SpecialEquipment Cost FinanceCategoryCode for " +
                            carVehicleOption.getDescriptionRawText() + " should be " +
                            CommonEnumManager.FinanceCategoryCode.Fee.getFinanceCategoryCode() +
                            " but is " + carVehicleOption.getCost().getFinanceCategoryCode());
                }
            }
        }
    }
}