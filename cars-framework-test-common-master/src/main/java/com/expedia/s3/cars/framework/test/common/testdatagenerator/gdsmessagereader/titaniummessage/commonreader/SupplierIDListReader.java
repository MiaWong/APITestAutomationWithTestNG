package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.basetypes.defn.v4.VendorSupplierIDListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/7/2016.
 */
public class SupplierIDListReader {
    private SupplierIDListReader(){

    }
    //Read VendorSupplierIDList
    public static VendorSupplierIDListType readVendorSupplierIDList(CarsSCSDataSource scsDataSource, List<Node> vendorNodeList) throws DataAccessException {
        final VendorSupplierIDListType vendorSupplierIDList = new VendorSupplierIDListType();
        vendorSupplierIDList.setVendorSupplierID(new ArrayList<Long>());
        for (int i = 0; i < vendorNodeList.size(); i++)
        {
            if (!StringUtils.isEmpty(vendorNodeList.get(i).getAttributes().getNamedItem("Code").getNodeValue())){
                vendorSupplierIDList.getVendorSupplierID().add(GDSMsgReadHelper.readVendorSupplierID(scsDataSource,
                        vendorNodeList.get(i).getAttributes().getNamedItem("Code").getTextContent()));
            }
        }
        return vendorSupplierIDList;
    }
}
