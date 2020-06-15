package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.framework.test.common.utils.HttpMessageSendUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yyang4 on 6/21/2018.
 */
public class VerifyProductTokenSearch
{
    private VerifyProductTokenSearch() {
    }

    public static void carInventoryKeyCompareVerifier(String  guid, CarProductType originalProduct, HttpClient httpClient) throws Exception
    {
        final long timeOut = 500;
        final CarInventoryKeyType originalCarIk = originalProduct.getCarInventoryKey();
        final String siteName = carProductTokenUriGet();
        final String encodeUri = String.format("https://%s/token/encode", siteName);
        final String decodeUri = String.format("https://%s/token/decode?token=", siteName);

        //encode
        String document = PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(originalCarIk));
        if (siteName.contains("cars-business-service")){
            document = document.replaceAll("urn:com", "urn");
        }
        final BytesContentProvider content = new BytesContentProvider(document.getBytes());
        final HttpMethod post = HttpMethod.POST;
        final HttpMessageSendUtil httpUtilEncode = new HttpMessageSendUtil(httpClient, encodeUri, createHeaders(post, guid), post.asString(), content, timeOut);
        final ContentResponse responseEncode = httpUtilEncode.sendHttpMessage();
        final String tokenRep = responseEncode.getContentAsString();


        //decode
        final String sendUri = decodeUri + tokenRep;
        final HttpMethod get = HttpMethod.GET;
        final HttpMessageSendUtil httpUtilDecode = new HttpMessageSendUtil(httpClient, sendUri, createHeaders(get, guid),
                get.asString(), null, timeOut);
        final ContentResponse responseDecode = httpUtilDecode.sendHttpMessage();
        String responseStr = responseDecode.getContentAsString();
        responseStr = responseStr.replaceAll("urn:com", "urn");

        final Document docResponse = PojoXmlUtil.stringToXml(responseStr);
        final CarInventoryKeyType newCarIK = PojoXmlUtil.docToPojo(docResponse,
                CarInventoryKeyType.class);
        final CarProductType newProduct = new CarProductType();
        newProduct.setCarInventoryKey(newCarIK);
        if(! CarProductComparator.isCorrespondingCar(originalProduct, newProduct, true, true)){
            Assert.fail(" The actual car product info is not equal the expected. ");
        }

    }


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
    public static String carProductTokenUriGet()
    {
        String carBSUri = SettingsProvider.SERVICE_ADDRESS;
        if(carBSUri.contains("https"))
        {
            carBSUri = carBSUri.substring(8).split("\\/")[0];
        }else
        {
            carBSUri = carBSUri.substring(7).split("\\/")[0];
        }
        return carBSUri;
    }
}
