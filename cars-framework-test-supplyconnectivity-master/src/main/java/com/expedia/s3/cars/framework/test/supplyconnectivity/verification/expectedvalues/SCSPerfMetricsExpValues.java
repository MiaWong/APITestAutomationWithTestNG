package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues;

import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.constant.datalogkeys.TP95SCSGetDetailsKeys;
import com.expedia.s3.cars.framework.test.supplyconnectivity.constant.datalogkeys.TP95SCSKeysExceptSearch;
import com.expedia.s3.cars.framework.test.supplyconnectivity.constant.datalogkeys.TP95SCSReserveKeys;
import com.expedia.s3.cars.framework.test.supplyconnectivity.constant.datalogkeys.TP95SCSSearchKeys;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.SCSSearchUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Map;

import static com.expedia.s3.cars.framework.test.common.verification.expectedvalues.DatalogExpValues.getExpCommonTP95Values;

/**
 * Created by v-mechen on 7/2/2017.
 */
@SuppressWarnings("PMD")
public class SCSPerfMetricsExpValues {
    /*

     */

    private SCSPerfMetricsExpValues()
    {}

    public static Map<String, String> getExpTP95SCSGetDetailsValues(CarSupplyConnectivityGetDetailsRequestType detailRequest,
                                                                                CarSupplyConnectivityGetDetailsResponseType detailResponse,
                                                                                Document spooferTransactions, String gdsMessageName)
    {
        //Get the common values need to be logged
        final PointOfSaleKeyType pointOfSaleKey = detailRequest.getPointOfSaleKey();
        final CarProductType reqCar = detailRequest.getCarProductList().getCarProduct().get(0);
        final CarProductType rspCar = (null == detailResponse.getCarProductList() || null == detailResponse.getCarProductList().getCarProduct() ||
                detailResponse.getCarProductList().getCarProduct().isEmpty()) ? null : detailResponse.getCarProductList().getCarProduct().get(0);
        final Map<String, String> expectedValues = getExpTP95SCSCommonValuesExceptSearch(pointOfSaleKey, reqCar, rspCar,
                detailRequest.getAuditLogTrackingData(), spooferTransactions, gdsMessageName, detailResponse.getErrorCollection());

        //Get the values for SCS GetDetails
        final String policyCategoryCount = (null == detailRequest.getCarPolicyCategoryCodeList() || null == detailRequest.getCarPolicyCategoryCodeList().getCarPolicyCategoryCode() ||
                detailRequest.getCarPolicyCategoryCodeList().getCarPolicyCategoryCode().isEmpty()) ? null : String.valueOf(detailRequest.getCarPolicyCategoryCodeList().getCarPolicyCategoryCode().size());
        expectedValues.put(TP95SCSGetDetailsKeys.POLICYCATEGORYCOUNT, policyCategoryCount);

        return expectedValues;

    }

    public static Map<String, String> getExpTP95SCSCostAvailValues(CarSupplyConnectivityGetCostAndAvailabilityRequestType request,
                                                                   CarSupplyConnectivityGetCostAndAvailabilityResponseType response,
                                                                   Document spooferTransactions, String gdsMessageName)
    {
        //Get the common values need to be logged
        final PointOfSaleKeyType pointOfSaleKey = request.getPointOfSaleKey();
        final CarProductType reqCar = request.getCarProductList().getCarProduct().get(0);
        final CarProductType rspCar = (null == response.getCarProductList() || null == response.getCarProductList().getCarProduct() ||
                response.getCarProductList().getCarProduct().isEmpty()) ? null : response.getCarProductList().getCarProduct().get(0);
        final Map<String, String> expectedValues = getExpTP95SCSCommonValuesExceptSearch(pointOfSaleKey, reqCar, rspCar,
                request.getAuditLogTrackingData(), spooferTransactions, gdsMessageName, response.getErrorCollection());

        return expectedValues;
    }

