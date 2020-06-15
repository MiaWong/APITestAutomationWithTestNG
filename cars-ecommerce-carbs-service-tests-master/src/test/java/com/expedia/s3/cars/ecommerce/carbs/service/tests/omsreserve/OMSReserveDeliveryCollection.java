package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CustomerLocationType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 10/29/2018.
 */
public class OMSReserveDeliveryCollection   extends SuiteCommon  {

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs373562tfs373564OMSReserveDeliveryCollectionPlaceID() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient));
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(),
                "373562", randomGuid);
        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDeliveryPlaceID("7271");
        specialHandleParam.setCollectionPlaceID("7278");
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        this.testOMSReserveDeliveryCollection(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs373563OMSReserveDeliveryCollectionHomeAddress() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient));
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_PAR_CDG.getTestScenario(),
                "373563", randomGuid);
        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setDeliveryAvailable(true);
        specialHandleParam.setCollectionAvailable(true);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        this.testOMSReserveDeliveryCollection(testData);
    }

    public void testOMSReserveDeliveryCollection(TestData testData) throws Exception {
        //send OMS Reserve Request
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID
                (testData);
        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(omRequestGenerate);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, true);

        //Verify delievry/collection phone exist in preparePurchase/retrieve response
        final CustomerLocationType bookDeliveryLocation = omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getDeliveryLocation();
        final CustomerLocationType bookCollectLocation = omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getCollectionLocation();
        Assert.assertTrue("Phone number not exist in preparePurchase response - CollectionLocation", existPhoneNumber(bookCollectLocation));
        Assert.assertTrue("Phone number not exist in preparePurchase response - DeliveryLocation", existPhoneNumber(bookDeliveryLocation));

        final CustomerLocationType retrieveDeliveryLocation =carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getDeliveryLocation();
        final CustomerLocationType retrieveCollectionLocation =carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCollectionLocation();
        Assert.assertTrue("Phone number not exist in retrieve response - CollectionLocation", existPhoneNumber(retrieveCollectionLocation));
        Assert.assertTrue("Phone number not exist in retrieve response - DeliveryLocation", existPhoneNumber(retrieveDeliveryLocation));
    }

    private boolean existPhoneNumber(CustomerLocationType location)
    {
        boolean exist = true;
        if(null == location || null == location.getPhone() || StringUtils.isEmpty(location.getPhone().getPhoneNumber()))
        {
            exist = false;
        }
        return exist;
    }
}
