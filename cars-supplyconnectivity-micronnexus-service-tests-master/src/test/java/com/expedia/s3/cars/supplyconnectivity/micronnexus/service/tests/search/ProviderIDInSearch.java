package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fehu on 10/11/2016.
 */
public class ProviderIDInSearch extends SuiteCommon{
    //MN serviceProvider
    final private static Long MNPROVIDERID = 3L;
    Logger logger = Logger.getLogger(getClass());

    //CASSS-604 - Verify ProviderID is added to CarProduct in SCS Response
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss_1082594_testMSCS_ProvideId_Search_StandaloneOnAirport() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        testCarMSCSSearchForProvideId(CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "1082594");
    }

    //Basic test method
    public void testCarMSCSSearchForProvideId(TestScenario scenarios, String tuid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        final StringBuilder errorMsg = new StringBuilder("");

        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        TestData testData = new TestData(httpClient, scenarios, tuid, guid, spooferTransport);
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport, logger, SettingsProvider.CARMNSCSDATASOURCE);

        for (CarSearchResultType resultType : searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult()) {
            if (!CollectionUtils.isEmpty(resultType.getCarProductList().getCarProduct())) {

                for (CarProductType carProductType : resultType.getCarProductList().getCarProduct()) {
                    if (!MNPROVIDERID.equals(carProductType.getProviderID())) {
                        errorMsg.append("\nCarSCS providerID is wrong, Expected: 3 Actual: " + String.valueOf(carProductType.getProviderID())
                                + "in car[" + String.valueOf(carProductType.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID())
                                + ":" + String.valueOf(carProductType.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode())
                                + "-" + String.valueOf(carProductType.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode())
                                + "-" + String.valueOf(carProductType.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode())
                                + "-" + String.valueOf(carProductType.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()) + "]");
                    }

                }

            }
        }
        if (!StringUtils.isEmpty(errorMsg.toString())) {
            Assert.fail(errorMsg.toString());
        }
    }
}