    public static Map<String, String> getExpTP95SCSReserveValues(CarSupplyConnectivityReserveRequestType request,
                                                                             CarSupplyConnectivityReserveResponseType response,
                                                                             Document spooferTransactions, String gdsMessageName,
                                                                 String... paymentType)
    {
        //Get the common values need to be logged
        final PointOfSaleKeyType pointOfSaleKey = request.getPointOfSaleKey();
        final CarProductType reqCar = request.getCarProduct();
        final CarProductType rspCar = (null == response.getCarReservation() || null == response.getCarReservation().getCarProduct() ) ?
                null : response.getCarReservation().getCarProduct();
        final Map<String, String> expectedValues = getExpTP95SCSCommonValuesExceptSearch(pointOfSaleKey, reqCar, rspCar,
                request.getAuditLogTrackingData(), spooferTransactions, gdsMessageName, response.getErrorCollection());

        //Get the values for SCS Reserve
        final String wasCCRequired = (null == request.getCreditCardFormOfPayment() || null == request.getCreditCardFormOfPayment().getCreditCard() ||
                StringUtils.isEmpty(request.getCreditCardFormOfPayment().getCreditCard().getCreditCardNumberEncrypted())) ? "false" : "true";
        expectedValues.put(TP95SCSReserveKeys.WASCCREQUIRED, wasCCRequired);

        //PaymentType
        if (null != paymentType && paymentType.length > 0 && !StringUtils.isEmpty(paymentType[0]))
        {
            expectedValues.put(TP95SCSReserveKeys.PAYMENTTYPE, paymentType[0]);
        }

        return expectedValues;
    }

    public static Map<String, String> getExpTP95SCSGetReservationValues(CarSupplyConnectivityGetReservationRequestType request,
                                                                                    CarSupplyConnectivityGetReservationResponseType response,
                                                                                    Document spooferTransactions, String gdsMessageName)
    {
        //Get the common values need to be logged
        final PointOfSaleKeyType pointOfSaleKey = request.getPointOfSaleKey();
        final CarProductType reqCar = request.getCarReservationList().getCarReservation().get(0).getCarProduct();
        final CarProductType rspCar = (null == response.getCarReservationList() || null == response.getCarReservationList().getCarReservation() ||
                response.getCarReservationList().getCarReservation().isEmpty() || null == response.getCarReservationList().getCarReservation().get(0).getCarProduct()) ?
                null : response.getCarReservationList().getCarReservation().get(0).getCarProduct();
        final Map<String, String> expectedValues = getExpTP95SCSCommonValuesExceptSearch(pointOfSaleKey, reqCar, rspCar,
                request.getAuditLogTrackingData(), spooferTransactions, gdsMessageName, response.getErrorCollection());

        return expectedValues;
    }

    public static Map<String, String> getExpTP95SCSCancelValues(CarSupplyConnectivityCancelRequestType request, CarSupplyConnectivityCancelResponseType response,
                                                                            Document spooferTransactions, String gdsMessageName)
    {
        //Get the common values need to be logged
        final PointOfSaleKeyType pointOfSaleKey = request.getPointOfSaleKey();
        final CarProductType reqCar = request.getCarReservation().getCarProduct();
        final CarProductType rspCar = (null == response.getCarReservation() || null == response.getCarReservation().getCarProduct())
                ? null : response.getCarReservation().getCarProduct();
        final Map<String, String> expectedValues = getExpTP95SCSCommonValuesExceptSearch(pointOfSaleKey, reqCar, rspCar,
                request.getAuditLogTrackingData(), spooferTransactions, gdsMessageName, response.getErrorCollection());

        return expectedValues;
    }


