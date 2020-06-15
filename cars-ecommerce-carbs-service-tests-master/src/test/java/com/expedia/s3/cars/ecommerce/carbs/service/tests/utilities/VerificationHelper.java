package com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail.verification.VerifyCarRateDetailInGetCostAndAvailabilityResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification.VerifyCarRateDetailInGetDetailsResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification.VerifyOptionListInGetDetailsResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification.VerifyPriceListInGetDetailsResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess.verification.VerifyCarRateDetailInGetOrderProcessResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess.verification.VerifyOptionListInGetOrderProcessResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyCarRateDetailInPreparePurchaseResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyOptionListInPreparePurchaseResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyPriceListInPreparePurchaseRsp;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyReferenceInPreparePurchaseResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve.verification.VerifyCarRateDetailInRetrieveResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.IfUpgradeCarReturnedInRspVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyCarPriceListInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyCarRateDetailInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyLocationCountInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyOptionListInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyPrepayPostpaInResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyReturnedLatLongInSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarbsGetCostAndAvailResponseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarbsGetDetailResponseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.GetOrderProcessBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.basic.PreparePurchaseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.GetOrderProcessVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarbsSearchResponseBasicVerification;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * Created by jiyu on 8/25/16.
 */
public final class VerificationHelper
{
    private VerificationHelper()
    {
    }

