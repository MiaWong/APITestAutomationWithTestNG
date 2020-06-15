package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.reserve;

import com.expedia.e3.data.basetypes.defn.v4.VendorSupplierIDListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionListType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.book.SpecialEquipVerification;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import scala.reflect.runtime.Settings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 4/6/2017.
 */
public class BookingWithSpecialEquipment extends SuiteCommon{

    //vendorCode:FF  /Firefly
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void specialEquipment_MNSCS_OneUnavailableSpecialEquipmentList() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
       SpooferTransport  spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
       String reserveGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "SpecialEquipment_OneUnavailable") ;
       specialEquipmentForMNWarning(spooferTransport, CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "727585", 1040, reserveGuid, ReserveDefaultValue.CARSPECIALEQUIPMENTLIST_ONEUNAVAILABLE );
    }

    //vendorCode:CE  /Centauro
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void specialEquipment_MNSCS_MultiUnavailableSpecialEquipmentList() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SpooferTransport  spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        String reserveGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "SpecialEquipment_MultiUnavailable") ;
        specialEquipmentForMNWarning(spooferTransport, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "727586",   1015, reserveGuid, ReserveDefaultValue.MULTISPECIALEQUIPMENT);
    }

    //vendorCode:CE  /Centauro
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void specialEquipment_MNSCS_UnavailableAndAvailableSpecialEquipmentList() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SpooferTransport  spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        String reserveGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "SpecialEquipment_OneUnconfirmedOneUnavailable") ;
        specialEquipmentForMNWarning(spooferTransport, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_PMO.getTestScenario(), "727587", 1015, reserveGuid, ReserveDefaultValue.CARSPECIALEQUIPMENTLIST_MULTISPECIALEQUIPMENT);
    }


    private void specialEquipmentForMNWarning(SpooferTransport  spooferTransport, TestScenario scenario, String tuid, long vendorSupplierID , String reserveGuid, ReserveDefaultValue reserveDefaultValue) throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        //search
        SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), PojoXmlUtil.getRandomGuid());
        CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);
        setVendor(vendorSupplierID, searchRequestType);

        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, testData.getGuid());


        BasicRequestActions requestActions = new BasicRequestActions();
        SCSRequestGenerator scsRequestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());

        //getDetails
        requestActions.getDetail(scsRequestGenerator, httpClient, testData);


        //reserve
        final CarSupplyConnectivityReserveRequestType reserveRequestType = scsRequestGenerator.createReserveRequest();

        //set unavailable Equipment
        setEquipment(reserveDefaultValue, reserveRequestType);

        ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequestType, reserveGuid);
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(reserveGuid), reserveGuid, testData.getScenarios());

        reserveVerify(reserveVerificationInput, verificationContext);

        //cancel
        scsRequestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        requestActions.cancel(scsRequestGenerator, httpClient,testData);
    }

    private void reserveVerify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext verificationContext) {

        SpecialEquipVerification verification = new SpecialEquipVerification();
        final IVerification.VerificationResult verificationResult = verification.verify(reserveVerificationInput, verificationContext);
        if (!verificationResult.isPassed())
        {
            Assert.fail(verificationResult.toString());
        }
    }

    private void setEquipment(ReserveDefaultValue reserveDefaultValue, CarSupplyConnectivityReserveRequestType reserveRequestType) {
        if (StringUtils.isNotBlank(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode())) {

            if(null == reserveRequestType.getCarSpecialEquipmentList() || CollectionUtils.isEmpty(reserveRequestType.getCarSpecialEquipmentList().getCarSpecialEquipment())) {
                CarSpecialEquipmentListType carSpecialEquipmentListType = new CarSpecialEquipmentListType();
                reserveRequestType.setCarSpecialEquipmentList(carSpecialEquipmentListType);
                List<CarSpecialEquipmentType> carSpecialEquipmentTypes = new ArrayList<>();
                carSpecialEquipmentListType.setCarSpecialEquipment(carSpecialEquipmentTypes);
                for(String code : Arrays.asList(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(",")))
                {
                    CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                    carSpecialEquipment.setCarSpecialEquipmentCode(code);
                    carSpecialEquipmentTypes.add(carSpecialEquipment);

                }
            }
            else
            {
                for(String code : Arrays.asList(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(",")))
                {
                    CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                    carSpecialEquipment.setCarSpecialEquipmentCode(code);
                    reserveRequestType.getCarSpecialEquipmentList().getCarSpecialEquipment().add(carSpecialEquipment);

                }
            }

        }
        if (StringUtils.isNotBlank(reserveDefaultValue.getReserveConfigValue().getSpecialEquipmentEnumType()))
            {
                if(null == reserveRequestType.getCarProduct().getCarVehicleOptionList() || CollectionUtils.isEmpty(reserveRequestType.getCarProduct().getCarVehicleOptionList().getCarVehicleOption())) {
                    CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
                    reserveRequestType.getCarProduct().setCarVehicleOptionList(carVehicleOptionListType);
                    List<CarVehicleOptionType> carVehicleOptionTypes = new ArrayList<>();
                    carVehicleOptionListType.setCarVehicleOption(carVehicleOptionTypes);
                    for (String code : Arrays.asList(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(",")))
                    {
                        CarVehicleOptionType carVehicleOpt = new CarVehicleOptionType();
                        carVehicleOpt.setCarVehicleOptionCategoryCode("special equipment");
                        carVehicleOpt.setCarSpecialEquipmentCode(code);
                        carVehicleOptionTypes.add(carVehicleOpt);

                    }
                }
                else {
                    for (String code : Arrays.asList(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(","))) {
                        CarVehicleOptionType carVehicleOpt = new CarVehicleOptionType();
                        carVehicleOpt.setCarVehicleOptionCategoryCode("special equipment");
                        carVehicleOpt.setCarSpecialEquipmentCode(code);
                        reserveRequestType.getCarProduct().getCarVehicleOptionList().getCarVehicleOption().add(carVehicleOpt);
                    }
                }
            }
    }

    private void setVendor(long vendorSupplierID, CarSupplyConnectivitySearchRequestType searchRequestType) {
        VendorSupplierIDListType vendorSupplierIDListType = new VendorSupplierIDListType();
        List<Long> vendorSupplierIDs = new ArrayList<>();
        vendorSupplierIDs.add(vendorSupplierID);
        vendorSupplierIDListType.setVendorSupplierID(vendorSupplierIDs);
        searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).setVendorSupplierIDList(vendorSupplierIDListType);
    }
}
