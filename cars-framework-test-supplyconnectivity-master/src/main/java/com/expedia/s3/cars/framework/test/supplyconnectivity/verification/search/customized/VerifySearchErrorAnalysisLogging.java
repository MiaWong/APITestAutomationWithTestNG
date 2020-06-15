package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized;

import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.DataLogKeys_ErrorAnalysis;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues.SCSErrorAnalysisExpValues;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 8/23/2017.
 */
public class VerifySearchErrorAnalysisLogging implements ISearchVerification{
    @Override
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext) {
        return null;
    }

    public void verify(SearchVerificationInput input, BasicVerificationContext verificationContext
         , List<Map> splunkResult, TestData testdata)
    {
        IVerification.VerificationResult results = new IVerification.VerificationResult(getName(), true, Arrays.asList("Success"));
        //get expected ErrorAnalysis logging value
        final List<Map<String, String>> expValues = SCSErrorAnalysisExpValues.genExpeErrorAnalysisMNSCSSearch(testdata, input.getRequest(), input.getResponse(),
                verificationContext.getSpooferTransactions());


        //Compare result
        if(null == splunkResult || CollectionUtils.isEmpty(splunkResult))
        {
            results = new IVerification.VerificationResult(getName(), false, Arrays.asList("ErrorAnalysis logging for Search is not found in actual values !"));
        }
        else if(CollectionUtils.isNotEmpty(splunkResult) && expValues.size() != splunkResult.size())
        {
            results = new IVerification.VerificationResult(getName(), false, Arrays.asList("Expect values size not equal to actural values size!"
                    + "expect size :" + expValues.size() + "actural size : " + splunkResult.size()));
        }
        else
        {
            results = getVerificationResult(splunkResult, expValues);

        }


        if (!results.isPassed()) {
            Assert.fail(results.toString());
        }


    }

    private VerificationResult getVerificationResult(List<Map> splunkResult, List<Map<String, String>> expValues) {
        VerificationResult results = null;
        final StringBuffer compareResults = new StringBuffer();
        for(int i=0; expValues.size()>i ;i++)
        {
            for(int j=0; splunkResult.size()>j ; j++)
            {
               final String expectedVendorIDValues = expValues.get(i).get(DataLogKeys_ErrorAnalysis.REQUESTSUPPLIERIDS).replace("[", "").replace("]", "").replace("\r\n", "").replace(" ","").trim();
               final String actualVendorIDValues = ((String)splunkResult.get(j).get(DataLogKeys_ErrorAnalysis.REQUESTSUPPLIERIDS)).replace("[", "").replace("]", "").replace("\r\n", "").replace("\"", "").trim();

                if(expValues.get(i).get(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE).equals(splunkResult.get(j).get(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE))
                        && expectedVendorIDValues.equals(actualVendorIDValues))
                {
                    final String compareResult =   CompareUtil.compareSplunkMap(expValues.get(i), splunkResult.get(j));
                    compareResults.append(compareResult);
                    compareResults.append('\n');
                    break;
                }
            }

        }

        if(StringUtil.isNotBlank(compareResults.toString()))
        {
            results = new VerificationResult(getName(), false, Arrays.asList(compareResults.toString()));
        }
        return results;
    }
}
