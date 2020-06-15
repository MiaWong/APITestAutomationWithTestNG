package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.persontypes.defn.v4.PersonNameType;
import com.expedia.e3.data.persontypes.defn.v4.PersonType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.commonreader.VehAvailNodeHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarCatalogMakeModelReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 12/20/2016.
 */
public class VRSRsp {
    final private Logger logger = Logger.getLogger(VRSRsp.class);

    final private CarReservationType carReservationType;
    private List<String> errors;
    private AdvisoryTextListType advisoryTextList;

    public AdvisoryTextListType getAdvisoryTextList() {
        return advisoryTextList;
    }

    public void setAdvisoryTextList(AdvisoryTextListType advisoryTextList) {
        this.advisoryTextList = advisoryTextList;
    }

    public List<String> getErrors() {
        return errors;
    }

    public CarReservationType getCarReservationType() {
        return carReservationType;
    }

    public VRSRsp(Node response, CarsSCSDataSource scsDataSource) {
        carReservationType = new CarReservationType();
        try {
            carReservationType.setCarProduct(readCarproduct(response, scsDataSource));

            carReservationType.setTravelerList(travelerList(response));
            carReservationType.setReferenceList(referenceList(response));
            carReservationType.setBookingStateCode(bookStatus(response));
            carReservationType.setCarSpecialEquipmentList(readCarSpecialEquipment(response, scsDataSource));
            errors = readSpecialEquipWarnings(response, scsDataSource);
            this.advisoryTextList = getAdvisoryTextList(response);
        } catch (Exception e) {
            logger.error("there is exception: " + e);
        }
    }

    private List<String> readSpecialEquipWarnings(Node nodeObject, CarsSCSDataSource scsDataSource) throws Exception {
        final List<String> warning = new ArrayList<>();
        //Get waring node from VRS
        final List<Node> specialEquipmentListWarnings = PojoXmlUtil.getNodesByTagName(nodeObject, "Warning");
        if (CollectionUtils.isNotEmpty(specialEquipmentListWarnings)) {
            for (final Node specialEquipmentListWarning : specialEquipmentListWarnings) {
                if ("3".equals(specialEquipmentListWarning.getAttributes().getNamedItem("Code").getTextContent())) {
                    final String warningInfo = specialEquipmentListWarning.getTextContent().trim();
                    final String spCode = warningInfo.split(":")[1];
                    addWarning(scsDataSource, warning, spCode);


                }

            }
        }


        return warning;
    }

