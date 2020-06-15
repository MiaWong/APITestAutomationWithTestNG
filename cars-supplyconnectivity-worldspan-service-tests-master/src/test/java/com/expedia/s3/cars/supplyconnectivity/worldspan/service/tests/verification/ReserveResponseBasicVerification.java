package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.basic.VerifyReserveBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jiyu on 8/29/16.
 */
public class ReserveResponseBasicVerification implements IReserveVerification {
    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarReservation());
        Assert.assertNotNull(input.getResponse().getCarReservation().getCarProduct());

        //invoke a chain of driver age verifications and return the result...
        ChainedVerification<ReserveVerificationInput, BasicVerificationContext> verifications =
                new ChainedVerification<>(getName(), Arrays.asList(new VerifyReserveBasic()));

        return verifications.verify(input, verificationContext);
    }

    public static void verifyCarLocationInfo(BasicVerificationContext basicVerificationContext,CarSupplyConnectivityReserveResponseType response, DataSource carWorldSpandataSource,DataSource carsInventorydatasource) throws DataAccessException {
        verifySpooferReqCarLocationInfo(basicVerificationContext,carWorldSpandataSource,carsInventorydatasource);
        verifySCSResCarLocationInfo(response,carsInventorydatasource);
    }

    private static void verifySpooferReqCarLocationInfo(BasicVerificationContext basicVerificationContext, DataSource carWorldSpandataSource,DataSource carsInventorydatasource) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder();
        final Document spooferDoc = basicVerificationContext.getSpooferTransactions();
        if (spooferDoc == null) {
            errorMsg.append(" GDS Request is null! ");
        } else {
            final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(carWorldSpandataSource);
            final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
            final NodeList reqNodeList = spooferDoc.getElementsByTagName("Request");
            if (reqNodeList != null && reqNodeList.getLength() > 0) {
                for (int i = 0; i < reqNodeList.getLength(); i++) {
                    final Node reqNodeRoot = reqNodeList.item(i);
                    final Node reqNode = reqNodeRoot.getFirstChild().getFirstChild().getFirstChild();
                    final String reqNodeName = reqNode.getNodeName();
                    if ("veh:VehicleSearchAvailabilityReq".equals(reqNodeName)) {
                        final NodeList vendorLocationList = reqNode.getChildNodes().item(1).getChildNodes();
                        if (vendorLocationList != null && vendorLocationList.getLength() > 0) {
                            for (int j = 0; j < vendorLocationList.getLength(); j++) {
                                final Node locationNode = vendorLocationList.item(j);
                                final NamedNodeMap attrs = locationNode.getAttributes();
                                final String locationCode = attrs.getNamedItem("LocationCode").getNodeValue();
                                final String vendorCode = attrs.getNamedItem("VendorCode").getNodeValue();
                                final String vendorLocationID = attrs.getNamedItem("VendorLocationID").getNodeValue().substring(1);
                                final List<CarVendor> carVendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                                final List<ExternalSupplyServiceDomainValueMap> locationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", locationCode + "%" + vendorLocationID, carVendorList);
                                if (CollectionUtils.isEmpty(locationMapList)) {
                                    errorMsg.append(" VendorLocation send to GDS request is error! ");
                                    break;
                                }
                            }
                        }
                    } else if ("veh:VehicleRulesReq".equals(reqNodeName)) {
                        final Node vendorLocationNode = reqNode.getChildNodes().item(1).getFirstChild();
                        final Node vendorCodeNode = reqNode.getChildNodes().item(1).getLastChild().getFirstChild().getFirstChild();
                        final NamedNodeMap locationAttrs = vendorLocationNode.getAttributes();
                        final String startLocationCode = locationAttrs.getNamedItem("PickupLocation").getNodeValue();
                        final String startVendorLocationID = locationAttrs.getNamedItem("PickupLocationNumber").getNodeValue().substring(1);
                        final String endLocationCode = locationAttrs.getNamedItem("ReturnLocation").getNodeValue();
                        final String endVendorLocationID = locationAttrs.getNamedItem("ReturnLocationNumber").getNodeValue().substring(1);
                        final String vendorCode = vendorCodeNode.getAttributes().getNamedItem("Code").getNodeValue();
                        final List<CarVendor> carVendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                        final List<ExternalSupplyServiceDomainValueMap> startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", startLocationCode + "%" + startVendorLocationID, carVendorList);
                        final List<ExternalSupplyServiceDomainValueMap> endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", endLocationCode + "%" + endVendorLocationID, carVendorList);
                        if (CollectionUtils.isEmpty(startLocationMapList) || CollectionUtils.isEmpty(endLocationMapList)) {
                            errorMsg.append(" VendorLocation send to GDS request is error! ");
                        }

                    }
                    if (!StringUtils.isEmpty(String.valueOf(errorMsg))) {
                        break;
                    }
                }
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }

    }

    private static void verifySCSResCarLocationInfo(CarSupplyConnectivityReserveResponseType response, DataSource carsInventorydatasource) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder("");
        if (null != response.getCarReservation() && null != response.getCarReservation().getCarProduct()) {
            final CarProductType productType = response.getCarReservation().getCarProduct();
            final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
            final CarLocationKeyType startLocationReturn2 = productType.getCarPickupLocation().getCarLocationKey();
            final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
            final CarLocationKeyType endLocationReturn2 = productType.getCarDropOffLocation().getCarLocationKey();
            if ((null == startLocationReturn.getCarVendorLocationID() || 0L == startLocationReturn.getCarVendorLocationID()) || (null == startLocationReturn2.getCarVendorLocationID() || 0L == startLocationReturn2.getCarVendorLocationID())) {
                errorMsg.append("No CarVendorLocationID returned in CarPickupLocationKey or CarPickupLocation!");
            }
            if ((null == endLocationReturn.getCarVendorLocationID() || 0L == endLocationReturn.getCarVendorLocationID()) || (null == endLocationReturn2.getCarVendorLocationID() || 0L == endLocationReturn2.getCarVendorLocationID())) {
                errorMsg.append("No CarVendorLocationID returned in CarDropOffLocationKey or CarDropOffLocation!");
            }
            final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
            final CarVendorLocation startLocationVerify = carsInventoryHelper.getCarLocation(startLocationReturn.getCarVendorLocationID());
            final CarVendorLocation endLocationVerify = carsInventoryHelper.getCarLocation(endLocationReturn.getCarVendorLocationID());
            if (startLocationVerify == null) {
                errorMsg.append("No matched pick up CarVendorLocation recode find in DB CarVendorLocationID!");
            }
            if (endLocationVerify == null) {
                errorMsg.append("No matched drop off CarVendorLocation recode find in DB by CarVendorLocationID!");
            }
            if (!startLocationReturn.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startLocationReturn.getCarLocationCategoryCode().equals(startLocationVerify.getCarLocationCategoryCode()) || !startLocationReturn.getSupplierRawText().equals(startLocationVerify.getSupplierRawText())
                    || !startLocationReturn2.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startLocationReturn2.getCarLocationCategoryCode().equals(startLocationVerify.getCarLocationCategoryCode()) || !startLocationReturn2.getSupplierRawText().equals(startLocationVerify.getSupplierRawText())) {
                errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
            }
            if (!endLocationReturn.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endLocationReturn.getCarLocationCategoryCode().equals(endLocationVerify.getCarLocationCategoryCode()) || !endLocationReturn.getSupplierRawText().equals(endLocationVerify.getSupplierRawText())
                    || !endLocationReturn2.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endLocationReturn2.getCarLocationCategoryCode().equals(endLocationVerify.getCarLocationCategoryCode()) || !endLocationReturn2.getSupplierRawText().equals(endLocationVerify.getSupplierRawText())) {
                errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
            }

        }
        if (!StringUtils.isEmpty(String.valueOf(errorMsg))) {
            Assert.fail(errorMsg.toString());
        }
    }
}
