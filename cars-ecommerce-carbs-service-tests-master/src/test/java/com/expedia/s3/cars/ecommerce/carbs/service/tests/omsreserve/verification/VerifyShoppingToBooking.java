package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.PriceListType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging.VerifyBooking;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging.VerifyBookingItem;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.action.ActionSequenceAbortException;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarReservationData;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarReservationDataExtended;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VCRRReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VCRRRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VRURReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VRURRes;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.VSARReq;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by fehu on 11/22/2018.
 */
@SuppressWarnings("PMD")
public class VerifyShoppingToBooking {

    private VerifyShoppingToBooking()
    {
    }

    public static void totalVerify(CarECommerceGetDetailsResponseType detailsResponseType, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj)
    {
        List<CostType> detailCostList = detailsResponseType.getCarProductList().getCarProduct().get(0).getCostList().getCost();
        List<CostType> bookingCostList = carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getCostList().getCost();

        Boolean isEqual = false;
        for (CostType costType : detailCostList)
        {
            if (!costType.getDescriptionRawText().contains("Total"))
            {
                continue;
            }
            for (CostType costType1 : bookingCostList)
            {
                if (!costType1.getDescriptionRawText().contains("Total"))
                {
                    continue;
                }
                final double costTotalAmountDetail = CostPriceCalculator.calculateCostAmount(costType, 0, true);
                final double costTotalAmountBooking = CostPriceCalculator.calculateCostAmount(costType1, 0, true);
                if (Math.abs(costTotalAmountDetail - costTotalAmountBooking) <= 0.01
                        && costType.getFinanceApplicationCode().equals(costType1.getFinanceApplicationCode())
                        && costType.getFinanceApplicationUnitCount().equals(costType1.getFinanceApplicationUnitCount())
                        && costType.getFinanceCategoryCode().equals(costType1.getFinanceCategoryCode())
                        && costType.getLegacyFinanceKey().getLegacyMonetaryCalculationID() == costType1.getLegacyFinanceKey().getLegacyMonetaryCalculationID()
                        && costType.getLegacyFinanceKey().getLegacyMonetaryCalculationID() == costType1.getLegacyFinanceKey().getLegacyMonetaryCalculationID()
                        && costType.getLegacyFinanceKey().getLegacyMonetaryCalculationID() == costType1.getLegacyFinanceKey().getLegacyMonetaryCalculationID()

                        )
                {
                    isEqual = true;
                    break;
                }
            }

        }

        if (!isEqual)
        {
            Assert.fail("\r\nTotal Price in GetDetails is not equal to Prepare Purchase.");

        }
    }

    public static  void isBaseRateEqual(TestData testData, StringBuffer errorMsg, CarProductType carProductType, String serviceName)
    {
        boolean isEqual = false;
        for (final CostType costType : carProductType.getCostList().getCost())
        {
            if (!"base".equals(costType.getFinanceCategoryCode()) || !costType.getDescriptionRawText().contains("base"))
            {
                continue;
            }
            for (final PriceType priceType : carProductType.getPriceList().getPrice())
            {
                if (!"base".equals(priceType.getFinanceCategoryCode()) || !priceType.getDescriptionRawText().contains("base"))
                {
                    continue;
                }
                final double costBaseAmount = CostPriceCalculator.getCostAmountByFinanceCategoryCode(carProductType.getCostList(), testData.getScenarios().getSupplierCurrencyCode(), "Base", 0, null, null, null, true);

                final double priceBaseAmount = CostPriceCalculator.getCostAmountByFinanceCategoryCode(carProductType.getCostList(), testData.getScenarios().getSupplierCurrencyCode(), "Base", 0, null, null, null, true);

                if (Math.abs(costBaseAmount - priceBaseAmount) <= 0.01 && costType.getFinanceApplicationCode().equals(priceType.getFinanceApplicationCode()) && costType.getFinanceApplicationUnitCount().equals(priceType.getFinanceApplicationUnitCount()) && costType.getFinanceCategoryCode().equals(priceType.getFinanceCategoryCode()))
                {
                    isEqual = true;
                    break;
                }
            }
        }

        if (!isEqual)
        {
            errorMsg.append(String.format("\r\nCarProduct Base Rate in PriceList is not equals to Base Cost, In Action : [%s]", serviceName));
        }
    }

