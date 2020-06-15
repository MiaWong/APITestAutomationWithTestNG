package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class Language //implements Serializable
{
    //@SerializedName("LanguageCode")
    String languageCode;
    //@SerializedName("CountryAlpha2Code")
    String countryAlpha2Code;

    //  getters
    public String getLanguageCode() { return this.languageCode; }
    public String getCountryAlpha2Code() { return this.countryAlpha2Code; }

    //  setters
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public void setCountryAlpha2Code(String countryAlpha2Code) { this.countryAlpha2Code = countryAlpha2Code; }

}