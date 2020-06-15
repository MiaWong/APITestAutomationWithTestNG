package com.expedia.s3.cars.ecommerce.carbs.service.commonutils;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.utils.HttpMessageSendUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.axis.utils.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.Assert;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fehu on 9/26/2016.
 */
public class CommonUtil {
    private  CommonUtil() {
    }

    public static void notNullErrorMsg(String errorMsg)
    {
        if (!StringUtils.isEmpty(errorMsg))
        {
            Assert.fail(errorMsg);
        }
    }

    public static Integer getClientIDbyCode(String clientCode)
    {
        if (CarCommonConstantManager.ONE_FX936.equals(clientCode))
        {
            return 1;
        } else if (CarCommonConstantManager.QGPDJ8.equals(clientCode))
        {
            return 2;
        } else if (CarCommonConstantManager.W0DFCJ.equals(clientCode))
        {
            return 3;
        } else if (CarCommonConstantManager.S7JWZD.equals(clientCode))
        {
            return 4;
        } else if (CarCommonConstantManager.ZERO_Q7XRN.equals(clientCode))
        {
            return 5;
        } else if (CarCommonConstantManager.RT348B.equals(clientCode))
        {
            return 6;
        }else if (CarCommonConstantManager.ZCS52L.equals(clientCode))
        {
            return 7;
        }
        else
        {
            return null;
        }
    }

    public static CarInventoryKeyType decodeProductToken(String token) throws Exception {
        final HttpClient httpClient = new HttpClient(new SslContextFactory(true));
        httpClient.start();
        final String sendUri = SettingsProvider.SERVICE_ADDRESS.replace("restservice", "token/decode?token=") + token;
        final HttpMethod get = HttpMethod.GET;
        final HttpMessageSendUtil httpUtilDecode = new HttpMessageSendUtil(httpClient, sendUri, null,
                get.asString(), null, 3000);
        final ContentResponse responseDecode = httpUtilDecode.sendHttpMessage();
        String responseStr = responseDecode.getContentAsString();
        responseStr = responseStr.replaceAll("urn:com", "urn");

        final Document docResponse = PojoXmlUtil.stringToXml(responseStr);
        httpClient.stop();

       return PojoXmlUtil.docToPojo(docResponse,
                CarInventoryKeyType.class);

    }

   /* public static CarInventoryKeyType encodeProductToken(String token)
    {

    }*/

    public static Map<String,String> createHeaders (HttpMethod method, String guid){
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("Connection", "Keep-Alive");
        headers.put("Content-Type", "application/xml");
        if(HttpMethod.POST.equals(method))
        {
            headers.put("Accept","application/xml");
        }
        // E3JMS-L-activityId
        // Note: If need Spoofer,we all need to add OriginalGUID in request header.
        headers.put("e3jms-l-activityId-propname", "activityId");
        headers.put("E3JMS-L-activityId", guid);
        return headers;
    }

    public static void setVendor(TestData testData, String vendorCode)
    {
        final TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode(vendorCode);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
    }
}
