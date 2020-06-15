package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender;

import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.eclipse.jetty.client.HttpClient;

/**
 * Created by fehu on 8/11/2016.
 */
public class AmadeusSCSRequestSender {

    public static CarSupplyConnectivityLocationSearchResponseType getSCSLocationSearchResponse(String guid, HttpClient httpClient, CarSupplyConnectivityLocationSearchRequestType request)
    {
        SimpleE3FIHttpTransport<CarSupplyConnectivityLocationSearchRequestType, CarSupplyConnectivityLocationSearchResponseType, Object> transport =
                new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                        SettingsProvider.SERVICE_ADDRESS, 30000, request, CarSupplyConnectivityLocationSearchResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();
    }

    public static CarSupplyConnectivitySearchResponseType getSCSSearchResponse(String guid, HttpClient httpClient, CarSupplyConnectivitySearchRequestType searchRequestType)
    {
        SimpleE3FIHttpTransport<CarSupplyConnectivitySearchRequestType, CarSupplyConnectivitySearchResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, searchRequestType, CarSupplyConnectivitySearchResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarSupplyConnectivityGetDetailsResponseType getSCSDetailsResponse(String guid, HttpClient httpClient, CarSupplyConnectivityGetDetailsRequestType getDetailsRequestType) {
        SimpleE3FIHttpTransport<CarSupplyConnectivityGetDetailsRequestType, CarSupplyConnectivityGetDetailsResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, getDetailsRequestType, CarSupplyConnectivityGetDetailsResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarSupplyConnectivityGetReservationResponseType getSCSReservationResponse(String guid, HttpClient httpClient, CarSupplyConnectivityGetReservationRequestType getReservationRequestType) {
        SimpleE3FIHttpTransport<CarSupplyConnectivityGetReservationRequestType, CarSupplyConnectivityGetReservationResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, getReservationRequestType, CarSupplyConnectivityGetReservationResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarSupplyConnectivityReserveResponseType getSCSReserveResponse(String guid, HttpClient httpClient, CarSupplyConnectivityReserveRequestType reserveRequestType) {
        SimpleE3FIHttpTransport<CarSupplyConnectivityReserveRequestType, CarSupplyConnectivityReserveResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, reserveRequestType, CarSupplyConnectivityReserveResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarSupplyConnectivityGetCostAndAvailabilityResponseType getSCSGetCostAndAvailabilityResponse(String guid, HttpClient httpClient, CarSupplyConnectivityGetCostAndAvailabilityRequestType getCostAndAvailRequestType) {
        SimpleE3FIHttpTransport<CarSupplyConnectivityGetCostAndAvailabilityRequestType, CarSupplyConnectivityGetCostAndAvailabilityResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, getCostAndAvailRequestType, CarSupplyConnectivityGetCostAndAvailabilityResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarSupplyConnectivityCancelResponseType getSCSCancelResponse(String guid, HttpClient httpClient, CarSupplyConnectivityCancelRequestType cancelRequestType) {
        SimpleE3FIHttpTransport<CarSupplyConnectivityCancelRequestType, CarSupplyConnectivityCancelResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, cancelRequestType, CarSupplyConnectivityCancelResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

}
