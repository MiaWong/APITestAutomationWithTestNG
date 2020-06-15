package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.gdsmapverification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRSReq;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;


/**
 * Created by v-mechen on 3/1/2017.
 */


public class VerifyTVRSReqestIsSentCorrectly implements IReserveVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final String errorMessage = this.verifyTVRSReqMatched(input.getRequest(), verificationContext);
        return VerificationHelper.getVerificationResult(errorMessage, this.getName());
    }



    private String verifyTVRSReqMatched(CarSupplyConnectivityReserveRequestType request, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        //Read TVAR request
        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        //System.out.println(PojoXmlUtil.toString(verificationContext.getSpooferTransactions()));
        final TVRSReq gdsReq = new TVRSReq(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.RESERVEREQUEST).item(0), scsDataSource);

        //Compare PrimaryLangID
        final String expPrimaryLangID = getExpectedLangID(request);
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", expPrimaryLangID, gdsReq.getPrimaryLangID(), errorMsg, new ArrayList<String>());

        //Verify ISOCountry
        CarNodeComparator.isStringNodeEqual("ISOCountry", getExpISOCountry(request), gdsReq.getIsoCountry(), errorMsg, new ArrayList<String>());

        //Compare ReferenceId
        CarNodeComparator.isStringNodeEqual("ReferenceId", request.getCarProduct().getCarInventoryKey().getCarRate().getCarRateQualifierCode(),
                gdsReq.getReferenceId(), errorMsg, new ArrayList<String>());

        //Verify request: Traveler - no need seconde address line in TVRS request
        if(null!=request.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).getSecondAddressLine())
        {
            request.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).setFirstAddressLine(
                    request.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).getFirstAddressLine() + " " +
                            request.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).getSecondAddressLine());
        }
        request.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).setSecondAddressLine(null);
        //No Age in TVRS request
        gdsReq.getTravelerList().getTraveler().get(0).getPerson().setAge(request.getTravelerList().getTraveler().get(0).getPerson().getAge());
        //GDS user name/email is upper case
        request.getTravelerList().getTraveler().get(0).getPerson().getPersonName().setLastName(request.getTravelerList().getTraveler().get(0).getPerson().getPersonName().getLastName().toUpperCase());
        request.getTravelerList().getTraveler().get(0).getContactInformation().getEmailAddressEntryList().getEmailAddressEntry().get(0).setEmailAddress(request.getTravelerList().getTraveler().get(0).getContactInformation().getEmailAddressEntryList().getEmailAddressEntry().get(0).getEmailAddress().toUpperCase());
        //Conbine phone number
        request.getTravelerList().getTraveler().get(0).getContactInformation().getPhoneList().getPhone().get(0).setPhoneNumber(request.getTravelerList().getTraveler().get(0).getContactInformation().getPhoneList().getPhone().get(0).getPhoneCountryCode()
                + "-" + request.getTravelerList().getTraveler().get(0).getContactInformation().getPhoneList().getPhone().get(0).getPhoneAreaCode()
                + "-" + request.getTravelerList().getTraveler().get(0).getContactInformation().getPhoneList().getPhone().get(0).getPhoneNumber());
        request.getTravelerList().getTraveler().get(0).getContactInformation().getPhoneList().getPhone().get(0).setPhoneAreaCode(null);
        request.getTravelerList().getTraveler().get(0).getContactInformation().getPhoneList().getPhone().get(0).setPhoneCountryCode(null);
        //Compare
        StringBuilder errorMsgBuilderTemp = new StringBuilder();
        CompareUtil.compareObject(request.getTravelerList(), gdsReq.getTravelerList(), new ArrayList<>(), errorMsgBuilderTemp);
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            errorMsg.append("\nTravelerList is not expected in TVRS request : ").append(errorMsgBuilderTemp.toString());
        }

        //Verify request: AirFlight
        errorMsgBuilderTemp = new StringBuilder();
        CompareUtil.compareObject(request.getAirFlight(), gdsReq.getAirFlight(), new ArrayList<>(), errorMsgBuilderTemp);
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            errorMsg.append("\ntAirFlight is not expected in TVRS request : ").append(errorMsgBuilderTemp.toString());
        }

        //Verify request: SepcialEquipmentList
        errorMsgBuilderTemp = new StringBuilder();
        CompareUtil.compareObject(request.getCarSpecialEquipmentList(), gdsReq.getCarSpecialEquipmentList(), new ArrayList<>(), errorMsgBuilderTemp);
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            errorMsg.append("\nCarSpecialEquipmentList is not expected in TVRS request : ").append(errorMsgBuilderTemp.toString());
        }



        return errorMsg.toString().trim();

    }

    /*
    Get expected PrimaryLangID from request
     */
    private String getExpectedLangID(CarSupplyConnectivityReserveRequestType request){
        String expPrimaryLangID = null;
        if(null != request.getLanguage() && null !=request.getLanguage().getLanguageCode()){
            expPrimaryLangID = request.getLanguage().getLanguageCode() + "-" + request.getLanguage().getCountryAlpha2Code();
        }
        return expPrimaryLangID;
    }

    /*
    Get expected ISOCountry from request
     */
    private String getExpISOCountry(CarSupplyConnectivityReserveRequestType request){
        return request.getPointOfSaleKey().getJurisdictionCountryCode().substring(0,2);
    }


}

