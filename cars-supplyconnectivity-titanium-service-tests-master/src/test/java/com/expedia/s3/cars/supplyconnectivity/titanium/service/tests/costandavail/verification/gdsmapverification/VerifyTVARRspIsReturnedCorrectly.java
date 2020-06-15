package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.gdsmapverification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.utils.CarProductUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 1/8/2017.
 */


public class VerifyTVARRspIsReturnedCorrectly implements IGetCostAndAvailabilityVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, SQLException, ParserConfigurationException {
        final List remarks = this.verifyTVARRspReturned(input.getRequest(),
                    input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }



    private List verifyTVARRspReturned(CarSupplyConnectivityGetCostAndAvailabilityRequestType request,
                                       CarSupplyConnectivityGetCostAndAvailabilityResponseType response, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException, SQLException {
        //Get car from TVAR response
        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        final TVARRsp tvarRsp = new TVARRsp(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE).item(0),
                scsDataSource, false, false);
        final CarProductType tvarCar = CarProductUtil.getMatchedCarFromList(tvarRsp.getCarProduct(), request.getCarProductList().getCarProduct().get(0));


        //Compare carProduct
        final List remarks = new ArrayList();
        final List<String> ignoreNodeList = new ArrayList<String>();
        ignoreNodeList.add(CarTags.SUPPLY_SUBSET_ID);
        //Get POS config value to see if we need to return CarCatalogMakeModel
        final PosConfigHelper configHelper = new PosConfigHelper(carTitaniumSCSDatasource);
        final boolean returnMakeModelBoolean = configHelper.checkPosConfigFeatureEnable(verificationContext.getScenario(), "1", PosConfigSettingName.ENABLEDYNAMICCONTENTFROMGDS_ENABLE);
        if(!returnMakeModelBoolean)
        {
            ignoreNodeList.add(CarTags.CAR_CATALOG_MAKE_MODEL);
        }
        //Get PackageBoolean from request
        tvarCar.getCarInventoryKey().setPackageBoolean(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean());
        CarProductComparator.isCarProductEqual(tvarCar, response.getCarProductList().getCarProduct().get(0),
                remarks, ignoreNodeList);

        //Verify PrimaryLangID in GDS response
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", tvarRsp.getPrimaryLangID(), tvarRsp.getPrimaryLangID(), remarks, new ArrayList<String>());


        //Verify providerID
        if(response.getCarProductList().getCarProduct().get(0).getProviderID() != 7)
        {
            remarks.add("ProviderID should be 7 in TSCS response, actual value: " + response.getCarProductList().getCarProduct().get(0).getProviderID() + "!\n");
        }

        return remarks;
    }



}

