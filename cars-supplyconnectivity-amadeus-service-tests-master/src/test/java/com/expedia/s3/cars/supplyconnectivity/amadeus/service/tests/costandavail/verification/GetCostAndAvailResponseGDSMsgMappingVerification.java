package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
public class GetCostAndAvailResponseGDSMsgMappingVerification implements IGetCostAndAvailabilityVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        List<String> ignoreList = new ArrayList<>();
        ignoreList.add(CarTags.SUPPLY_SUBSET_ID);
        ignoreList.add(CarTags.CAR_DROP_OFF_LOCATION);
        ignoreList.add(CarTags.CAR_PICK_UP_LOCATION);
//        ignoreList.add(CarTags.CAR_CATALOG_MAKE_MODEL);
        ignoreList.add(CarTags.CAR_DOOR_COUNT);

        if (remarks.size() > 0) {
            return new VerificationResult(getName(), isPassed, remarks);
        } else {
            try {
                CarProductType getCostAndAvailabilityCar = input.getResponse().getCarProductList().getCarProduct().get(0);
                CarProductComparator.isCarProductEqual(GetGdsMsg(input.getRequest().getCarProductList().getCarProduct().get(0),
                        verificationContext, remarks), getCostAndAvailabilityCar, remarks, ignoreList);
            } catch (DataAccessException e) {
                remarks.add("GetCostAndAvailabilityGDSMsgMap Response compare with GDS Msg verification failed. error message is like below: " + e.getMessage());
            }

            if (remarks.size() > 0) {
                remarks.add("GetCostAndAvailabilityGDSMsgMap Response compare with GDS Msg verification failed.");
                return new VerificationResult(this.getName(), isPassed, remarks);
            }
        }
        if (remarks.size() < 1) {
            isPassed = true;
        }
        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private CarProductType GetGdsMsg(CarProductType responseCar, BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        Node acaqRspNode = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY, GDSMsgNodeTags.RESPONSE);

        if (acaqRspNode != null) {
            ACAQRsp acaqRsp = new ACAQRsp(acaqRspNode, null, scsDataSource);

            for(CarProductType gdsCar : acaqRsp.getGdsCarProductList())
            {
                if(CarProductComparator.isCorrespondingCar(responseCar, gdsCar, false, true))
                {
                    return gdsCar;
                }
            }
            return acaqRsp.getGdsCarProductList().get(0);
        } else {
            remarks.add("Not Find ACAQ Response In GDS Msg");
        }

        return null;
    }
}