    public static boolean isPosConfigEnabled(String key, TestScenario testScenario) throws DataAccessException, SQLException {
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource());
        posConfigHelper.getPosConfigSettingValue(testScenario, key);
        return "1".equals(posConfigHelper.getPosConfigSettingValue(testScenario, key)) ? true : false;
    }


    public static String verifyBasicCarCateloguCompare(CarCatalogKeyType expectedCarKeyInRequest, CarCatalogKeyType expectedCarKeyInResponse)
    {
        if (null == expectedCarKeyInRequest)
        {
            return CommonErrorMsgs.MESSGAGE_NO_CAR_CATALOG_KEY_IN_REQUEST;
        }

        if (null == expectedCarKeyInResponse)
        {
            return CommonErrorMsgs.MESSGAGE_NO_CAR_CATALOG_KEY_IN_RESPONSE;
        }

        //  you may add more basic validation here
        final boolean isPassed = compare(expectedCarKeyInRequest, expectedCarKeyInResponse);
        if (!isPassed)
        {
            return CommonErrorMsgs.MESSAGE_NO_MATCHED_CAR;
        }

        return null;
    }

    private static boolean compare(CarCatalogKeyType expectedCarKeyInRequest, CarCatalogKeyType expectedCarKeyInResponse)
    {
        return (expectedCarKeyInRequest.getVendorSupplierID() == expectedCarKeyInResponse.getVendorSupplierID() &&
                expectedCarKeyInRequest.getCarVehicle().getCarCategoryCode() == expectedCarKeyInResponse.getCarVehicle().getCarCategoryCode() &&
                expectedCarKeyInRequest.getCarVehicle().getCarTypeCode() == expectedCarKeyInResponse.getCarVehicle().getCarTypeCode() &&
                expectedCarKeyInRequest.getCarVehicle().getCarTransmissionDriveCode() == expectedCarKeyInResponse.getCarVehicle().getCarTransmissionDriveCode() &&
                expectedCarKeyInRequest.getCarVehicle().getCarFuelACCode() == expectedCarKeyInResponse.getCarVehicle().getCarFuelACCode());
    }

    public static String verifyExtra(List<String> descriptionRawTextList)
    {
        final StringBuilder errorMsg = new StringBuilder();

        if (!descriptionRawTextList.isEmpty())
        {
            errorMsg.append(CommonErrorMsgs.MESSAGE_ERROR_COLLECTION_IN_RESPONSE);
            descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
        }

        if (errorMsg.toString().trim().length() > 0)
        {
            return errorMsg.toString();
        } else
        {
            return null;
        }
    }

    //  search basic verification
    public static String searchBasicVerification(SearchVerificationInput searchVerificationInput,
                                                 SpooferTransport spooferTransport,
                                                 TestScenario scenarios,
                                                 String guid,
                                                 boolean isRetrieveRecordRequired,
                                                 Logger logger) throws IOException {

            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

            final CarbsSearchResponseBasicVerification verifier = new CarbsSearchResponseBasicVerification();
            final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }



        return null;
    }

    public static String carRateDetailInSearchVerification(SearchVerificationInput searchVerificationInput,
                                                           SpooferTransport spooferTransport,
                                                           DataSource carsInventoryDatasource,
                                                           DataSource titaniumDatasource,
                                                           TestScenario scenarios,
                                                           String guid,
                                                           boolean isRetrieveRecordRequired,

                                                           Logger logger) throws Exception {


            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyCarRateDetailInSearchResponse verifier = new VerifyCarRateDetailInSearchResponse();
            verifier.setCarsInventoryDatasource(carsInventoryDatasource);
            verifier.setTitaniumDatasource(titaniumDatasource);
            final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }


        return null;
    }

    public static String carPriceListInSearchVerification(SearchVerificationInput searchVerificationInput,
                                                        SpooferTransport spooferTransport,
                                                        TestScenario scenarios,
                                                        String guid,
                                                        boolean isRetrieveRecordRequired,
                                                        Logger logger) throws IOException
    {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

            final VerifyCarPriceListInSearchResponse verifier = new VerifyCarPriceListInSearchResponse();

            final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }


        return null;
    }

    //--------------- GetDetail--------------------
    public static String getDetailsBasicVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                     SpooferTransport spooferTransport,
                                                     TestScenario scenarios,
                                                     String guid,
                                                     boolean isRetrieveRecordRequired,
                                                     Logger logger) throws IOException
    {
        final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final CarbsGetDetailResponseBasicVerification verifier = new CarbsGetDetailResponseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT +
                    " carGetDetailInBasicVerification is failed, Details is in logger.");
        }

        return null;
    }

    public static String carRateDetailInGetDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                               SpooferTransport spooferTransport,
                                                               DataSource carsInventoryDatasource,
                                                               DataSource titaniumDatasource,
                                                               TestScenario scenarios,
                                                               String guid,
                                                               boolean isRetrieveRecordRequired,
                                                               Logger logger) throws Exception {
      
               final Document spooferTransactions;

            spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;

            final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyCarRateDetailInGetDetailsResponse verifier = new VerifyCarRateDetailInGetDetailsResponse();
            verifier.setCarsInventoryDatasource(carsInventoryDatasource);
            verifier.setTitaniumDatasource(titaniumDatasource);
            final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT +
                        result);
            }

        return null;
    }

    public static String optionListInSearchVerification(SearchVerificationInput searchVerificationInput,
                                                        TestScenario scenarios,
                                                        String guid,
                                                        Logger logger) throws DataAccessException
    {
        final Document spooferTransactions = null;
        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final VerifyOptionListInSearchResponse verifier = new VerifyOptionListInSearchResponse();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT +
                    " OptionListInGetDetailsVerification is failed, Details is in logger.");
        }

        return null;
    }

    public static String optionListInGetDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                            SpooferTransport spooferTransport,
                                                            TestScenario scenarios,
                                                            String guid,
                                                            boolean isRetrieveRecordRequired,
                                                            Logger logger)
    {
        try
        {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyOptionListInGetDetailsResponse verifier = new VerifyOptionListInGetDetailsResponse();
            final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT +
                        " OptionListInGetDetailsVerification is failed, Details is in logger.");
            }
        } catch (IOException e)
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT +
                    " Failed to get Spoofer Message, get Excption: " + e.getStackTrace());
        }

        return null;
    }

    public static String priceListInGetDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                            SpooferTransport spooferTransport,
                                                            TestScenario scenarios,
                                                            String guid,
                                                            boolean isRetrieveRecordRequired,
                                                            Logger logger) throws IOException {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;

            final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyPriceListInGetDetailsResponse verifier = new VerifyPriceListInGetDetailsResponse();

            final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT +
                        result);
            }


        return null;
    }

    //--------------- GetCostAndAvail--------------------
    public static String getCostAndAvailabilityBasicVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                                 SpooferTransport spooferTransport,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 boolean isRetrieveRecordRequired,
                                                                 Logger logger)
    {
        try
        {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext getCostAndAvailabilityVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final CarbsGetCostAndAvailResponseBasicVerification verifier = new CarbsGetCostAndAvailResponseBasicVerification();
            final IVerification.VerificationResult result = verifier.verify(getCostAndAvailabilityVerificationInput, getCostAndAvailabilityVerificationContext);

            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
            }
        } catch (IOException e)
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT +
                    " Failed to get Spoofer Message, get Excption: " + e.getStackTrace());
        }

        return null;
    }

    public static String carRateDetailInGetCostAndAvailabilityVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                                           SpooferTransport spooferTransport,
                                                                           DataSource carsInventoryDatasource,
                                                                           DataSource titaniumDatasource,
                                                                           TestScenario scenarios,
                                                                           String guid,
                                                                           boolean isRetrieveRecordRequired,
                                                                           Logger logger) throws Exception {

            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext getCostAndAvailabilityVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyCarRateDetailInGetCostAndAvailabilityResponse verifier = new VerifyCarRateDetailInGetCostAndAvailabilityResponse();
            verifier.setCarsInventoryDatasource(carsInventoryDatasource);
            verifier.setTitaniumDatasource(titaniumDatasource);
            final IVerification.VerificationResult result = verifier.verify(getCostAndAvailabilityVerificationInput, getCostAndAvailabilityVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
            }


        return null;
    }

    //------------------GetOrderProcess-----------------------------
    public static String getOrderProcessBasicVerification(GetOrderProcessVerificationInput getOrderProcessVerificationInput,
                                                          SpooferTransport spooferTransport,
                                                          TestScenario scenarios,
                                                          String guid,
                                                          boolean isRetrieveRecordRequired,
                                                          Logger logger)
    {
        try
        {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext getOrderProcessVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final GetOrderProcessBasicVerification verifier = new GetOrderProcessBasicVerification();
            final IVerification.VerificationResult result = verifier.verify(getOrderProcessVerificationInput, getOrderProcessVerificationContext);

            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT + result);
            }
        } catch (IOException e)
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT + " Failed to get Spoofer Message, get Excption: " + e.getStackTrace());
        }

        return null;
    }

    public static String carRateDetailInGetOrderProcessVerification(GetOrderProcessVerificationInput getOrderProcessVerificationInput,
                                                                    SpooferTransport spooferTransport,
                                                                    DataSource carsInventoryDatasource,
                                                                    DataSource titaniumDatasource,
                                                                    TestScenario scenarios,
                                                                    String guid,
                                                                    boolean isRetrieveRecordRequired,
                                                                    Logger logger)
    {
        try
        {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext getOrderProcessVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyCarRateDetailInGetOrderProcessResponse verifier = new VerifyCarRateDetailInGetOrderProcessResponse();
            verifier.setCarsInventoryDatasource(carsInventoryDatasource);
            verifier.setTitaniumDatasource(titaniumDatasource);
            final IVerification.VerificationResult result = verifier.verify(getOrderProcessVerificationInput, getOrderProcessVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT +
                        " carRateDetailInGetOrderProcessVerification is failed, Details is in logger.");
            }
        } catch (IOException e)
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT + " Failed to get Spoofer Message, get Excption: " + e.getStackTrace());
        }

        return null;
    }

    public static String optionListInGetOrderProcessVerification(GetOrderProcessVerificationInput getOrderProcessVerificationInput,
                                                                 SpooferTransport spooferTransport,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 boolean isRetrieveRecordRequired,
                                                                 Logger logger)
    {
        try
        {
            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext getOrderProcessVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyOptionListInGetOrderProcessResponse verifier = new VerifyOptionListInGetOrderProcessResponse();
            final IVerification.VerificationResult result = verifier.verify(getOrderProcessVerificationInput, getOrderProcessVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + " optionListInGetOrderProcessVerification is failed, Details is in logger.");
            }
        } catch (IOException e)
        {
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT + " Failed to get Spoofer Message, get Excption: " + e.getStackTrace());
        }

        return null;
    }

    //------------------PreparePurchase-----------------------------
    public static String preparePurchaseBasicVerification(PreparePurshaseVerificationInput preparePurchaseVerificationInput,
                                                          SpooferTransport spooferTransport,
                                                          TestScenario scenarios,
                                                          String guid,
                                                          boolean isRetrieveRecordRequired,
                                                          Logger logger) throws IOException
    {
        final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext preparePurchaseVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final PreparePurchaseBasicVerification verifier = new PreparePurchaseBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(preparePurchaseVerificationInput, preparePurchaseVerificationContext);

        if (logger != null)
        {
            logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + result);
        }

        return null;
    }

    public static String carRateDetailInPreparePurchaseVerification(PreparePurshaseVerificationInput preparePurchaseVerificationInput,
                                                                    SpooferTransport spooferTransport,
                                                                    DataSource carsInventoryDatasource,
                                                                    DataSource titaniumDatasource,
                                                                    TestScenario scenarios,
                                                                    String guid,
                                                                    boolean isRetrieveRecordRequired,
                                                                    Logger logger) throws Exception {

            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext preparePurchaseVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyCarRateDetailInPreparePurchaseResponse verifier = new VerifyCarRateDetailInPreparePurchaseResponse();
            verifier.setCarsInventoryDatasource(carsInventoryDatasource);
            verifier.setTitaniumDatasource(titaniumDatasource);
            final IVerification.VerificationResult result = verifier.verify(preparePurchaseVerificationInput, preparePurchaseVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT +
                        result);
            }


        return null;
    }

    public static String optionListInPreparePurchaseVerification(PreparePurshaseVerificationInput preparePurchaseVerificationInput,
                                                                 SpooferTransport spooferTransport,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 boolean isRetrieveRecordRequired,
                                                                 Logger logger) throws IOException {

            final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;
            final BasicVerificationContext preparePurchaseVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyOptionListInPreparePurchaseResponse verifier = new VerifyOptionListInPreparePurchaseResponse();
            final IVerification.VerificationResult result = verifier.verify(preparePurchaseVerificationInput, preparePurchaseVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + result);
            }


        return null;
    }

    public static String priceListListInPreparePurchaseVerification(PreparePurshaseVerificationInput preparePurchaseVerificationInput,
                                                                 TestScenario scenarios,
                                                                 String guid,
                                                                 Logger logger)
    {
            final BasicVerificationContext preparePurchaseVerificationContext = new BasicVerificationContext(null, guid, scenarios);
            final VerifyPriceListInPreparePurchaseRsp verifier = new VerifyPriceListInPreparePurchaseRsp();
            final IVerification.VerificationResult result = verifier.verify(preparePurchaseVerificationInput, preparePurchaseVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + result);
            }


        return null;
    }

    public static void  referenceListInPreparePurchaseVerification(PreparePurshaseVerificationInput preparePurchaseVerificationInput,
                                                                    DataSource carsInventoryDatasource,
                                                                    Logger logger) throws DataAccessException {


            final VerifyReferenceInPreparePurchaseResponse verifier = new VerifyReferenceInPreparePurchaseResponse();
            verifier.verify(preparePurchaseVerificationInput, carsInventoryDatasource);


    }


    public static String carRateDetailInRetrieveVerification(RetrieveVerificationInput retrieveVerificationInput,
                                                             SpooferTransport spooferTransport,
                                                             DataSource carsInventoryDatasource,
                                                             TestScenario scenarios,
                                                             String guid,
                                                             boolean isRetrieveRecordRequired,
                                                             Logger logger) throws Exception {
        final Document spooferTransactions = isRetrieveRecordRequired ? spooferTransport.retrieveRecords(guid) : null;


            final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
            final VerifyCarRateDetailInRetrieveResponse verifier = new VerifyCarRateDetailInRetrieveResponse();
            verifier.setCarsInventoryDatasource(carsInventoryDatasource);
            final IVerification.VerificationResult result = verifier.verify(retrieveVerificationInput, basicVerificationContext);

            if (!result.isPassed())
            {
                if (logger != null)
                {
                    logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
                }
                Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETRESERVATION_VERIFICATION_PROMPT +
                        result);
            }

        return null;
    }

    public static String isUpgradeCarReturnVerification(String guid, TestScenario scenarios, CarECommerceSearchRequestType requestType, CarECommerceSearchResponseType responseType, Logger logger)
    {
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestType, responseType);
        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, guid, scenarios);
        final IfUpgradeCarReturnedInRspVerifier verifier = new IfUpgradeCarReturnedInRspVerifier();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, verificationContext);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT +
                    result);
        }

        return null;
    }

    public static String latLongInSearchResponseVerification(SearchVerificationInput searchVerificationInput,
                                                             TestScenario scenario,
                                                             String guid,
                                                             Logger logger,
                                                             Map<String, Object> testParam)
    {
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(null, guid, scenario);
        final VerifyReturnedLatLongInSearchResponse verifier = new VerifyReturnedLatLongInSearchResponse();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, basicVerificationContext, testParam);
        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
        }
        return null;
    }

    public static String latLongLocationIndexLocationCountInSearchResponseVerification(SearchVerificationInput searchVerificationInput,
                                                                                       TestScenario scenario,
                                                                                       String guid,
                                                                                       Logger logger)
    {
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(null, guid, scenario);
        final VerifyLocationCountInSearchResponse verifier = new VerifyLocationCountInSearchResponse();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, basicVerificationContext);
        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + " location count search is failed, Details is in logger.");
        }
        return null;
    }

    public static String prepayPostpayInResponseVerification(List<CarProductType> carProducts,
                                                             TestScenario scenario,
                                                             String guid,
                                                             Logger logger,
                                                             DataSource carsInventoryDatasource)
    {
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(null, guid, scenario);
        final VerifyPrepayPostpaInResponse verifier = new VerifyPrepayPostpaInResponse();
        verifier.setCarsInventoryDatasource(carsInventoryDatasource);
        final IVerification.VerificationResult result = verifier.verify(carProducts, basicVerificationContext);
        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
            }
            Assert.fail(CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT + result);
        }
        return null;
    }

}