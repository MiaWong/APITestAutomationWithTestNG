package com.expedia.s3.cars.framework.test.common.execution.verification;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import org.w3c.dom.Document;

/**
 * A simple verification context that has the spoofer transactions, the test scenario and the originating guid
 * Created by sswaminathan on 8/9/16.
 */
public class BasicVerificationContext
{
    private Document spooferTransactions;
    private final String originatingGuid;
    private final TestScenario scenario;

    public BasicVerificationContext(Document spooferTransactions, String originatingGuid, TestScenario scenario)
    {
        this.spooferTransactions = spooferTransactions;
        this.originatingGuid = originatingGuid;
        this.scenario = scenario;
    }

    public Document getSpooferTransactions()
    {
        return spooferTransactions;
    }

    public void setSpooferTransactions(Document doc)
    {
        this.spooferTransactions = doc;
    }

    public String getOriginatingGuid()
    {
        return originatingGuid;
    }

    public TestScenario getScenario()
    {
        return scenario;
    }
}
