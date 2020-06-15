package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Arrays;

/**
 * Created by aaniyath on 07-10-2016.
 */
@SuppressWarnings("PMD")
public class VerifyCarProductCountWithDownstreamResponse implements ISearchVerification
{
    private static final String MESSAGE_SUCCESS = "Success";
    private static final String MESSAGE_CAR_COUNT_MISMATCH = "Mismatch in car count in scs response and downstream response";

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext) {
        return null != input && null != input.getResponse() && null != ((CarSupplyConnectivitySearchResponseType)input.getResponse()).getCarSearchResultList() && null != ((CarSupplyConnectivitySearchResponseType)input.getResponse()).getCarSearchResultList().getCarSearchResult();
    }

    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        String errorMessage = "";

        errorMessage = verifyCarProductCountWithDownstreamResponse(input.getResponse(), verificationContext);
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new IVerification.VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    public String verifyCarProductCountWithDownstreamResponse(CarSupplyConnectivitySearchResponseType response,
                                                          BasicVerificationContext verificationContext)
    {
        String scenarioPickUpLocationCode = verificationContext.getScenario().getPickupLocationCode();
        String scenarioDropOffLocationCode = verificationContext.getScenario().getDropOffLocationCode();

        int carCountInSCSResponse=0;
        for(CarSearchResultType carSearchResultType: response.getCarSearchResultList().getCarSearchResult())
        {
            for(CarProductType carProductType: carSearchResultType.getCarProductList().getCarProduct())
            {
                CarCatalogKeyType carCatergoryKey = carProductType.getCarInventoryKey().getCarCatalogKey();
                if (carCatergoryKey.getCarPickupLocationKey().getLocationCode().equals(scenarioPickUpLocationCode) &&
                        carCatergoryKey.getCarDropOffLocationKey().getLocationCode().equals(scenarioDropOffLocationCode))
                {
                    carCountInSCSResponse++;
                }
            }

        }

        int carCountInDownstreamResponse = 0;
        NodeList nodeList  = verificationContext.getSpooferTransactions().getElementsByTagName("OTA_VehAvailRateRS");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Element eElement = (Element) nodeList.item(i);
            if (getLocationCodeFromDownStream(eElement, "LocationDetails").equals(scenarioPickUpLocationCode)
                    && getLocationCodeFromDownStream(eElement, "DropOffLocationDetails").equals(scenarioDropOffLocationCode))
            {
                carCountInDownstreamResponse = carCountInDownstreamResponse + eElement.getElementsByTagName("VehVendorAvail").getLength();
            }
        }

        if(carCountInDownstreamResponse != carCountInSCSResponse) {
            return MESSAGE_CAR_COUNT_MISMATCH;
        }

        return null;
    }

    public String getLocationCodeFromDownStream(Element eElement, String type)
    {
        return eElement.getElementsByTagName(type)
                .item(0)
                .getAttributes()
                .getNamedItem("LocationCode")
                .getTextContent();
    }

}
