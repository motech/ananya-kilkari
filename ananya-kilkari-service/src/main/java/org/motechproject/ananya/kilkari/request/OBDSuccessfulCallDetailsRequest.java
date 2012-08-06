package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;

public class OBDSuccessfulCallDetailsRequest extends CallDetailsRequest {
    public OBDSuccessfulCallDetailsRequest(String subscriptionId, ServiceOption serviceOption, String msisdn, String campaingId, CallDurationRequest callDurationRequest, DateTime createdAt) {
        super(subscriptionId, serviceOption, msisdn, campaingId, callDurationRequest, createdAt, CampaignMessageCallSource.OBD);
    }
}
