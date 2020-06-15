package com.expedia.s3.cars.supply.service.tests.regression.getdetails.verification;


import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
//import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
//import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supply.messages.getdetails.defn.v4.CarSupplyGetDetailsResponseType;
//import com.expedia.s3.cars.supply.service.common.SettingsProvider;
//import com.expedia.s3.cars.supply.service.constant.POSConfigSettingName;
import org.w3c.dom.NodeList;
//import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by v-mechen on 1/16/2017.
 */
public class DynamicCarPolicyVerification implements IVerification
{
    //final private DataSource ssDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_SS_DATABASE_SERVER, SettingsProvider.DB_CAR_SS_DATABASE_NAME,
        //    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }

    public VerificationResult verifyCarpolicy(CarSupplyGetDetailsResponseType response,
                                              boolean gdsHasMerchantRules,
                                              BasicVerificationContext verificationContext,
                                              boolean featureFlag) throws DataAccessException, SQLException {

        final ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        //Get carPolicy in GDS response
        final NodeList tvrrs = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "OTA_VehRateRuleRS");
        final NodeList vrurs = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "VehicleRulesRsp");
        if(tvrrs.getLength() == 0 && vrurs.getLength() == 0) {
            remarks.add("No OTA_VehRateRuleRS or VehicleRulesRsp found from SpooferTransactions!");
            return new VerificationResult(this.getName(), isPassed, remarks);
        }
        else{
            final String gdsRsp = tvrrs.getLength() > 0 ? PojoXmlUtil.toString(tvrrs.item(0)) : PojoXmlUtil.toString(vrurs.item(0));
            //Do verify
            isSSMerchantRulesCorrect(response, gdsRsp, verificationContext.getScenario(), remarks,
                    gdsHasMerchantRules, featureFlag);

        }

        if (!remarks.isEmpty()) {
            return new VerificationResult("CarPolicy - MerchantRules verify", isPassed, remarks);
        }

        if (remarks.isEmpty()) {
            isPassed = true;
        }
        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    //Verify merchantRules in ss response is correct or not
    public void isSSMerchantRulesCorrect(CarSupplyGetDetailsResponseType response, String gdsRsp,
                                         TestScenario scenario, List remarks, boolean gdsHasMerchantRules,
                                         boolean featureFlag) throws DataAccessException, SQLException {


        //final PosConfigHelper configHelper = new PosConfigHelper(ssDatasource, null, "stt05");
        //get config value
        //final boolean featureFlag = configHelper.checkPosConfigFeatureEnable(scenario, "1", POSConfigSettingName.GETDETAILS_USEMERCHANTRULESFROMDETAILSRESPONSE_ENABLE);
        //final String supportedSCSIDs = configHelper.getPosConfigSettingValue(scenario, POSConfigSettingName.GETDETAILS_USEMERCHANTRULESFROMDETAILSRESPONSE_SUPPORTEDSCSIDS);

        //See if merchantRules should from GDS
        final CarProductType ssCar = response.getCarProductList().getCarProduct().get(0);
        final boolean shouldFromGDS = shouldGetMerchantRulesFromGDS(ssCar, featureFlag, "7");

        //When GDS has no merchant rules, carss should get from ExpediaSCS
        final List<String> ssMerchantRules = getMerchantRules(ssCar);
        if(!gdsHasMerchantRules && ssMerchantRules.isEmpty()){
            remarks.add("There is no MerchantRules in CarSS response when GDS MerchantRules is missing!");
        }

        //Verify ss response is from GDS when it should be
        if(gdsHasMerchantRules && shouldFromGDS && !isSSMerchantRulesFromGDS(ssMerchantRules, gdsRsp)){
            remarks.add("MerchantRules is not from GDS!");
        }
        if(gdsHasMerchantRules && !shouldFromGDS && isSSMerchantRulesFromGDS(ssMerchantRules, gdsRsp)){
            remarks.add("MerchantRules shouldn't from GDS!");
        }

    }

    //Verify if merchantRules are from GDS
    public boolean isSSMerchantRulesFromGDS(List<String> ssMerchantRules, String gdsRsp) {
        boolean isFromGDS = true;

        //See if merchant rules are from GDS
        for(final String ssMerchantRule : ssMerchantRules){
            if(!gdsRsp.contains(ssMerchantRule)){
                isFromGDS = false;
                break;
            }
        }

        return isFromGDS;
    }


    //Get merchant rules from CarPorduct
    public List<String> getMerchantRules(CarProductType car){
        final List<String> merchantRules = new ArrayList<String>();
        for(final CarPolicyType policy : car.getCarPolicyList().getCarPolicy()){
            if(policy.getCarPolicyCategoryCode().equals("MerchantRules")){
                merchantRules.add(policy.getCarPolicyRawText().replace("<", "&lt;").replace(">", "&gt;")); //We need replace is because "&lt;" is format to '<', "&gt;" is updated to'>' after getCarPolicyRawText //&lt;h2
            }
        }
        return merchantRules;
    }

    //Method to get if we should get merchant rules from GDS
    public boolean shouldGetMerchantRulesFromGDS(CarProductType car, boolean featureFlag, String supportedSCSIDs)
    {
        boolean shouldFromGDS = false;
        final String supportedSCSIDString = supportedSCSIDs + ",";
        //Need turn on feature flag and SCSID is supported
        if(featureFlag && supportedSCSIDString.contains(car.getProviderID()+",")){
            shouldFromGDS = true;
        }
        return shouldFromGDS;
    }


}
