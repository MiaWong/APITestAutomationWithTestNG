package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.framework.action.ActionSequenceAbortException;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by v-mechen on 9/12/2018.
 */
public class VerifyBookingLogging {
    private  VerifyBookingLogging()
    {
    }
    public static void verifyBookingLogging(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator, TestData param, SpooferTransport spooferTransport) throws DataAccessException, IOException, SQLException, ActionSequenceAbortException
    {
        VerifyBooking.verifyBooking(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());

        VerifyBookingItemCar.verifyBookingItemCar(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType(), param.getScenarios(), param.getGuid(),
                carbsOMReserveReqAndRespGenerator.getStandaloneSearchResponseType(), spooferTransport);

        boolean cancelled = true;
        if (param.getTestScenarioSpecialHandleParam().isHertzPrepayTestCase() || "Y".equalsIgnoreCase(param.getNeedPrepaidCar()))
        {
            cancelled = false;
        }
        VerifyBookingItem.verifyBookingItem(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType(), cancelled, param.getScenarios());


        VerifyBookingAmount.verifyBookingAmount(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
    }
}
