package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by miawang on 1/12/2017.
 */
public class ARIAReq
{
    public Map<Long, String> buildVendorDiscountNums(Node request, CarsSCSDataSource amadeusSCSDataSource) throws DataAccessException {
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        return commonNodeReader.buildDiscountNumList(request, amadeusSCSDataSource, null, null);
    }
}