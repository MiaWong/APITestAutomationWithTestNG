package com.expedia.s3.cars.framework.test.common.testdatagenerator.jsonconverter.commonsearchobject;

/**
 * Created by jiyu on 1/13/17.
 */
public class LoyaltyProgram
{
    private Long sequence;
    private String loyaltyProgramCategoryCode;
    private String loyaltyProgramCode;
    private String loyaltyProgramMembershipCode;

    //  getters
    public Long getSequence() { return sequence; }
    public String getLoyaltyProgramCategoryCode() { return loyaltyProgramCategoryCode;}
    public String getLoyaltyProgramCode() { return loyaltyProgramCode; }
    public String getLoyaltyProgramMembershipCode() { return loyaltyProgramMembershipCode; }

    //  setters
    public void setSequence(Long sequence) { this.sequence = sequence; }
    public void setLoyaltyProgramCategoryCode(String loyaltyProgramCategoryCode) { this.loyaltyProgramCategoryCode = loyaltyProgramCategoryCode;}
    public void setLoyaltyProgramCode(String loyaltyProgramCode) { this.loyaltyProgramCode = loyaltyProgramCode; }
    public void setLoyaltyProgramMembershipCode(String loyaltyProgramMembershipCode) { this.loyaltyProgramMembershipCode = loyaltyProgramMembershipCode; }

}
