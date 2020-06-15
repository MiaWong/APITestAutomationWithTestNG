package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 6/29/2017.
 */
public class SpecialEquipForReserveVerification implements IReserveVerification{

    public VerificationResult verify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext verificationContext, CarsSCSDataSource scsDataSource, CommonEnumManager.ServieProvider serviceProvider) throws DataAccessException {

        final  List <String> specialEquips = getSpecialEquipCode(reserveVerificationInput);

        List<String> gdsSpecialEquipCode = null;
        if(CommonEnumManager.ServieProvider.worldSpanSCS == serviceProvider)
        {
            gdsSpecialEquipCode = getGdsSpecialEquipCode(verificationContext, scsDataSource);
        }
        if(CommonEnumManager.ServieProvider.Amadeus == serviceProvider)
        {
            gdsSpecialEquipCode = getGdsSpecialEquipCodeForAmadeus(verificationContext, scsDataSource);
        }
       if (CompareUtil.compareObject(specialEquips, gdsSpecialEquipCode, null, null))
        {
           return new VerificationResult(this.getName(), true, Arrays.asList(new String[]{"Successful"}));
        }
       return  new VerificationResult(this.getName(), false, Arrays.asList(new String[]{"failed"}));
    }


    public VerificationResult verifyAllSpeEquipSuppress(BasicVerificationContext verificationContext) {

        if (isGdsSpecialEquipCodeExist(verificationContext))
        {
            return new VerificationResult(this.getName(), false, Arrays.asList(new String[]{"All specialmentEquip should be suppressed"}));
        }
        return  new VerificationResult(this.getName(), true, Arrays.asList(new String[]{"success"}));
    }

    private List<String> getGdsSpecialEquipCode(BasicVerificationContext verificationContext, CarsSCSDataSource scsDataSource) throws DataAccessException {

        final Element vehicleCreateReservationReq = (Element) verificationContext.getSpooferTransactions().getElementsByTagNameNS("*","VehicleCreateReservationReq").item(0);
        final List<Node> gdsSpecialEquipments = PojoXmlUtil.getNodesByTagName(vehicleCreateReservationReq, "SpecialEquipment");
        final  List<String> gdsSpecialEquipCode = new ArrayList<>();
        for (final Node gdsSpecialEquipment : gdsSpecialEquipments) {

            final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap("CarSpecialEquipment", gdsSpecialEquipment.getAttributes().getNamedItem("Type").getTextContent());
            if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMaps)) {
                for (final ExternalSupplyServiceDomainValueMap externalSupplyServiceDomainValueMap : externalSupplyServiceDomainValueMaps) {
                    if (7 == externalSupplyServiceDomainValueMap.getMessageSystemID()) {
                        gdsSpecialEquipCode.add(externalSupplyServiceDomainValueMap.getDomainValue());
                    }
                }
            }


        }
        return gdsSpecialEquipCode;
    }

    private boolean isGdsSpecialEquipCodeExist(BasicVerificationContext verificationContext) {
        final Element vehicleCreateReservationReq = (Element) verificationContext.getSpooferTransactions().getElementsByTagNameNS("*","VehicleCreateReservationReq").item(0);
        final List<Node> gdsSpecialEquipments = PojoXmlUtil.getNodesByTagName(vehicleCreateReservationReq, "SpecialEquipment");
        return CollectionUtils.isNotEmpty(gdsSpecialEquipments);
    }
    private List<String> getGdsSpecialEquipCodeForAmadeus(BasicVerificationContext verificationContext, CarsSCSDataSource scsDataSource) throws DataAccessException {

        final Element carSell = (Element) verificationContext.getSpooferTransactions().getElementsByTagNameNS("*","Car_Sell").item(0);
        final List<Node> gdsSpecialEquipments = PojoXmlUtil.getNodesByTagName(carSell, "specialEquipPrefs");
        final List<String> gdsSpecialEquipCode = new ArrayList<>();
        for (final Node gdsSpecialEquipment : gdsSpecialEquipments) {
            final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap("CarSpecialEquipment", gdsSpecialEquipment.getTextContent());
            if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMaps)) {
                for (final ExternalSupplyServiceDomainValueMap externalSupplyServiceDomainValueMap : externalSupplyServiceDomainValueMaps) {
                    gdsSpecialEquipCode.add(externalSupplyServiceDomainValueMap.getDomainValue());
                }
            }
        }
        return gdsSpecialEquipCode;
    }


    private List <String> getSpecialEquipCode(ReserveVerificationInput reserveVerificationInput) {
        final List<CarSpecialEquipmentType> carSpecialEquipments = reserveVerificationInput.getRequest().getCarSpecialEquipmentList().getCarSpecialEquipment();
        final  List <String> specialEquips = new ArrayList<>();
        for (final CarSpecialEquipmentType carSpecialEquipmentType : carSpecialEquipments)
        {
            specialEquips.add(carSpecialEquipmentType.getCarSpecialEquipmentCode());
        }
        return specialEquips;
    }

    public static void setEquipment(ReserveDefaultValue reserveDefaultValue, CarSupplyConnectivityReserveRequestType reserveRequestType)
    {
        if (StringUtils.isNotBlank(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode())) {

            if (null == reserveRequestType.getCarSpecialEquipmentList() || CollectionUtils.isEmpty(reserveRequestType.getCarSpecialEquipmentList().getCarSpecialEquipment())) {
                final CarSpecialEquipmentListType carSpecialEquipmentListType = new CarSpecialEquipmentListType();
                reserveRequestType.setCarSpecialEquipmentList(carSpecialEquipmentListType);
                final List<CarSpecialEquipmentType> carSpecialEquipmentTypes = new ArrayList<>();
                carSpecialEquipmentListType.setCarSpecialEquipment(carSpecialEquipmentTypes);
                for (final String code : Arrays.asList(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(","))) {
                   final CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                    carSpecialEquipment.setCarSpecialEquipmentCode(code);
                    carSpecialEquipmentTypes.add(carSpecialEquipment);
                }

            } else {
                for (final String code : Arrays.asList(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode().split(","))) {
                    final CarSpecialEquipmentType carSpecialEquipment = new CarSpecialEquipmentType();
                    carSpecialEquipment.setCarSpecialEquipmentCode(code);
                    reserveRequestType.getCarSpecialEquipmentList().getCarSpecialEquipment().add(carSpecialEquipment);

                }
            }
        }
    }
    @Override
    public VerificationResult verify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext basicVerificationContext) {
        return null;
    }
}
