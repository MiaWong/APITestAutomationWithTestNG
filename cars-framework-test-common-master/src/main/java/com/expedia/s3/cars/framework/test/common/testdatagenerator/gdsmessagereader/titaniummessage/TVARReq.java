package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.SupplierItemMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.DateLocationReader.readCostAvailReqDateLocation;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.DateLocationReader.readSearchReqDateLocation;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.SupplierIDListReader.readVendorSupplierIDList;

/**
 * Created by v-mechen on 11/29/2016.
 */
public class TVARReq {
    private CarSearchCriteriaType carSearchCriteria;
    private CarProductType carProduct; //For costAvail
    private boolean ccGuarantee;
    private String tourCode;
    private List<String> tourCodeList;
    private String isoCountry;
    private String primaryLangID;
    private String pickUpLocationCode;
    private String returnLocationCode;
    private List<SupplierItemMap> supplierItemMaps;

    public TVARReq(Node request, CarsSCSDataSource scsDataSource) throws DataAccessException {
        this.carSearchCriteria = new CarSearchCriteriaType();
        this.carProduct = new CarProductType();
        this.carProduct.setCarInventoryKey(new CarInventoryKeyType());
        this.carProduct.getCarInventoryKey().setCarCatalogKey(new CarCatalogKeyType());

        //for vendorsupplierIDList
        final List<Node> vendorPrefNodeList = PojoXmlUtil.getNodesByTagName(request, "VendorPref");
        if (!vendorPrefNodeList.isEmpty())
        {
            this.carSearchCriteria.setVendorSupplierIDList(readVendorSupplierIDList(scsDataSource, vendorPrefNodeList));
            this.carProduct.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(
                    GDSMsgReadHelper.readVendorSupplierID(scsDataSource, vendorPrefNodeList.get(0).getAttributes().getNamedItem("Code").getTextContent()));
        }

        //Date Location - locationID need to be mapped for offairport search
        final Node vehRentalCoreNode = PojoXmlUtil.getNodeByTagName(request, "VehRentalCore");
        Long supplierIDForLocationIDMap = 0L;
        if (this.carSearchCriteria.getVendorSupplierIDList().getVendorSupplierID().size() == 1){
            supplierIDForLocationIDMap = this.carSearchCriteria.getVendorSupplierIDList().getVendorSupplierID().get(0);
        }
        readSearchReqDateLocation(this.carSearchCriteria, vehRentalCoreNode, scsDataSource, supplierIDForLocationIDMap);
        readCostAvailReqDateLocation(carProduct, vehRentalCoreNode, scsDataSource, supplierIDForLocationIDMap); //For costAvail

        //Read PickUpLoationCode and ReturnLocationCode
        this.pickUpLocationCode = PojoXmlUtil.getNodeByTagName(vehRentalCoreNode, "PickUpLocation").getAttributes().getNamedItem("LocationCode").getTextContent();
        this.returnLocationCode = PojoXmlUtil.getNodeByTagName(vehRentalCoreNode, "ReturnLocation").getAttributes().getNamedItem("LocationCode").getTextContent();

        //Read CarRate
        final List<Node> rateQualifierNode = PojoXmlUtil.getNodesByTagName(request,"RateQualifier");
        if (!rateQualifierNode.isEmpty() && rateQualifierNode.get(0).getAttributes().getNamedItem("RateQualifier") != null)
        {
            this.carSearchCriteria.getCarRate().setRateCode(rateQualifierNode.get(0).getAttributes().getNamedItem("RateQualifier").getTextContent());
            this.carProduct.getCarInventoryKey().getCarRate().setRateCode(rateQualifierNode.get(0).getAttributes().getNamedItem("RateQualifier").getTextContent());
        }
        if (!rateQualifierNode.isEmpty() && rateQualifierNode.get(0).getAttributes().getNamedItem("CorpDiscountNmbr") != null)
        {
            this.carSearchCriteria.getCarRate().setCorporateDiscountCode(rateQualifierNode.get(0).getAttributes().getNamedItem("CorpDiscountNmbr").getTextContent());
            this.carProduct.getCarInventoryKey().getCarRate().setCorporateDiscountCode(rateQualifierNode.get(0).getAttributes().getNamedItem("CorpDiscountNmbr").getTextContent());
        }

        //Read ISOCountry
        this.isoCountry = PojoXmlUtil.getNodeByTagName(request, "Source").getAttributes().getNamedItem("ISOCountry").getTextContent();

        //Read primaryLangID
        if (request.getAttributes().getNamedItem("PrimaryLangID") == null){
            this.primaryLangID = null;
        }
        else{
            this.primaryLangID = request.getAttributes().getNamedItem("PrimaryLangID").getTextContent();
        }

        //Read SupplierItemMap
        this.supplierItemMaps = readSupplierItemMap(request, scsDataSource, vendorPrefNodeList);
    }