    public static void pointOfSaleToPointOfSupplyExchangeRateVerify(PreparePurchaseResponseType preparePurchaseResponseType, GetOrderProcessResponseType getOrderProcessResponseType, CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType, StringBuilder errorMsg)
    {
        if(null == getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate())
        {
            errorMsg.append("There is no PointOfSaleToPointOfSupplyExchangeRate return in CEAR response! \n");
        }
        if (null == getOrderProcessResponseType.getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate())
        {
            errorMsg.append("There is no PointOfSaleToPointOfSupplyExchangeRate return in COGO response! \n");
        }
        if (null == preparePurchaseResponseType.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData()
                .getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate())
        {
            errorMsg.append("There is no PointOfSaleToPointOfSupplyExchangeRate return in COPP response! \n");

        }
        if ((errorMsg.toString().trim().length() == 0) && !(
                (getCostAndAvailabilityResponseType.getCurrencyConversionRate().getPointOfSaleToPointOfSupplyExchangeRate().getDecimal()
                        == getOrderProcessResponseType.getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate().getDecimal())
                        || (getOrderProcessResponseType.getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate().getDecimal()
                        == preparePurchaseResponseType.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData()
                        .getCarOfferData().getCarReservation().getPointOfSaleToPointOfSupplyExchangeRate().getDecimal()))
                )
        {
            errorMsg.append("PointOfSaleToPointOfSupplyExchangeRate is different between CEAR , COGO , COPP !");
        }
    }

    public static void verifyForMisc(StringBuilder errorMsg ,TestData testData, CarProductType selectCarProduct, CarECommerceGetDetailsResponseType carECommerceGetDetailsResponseType, PreparePurchaseResponseType preparePurchaseResponseType) throws DataAccessException
    {
        CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        int clientId = carBSHelper.getClientIDByCode(testData.getClientCode());
        final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());

