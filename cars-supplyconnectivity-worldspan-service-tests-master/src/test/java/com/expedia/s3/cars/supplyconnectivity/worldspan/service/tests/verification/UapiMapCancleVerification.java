package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

/**
 * Created by yyang4 on 1/5/2017.
 */
public class UapiMapCancleVerification {

    private static final Logger logger = Logger.getLogger(SearchResponsesBasicVerification.class);

    public static void VerifyWSCSCancelForuAPI(BasicVerificationContext verificationContext, CancelVerificationInput verificationInput, DataSource scsDataSource, DataSource carsInventoryDs, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Document gdsMessageDoc = verificationContext.getSpooferTransactions();
        final CarSupplyConnectivityCancelResponseType scsRsp = verificationInput.getResponse();
        if (CompareUtil.isObjEmpty(gdsMessageDoc)) {
            org.testng.Assert.fail("No GDS messages found ! ");
        }
        // 1. Verify cancel through DIR when cancel.uAPI.enable =0, otherwise, cancel through uAPI
        List<Node> pnrCancelCrsdata = PojoXmlUtil.getNodesByTagName(gdsMessageDoc.getFirstChild(),GDSMsgNodeTags.WorldSpanNodeTags.VCRQ_REQUEST_TYPE);

        if (CompareUtil.isObjEmpty(pnrCancelCrsdata)) {
            Assert.fail("Get PNRi uAPI cancel message faild in CRS log: when Cancel.uAPI.enable=1.");
        }

        // 2. Response mapping verify
        if (!"Cancelled".equals(scsRsp.getCarReservation().getBookingStateCode())) {
            Assert.fail(String.format("BookingStateCode=%s returned in SCS Cancel response, it is not equal the expected value=Cancelled}", scsRsp.getCarReservation().getBookingStateCode()));
        }
    }
}

