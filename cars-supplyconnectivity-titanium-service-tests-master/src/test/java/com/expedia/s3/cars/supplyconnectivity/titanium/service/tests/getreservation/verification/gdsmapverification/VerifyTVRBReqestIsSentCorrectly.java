package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.verification.gdsmapverification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRBReq;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;


/**
 * Created by v-mechen on 3/1/2017.
 */


public class VerifyTVRBReqestIsSentCorrectly implements IGetReservationVerification {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final String errorMessage = this.verifyTVRBReqMatched(input.getRequest(), verificationContext);

        return VerificationHelper.getVerificationResult(errorMessage, this.getName());
    }



    private String verifyTVRBReqMatched(CarSupplyConnectivityGetReservationRequestType request, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        //Read TVAR request
        final TVRBReq gdsReq = new TVRBReq(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.GETRESERVATIONREQUEST).item(0));

        //Compare PrimaryLangID
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", getExpectedLangID(request), gdsReq.getPrimaryLangID(), errorMsg, new ArrayList<String>());

        //Verify ISOCountry
        CarNodeComparator.isStringNodeEqual("ISOCountry", getExpISOCountry(request), gdsReq.getIsoCountry(), errorMsg, new ArrayList<String>());

        //Compare ReferenceId
        CarNodeComparator.isStringNodeEqual("ReferenceId", getExpectedPNR(request),
                gdsReq.getReferenceId(), errorMsg, new ArrayList<String>());


        return errorMsg.toString().trim();

    }

    /*
    Get expected PrimaryLangID from request
     */
    private String getExpectedLangID(CarSupplyConnectivityGetReservationRequestType request){
        String expPrimaryLangID = null;
        if(null != request.getLanguage() && null !=request.getLanguage().getLanguageCode()){
            expPrimaryLangID = request.getLanguage().getLanguageCode() + "-" + request.getLanguage().getCountryAlpha2Code();
        }
        return expPrimaryLangID;
    }

    /*
    Get expected ISOCountry from request
     */
    private String getExpISOCountry(CarSupplyConnectivityGetReservationRequestType request){
        return request.getPointOfSaleKey().getJurisdictionCountryCode().substring(0,2);
    }

    /*
    Get expected PNR from request
     */
    private String getExpectedPNR(CarSupplyConnectivityGetReservationRequestType request){
        String expPNR = null;
        for(final ReferenceType reference : request.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference())
        {
            if(reference.getReferenceCategoryCode().equals("PNR"))
            {
                expPNR = reference.getReferenceCode() ;
            }
        }
        return expPNR;
    }


}