    /*<RateQualifiers Code="8" CodeContext="SUPPLIER_ID">
    <RateQualifier Code="TestAccountNo" CodeContext="ACCOUNT_NO"></RateQualifier>
    <RateQualifier Code="TestIATA" CodeContext="IATA"></RateQualifier>
    <RateQualifier Code="TestRestStation" CodeContext="RES_STATION_ID"></RateQualifier>
    <RateQualifier Code="TestContractID" CodeContext="CONTRACT_ID"></RateQualifier>
    </RateQualifiers>
    </TPA_Extensions>*/
    private List<SupplierItemMap> readSupplierItemMap(Node request, CarsSCSDataSource scsDataSource, List<Node> vendorPrefNodeList) throws DataAccessException {
        final List<SupplierItemMap> supplierItemMaps = new ArrayList<SupplierItemMap>();
        //Read SupplierItemMap - rateIdentifierKeys from RateQualifier
        final List<Node> rateQualifierNodes = PojoXmlUtil.getNodesByTagName(request,"RateQualifier");
        for(final Node rateQualifierNode : rateQualifierNodes)
        {
            final SupplierItemMap supplierItemMap = new SupplierItemMap();
            supplierItemMap.setItemKey(rateQualifierNode.getAttributes().getNamedItem("CodeContext").getTextContent());
            supplierItemMap.setItemValue(rateQualifierNode.getAttributes().getNamedItem("Code").getTextContent());
            supplierItemMaps.add(supplierItemMap);
        }
        //Read nonRateIdentifierKeys-ContractType from vendor code
        if(!vendorPrefNodeList.isEmpty()) {
            final String exVendorCode = vendorPrefNodeList.get(0).getAttributes().getNamedItem("Code").getTextContent();
            final List<ExternalSupplyServiceDomainValueMap> extendedVendorList = scsDataSource.
                    getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.CAR_VENDOR_EXTENDED, null, exVendorCode);

            if (!extendedVendorList.isEmpty()) {
                final SupplierItemMap supplierItemMap = new SupplierItemMap();
                supplierItemMap.setItemKey("ContractType");
                supplierItemMap.setItemValue(extendedVendorList.get(0).getDomainValue().split(":")[1]);
                supplierItemMaps.add(supplierItemMap);
            }
        }

        return supplierItemMaps;
    }

    public CarSearchCriteriaType getCarSearchCriteria() {
        return this.carSearchCriteria;
    }

    public void setCarSearchCriteria(CarSearchCriteriaType carSearchCriteria) {
        this.carSearchCriteria = carSearchCriteria;
    }


    public CarProductType getCarCarProduct() {
        return this.carProduct;
    }

    public void setCarCarProduct(CarProductType carProduct) {
        this.carProduct = carProduct;
    }

    public boolean isCCGuarantee() {
        return this.ccGuarantee;
    }

    public void setCCGuarantee(boolean ccGuarantee) {
        this.ccGuarantee = ccGuarantee;
    }


    public String getTourCode() {
        return this.tourCode;
    }

    public void setTourCode(String tourCode) {
        this.tourCode = tourCode;
    }

    public List<String> getTourCodeList() {
        return this.tourCodeList;
    }

    public void setTourCodeList(List<String> tourCodeList) {
        this.tourCodeList = tourCodeList;
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

    public String getPickUpLocationCode() {
        return this.pickUpLocationCode;
    }

    public void setPickUpLocationCode(String pickUpLocationCode) {
        this.pickUpLocationCode = pickUpLocationCode;
    }

    public String getReturnLocationCode() {
        return this.returnLocationCode;
    }

    public void setReturnLocationCode(String returnLocationCode) {
        this.returnLocationCode = returnLocationCode;
    }

    public List<SupplierItemMap> getSupplierItemMaps() {
        return supplierItemMaps;
    }

    public void setSupplierItemMaps(List<SupplierItemMap> supplierItemMaps) {
        this.supplierItemMaps = supplierItemMaps;
    }

}

