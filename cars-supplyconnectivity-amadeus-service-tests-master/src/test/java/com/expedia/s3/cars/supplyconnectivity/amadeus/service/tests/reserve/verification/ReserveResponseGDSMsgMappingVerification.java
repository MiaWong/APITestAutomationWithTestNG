package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
public class ReserveResponseGDSMsgMappingVerification implements IReserveVerification {
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        List<String> ignoreList = new ArrayList<>();
        ignoreList.add(CarTags.CAR_VEHICLE_OPTIONLIST);
        ignoreList.add(CarTags.CAR_VENDOR_LOCATION_ID);

        if (remarks.size() > 0) {
            return new VerificationResult(getName(), isPassed, remarks);
        } else {
            try {
                final CarProductType reserveCar = input.getResponse().getCarReservation().getCarProduct();
                final CarInventoryKeyType reqInventoryKey = input.getRequest().getCarProduct().getCarInventoryKey();
                final CarProductType gdsCarproduct = GetGdsMsg(verificationContext, reqInventoryKey, remarks);
                gdsCarproduct.setAvailStatusCode(input.getRequest().getCarProduct().getAvailStatusCode());
                CarProductComparator.isCarProductEqual(gdsCarproduct, reserveCar, remarks, ignoreList);
            } catch (DataAccessException e) {
                remarks.add("ReserveGDSMsgMap Response compare with GDS Msg verification failed. error message is like below: " + e.getMessage());
            }

            if (remarks.size() > 0) {
                remarks.add("ReserveGDSMsgMap Response compare with GDS Msg verification failed.");
                return new VerificationResult(this.getName(), isPassed, remarks);
            }
        }
        if (remarks.size() < 1) {
            isPassed = true;
        }
        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }



    private CarProductType GetGdsMsg(BasicVerificationContext verificationContext, CarInventoryKeyType reqInventoryKey, ArrayList remarks) throws DataAccessException {
      CarReservationType carReservation = ReserveVerificationHelper.getCarReservationType(verificationContext, reqInventoryKey, remarks);
      return carReservation.getCarProduct();
    }
}
