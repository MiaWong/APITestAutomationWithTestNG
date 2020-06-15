package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.e3.data.cartypes.defn.v5.AirFlightType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.traveltypes.defn.v4.CustomerType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.AirFlightReader;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CustomerReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by v-mechen on 1/8/2017.
 */
public class TVRSReq {

    private String isoCountry;
    private String primaryLangID;
    private String referenceId;
    private TravelerListType travelerList;
    private CustomerType customer;
    private AirFlightType airFlight;
    private CarSpecialEquipmentListType carSpecialEquipmentList;

    public TVRSReq(Node request, CarsSCSDataSource scsDataSource) throws DataAccessException {
        //Read ISOCountry
        this.isoCountry = PojoXmlUtil.getNodeByTagName(request, "Source").getAttributes().getNamedItem("ISOCountry").getTextContent();

        //Read primaryLangID
        if (request.getAttributes().getNamedItem("PrimaryLangID") == null){
            this.primaryLangID = null;
        }
        else{
            this.primaryLangID = request.getAttributes().getNamedItem("PrimaryLangID").getTextContent();
        }

        //Read reference ID
        //this.referenceId = PojoXmlUtil.getNodeByTagName(request, "UniqueID").getAttributes().getNamedItem("ID").getTextContent();
        this.referenceId = PojoXmlUtil.getNodeByTagName(request, "RateReference").getTextContent();
        //Customer
        final CustomerReader customerReader = new CustomerReader();
        this.customer = customerReader.readCustomer(request);

        //TravelerList
        this.travelerList = customerReader.readTravelerList(this.customer);

        //AirFlight
        this.airFlight = AirFlightReader.readAirFlight(request);

        //CarSpecialEquipmentList
        this.carSpecialEquipmentList = readSpecialEquipList(request, scsDataSource);

    }

    //        <SpecialEquipPref EquipType="13" Quantity="1"></SpecialEquipPref>
    /// <summary>
    ///
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <param name="domainValueMapHelper"></param>
    /// <returns></returns>
    public static CarSpecialEquipmentListType readSpecialEquipList(Node request, CarsSCSDataSource scsDataSource) throws DataAccessException {
        CarSpecialEquipmentListType specialEquipList = new CarSpecialEquipmentListType();
        specialEquipList.setCarSpecialEquipment(new ArrayList<CarSpecialEquipmentType>());
        final List<Node> specialEquipNodeList = PojoXmlUtil.getNodesByTagName(request, "SpecialEquipPref");
        for (final Node node : specialEquipNodeList)
        {

            final String code = scsDataSource.getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.CAR_SPECIAL_EQUIPMENT, null, node.getAttributes().getNamedItem("EquipType").getTextContent()).get(0).getDomainValue();
            final CarSpecialEquipmentType specialEquip = new CarSpecialEquipmentType();
            specialEquip.setCarSpecialEquipmentCode(code);
            specialEquipList.getCarSpecialEquipment().add(specialEquip);
        }
        if(specialEquipNodeList.isEmpty())
        {
            specialEquipList = null;
        }
        return specialEquipList;
    }

    public String getIsoCountry() {
        return this.isoCountry;
    }

    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }

    public String getPrimaryLangID() {
        return this.primaryLangID;
    }

    public void setPrimaryLangID(String primaryLangID) {
        this.primaryLangID = primaryLangID;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public CarSpecialEquipmentListType getCarSpecialEquipmentList() {
        return carSpecialEquipmentList;
    }

    public void setCarSpecialEquipmentList(CarSpecialEquipmentListType carSpecialEquipmentList) {
        this.carSpecialEquipmentList = carSpecialEquipmentList;
    }

    public TravelerListType getTravelerList() {
        return travelerList;
    }

    public void setTravelerList(TravelerListType travelerList) {
        this.travelerList = travelerList;
    }

    public AirFlightType getAirFlight() {
        return airFlight;
    }

    public void setAirFlight(AirFlightType airFlight) {
        this.airFlight = airFlight;
    }

    public CustomerType getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerType customer) {
        this.customer = customer;
    }

}

