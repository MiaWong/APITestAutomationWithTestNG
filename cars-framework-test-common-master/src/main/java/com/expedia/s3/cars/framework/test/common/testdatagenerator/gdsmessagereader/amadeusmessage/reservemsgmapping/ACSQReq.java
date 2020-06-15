package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by miawang on 1/23/2017.
 */
public class ACSQReq {

    public Map<Long, String> buildVendorDiscountNums(Node request, CarsSCSDataSource amadeusSCSDataSource) throws DataAccessException {
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        return commonNodeReader.buildDiscountNumList(request, amadeusSCSDataSource, null, null);
    }

    public void buildCarInventory(CarProductType carProduct, Node nodeAcsqCarSellReq, CarsSCSDataSource scsDataSource) throws DataAccessException {
        //nodeAcsqCarSellReq
        if (null == carProduct.getCarInventoryKey()) {
            carProduct.setCarInventoryKey(new CarInventoryKeyType());
        }
        if(null == carProduct.getCarInventoryKey().getCarCatalogKey())
        {
            carProduct.getCarInventoryKey().setCarCatalogKey(new CarCatalogKeyType());
        }

        final Node sellDataReqNode = PojoXmlUtil.getNodeByTagName(nodeAcsqCarSellReq, "sellData");

        final Node carCompanyDataNode = PojoXmlUtil.getNodeByTagName(sellDataReqNode, "companyIdentification");

        /// 1.Get VendorSupplierID
        carProduct.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(
                GDSMsgReadHelper.readVendorSupplierID(scsDataSource, PojoXmlUtil.getNodeByTagName(carCompanyDataNode, "companyCode").getTextContent()));

        /// 2.Get CarVehicle
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readCarVehicle(carProduct.getCarInventoryKey(), PojoXmlUtil.getNodeByTagName(sellDataReqNode, "vehicleInformation"), scsDataSource, false);

        // 3.Get CarPickupLocationKey // 4.Get CarDropOffLocationKey
        commonNodeReader.readCarPickupAndDropOffLocationKey(carProduct.getCarInventoryKey(), sellDataReqNode, "locationInfo");
    }
}
