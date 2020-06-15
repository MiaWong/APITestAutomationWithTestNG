package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.gdsmapverification;

import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.timetypes.defn.v4.RecurringPeriodType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.CarProductUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.constant.POSConfigSettingName;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 1/8/2017.
 */


public class VerifyTVRRRspIsReturnedCorrectly implements IGetDetailsVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, SQLException, ParserConfigurationException {
        final List remarks = this.verifyTVRRRspReturned((CarSupplyConnectivityGetDetailsRequestType)input.getRequest(),
                    (CarSupplyConnectivityGetDetailsResponseType)input.getResponse(), verificationContext);

        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }



    private List verifyTVRRRspReturned(CarSupplyConnectivityGetDetailsRequestType request, CarSupplyConnectivityGetDetailsResponseType response, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException, SQLException {
        //Get car from TVAR response
        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        final TVARRsp tvarRsp = new TVARRsp(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE).item(0),
                scsDataSource, false, true);
        final CarProductType tvarCar = CarProductUtil.getMatchedCarFromList(tvarRsp.getCarProduct(), request.getCarProductList().getCarProduct().get(0));

        //Parse TVRR response
        final TVRRRsp tvrrRsp = new TVRRRsp(verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.DETAILSRESPONSE).item(0),
                scsDataSource, false, request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
        final CarProductType tvrrCar = tvrrRsp.getCarProduct();

        //Apply values from TVAR car to TVRR car
        //expCar.CarInventoryKey = tvarCar.CarInventoryKey;
        tvrrCar.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(tvarCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());
        tvrrCar.getCarInventoryKey().setSupplySubsetID(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getSupplySubsetID());
        tvrrCar.getCarInventoryKey().setCarRate(tvarCar.getCarInventoryKey().getCarRate());
        tvrrCar.getCarInventoryKey().setPackageBoolean(tvarCar.getCarInventoryKey().getPackageBoolean());
        tvrrCar.getCarInventoryKey().setProductCategoryCodeList(tvarCar.getCarInventoryKey().getProductCategoryCodeList());
        tvrrCar.setAvailStatusCode(tvarCar.getAvailStatusCode());
        tvrrCar.getCarInventoryKey().setCarPickUpDateTime(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarPickUpDateTime());
        tvrrCar.getCarInventoryKey().setCarDropOffDateTime(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarDropOffDateTime());
        tvrrCar.setCarCatalogMakeModel(tvarCar.getCarCatalogMakeModel());
        tvrrCar.setCarDoorCount(tvarCar.getCarDoorCount());

        //Handle policy for CASSS-3371 CMA: Change R/R from Static to Dynamic for Titanium GDS
        //If Rules.setPolicyCategoryToMerchantRules/enable is on, policy except for Arrival should be renamed to merchantRules
        final PosConfigHelper configHelper = new PosConfigHelper(carTitaniumSCSDatasource);
        final boolean featureFlag = configHelper.checkPosConfigFeatureEnable(verificationContext.getScenario(),
                "1", POSConfigSettingName.RULES_SETPOLICYCATEGORYTOMERCHANTRULES_ENABLE);
        if (featureFlag)
        {
            for(final CarPolicyType policy : tvrrCar.getCarPolicyList().getCarPolicy())
            {
                if (!policy.getCarPolicyCategoryCode().equals("Arrival"))
                {
                    policy.setCarPolicyCategoryCode("MerchantRules");
                }
            }
        }


        //Handle TimeRangeList - if it's correct for dropoff date, it should be fine
        handleTimeRangeList(response, tvrrCar);

        //Compare carProduct
        final List remarks = new ArrayList();
        final List<String> ignoreNodeList = new ArrayList<String>();
        ignoreNodeList.add(CarTags.SUPPLY_SUBSET_ID);
        //Get POS config value to see if we need to return CarCatalogMakeModel
        final boolean returnMakeModelBoolean = configHelper.checkPosConfigFeatureEnable(verificationContext.getScenario(), "1", PosConfigSettingName.ENABLEDYNAMICCONTENTFROMGDS_ENABLE);
        if(!returnMakeModelBoolean)
        {
            ignoreNodeList.add(CarTags.CAR_CATALOG_MAKE_MODEL);
        }
        //Get PackageBoolean from request
        tvrrCar.getCarInventoryKey().setPackageBoolean(request.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getPackageBoolean());
        CarProductComparator.isCarProductEqual(tvrrCar, response.getCarProductList().getCarProduct().get(0),
                remarks, ignoreNodeList);

        //Verify PrimaryLangID in GDS response
        CarNodeComparator.isStringNodeEqual("PrimaryLangID", tvarRsp.getPrimaryLangID(), tvrrRsp.getPrimaryLangID(), remarks, new ArrayList<String>());


        //Verify providerID
        if(response.getCarProductList().getCarProduct().get(0).getProviderID() != 7)
        {
            remarks.add("ProviderID should be 7 in TSCS response, actual value: " + response.getCarProductList().getCarProduct().get(0).getProviderID() + "!\n");
        }

        return remarks;
    }

    //Handle TimeRangeList - if it's correct for dropoff date, it should be fine
    private void handleTimeRangeList(CarSupplyConnectivityGetDetailsResponseType response, CarProductType tvrrCar){
        if(null!=response.getCarProductList().getCarProduct().get(0).getCarDropOffLocation()) {
            final List<RecurringPeriodType> periodList = response.getCarProductList().getCarProduct().get(0).getCarDropOffLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod();
            if(periodList.size() == 2)
            {
                periodList.get(0).setTimeRangeList(tvrrCar.getCarDropOffLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getTimeRangeList());
            }
        }

    }


}

