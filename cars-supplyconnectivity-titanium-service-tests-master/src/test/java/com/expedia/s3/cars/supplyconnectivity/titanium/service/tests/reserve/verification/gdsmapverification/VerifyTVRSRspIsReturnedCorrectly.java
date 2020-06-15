package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.gdsmapverification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRSRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 1/8/2017.
 */


public class VerifyTVRSRspIsReturnedCorrectly implements IReserveVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, SQLException, ParserConfigurationException {
        final List remarks = this.verifyTVRSRspReturned(input. getRequest(), input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }



    private List verifyTVRSRspReturned(CarSupplyConnectivityReserveRequestType request, CarSupplyConnectivityReserveResponseType response, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException, SQLException {
        //Get car from GDS response
        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        final TVRSRsp gdsRsp = new TVRSRsp(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.RESERVERESPONSE).item(0),
                scsDataSource, false);

        //Compare carProduct
        final List remarks = new ArrayList();
        final List<String> ignoreNodeList = new ArrayList<String>();
        ignoreNodeList.add(CarTags.SUPPLY_SUBSET_ID);
        ignoreNodeList.add(CarTags.CAR_RATE_QUALIFIER_CODE);
        //Get POS config value to see if we need to return CarCatalogMakeModel
        final PosConfigHelper configHelper = new PosConfigHelper(carTitaniumSCSDatasource);
        final boolean returnMakeModelBoolean = configHelper.checkPosConfigFeatureEnable(verificationContext.getScenario(), "1", PosConfigSettingName.ENABLEDYNAMICCONTENTFROMGDS_ENABLE);
        if(!returnMakeModelBoolean)
        {
            ignoreNodeList.add(CarTags.CAR_CATALOG_MAKE_MODEL);
        }
        //Get PackageBoolean from request
        gdsRsp.getCarProduct().getCarInventoryKey().setPackageBoolean(request.getCarProduct().getCarInventoryKey().getPackageBoolean());
        CarProductComparator.isCarProductEqual(gdsRsp.getCarProduct(), response.getCarReservation().getCarProduct(),
                remarks, ignoreNodeList);

        //Verify PrimaryLangID in GDS response
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", gdsRsp.getPrimaryLangID(), gdsRsp.getPrimaryLangID(), remarks, new ArrayList<String>());

        //Verify providerID
        if(response.getCarReservation().getCarProduct().getProviderID() != 7)
        {
            remarks.add("ProviderID should be 7 in TSCS response, actual value: " + response.getCarReservation().getCarProduct().getProviderID() + "!\n");
        }

        //Verify response: travelerList
        final StringBuilder errorMsgBuilderTemp = new StringBuilder();
        CompareUtil.compareObject(gdsRsp.getTravelerList(), response.getCarReservation().getTravelerList(),  new ArrayList<>(), errorMsgBuilderTemp);
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\nTravelerList is not expected in SCS reserve response : " + errorMsgBuilderTemp.toString());
            /*remarks.add("\nExpect:" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(gdsRsp.getTravelerList())));
            remarks.add("\nActual:" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response.getCarReservation().getTravelerList())));*/
        }

        //Verify response: referenceList
        CompareUtil.compareObject(gdsRsp.getRefereceList(), response.getCarReservation().getReferenceList()
                ,  new ArrayList<>(), errorMsgBuilderTemp);
        if (!org.apache.commons.lang.StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\nRefereceList is not expected in SCS reserve response : " + errorMsgBuilderTemp.toString());
            remarks.add("\nExpect:" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(gdsRsp.getRefereceList())));
            remarks.add("\nActual:" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response.getCarReservation().getReferenceList())));
        }

        //Verify response: bookStatusCode
        CarNodeComparator.isStringNodeEqual("BookingStateCode", gdsRsp.getBookStatusCode(), response.getCarReservation().getBookingStateCode(), remarks, new ArrayList<String>());


        return remarks;
    }



}