    public static Map<String, String> getExpTP95SCSCommonValuesExceptSearch(PointOfSaleKeyType pointOfSaleKey, CarProductType reqCar,
                                                                            CarProductType rspCar, AuditLogTrackingDataType trackingData,
                                                                            Document spooferTransactions, String gdsMessageName,
                                                                            Object errorCollectionObj)
    {
        //Get the common values need to be logged
        final Map<String, String> expectedValues = getExpCommonTP95Values(trackingData, pointOfSaleKey);
        //Get the values for SCS message except search
        //hadDownStreamCalls
        final NodeList crsDataList = spooferTransactions.getElementsByTagNameNS("*", gdsMessageName);
        final String hadDownStreamCalls = crsDataList.getLength() > 0 ? "true" : null;
        expectedValues.put(TP95SCSKeysExceptSearch.HADDOWNSTREAMCALLS, hadDownStreamCalls);
        //requestVendorIDs
        final String requestVendorIDs = CarProductUtils.getVendorIDs(reqCar);
        expectedValues.put(TP95SCSKeysExceptSearch.REQUESTVENDORIDS, requestVendorIDs);

        //add Location Keys To Expected Values Except Search
        addLocationKeysToExpValuesExceptSearch(expectedValues, reqCar);

        //responseVendorIDs
        final String responseVendorIDs = CarProductUtils.getVendorIDs(rspCar);
        expectedValues.put(TP95SCSKeysExceptSearch.RESPONSEVENDORIDS, responseVendorIDs);
        //responseCarCount
        final String responseCarCount = CarProductUtils.getCarCount(rspCar);
        expectedValues.put(TP95SCSKeysExceptSearch.RESPONSECARCOUNT, responseCarCount);
        //isSuccessful
        final String isSuccessful = StringUtils.isEmpty(responseCarCount) ? "false" : "true";
        expectedValues.put(TP95SCSKeysExceptSearch.ISSUCCESSFUL, isSuccessful);
        //PIIDS
        expectedValues.put(TP95SCSKeysExceptSearch.PIIDS, reqCar.getCarProductToken());
        //HASSOFTERRORS
        final String hasSoftErros = getHasSoftErros(errorCollectionObj);
        expectedValues.put(TP95SCSKeysExceptSearch.HASSOFTERRORS, hasSoftErros);

        //get CD code
        final String requestCDCodes = CarProductUtils.getRequestCDCodes(reqCar);
        //if (!(reqCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID() > 0) &&
               // (StringUtils.isEmpty(pickupLocations)))
        //{
            //requestCDCodes = null;
        //}
        expectedValues.put(TP95SCSKeysExceptSearch.REQUESTCDCODES, requestCDCodes);
        final String responseCDCodes = CarProductUtils.getResponseCDCodes(rspCar, isSuccessful);
        expectedValues.put(TP95SCSKeysExceptSearch.RESPONSECDCODES, responseCDCodes);

        //get request total cost and currency code
        final String requestTotalCost = CarProductUtils.getTotalCost(reqCar);
        final String requestTotalCostCurrency = CarProductUtils.getTotalCostCurrency(reqCar);
        final String responseTotalCost = CarProductUtils.getTotalCost(rspCar);
        final String responseTotalCostCurrency = CarProductUtils.getTotalCostCurrency(rspCar);
        if(null != requestTotalCostCurrency)
        {
            expectedValues.put(TP95SCSKeysExceptSearch.REQUESTTOTALCOST, requestTotalCost);
            expectedValues.put(TP95SCSKeysExceptSearch.REQUESTTOTALCOSTCURRENCY, requestTotalCostCurrency);
        }

        //get response total cost and currency code
        if(null != responseTotalCostCurrency)
        {
            expectedValues.put(TP95SCSKeysExceptSearch.RESPONSETOTALCOST, responseTotalCost);
            expectedValues.put(TP95SCSKeysExceptSearch.RESPONSETOTALCOSTCURRENCY, responseTotalCostCurrency);
        }
        if (gdsMessageName .equals(GDSMsgNodeTags.WorldSpanNodeTags.URRR_REQUEST_TYPE))
        {
            expectedValues.remove("RequestCDCodes");
            expectedValues.remove("ResponseCDCodes");
            expectedValues.remove("ResponseTotalCost");
            expectedValues.remove("ResponseTotalCostCurrency");
            expectedValues.remove("DropoffLocationCategoryFilter");
            expectedValues.remove("DropoffLocations");
        }
        if (gdsMessageName .equals(GDSMsgNodeTags.WorldSpanNodeTags.VCRQ_REQUEST_TYPE ))
        {
            expectedValues.remove("PickupLocationCategoryFilter");
            expectedValues.remove("DropoffLocationCategoryFilter");
            expectedValues.remove("RequestCDCodes");
            expectedValues.remove("ResponseCDCodes");
            expectedValues.remove("ResponseTotalCost");
            expectedValues.remove("ResponseTotalCostCurrency");
        }

        return expectedValues;
    }