        boolean actconfig = clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.PRICING_REMOVEMISCFORGDSP_ENABLE, clientId, "1");
        if(actconfig && CollectionUtils.isNotEmpty(CostPriceCalculator.getPriceListByFinanceCategoryCode(selectCarProduct.getPriceList()
                , testData.getScenarios().getSupplierCurrencyCode(), "Misc", null)))
        {
            errorMsg.append("Misc should not exist when Pricing.removeMiscForGDSP/enable is on in Search response!");
        }
        if(actconfig && CollectionUtils.isNotEmpty(CostPriceCalculator.getPriceListByFinanceCategoryCode(carECommerceGetDetailsResponseType.getCarProductList().getCarProduct().get(0).getPriceList()
                , testData.getScenarios().getSupplierCurrencyCode(), "Misc", null)))
        {
            errorMsg.append("Misc should not exist when Pricing.removeMiscForGDSP/enable is on in Details response!");
        }

        //COPP - same as GetDetails
        CompareUtil.compareObject(preparePurchaseResponseType.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getCarProduct().getPriceList().getPrice(), carECommerceGetDetailsResponseType.getCarProductList().getCarProduct()
                .get(0).getPriceList().getPrice(), null, errorMsg);
    }
    public static void checkCouponCodeSearch(StringBuilder errorMsg, Document spooferDoc,
                                             TestData testData, List<CarSearchResultType> searchResultList) throws DataAccessException
    {

        final List<Node> vsarReqList = PojoXmlUtil.getNodesByTagName(spooferDoc.getDocumentElement(), GDSMsgNodeTags
                .WorldSpanNodeTags.VSAR_REQUEST_TYPE);
        for (int m = 0; m < vsarReqList.size(); m++)
        {
            String expectedCPCode = testData.getCarRate().getPromoCode();
            String dir_CPCode = "";
            VSARReq vsarReq = new VSARReq(vsarReqList.get(m), DatasourceHelper.getWorldspanSCSDatasource()
                    , DatasourceHelper.getCarInventoryDatasource(), testData.getScenarios());
            if (null == vsarReq.getCarInventoryKey().getCarRate().getPromoCode())
            {
                errorMsg.append("No PromoCode value in VSAR request");
            }
            dir_CPCode = vsarReq.getCarInventoryKey().getCarRate().getPromoCode();

            if (!expectedCPCode.equals(dir_CPCode))
                errorMsg.append("Verify Coupon Code in request" + m + ": Expected value is "
                        + expectedCPCode + ", but actual value is" + dir_CPCode);
        }
        //Loop CarSearchResultList to check car product
        int i = 0, k = 0;
        for (CarSearchResultType carSearchResult : searchResultList)
        {
            i++;

            for (CarProductType car : carSearchResult.getCarProductList().getCarProduct())
            {
                k++;
                String mas_CPCode = car.getCarInventoryKey().getCarRate().getPromoCode();

                if (mas_CPCode == null)
                {
                    errorMsg.append("Verify Coupon Code in search response: There should have Coupon Code returned " + " in search index [" + i + "] car index [" + k + "]");
                }
                else
                {
                    if (!testData.getCarRate().getPromoCode().equals(mas_CPCode))
                        errorMsg.append("Verify Coupon Code in request search + " + "in search index [" + i + "] car index [" + k + "]" + ": Expected value is " + testData.getCarRate().getPromoCode() + ", but actual value is" + mas_CPCode);
                }
            }
        }
    }

    public static void checkCouponCodeGetDetails(StringBuilder errorMsg, Document spooferDoc, TestData testData, String mas_CPCode) throws DataAccessException
    {

        String expected_CPCode = testData.getCarRate().getPromoCode();
        final Node vrurReqNode = PojoXmlUtil.getNodeByTagName(spooferDoc.getDocumentElement(), GDSMsgNodeTags.WorldSpanNodeTags.VRUR_REQUEST_TYPE);

        final VRURReq vrurReqeust = new VRURReq(vrurReqNode, DatasourceHelper.getWorldspanSCSDatasource(), DatasourceHelper.getCarInventoryDatasource());


        if (StringUtil.isBlank(vrurReqeust.getInventoryKey().getCarRate().getPromoCode()))
        {
            errorMsg.append("No coupon code sent in gds message for getDetails!");
        }
        else if (!expected_CPCode.equals(vrurReqeust.getInventoryKey().getCarRate().getPromoCode()))
        {
            errorMsg.append("Verify CouponCode in getDetails request: Expected value is " + testData.getCarRate().getPromoCode() + ", but actual value is " + vrurReqeust.getInventoryKey().getCarRate().getPromoCode());
        }


        //Loop CarSearchResultList to check car product
        //String mas_CPCode = getDetailResponse.CarProductList.CarProduct[0].CarInventoryKey.CarRate.PromoCode;
        if (mas_CPCode == null)
        {
            errorMsg.append("Verify CouponCode in getDetails response: no CouponCode returned ");
        }
        if (!mas_CPCode.equals(testData.getCarRate().getPromoCode()))
        {
            errorMsg.append("Verify CouponCode in getDetails response: Expected value is " + testData.getCarRate().getPromoCode() + ", but actual value is" + mas_CPCode);
        }

    }

    public static void checkCouponCodePreparePurchase(StringBuilder errorMsg, Document spooferDoc,
                                                      TestData testData, PreparePurchaseResponseType prepareResponse) throws DataAccessException
    {
        String expected_CPCode = testData.getCarRate().getPromoCode();
        CarProductType car = prepareResponse.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();
        // Get CouponCode from VCRR and verify it.
        final Node vcrrReqNode = PojoXmlUtil.getNodeByTagName(spooferDoc.getDocumentElement(), GDSMsgNodeTags.WorldSpanNodeTags.VCRR_REQUEST_TYPE);
        final Node vcrrRspNode = PojoXmlUtil.getNodeByTagName(spooferDoc.getDocumentElement(), GDSMsgNodeTags.WorldSpanNodeTags.VCRR_RESPONSE_TYPE);
        final VCRRRsp vcrrRsp = new VCRRRsp(vcrrRspNode, null, DatasourceHelper.getWorldspanSCSDatasource(), DatasourceHelper.getCarInventoryDatasource());

        final VCRRReq vcrrReq = new VCRRReq(vcrrReqNode, DatasourceHelper.getWorldspanSCSDatasource(), DatasourceHelper.getCarInventoryDatasource());

        String couponCodeInReq = vcrrReq.getCarInventoryKey().getCarRate().getPromoCode();

        String couponCodeInResp = vcrrRsp.getCarInventoryKey().getCarRate().getPromoCode();

        if (StringUtil.isBlank(couponCodeInReq))
        {
            errorMsg.append("No CouponCode in VCRR request");
        }
        if (!expected_CPCode.equals(couponCodeInReq))
        {
            errorMsg.append(String.format("CouponCode in VCRR request is incorrect. Expected CouponCode=%s, ActualCouponCode=%S", expected_CPCode, couponCodeInReq));
        }
        if (StringUtil.isBlank(couponCodeInResp))
        {
            errorMsg.append("No CouponCode in VCRR response");
        }
        if (!expected_CPCode.equals(couponCodeInResp))
        {
            errorMsg.append(String.format("CouponCode in VCRR response is incorrect. Expected CouponCode=%S, ActualCouponCode=%S", expected_CPCode, couponCodeInResp));
        }

        // Mase response
        String mas_CouponCode = car.getCarInventoryKey().getCarRate().getPromoCode();
        if (StringUtil.isBlank(mas_CouponCode))
        {
            errorMsg.append("Verify CouponCode in PreparePurchase: no CouponCode number returned ");
        }
        if (!mas_CouponCode.equals(expected_CPCode))
        {
            errorMsg.append("Verify CouponCode in PreparePurchase: Expected CouponCode number is "
                    + expected_CPCode + ", but actual value is" + mas_CouponCode);
        }
    }

    public static void discountInCostPriceListVerifier(Document spooferDoc, CarECommerceGetDetailsRequestType req, CarECommerceGetDetailsResponseType rsp, String surfaceDiscountFlag, StringBuilder errorMsg) throws DataAccessException
    {
        // Get VRDi/VRUR message from crslog

        double totalDiscount_GDSRsp = 0;
        double totalMandaryCharge_GDSRsp = 0;

        double miscFromVRD = 0;
        //Get all the Cost/Price and their amount for verifications
        final Node vrurResNode = PojoXmlUtil.getNodeByTagName(spooferDoc.getDocumentElement(), GDSMsgNodeTags.WorldSpanNodeTags.VRUR_RESPONSE_TYPE);

        final VRURRes vrurResponse = new VRURRes(vrurResNode, req.getCarProductList().getCarProduct().get(0).getCarInventoryKey(), DatasourceHelper.getWorldspanSCSDatasource(), DatasourceHelper.getCarInventoryDatasource(), true);


        for (final CostType cost : vrurResponse.getCarProduct().getCostList().getCost())
        {
            if ("TotalDiscount".equals(cost.getFinanceCategoryCode()))
            {
                totalDiscount_GDSRsp = CostPriceCalculator.calculateCostAmount(cost, 0, true);
            }
            if ("Misc".equals(cost.getFinanceCategoryCode()))
            {
                miscFromVRD = CostPriceCalculator.calculateCostAmount(cost, 0, true);
            }
        }


        final CostListType cedrCostList = rsp.getCarProductList().getCarProduct().get(0).getCostList();
        final PriceListType cedrPriceList = rsp.getCarProductList().getCarProduct().get(0).getPriceList();
        final String posCurrency = req.getCurrencyCode();
        final  String posuCurrency = CostPriceCalculator.getCostPosuCurrencyCode(cedrCostList, posCurrency);
        //Discount in Cost
        final CostType discountCost_POS = CostPriceCalculator.getCostByFinanceCategoryCode(cedrCostList, posCurrency, "TotalDiscount", null, null);
        final  CostType discountCost_POSu = CostPriceCalculator.getCostByFinanceCategoryCode(cedrCostList, posuCurrency, "TotalDiscount", null, null);
        double discountValueInCost = discountCost_POSu != null ? CostPriceCalculator.calculateCostAmount(discountCost_POSu, 0, true) : 0;
        //Discount in Price
        final PriceType discountPrice_POS = CostPriceCalculator.getPriceByFinanceCategoryCode(cedrPriceList, posCurrency, "TotalDiscount", null);
        final  PriceType discountPrice_POSu = CostPriceCalculator.getPriceByFinanceCategoryCode(cedrPriceList, posuCurrency, "TotalDiscount", null);
        double discountValueInPrice = discountPrice_POSu != null ? CostPriceCalculator.calculatePriceAmount(discountPrice_POSu, 0, true) : 0;
        //Total Cost
        double totalCost_Posu = CostPriceCalculator.getPosuTotalCost(cedrCostList, posCurrency);
        //Total Price
        double totalPrice_Posu = CostPriceCalculator.getPosuTotalPrice(cedrPriceList, posCurrency, true);
        //BaseRateTotal
        double baseRateTotal_POSu = CostPriceCalculator.getPriceAmountByFinanceCategoryCode(cedrPriceList, posuCurrency, "BaseRateTotal", 0, null, true);
        //Misc Cost
        final BigDecimal miscCost_POSu = CostPriceCalculator.getCostAmountByFinanceCategoryCode(cedrCostList, posuCurrency, "Misc");
        //Misc Price
        double miscPrice_POSu = CostPriceCalculator.getPriceAmountByFinanceCategoryCode(cedrPriceList, posuCurrency, "Misc", 0, null, true);
        //Discount in MandatoryCharge(Price)
        PriceType discountInManChargePrice_POSu = CostPriceCalculator.getPriceByFinanceCategoryCode(cedrPriceList, posuCurrency, "MandatoryCharge", "TotalDiscount");
        double discountValueInManCharge = discountInManChargePrice_POSu != null ? CostPriceCalculator.calculatePriceAmount(discountInManChargePrice_POSu, 0, true) : 0;

        //Veriy Misc in CostList
        if (!(Math.abs(miscFromVRD) > 0.001))
        {
            miscFromVRD = totalCost_Posu - totalMandaryCharge_GDSRsp - totalDiscount_GDSRsp;
        }
        if (miscFromVRD > 0.01 && miscCost_POSu.doubleValue() - miscFromVRD > 0.01)
        {
            errorMsg.append("Misc Cost should exist in CEDR response!\r\n");
        }

        //Verification begin
        if (totalDiscount_GDSRsp != 0)
        {
            //Verify Discount Cost exist
            if (discountCost_POS == null || discountCost_POSu == null)
            {
                errorMsg.append("Discount Cost should exist in CEDR response!\r\n");
            }

            //Verify the Discount value in CostList
            if (Math.abs(totalDiscount_GDSRsp - discountValueInCost) > 0.01)
            {
                errorMsg.append(String.format("Discount in CEDR response CostList is not same as VRD response, VRD response: %s, CEDR response: %s!\r\n", totalDiscount_GDSRsp, discountValueInCost));
            }

            //Verify Total Price is equal to Total Cost - We just support for Agency now
            if (Math.abs(totalCost_Posu - totalPrice_Posu) > 0.01)
            {
                errorMsg.append(String.format("Total Price is not equal to Total Cost(POSu), TotalCost: %s, TotalPrice: %s!\r\n", totalCost_Posu, totalPrice_Posu));
            }

            //If surfaceDiscount flag is true, then Discount should exist in PriceList and Misc doesn't include Discount
            if (surfaceDiscountFlag == "1")
            {
                //Discount should exist in PriceList and MandatoryCharge for Discount doesn't exist
                if (discountPrice_POS == null || discountPrice_POSu == null)
                {
                    errorMsg.append("Discount Price should exist in CEDR response!\r\n");
                }
                if (discountInManChargePrice_POSu != null)
                {
                    errorMsg.append("Discount in MandatoryCharge should not exist in CEDR response!\r\n");
                }

                //Discount value in PriceList is equal to Cost Discount
                if (Math.abs(discountValueInPrice - discountValueInCost) > 0.01)
                {
                    errorMsg.append(String.format("Discount in PriceList should be equal to Cost Discount, Cost Discount: %s, Price Discount: %s!\r\n", discountValueInCost, discountValueInPrice));
                }

                //BaseRateTotal + Misc + Discount should be equal to total
                if (Math.abs(totalPrice_Posu - (baseRateTotal_POSu + miscPrice_POSu + discountValueInPrice + miscCost_POSu.doubleValue())) > 0.01)
                {
                    errorMsg.append(String.format("Total Price is not equal to baseRateTotal_POSu + miscPrice_POSu + discountValueInPrice(POSu), TotalPrice: %s, baseRateTotal_POSu: %s, miscPrice_POSu: %s, discountValueInPrice(POSu): {3}!\r\n", totalPrice_Posu, baseRateTotal_POSu, miscPrice_POSu, discountValueInPrice));
                }
            }
            else
            {
                //Discount should not exist  in PriceList and MandatoryCharge for Discount exist
                if (discountPrice_POS != null || discountPrice_POSu != null)
                {
                    errorMsg.append("Discount Price should not exist in CEDR response!\r\n");
                }
                if (discountInManChargePrice_POSu == null)
                {
                    errorMsg.append("Discount in MandatoryCharge should exist in CEDR response!\r\n");
                }

                //Discount value in MandatoryCharge is equal to Cost Discount
                if (Math.abs(discountValueInManCharge - discountValueInCost) > 0.01)
                {
                    errorMsg.append(String.format("Discount in PriceList as Mandary Charge should be equal to Cost Discount, Cost Discount: %s, Price Discount as Mandary Charge: %s!\r\n", discountValueInCost, discountValueInManCharge));
                }

                //BaseRateTotal + Misc should be equal to total
                if (Math.abs(totalPrice_Posu - (baseRateTotal_POSu + miscPrice_POSu + miscCost_POSu.doubleValue())) > 0.01)
                {
                    errorMsg.append(String.format("Total Price is not equal to baseRateTotal_POSu + miscPrice_POSu, TotalPrice: %s, baseRateTotal_POSu: %s, miscPrice_POSu: %s!\r\n", totalPrice_Posu, baseRateTotal_POSu, miscPrice_POSu));
                }
            }

        }
        else
        {
            //Discount Cost or Price should not exist
            if (discountCost_POS != null || discountCost_POSu != null || discountPrice_POS != null || discountPrice_POSu != null)
            {
                errorMsg.append("Discount Cost/Price should not exist in CEDR response when no TDIS in VRD response!\r\n");
            }
        }


    }


    public static void isBookingDataCorrectVerifier(TestData testData, PreparePurchaseRequestType preparePurchaseRequest, PreparePurchaseResponseType preparePurchaseResponse, CarECommerceSearchResponseType carBSSearchResponse_Publish) throws DataAccessException, IOException, SQLException, ActionSequenceAbortException
    {
        VerifyBooking.verifyBooking(preparePurchaseRequest, preparePurchaseResponse);

        VerifyBookingItem.verifyBookingItem(preparePurchaseRequest, preparePurchaseResponse, false, testData.getScenarios());
    }

    public static void eblVerifierOMS(TestData testData, StringBuilder errorMsg,PreparePurchaseResponseType preparePurchaseResponse) throws DataAccessException, SQLException
    {
        //Get EBL configuration
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), SettingsProvider.CARBS_POS_SET_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
        final  String eblFlag = posConfigHelper.getPosConfigSettingValue(testData.getScenarios(), ServiceConfigs.CARBS_BOOKING_ENHANCEBOOKINGLOGGING);

        //If EBL if off, verify no data logged; else verify the detail logging
        if (eblFlag.equals("1")&& eblFlag.equals("true"))
        {
            enhancedBookingLoggingVerifier(errorMsg, preparePurchaseResponse,
                    false);
        }
        else
        {
           enhancedBookingLoggingVerifier(errorMsg, preparePurchaseResponse,
                    true);
        }
    }
   //Verify no enhanced booking logged when enhanced booking logging is disabled or Booking.AugumentReservationWithDetails is disabled
    public static void  enhancedBookingLoggingVerifier(StringBuilder errorMsg, PreparePurchaseResponseType response, boolean verifyLogExist) throws DataAccessException
    {
        //Get actual value
        final String bookingItemID = BookingVerificationUtils.getBookingItemID(response);
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final  CarReservationData actCarReservationData = carBSHelper.getCarReservationDataByBookingItemID(bookingItemID);
        final  List<CarReservationDataExtended> actCarReservationExtList = carBSHelper.getCarReservationDataExtendedByBookingItemID(bookingItemID);

        //if enhanced booking logging exist, fail the test case
        if (!verifyLogExist)
        {
            if (null != actCarReservationData) errorMsg.append("CarReservationData should not be logged!  ");
            if (CollectionUtils.isNotEmpty(actCarReservationExtList)) errorMsg.append("CarReservationDataExtended should not be logged!  ");
        }
        else
        {
            if (null == actCarReservationData) errorMsg.append("CarReservationData should be logged!  ");
        }

    }

}