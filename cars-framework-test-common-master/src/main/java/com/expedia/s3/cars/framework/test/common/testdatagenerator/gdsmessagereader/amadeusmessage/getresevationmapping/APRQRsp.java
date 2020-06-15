package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.getresevationmapping;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.persontypes.defn.v4.ContactInformationType;
import com.expedia.e3.data.persontypes.defn.v4.EmailAddressEntryListType;
import com.expedia.e3.data.persontypes.defn.v4.EmailAddressEntryType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.timetypes.defn.v4.DateRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.CustomerType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSReaderUtil;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping.ARISRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 2/14/2017.
 */
@SuppressWarnings("PMD")
public class APRQRsp
{
    public String buildNumber(Node pnrRetrieveResponse)
    {
        String number = null;

        final String segmentName = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "elementManagementItinerary"), "segmentName").getTextContent();
        final String qualifier = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "elementManagementItinerary"), "reference"), "qualifier").getTextContent();
        if (segmentName.equals("CCR") && qualifier.equals("ST"))
        {
            number = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "elementManagementItinerary"), "reference"), "number").getTextContent();
        }
        return number;
    }

    public void buildBookingStateCode(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        String bookingStateCode = null;

        //List<Node> car_segment = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodesByTagName(pnrRetrieveResponse, "originDestinationDetails"), "itineraryInfo");
        final List<Node> itineraryInfoNodeList = PojoXmlUtil.getNodesByTagName(pnrRetrieveResponse, "itineraryInfo");
        if (null != itineraryInfoNodeList && !itineraryInfoNodeList.isEmpty())
        {
            for (final Node itineraryInfoNode : itineraryInfoNodeList)
            {
                final Node segmentName = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(itineraryInfoNode, "elementManagementItinerary"), "segmentName");
                if (segmentName != null && segmentName.getTextContent().equals("CCR"))
                {
                    final String state = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(itineraryInfoNode, "relatedProduct"), "status").getTextContent();

                    bookingStateCode = ASCSGDSReaderUtil.getBookingStateCode(state);
                }
            }
        }
        if (null != bookingStateCode && carReservation != null)
        {
            carReservation.setBookingStateCode(bookingStateCode);
        }
    }

    public void buildReferenceList(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if (null == carReservation.getReferenceList())
        {
            carReservation.setReferenceList(new ReferenceListType());
        }

        if (null == carReservation.getReferenceList().getReference())
        {
            carReservation.getReferenceList().setReference(new ArrayList<>());
        }

        final ReferenceType referenceVendor = new ReferenceType();
        carReservation.getReferenceList().getReference().add(referenceVendor);
        referenceVendor.setReferenceCategoryCode("Vendor");
        referenceVendor.setReferenceCode("");

        final List<Node> itineraryInfoNodes = PojoXmlUtil.getNodesByTagName(pnrRetrieveResponse, "itineraryInfo");
        if (null != itineraryInfoNodes && !itineraryInfoNodes.isEmpty())
        {
            for (final Node itineraryInfoNode : itineraryInfoNodes)
            {
                final Node segmentName = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(itineraryInfoNode, "elementManagementItinerary"), "segmentName");
                if (segmentName != null && segmentName.getTextContent().equals("CCR"))
                {
                    final List<Node> reservationNodes = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(itineraryInfoNode, "typicalCarData"), "cancelOrConfirmNbr"), "reservation");
                    if (null != reservationNodes && !reservationNodes.isEmpty())
                    {
                        for (final Node reservationNode : reservationNodes)
                        {
                            final Node controlTypeNode = PojoXmlUtil.getNodeByTagName(reservationNode, "controlType");

                            if (controlTypeNode != null && controlTypeNode.getTextContent().equals("2"))
                            {
                                referenceVendor.setReferenceCode(PojoXmlUtil.getNodeByTagName(reservationNode, "controlNumber").getTextContent());
                            }
                        }
                    }
                }
            }
        }

        final ReferenceType referencePNR = new ReferenceType();
        carReservation.getReferenceList().getReference().add(referencePNR);
        referencePNR.setReferenceCategoryCode("PNR");
        referencePNR.setReferenceCode("");
        final Node pnrNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "reservationInfo"), "reservation"), "controlNumber");
        if (null != pnrNode)
        {
            referencePNR.setReferenceCode(pnrNode.getTextContent());
        }
    }

    // CarInventoryKey
    public void buildCarInventoryKey(CarProductType carProductType, Node pnrRetrieveResponse, CarsSCSDataSource scsDataSource, ARISRsp arisResponse) throws DataAccessException
    {
        if(null == carProductType.getCarInventoryKey())
        {
            carProductType.setCarInventoryKey(new CarInventoryKeyType());
        }
        if(null == carProductType.getCarInventoryKey().getCarCatalogKey())
        {
            carProductType.getCarInventoryKey().setCarCatalogKey(new CarCatalogKeyType());
        }

        // 1.Get VendorSupplierID
        final Node carCompanyDataNode = PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "companyIdentification");
        if (carCompanyDataNode != null)
        {
            final String companyCode = PojoXmlUtil.getNodeByTagName(carCompanyDataNode, "companyCode").getTextContent();
            carProductType.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(GDSMsgReadHelper.readVendorSupplierID(scsDataSource, companyCode));
        }

        // 2.CarVehicle
        AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readCarVehicle(carProductType.getCarInventoryKey(), PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "vehicleCharacteristic"), scsDataSource, true);

        // 3.Get CarPickupLocationKey // 4.Get CarDropOffLocationKey
        // AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:locationInfo[1]/:locationDescription/:code
        // AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:locationInfo[1]/:locationType
        buildCarPickupAndDropOffLocationKey(carProductType.getCarInventoryKey(), pnrRetrieveResponse);

        // 5.Get CarPickUpDateTime (For example: 2012-11-24T12:00:00)
        // 6.Get CarDropOffDateTime
        // AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:pickupDropoffTimes/:endDateTime/:month
        final Node typicalCarDataNode = PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData");
        if(null != typicalCarDataNode)
        {
            final Node pickupDropoffTimeNode = PojoXmlUtil.getNodeByTagName(typicalCarDataNode, "pickupDropoffTimes");
            commonNodeReader.readCarPickUpAndDropOffDateTime(carProductType.getCarInventoryKey(), pickupDropoffTimeNode);
        }

        buildCarRate(carProductType.getCarInventoryKey(), pnrRetrieveResponse, scsDataSource, arisResponse);
    }

    private void buildCarPickupAndDropOffLocationKey(CarInventoryKeyType carInventoryKey, Node pnrRetrieveResponse)
    {
        if(null == carInventoryKey.getCarCatalogKey())
        {
            carInventoryKey.setCarCatalogKey(new CarCatalogKeyType());
        }
        // CarPickupLocationKey
        // CarDropOffLocationKey
        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:locationInfo[1]/:locationDescription/:code
        ////:AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:locationInfo[1]/:locationType
        List<Node> locationInfoList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "locationInfo");
        if (locationInfoList != null)
        {
            for (Node locationInfo : locationInfoList)
            {
                Node locationDescNode = PojoXmlUtil.getNodeByTagName(locationInfo, "locationDescription");
                if(null != locationDescNode)
                {
                    String code = PojoXmlUtil.getNodeByTagName(locationDescNode, "code").getTextContent().trim();
                    String name = PojoXmlUtil.getNodeByTagName(locationDescNode, "name").getTextContent().trim();

                    String locationType = PojoXmlUtil.getNodeByTagName(locationInfo, "locationType").getTextContent().trim();
                    if (locationType.equals("176") && code.equals("1A"))
                    {
                        if (null == carInventoryKey.getCarCatalogKey().getCarPickupLocationKey())
                        {
                            carInventoryKey.getCarCatalogKey().setCarPickupLocationKey(new CarLocationKeyType());
                        }
                        carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().setLocationCode(name.substring(0, 3));
                        if (name.length() >= 4)
                        {
                            carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().setCarLocationCategoryCode(name.substring(3, 4));
                        }
                        if (name.length() > 4)
                        {
                            carInventoryKey.getCarCatalogKey().getCarPickupLocationKey().setSupplierRawText("0" + name.substring(4));
                        }

                    } else if (locationType.equals("DOL") && code.equals("1A"))
                    {
                        if (null == carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey())
                        {
                            carInventoryKey.getCarCatalogKey().setCarDropOffLocationKey(new CarLocationKeyType());
                        }
                        carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().setLocationCode(name.substring(0, 3));
                        if (name.length() >= 4)
                        {
                            carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().setCarLocationCategoryCode(name.substring(3, 4));
                        }
                        if (name.length() > 4)
                        {
                            carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().setSupplierRawText("0" + name.substring(4));
                        }
                    }
                }
            }
        }
    }

    // CarInvantoryKe/CarRate
    public void buildCarRate(CarInventoryKeyType carInventoryKey, Node pnrRetrieveResponse, CarsSCSDataSource scsDataSource, ARISRsp arisResponse) throws DataAccessException
    {
        if(null == carInventoryKey.getCarRate())
        {
            carInventoryKey.setCarRate(new CarRateType());
        }

        //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/rateInfo[1]/tariffInfo/rateType
        final List<Node> rateInfos = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "rateInfo");
        if (rateInfos != null)
        {
            for (final Node rateInfo : rateInfos)
            {
                final Node rateType = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateInfo, "tariffInfo"), "rateType");
                if (rateType != null)
                {
                    carInventoryKey.getCarRate().setCarRateQualifierCode(rateType.getTextContent());
                }

                //TODO Mia add this in arisResponse
                // carRate.setRatePeriodCode(arisResponse.getRatePeriodCode());

                // AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/rateInfo[1]/rateInformation/category
                final Node rateInformationNode = PojoXmlUtil.getNodeByTagName(rateInfo, "rateInformation");
                if(null != rateInformationNode)
                {
                    final Node rateCategoryCode = PojoXmlUtil.getNodeByTagName(rateInformationNode, "category");

                    if (rateCategoryCode != null)
                    {
                        String rateCategory = rateCategoryCode.getTextContent();
                        if (isNumberic(rateCategoryCode.getTextContent()))
                        {
                            if (rateCategory.length() == 1)
                            {
                                rateCategory = "00" + rateCategory;
                            } else if (rateCategory.length() == 2)
                            {
                                rateCategory = "0" + rateCategory;
                            }
                        }
                        carInventoryKey.getCarRate().setRateCategoryCode(GDSMsgReadHelper.
                                getDomainValueByDomainTypeAndExternalDomainValue(CommonConstantManager.DomainType.RATE_CATEGORY, rateCategory, scsDataSource));
                    }
                }
                //getRateCatalogCode(rateCategoryCode.InnerText.Trim());
            }
        }
        // AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/rateCodeGroup/rateCodeInfo/fareCategories/fareType
        final Node fareType = PojoXmlUtil.getNodeByTagName(
                PojoXmlUtil.getNodeByTagName(
                        PojoXmlUtil.getNodeByTagName(
                                PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"),
                                        "rateCodeGroup"),
                                "rateCodeInfo"),
                        "fareCategories"),
                "fareType");

        if (fareType != null)
        {
            carInventoryKey.getCarRate().setRateCode(fareType.getTextContent());
        }

        // AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:customerInfo/:customerReferences/:referenceQualifier
        final List<Node> customerReferences = PojoXmlUtil.getNodesByTagName(pnrRetrieveResponse, "customerReferences");
        if (null != customerReferences)
        {
            for (final Node customerReference : customerReferences)
            {
                final Node referenceQualifier = PojoXmlUtil.getNodeByTagName(customerReference, "referenceQualifier");
                if (referenceQualifier != null && referenceQualifier.getTextContent().equals("CD"))
                {
                    // AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:customerInfo/:customerReferences/:referenceNumber
                    carInventoryKey.getCarRate().setCorporateDiscountCode(PojoXmlUtil.getNodeByTagName(customerReference, "referenceNumber").getTextContent());
                }
            }
        }

        //TODO Mia get ratePeriod from request.
        /*
        if (String.IsNullOrEmpty(carRate.RatePeriodCode) && carSCSGetReservationRequest.CarReservationList.CarReservation[0].CarProduct.CarInventoryKey.CarRate.RatePeriodCode != null)
        {
            carRate.RatePeriodCode = carSCSGetReservationRequest.CarReservationList.CarReservation[0].CarProduct.CarInventoryKey.CarRate.RatePeriodCode;
        }
        */
    }

    // CarCatalogMakeModel
    public void buildCarCatalogMakeModel(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if (null == carReservation.getCarProduct().getCarCatalogMakeModel())
        {
            carReservation.getCarProduct().setCarCatalogMakeModel(new CarCatalogMakeModelType());
        }

        if (null != pnrRetrieveResponse)
        {
            final Node originDestinationDetailsNode = PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "originDestinationDetails");
            if(null != originDestinationDetailsNode)
            {
                final Node itineraryInfoNode = PojoXmlUtil.getNodeByTagName(originDestinationDetailsNode, "itineraryInfo");
                if(null != itineraryInfoNode)
                {
                    final Node typicalCarDataNode = PojoXmlUtil.getNodeByTagName(itineraryInfoNode, "typicalCarData");
                    if(null != typicalCarDataNode)
                    {
                        final Node vehicleInformationNode = PojoXmlUtil.getNodeByTagName(typicalCarDataNode, "vehicleInformation");
                        if(null != vehicleInformationNode)
                        {
                            final Node carModel = PojoXmlUtil.getNodeByTagName(vehicleInformationNode, "carModel");
                            if (null != carModel)
                            {
                                carReservation.getCarProduct().getCarCatalogMakeModel().setCarMakeString(carModel.getTextContent());
                            }

                            final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
                            commonNodeReader.readCarDoorCount(carReservation.getCarProduct().getCarCatalogMakeModel(), vehicleInformationNode);
                        }
                    }
                }
            }
        }
    }

    public void buildDeliveryLocation(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if (null == carReservation.getDeliveryLocation())
        {
            carReservation.setDeliveryLocation(new CustomerLocationType());
        }
        readCustomerLocationFromDeliveryAndCollectionNode(carReservation.getDeliveryLocation(), pnrRetrieveResponse, "DEL", "7");
        if(null == carReservation.getDeliveryLocation().getPhone() && null == carReservation.getDeliveryLocation().getAddress()
                && null == carReservation.getDeliveryLocation().getCustomerLocationCode())
        {
            carReservation.setDeliveryLocation(null);
        }
    }

    public void buildCollectionLocation(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if (null == carReservation.getCollectionLocation())
        {
            carReservation.setCollectionLocation(new CustomerLocationType());
        }
        readCustomerLocationFromDeliveryAndCollectionNode(carReservation.getCollectionLocation(), pnrRetrieveResponse, "COL", "117");
        if(null == carReservation.getCollectionLocation().getPhone() && null == carReservation.getCollectionLocation().getAddress()
                && null == carReservation.getCollectionLocation().getCustomerLocationCode())
        {
            carReservation.setCollectionLocation(null);
        }
    }

    // DeliveryLocation
    private void readCustomerLocationFromDeliveryAndCollectionNode(CustomerLocationType customerLocation, Node pnrRetrieveResponse, String locationPurpose, String locationType)
    {
        //In the PNR_Retrive response, if the pnr:originDestinationDetails/pnr:itineraryInfo/pnr:typicalCarData/pnr:locationInfo, Look for Locationtype
        //locationType = '7' :  Delivery location is specified in Reserve request.So, Delivery Location details will come in node: DeliveryLocation
        //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[2]/addressDeliveryCollection
        final List<Node> deliveryAndCollectionList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "deliveryAndCollection");
        if (null == deliveryAndCollectionList || deliveryAndCollectionList.isEmpty())
        {
            final List<Node> locationInfoList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "locationInfo");
            if (locationInfoList != null)
            {
                for (final Node locationInfoNode : locationInfoList)
                {
                    final Node locationTypeNode = PojoXmlUtil.getNodeByTagName(locationInfoNode, "locationType");
                    if (null != locationTypeNode && locationTypeNode.getTextContent().equals(locationType))
                    {
                        final String name = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(locationInfoNode, "locationDescription"), "name").getTextContent();
                        //String phoneNumber = getPhoneNumber();
                        //if (!(String.IsNullOrEmpty(phoneNumber)))
                        //{
                        //    name = name.Substring(0, name.Length - phoneNumber.Length).Trim();
                        //}
                        customerLocation.setPhone(new PhoneType());
                        customerLocation.getPhone().setPhoneNumber(name.split(" ")[1]);
                        final String placeID = name.split(" ")[0];
                        if (isNumberic(placeID))
                        {
                            customerLocation.setCustomerLocationCode(placeID);
                        } else
                        {
                            if (null == customerLocation.getAddress())
                            {
                                customerLocation.setAddress(new AddressType());
                            }
                            customerLocation.getAddress().setFirstAddressLine(name);
                        }
                        break;
                    }
                }
            }
        } else
        {
            for (final Node deliveryAndCollection : deliveryAndCollectionList)
            {
                final Node addressDeliveryCollectionNode = PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "addressDeliveryCollection");

                // AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/
                // addressDeliveryCollection/addressUsageDetails/purpose
                final Node purpose = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "addressUsageDetails"), "purpose");

                if (null != purpose && purpose.getTextContent().equals(locationPurpose))
                {
                    if (null == customerLocation.getAddress())
                    {
                        customerLocation.setAddress(new AddressType());
                    }

                    //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/addressDeliveryCollection/addressDetails/format
                    final Node formatNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "addressDetails"), "format");

                    if (null != formatNode && formatNode.getTextContent().equals("5"))
                    {
                        //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/1[1]/addressDeliveryCollection/addressDetails/line1
                        final Node line1 = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "addressDetails"), "line1");

                        if (null != line1)
                        {
                            customerLocation.getAddress().setFirstAddressLine(line1.getTextContent());
                        }
                    }
                    //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[2]/addressDeliveryCollection/city
                    final Node cityNode = PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "city");
                    if (cityNode != null)
                    {
                        customerLocation.getAddress().setCityName(cityNode.getTextContent());
                    }
                    //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/addressDeliveryCollection/zipCode
                    final Node zipCodeNode = PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "zipCode");
                    if (zipCodeNode != null)
                    {
                        customerLocation.getAddress().setPostalCode(zipCodeNode.getTextContent());
                    }

                    final Node countryCodeNode = PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "countryCode");
                    if (countryCodeNode != null)
                    {
                        customerLocation.getAddress().setCountryAlpha3Code(GDSMsgReadHelper.getCountryAlpha3CodeFromCountryCode(countryCodeNode.getTextContent()));
                    }

                    if(!PojoXmlUtil.getNodesByTagName(addressDeliveryCollectionNode, "regionDetails").isEmpty()) {
                        final Node regionCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "regionDetails"), "code");
                        if (regionCode != null) {
                            customerLocation.getAddress().setProvinceName(regionCode.getTextContent());
                        }
                    }

                    if(!PojoXmlUtil.getNodesByTagName(addressDeliveryCollectionNode, "locationDetails").isEmpty()) {
                        final Node locationCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(addressDeliveryCollectionNode, "locationDetails"), "code");
                        if (locationCode != null) {
                            customerLocation.setCustomerLocationCode(locationCode.getTextContent());
                        }
                    }

                    //	<phoneNumber>
                    /*<phoneOrEmailType>PHO</phoneOrEmailType>
                    <telephoneNumberDetails>
                    <telephoneNumber>2220349999999999999</telephoneNumber>
                    </telephoneNumberDetails>
                    </phoneNumber>
                    </deliveryAndCollection>*/
                    List<Node> phoneNumberNodes = PojoXmlUtil.getNodesByTagName(deliveryAndCollection, "phoneNumber");
                    for(Node phoneNumberNode : phoneNumberNodes)
                    {
                        String phoneOrEmailType = PojoXmlUtil.getNodeByTagName(phoneNumberNode, "phoneOrEmailType").getTextContent();
                        if(phoneOrEmailType.equals("PHO"))
                        {
                            String phoneNumer = PojoXmlUtil.getNodeByTagName(phoneNumberNode, "telephoneNumber").getTextContent();
                            customerLocation.setPhone(new PhoneType());
                            customerLocation.getPhone().setPhoneNumber(phoneNumer);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isNumberic(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    public String buildPhoneNumber(Node pnrRetrieveResponse)
    {
        String phoneNumber = null;
        // CarReservation/TravelerList/Traveler/Person/ContactInformation
        final List<Node> emailList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "dataElementsMaster"), "dataElementsIndiv");
        if (null != emailList && !emailList.isEmpty())
        {
            for (final Node email : emailList)
            {
                final Node segmentName = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(email, "elementManagementData"), "segmentName");
                if (null != segmentName && segmentName.equals("AP"))
                {
                    final Node type = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(email, "otherDataFreetext"), "freetextDetail"), "type");
                    if (null != type)
                    {
                        phoneNumber = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(email, "otherDataFreetext"), "longFreetext").getTextContent();
                    }
                }
            }
        }
        return phoneNumber;
    }

    // TravelerList
    public void buildTravelerList(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if (null == carReservation.getTravelerList())
        {
            carReservation.setTravelerList(new TravelerListType());
        }
        AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.buildTravelerList(carReservation.getTravelerList(), pnrRetrieveResponse, "travellerInformation");
    }

    // Customer
    public void buildCustomer(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if (null == carReservation.getCustomer())
        {
            carReservation.setCustomer(new CustomerType());
        }
        if (null == carReservation.getCustomer().getContactInformation())
        {
            carReservation.getCustomer().setContactInformation(new ContactInformationType());
        }
        if (null == carReservation.getCustomer().getContactInformation().getEmailAddressEntryList())
        {
            carReservation.getCustomer().getContactInformation().setEmailAddressEntryList(new EmailAddressEntryListType());
        }
        if (null == carReservation.getCustomer().getContactInformation().getEmailAddressEntryList().getEmailAddressEntry())
        {
            carReservation.getCustomer().getContactInformation().getEmailAddressEntryList().setEmailAddressEntry(new ArrayList<>());
        }

        final List<Node> emailList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "dataElementsMaster"), "dataElementsIndiv");
        if (null != emailList && !emailList.isEmpty())
        {
            for (final Node email : emailList)
            {
                final Node segmentName = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(email, "elementManagementData"), "segmentName");
                if (null != segmentName && segmentName.getTextContent().equals("AM"))
                {
                    final Node otherDataFreeTextNode = PojoXmlUtil.getNodeByTagName(email, "otherDataFreetext");
                    final Node type = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(otherDataFreeTextNode, "freetextDetail"), "type");
                    final Node subjectQualifier = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(otherDataFreeTextNode, "freetextDetail"), "subjectQualifier");
                    if (null != type && null != subjectQualifier && subjectQualifier.getTextContent().equals("3") && (type.getTextContent().equals("P08") || type.getTextContent().equals("P02")))
                    {
                        final EmailAddressEntryType emailAddressEntry = new EmailAddressEntryType();
                        emailAddressEntry.setEmailAddress(PojoXmlUtil.getNodeByTagName(otherDataFreeTextNode, "longFreetext").getTextContent());
                        carReservation.getCustomer().getContactInformation().getEmailAddressEntryList().getEmailAddressEntry().add(emailAddressEntry);
                    }
                }
            }
        }
    }

    //DateRange
    public DateRangeType buildDateRange(Node pnrRetrieveResponse, String pickupOrDropoffType)
    {
        final DateRangeType dateRange = new DateRangeType();
        DateTime dateTime = null;
        if (pickupOrDropoffType.equals(ASCSGDSReaderUtil.PickupOrDropoffType.PICKUP_TYPE))
        {
            final Node beginDateTimeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "pickupDropoffTimes"), "beginDateTime");
            final String beginYear = PojoXmlUtil.getNodeByTagName(beginDateTimeNode, "year").getTextContent();
            final String beginMonth = PojoXmlUtil.getNodeByTagName(beginDateTimeNode, "month").getTextContent();
            final String beginDay = PojoXmlUtil.getNodeByTagName(beginDateTimeNode, "day").getTextContent();
            if (null != beginYear && null != beginMonth && null != beginDay)
            {
                dateTime = DateTime.getInstanceByDate(Integer.getInteger(beginYear), Integer.getInteger(beginMonth), Integer.getInteger(beginDay));
            }
        } else if (pickupOrDropoffType.equals(ASCSGDSReaderUtil.PickupOrDropoffType.DROPOFF_TYPE))
        {
            final Node endDateTimeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "pickupDropoffTimes"), "endDateTime");
            final String endYear = PojoXmlUtil.getNodeByTagName(endDateTimeNode, "year").getTextContent();
            final String endMonth = PojoXmlUtil.getNodeByTagName(endDateTimeNode, "month").getTextContent();
            final String endDay = PojoXmlUtil.getNodeByTagName(endDateTimeNode, "day").getTextContent();
            if (null != endYear && null != endMonth && null != endDay)
            {
                dateTime = DateTime.getInstanceByDate(Integer.getInteger(endYear), Integer.getInteger(endMonth), Integer.getInteger(endDay));
            }
        }

        if (null != dateTime)
        {
            dateRange.setMinDate(dateTime);
            dateRange.setMaxDate(dateTime);
        }

        return dateRange;
    }

    //CarLocationKey
    public CarLocationKeyType buildCarLocationKey(Node pnrRetrieveResponse, String pickupOrDropoffType)
    {
        final CarLocationKeyType carLocationKey = new CarLocationKeyType();

        final List<Node> locationInfoList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "locationInfo");
        if (locationInfoList != null)
        {
            for (final Node locationInfo : locationInfoList)
            {
                final String locationType = PojoXmlUtil.getNodeByTagName(locationInfo, "locationType").getTextContent();
                final String code = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(locationInfo, "locationDescription"), "code").getTextContent();
                final String name = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(locationInfo, "locationDescription"), "name").getTextContent();
                if (null != locationType && locationType.equals(pickupOrDropoffType) && code.equals("1A"))
                {
                    carLocationKey.setLocationCode(name.substring(0, 3));
                    if (name.length() >= 4)
                    {
                        carLocationKey.setCarLocationCategoryCode(name.substring(3, 4));
                    }
                    if (name.length() >= 6)
                    {
                        carLocationKey.setSupplierRawText("0" + name.substring(4, 6));
                    }
                    break;
                }
            }
        }
        return carLocationKey;
    }

    //Billing number and eVoucher
    public void buildBillingNumberAndeVoucher(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if(null  == carReservation.getPaymentInfo())
        {
            carReservation.setPaymentInfo(new PaymentInfoType());
        }

        ////AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/billingData/billingInfo/billingQualifier
        final Node typicalCarDataNode = PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData");
        if(null != typicalCarDataNode)
        {
            final Node billingDataNode = PojoXmlUtil.getNodeByTagName(typicalCarDataNode, "billingData");
            if (null != billingDataNode)
            {
                final Node billingInfoNode = PojoXmlUtil.getNodeByTagName(billingDataNode, "billingInfo");
                if (null != billingInfoNode)
                {
                    final Node billingQualifier = PojoXmlUtil.getNodeByTagName(billingInfoNode, "billingQualifier");

                    if (billingQualifier != null && billingQualifier.getTextContent().equals("901"))
                    {
                        final Node billingDetails = PojoXmlUtil.getNodeByTagName(billingInfoNode, "billingDetails");
                        if (billingDetails != null)
                        {
                            carReservation.getPaymentInfo().setBillingCode(billingDetails.getTextContent());
                        }
                    }
                }
            }

            Node electronicVoucherNumberNode = PojoXmlUtil.getNodeByTagName(typicalCarDataNode, "electronicVoucherNumber");
            ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/electronicVoucherNumber/documentDetails/number
            if(null != electronicVoucherNumberNode)
            {
                Node documentDetailsNode = PojoXmlUtil.getNodeByTagName(electronicVoucherNumberNode, "documentDetails");
                if(null != documentDetailsNode)
                {
                    final Node eVoucher = PojoXmlUtil.getNodeByTagName(documentDetailsNode, "number");
                    if (eVoucher != null)
                    {
                        carReservation.getPaymentInfo().setVendorPaymentVoucherCode(eVoucher.getTextContent());
                    }
                }
            }
        }

        if(StringUtils.isEmpty(carReservation.getPaymentInfo().getBillingCode()) && StringUtils.isEmpty(carReservation.getPaymentInfo().getPaymentVoucherCode())
                && StringUtils.isEmpty(carReservation.getPaymentInfo().getVendorPaymentVoucherCode())){
            carReservation.setPaymentInfo(null);
        }
    }

    public void buildLoyaltyProgram(CarProductType carProductType, Node pnrRetrieveResponse)
    {
        if(null == carProductType.getCarInventoryKey())
        {
            carProductType.setCarInventoryKey(new CarInventoryKeyType());
        }
        if(null == carProductType.getCarInventoryKey().getCarRate())
        {
            carProductType.getCarInventoryKey().setCarRate(new CarRateType());
        }
        if(null == carProductType.getCarInventoryKey().getCarRate().getLoyaltyProgram())
        {
            carProductType.getCarInventoryKey().getCarRate().setLoyaltyProgram(new LoyaltyProgramType());
        }

        final LoyaltyProgramType loyaltyProgram = carProductType.getCarInventoryKey().getCarRate().getLoyaltyProgram();
        ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/fFlyerNbr/airlineFrequentTraveler/membershipNumber
        final Node typicalCarDataNode = PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData");
        if(null != typicalCarDataNode)
        {
            Node fFlyerNbrNode = PojoXmlUtil.getNodeByTagName(typicalCarDataNode, "fFlyerNbr");
            if(null != fFlyerNbrNode)
            {
                Node airlineFrequentTravelerNode = PojoXmlUtil.getNodeByTagName(fFlyerNbrNode, "airlineFrequentTraveler");
                if(null != airlineFrequentTravelerNode)
                {
                    final Node FrequentFlyerNumber = PojoXmlUtil.getNodeByTagName(airlineFrequentTravelerNode, "membershipNumber");
                    if (FrequentFlyerNumber != null)
                    {
                        loyaltyProgram.setLoyaltyProgramCode("FrequentFlyerNumber");
                        loyaltyProgram.setLoyaltyProgramMembershipCode(FrequentFlyerNumber.getTextContent());
                    }
                }
            }
            ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/customerInfo/customerReferences/referenceQualifier
            final List<Node> customerReferences = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(typicalCarDataNode, "customerInfo"), "customerReferences");
            if (customerReferences != null)
            {
                for (final Node customerReference : customerReferences)
                {
                    final Node referenceQualifier = PojoXmlUtil.getNodeByTagName(customerReference, "referenceQualifier");
                    if (referenceQualifier != null && referenceQualifier.getTextContent().equals("1"))
                    {
                        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:PNR_Reply/:originDestinationDetails/:itineraryInfo/:typicalCarData/:customerInfo/:customerReferences/:referenceNumber
                        final Node loyaltyCardNumber = PojoXmlUtil.getNodeByTagName(customerReference, "referenceNumber");
                        if (loyaltyCardNumber != null)
                        {
                            loyaltyProgram.setLoyaltyProgramCode("LoyaltyCardNumber");
                            loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCardNumber.getTextContent());
                        }
                    }
                }
            }
        }
    }

    public void buildAgencyData(CarReservationType carReservation, Node pnrRetrieveResponse)
    {
        if(null == carReservation.getAgencyData())
        {
            carReservation.setAgencyData(new AgencyDataType());
        }
        final Node officeIDNode = PojoXmlUtil.getNodeByTagName(
                PojoXmlUtil.getNodeByTagName(
                        PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "securityInformation"),
                        "secondRpInformation"),
                "creationOfficeId");
        if (officeIDNode != null)
        {
            carReservation.getAgencyData().setAgencyIdentificationString(officeIDNode.getTextContent());
        }
    }

    public AirFlightType buildAirFlight()
    {
        final AirFlightType airFlight = new AirFlightType();
        ///AmadeusSessionManagerResponse/RawAmadeusXml/Car_SellReply/carSegment/arrivalInfo/inboundCarrierDetails/carrier
        ///
        return airFlight;
    }

    public AdvisoryTextListType buildAdvisoryTextList(Node pnrRetrieveResponse)
    {
        final AdvisoryTextListType advisoryTextList = new AdvisoryTextListType();
        if (null == advisoryTextList.getAdvisoryText())
        {
            advisoryTextList.setAdvisoryText(new ArrayList<>());
        }
        ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/marketingInfo/text[1]
        final List<Node> texts = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "marketingInfo"), "text");
        if (texts != null)
        {
            for (final Node text : texts)
            {
                advisoryTextList.getAdvisoryText().add(text.getTextContent());
            }
        }
        return advisoryTextList;
    }

    // Phone in DeliveryLocation node
    public String buildPhoneOfDeliveryLocation(Node pnrRetrieveResponse)
    {
        String phone = "";

        //AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[2]/addressDeliveryCollection
        final List<Node> deliveryAndCollectionList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "deliveryAndCollection");
        if (null == deliveryAndCollectionList || deliveryAndCollectionList.isEmpty())
        {
            final List<Node> locationInfoList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "locationInfo");
            if (locationInfoList != null)
            {
                for (final Node locationInfo : locationInfoList)
                {
                    final Node locationType = PojoXmlUtil.getNodeByTagName(locationInfo, "locationType");
                    if (locationType != null && locationType.getTextContent().equals("7"))
                    {
                        final String name = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(locationInfo, "locationDescription"), "name").getTextContent();
                        phone = name.split(" ")[1];
                        break;
                    }
                }
            }
        } else
        {
            for (final Node deliveryAndCollection : deliveryAndCollectionList)
            {
                ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/addressDeliveryCollection/addressUsageDetails/purpose
                final Node purpose = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "addressDeliveryCollection"), "addressUsageDetails"), "purpose");

                if (purpose != null && purpose.getTextContent().equals("DEL"))
                {
                    ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/phoneNumber/phoneOrEmailType
                    final Node phoneType = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "phoneNumber"), "phoneOrEmailType");
                    if (phoneType != null && phoneType.getTextContent().equals("PHO"))
                    {
                        final Node phoneNumberNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "phoneNumber"), "telephoneNumberDetails"), "telephoneNumber");
                        if (phoneNumberNode != null)
                        {
                            phone = phoneNumberNode.getTextContent();
                        }
                    }

                }
            }
        }

        return phone;
    }

    // Phone in CollectionLocation node
    public String buildPhoneOfCollectionLocation(Node pnrRetrieveResponse)
    {
        String phone = "";
        ////AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[2]/addressDeliveryCollection
        final List<Node> deliveryAndCollectionList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "deliveryAndCollection");

        if (deliveryAndCollectionList == null || deliveryAndCollectionList.isEmpty())
        {
            final List<Node> locationInfoList = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(pnrRetrieveResponse, "typicalCarData"), "locationInfo");
            if (locationInfoList != null && !locationInfoList.isEmpty())
            {
                for (final Node locationInfo : locationInfoList)
                {
                    final Node locationType = PojoXmlUtil.getNodeByTagName(locationInfo, "locationType");
                    if (locationType != null && locationType.getTextContent().equals("117"))
                    {
                        final String name = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(locationInfo, "locationDescription"), "name").getTextContent();
                        phone = name.split(" ")[1];

                        break;
                    }
                }
            }
        } else
        {
            for (final Node deliveryAndCollection : deliveryAndCollectionList)
            {
                ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/addressDeliveryCollection/addressUsageDetails/purpose
                final Node purpose = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "addressDeliveryCollection"), "addressUsageDetails"), "purpose");
                if (purpose != null && purpose.getTextContent().equals("COL"))
                {
                    ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/deliveryAndCollection[1]/phoneNumber/phoneOrEmailType
                    final Node phoneType = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "phoneNumber"), "phoneOrEmailType");
                    if (phoneType != null && phoneType.getTextContent().equals("PHO"))
                    {
                        final Node phoneNumberNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(deliveryAndCollection, "phoneNumber"), "telephoneNumberDetails"), "telephoneNumber");
                        if (phoneNumberNode != null)
                        {
                            phone = phoneNumberNode.getTextContent();
                        }
                    }

                }
            }
        }
        return phone;
    }
}
