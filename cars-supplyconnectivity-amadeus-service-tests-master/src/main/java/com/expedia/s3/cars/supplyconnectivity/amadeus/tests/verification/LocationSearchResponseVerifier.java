package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators.TestScenarios;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.w3c.dom.Document;

import java.util.List;


/**
 * Created by alibadisos on 10/7/16.
 */
public class LocationSearchResponseVerifier
{
    private TestScenarios scenarios;
    private Document downstreamMessages;
    private CarSupplyConnectivityLocationSearchRequestType request;
    private CarSupplyConnectivityLocationSearchResponseType response;

    public void setScenarios(TestScenarios scenarios)
    {
        this.scenarios = scenarios;
    }

    public void setResponse(CarSupplyConnectivityLocationSearchResponseType response)
    {
        this.response = response;
    }

    public void setRequest(CarSupplyConnectivityLocationSearchRequestType request)
    {
        this.request = request;
    }

    public void setDownstreamMessages(Document downstreamMessages)
    {
        this.downstreamMessages = downstreamMessages;
    }

    public LocationSearchResponseVerifier(TestScenarios scenarios,
                                          CarSupplyConnectivityLocationSearchRequestType request,
                                          CarSupplyConnectivityLocationSearchResponseType response,
                                          Document downstreammessages)
    {
        this.scenarios = scenarios;
        this.request = request;
        this.response = response;
        this.downstreamMessages = downstreammessages;
    }

    public static void verifyCarLocationReturned(CarSupplyConnectivityLocationSearchRequestType request,
                                                 CarSupplyConnectivityLocationSearchResponseType response)
    {
        //verify car product returned
        StringBuilder errorMsg = new StringBuilder("");
        boolean matchedCarLocationReturned = false;
        if (null == response)
        {
            errorMsg.append("search Response is null.");
        }
        else
        {
            if (null == response.getCarLocationList() || CollectionUtils.isEmpty(response.getCarLocationList().getCarLocation()))
            {
                errorMsg.append("No car locations return in response.");
            } else {
                for (CarLocationType result : response.getCarLocationList().getCarLocation() )
                {
                    if (null != result.getCarLocationKey())
                    {
                        matchedCarLocationReturned = true;
                        break;
                    }
                }
            }
            if (!matchedCarLocationReturned)
            {
                errorMsg.append("No Car location returned in CarSCS response.");
            }
            else
            {
                if ( null != request.getNumberOfItems() && request.getNumberOfItems() > 0 )
                {
                    if ( response.getNumberOfItemsRemaining() > 0 )
                    {
                        // we only return locations for supported suppliers, so the number of items in the response is less than or equal to the number requested
                        Assert.assertTrue(request.getNumberOfItems().intValue() >=
                                response.getCarLocationList().getCarLocation().size(),
                            "Expect the number of requested items to be greater than or equal to the number of returned items" );
                    }
                    if ( response.getNumberOfItemsRemaining() == 0 )
                    {
                        Assert.assertTrue(request.getNumberOfItems() >=
                                response.getCarLocationList().getCarLocation().size());
                    }
                }
            }

            if (null != response.getErrorCollection())
            {
                List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(response.getErrorCollection(), "DescriptionRawText");
                if (!CollectionUtils.isEmpty(descriptionRawTextList))
                {
                    errorMsg.append("ErrorCollection is present in response");
                    descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
                }
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }
}
