package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.core.activity.ActivityCreationConfiguration;
import com.expedia.s3.cars.framework.core.activity.ActivitySystem;
import com.expedia.s3.cars.framework.servicerequest.IServiceTransport;

/**
 * Created by sswaminathan on 8/4/16.
 */
public class RequestSender
{
    private RequestSender()
    {
    }

    public static void sendWithTransport(IServiceTransport<?,?,?> transport, String originatingGuid)
    {
        ActivitySystem.runInNewContext(new ActivityCreationConfiguration("ServiceTransportActivity",
                originatingGuid, "ServiceTransportOperation", transport));
    }

}
