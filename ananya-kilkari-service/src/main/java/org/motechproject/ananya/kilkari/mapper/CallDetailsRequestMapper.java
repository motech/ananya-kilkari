package org.motechproject.ananya.kilkari.mapper;


import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class CallDetailsRequestMapper {
    public OBDSuccessfulCallDetailsRequest mapOBDRequest(OBDSuccessfulCallDetailsWebRequest webRequest) {
        CallDurationWebRequest callDurationWebRequest = webRequest.getCallDurationWebRequest();
        CallDurationRequest callDurationRequest = new CallDurationRequest(DateUtils.parseDateTime(callDurationWebRequest.getStartTime()), DateUtils.parseDateTime(callDurationWebRequest.getEndTime()));
        return new OBDSuccessfulCallDetailsRequest(webRequest.getSubscriptionId(), ServiceOption.getFor(webRequest.getServiceOption()),
                webRequest.getMsisdn(), webRequest.getCampaignId(), callDurationRequest, webRequest.getCreatedAt());
    }
}
