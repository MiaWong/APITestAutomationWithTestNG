package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;

import java.util.*;

/**
 * Created by jiyu on 9/15/16.
 */
@SuppressWarnings("PMD")
public class VerifyPhoneListReceivedInResponse extends PhoneListCaseInGetDetails
{
    private static final String  MESSAGE_NO_CAR_PRODUCT_IN_PESPONSE = "No car product list in input response.";
    private static final String  MESSAGE_NO_PHONELIST_IN_PESPONSE = "No phone list in input response.";
    private static final String  TAG_PHONE_CATEGORY = "Phone category";
    private static final String  MESSAGE_NO_MATCH_CATEGORY = " is not a matched Phone category";
    private static final String  MESSAGE_NO_VALID_CATEGORY = " is not valid Phone category";
    private static final String  MESSAGE_EMPTY = " is empty!";
    private static final String  MESSAGE_NO_MATCH_PHONE = "PhoneList is not matched in Request/Response. Please debug!";

    /*
    Expedia domain values for Phone category:
    0          unknown
    1          Home
    2          Business
    6          Fax
    10         Mobile

    OTA domain values Phone Technology Type           PTT:
    1          Voice
    2          Data
    3          Fax
    4          Pager
    5          Mobile
    6          TTY
    7          Telex
    8          Voice over IP
    */

    public VerifyPhoneListReceivedInResponse()
    {
        //  Expedia domain values for Phone category:
        if (phoneCatagory == null) {
            phoneCatagory = new HashMap<>();
            phoneCatagory.put("0", "Unknown");
            phoneCatagory.put("1", "Home");
            phoneCatagory.put("2", "Business");
            phoneCatagory.put("6", "Fax");
            phoneCatagory.put("10","Mobile");
        }

        //  OTA domain values Phone Technology Type PTT:
        if (phoneTechnology == null) {
            phoneTechnology = new HashMap<>();
            phoneTechnology.put("1", "Voice");
            phoneTechnology.put("2", "Data");
            phoneTechnology.put("3", "Fax");
            phoneTechnology.put("4", "Pager");
            phoneTechnology.put("5", "Mobile");
            phoneTechnology.put("6", "TTY");
            phoneTechnology.put("7", "Telex");
            phoneTechnology.put("8", "Voice over IP");
        }

    }

    private static Map<String, String> phoneCatagory = null;
    private static Map<String, String> phoneTechnology = null;

    @SuppressWarnings("CPD-START")
    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        return Optional.ofNullable(input.getRequest())
                .map(CarSupplyConnectivityGetDetailsRequestType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(car -> car == null ? null : car.get(0))
                .map(CarProductType::getCarPickupLocation)
                .map(CarLocationType::getPhoneList)
                .isPresent() &&
               Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(car -> car == null ? null : car.get(0))
                .map(CarProductType::getCarPickupLocation)
                .map(CarLocationType::getPhoneList)
                .isPresent();
    }

    @SuppressWarnings("CPD-END")
    private String verifyPhoneList(List<CarProductType> carListInRequest, List<CarProductType> carListInResponse)
    {
        String errorMessage = "";

        HashMap<String, PhoneType> phoneListInRequest = new HashMap<>();
        for (final PhoneType phoneInRequest : carListInRequest.get(0).getCarPickupLocation().getPhoneList().getPhone()) {
            phoneListInRequest.put(phoneInRequest.getPhoneCategoryCode(), phoneInRequest);
        }

        PhoneType phoneSearch = null;

        for (final PhoneType phone: carListInResponse.get(0).getCarPickupLocation().getPhoneList().getPhone()) {

            if (phoneListInRequest.containsKey(phone.getPhoneCategoryCode())) {
                phoneSearch = phoneListInRequest.get(phone.getPhoneCategoryCode());
                phoneListInRequest.remove(phone.getPhoneCategoryCode());
            }
            else {
                phoneSearch = null;
                errorMessage = phone.getPhoneCategoryCode() + MESSAGE_NO_MATCH_CATEGORY;
                continue;
            }

            if (!phoneCatagory.containsKey(phone.getPhoneCategoryCode())) {
                errorMessage = phone.getPhoneCategoryCode() + MESSAGE_NO_VALID_CATEGORY;
                continue;
            }

            if (null == phone.getPhoneCountryCode() &&
                null == phone.getPhoneAreaCode() &&
                null == phone.getPhoneNumber()) {
                errorMessage = TAG_PHONE_CATEGORY + phone.getPhoneCategoryCode() + MESSAGE_EMPTY;
                continue;
            }

            if (phone.getPhoneCategoryCode().equals(phoneSearch.getPhoneCategoryCode()) &&
                    phone.getPhoneCountryCode().equals(phoneSearch.getPhoneCountryCode()) &&
                    phone.getPhoneAreaCode().equals(phoneSearch.getPhoneAreaCode()) &&
                    phone.getPhoneNumber().equals(phone.getPhoneNumber())) {
                //  got matched phone
                continue;
            }
        }

        if (!phoneListInRequest.isEmpty()) {
            errorMessage = MESSAGE_NO_MATCH_PHONE;
        }
        return errorMessage;
    }


    @Override
    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        if (!Optional.ofNullable(input.getResponse())
                .map(CarSupplyConnectivityGetDetailsResponseType::getCarProductList)
                .map(CarProductListType::getCarProduct)
                .map(car -> car == null ? null : car.get(0))
                .map(CarProductType::getCarPickupLocation)
                .map(CarLocationType::getPhoneList)
                .map(PhoneListType::getPhone)
                .isPresent() )
        {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(MESSAGE_NO_PHONELIST_IN_PESPONSE));
        }

        String errorMessage = verifyPhoneList(
                input.getRequest().getCarProductList().getCarProduct(),
                input.getResponse().getCarProductList().getCarProduct());

        return new IVerification.VerificationResult(getName(), errorMessage.isEmpty(), Arrays.asList(errorMessage));
    }
}
