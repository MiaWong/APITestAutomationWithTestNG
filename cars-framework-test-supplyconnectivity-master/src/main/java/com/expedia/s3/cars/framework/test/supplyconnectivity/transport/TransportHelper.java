package com.expedia.s3.cars.framework.test.supplyconnectivity.transport;

import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
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
import org.eclipse.jetty.client.HttpClient;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by jiyu on 8/25/16.
 */
public final class TransportHelper
{
    private TransportHelper() {}

    public static SearchVerificationInput sendReceive(HttpClient httpClient,
                                                      String serviceAddr,
                                                      String e3destination,
                                                      CarSupplyConnectivitySearchRequestType request,
                                                      String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return new MessageTransmissionHelper<   CarSupplyConnectivitySearchRequestType,
                                                CarSupplyConnectivitySearchResponseType,
                                                SearchVerificationInput>(
                CarSupplyConnectivitySearchRequestType.class,
                CarSupplyConnectivitySearchResponseType.class,
                SearchVerificationInput.class).sendReceive(httpClient, serviceAddr, e3destination, request, guid);

    }

    public static GetDetailsVerificationInput sendReceive(HttpClient httpClient,
                                                          String serviceAddr,
                                                          String e3destination,
                                                          CarSupplyConnectivityGetDetailsRequestType request,
                                                          String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return new MessageTransmissionHelper<   CarSupplyConnectivityGetDetailsRequestType,
                                                CarSupplyConnectivityGetDetailsResponseType,
                                                GetDetailsVerificationInput>(
                CarSupplyConnectivityGetDetailsRequestType.class,
                CarSupplyConnectivityGetDetailsResponseType.class,
                GetDetailsVerificationInput.class).sendReceive(httpClient, serviceAddr, e3destination, request, guid);

    }

    public static GetCostAndAvailabilityVerificationInput sendReceive(  HttpClient httpClient,
                                                                        String serviceAddr,
                                                                        String e3destination,
                                                                        CarSupplyConnectivityGetCostAndAvailabilityRequestType request,
                                                                        String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return new MessageTransmissionHelper<   CarSupplyConnectivityGetCostAndAvailabilityRequestType,
                                                CarSupplyConnectivityGetCostAndAvailabilityResponseType,
                                                GetCostAndAvailabilityVerificationInput>(
                CarSupplyConnectivityGetCostAndAvailabilityRequestType.class,
                CarSupplyConnectivityGetCostAndAvailabilityResponseType.class,
                GetCostAndAvailabilityVerificationInput.class).sendReceive(httpClient, serviceAddr, e3destination, request, guid);

    }

    public static ReserveVerificationInput sendReceive(HttpClient httpClient,
                                                       String serviceAddr,
                                                       String e3destination,
                                                       CarSupplyConnectivityReserveRequestType request,
                                                       String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return new MessageTransmissionHelper<   CarSupplyConnectivityReserveRequestType,
                                                CarSupplyConnectivityReserveResponseType,
                                                ReserveVerificationInput>(
                CarSupplyConnectivityReserveRequestType.class,
                CarSupplyConnectivityReserveResponseType.class,
                ReserveVerificationInput.class).sendReceive(httpClient, serviceAddr, e3destination, request, guid);

    }

    public static GetReservationVerificationInput sendReceive(HttpClient httpClient,
                                                              String serviceAddr,
                                                              String e3destination,
                                                              CarSupplyConnectivityGetReservationRequestType request,
                                                              String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return new MessageTransmissionHelper<   CarSupplyConnectivityGetReservationRequestType,
                                                CarSupplyConnectivityGetReservationResponseType,
                                                GetReservationVerificationInput>(
                CarSupplyConnectivityGetReservationRequestType.class,
                CarSupplyConnectivityGetReservationResponseType.class,
                GetReservationVerificationInput.class).sendReceive(httpClient, serviceAddr, e3destination, request, guid);

    }

    public static CancelVerificationInput sendReceive(HttpClient httpClient,
                                                      String serviceAddr,
                                                      String e3destination,
                                                      CarSupplyConnectivityCancelRequestType request,
                                                      String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return new MessageTransmissionHelper<   CarSupplyConnectivityCancelRequestType,
                                                CarSupplyConnectivityCancelResponseType,
                                                CancelVerificationInput>(
                CarSupplyConnectivityCancelRequestType.class,
                CarSupplyConnectivityCancelResponseType.class,
                CancelVerificationInput.class).sendReceive(httpClient, serviceAddr, e3destination, request, guid);

    }

}
