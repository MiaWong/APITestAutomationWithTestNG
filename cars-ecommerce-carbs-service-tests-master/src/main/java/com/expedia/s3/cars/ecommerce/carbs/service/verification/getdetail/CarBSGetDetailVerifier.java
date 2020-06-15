package com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.CarbsCommonVerification;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.Assert;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by fehu on 11/9/2016.
 */
public class CarBSGetDetailVerifier {

    private CarBSGetDetailVerifier() {
    }

    public static void isCarbsGetDetailWorksVerifier(String guid, TestScenario scenarios, CarECommerceGetDetailsRequestType requestType, CarECommerceGetDetailsResponseType responseType) throws IOException {

        final BasicVerificationContext getDetailVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final GetDetailsVerificationInput getDetailVerificationInput = new GetDetailsVerificationInput(requestType, responseType);
       /* final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCarbsGetDetailWorksVerifier", Arrays.asList(new CarbsGetDetailResponseBasicVerification()));
      */
       final CarbsGetDetailResponseBasicVerification verifications = new CarbsGetDetailResponseBasicVerification();
       final IVerification.VerificationResult result = verifications.verify(getDetailVerificationInput, getDetailVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT+ result.toString());
        }

    }

     //just for MN car verify
    public static void retrySearchForGetDetailsVerifier(String estimatedTotalFeature, SpooferTransport spooferTransport, String guid, TestScenario scenarios, CarECommerceGetDetailsRequestType requestType, CarECommerceGetDetailsResponseType responseType) throws Exception {

        final Document spooferTransactions = SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getDetailVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsVerificationInput getDetailVerificationInput = new GetDetailsVerificationInput(requestType, responseType);
        final ChainedVerification<GetDetailsVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("RetrySearchForGetDetailsVerifier", Arrays.asList(new CarbsGetDetailResponseBasicVerification()));
        final IVerification.VerificationResult result = verifications.verify(getDetailVerificationInput, getDetailVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT+ result.toString());
        }
        if(SettingsProvider.SPOOFER_ENABLE)
        {
            final Document doc = getDetailVerificationContext.getSpooferTransactions();

            //when do get detail ,Shopping.alwaysRenewIDContext/enable is always 1, so for the SCSMN car will retry search,if car BS retry search ,there will be 2 VAR send
            if (doc.getElementsByTagName("VehAvailRateRS").getLength() == 1 && !"ContainedEstimatedTotalAndTotalReferencePrice".equalsIgnoreCase(estimatedTotalFeature))
            {
                Assert.fail(new IVerification.VerificationResult("RetrySearchForGetdetailAndCostandAvailVerify", false, Arrays.asList("There should  be retry search")).toString());
            }
            if (doc.getElementsByTagName("VehAvailRateRS").getLength() == 2 && "ContainedEstimatedTotalAndTotalReferencePrice".equalsIgnoreCase(estimatedTotalFeature))
            {
                Assert.fail(new IVerification.VerificationResult("RetrySearchForGetdetailAndCostandAvailVerify", false, Arrays.asList("There should not be retry search")).toString());
            }
        }
        else
        {
            Assert.fail("To verify if EstimatedTotalAndTotalReferencePrice is return correct, should use spoofer, please enable Config : serviceTransport.server.useSpoofer");
        }
    }

    //hertz prepay verify

    public static String verifyIfPrePayBooleanReturnInGetDetailsRequestAndResponseForHertz(CarECommerceGetDetailsRequestType requestType,
                                                                                         CarECommerceGetDetailsResponseType responseType,
                                                                                         DataSource inventoryDataSource) throws IOException, DataAccessException
    {
        final StringBuilder errorMsg = new StringBuilder();

        final CarbsCommonVerification verifications = new CarbsCommonVerification();
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(inventoryDataSource);

        errorMsg.append(verifications.verifyHertzPrepayCarIsCorrect(requestType.getCarProductList().getCarProduct().get(0), inventoryHelper))
                .append(verifications.verifyHertzPrepayCarIsCorrect(responseType.getCarProductList().getCarProduct().get(0), inventoryHelper));

        if (!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }
        return errorMsg.toString();
    }


    public static String verifyIfPrePayCarReferencePriceReturnForGetDetail(CarECommerceGetDetailsResponseType responseType, boolean shouldReferencePriceReturned) throws DataAccessException
    {
        final StringBuilder errorMsg = new StringBuilder();

        final CarbsCommonVerification verifications = new CarbsCommonVerification();

        errorMsg.append(verifications.verifyIfPrePayCarReferencePriceReturn(responseType.getCarProductList().getCarProduct().get(0), shouldReferencePriceReturned));

        if (!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }
        return errorMsg.toString();
    }
}
