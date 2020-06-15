package com.expedia.s3.cars.supply.service.verification;

import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.BasicKeys;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.TP95Keys;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.expectedvalues.DatalogExpValues;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;

/**
 * Created by v-mechen on 11/26/2017.
 */
public class PerfMetricsVerifier {
    private PerfMetricsVerifier()
    {}

    public static void verifyPerfMetrics(AuditLogTrackingDataType trackingData, PointOfSaleKeyType pointOfSaleKey,
                                         Document rsp, Object errorCollectionObj, String actionType,
                                         List<Map> splunkResult)
    {
        //Get the common keys and values need to be logged
        final Map<String, String> expValues = DatalogExpValues.getExpCommonTP95Values(trackingData,
                pointOfSaleKey);
        expValues.put(TP95Keys.ISSUCCESSFUL, "true");
        expValues.put(TP95Keys.HASSOFTERRORS, getHasSoftErros(errorCollectionObj));

        //get actual values
        Map<String, String> actValues = null;
        for(final Map splunk : splunkResult)
        {
            if(splunk.containsKey(BasicKeys.ACTIONTYPE) && splunk.get(BasicKeys.ACTIONTYPE).equals(actionType))
            {
                actValues = splunk;
            }
        }

        //Compare result
        if(actValues == null || actValues.isEmpty())
        {
            Assert.fail("Fail to get log from splunk.");
        }
        else
        {
            final String compareResult = CompareUtil.compareSplunkMap(expValues, actValues);
            if(!compareResult.trim().isEmpty())
            {
                Assert.fail(compareResult);
            }

        }


    }

    public static String getHasSoftErros(Object errorCollectionObj)
    {
        NodeList descriptionRawTextList = null;
        if (null != errorCollectionObj)
        {
            descriptionRawTextList = PojoXmlUtil.pojoToDoc(errorCollectionObj).getElementsByTagNameNS("*","DescriptionRawText");
        }

        if(null == descriptionRawTextList || descriptionRawTextList.getLength() == 0)
        {
            return "false";
        }
        else
        {
            return "true";
        }
    }
}
