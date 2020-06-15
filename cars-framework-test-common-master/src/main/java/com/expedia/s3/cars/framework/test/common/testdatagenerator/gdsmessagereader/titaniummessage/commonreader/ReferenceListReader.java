package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 2/28/2017.
 */
public class ReferenceListReader {
    private ReferenceListReader(){

    }
    /// <summary>
    /// Read referenceList
    /// </summary>
    /// <param name="xmlDoc"></param>
    /// <returns></returns>
    public static ReferenceListType readReferenceList(Node response)
    {
        final ReferenceListType referenceList = new ReferenceListType();
        referenceList.setReference(new ArrayList<ReferenceType>());

        //<ConfID ID="21199600" Instance="1" Type="14"></ConfID>
        //PNR from ConfID
        final List<Node> confIDList = PojoXmlUtil.getNodesByTagName(response,"ConfID");
        for (final Node confID : confIDList)
        {
            if (confID.getAttributes().getNamedItem("Type").getTextContent().equals("14"))
            {
                final ReferenceType reference = new ReferenceType();
                reference.setReferenceCode(confID.getAttributes().getNamedItem("ID").getTextContent());
                reference.setReferenceCategoryCode("PNR");
                referenceList.getReference().add(reference);
                continue;
            }
            if (confID.getAttributes().getNamedItem("Type").getTextContent().equals("25"))
            {
                final ReferenceType reference = new ReferenceType();
                reference.setReferenceCode(confID.getAttributes().getNamedItem("ID").getTextContent());
                reference.setReferenceCategoryCode("Vendor");
                referenceList.getReference().add(reference);
                continue;
            }
        }

        return referenceList;
    }
}