    public static void addLocationKeysToExpValuesExceptSearch(Map<String, String> expectedValues, CarProductType car)
    {
        final String pickupLocationCategoryFilter = CarProductUtils.getPickupLocationCategoryFilter(car);
        expectedValues.put(TP95SCSKeysExceptSearch.PICKUPLOCATIONCATEGORYFILTER, pickupLocationCategoryFilter);
        final String dropoffLocationCategoryFilter = CarProductUtils.getDropoffLocationCategoryFilter(car);
        expectedValues.put(TP95SCSKeysExceptSearch.DROPOFFLOCATIONCATEGORYFILTER, dropoffLocationCategoryFilter);
        final String pickupLocations = car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode();
        if (!StringUtils.isEmpty(pickupLocations)) {
            expectedValues.put(TP95SCSKeysExceptSearch.PICKUPLOCATIONS, pickupLocations);
        }
        final String dropoffLocations = car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode();
        if (!StringUtils.isEmpty(dropoffLocations)) {
            expectedValues.put(TP95SCSKeysExceptSearch.DROPOFFLOCATIONS, dropoffLocations);
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


    public static Map<String, String> getExpTP95SCSSearchValues(CarSupplyConnectivitySearchRequestType carSCSSearchRequest,
                                                                CarSupplyConnectivitySearchResponseType carSCSSearchResponse,
                                                                Document spooferTransactions, String gdsMessageName)
    {
        //Get the common keys and values need to be logged
        final Map<String, String> expectedValues = getExpCommonTP95Values(carSCSSearchRequest.getAuditLogTrackingData(),
                carSCSSearchRequest.getPointOfSaleKey());
        //Get expected key values
        final String searchCriteriaCount = String.valueOf(carSCSSearchRequest.getCarSearchCriteriaList().getCarSearchCriteria().size());;
        expectedValues.put(TP95SCSSearchKeys.SEARCHCRITERIACOUNT, searchCriteriaCount);
        final String requestVendorIDs = SCSSearchUtil.getRequestVendorIDsFromSCSsearchRequest(carSCSSearchRequest);
        expectedValues.put(TP95SCSSearchKeys.REQUESTVENDORIDS, requestVendorIDs);
        final String pickupLocationCategoryFilter = SCSSearchUtil.getPickupLocationCategoryFilterFromSCSsearchRequest(carSCSSearchRequest);
        expectedValues.put(TP95SCSSearchKeys.PICKUPLOCATIONCATEGORYFILTER, pickupLocationCategoryFilter);
        final String dropoffLocationCategoryFilter = SCSSearchUtil.getDropoffLocationCategoryFilterFromSCSsearchRequest(carSCSSearchRequest);
        expectedValues.put(TP95SCSSearchKeys.DROPOFFLOCATIONCATEGORYFILTER, dropoffLocationCategoryFilter);
        final String pickupLocations = SCSSearchUtil.getPickupLocationsFromSCSsearchRequest(carSCSSearchRequest);
        expectedValues.put(TP95SCSSearchKeys.PICKUPLOCATIONS, pickupLocations);
        final String hasSoftErros = SCSSearchUtil.getHasSoftErrorsFromSCSSearchResponse(carSCSSearchResponse);
        expectedValues.put(TP95SCSSearchKeys.HASSOFTERRORS, hasSoftErros);
        final String responseVendorIDs = SCSSearchUtil.getResponsetVendorIDsFromSCSsearchResponse(carSCSSearchResponse);
        expectedValues.put(TP95SCSSearchKeys.RESPONSEVENDORIDS, responseVendorIDs);
        final String responseCarCount = SCSSearchUtil.getResponseCarCountFromSCSSearchResponse(carSCSSearchResponse);
        expectedValues.put(TP95SCSSearchKeys.RESPONSECARCOUNT, responseCarCount);
        final String isSuccessful = (null == responseCarCount) ? "false" : "true";
        expectedValues.put(TP95SCSSearchKeys.ISSUCCESSFUL, isSuccessful);
        final NodeList crsDataList = spooferTransactions.getElementsByTagNameNS("*", gdsMessageName);
        if (crsDataList.getLength() > 0)
        {
            expectedValues.put(TP95SCSSearchKeys.HADDOWNSTREAMCALLS, "true");
            if ("VehAvailRateRQ".equals(gdsMessageName))
            {
                expectedValues.put(TP95SCSSearchKeys.DOWNSTREAMMNCALLELAPSEDTIME_REQUEST1, "");
            }
            else if (CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST.equals(gdsMessageName)
                    || CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE.equals(gdsMessageName))
            {
                expectedValues.put(TP95SCSSearchKeys.DOWNSTREAMTICALLELAPSEDTIME_REQUEST1, "");
            }
            else if (GDSMsgNodeTags.WorldSpanNodeTags.VSAR_REQUEST_TYPE.equals(gdsMessageName)){
                expectedValues.put(TP95SCSSearchKeys.DOWNSTREAMWSCSCALLELAPSEDTIME_REQUEST1, "");
            }
        }
        return expectedValues;

    }
}
