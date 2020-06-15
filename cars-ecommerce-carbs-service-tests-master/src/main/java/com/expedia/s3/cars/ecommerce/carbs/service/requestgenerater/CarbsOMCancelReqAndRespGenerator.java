package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;


import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonTypeGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import expedia.om.supply.datatype.defn.v1.ChangeTargetDataType;
import expedia.om.supply.datatype.defn.v1.ConfiguredProductDataType;
import expedia.om.supply.messages.defn.v1.*;

/**
 * Created by fehu on 8/30/2016.
 */
public class CarbsOMCancelReqAndRespGenerator {

    private GetChangeProcessRequest getChangeProcessRequestType;
    private GetChangeProcessResponseType getChangeProcessResponseType;
    private PrepareChangeRequest prepareChangeRequestType;
    private PrepareChangeResponseType prepareChangeResponseType;
    private CommitPrepareChangeRequest commitPrepareChangeRequestType;
    private CommitPrepareChangeResponseType commitPrepareChangeResponseType;
    private RollbackPrepareChangeRequest rollbackPrepareChangeRequestType;
    private RollbackPrepareChangeResponseType rollbackPrepareChangeResponseType;
    private CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator;
    private CarECommerceSearchRequestType searchRequestType;
    private Long recordLocator;
    private ConfiguredProductDataType configuredProductDataType;


    public CarbsOMCancelReqAndRespGenerator(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) {

        this.carbsOMReserveReqAndRespGenerator = carbsOMReserveReqAndRespGenerator;
        this.searchRequestType = carbsOMReserveReqAndRespGenerator.getSearchRequestType();
        this.recordLocator = carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getRecordLocator();
        this.configuredProductDataType = carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getConfiguredProductData();
    }

