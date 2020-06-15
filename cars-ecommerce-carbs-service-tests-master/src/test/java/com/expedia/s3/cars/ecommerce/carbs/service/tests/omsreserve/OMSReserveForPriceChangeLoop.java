package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarOfferDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.OmReserveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.OmRetrieveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItemCar;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.CarBookingDatasource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.GDSPCarType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.ResultFilter;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.RetrieveRequest;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import expedia.om.supply.messages.defn.v1.StatusCodeCategoryType;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 3/13/2018.
 * Test plan for CASSS-8682 Car price change loop at PreparePurchase due to different prices in shopping & reserve path
 *https://confluence.expedia.biz/display/SSG/CASSS-10578+Design+for+Price+change+loop+fix+in+packages+under+GDSP+Merchant
 */
@SuppressWarnings("PMD")
public class OMSReserveForPriceChangeLoop extends SuiteCommon {
   final public Logger logger = Logger.getLogger(OMSReserveForPriceChangeLoop.class);
    private static final String setPosConfigUrl = AppConfig.resolveStringValue("${setPosConfig.server.address}");
    final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(),setPosConfigUrl);
    final public static String  environment = AppConfig.resolveStringValue("${environment.name}");
    final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());

    @SuppressWarnings("CPD-START")
    //package GDSP GDSPNetRate when set tolerance is 0.02, and PriceChange.totalPriceToleranceGDSP/enable 1， verify if price change less than 0.02, it should booking success
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 1)
    public void omsReserve10391ForPriceChangeLoopTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenario, "0.02", ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP, environment))
        {
            Assert.fail("The pos config for " + ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP + " is not expect 0.02");
        }
        omsReserveTest("10391", scenario, false, "XHXGCX", "Y", GDSPCarType.GDSPNetRate, true);//GDSP all prepaid car
    }

    //package GDSP commission prepaid car second booking success(currency conversion), .02 tolerance scenario
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 2)
    public void omsReserve10392ForPriceChangeLoopTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenario, "0.02", ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP, environment))
        {
            Assert.fail("The pos config for " + ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP + " is not expect 0.02");
        }
        omsReserveTest("10392", scenario, false, "XHXGCX", "Y", GDSPCarType.GDSPCommission, true);//GDSP all prepaid car
    }

    //standalone GDSP GDSPNetRate prepaid car second booking success(currency conversion), .02 tolerance scenario
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 3)
    public void omsReserve10393ForPriceChangeLoopTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OffAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenario, "0.02", ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP, environment))
        {
            Assert.fail("The pos config for " + ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP + " is not expect 0.02");
        }
        omsReserveTest("10393", scenario, false, "XHXGCX", "Y", GDSPCarType.GDSPNetRate, true);//GDSP all prepaid car
    }

    //standalone GDSP  commission prepaid car second booking success(currency conversion), .02 tolerance scenario
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, priority = 4)
    public void omsReserve10394ForPriceChangeLoopTest() throws Exception
    {
        final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        if (!posConfigHelper.checkPosConfigFeatureEnable(scenario, "0.02", ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP, environment))
        {
            Assert.fail("The pos config for " + ServiceConfigs.PRICECHANGE_TOTALPRICETOLERANCE_GDSP + " is not expect 0.02");
        }
        omsReserveTest("10394", scenario, false, "XHXGCX", "Y", GDSPCarType.GDSPCommission, true);//GDSP all prepaid car
    }


    //package GDSP GDSPNetRate prepaid car second booking success (currency conversion)
    @Test(groups = {TestGroup.BOOKING_REGRESSION, "101911"}, priority = 5)
    public void omsReserve10191ForPriceChangeLoopTest() throws Exception {

        try{
            if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
            {
                Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
            }
            final TestScenario scenario = CommonScenarios.Worldspan_CA_GDSP_Package_USLocation_OnAirport.getTestScenario();
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

            omsReserveTest("10191",scenario, true, "XHXGCX", "Y", GDSPCarType.GDSPNetRate,false);//GDSP all prepaid car


        }catch (Exception e)
        {
            throw new Exception(e);
        }
    }

    //package GDSP  commssion prepaid car second booking success(currency conversion)
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10191ForPriceChangeLoopTest"})
    public void omsReserve10198ForPriceChangeLoopTest() throws Exception {

        try{

            if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
            {
                Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
            }
            final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario();
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

            omsReserveTest("10198",scenario, true, "XHXGCX", "Y", GDSPCarType.GDSPCommission,false);//GDSP all prepaid car


        }catch (Exception e)
        {
            throw new Exception(e);
        }finally {
            posConfigHelper.rollbackPosConfigList();

        }
    }

    //standalone GDSP GDSPNetRate prepaid one way car second booking success( no currency conversion)
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10198ForPriceChangeLoopTest"})
    public void omsReserve10193ForPriceChangeLoopTest() throws Exception
    {
        try
        {

            if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
            {
                Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
            }
            final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OffAirport.getTestScenario();
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "3:C", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

            omsReserveTest("10193", scenario, true, "XHXGCX", "Y", GDSPCarType.GDSPNetRate,false);//GDSP all prepaid car

        } catch (Exception e)
        {
            throw new Exception(e);
        } finally
        {
            posConfigHelper.rollbackPosConfigList();

        }
    }

    //standalone GDSP  commission prepaid one way car second booking success(no currency conversion)
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10192ForPriceChangeLoopTest"})
    public void omsReserve10293ForPriceChangeLoopTest() throws Exception
    {

        try
        {

            if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
            {
                Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
            }
            final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "3:C", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

            omsReserveTest("10293", scenario, true, "XHXGCX", "Y", GDSPCarType.GDSPCommission,false);//GDSP all prepaid car


        } catch (Exception e)
        {
            throw new Exception(e);
        } finally
        {
            posConfigHelper.rollbackPosConfigList();

        }
    }

    //standalone agency no prepaid one way car second booking success
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10193ForPriceChangeLoopTest"})
    public void omsReserve10192ForPriceChangeLoopTest() throws Exception
    {
        try
        {

            if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
            {
                Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
            }
            final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario();
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

            omsReserveTest("10192", scenario, true, "XHXGCX", "N", null,false);

        } catch (Exception e)
        {
            throw new Exception(e);
        } finally
        {
            posConfigHelper.rollbackPosConfigList();

        }
    }

        //standalone agency no prepaid one way car second booking failed (Total lower than Base)
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10293ForPriceChangeLoopTest"})
        public void omsReserve10295ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10295", scenario, true, "XHXGCX", "N", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }

        }


        //standalone agency no prepaid one way car first booking failed. no loop
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10295ForPriceChangeLoopTest"})
        public void omsReserve10194ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 14, "0"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10194", scenario, false, "QZ7FKN", "N", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }
        }
        //standalone agency  prepaid one way car second booking success
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10194ForPriceChangeLoopTest"})
        public void omsReserve10195ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10195", scenario, true, "XHXGCX", "Y", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }
        }

        //standalone agency  no prepaid  car second booking success
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10195ForPriceChangeLoopTest"})
        public void omsReserve10196ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10196", scenario, true, "XHXGCX", "N", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }
        }

        //standalone agency   prepaid  car no loop
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10196ForPriceChangeLoopTest"})
        public void omsReserve10197ForPriceChangeLoopTest() throws Exception
        {

           try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10197", scenario, false, "XHXGCX", "Y", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }

        }

        //standalone/agency/prepaid car no loop
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10197ForPriceChangeLoopTest"})
        public void omsReserve10199ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C|3:HC,FC,FHC", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10199", scenario, false, "XHXGCX", "Y",  null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();
            }
        }

        //standalone/agency/prepaid car no loop
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10199ForPriceChangeLoopTest"})
        public void omsReserve10200ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10200", scenario, false, "XHXGCX", "Y", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }
        }

        //standalone/agency/current conversation  round trip second booking failed
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10200ForPriceChangeLoopTest"})
        public void omsReserve10201ForPriceChangeLoopTest() throws Exception
        {

            try
            {

                if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1"))
                {
                    Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
                }
                final TestScenario scenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario();
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1:C", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
                posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

                omsReserveTest("10201", scenario, true, "XHXGCX", "N", null,false);

            } catch (Exception e)
            {
                throw new Exception(e);
            } finally
            {
                posConfigHelper.rollbackPosConfigList();

            }
        }

        //standalone/agency/current conversation/one way second booking failed(total less than base)
        @Test(groups = {TestGroup.BOOKING_REGRESSION}, dependsOnMethods = {"omsReserve10201ForPriceChangeLoopTest"})
        public void omsReserve10202ForPriceChangeLoopTest() throws Exception {

        try {

            if (!clientConfigHelper.checkClientConfig(environment, ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, 15, "1")) {
                Assert.fail("Client config for Booking.adjustReservePriceDifference/enable is not set right");
            }
            final TestScenario scenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MCO_LAX_OnAirport.getTestScenario();
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "1:C", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_APPLICABLEBUSINESSMODELPURCHASETYPECOMBINATION, false);
            posConfigHelper.setFeatureEnable(scenario, "0", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_PREPAID_ENABLE, false);
            posConfigHelper.setFeatureEnable(scenario, "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE, false);

            omsReserveTest("10202", scenario, true, "XHXGCX", "N", null,false);//XHXGCX

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            posConfigHelper.rollbackPosConfigList();

        }
    }


    private void omsReserveTest(String tuid, TestScenario scenario, Boolean featureEnable, String clientCode, String prepaidCar, GDSPCarType carType, Boolean toleranceChangeVerify) throws Exception {

        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        String  specifyGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "BOOKING_LOOP_FOR_PRICE_CHANGE");
        if (toleranceChangeVerify)
        {
            specifyGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "BOOKING_PRICE_CHANGE_LESSTHAN_0.02");
        }
        final TestData testData = new TestData(httpClient, scenario, tuid, specifyGuid,spooferTransport);
        testData.setClientCode(clientCode);
        testData.setNeedPrepaidCar(prepaidCar);
        if(null != carType)
        {
            ResultFilter resultFilter = new ResultFilter();
            resultFilter.setCarType(carType);
            testData.setResultFilter(resultFilter);
        }

       //search
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(specifyGuid, httpClient, request);
        CarProductType carProductType =  CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails(when send preparepurchase request, there is CEDR send downstream, for this userstory, we need to verify the CEDR response'price is stored
        // right  in BookingItemCar table for  ShoppingTotalBaseRate and ShoppingEstimatedTotalTaxAndFeeAmt, cause we set same template for getdetails, so can directly use
        // this getdetail response to do verify.)
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(specifyGuid, httpClient, getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(specifyGuid, scenario, getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(specifyGuid, httpClient, getCostAndAvailabilityRequestType);
       CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(specifyGuid, scenario, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        //first booking
        firstOMReserve(toleranceChangeVerify,featureEnable, testData, carOMSReqAndRespObj, specifyGuid);
         //second Booking
        if(featureEnable)
        secondOMReserve(testData, carOMSReqAndRespObj,prepaidCar, specifyGuid ,getDetailsResponseType);

    }


    private void carBookItemdbVerify(TestData testData, CarECommerceGetDetailsResponseType getDetailsResponseType, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj) throws DataAccessException {
        double miscAmount = 0;
        double totalAmount = 0;
        double markupAmount = 0;
        for(PriceType priceType : getDetailsResponseType.getCarProductList().getCarProduct().get(0).getPriceList().getPrice())
       {

           //Cause the spoofer response template of USD currency, and as the design we need  stored the  shopping response price,
           //so the test result is always stored the price with USD currency(even currency conversation case).
           if ("Misc".equalsIgnoreCase(priceType.getFinanceCategoryCode()) &&
                   "USD".equalsIgnoreCase(priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
           {
               int amount = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();
               long placeCount = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();

               miscAmount = amount/Math.pow(10,placeCount) * priceType.getFinanceApplicationUnitCount();
           }

           if ("Total".equalsIgnoreCase(priceType.getFinanceCategoryCode()) &&
                   "USD".equalsIgnoreCase(priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
           {
               int amount = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();
               long placeCount = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
               totalAmount = amount/Math.pow(10,placeCount) * priceType.getFinanceApplicationUnitCount();
           }

           if(priceType.getFinanceCategoryCode().equalsIgnoreCase("MaxMarginAmt") &&
                   "USD".equalsIgnoreCase(priceType.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
           {
               int amount = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();
               long placeCount = priceType.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
               markupAmount =  amount/Math.pow(10,placeCount) * priceType.getFinanceApplicationUnitCount();
           }
       }

        double shoppingTransactionAmtCost  = (double)Math.round((totalAmount - miscAmount - markupAmount)*100)/100;
        long bookingItemID = carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getSupplyRecordLocator().getBookingItemID();
        System.out.println("******bookingItemID****** : " + bookingItemID);
        CarBookingDatasource carBookingDatasource = new CarBookingDatasource(DatasourceHelper.getCarsBookingDatasource());
        BookingItemCar bookingItemCar = carBookingDatasource.getBookingItemCarByBookingItemID((int)bookingItemID);

        if(Double.compare(shoppingTransactionAmtCost, Double.parseDouble(bookingItemCar.getShoppingTransactionAmtCost()))!= 0)
        {
            Assert.fail("TUID:"+ testData.getTuid() +" ShoppingTransactionAmtCost in DB is wrong , expect value "+ shoppingTransactionAmtCost + " actural value "+ bookingItemCar.getShoppingTransactionAmtCost());
        }

        if(Double.compare(miscAmount, Double.parseDouble(bookingItemCar.getShoppingEstimatedTotalTaxAndFeeAmt()))!= 0)
        {
            Assert.fail("TUID:"+ testData.getTuid() +" ShoppingEstimatedTotalTaxAndFeeAmt in DB is wrong , expect value "+ miscAmount+ " actural value "+ bookingItemCar.getShoppingTransactionAmtCost());
        }
    }

    private void firstOMReserve(Boolean toleranceChangeVerify, Boolean featureEnable, TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj, String specifyGuid) throws Exception {
        CarbsOMReserveRequestSender.CarBSGetOrderProcessSend(testData, carOMSReqAndRespObj);
        //2.Create record
        CarbsOMReserveRequestSender.CarBSCreateRecordSend(testData, carOMSReqAndRespObj);
        try {
            //	3.Build and send PreparePurchaseRequest request to CarBS (Associate Product with bookingID and insert related record into BookingDB).
            sendPreparePurchaseRequestForFirstBooking(toleranceChangeVerify, featureEnable, testData, carOMSReqAndRespObj,specifyGuid);

        }catch (Exception e)
        {
            logger.info(e.getMessage());
            //	5.Build and send RollBackPreparePurchaseRequest request to CarBS (Cancel the booking before commit book successfully).
            CarbsOMReserveRequestSender.CarBSRollbackPreparePurchaseSend(testData, carOMSReqAndRespObj);
            throw new Exception(e);
        }
    }

    private void secondOMReserve(TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj, String prepaidCar, String specifyGuid, CarECommerceGetDetailsResponseType getDetailsResponseType) throws Exception {
        boolean bookingSuccess = true;
        sendGetOrderProcessForSecondBooking(testData, carOMSReqAndRespObj);
        //2.Create record
        CarbsOMReserveRequestSender.CarBSCreateRecordSend(testData, carOMSReqAndRespObj);
        try {
            //	3.Build and send PreparePurchaseRequest request to CarBS (Associate Product with bookingID and insert related record into BookingDB).
              sendPreparePurchaseRequestForSecondBooking(testData, carOMSReqAndRespObj, specifyGuid);

              CarbsOMReserveRequestSender.CarBSCommitPreparePurchaseSend(testData, carOMSReqAndRespObj);
        }catch (Exception e)
        {
            logger.info(e.getMessage());
            //	5.Build and send RollBackPreparePurchaseRequest request to CarBS (Cancel the booking before commit book successfully).
            CarbsOMReserveRequestSender.CarBSRollbackPreparePurchaseSend(testData, carOMSReqAndRespObj);

            // CASSS-9969 Validate reserve total cost is not less than its base cost during AdjustReservePriceDifferenceAction
            if(posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), "1", ServiceConfigs.BOOKING_ADJUSTRESERVEPRICEDIFFERENCE_VALIDATERESERVETOTAL_ENABLE,environment))
            {
                bookingSuccess = false;
                logger.info("***sencond booking failed for***  " + carOMSReqAndRespObj.getPreparePurchaseResponseType().getResponseStatus().getStatusMessage());
            }
           else
           {
               Assert.fail(e.getMessage());
           }
        }

        //todo perfmetric verify

        if(bookingSuccess)
        {
            //retrieve
            retrievePriceTotalVerify(testData, carOMSReqAndRespObj);


            //Agency prepaid Car can't be cancel
            if (!("Y".equals(prepaidCar) && testData.getScenarios().getBusinessModel() == CommonEnumManager.BusinessModel.Agency.getBusinessModel()))
            {
                final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
                CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
            }

            carBookItemdbVerify(testData, getDetailsResponseType, carOMSReqAndRespObj);
        }
    }

    private void retrievePriceTotalVerify(TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj) throws DataAccessException, IOException {
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carOMSReqAndRespObj);
        final RetrieveRequest retrieveRequest = carbsOMRetrieveReqAndRespGenerator.createRetrieveRequest();
        final RetrieveResponseType retrieveResponseType = CarbsOMServiceSender.sendRetrieveResponse(testData.getGuid(), httpClient,retrieveRequest);
        OmRetrieveVerifier.isOMRetrieveWorksVerifier(testData.getGuid(), testData.getScenarios(), retrieveRequest, retrieveResponseType, false);

        List<PriceType> retrievePriceTypeList = new ArrayList<>();
        List<PriceType> preparePurchasePriceTypeList = new ArrayList<>();
        for(PriceType priceType : retrieveResponseType.getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getPriceList().getPrice())
        {
            if("Total".equalsIgnoreCase(priceType.getFinanceCategoryCode()))
            {
                retrievePriceTypeList.add(priceType);
            }
        }
        for(PriceType priceType : carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getPriceList()
        .getPrice())
        {
            if("Total".equalsIgnoreCase(priceType.getFinanceCategoryCode()))
            {
                preparePurchasePriceTypeList.add(priceType);
            }
        }

        CompareUtil.compareObject(retrievePriceTypeList, preparePurchasePriceTypeList, null, null);
    }

    private void sendPreparePurchaseRequestForSecondBooking(TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj, String specifyGuid) throws Exception {
        final PreparePurchaseRequest preparePurchaseRequest = carOMSReqAndRespObj.createPreparePurchaseRequest(testData);

        CarOfferDataType carOfferDataType = carOMSReqAndRespObj.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData();
        preparePurchaseRequest.getConfiguredProductData().setCarOfferData(carOfferDataType);
        final PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(specifyGuid, testData.getHttpClient(), preparePurchaseRequest);

        carOMSReqAndRespObj.setPreparePurchaseRequestType(preparePurchaseRequest);
        carOMSReqAndRespObj.setPreparePurchaseResponseType(preparePurchaseResponse);

        OmReserveVerifier.isPreparePurchaseWorksVerifier(testData.getGuid(),testData.getScenarios(),preparePurchaseRequest,preparePurchaseResponse);
    }

    private void sendGetOrderProcessForSecondBooking(TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj) throws DataAccessException, IOException {
        final GetOrderProcessRequest getOrderProcessRequest = carOMSReqAndRespObj.createGetOrderProcessRequest(testData);

        //set the first booking failed response carOffer to sencond getOrderprocess request
        CarOfferDataType carOfferDataType = carOMSReqAndRespObj.getPreparePurchaseResponseType().getUpdatedConfiguredOfferData().getCarOfferData();
        getOrderProcessRequest.getConfiguredOfferData().setCarOfferData(carOfferDataType);
        final GetOrderProcessResponseType getOrderProcessResponse = CarbsOMServiceSender.sendGetOrderProcessResponse(testData.getGuid(), testData.getHttpClient(), getOrderProcessRequest);

        carOMSReqAndRespObj.setGetOrderProcessRequestType(getOrderProcessRequest);
        carOMSReqAndRespObj.setGetOrderProcessResponseType(getOrderProcessResponse);

        OmReserveVerifier.isGetOrderProcessWorksVerifier(testData.getGuid(),testData.getScenarios(),getOrderProcessRequest, getOrderProcessResponse);
    }

    private void sendPreparePurchaseRequestForFirstBooking(Boolean toleranceChangeVerify, Boolean featureEnable, TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj, String specifyGuid) throws DataAccessException, IOException, SQLException
    {
        final PreparePurchaseRequest preparePurchaseRequest = carOMSReqAndRespObj.createPreparePurchaseRequest(testData);

        final PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(specifyGuid, testData.getHttpClient(), preparePurchaseRequest);

        if(featureEnable && StatusCodeCategoryType.SUCCESS.equals(preparePurchaseResponse.getResponseStatus().getStatusCodeCategory()))
        {
            Assert.fail("TUID:"+ testData.getTuid() +" For first booking, it should be failed for price change");
        }

        if(featureEnable && !Boolean.TRUE.equals(preparePurchaseResponse.getUpdatedConfiguredOfferData().getCarOfferData().getPriceChangeAtReserveBoolean()))
        {
            Assert.fail("TUID:"+ testData.getTuid() +" For first booking, There should be PriceChangeAtReserveBoolean node in response");

        }

        if (toleranceChangeVerify && !StatusCodeCategoryType.SUCCESS.equals(preparePurchaseResponse.getResponseStatus().getStatusCodeCategory()))
        {
            Assert.fail("TUID:" + testData.getTuid() + " For first booking, it should be success for price change less than 0.02");
        }

        if(toleranceChangeVerify && null != preparePurchaseResponse.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getPriceChangeAtReserveBoolean())
        {
            Assert.fail("TUID:"+ testData.getTuid() +" For .02 scenario, for first booking, It should not have PriceChangeAtReserveBoolean node in response");
        }

        carOMSReqAndRespObj.setPreparePurchaseRequestType(preparePurchaseRequest);
        carOMSReqAndRespObj.setPreparePurchaseResponseType(preparePurchaseResponse);
    }



   // perfmetric verify
    private void verificationResult(TestData testData) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        try {
            Thread.sleep(10000);

            final Date endTime = new Date();
            final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                    " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
            // Splunk host address
            final String hostName = "https://splunk.us-west-2.test.expedia.com";
            // Splunk host port, default value is 8089
            final int hostPort = 8089;
            final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);


            if (!splunkResult.get(0).containsKey("PriceChangeAtReserve")|| !"True".equalsIgnoreCase(splunkResult.get(0).get("PriceChangeAtReserve").toString()))
            {
                Assert.fail("PriceChangeAtReserve=true is not logged.");
            }
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

}


