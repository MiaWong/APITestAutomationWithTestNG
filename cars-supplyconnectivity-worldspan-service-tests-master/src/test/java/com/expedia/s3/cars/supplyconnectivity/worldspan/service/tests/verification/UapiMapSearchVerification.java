package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleType;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.PoSToWorldspanDefaultSegmentMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VSARReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VSARRsp;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.DateTimeUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapCommonVerification
        .compareCostList;

/**
 * Created by yyang4 on 12/19/2016.
 */
public class UapiMapSearchVerification
{
    private static final Logger logger = Logger.getLogger(SearchResponsesBasicVerification.class);

    public static void uapiMapVerifierWSCSSearch(BasicVerificationContext verificationContext,
                                                 SearchVerificationInput verificationInput, DataSource scsDataSource,
                                                 DataSource carsInventoryDs, HttpClient httpClient, boolean isNeedVerifyRspMapping) throws
            DataAccessException, ParserConfigurationException, SQLException
    {
        final TestScenario testScenario = verificationContext.getScenario();
        final CarSupplyConnectivitySearchRequestType scsReq = verificationInput.getRequest();
        final CarSupplyConnectivitySearchResponseType scsRes = verificationInput.getResponse();
        final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        final StringBuilder failMsg = new StringBuilder();
        final StringBuilder errorMsg = new StringBuilder();

        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        if (CompareUtil.isObjEmpty(gdsMessageDoc))
        {
            Assert.fail("No GDS messages found ! ");
        }

        final List<Node> vsarList = PojoXmlUtil.getNodesByTagName(gdsMessageDoc.getFirstChild(), GDSMsgNodeTags
                .WorldSpanNodeTags.CRS_MESSGE_TYPE);
        if (CompareUtil.isObjEmpty(vsarList))
        {
            Assert.fail("No VSAR messages find ! ");
        }
        final List<CarSearchCriteriaType> expectedCarSearchCriteriaList = scsReq.getCarSearchCriteriaList()
                .getCarSearchCriteria();
        //Request mapping and Get Actual Search Result List
        verifyResultListCount(testScenario, vsarList, expectedCarSearchCriteriaList);

        final List<CarSearchResultType> expectedCarSearchResultList = new ArrayList<CarSearchResultType>();
        final List<CarSearchCriteriaType> actualCarSearchCriteriaList = new ArrayList<CarSearchCriteriaType>();
        for (Node crsData : vsarList)
        {
            long searchSequence = 0;
            long supplySubsetID = 0;
            final Node vsarReqNode = PojoXmlUtil.getNodeByTagName(crsData, GDSMsgNodeTags.WorldSpanNodeTags
                    .VSAR_REQUEST_TYPE);
            //Get actual CarSearchCriteria from VSAR
            final VSARReq vsarReq = new VSARReq(vsarReqNode, scsDataSource, carsInventoryDs, verificationContext
                    .getScenario());
            final CarSearchCriteriaType actualCarSearchCriteria = vsarReq.carSearchCriteria;
            boolean isCarSearchCriteriaEqual = false;
            List<Long> carCategoryCodeInReq = null;
            //Compare  SearchCriteria node
            for (CarSearchCriteriaType expectedcarSearchCriteria : expectedCarSearchCriteriaList)
            {
                boolean isBSCodeEqual = false;
                boolean isLocationMatch = false;
                boolean isVendorSupplierIDListMatch = false;
                boolean isCarRateMatch = false;
                boolean isBranchCodeMatch = false;
                boolean isITNumberEqual = false;
                if (UapiMapCommonVerification.isCarLocationKeyTypeEqual(expectedcarSearchCriteria
                        .getCarTransportationSegment().getStartCarLocationKey(), actualCarSearchCriteria
                        .getCarTransportationSegment().getStartCarLocationKey())
                        && UapiMapCommonVerification.isCarLocationKeyTypeEqual(expectedcarSearchCriteria
                        .getCarTransportationSegment().getEndCarLocationKey(), actualCarSearchCriteria
                        .getCarTransportationSegment().getEndCarLocationKey())
                        && DateTimeUtil.getDiffSeconds(actualCarSearchCriteria.getCarTransportationSegment()
                        .getSegmentDateTimeRange().getStartDateTimeRange().getMinDateTime(),
                        expectedcarSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange()
                                .getStartDateTimeRange().getMinDateTime()) < 1
                        && DateTimeUtil.getDiffSeconds(actualCarSearchCriteria.getCarTransportationSegment()
                        .getSegmentDateTimeRange().getEndDateTimeRange().getMinDateTime(), expectedcarSearchCriteria
                        .getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().getMinDateTime
                                ()) < 1
                        && CompareUtil.compareObject(expectedcarSearchCriteria.getVendorSupplierIDList()
                        .getVendorSupplierID(), actualCarSearchCriteria.getVendorSupplierIDList().getVendorSupplierID
                        (), null, errorMsg.append("VendorSupplierID: ")))
                {
                    isLocationMatch = true;
                    //Compare VendorSupplierIDList
                    isVendorSupplierIDListMatch = true;
                    //Compare PackageBoolean
                    String expBranchCode = null;
                    final PoSToWorldspanDefaultSegmentMap defaultSegmentMap = scsHelper
                            .getPoSToWorldspanDefaultSegmentMap(testScenario);
                    if (expectedcarSearchCriteria.getPackageBoolean())
                    {
                        expBranchCode = CompareUtil.isObjEmpty(defaultSegmentMap) ? "" : defaultSegmentMap
                                .getPackageBranchCode();
                    } else
                    {
                        expBranchCode = CompareUtil.isObjEmpty(defaultSegmentMap) ? "" : defaultSegmentMap
                                .getBranchCode();
                    }
                    if (CompareUtil.compareObject(expBranchCode, vsarReq.branchCode, null, errorMsg))
                    {
                        isBranchCodeMatch = true;
                    } else
                    {
                        failMsg.append(errorMsg);
                        continue;
                    }

                    //region SupplySubSetID
                    final List<SupplySubSetToWorldSpanSupplierItemMap> supplySubsetMap = inventoryHelper
                            .getWorldSpanSupplierItemMap(expectedcarSearchCriteria.getSupplySubsetIDEntryList()
                                    .getSupplySubsetIDEntry());
                    for (SupplySubSetToWorldSpanSupplierItemMap mapInfo : supplySubsetMap)
                    {
                        actualCarSearchCriteria.setCarRate(CompareUtil.isObjEmpty(actualCarSearchCriteria.getCarRate
                                ()) ? new CarRateType() : actualCarSearchCriteria.getCarRate());
                        expectedcarSearchCriteria.setCarRate(CompareUtil.isObjEmpty(expectedcarSearchCriteria
                                .getCarRate()) ? new CarRateType() : expectedcarSearchCriteria.getCarRate());
                        //Compare CarRate
                        //Get CDCode
                        if (CompareUtil.isObjEmpty(expectedcarSearchCriteria.getCarRate().getCorporateDiscountCode()))
                        {
                            if (Boolean.parseBoolean(mapInfo.getCorporateDiscountCodeRequiredInBooking()))
                            {
                                expectedcarSearchCriteria.getCarRate().setCorporateDiscountCode(mapInfo
                                        .getCorporateDiscountCode());
                            } else
                            {
                                expectedcarSearchCriteria.getCarRate().setCorporateDiscountCode(null);
                            }
                        }
                        //Get RateCode
                        if (CompareUtil.isObjEmpty(expectedcarSearchCriteria.getCarRate().getRateCode()))
                        {
                            if (!CompareUtil.isObjEmpty(mapInfo.getRateCode()))
                            {
                                expectedcarSearchCriteria.getCarRate().setRateCode(mapInfo.getRateCode());
                            }
                        }

                        //is prepay send in gds req Hertz+Prepaid
                        //https://confluence/display/SSG/Test+plan+for+CASSS-9855+Hertz+Prepaid
                        //https://jira.expedia.biz/browse/CASSS-10076
                        if(null != mapInfo.getPrepaidBool() && mapInfo.getPrepaidBool().equals("1"))
                        {
                            StringBuilder vids = new StringBuilder();
                            for(long venderId : expectedcarSearchCriteria.getVendorSupplierIDList().getVendorSupplierID())
                            {
                                vids.append(", " + venderId);
                            }
                            if(!vsarReq.getRateCategory().equalsIgnoreCase(CommonConstantManager.RateCategory.PREPAY) && vids.toString().contains("40"))
                            {
                                failMsg.append("/n Should send RateCategory=\"Prepay\" in VSAR request, but can't find.");
                            }
                        }

                        List<String> ignoreList = new ArrayList<>(Arrays.asList("carRateQualifierCode"));
                        if(!CompareUtil.isObjEmpty(actualCarSearchCriteria.getCarRate())
                                && !CompareUtil.isObjEmpty(actualCarSearchCriteria.getCarRate().getRateCategoryCode())
                                && actualCarSearchCriteria.getCarRate().getRateCategoryCode().equals(CommonConstantManager.RateCategory.PREPAY))
                        {
                            ignoreList.add("rateCategoryCode");
                        }
                        isCarRateMatch = CompareUtil.compareObject(actualCarSearchCriteria.getCarRate(),
                                expectedcarSearchCriteria.getCarRate(), ignoreList, errorMsg.append("CarRate: "));
                        failMsg.append(errorMsg);
                        //Compare bsCode
                        if (mapInfo.getIataAgencyCode().length() == 6)
                        {
                            mapInfo.setIataAgencyCode("00" + mapInfo.getIataAgencyCode());
                        }
                        if (CompareUtil.compareObject(mapInfo.getIataAgencyCode(), vsarReq.bsCode, null, errorMsg
                                .append("SupplySubSetToWorldSpanSupplierItemMap iataAgencyCode: ")))
                        {
                            isBSCodeEqual = true;
                        } else
                        {
                            failMsg.append(errorMsg);
                            continue;
                        }

                        //Compare ITNumber
                        if (CompareUtil.isObjEmpty(mapInfo.getItNumber()) && CompareUtil.isObjEmpty(vsarReq
                                .tourCodeList))
                        {
                            isITNumberEqual = true;
                        } else if (CompareUtil.compareObject(mapInfo.getItNumber(), vsarReq.tourCodeList.size(),
                                null, errorMsg.append("SupplySubSetToWorldSpanSupplierItemMap itNumber: ")))
                        {
                            isITNumberEqual = true;
                        } else if (vsarReq.tourCodeList.contains(mapInfo.getItNumber()))
                        {
                            isITNumberEqual = true;
                        } else
                        {
                            failMsg.append(errorMsg);
                            continue;
                        }

                        if (isLocationMatch && isVendorSupplierIDListMatch && isCarRateMatch && isBSCodeEqual &&
                                isBranchCodeMatch && isITNumberEqual)
                        {
                            isCarSearchCriteriaEqual = true;
                            searchSequence = expectedcarSearchCriteria.getSequence();
                            actualCarSearchCriteria.setSequence(searchSequence);
                            actualCarSearchCriteriaList.add(actualCarSearchCriteria);
                            supplySubsetID = mapInfo.getSupplySubsetID();

                            carCategoryCodeInReq = new ArrayList<Long>();
                            for (CarVehicleType carVehicle : expectedcarSearchCriteria.getCarVehicleList()
                                    .getCarVehicle())
                            {
                                if (!carCategoryCodeInReq.contains(carVehicle.getCarCategoryCode()))
                                {
                                    carCategoryCodeInReq.add(carVehicle.getCarCategoryCode());
                                }
                            }
                            break;
                        } else
                        {
                            Assert.fail(failMsg.toString());
                        }
                    }

                    // PrePaidFuelBoolean
                    // UnlimitedMileageBoolean
                }

                if (isCarSearchCriteriaEqual == true)
                {
                    break;
                }
            }

            // end of the foreach

            //for Response
            if (isCarSearchCriteriaEqual == false)
            {
                final String vendorSupplierList = org.apache.commons.lang.StringUtils.join(actualCarSearchCriteria
                        .getVendorSupplierIDList().getVendorSupplierID(), ",");
                Assert.fail(String.format("The Actual CarSearchCriteria: VendorSupplierID=%s, TargetBranch=%s not in " +
                        "expected CarSearchCriteriaList! \r\n", vendorSupplierList, vsarReq.branchCode));
            }

            if(isCarSearchCriteriaEqual && isNeedVerifyRspMapping)
            {
                final Node vsarResNode = PojoXmlUtil.getNodeByTagName(crsData, GDSMsgNodeTags.WorldSpanNodeTags
                        .VSAR_RESPONSE_TYPE);
                final List<CarProductType> carProductList = VSARRsp.readProductList(vsarResNode,
                        actualCarSearchCriteria.getCarRate().getCorporateDiscountCode(), scsDataSource,
                        carsInventoryDs, SettingsProvider.ENVIRONMENT_NAME);

                if (!CompareUtil.isObjEmpty(carProductList))
                {
                    final CarSearchResultType searchResult = new CarSearchResultType();
                    final CarProductListType carProductListType = new CarProductListType();
                    final List<CarProductType> carProductTypeList = new ArrayList<CarProductType>();
                    carProductListType.setCarProduct(carProductTypeList);
                    searchResult.setCarProductList(carProductListType);
                    searchResult.setSequence(searchSequence);
                    final boolean isLocationEqual = UapiMapCommonVerification.isCarLocationKeyTypeEqual
                            (carProductList.get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey(),
                                    actualCarSearchCriteria.getCarTransportationSegment().getStartCarLocationKey())
                            && UapiMapCommonVerification.isCarLocationKeyTypeEqual(carProductList.get(0)
                            .getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey(),
                            actualCarSearchCriteria.getCarTransportationSegment().getEndCarLocationKey());
                    if (isLocationEqual)
                    {
                        for (CarProductType carProduct : carProductList)
                        {
                            if (carCategoryCodeInReq.contains(carProduct.getCarInventoryKey().getCarCatalogKey()
                                    .getCarVehicle().getCarCategoryCode()) || carCategoryCodeInReq.contains(0L))
                            {
                                if (carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() > 0L
                                        && carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()
                                        .getCarCategoryCode() > 0L
                                        && carProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()
                                        .getCarTypeCode() > 0L)
                                {
                                    searchResult.getCarProductList().getCarProduct().add(carProduct);
                                    carProduct.getCarInventoryKey().setSupplySubsetID(supplySubsetID);
                                    carProduct.getCarInventoryKey().getCarRate().setLoyaltyProgram
                                            (actualCarSearchCriteria.getCarRate().getLoyaltyProgram());
                                    //copy PromoCode from request if needed
                                    final String couponSupport = inventoryHelper.getCarBehaviorAttributValue
                                            (carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()
                                                    , carProduct.getCarInventoryKey().getSupplySubsetID(), 21L);
                                    if (!CompareUtil.isObjEmpty(vsarReq.carInventoryKey.getCarRate().getPromoCode())
                                            && "1".equals(couponSupport))
                                    {
                                        carProduct.getCarInventoryKey().getCarRate().setPromoCode(vsarReq
                                                .carInventoryKey.getCarRate().getPromoCode());
                                    }
                                }
                            }
                        }
                    }
                    if (!CompareUtil.isObjEmpty(expectedCarSearchResultList))
                    {
                        boolean exist = false;
                        for (CarSearchResultType result : expectedCarSearchResultList)
                        {
                            if (CompareUtil.compareObject(result.getSequence(), searchResult.getSequence(), null,
                                    errorMsg))
                            {
                                exist = true;
                                result.getCarProductList().getCarProduct().addAll(carProductList);
                            }
                        }
                        if (!exist)
                        {
                            expectedCarSearchResultList.add(searchResult);
                        }

                    } else
                    {
                        expectedCarSearchResultList.add(searchResult);
                    }
                }
            }
        }
        //region response mapping
        compareTwoSearchReusltList(scsRes.getCarSearchResultList().getCarSearchResult(), expectedCarSearchResultList,
                testScenario, verificationContext.getOriginatingGuid(), scsDataSource, httpClient);

    }

