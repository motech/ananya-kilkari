package org.motechproject.ananya.kilkari.obd.service.request;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;

public class OBDSuccessfulCallDetailsRequestBuilder {
    private String subscriptionId;
    private String campaignId;
    private String msisdn;
    private ServiceOption serviceOption;
    private DateTime startTime;
    private DateTime endTime;

    public OBDSuccessfulCallDetailsRequestBuilder withDefaults() {
        subscriptionId = "subscriptionId";
        campaignId = "WEEK1";
        msisdn = "1234567890";
        serviceOption = ServiceOption.HELP;
        startTime = DateTime.now();
        endTime = DateTime.now();
        return this;
    }

    public OBDSuccessfulCallDetailsRequest build() {
        return new OBDSuccessfulCallDetailsRequest(subscriptionId, serviceOption, msisdn, campaignId, new CallDurationRequest(startTime, endTime), DateTime.now());
    }
}
