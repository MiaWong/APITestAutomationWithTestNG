package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;


/**
 * Created by v-mechen on 1/8/2017.
 */
public class TVRBReq {

    private String isoCountry;
    private String primaryLangID;
    private String referenceId;

    public TVRBReq(Node request) {
        //Read ISOCountry
        this.isoCountry = PojoXmlUtil.getNodeByTagName(request, "Source").getAttributes().getNamedItem("ISOCountry").getTextContent();

        //Read primaryLangID
        if (request.getAttributes().getNamedItem("PrimaryLangID") == null){
            this.primaryLangID = null;
        }
        else{
            this.primaryLangID = request.getAttributes().getNamedItem("PrimaryLangID").getTextContent();
        }

        //Read reference ID
        this.referenceId = PojoXmlUtil.getNodeByTagName(request, "UniqueID").getAttributes().getNamedItem("ID").getTextContent();

    }

    public String getIsoCountry() {
        return this.isoCountry;
    }

    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }

    public String getPrimaryLangID() {
        return this.primaryLangID;
    }

    public void setPrimaryLangID(String primaryLangID) {
        this.primaryLangID = primaryLangID;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

}

