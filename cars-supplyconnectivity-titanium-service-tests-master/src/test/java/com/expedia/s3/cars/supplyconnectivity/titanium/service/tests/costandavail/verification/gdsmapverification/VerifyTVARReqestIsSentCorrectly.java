package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.gdsmapverification;

import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARReq;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarsInventoryKeyComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

/**
 * Created by v-mechen on 1/8/2017.
 */


public class VerifyTVARReqestIsSentCorrectly implements IGetCostAndAvailabilityVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final String errorMessage = this.verifyTVARReqMatched(input.getRequest(), verificationContext);

        return VerificationHelper.getVerificationResult(errorMessage, this.getName());
    }



    private String verifyTVARReqMatched(CarSupplyConnectivityGetCostAndAvailabilityRequestType request, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        //Read TVAR request
        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        //System.out.println(PojoXmlUtil.toString(verificationContext.getSpooferTransactions()));
        final TVARReq tvarrReq = new TVARReq(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST).item(0), scsDataSource);

        //Compare PrimaryLangID
        final String expPrimaryLangID = getExpectedLangID(request);
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", expPrimaryLangID, tvarrReq.getPrimaryLangID(), errorMsg, new ArrayList<String>());

        //Verify ISOCountry
        CarNodeComparator.isStringNodeEqual("ISOCountry", getExpISOCountry(request), tvarrReq.getIsoCountry(), errorMsg, new ArrayList<String>());

        //Compare CarsInventoryKeyComparator
        //final List remarks = new ArrayList();
        final List<String> ignoreNodeList = new ArrayList<String>();
        ignoreNodeList.add(CarTags.SUPPLY_SUBSET_ID);
        ignoreNodeList.add(CarTags.RATE_PERIOD_CODE);
        ignoreNodeList.add(CarTags.PACKAGEBOOLEAN);
        //No need to compare CatalogKey -> TVAR doesn't send CatalogKey, so set CatalogKey from SCS request
        //Same way to CarRateQualifierCode and RateCategoryCode
        tvarrReq.getCarCarProduct().getCarInventoryKey().getCarCatalogKey().setCarVehicle(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarVehicle());
        tvarrReq.getCarCarProduct().getCarInventoryKey().setCarRate(new CarRateType());
        tvarrReq.getCarCarProduct().getCarInventoryKey().getCarRate().setRateCategoryCode(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getRateCategoryCode());
        tvarrReq.getCarCarProduct().getCarInventoryKey().getCarRate().setCarRateQualifierCode(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getCarRateQualifierCode());
        CarsInventoryKeyComparator.isCarInventoryKeyEqual(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey(),
                tvarrReq.getCarCarProduct().getCarInventoryKey(),
                 errorMsg, ignoreNodeList);
        /*CarProductComparator.isCarProductEqual(tvarrReq.getCarCarProduct(), request.getCarProductList().getCarProduct().get(0),
                remarks, ignoreNodeList);*/

        return errorMsg.toString().trim();

    }

    /*
    Get expected PrimaryLangID from request
     */
    private String getExpectedLangID(CarSupplyConnectivityGetCostAndAvailabilityRequestType request){
        String expPrimaryLangID = null;
        if(null != request.getLanguage() && null !=request.getLanguage().getLanguageCode()){
            expPrimaryLangID = request.getLanguage().getLanguageCode() + "-" + request.getLanguage().getCountryAlpha2Code();
        }
        return expPrimaryLangID;
    }

    /*
    Get expected ISOCountry from request
     */
    private String getExpISOCountry(CarSupplyConnectivityGetCostAndAvailabilityRequestType request){
        return request.getPointOfSaleKey().getJurisdictionCountryCode().substring(0,2);
    }


}

