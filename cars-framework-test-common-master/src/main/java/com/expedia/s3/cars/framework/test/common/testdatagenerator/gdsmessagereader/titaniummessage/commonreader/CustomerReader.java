package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.persontypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressListType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.traveltypes.defn.v4.CustomerType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 2/28/2017.
 */
public class CustomerReader {
    /// <summary>
    /// Build TravelerList Customer
    /// </summary>
    /// <param name="customer"></param>
    /// <returns></returns>
    public TravelerListType readTravelerList(CustomerType customer)
    {
        final TravelerListType travelerList = new TravelerListType();
        travelerList.setTraveler(new ArrayList<TravelerType>());
        final TravelerType traveler = new TravelerType();
        traveler.setPerson(customer.getPerson());
        traveler.setContactInformation(customer.getContactInformation());
        travelerList.getTraveler().add(traveler);
        return travelerList;
    }

    /// <summary>
    /// Read customer
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public CustomerType readCustomer(Node gdsNode)
    {
        final CustomerType customer = new CustomerType();
        final Node customerNode = PojoXmlUtil.getNodeByTagName(gdsNode, "Customer");

        //Name
        final Node nameNode = PojoXmlUtil.getNodeByTagName(customerNode, "PersonName");
        customer.setPerson(new PersonType());
        customer.getPerson().setPersonName(new PersonNameType());
        if (!PojoXmlUtil.getNodesByTagName(nameNode, "GivenName").isEmpty())
        {
            customer.getPerson().getPersonName().setFirstName(PojoXmlUtil.getNodeByTagName(nameNode, "GivenName").getTextContent());
        }
        if (!PojoXmlUtil.getNodesByTagName(nameNode, "Surname").isEmpty())
        {
            customer.getPerson().getPersonName().setLastName(PojoXmlUtil.getNodeByTagName(nameNode, "Surname").getTextContent());
        }

        //Contact info
        customer.setContactInformation(new ContactInformationType());
        //Phone number
        customer.getContactInformation().setPhoneList(new PhoneListType());
        customer.getContactInformation().getPhoneList().setPhone(new ArrayList<PhoneType>());
        final List<Node> phoneNumNodeList = PojoXmlUtil.getNodesByTagName(customerNode, "Telephone");
        for (final Node phoneNumNode : phoneNumNodeList)
        {
            final PhoneType phone = new PhoneType();
            //TODO: not sure if we will parse the number to CountryCode/AreaCode/PhoneNumber and Extension or not
            //if (phoneNumNode.Attributes["AreaCode"] != null) phone.PhoneAreaCode = phoneNumNode.Attributes["AreaCode"].Value;
            //if (phoneNumNode.Attributes["CountryCode"] != null) phone.PhoneCountryCode = phoneNumNode.Attributes["CountryCode"].Value;
            if (null != phoneNumNode.getAttributes().getNamedItem("PhoneNumber"))
            {
                phone.setPhoneNumber(phoneNumNode.getAttributes().getNamedItem("PhoneNumber").getTextContent());
            }
            customer.getContactInformation().getPhoneList().getPhone().add(phone);
        }
        //eMail
        customer.getContactInformation().setEmailAddressEntryList(new EmailAddressEntryListType());
        customer.getContactInformation().getEmailAddressEntryList().setEmailAddressEntry(new ArrayList<EmailAddressEntryType>());
        final List<Node> emailNodeList = PojoXmlUtil.getNodesByTagName(customerNode, "Email");
        for (final Node emailNode : emailNodeList)
        {
            final EmailAddressEntryType email = new EmailAddressEntryType();
            email.setEmailAddress(emailNode.getTextContent());
            customer.getContactInformation().getEmailAddressEntryList().getEmailAddressEntry().add(email);
        }

        //Address
        readAddress(customer, customerNode);


        ////LoyaltyNumber TODO: not sure if we can send loyalty number or not
        //XmlNodeList loyaltyCardNL = travelerXmlDoc.GetElementsByTagName("LoyaltyCard", uapiNameSpace.all);
        //foreach (XmlNode loyaltyCardN in loyaltyCardNL)
        //{
        //    LoyaltyProgram loyalty = new LoyaltyProgram();
        //    if (loyaltyCardN.Attributes["SupplierType"].Value == "Vehicle") loyalty.LoyaltyProgramCategoryCode = "Car";
        //    if (loyaltyCardN.Attributes["SupplierType"].Value == "Air") loyalty.LoyaltyProgramCategoryCode = "Air";
        //    loyalty.LoyaltyProgramCode = loyaltyCardN.Attributes["SupplierCode"].Value;
        //    loyalty.LoyaltyProgramMembershipCode = loyaltyCardN.Attributes["CardNumber"].Value;
        //    traveler.LoyaltyProgramList.Add(loyalty);
        //}


        return customer;
    }

    public void readAddress(CustomerType customer, Node customerNode) {
        customer.getContactInformation().setAddressList(new AddressListType());
        customer.getContactInformation().getAddressList().setAddress(new ArrayList<AddressType>());
        final List<Node> addressNodeList = PojoXmlUtil.getNodesByTagName(customerNode, "Address");
        for (final Node addressNode : addressNodeList) {
            final AddressType address = new AddressType();
            address.setAddressCategoryCode("Home");
            if (!PojoXmlUtil.getNodesByTagName(addressNode, "AddressLine").isEmpty()) {
                address.setFirstAddressLine(PojoXmlUtil.getNodeByTagName(addressNode, "AddressLine").getTextContent());
            }
            address.setSecondAddressLine("");
            if (!PojoXmlUtil.getNodesByTagName(addressNode, "CityName").isEmpty()) {
                address.setCityName(PojoXmlUtil.getNodeByTagName(addressNode, "CityName").getTextContent());
            }
            if (!PojoXmlUtil.getNodesByTagName(addressNode, "PostalCode").isEmpty()) {
                address.setPostalCode(PojoXmlUtil.getNodeByTagName(addressNode, "PostalCode").getTextContent());
            }
            if (!PojoXmlUtil.getNodesByTagName(addressNode, "StateProv").isEmpty()) {
                if (null == PojoXmlUtil.getNodeByTagName(addressNode, "StateProv").getAttributes().getNamedItem("StateCode")) {
                    address.setProvinceName(PojoXmlUtil.getNodeByTagName(addressNode, "StateProv").getTextContent());
                } else {
                    address.setProvinceName(PojoXmlUtil.getNodeByTagName(addressNode, "StateProv").getAttributes().getNamedItem("StateCode").getTextContent());
                }
            }
            if (!PojoXmlUtil.getNodesByTagName(customerNode, "CitizenCountryName").isEmpty()) {
                final String gdsCountryCode = PojoXmlUtil.getNodeByTagName(customerNode, "CitizenCountryName").getAttributes().getNamedItem("Code").getTextContent();
                address.setCountryAlpha3Code(GDSMsgReadHelper.getCountryAlpha3CodeFromCountryCode(gdsCountryCode));
            }

            customer.getContactInformation().getAddressList().getAddress().add(address);
        }
    }
}
