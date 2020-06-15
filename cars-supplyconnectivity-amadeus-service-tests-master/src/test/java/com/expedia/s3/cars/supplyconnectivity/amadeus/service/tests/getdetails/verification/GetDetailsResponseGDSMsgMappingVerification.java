package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ARIARsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
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
public class GetDetailsResponseGDSMsgMappingVerification implements IGetDetailsVerification {
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        List<String> ignoreList = new ArrayList<>();
        ignoreList.add(CarTags.SUPPLY_SUBSET_ID);
        //TODO Mia this field is not specify in two old automation framework, need go to xlt to find out the mapping filed.
        ignoreList.add(CarTags.CAR_MILEAGE_DISTANCEUNITCOUNT);
        ignoreList.add(CarTags.AVAIL_STATUS_CODE);
        ignoreList.add(CarTags.AVAIL_STATUS_CODE);
        ignoreList.add(CarTags.SHUTTLE_CATEGORY_CODE);

        if (remarks.size() > 0) {
            return new VerificationResult(getName(), isPassed, remarks);
        } else {
            try {
                CarProductType getDetailsCar = input.getResponse().getCarProductList().getCarProduct().get(0);
                CarProductComparator.isCarProductEqual(GetGdsMsg(input.getRequest().getCarProductList().getCarProduct().get(0).getCarInventoryKey(),
                        verificationContext, remarks), getDetailsCar, remarks, ignoreList);
            } catch (DataAccessException e) {
                remarks.add("GetDetailsGDSMsgMap Response compare with GDS Msg verification failed. error message is like below: " + e.getMessage());
            }

            if (remarks.size() > 0) {
                remarks.add("GetDetailsGDSMsgMap Response compare with GDS Msg verification failed.");
                return new VerificationResult(this.getName(), isPassed, remarks);
            }
        }
        if (remarks.size() < 1) {
            isPassed = true;
        }
        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private CarProductType GetGdsMsg(CarInventoryKeyType inventoryKey_request, BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        Node ariaRspNode = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETDETAILS, GDSMsgNodeTags.AmadeusNodeTags.ARIA_CAR_GET_DETAIL_RESPONSE_TYPE);

        if (ariaRspNode != null) {
            ARIARsp ariaRsp = new ARIARsp(ariaRspNode, scsDataSource, inventoryKey_request);
            return ariaRsp.getCar();
        } else {
            remarks.add("Not Find ARIA Response In GDS Msg");
        }

        return null;
    }
}
