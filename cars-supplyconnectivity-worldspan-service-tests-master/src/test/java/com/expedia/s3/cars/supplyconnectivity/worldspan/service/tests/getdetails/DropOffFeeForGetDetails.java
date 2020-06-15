package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.getdetails;

import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.SuiteContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * Created by fehu on 2/21/2017.
 * CASSS-5155: High drop-off charge is being displayed on MX POS
 */
public class DropOffFeeForGetDetails extends SuiteContext {

    /*
    *when GetDetails.fixDropChargeLegacyFinanceKey/enable is 1, verify  ClassID/SystemId/CalculationID is 18/1/27
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10111OneWayFeeGetDetails() throws Exception {

        TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_MX_Agency_Standalone_OnAirport_oneway.getTestScenario(), "10111", PojoXmlUtil.getRandomGuid());

        getDetailsforDropoffCharge(testData, SettingsProvider.CARWORLDSPANSCSDATASOURCE, true);
    }

    /*
    *when GetDetails.fixDropChargeLegacyFinanceKey/enable is 0, verify  ClassID/SystemId/CalculationID is 3/1/10
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void CASSS10112OneWayFeeGetDetails() throws Exception {

        TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_FR_Agency_Standalone_nonFRLocation_oneway.getTestScenario(), "10112", PojoXmlUtil.getRandomGuid());


        getDetailsforDropoffCharge(testData, SettingsProvider.CARWORLDSPANSCSDATASOURCE, false);
    }

    private void getDetailsforDropoffCharge(TestData testData, DataSource dataSource, boolean enable) throws Exception {
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport, logger, dataSource);

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);

        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);
        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput, spooferTransport,testData.getScenarios() , guid, logger);

        if (enable) {
            verifyEnable(getDetailsVerificationInput);
        } else {
            verifyNotEnable(getDetailsVerificationInput);
        }
    }

    private void verifyEnable(GetDetailsVerificationInput getDetailsVerificationInput) {
        for (CostType cost : getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct().get(0).getCostList().getCost()) {
            if (cost.getDescriptionRawText().equalsIgnoreCase("DROP CHARGE") && !(
                    18l == cost.getLegacyFinanceKey().getLegacyMonetaryClassID() &&
                            1l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() &&
                            27l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID())) {
                Assert.fail("It map wrong ,expect 18, 1, 27 actual is " + cost.getLegacyFinanceKey().getLegacyMonetaryClassID() + ", " +
                        cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() + ", " + cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID());
            }
        }
    }

    private void verifyNotEnable(GetDetailsVerificationInput getDetailsVerificationInput) {
        for (CostType cost : getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct().get(0).getCostList().getCost()) {
            if (cost.getDescriptionRawText().equalsIgnoreCase("DROP CHARGE") && !(
                    3l == cost.getLegacyFinanceKey().getLegacyMonetaryClassID() &&
                            1l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() &&
                            10l == cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID())) {
                Assert.fail("It map wrong ,expect 3, 1, 10 actual is " + cost.getLegacyFinanceKey().getLegacyMonetaryClassID() + ", " +
                        cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() + ", " + cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID());
            }
        }
    }
}
