package com.expedia.s3.cars.supply.service.tests.regression.getdetails.verification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsResponseType;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by v-mechen on 1/17/2017.
 */
public class VerificationHelper {
    private VerificationHelper() {
    }
    public static void dynamicCarPolicyVerification(CarSupplyGetDetailsResponseType response,
                                                                 SpooferTransport spooferTransport,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 Logger logger, boolean gdsHasMerchantRules,
                                                                 boolean featureFlag) throws IOException, DataAccessException, SQLException {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);

        //System.out.println("spooferxml" + PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final DynamicCarPolicyVerification verifier = new DynamicCarPolicyVerification();
        final IVerification.VerificationResult result = verifier.verifyCarpolicy(response, gdsHasMerchantRules, verificationContext, featureFlag);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }
    }
}