    private void addWarning(CarsSCSDataSource scsDataSource, List<String> warning, String spCode) throws DataAccessException {
        if (StringUtil.isNotBlank(spCode)) {
            final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap("CarSpecialEquipment", spCode);

            if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMaps)) {
                warning.add(externalSupplyServiceDomainValueMaps.get(0).getDomainValue());
            }
        }
    }


    private CarSpecialEquipmentListType readCarSpecialEquipment(Node nodeObject, CarsSCSDataSource scsDataSource) throws Exception {
        final CarSpecialEquipmentListType carSpecialEquipmentListType = new CarSpecialEquipmentListType();
        final List<CarSpecialEquipmentType> carSpecialEquipmentTypes = new ArrayList<>();
        carSpecialEquipmentListType.setCarSpecialEquipment(carSpecialEquipmentTypes);


        final List<Node> specialEquipPrefList = PojoXmlUtil.getNodesByTagName(nodeObject, "Equipment");
        if (CollectionUtils.isNotEmpty(specialEquipPrefList)) {
            for (final Node specialEquip : specialEquipPrefList) {
                final CarSpecialEquipmentType carSpecialEquipmentType = new CarSpecialEquipmentType();

                final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap("CarSpecialEquipment", specialEquip.getAttributes().getNamedItem("EquipType").getTextContent());

                if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMaps)) {
                    carSpecialEquipmentType.setCarSpecialEquipmentCode(externalSupplyServiceDomainValueMaps.get(0).getDomainValue());
                    carSpecialEquipmentType.setBookingStateCode("Unconfirmed");
                    carSpecialEquipmentTypes.add(carSpecialEquipmentType);
                }
            }
        }


        return carSpecialEquipmentListType;
    }

    private CarProductType readCarproduct(Node nodeObject, CarsSCSDataSource scsDataSource) throws Exception {
        final CarProductType carProductType = new CarProductType();

        //set CarinventoryKey
        final CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
        carProductType.setCarInventoryKey(carInventoryKey);

        VehAvailNodeHelper.setCarInventoryKey(nodeObject, scsDataSource, carInventoryKey);

        //set CarRate
        VehAvailNodeHelper.setCarRate(nodeObject, carInventoryKey);

        //CostList
        VehAvailNodeHelper.detailsCostList(nodeObject, carProductType, scsDataSource);


        //CarCatalogMakeModel
        carProductType.setCarCatalogMakeModel(CarCatalogMakeModelReader.readCarCatalogMakeModel(nodeObject));
        //Based on CASSS-10368 Micronnexus : Show Number of Doors from GDS for both Min and Max, if min door count not exist, get min door count from max door count
        if (carProductType.getCarCatalogMakeModel().getCarMinDoorCount() == 0) {
            carProductType.getCarCatalogMakeModel().setCarMinDoorCount(carProductType.getCarCatalogMakeModel().getCarMaxDoorCount());
        }

        //priceEquip
        VehAvailNodeHelper.carVehicleOption(nodeObject, scsDataSource, carProductType);
        return carProductType;
    }

    private String bookStatus(Node nodeObject) {
        final String bookStaus = PojoXmlUtil.getNodeByTagName(nodeObject, "VehResRSCore").
                getAttributes().getNamedItem("ReservationStatus").getTextContent();
        if ("Confirmed".equalsIgnoreCase(bookStaus)) {
            return "Booked";
        }
        return null;
    }

    private TravelerListType travelerList(Node nodeObject) {

        final List<Node> firstNames = PojoXmlUtil.getNodesByTagName(nodeObject, "GivenName");
        final List<Node> lastNames = PojoXmlUtil.getNodesByTagName(nodeObject, "Surname");
        final TravelerListType travelerListType = new TravelerListType();
        final List<TravelerType> travelerTypes = new ArrayList<>();
        travelerListType.setTraveler(travelerTypes);

        for (int i = 0; firstNames.size() > i; i++) {
            final TravelerType travelerType = new TravelerType();
            final PersonType personType = new PersonType();
            final PersonNameType personNameType = new PersonNameType();
            personType.setPersonName(personNameType);
            personNameType.setFirstName(firstNames.get(i).getTextContent());
            personNameType.setLastName(lastNames.get(i).getTextContent());
            travelerType.setPerson(personType);
            travelerTypes.add(travelerType);
        }
        return travelerListType;
    }

    private ReferenceListType referenceList(Node nodeObject) {
        final String pnrCode = PojoXmlUtil.getNodeByTagName(nodeObject, "ConfID").getAttributes().getNamedItem("ID_Context").getTextContent();
        final String vendorCode = PojoXmlUtil.getNodeByTagName(nodeObject, "Vendor").getTextContent();
        final ReferenceListType referenceListType = new ReferenceListType();
        final List<ReferenceType> referenceTypes = new ArrayList<>();
        final ReferenceType referenceType = new ReferenceType();
        referenceType.setReferenceCategoryCode("PNR");
        referenceType.setReferenceCode(pnrCode);

        final ReferenceType referenceType1 = new ReferenceType();
        referenceType1.setReferenceCode(vendorCode);
        referenceType1.setReferenceCategoryCode("Vendor");
        referenceTypes.add(referenceType);
        referenceTypes.add(referenceType1);
        referenceListType.setReference(referenceTypes);

        return referenceListType;

    }

    public static AdvisoryTextListType getAdvisoryTextList(Node nodeObject)
    {
        final AdvisoryTextListType advisoryTextList = new AdvisoryTextListType();
        advisoryTextList.setAdvisoryText(new ArrayList<>());
        final List<Node> paymentNodes = PojoXmlUtil.getNodesByTagName(nodeObject, "PaymentForm");
        if(!CollectionUtils.isEmpty(paymentNodes))
        {
            for(int i = 0; i<  paymentNodes.get(0).getChildNodes().getLength(); i++)
            {
                final Node childNode = paymentNodes.get(0).getChildNodes().item(i);
                advisoryTextList.getAdvisoryText().add(childNode.getLocalName() + " - " + childNode.getAttributes().getNamedItem("ValueType").getTextContent());
            }
        }

        return advisoryTextList;
    }

}
