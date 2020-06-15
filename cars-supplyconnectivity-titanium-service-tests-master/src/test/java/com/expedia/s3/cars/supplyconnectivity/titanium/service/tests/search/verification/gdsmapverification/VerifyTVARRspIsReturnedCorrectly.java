package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.gdsmapverification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyTVARRspIsReturnedCorrectly implements ISearchVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, SQLException, ParserConfigurationException {
        final List remarks = this.verifyTVARRspReturned((CarSupplyConnectivitySearchRequestType)input.getRequest(),
                    (CarSupplyConnectivitySearchResponseType)input.getResponse(), verificationContext);


        return VerificationHelper.getVerificationResult(remarks, this.getName());
    }



    private List verifyTVARRspReturned(CarSupplyConnectivitySearchRequestType request, CarSupplyConnectivitySearchResponseType response, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException, SQLException {
        //Get GDS response list from spoofer
        final NodeList gdsRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE);
        //Compare carProductList by Sequence
        final List remarks = new ArrayList();
        for(final CarSearchResultType carSearchResult : response.getCarSearchResultList().getCarSearchResult())
        {
            //Get matched TVAR response by sequence
            final TVARRsp tvarRsp = getMatchedTVARRsp(carSearchResult, gdsRspNodeList);
            //compare response CarProductList
            final List<String> ignoreNodeList = new ArrayList<String>();
            ignoreNodeList.add(CarTags.SUPPLY_SUBSET_ID);
            if(tvarRsp == null && (carSearchResult.getCarProductList() == null || carSearchResult.getCarProductList().getCarProduct() == null ||
            carSearchResult.getCarProductList().getCarProduct().isEmpty()))
            {
                continue;
            }
            //Get POS config value to see if we need to return CarCatalogMakeModel
            final PosConfigHelper configHelper = new PosConfigHelper(carTitaniumSCSDatasource, SettingsProvider.SERVICE_ADDRESS);
            final boolean returnMakeModelBoolean = configHelper.checkPosConfigFeatureEnable(verificationContext.getScenario(), "1", PosConfigSettingName.ENABLEDYNAMICCONTENTFROMGDS_ENABLE);
            if(!returnMakeModelBoolean)
            {
                ignoreNodeList.add(CarTags.CAR_CATALOG_MAKE_MODEL);
            }

            //Get expected packageBoolean from request
            final Boolean expPackageBoolean = getPackageBooleanFromRequest(request, carSearchResult);
            for(final CarProductType tvarCar : tvarRsp.getCarProduct().getCarProduct())
            {
                tvarCar.getCarInventoryKey().setPackageBoolean(expPackageBoolean);
            }
            CarProductComparator.isCarProductListEqual(tvarRsp.getCarProduct().getCarProduct(), carSearchResult.getCarProductList().getCarProduct(),
                    7, remarks, ignoreNodeList);

            //Verify PrimaryLangID in GDS response
            final String expPrimaryLangID = getExpectedLangID(request);
            CarNodeComparator.isStringNodeEqual("PrimaryLangID", expPrimaryLangID.toLowerCase(Locale.US), tvarRsp.getPrimaryLangID().toLowerCase(Locale.US), remarks, new ArrayList<String>());


        }


        return remarks;
    }

    /*
    Get expected PrimaryLangID from request
     */
    private String getExpectedLangID(CarSupplyConnectivitySearchRequestType request){
        String expPrimaryLangID = null;
        if(null != request.getLanguage() && null !=request.getLanguage().getLanguageCode()){
            expPrimaryLangID = request.getLanguage().getLanguageCode() + "-" + request.getLanguage().getCountryAlpha2Code();
        }
        return expPrimaryLangID;
    }

    //Get matched TVAR response by sequence
    private TVARRsp getMatchedTVARRsp(CarSearchResultType carSearchResult, NodeList gdsRspNodeList) throws DataAccessException {

        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        for(int i= 0; i< gdsRspNodeList.getLength(); i++) {
            //matchedRsp.getAttributes().getNamedItem("Sequence")
            if(carSearchResult.getSequence() == Long.parseLong(gdsRspNodeList.item(i).getAttributes().getNamedItem("Sequence").getTextContent()))
            {
                return new TVARRsp(gdsRspNodeList.item(i), scsDataSource, false, false);
            }
        }
        return null;
    }

    //Get PackageBoolean From Request
    private Boolean getPackageBooleanFromRequest(CarSupplyConnectivitySearchRequestType request, CarSearchResultType carSearchResult) throws DataAccessException {

        for(final CarSearchCriteriaType carSearchCriteria : request.getCarSearchCriteriaList().getCarSearchCriteria()) {
            //matchedRsp.getAttributes().getNamedItem("Sequence")
            if(carSearchResult.getSequence() == carSearchCriteria.getSequence())
            {
                return carSearchCriteria.getPackageBoolean();
            }
        }
        return null;
    }

}

