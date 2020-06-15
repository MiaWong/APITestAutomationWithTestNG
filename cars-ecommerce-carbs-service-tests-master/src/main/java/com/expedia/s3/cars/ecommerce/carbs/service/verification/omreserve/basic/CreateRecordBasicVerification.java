package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic;

import com.expedia.s3.cars.ecommerce.carbs.service.database.CarbsDB;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.CreateRecordVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * Created by fehu on 11/10/2016.
 */
public class CreateRecordBasicVerification implements IVerification<CreateRecordVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_CORRECT_IN_RESPONSE = "Send CreateRecord message failed. No CarProduct in response!";
    private static final String MESSAGE_SUCCESS = "Success";
    final private Logger logger =  Logger.getLogger(getClass().getName());
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(CreateRecordVerificationInput input, BasicVerificationContext basicVerificationContext) {

        if (!input.getResponse().getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS)) {
            return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CORRECT_IN_RESPONSE));
        }
        //query recordLocatioId from db
        final CarbsDB carbsDB = new CarbsDB(DatasourceHelper.getCarBSDatasource());
        try {
            final long cout = carbsDB.getCountByBookingRecordLocatorID(input.getResponse().getRecordLocator());
            if (1 != cout) {
                return new VerificationResult(getName(), false, Arrays.asList("Verify CreateRecord message failed. RecordLocator is not unique;"));
            }
        } catch (DataAccessException e) {
            logger.error(e.getStackTrace(), e.getCause());
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }
}
