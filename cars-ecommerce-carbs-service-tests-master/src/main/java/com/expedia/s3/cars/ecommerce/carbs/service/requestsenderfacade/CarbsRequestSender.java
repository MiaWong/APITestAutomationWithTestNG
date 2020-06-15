package com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationRequestType;
import com.expedia.s3.cars.ecommerce.messages.getreservation.defn.v4.CarECommerceGetReservationResponseType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveRequestType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchRequest;
import com.expedia.s3.cars.messages.locationiata.search.defn.v1.CarLocationIataSearchResponse;
import org.eclipse.jetty.client.HttpClient;

/**
 * Created by fehu on 8/22/2016.
 */
public class CarbsRequestSender {

    private CarbsRequestSender() {
    }

    public static CarECommerceSearchResponseType getCarbsSearchResponse(String guid, HttpClient httpClient, CarECommerceSearchRequestType searchRequestType) {
       final SimpleE3FIHttpTransport<CarECommerceSearchRequestType, CarECommerceSearchResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, searchRequestType, CarECommerceSearchResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();
    }

    public static CarECommerceGetDetailsResponseType getCarbsDetailsResponse(String guid, HttpClient httpClient, CarECommerceGetDetailsRequestType getDetailsRequestType) {
        final SimpleE3FIHttpTransport<CarECommerceGetDetailsRequestType, CarECommerceGetDetailsResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, getDetailsRequestType, CarECommerceGetDetailsResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();
    }

    public static CarECommerceGetReservationResponseType getCarbsGetReservationResponse(String guid, HttpClient httpClient, CarECommerceGetReservationRequestType getReservationRequestType) {
        final SimpleE3FIHttpTransport<CarECommerceGetReservationRequestType, CarECommerceGetReservationResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, getReservationRequestType, CarECommerceGetReservationResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }


    public static CarECommerceReserveResponseType getCarbsReserveResponse(String guid, HttpClient httpClient, CarECommerceReserveRequestType reserveRequestType) {
        final  SimpleE3FIHttpTransport<CarECommerceReserveRequestType, CarECommerceReserveResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, reserveRequestType, CarECommerceReserveResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarECommerceGetCostAndAvailabilityResponseType getCarbsGetCostAndAvailabilityResponse(String guid, HttpClient httpClient, CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailRequestType) {
        final SimpleE3FIHttpTransport<CarECommerceGetCostAndAvailabilityRequestType, CarECommerceGetCostAndAvailabilityResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                30000, getCostAndAvailRequestType, CarECommerceGetCostAndAvailabilityResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }

    public static CarECommerceCancelResponseType getCarbsCancelResponse(String guid, HttpClient httpClient, CarECommerceCancelRequestType cancelRequestType) {
        final SimpleE3FIHttpTransport<CarECommerceCancelRequestType, CarECommerceCancelResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS,
                50000, cancelRequestType, CarECommerceCancelResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }


    public static CarLocationIataSearchResponse getCarbsLocationSearchResponse(String guid, HttpClient httpClient, CarLocationIataSearchRequest carLocationIataSearchRequest) {
        final SimpleE3FIHttpTransport<CarLocationIataSearchRequest, CarLocationIataSearchResponse, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_LOCATION_ADDRESS,
                30000, carLocationIataSearchRequest, CarLocationIataSearchResponse.class);

        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();

    }
}
