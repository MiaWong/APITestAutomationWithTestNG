package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import org.w3c.dom.Node;

import java.util.Arrays;

/**
 * Created by fehu on 4/5/2017.
 */
public class GetDetailsRetrySearchVerification implements IGetDetailsVerification {


    @Override
    public VerificationResult verify(GetDetailsVerificationInput getDetailsVerificationInput, BasicVerificationContext verificationContext) {
        final Node varResponse = PojoXmlUtil.getNodeByTagName(verificationContext.getSpooferTransactions().getDocumentElement(),"VehAvailRateRS");
        final Node vrrResponse = PojoXmlUtil.getNodeByTagName(verificationContext.getSpooferTransactions().getDocumentElement(), "VehRateRuleRS");

        if (null != varResponse && null != vrrResponse)
        {
            return new VerificationResult(this.getName(), true, Arrays.asList(new String[]{"retry search success!"}));
        }

       return  new VerificationResult(this.getName(), false, Arrays.asList(new String[]{"retry search failed!"}));

    }
}
