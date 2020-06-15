package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;


import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonTypeGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import expedia.om.supply.messages.defn.v1.RetrieveRequest;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;

/**
 * Created by fehu on 9/5/2016.
 */
@SuppressWarnings("PMD")
public class CarbsOMRetrieveReqAndRespGenerator {
    private RetrieveRequest retrieveRequestType;
    private RetrieveResponseType retrieveResponseType;
    private CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator;
    private long recordLocator;
    private CarECommerceSearchRequestType searchRequestType;

    public CarbsOMRetrieveReqAndRespGenerator(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) {
        this.carbsOMReserveReqAndRespGenerator = carbsOMReserveReqAndRespGenerator;
        this.searchRequestType = carbsOMReserveReqAndRespGenerator.getSearchRequestType();
        this.recordLocator = carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getRecordLocator();
    }

    public RetrieveRequest createRetrieveRequest() throws DataAccessException {
        RetrieveRequest request = new RetrieveRequest();

        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType));
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setRecordLocator(recordLocator);
        return request;
    }

    public RetrieveRequest createRetrieveRequest(TestData testData) throws DataAccessException {
        RetrieveRequest request = createRetrieveRequest();
        request.setSiteMessageInfo(CarCommonTypeGenerator.createSiteMessageInfo(searchRequestType, testData.isSetHyphenInLanguage()));

        return request;
    }

    public RetrieveRequest getRetrieveRequestType() {
        return retrieveRequestType;
    }

    public void setRetrieveRequestType(RetrieveRequest retrieveRequestType) {
        this.retrieveRequestType = retrieveRequestType;
    }

    public CarbsOMReserveReqAndRespGenerator getCarbsOMReserveReqAndRespGenerator() {
        return carbsOMReserveReqAndRespGenerator;
    }

    public void setCarbsOMReserveReqAndRespGenerator(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) {
        this.carbsOMReserveReqAndRespGenerator = carbsOMReserveReqAndRespGenerator;
    }

    public long getRecordLocator() {
        return recordLocator;
    }

    public void setRecordLocator(long recordLocator) {
        this.recordLocator = recordLocator;
    }

    public CarECommerceSearchRequestType getSearchRequestType() {
        return searchRequestType;
    }

    public void setSearchRequestType(CarECommerceSearchRequestType searchRequestType) {
        this.searchRequestType = searchRequestType;
    }

    public RetrieveResponseType getRetrieveResponseType() {
        return retrieveResponseType;
    }

    public void setRetrieveResponseType(RetrieveResponseType retrieveResponseType) {
        this.retrieveResponseType = retrieveResponseType;
    }
}
