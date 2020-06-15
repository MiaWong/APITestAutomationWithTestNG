package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater;

import com.expedia.e3.data.basetypes.defn.v4.LegacySiteKeyType;
import com.expedia.e3.data.basetypes.defn.v4.SiteKeyType;
import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.messagetypes.defn.v4.MessageInfoType;
import com.expedia.e3.data.messagetypes.defn.v4.SiteMessageInfoType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common.CarCommonRequestGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailRequestType;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailResponseType;
import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by fehu on 9/5/2016.
 */
public class CarbsOMGetChangeDetailReqAndRespGenerator {

    private GetChangeDetailRequestType getChangeDetailRequest;
    private GetChangeDetailResponseType getChangeDetailResponseType;
    private CarECommerceSearchRequestType searchRequestType;
    private CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator;
    private long recordLocator;
    public CarbsOMGetChangeDetailReqAndRespGenerator(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) {
        this.carbsOMReserveReqAndRespGenerator = carbsOMReserveReqAndRespGenerator;
        this.searchRequestType = carbsOMReserveReqAndRespGenerator.getSearchRequestType();
        this.recordLocator = carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getRecordLocator();

    }

    public GetChangeDetailRequestType createGetChangeDetailRequest(boolean withTPID) throws DataAccessException {
        final GetChangeDetailRequestType request = new GetChangeDetailRequestType();
        request.setMessageInfo(createMessageInfo(CarCommonRequestGenerator.getMessageName(request)));
        request.setSiteMessageInfo(createSiteMessageInfo(withTPID));

        if(recordLocator > 0){
            request.setRecordLocator(recordLocator);}
        // TODO: 11/8/2016  temporary
        request.setChangeAction("Cancel");

        return request;
    }

    private MessageInfoType createMessageInfo(String messageNameString)
    {
        final MessageInfoType messageInfo = new MessageInfoType();
        messageInfo.setMessageGUID(searchRequestType.getMessageInfo().getMessageGUID());
        messageInfo.setTransactionGUID(searchRequestType.getMessageInfo().getTransactionGUID());
        messageInfo.setCreateDateTime(DateTime.getInstanceByDateTime(new Date()));
        messageInfo.setMessageNameString(messageNameString);
        messageInfo.setMessageVersion("1.0.1");
        return messageInfo;
    }

    private  SiteMessageInfoType createSiteMessageInfo(boolean withTPID) throws DataAccessException {
        final SiteMessageInfoType siteMessageInfo = new SiteMessageInfoType();
        final PointOfSaleKeyType pointOfSaleKey = new PointOfSaleKeyType();
        pointOfSaleKey.setJurisdictionCountryCode(searchRequestType.getSiteMessageInfo().getPointOfSaleKey().getJurisdictionCountryCode());
        pointOfSaleKey.setCompanyCode(searchRequestType.getSiteMessageInfo().getPointOfSaleKey().getCompanyCode());
        pointOfSaleKey.setManagementUnitCode(searchRequestType.getSiteMessageInfo().getPointOfSaleKey().getManagementUnitCode());
        siteMessageInfo.setPointOfSaleKey(pointOfSaleKey);

        setSiteKey(siteMessageInfo, pointOfSaleKey, withTPID);

        //Added Abacus data in request
        siteMessageInfo.setAbacusExperimentID(1000);
        siteMessageInfo.setAbacusExperimentValue(2000);
        siteMessageInfo.setAbacusTreatmentGroupID(3000);
        return siteMessageInfo;
    }

    private void setSiteKey(SiteMessageInfoType siteMessageInfo, PointOfSaleKeyType pointOfSaleKey, boolean withTPID) throws DataAccessException {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        final List<AuditLogTrackingDataType> myTPIDToPoSAttributeMapList = carsInventoryDataSource.getAuditLogTPID(pointOfSaleKey.getJurisdictionCountryCode(),
                pointOfSaleKey.getCompanyCode(), pointOfSaleKey.getManagementUnitCode());
        final SiteKeyType siteKey = new SiteKeyType();
        siteKey.setSiteID(CollectionUtils.isEmpty(myTPIDToPoSAttributeMapList) ? 3l : myTPIDToPoSAttributeMapList.get(0).getAuditLogTPID());
        siteKey.setReportingID(siteKey.getSiteID());
        siteMessageInfo.setSiteKey(siteKey);

        if (withTPID)
        {
            final LegacySiteKeyType legacySiteKey = new LegacySiteKeyType();
            legacySiteKey.setEAPID(CollectionUtils.isEmpty(myTPIDToPoSAttributeMapList)? 0l : myTPIDToPoSAttributeMapList.get(0).getAuditLogEAPID());
            legacySiteKey.setGPID(0l);
            legacySiteKey.setTPID(siteKey.getSiteID());
            siteMessageInfo.setLegacySiteKey(legacySiteKey);
        }

    }

    public GetChangeDetailRequestType getGetChangeDetailRequest() {
        return getChangeDetailRequest;
    }

    public void setGetChangeDetailRequest(GetChangeDetailRequestType getChangeDetailRequest) {
        this.getChangeDetailRequest = getChangeDetailRequest;
    }

    public GetChangeDetailResponseType getGetChangeDetailResponseType() {
        return getChangeDetailResponseType;
    }

    public void setGetChangeDetailResponseType(GetChangeDetailResponseType getChangeDetailResponseType) {
        this.getChangeDetailResponseType = getChangeDetailResponseType;
    }

    public CarECommerceSearchRequestType getSearchRequestType() {
        return searchRequestType;
    }

    public void setSearchRequestType(CarECommerceSearchRequestType searchRequestType) {
        this.searchRequestType = searchRequestType;
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
}
