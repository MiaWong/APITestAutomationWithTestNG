package com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.basic.CommitPrepareChangeBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.basic.GetChangeProcessBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.basic.PrepareChangeBasicVefification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.basic.RollbackPrepareChangeBasicVerifiaction;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input.CommitPrepareChangeVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input.GetChangeProcessVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input.PrepareChangeVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input.RollbackPrepareChangeVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.GetChangeProcessRequest;
import expedia.om.supply.messages.defn.v1.GetChangeProcessResponseType;
import expedia.om.supply.messages.defn.v1.PrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.PrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.SupplyErrorType;
import org.testng.Assert;

import java.io.IOException;

/**
 * Created by fehu on 11/10/2016.
 */
public class OmCancelVerifier {

    private OmCancelVerifier() {
    }

    public static void isGetChangeProcessWorksVerifier(String guid, TestScenario scenarios, GetChangeProcessRequest requestType, GetChangeProcessResponseType responseType) throws IOException {

        final BasicVerificationContext getChangeProcessVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final GetChangeProcessVerificationInput getChangeProcessVerificationInput = new GetChangeProcessVerificationInput(requestType, responseType);
        /*final ChainedVerification<GetChangeProcessVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isGetChangeProcessWorksVerifier", Arrays.asList(new GetChangeProcessBasicVerification()));
*/
        final GetChangeProcessBasicVerification verifications =new GetChangeProcessBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(getChangeProcessVerificationInput, getChangeProcessVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

    }

    public static void isPrepareChangeWorksVerifier(String guid, TestScenario scenarios, PrepareChangeRequest requestType, PrepareChangeResponseType responseType) throws IOException {

        final BasicVerificationContext prepareChangeContext = new BasicVerificationContext(null, guid, scenarios);
        final PrepareChangeVerificationInput prepareChangeVerificationInput = new PrepareChangeVerificationInput(requestType, responseType);
       /* final ChainedVerification<PrepareChangeVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isPrepareChangeWorksVerifier", Arrays.asList(new PrepareChangeBasicVefification()));
*/
        final PrepareChangeBasicVefification vefifications = new PrepareChangeBasicVefification();
        final IVerification.VerificationResult result = vefifications.verify(prepareChangeVerificationInput, prepareChangeContext);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

    }

    public static void isCommitPrepareChangeWorksVerifier( String guid, TestScenario scenarios, CommitPrepareChangeRequest requestType, CommitPrepareChangeResponseType responseType) throws IOException {
        final BasicVerificationContext CommitPrepareChangeVerificationContext = new BasicVerificationContext(null, guid, scenarios);
        final CommitPrepareChangeVerificationInput CommitPrepareChangeVerificationInput = new CommitPrepareChangeVerificationInput(requestType, responseType);
       /* final ChainedVerification<CommitPrepareChangeVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isCommitPrepareChangeWorksVerifier", Arrays.asList(new CommitPrepareChangeBasicVerification()));
*/
        final CommitPrepareChangeBasicVerification verifications = new CommitPrepareChangeBasicVerification();
        final IVerification.VerificationResult result = verifications.verify(CommitPrepareChangeVerificationInput, CommitPrepareChangeVerificationContext);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }


    }

    public static void isRollbackPrepareChangeWorksVerifier(String guid, TestScenario scenarios, RollbackPrepareChangeRequest requestType, RollbackPrepareChangeResponseType responseType) throws IOException {
        final BasicVerificationContext context = new BasicVerificationContext(null, guid, scenarios);
        final RollbackPrepareChangeVerificationInput rollbackPrepareChangeVerificationInput = new RollbackPrepareChangeVerificationInput(requestType, responseType);
      /*  final ChainedVerification<RollbackPrepareChangeVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>("isRollbackPrepareChangeWorksVerifier", Arrays.asList(new RollbackPrepareChangeBasicVerifiaction()));
*/
        final RollbackPrepareChangeBasicVerifiaction verifiactions = new RollbackPrepareChangeBasicVerifiaction();
        final IVerification.VerificationResult result = verifiactions.verify(rollbackPrepareChangeVerificationInput, context);

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }
    }


    //prepay car should can't cancel.
    public static void isPrepayCarCannotCancel(GetChangeProcessResponseType responseType) throws IOException
    {
        if(null == responseType.getResponseStatus().getSupplyErrorList()||
                null == responseType.getResponseStatus().getSupplyErrorList())
        {
            Assert.fail("\nPrepay car can not be cancel.");
        }

        boolean prepayCannotCancelExpectMsg = false;
        for(final SupplyErrorType supplyError : responseType.getResponseStatus().getSupplyErrorList().getSupplyError())
        {
            if (supplyError.getErrorDescription().contains("PrepaidAgency offers cannot be cancelled"))
            {
                prepayCannotCancelExpectMsg = true;
                break;
            }
        }

        if(!prepayCannotCancelExpectMsg)
        {
            Assert.fail("\n Do not get expected cancel error \"PrepaidAgency offers cannot be cancelled\" for hertz prepay car.");
        }
    }
}