    public GetChangeProcessRequest createGetChangeProcessRequest() throws DataAccessException {
        final GetChangeProcessRequest request = new GetChangeProcessRequest();

        request.setSupplyRecordLocator(getSupplyRecordLocatorType());
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getSiteMessageInfo());
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));

        request.setChangeTargetData(getChangeTargetDataType());
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.getChangeProcessRequestType = request;
        return request;
    }

    private SupplyRecordLocatorType getSupplyRecordLocatorType() {
        final SupplyRecordLocatorType supplyRecordLocatorType = new SupplyRecordLocatorType();
        supplyRecordLocatorType.setRecordLocator(recordLocator);
        return supplyRecordLocatorType;
    }

    private ChangeTargetDataType getChangeTargetDataType() {
        final ChangeTargetDataType changeTargetDataType = new ChangeTargetDataType();
        changeTargetDataType.setNamespace(configuredProductDataType.getNamespace());
        changeTargetDataType.setType(configuredProductDataType.getType());

        changeTargetDataType.setCarOfferData(configuredProductDataType.getCarOfferData());
        return changeTargetDataType;
    }

    public PrepareChangeRequest createPrepareChangeRequest() throws DataAccessException {
        final PrepareChangeRequest request = new PrepareChangeRequest();

        request.setChangeContextID(this.getChangeProcessResponseType.getChangeContextID());
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getSiteMessageInfo());
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setOrderOperationCorrelationID(PojoXmlUtil.getRandomGuid());
        request.setChangeTargetData(getChangeTargetDataType());
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.prepareChangeRequestType = request;

        return request;
    }

    public CommitPrepareChangeRequest createCommitPrepareChangeRequest() throws DataAccessException {
        final CommitPrepareChangeRequest request = new CommitPrepareChangeRequest();
        request.setChangeContextID(this.getChangeProcessResponseType.getChangeContextID());
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getSiteMessageInfo());
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.commitPrepareChangeRequestType = request;
        return request;
    }

    public RollbackPrepareChangeRequest createRollbackPrepareChangeRequest() throws DataAccessException {
        final RollbackPrepareChangeRequest request = new RollbackPrepareChangeRequest();
        request.setChangeContextID(this.getChangeProcessResponseType.getChangeContextID());
        request.setMessageInfo(CarCommonTypeGenerator.createMessageInfo(CarCommonRequestGenerator.getMessageName(request),
                searchRequestType));
        request.setSiteMessageInfo(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getSiteMessageInfo());
        request.setPointOfSaleCustomerIdentifier(CarCommonTypeGenerator.createPointOfSaleCunstomerIdentifier(searchRequestType.getAuditLogTrackingData().getLogonUserKey().getUserID()));
        request.setExperimentMessageInfo(CarCommonTypeGenerator.buildExperimentMessageInfo());
        this.rollbackPrepareChangeRequestType = request;
        return request;
    }


    public GetChangeProcessResponseType getGetChangeProcessResponseType() {
        return getChangeProcessResponseType;
    }

    public void setGetChangeProcessResponseType(GetChangeProcessResponseType getChangeProcessResponseType) {
        this.getChangeProcessResponseType = getChangeProcessResponseType;
    }

    public GetChangeProcessRequest getGetChangeProcessRequestType() {
        return getChangeProcessRequestType;
    }

    public void setGetChangeProcessRequestType(GetChangeProcessRequest getChangeProcessRequestType) {
        this.getChangeProcessRequestType = getChangeProcessRequestType;
    }

    public PrepareChangeRequest getPrepareChangeRequestType() {
        return prepareChangeRequestType;
    }

    public void setPrepareChangeRequestType(PrepareChangeRequest prepareChangeRequestType) {
        this.prepareChangeRequestType = prepareChangeRequestType;
    }

    public CommitPrepareChangeRequest getCommitPrepareChangeRequestType() {
        return commitPrepareChangeRequestType;
    }

    public void setCommitPrepareChangeRequestType(CommitPrepareChangeRequest commitPrepareChangeRequestType) {
        this.commitPrepareChangeRequestType = commitPrepareChangeRequestType;
    }

    public RollbackPrepareChangeRequest getRollbackPrepareChangeRequestType() {
        return rollbackPrepareChangeRequestType;
    }

    public void setRollbackPrepareChangeRequestType(RollbackPrepareChangeRequest rollbackPrepareChangeRequestType) {
        this.rollbackPrepareChangeRequestType = rollbackPrepareChangeRequestType;
    }

    public CarECommerceSearchRequestType getSearchRequestType() {
        return searchRequestType;
    }

    public void setSearchRequestType(CarECommerceSearchRequestType searchRequestType) {
        this.searchRequestType = searchRequestType;
    }

    public PrepareChangeResponseType getPrepareChangeResponseType() {
        return prepareChangeResponseType;
    }

    public void setPrepareChangeResponseType(PrepareChangeResponseType prepareChangeResponseType) {
        this.prepareChangeResponseType = prepareChangeResponseType;
    }

    public CommitPrepareChangeResponseType getCommitPrepareChangeResponseType() {
        return commitPrepareChangeResponseType;
    }

    public void setCommitPrepareChangeResponseType(CommitPrepareChangeResponseType commitPrepareChangeResponseType) {
        this.commitPrepareChangeResponseType = commitPrepareChangeResponseType;
    }

    public RollbackPrepareChangeResponseType getRollbackPrepareChangeResponseType() {
        return rollbackPrepareChangeResponseType;
    }

    public void setRollbackPrepareChangeResponseType(RollbackPrepareChangeResponseType rollbackPrepareChangeResponseType) {
        this.rollbackPrepareChangeResponseType = rollbackPrepareChangeResponseType;
    }

    public CarbsOMReserveReqAndRespGenerator getCarbsOMReserveReqAndRespGenerator() {
        return carbsOMReserveReqAndRespGenerator;
    }

    public void setCarbsOMReserveReqAndRespGenerator(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) {
        this.carbsOMReserveReqAndRespGenerator = carbsOMReserveReqAndRespGenerator;
    }

    public Long getRecordLocator() {
        return recordLocator;
    }

    public void setRecordLocator(Long recordLocator) {
        this.recordLocator = recordLocator;
    }

    public ConfiguredProductDataType getConfiguredProductDataType() {
        return configuredProductDataType;
    }

    public void setConfiguredProductDataType(ConfiguredProductDataType configuredProductDataType) {
        this.configuredProductDataType = configuredProductDataType;
    }
}
