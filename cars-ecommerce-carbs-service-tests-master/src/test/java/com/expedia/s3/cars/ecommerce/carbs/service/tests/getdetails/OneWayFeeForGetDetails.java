package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by fehu on 2/19/2017.
 * CASSS-5155: High drop-off charge is being displayed on MX POS
 */
@SuppressWarnings("PMD")
public class OneWayFeeForGetDetails extends SuiteCommon{

    private static final String setPosConfigUrl = AppConfig.resolveStringValue("${setPosConfig.server.address}");
    private static  SpooferTransport spooferTransport = null;

    /**
     *  When config both enable,  scs  enable, Verify CarBS is getting dropoff fee from CSDR
     *  set the dropOffcharge  amout US350 in VRUR  template , if the carBSgetDetails response onewayFee is  US350, verify success.
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10101OneWayFeeGetDetails() throws Exception {

        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_MX_Agency_Standalone_OnAirport_oneway.getTestScenario(), "10101", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        //posConfig and client config enable
        checkConfig(testData.getScenarios(), "1", 5, "1");

        dropOffFeeVerify(testData,"0Q7XRN","DropOff_Fee_ForGetdetails", 350);

    }


    /**
     *  When pos enable and client   enable, scs not enable ,and drop charge response in VRUR like
     *  <vehicle:VehicleCharge Category="Surcharge" Name="DROP CHARGE" Type="Rental">     *
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10113OneWayFeePackageGetDetails() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_FR_GDSP_Standalone_nonFRLocation_oneway.getTestScenario(), "10113", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        // pos enable and client  no enable
        checkConfig(testData.getScenarios(), "1", 5, "1");

        dropOffFeeVerify(testData,"0Q7XRN","DropOff_Fee_ForGetdetails", 300);

    }

    /**
     *  When pos enable and client   enable, scs not enable , and drop charge response in VRUR like
     *  <vehicle:VehicleCharge Category="Surcharge" Name="DROP CHARGE" Type="Rental">
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10114OneWayFeePackageGetDetails() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_FR_GDSP_Standalone_nonFRLocation_oneway.getTestScenario(), "10114", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        // pos enable and client  no enable
        checkConfig(testData.getScenarios(), "1", 5, "1");

        dropOffFeeVerify(testData,"0Q7XRN","DropOff_Fee_WithTypeRental", 350);

    }

    /**
     *  When pos enable and client  no enable, scs config enable,Verify CarBS is getting dropoff fee from CSAR
     *  our costandavail template drop-off fee is USD300 , if the carBSgetDetails response onewayFee is  US300, verify success.
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10103OneWayFeePackageGetDetails() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_MX_Agency_Standalone_OnAirport_oneway.getTestScenario(), "10103", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        // pos enable and client  no enable
        checkConfig(testData.getScenarios(), "1", 3, "0");

        dropOffFeeVerify(testData,"W0DFCJ","DropOff_Fee_ForGetdetails", 300);

    }


    /**
     *  When pos no enable and client enable, scs Config enable , CarBS is getting dropoff fee from CSAR
     *  our costandavail template drop-off fee is USD300 , if the carBSgetDetails response onewayFee is  US300, verify success.
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10105OneWayFeePackageGetDetails() throws Exception {
        spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_GBR_GDSP_Standalone_OnAirport_oneway.getTestScenario(), "10105", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        // pos no enable and client enable
        checkConfig(testData.getScenarios(),"0", 5, "1");

        dropOffFeeVerify(testData,"0Q7XRN","DropOff_Fee_ForGetdetails", 300);

    }

    private void dropOffFeeVerify(TestData testData, String clientCode, String scenarioNameForSpoofer, long expectAmout) throws DataAccessException, IOException {
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        request.setClientCode(clientCode);
        final  CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(),httpClient,request);
        //verification
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request,response, testData);
        final   CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        getDetailsRequestType.setClientCode(clientCode);

        final  String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, scenarioNameForSpoofer);
        final  CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid,httpClient,getDetailsRequestType);
        //verification
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);
        final IVerification.VerificationResult result = verifyResult(getDetailsResponseType,expectAmout);
        if(!result.isPassed())
        {
            Assert.fail(result.toString());
        }
    }

    private void checkConfig(TestScenario scenario, String expectPosValue, int expectClientCode, String expectClientValue) throws com.expedia.s3.cars.framework.core.dataaccess.DataAccessException, SQLException {
        final  ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());
        if(!clientConfigHelper.checkClientConfig(PojoXmlUtil.getEnvironment(), PosConfigSettingName.GETDETAILSDROPCHARGE_SETTINGNAME_CLIENT, expectClientCode, expectClientValue))
        {
            Assert.fail("please set clientConfig GetDetails.useGetDetailsDropCharge/enable " + expectClientValue);
        }

        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(),setPosConfigUrl);
        if(!posConfigHelper.checkPosConfigFeatureEnable(scenario, expectPosValue, PosConfigSettingName.GETDETAILSDROPCHARGE_SETTINGNAME_POS, false))
        {
            Assert.fail("please set posConfig GetDetails.useGetDetailsDropCharge/enable " + expectPosValue);
        }

    }

    private IVerification.VerificationResult verifyResult(CarECommerceGetDetailsResponseType getDetailsResponseType,  double expectAmout) {
        for (CostType cost : getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCostList().getCost())
        {
            final  String currencyCode = cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
            final long decimalPlaceCount = cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
            final double amountValue =cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / Math.pow(10, decimalPlaceCount);

            if (18l == cost.getLegacyFinanceKey().getLegacyMonetaryClassID() &&
                    1l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() &&
                    27l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID() &&cost.getDescriptionRawText().contains("DROP CHARGE") && expectAmout == amountValue
                    && "USD".equalsIgnoreCase(currencyCode))
            {
                return  new IVerification.VerificationResult("DROP CHARGE verify", true, Arrays.asList("success"));

            }
            else
            if (3l == cost.getLegacyFinanceKey().getLegacyMonetaryClassID() &&
                    1l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() &&
                    10l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID() && cost.getDescriptionRawText().contains("DROP CHARGE") && expectAmout == amountValue
                    && "USD".equalsIgnoreCase(currencyCode))
            {
                return  new IVerification.VerificationResult("DROP CHARGE verify", true, Arrays.asList("success"));

            }

        }
        return new IVerification.VerificationResult("DROP CHARGE verify", false, Arrays.asList("there is no drop charge in CarBSgetDetails response"));

    }

}
