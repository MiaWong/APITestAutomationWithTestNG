package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/10/17.
 */
public class AuditLogTrackingData //implements Serializable
{
    //@SerializedName("LogonUserID")
    String logonUserID;
    //@SerializedName("TravelerUserID")
    String travelerUserID;
    //@SerializedName("AuditLogGUID")
    String auditLogGUID;
    //@SerializedName("AuditLogTPID")
    String auditLogTPID;
    //@SerializedName("AuditLogEAPID")
    String auditLogEAPID;
    //@SerializedName("AuditLogGPID")
    String auditLogGPID;
    //@SerializedName("AuditLogLanguageId")
    String auditLogLanguageId;
    //@SerializedName("AuditLogForceLogging")
    boolean auditLogForceLogging;
    //@SerializedName("AuditLogForceDownstreamTransaction")
    boolean auditLogForceDownstreamTransaction;

    //  getters
    public String getLogonUserID() { return this.logonUserID; }
    public String getTravelerUserID() { return this.travelerUserID; }
    public String getAuditLogGUID() { return this.auditLogGUID; }
    public String getAuditLogTPID() { return this.auditLogTPID; }
    public String getAuditLogEAPID() { return this.auditLogEAPID; }
    public String getAuditLogGPID() { return this.auditLogGPID; }
    public String getAuditLogLanguageId() { return this.auditLogLanguageId; }
    public boolean isAuditLogForceLogging() { return this.auditLogForceLogging; }
    public boolean isAuditLogForceDownstreamTransaction() { return this.auditLogForceDownstreamTransaction; }


    //  setters
    public void setLogonUserID(String logonUserID) { this.logonUserID = logonUserID; }
    public void setTravelerUserID(String travelerUserID) { this.travelerUserID = travelerUserID; }
    public void setAuditLogGUID(String auditLogGUID) { this.auditLogGUID = auditLogGUID; }
    public void setAuditLogTPID(String auditLogTPID) { this.auditLogTPID = auditLogTPID; }
    public void setAuditLogEAPID(String auditLogEAPID) { this.auditLogEAPID = auditLogEAPID; }
    public void setAuditLogGPID(String auditLogGPID) { this.auditLogGPID = auditLogGPID; }
    public void setAuditLogLanguageId(String auditLogLanguageId) { this.auditLogLanguageId = auditLogLanguageId; }
    public void setAuditLogForceLogging(boolean auditLogForceLogging) { this.auditLogForceLogging = auditLogForceLogging; }
    public void setAuditLogForceDownstreamTransaction(boolean auditLogForceDownstreamTransaction) { this.auditLogForceDownstreamTransaction = auditLogForceDownstreamTransaction; }

}
