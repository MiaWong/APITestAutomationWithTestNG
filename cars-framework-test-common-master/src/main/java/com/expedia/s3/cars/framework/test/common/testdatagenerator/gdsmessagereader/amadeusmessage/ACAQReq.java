package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miawang on 12/5/2016.
 */
public class ACAQReq {
    final private List<String> pictureSizes = new ArrayList<>();
    final private Map<Long, String> vendorDiscountNumsMap = new HashMap();
    final private Map<String, List<String>> vendorLocMap = new HashMap<>();

    @SuppressWarnings("PMD")
    public ACAQReq()
    {
    }

    /**
     *
     */
    public ACAQReq(Node request) throws DataAccessException
    {
        buildVendorLocMap (request);
    }

    public ACAQReq(Node request, CarsSCSDataSource scsDataSource) throws DataAccessException {
        //Get picture size
        final List<Node> picSizeNodes = PojoXmlUtil.getNodesByTagName(request, "pictureSize");
        for (final Node picSizeNode : picSizeNodes) {
            final String picSize = PojoXmlUtil.getNodeByTagName(picSizeNode, "option").getTextContent();
            pictureSizes.add(picSize);
        }

        buildDiscountNumList(request, scsDataSource);

        buildVendorLocMap(request);
    }

    private void buildDiscountNumList (Node request, CarsSCSDataSource scsDataSource) throws DataAccessException
    {
        //Load XML from input String
        //VendorCode
        final List<Node> providerSpecificOptions = PojoXmlUtil.getNodesByTagName(request, "providerSpecificOptions");
        for (final Node providerSpecificOption : providerSpecificOptions)
        {
            final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            vendorDiscountNumsMap.putAll(commonNodeReader.buildDiscountNumList(providerSpecificOption, scsDataSource,
                    "loyaltyNumbersList", "customerReferenceInfo"));
        }
    }

    public List<String> getPictureSizes()
    {
        return pictureSizes;
    }

    public Map<Long, String> getVendorDiscountNumsMap()
    {
        return vendorDiscountNumsMap;
    }

    public void buildVendorLocMap (Node request) throws DataAccessException
    {
        //Load XML from input String
        final List<Node> providerSpecificOptions = PojoXmlUtil.getNodesByTagName(request, "providerSpecificOptions");
        for (final Node providerSpecificOption : providerSpecificOptions)
        {
            final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            vendorLocMap.putAll(commonNodeReader.buildVendorLocMap(providerSpecificOption));
        }
    }

    public Map<String, List<String>> getVendorLocMap()
    {
        return vendorLocMap;
    }
}