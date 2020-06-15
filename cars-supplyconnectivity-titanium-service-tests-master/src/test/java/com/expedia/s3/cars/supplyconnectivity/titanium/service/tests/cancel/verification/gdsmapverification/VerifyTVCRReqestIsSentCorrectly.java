package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification.gdsmapverification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVCRReq;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.ICancelVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;


/**
 * Created by v-mechen on 3/1/2017.
 */


public class VerifyTVCRReqestIsSentCorrectly implements ICancelVerification {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(CancelVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(CancelVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final String errorMessage = this.verifyTVCRReqMatched(input.getRequest(), verificationContext);

        return VerificationHelper.getVerificationResult(errorMessage, this.getName());
    }



    private String verifyTVCRReqMatched(CarSupplyConnectivityCancelRequestType request, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        //Read TVCR request
        final TVCRReq gdsReq = new TVCRReq(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.CANCELREQUEST).item(0));

        //Compare PNR
        CarNodeComparator.isStringNodeEqual("PNR", getExpectedPNR(request), gdsReq.getPnr(), errorMsg, new ArrayList<String>());

        return errorMsg.toString().trim();

    }

    /*
    Get expected PNR from request
     */
    private String getExpectedPNR(CarSupplyConnectivityCancelRequestType request){
        String expPNR = null;
        for(final ReferenceType reference : request.getCarReservation().getReferenceList().getReference())
        {
            if(reference.getReferenceCategoryCode().equals("PNR"))
            {
                expPNR = reference.getReferenceCode() ;
            }
        }
        return expPNR;
    }




}

