package com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade;


import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailRequestType;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailResponseType;
import expedia.om.supply.messages.defn.v1.*;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;

import javax.xml.bind.JAXBElement;

/**
 * Created by fehu on 9/1/2016.
 */
public class CarbsOMServiceSender {

    private final static Logger LOGGER = Logger.getLogger(CarbsOMServiceSender.class);

    private CarbsOMServiceSender() {
    }

    public static GetOrderProcessResponseType sendGetOrderProcessResponse(String guid, HttpClient httpClient, GetOrderProcessRequest getOrderProcessRequestType) {
       final SimpleE3FIHttpTransport<GetOrderProcessRequest, GetOrderProcessResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORGETORDER,
                SettingsProvider.GETPROCESSORDER_SERVICE_ADDRESS,
                30000, getOrderProcessRequestType, GetOrderProcessResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final  Object rawResponse = transport.getServiceRequestContext().getResponse();

        GetOrderProcessResponseType getOrderProcessResponse = null;

        if (rawResponse instanceof JAXBElement) {
            getOrderProcessResponse = (GetOrderProcessResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            getOrderProcessResponse = (GetOrderProcessResponseType) rawResponse;
        }

        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getOrderProcessRequestType)));
        //LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getOrderProcessResponse)));

        return getOrderProcessResponse;
    }

    public static CreateRecordResponseType sendCreateRecordResponse(String guid, HttpClient httpClient, CreateRecordRequest createRecordRequestType) {
        final SimpleE3FIHttpTransport<CreateRecordRequestType, CreateRecordResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORCREATERECORD,
                SettingsProvider.CREATERECORD_SERVICE_ADDRESS,
                30000, createRecordRequestType, CreateRecordResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        CreateRecordResponseType createRecordResponseType = null;

        if (rawResponse instanceof JAXBElement) {
            createRecordResponseType = (CreateRecordResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            createRecordResponseType = (CreateRecordResponseType) rawResponse;
        }

        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(createRecordRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(createRecordResponseType)));
        return createRecordResponseType;

    }

    public static PreparePurchaseResponseType sendPreparePurchaseResponse(String guid, HttpClient httpClient, PreparePurchaseRequest preparePurchaseRequestType) {
        final SimpleE3FIHttpTransport<PreparePurchaseRequestType, PreparePurchaseResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORPREPAREPURCHASE,
                SettingsProvider.PREPAREPURCHASE_SERVICE_ADDRESS,
                30000, preparePurchaseRequestType, PreparePurchaseResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        PreparePurchaseResponseType preparePurchaseResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            preparePurchaseResponseType = (PreparePurchaseResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            preparePurchaseResponseType = (PreparePurchaseResponseType) rawResponse;
        }
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(preparePurchaseRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(preparePurchaseResponseType)));
        return preparePurchaseResponseType;

    }

    public static CommitPreparePurchaseResponseType sendCommitPreparePurchaseResponse(String guid, HttpClient httpClient, CommitPreparePurchaseRequest commitPreparePurchaseRequestType) {
        final SimpleE3FIHttpTransport<CommitPreparePurchaseRequestType, CommitPreparePurchaseResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORCOMMITPURCHASE,
                SettingsProvider.COMMITPREPAREPURCHASE_SERVICE_ADDRESS,
                30000, commitPreparePurchaseRequestType, CommitPreparePurchaseResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        CommitPreparePurchaseResponseType commitPreparePurchaseResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            commitPreparePurchaseResponseType = (CommitPreparePurchaseResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            commitPreparePurchaseResponseType = (CommitPreparePurchaseResponseType) rawResponse;
        }
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(commitPreparePurchaseRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(commitPreparePurchaseResponseType)));
        return commitPreparePurchaseResponseType;

    }

    public static RollbackPreparePurchaseResponseType sendRollbackPreparePurchaseResponse(String guid, HttpClient httpClient, RollbackPreparePurchaseRequest rollbackPreparePurchaseRequestType) {
        final  SimpleE3FIHttpTransport<RollbackPreparePurchaseRequestType, RollbackPreparePurchaseResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORROLLBACKPUCHASE,
                SettingsProvider.ROLLBACKPREPAREPURCHASE_SERVICE_ADDRESS,
                30000, rollbackPreparePurchaseRequestType, RollbackPreparePurchaseResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final  Object rawResponse = transport.getServiceRequestContext().getResponse();
        RollbackPreparePurchaseResponseType rollbackPreparePurchaseResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            rollbackPreparePurchaseResponseType = (RollbackPreparePurchaseResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            rollbackPreparePurchaseResponseType = (RollbackPreparePurchaseResponseType) rawResponse;
        }
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rollbackPreparePurchaseRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rollbackPreparePurchaseResponseType)));
        return rollbackPreparePurchaseResponseType;

    }

    public static RollbackPrepareChangeResponseType sendRollbackPrepareChangeResponse(String guid, HttpClient httpClient, RollbackPrepareChangeRequestType rollbackPrepareChangeRequestType) {
        final SimpleE3FIHttpTransport<RollbackPrepareChangeRequestType, RollbackPrepareChangeResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORROLLBACKPREPARE,
                SettingsProvider.ROLLBACKPREPARECHANGE_SERVICE_ADDRESS,
                30000, rollbackPrepareChangeRequestType, RollbackPrepareChangeResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        RollbackPrepareChangeResponseType rollbackPrepareChangeResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            rollbackPrepareChangeResponseType = (RollbackPrepareChangeResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            rollbackPrepareChangeResponseType = (RollbackPrepareChangeResponseType) rawResponse;
        }
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rollbackPrepareChangeRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rollbackPrepareChangeResponseType)));
        return rollbackPrepareChangeResponseType;

    }

    public static PrepareChangeResponseType sendPrepareChangeResponse(String guid, HttpClient httpClient, PrepareChangeRequestType prepareChangeRequestType) {
        final SimpleE3FIHttpTransport<PrepareChangeRequestType, PrepareChangeResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORPREPARECHANGE,
                SettingsProvider.PREPARECHANGE_SERVICE_ADDRESS,
                30000, prepareChangeRequestType, PrepareChangeResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        PrepareChangeResponseType prepareChangeResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            prepareChangeResponseType = (PrepareChangeResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            prepareChangeResponseType = (PrepareChangeResponseType) rawResponse;
        }

        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(prepareChangeRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(prepareChangeResponseType)));
        return prepareChangeResponseType;

    }

    public static CommitPrepareChangeResponseType sendCommitPrepareChangeResponse(String guid, HttpClient httpClient, CommitPrepareChangeRequestType commitPrepareChangeRequestType) {
        final  SimpleE3FIHttpTransport<CommitPrepareChangeRequestType, CommitPrepareChangeResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORCOMMITPREPARE,
                SettingsProvider.COMMITPREPARECHANGE_SERVICE_ADDRESS,
                30000, commitPrepareChangeRequestType, CommitPrepareChangeResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        CommitPrepareChangeResponseType commitPrepareChangeResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            commitPrepareChangeResponseType = (CommitPrepareChangeResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            commitPrepareChangeResponseType = (CommitPrepareChangeResponseType) rawResponse;
        }

        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(commitPrepareChangeRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(commitPrepareChangeResponseType)));
        return commitPrepareChangeResponseType;

    }

    public static GetChangeProcessResponseType sendGetChangeProcessResponse(String guid, HttpClient httpClient, GetChangeProcessRequestType getChangeProcessRequestType) {
        final SimpleE3FIHttpTransport<GetChangeProcessRequestType, GetChangeProcessResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORGETCHANGE,
                SettingsProvider.GETCHANGEPROCESS_SERVICE_ADDRESS,
                30000, getChangeProcessRequestType, GetChangeProcessResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final Object rawResponse = transport.getServiceRequestContext().getResponse();
        GetChangeProcessResponseType getChangeProcessResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            getChangeProcessResponseType = (GetChangeProcessResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            getChangeProcessResponseType = (GetChangeProcessResponseType) rawResponse;
        }

        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getChangeProcessRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getChangeProcessResponseType)));
        return getChangeProcessResponseType;

    }

    public static RetrieveResponseType sendRetrieveResponse(String guid, HttpClient httpClient, RetrieveRequest retrieveRequestType) {
        final SimpleE3FIHttpTransport<RetrieveRequestType, RetrieveResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONFORRETRIEVE,
                SettingsProvider.RETRIEVE_SERVICE_ADDRESS,
                30000, retrieveRequestType, RetrieveResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final  Object rawResponse = transport.getServiceRequestContext().getResponse();
        RetrieveResponseType retrieveResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            retrieveResponseType = (RetrieveResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            retrieveResponseType = (RetrieveResponseType) rawResponse;
        }
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(retrieveRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(retrieveResponseType)));
        return retrieveResponseType;

    }

    public static GetChangeDetailResponseType sendGetChangeDetailResponse(String guid, HttpClient httpClient, GetChangeDetailRequestType getChangeDetailRequestType) {
        final  SimpleE3FIHttpTransport<GetChangeDetailRequestType, GetChangeDetailResponseType, Object> transport
                = new SimpleE3FIHttpTransport<>(httpClient, SettingsProvider.SERVICE_E3DESTINATIONGETCHANGEDETAIL,
                SettingsProvider.GETCHANGEDETAIL_SERVICE_ADDRESS,
                30000, getChangeDetailRequestType, GetChangeDetailResponseType.class);

        RequestSender.sendWithTransport(transport, guid);
        final  Object rawResponse = transport.getServiceRequestContext().getResponse();
        GetChangeDetailResponseType getChangeDetailResponseType = null;
        if (rawResponse instanceof JAXBElement) {
            getChangeDetailResponseType = (GetChangeDetailResponseType) ((JAXBElement) rawResponse).getValue();
        } else {
            getChangeDetailResponseType = (GetChangeDetailResponseType) rawResponse;
        }
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getChangeDetailRequestType)));
        LOGGER.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getChangeDetailResponseType)));
        return getChangeDetailResponseType;

    }


}
