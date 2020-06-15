package com.expedia.s3.cars.ecommerce.carbs.service.database.pojo;


import java.util.Date;

/**
 * Created by fehu on 11/16/2016.
 */
public class LanguageWin32{
     private int langID;
    private String languageWin32Name;
    private String languageNameDisplay;
    private String countryCode;
    private String languageISOCode;
    private String statusCode;
    private Date updateDate;
    private String lastUpdatedBy;
    private String localeCode;

    public int getLangID() {
        return langID;
    }

    public void setLangID(int langID) {
        this.langID = langID;
    }

    public String getLanguageWin32Name() {
        return languageWin32Name;
    }

    public void setLanguageWin32Name(String languageWin32Name) {
        this.languageWin32Name = languageWin32Name;
    }

    public String getLanguageNameDisplay() {
        return languageNameDisplay;
    }

    public void setLanguageNameDisplay(String languageNameDisplay) {
        this.languageNameDisplay = languageNameDisplay;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLanguageISOCode() {
        return languageISOCode;
    }

    public void setLanguageISOCode(String languageISOCode) {
        this.languageISOCode = languageISOCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }
}
