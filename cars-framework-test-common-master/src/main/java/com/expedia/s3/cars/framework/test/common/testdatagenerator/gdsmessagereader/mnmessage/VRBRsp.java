package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.commonreader.VehAvailNodeHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarCatalogMakeModelReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 12/25/2016.
 */
public class VRBRsp {

    final private CarProductType carProductType;
    final private ReferenceListType referenceListType;

    public ReferenceListType getReferenceListType() {
        return referenceListType;
    }

    public CarProductType getCarProductType() {
        return carProductType;
    }

    public VRBRsp(Node nodeObject, CarsSCSDataSource scsDataSource) throws Exception {
        this.carProductType = getCarproduct(nodeObject, scsDataSource);
        this.referenceListType = getReferenceListType(nodeObject);
    }

    public ReferenceListType getReferenceListType(Node nodeObject)
    {
        final ReferenceListType referenceListType  = new ReferenceListType();
        final  List<ReferenceType> referenceTypeList = new ArrayList<>();
        referenceListType.setReference(referenceTypeList);
        final ReferenceType referenceTypeForPNR = new ReferenceType();
        referenceTypeForPNR.setReferenceCategoryCode("PNR");
        referenceTypeForPNR.setReferenceCode(PojoXmlUtil.getNodeByTagName(nodeObject ,"ConfID").getAttributes().getNamedItem("ID_Context").getTextContent());
        referenceTypeList.add(referenceTypeForPNR);
        final ReferenceType referenceTypeForVendor = new ReferenceType();
        referenceTypeForVendor.setReferenceCategoryCode("Vendor");
        referenceTypeForVendor.setReferenceCode(PojoXmlUtil.getNodeByTagName(nodeObject ,"Vendor").getTextContent());
        referenceTypeList.add(referenceTypeForVendor);
          return referenceListType;
    }

    public CarProductType getCarproduct(Node nodeObject, CarsSCSDataSource scsDataSource) throws Exception {
        final CarProductType carProductType = new CarProductType();

        //CarInventory Key
        final CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
        carProductType.setCarInventoryKey(carInventoryKey);

        //set CarinventoryKey
        VehAvailNodeHelper.setCarInventoryKey(nodeObject, scsDataSource, carInventoryKey);


        //set CarRate
        VehAvailNodeHelper.setCarRate(nodeObject, carInventoryKey);


        //CarCatalogMakeModel
        carProductType.setCarCatalogMakeModel(CarCatalogMakeModelReader.readCarCatalogMakeModel(nodeObject));
        //Based on CASSS-10368 Micronnexus : Show Number of Doors from GDS for both Min and Max, if min door count not exist, get min door count from max door count
        if(carProductType.getCarCatalogMakeModel().getCarMinDoorCount() == 0)
        {
            carProductType.getCarCatalogMakeModel().setCarMinDoorCount(carProductType.getCarCatalogMakeModel().getCarMaxDoorCount());
        }

        //CostList
        //VehAvailNodeHelper.costList(nodeObject, carProductType, scsDataSource);
        //costList
        VehAvailNodeHelper.detailsCostList(nodeObject, carProductType, scsDataSource);


        return  carProductType;
    }


}
