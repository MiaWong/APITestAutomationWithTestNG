package com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.CarbsCommonVerification;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn
        .v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn
        .v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.testng.Assert;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by fehu on 11/10/2016.
 */
public class CarBSGetCostAndAvailVerifier
{

    private CarBSGetCostAndAvailVerifier()
    {
    }

    //hertz prepay verify
    @SuppressWarnings("CPD_Start")
    public static String verifyIfPrePayBooleanReturnInGetDetailsRequestAndResponseForHertz(CarECommerceGetCostAndAvailabilityRequestType costAndAvailabilityRequestType,
                                                                                           CarECommerceGetCostAndAvailabilityResponseType costAndAvailabilityResponseType,
                                                                                           DataSource inventoryDataSource) throws IOException, DataAccessException
    {
        final StringBuilder errorMsg = new StringBuilder();

        final CarbsCommonVerification carbsCommonVerification = new CarbsCommonVerification();
        final CarsInventoryHelper invDBHelper = new CarsInventoryHelper(inventoryDataSource);

        errorMsg.append(carbsCommonVerification.verifyHertzPrepayCarIsCorrect(costAndAvailabilityRequestType.getCarProductList().getCarProduct().get(0), invDBHelper))
                .append(carbsCommonVerification.verifyHertzPrepayCarIsCorrect(costAndAvailabilityResponseType.getCarProductList().getCarProduct().get(0), invDBHelper));

        if (!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }
        return errorMsg.toString();
    }
    @SuppressWarnings("CPD_End")

    public static void isCarbsGetCostAndAvailWorksVerifier(String guid, TestScenario scenarios, CarECommerceGetCostAndAvailabilityRequestType requestType, CarECommerceGetCostAndAvailabilityResponseType responseType) throws IOException

    {
        final BasicVerificationContext getCostAndAvailVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInput = new GetCostAndAvailabilityVerificationInput(requestType, responseType);
      /*  final ChainedVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCarbsGetCostAndAvailWorksVerifier", Arrays.asList(new CarbsGetCostAndAvailResponseBasicVerification()));
*/
        final CarbsGetCostAndAvailResponseBasicVerification verifications = new CarbsGetCostAndAvailResponseBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(getCostAndAvailVerificationInput, getCostAndAvailVerificationContext);

        if (!result.isPassed())
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result.toString());
        }

    }
}