    private static void verifyResultListCount(TestScenario testScenario, List<Node> vsarList,
                                              List<CarSearchCriteriaType> expectedCarSearchCriteriaList) throws
            DataAccessException, SQLException
    {
        final String setPosConfigUrl = AppConfig.resolveStringValue("${serviceTransport.posConfig.endPoint}");
        final PosConfigHelper posConfigHelper = new PosConfigHelper(SettingsProvider.CARWORLDSPANSCSDATASOURCE,
                setPosConfigUrl, "aws-stt01");
        if (posConfigHelper.checkPosConfigFeatureEnable(testScenario, "0", PosConfigSettingName
                .SEARCH_FILTERINVALIDLOCATIONCODE_ENABLE, false))
        {
            if (!CompareUtil.isObjEmpty(expectedCarSearchCriteriaList) && expectedCarSearchCriteriaList.size() >
                    vsarList.size())
            {
                Assert.fail(String.format("The VSAR count: %s less than to CarSearchCriteria count in CCSR " +
                        "request:%s!", vsarList.size(), expectedCarSearchCriteriaList.size()));
            }
        }
    }

    public static void compareTwoSearchReusltList(List<CarSearchResultType> actualSearchResultList,
                                                  List<CarSearchResultType> expectedSearchREsultList, TestScenario
                                                          testScenario, String guid, DataSource scsDataSouce,
                                                  HttpClient httpClient) throws DataAccessException,
            ParserConfigurationException
    {
        final StringBuilder errorMsg = new StringBuilder();
        for (CarSearchResultType expectedResult : expectedSearchREsultList)
        {
            boolean existResult = false;
            for (CarSearchResultType actualResult : actualSearchResultList)
            {
                if (CompareUtil.compareObject(expectedResult.getSequence(), actualResult.getSequence(), null, errorMsg))
                {
                    existResult = true;

                    if (!CompareUtil.compareObject(expectedResult.getCarProductList().getCarProduct().size(),
                            actualResult.getCarProductList().getCarProduct().size(), null, errorMsg))
                    {
                        Assert.fail("CarProductList:" + errorMsg.append(String.format(" when the Search Sequence=%s" +
                                ".\n", actualResult.getSequence())).toString());
                    } else
                    {
                        for (CarProductType expectedProduct : expectedResult.getCarProductList().getCarProduct())
                        {
                            for (CarProductType actualProduct : actualResult.getCarProductList().getCarProduct())
                            {
                                if (UapiMapCommonVerification.isSameCarInventoryKey(actualProduct.getCarInventoryKey
                                        (), expectedProduct.getCarInventoryKey()))
                                {
                                    UapiMapCommonVerification.isCarInventoryKeyEqual(actualProduct.getCarInventoryKey
                                            (), expectedProduct.getCarInventoryKey(), String.format("Search with " +
                                                    "Sequence=%s, (CarInventoryKey-supplierID:SIPP=%s:%s%s%s%s}): " +
                                                    "CarInventoryKey is not correctly mapped to SCS response, SCS " +
                                                    "response:",
                                            actualResult.getSequence(),
                                            actualProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID
                                                    (), actualProduct.getCarInventoryKey().getCarCatalogKey()
                                                    .getCarVehicle().getCarCategoryCode(),
                                            actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()
                                                    .getCarTypeCode(), actualProduct.getCarInventoryKey()
                                                    .getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode(),
                                            actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()
                                                    .getCarFuelACCode()), false, false, false, false);
                                    //region car product map verify
                                    //carInventoryKey
                                    if (CompareUtil.isObjEmpty(expectedProduct.getCarInventoryKey().getCarCatalogKey
                                            ().getCarDropOffLocationKey().getCarLocationCategoryCode()))
                                    {
                                        expectedProduct.getCarInventoryKey().getCarCatalogKey()
                                                .getCarDropOffLocationKey().setCarLocationCategoryCode
                                                (expectedProduct.getCarInventoryKey().getCarCatalogKey()
                                                        .getCarPickupLocationKey().getCarLocationCategoryCode());
                                    }
                                    //ReservationGuaranteeCategory
                                    if ((CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals
                                            (actualProduct.getReservationGuaranteeCategory()) &&
                                            !CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals
                                                    (expectedProduct.getReservationGuaranteeCategory()))
                                            || (!CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals
                                            (actualProduct.getReservationGuaranteeCategory()) &&
                                            CommonConstantManager.ReservationGuaranteeCategory.REQUIRED.equals
                                                    (expectedProduct.getReservationGuaranteeCategory())))
                                    {
                                        Assert.fail(String.format("CCGuarantee is not correctly mapped from VSAR " +
                                                "response to SCS response, SCS request: %s, VSAR response: %s!\r\n",
                                                actualProduct.getReservationGuaranteeCategory(), expectedProduct
                                                        .getReservationGuaranteeCategory()));
                                    }

                                    //availStatusCode
                                    if (!CompareUtil.compareObject(expectedProduct.getAvailStatusCode(),
                                            actualProduct.getAvailStatusCode(), null, errorMsg))
                                    {
                                        Assert.fail(String.format("Search with Sequence=%s, " +
                                                        "(CarInventoryKey-supplierID:SIID=%s:%s%s%s%s):  " +
                                                        "AvailStatusCode : ",
                                                actualResult.getSequence(),
                                                actualProduct.getCarInventoryKey().getCarCatalogKey()
                                                        .getVendorSupplierID(), actualProduct.getCarInventoryKey()
                                                        .getCarCatalogKey().getCarVehicle().getCarCategoryCode(),
                                                actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()
                                                        .getCarTypeCode(), actualProduct.getCarInventoryKey()
                                                        .getCarCatalogKey().getCarVehicle()
                                                        .getCarTransmissionDriveCode(),
                                                actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle()
                                                        .getCarFuelACCode()) + errorMsg.toString());
                                    }

                                    //costList for Agency/GDSP car
                                    if (!BusinessModel.Merchant.equals(testScenario.getBusinessModel()))
                                    {
                                        compareCostList(expectedProduct.getCostList().getCost(), actualProduct
                                                .getCostList().getCost(), testScenario.getSupplierCurrencyCode(),
                                                guid, true, httpClient);
                                    }

                                    //carMileage
                                    UapiMapCommonVerification.isCarMileageEqual(expectedProduct.getCarMileage(), actualProduct.getCarMileage(), String.format("Search with Sequence=%s, (CarInventoryKey-supplierID:SIID=%s:%s%s%s%s): CarMileage compare between VSAR response and SCS response: \r\n",
                                            actualResult.getSequence(),
                                            actualProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode(),
                                            actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode(), actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode(),
                                            actualProduct.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()));
                                }
                            }
                        }
                    }
                }
            }
            if (!existResult)
            {
                Assert.fail(String.format("The search result with Sequence=%s is missing in search response.\n", expectedResult.getSequence()));
            }
        }
    }

    public static void  verifyIfPrePayBooleanReturnInSearchResponseForHertz(SearchVerificationInput verificationInput,  DataSource carsInventoryDs) throws
            DataAccessException, ParserConfigurationException, SQLException
    {
        CarSupplyConnectivitySearchResponseType response = verificationInput.getResponse();

        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDs);
        for(CarSearchResultType searchResult : response.getCarSearchResultList().getCarSearchResult())
        {
            for(CarProductType carProduct : searchResult.getCarProductList().getCarProduct())
            {
                if(carProduct.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 40)
                {
                    UapiMapCommonVerification commonVerifier = new UapiMapCommonVerification();
                    commonVerifier.verifyIfPrePayBooleanReturnInProductForHertz(carProduct, inventoryHelper);
                }
            }
        }
    }
}
