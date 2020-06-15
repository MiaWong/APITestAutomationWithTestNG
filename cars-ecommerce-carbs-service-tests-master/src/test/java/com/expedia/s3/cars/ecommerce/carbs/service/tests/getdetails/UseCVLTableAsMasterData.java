package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VRURRes;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by fehu on 9/3/2018.
 * Test plan for user story 402517 - Return CarShuttleCatagoryCode in CarProduct,
 * for GetReservation, by looking up in the Car Vendor Location table.
 * client Config: PopulateCarVendorLocationInfo.useCVLTableAsMasterData/enable
 */
public class UseCVLTableAsMasterData  extends SuiteCommon{

    private final Logger logger = Logger.getLogger(UseCVLTableAsMasterData.class);

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs428126WorldSpaneCarBSGetDetailsfromGDSUSclienID1enableON() throws Exception
    {
        testCarbsGetdetails(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "428126", "1FX936", true, false);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs42131WorldSpaneCarBSGetDetailsfromGDSUSclienID1enableON() throws Exception
    {
        testCarbsGetdetails(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "42131", "1FX936", true, true);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs428136WorldSpaneCarBSGetDetailsfromGDSUSclienID2enableOFF() throws Exception
    {
        testCarbsGetdetails(CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "428136", "QGPDJ8", false, false );

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs428131WorldSpaneCarBSGetDetailsfromGDSUSclienID2enableOFF() throws Exception
    {
        testCarbsGetdetails(CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "428131", "QGPDJ8", false, true);

    }
    private void testCarbsGetdetails(TestScenario scenarios, String tuid, String clientCode, boolean useCVL, boolean returnEmptyCarShuttleCategoryCode) throws Exception {


        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);

        //search
        final CarbsRequestGenerator carbsSearchRequestGenerator = ExecutionHelper.executeSearch(testData,spooferTransport,DatasourceHelper.getCarInventoryDatasource(), logger);

        //getdetails
        final CarECommerceGetDetailsRequestType getDetailsRequestType = getCarECommerceGetDetailsRequestType(clientCode, returnEmptyCarShuttleCategoryCode, carbsSearchRequestGenerator);
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), httpClient,getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), scenarios, getDetailsRequestType, getDetailsResponseType);

        useCvlDataVerify(useCVL, returnEmptyCarShuttleCategoryCode, spooferTransport, testData, getDetailsRequestType, getDetailsResponseType);
    }

    private void useCvlDataVerify(boolean useCVL, boolean returnEmptyCarShuttleCategoryCode, SpooferTransport spooferTransport, TestData testData, CarECommerceGetDetailsRequestType getDetailsRequestType, CarECommerceGetDetailsResponseType getDetailsResponseType) throws DataAccessException, IOException
    {
        final String carShuttleCategoryCode_carbs = getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarPickupLocation().getCarShuttleCategoryCode();
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

        final String carShuttleCategoryCode_cvl = carsInventoryHelper.getCarShuttleCategoryCode(getDetailsResponseType.getCarProductList().getCarProduct()
        .get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID());

        final Document document = spooferTransport.retrieveRecords(testData.getGuid());
        final VRURRes tvrrRsp = new VRURRes(document.getElementsByTagNameNS("*", GDSMsgNodeTags.WorldSpanNodeTags.VRUR_RESPONSE_TYPE).item(0),
                getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarInventoryKey(),
                DatasourceHelper.getWorldspanSCSDatasource(), DatasourceHelper.getCarInventoryDatasource(),false);
        final String carShuttleCategoryCode_gds = tvrrRsp.getCarProduct().getCarPickupLocation().getCarShuttleCategoryCode();

        //feature on(ClientID=1) GDS return CarShuttleCategoryCode(GDS value should be used)
        if (useCVL && returnEmptyCarShuttleCategoryCode)
        {
            if (!carShuttleCategoryCode_carbs.equals(carShuttleCategoryCode_cvl))
            {
                Assert.fail("when config enable and GDS return null CarShuttleCategoryCode , carbs should use CVL CarShuttleCategoryCode");
            }
        }

        //feature off(ClientID except 1,5) GDS return null CarShuttleCategoryCode(null value should be used)
        else if (!useCVL && returnEmptyCarShuttleCategoryCode)
        {
            if (StringUtil.isNotBlank(carShuttleCategoryCode_carbs))
            {
                Assert.fail("when config not enable and GDS return  null CarShuttleCategoryCode , carbs should also return null CarShuttleCategoryCode");
            }
        }
        //feature on(ClientID=1) GDS return  CarShuttleCategoryCode(GDS value should be used)
        //feature off(ClientID except 1,5) GDS return CarShuttleCategoryCode(GDS value should be used)
        else if (!returnEmptyCarShuttleCategoryCode)
        {
            if (!carShuttleCategoryCode_carbs.equals(carShuttleCategoryCode_gds))
            {
                Assert.fail("when config enable or not enable  and GDS return  CarShuttleCategoryCode , carbs should return CarShuttleCategoryCode_GDS");
            }
        }

    }

    private CarECommerceGetDetailsRequestType getCarECommerceGetDetailsRequestType(String clientCode, boolean returnEmptyCarShuttleCategoryCode, CarbsRequestGenerator carbsSearchRequestGenerator) throws DataAccessException
    {
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        getDetailsRequestType.setClientCode(clientCode);
        getDetailsRequestType.getCarProductList().getCarProduct().get(0).setCarProductToken(null);
        getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarPickupLocation().setCarShuttleCategoryCode(null);

        if(returnEmptyCarShuttleCategoryCode)
        {
            // when  we pass "NULL" downstream for CDCode, our template return empty null CarShuttleCategoryCode
            getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setCorporateDiscountCode("NULL");
        }
        return getDetailsRequestType;
    }

}
