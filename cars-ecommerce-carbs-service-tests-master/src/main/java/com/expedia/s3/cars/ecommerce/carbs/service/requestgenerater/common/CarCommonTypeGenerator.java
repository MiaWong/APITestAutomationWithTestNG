package com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.common;

import com.expedia.e3.data.basetypes.defn.v4.LegacySiteKeyType;
import com.expedia.e3.data.basetypes.defn.v4.SiteKeyType;
import com.expedia.e3.data.basetypes.defn.v4.UserKeyType;
import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.messagetypes.defn.v5.*;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonConstantManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import expedia.om.supply.messages.defn.v1.OrderIdentifierType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by fehu on 8/31/2016.
 */
public class CarCommonTypeGenerator {
    private CarCommonTypeGenerator() {
    }

    public static MessageInfoType createMessageInfo(String messageNameString, CarECommerceSearchRequestType searchRequestType)
    {
        final MessageInfoType messageInfo = new MessageInfoType();
        messageInfo.setMessageGUID(searchRequestType.getMessageInfo().getMessageGUID());
        messageInfo.setTransactionGUID(CompareUtil.isObjEmpty(searchRequestType.getMessageInfo().getTransactionGUID())? UUID.randomUUID().toString() : searchRequestType.getMessageInfo().getTransactionGUID());
        messageInfo.setCreateDateTime(DateTime.getInstanceByDateTime(new Date()));
        messageInfo.setMessageNameString(messageNameString);
        messageInfo.setMessageVersion("1.0");

        if(null != searchRequestType.getMessageInfo())
        {
            if (!StringUtils.isEmpty(searchRequestType.getMessageInfo().getClientHostnameString()))
            {
                messageInfo.setClientHostnameString(searchRequestType.getMessageInfo().getClientHostnameString());
            }

            if (!StringUtils.isEmpty(searchRequestType.getMessageInfo().getClientName()))
            {
                messageInfo.setClientName(searchRequestType.getMessageInfo().getClientName());
            }

            if (!StringUtils.isEmpty(searchRequestType.getMessageInfo().getEndUserIPAddress()))
            {
                messageInfo.setEndUserIPAddress(searchRequestType.getMessageInfo().getEndUserIPAddress());
            }
        }

        return messageInfo;
    }


    public static SiteMessageInfoType createSiteMessageInfo(CarECommerceSearchRequestType searchRequestType) throws DataAccessException {
        final SiteMessageInfoType siteMessageInfo = new SiteMessageInfoType();
        final PointOfSaleKeyType pointOfSaleKey = new PointOfSaleKeyType();
        pointOfSaleKey.setJurisdictionCountryCode(searchRequestType.getSiteMessageInfo().getPointOfSaleKey().getJurisdictionCountryCode());
        pointOfSaleKey.setCompanyCode(searchRequestType.getSiteMessageInfo().getPointOfSaleKey().getCompanyCode());
        pointOfSaleKey.setManagementUnitCode(searchRequestType.getSiteMessageInfo().getPointOfSaleKey().getManagementUnitCode());
        siteMessageInfo.setPointOfSaleKey(pointOfSaleKey);
        //language set
        setLanguage(siteMessageInfo, pointOfSaleKey);
        //LegacySiteKeyType set
        setSiteKey(siteMessageInfo, pointOfSaleKey);

        return siteMessageInfo;
    }

    public static SiteMessageInfoType createSiteMessageInfo(CarECommerceSearchRequestType searchRequestType, boolean setHyphenInLanguage) throws DataAccessException {
        final SiteMessageInfoType siteMessageInfo = createSiteMessageInfo(searchRequestType);
        if(setHyphenInLanguage){
            siteMessageInfo.setLanguageCode(siteMessageInfo.getLanguageCode().replace('_', '-'));
        }
        return siteMessageInfo;
    }

    private static void setSiteKey(SiteMessageInfoType siteMessageInfo, PointOfSaleKeyType pointOfSaleKey) throws DataAccessException {
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        final List<AuditLogTrackingDataType> myTPIDToPoSAttributeMapList = carsInventoryDataSource.getAuditLogTPID(pointOfSaleKey.getJurisdictionCountryCode(),
                pointOfSaleKey.getCompanyCode(), pointOfSaleKey.getManagementUnitCode());
        final LegacySiteKeyType legacySiteKey = new LegacySiteKeyType();
        legacySiteKey.setEAPID(CollectionUtils.isEmpty(myTPIDToPoSAttributeMapList) ? 0l : myTPIDToPoSAttributeMapList.get(0).getAuditLogEAPID());
        legacySiteKey.setGPID(0l);
        legacySiteKey.setTPID(CollectionUtils.isEmpty(myTPIDToPoSAttributeMapList) ? 3l : myTPIDToPoSAttributeMapList.get(0).getAuditLogTPID());
        siteMessageInfo.setLegacySiteKey(legacySiteKey);
    }
    private static void setLanguage(SiteMessageInfoType siteMessageInfo, PointOfSaleKeyType pointOfSaleKey) throws DataAccessException {
       String language = "";
        switch (pointOfSaleKey.getJurisdictionCountryCode()) {
            case "USA":
                language = "en_US";
                break;
            case "GBR":
                language = "en_GB";
                break;
            case "FRA":
                 language = "fr_FR";
                break;
            case "DEU":
                language = "de_DE";
                break;
            case "CAN":
                language = "en_CA";
                break;
            default:
                language = "en_US";
                break;
        }
        siteMessageInfo.setLanguageCode(language);
    }

    public static PointOfSaleCustomerIdentifierType createPointOfSaleCunstomerIdentifier(Long userId)
    {
        final PointOfSaleCustomerIdentifierType posCustomerIdentifier = new PointOfSaleCustomerIdentifierType();
        final UserKeyType expediaLoginUserKey = new UserKeyType();
        expediaLoginUserKey.setUserID(Long.valueOf(userId));
        final SiteKeyType siteKeyType = new SiteKeyType();
        siteKeyType.setSiteID(1);
        siteKeyType.setReportingID(3l);
        expediaLoginUserKey.setSiteKey(siteKeyType);
        posCustomerIdentifier.setExpediaLoginUserKey(expediaLoginUserKey);

        return posCustomerIdentifier;
    }

    public static ExperimentMessageInfoType buildExperimentMessageInfo()
    {
        final ExperimentMessageInfoType experimentMessageInfo = new ExperimentMessageInfoType();
        final AbacusExperimentType abacusExperimentType = new AbacusExperimentType();
        abacusExperimentType.setAbacusExperimentID(CarCommonConstantManager.ABACUSEXPERIMENTID);

        abacusExperimentType.setAbacusExperimentValue(CarCommonConstantManager.ABACUSEXPERIMENTVALUE);

        abacusExperimentType.setAbacusTreatmentGroupID(CarCommonConstantManager.ABACUSTREATMENTGROUPID);
        experimentMessageInfo.setAbacusExperiment(abacusExperimentType);
        return experimentMessageInfo;
    }

    public static OrderIdentifierType createOrderIdentifier(String orderID, String orderNumber)
    {
        final OrderIdentifierType orderIdentifier = new OrderIdentifierType();
        orderIdentifier.setOrderID(null == orderID ? 0l : Long.valueOf(orderID));
        orderIdentifier.setOrderNumber(null == orderNumber ? BigInteger.ZERO: new BigInteger(orderNumber));
        return orderIdentifier;
    }
}
