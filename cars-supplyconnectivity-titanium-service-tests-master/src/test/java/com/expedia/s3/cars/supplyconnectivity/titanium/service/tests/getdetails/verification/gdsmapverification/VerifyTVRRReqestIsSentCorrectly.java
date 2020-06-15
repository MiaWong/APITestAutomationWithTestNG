package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.gdsmapverification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRRReq;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

/**
 * Created by v-mechen on 1/8/2017.
 */


public class VerifyTVRRReqestIsSentCorrectly implements IGetDetailsVerification {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final String errorMessage = this.verifyTVRRReqMatched((CarSupplyConnectivityGetDetailsRequestType)input.getRequest(), verificationContext);

        return VerificationHelper.getVerificationResult(errorMessage, this.getName());
    }



    private String verifyTVRRReqMatched(CarSupplyConnectivityGetDetailsRequestType request, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        //Read TVRR request
        //System.out.println(PojoXmlUtil.toString(verificationContext.getSpooferTransactions()));
        final TVRRReq tvarrReq = new TVRRReq(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.DETAILSREQUEST).item(0));

        //Compare PrimaryLangID
        final String expPrimaryLangID = getExpectedLangID(request);
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", expPrimaryLangID, tvarrReq.getPrimaryLangID(), errorMsg, new ArrayList<String>());

        //Verify ISOCountry
        CarNodeComparator.isStringNodeEqual("ISOCountry", getExpISOCountry(request), tvarrReq.getIsoCountry(), errorMsg, new ArrayList<String>());

        //Compare ReferenceId
        CarNodeComparator.isStringNodeEqual("ReferenceId", request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getCarRateQualifierCode(),
                tvarrReq.getReferenceId(), errorMsg, new ArrayList<String>());

        return errorMsg.toString().trim();

    }

    /*
    Get expected PrimaryLangID from request
     */
    private String getExpectedLangID(CarSupplyConnectivityGetDetailsRequestType request){
        String expPrimaryLangID = null;
        if(null != request.getLanguage() && null !=request.getLanguage().getLanguageCode()){
            expPrimaryLangID = request.getLanguage().getLanguageCode() + "-" + request.getLanguage().getCountryAlpha2Code();
        }
        return expPrimaryLangID;
    }

    /*
    Get expected ISOCountry from request
     */
    private String getExpISOCountry(CarSupplyConnectivityGetDetailsRequestType request){
        return request.getPointOfSaleKey().getJurisdictionCountryCode().substring(0,2);
    }


}

