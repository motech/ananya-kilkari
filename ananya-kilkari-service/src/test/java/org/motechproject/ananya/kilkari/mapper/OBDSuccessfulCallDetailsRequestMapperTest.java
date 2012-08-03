package org.motechproject.ananya.kilkari.mapper;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import static org.junit.Assert.assertEquals;

public class OBDSuccessfulCallDetailsRequestMapperTest {
    @Test
    public void shouldMapFromWebRequest(){
        String subscriptionId = "subscriptionId";
        String campaignId= "WEEK34";
        String msisdn = "1234567890";
        OBDSuccessfulCallDetailsRequestMapper requestMapper = new OBDSuccessfulCallDetailsRequestMapper();
        OBDSuccessfulCallDetailsWebRequest webRequest = new OBDSuccessfulCallDetailsWebRequest();
        webRequest.setServiceOption(ServiceOption.HELP.name());
        webRequest.setSubscriptionId(subscriptionId);
        CallDurationWebRequest callDurationWebRequest = new CallDurationWebRequest();
        callDurationWebRequest.setEndTime("23-12-2012 12-59-34");
        callDurationWebRequest.setStartTime("22-11-2011 11-55-35");
        webRequest.setCallDurationWebRequest(callDurationWebRequest);
        webRequest.setCampaignId(campaignId);
        webRequest.setMsisdn(msisdn);

        OBDSuccessfulCallDetailsRequest request = requestMapper.map(webRequest);
        assertEquals(ServiceOption.HELP, request.getServiceOption());
        assertEquals(webRequest.getCreatedAt(), request.getCreatedAt());
        assertEquals(subscriptionId, request.getSubscriptionId());
        assertEquals(new DateTime(2012, 12, 23, 12, 59, 34), request.getCallDurationRequest().getEndTime());
        assertEquals(new DateTime(2011, 11, 22, 11, 55, 35), request.getCallDurationRequest().getStartTime());
        assertEquals(msisdn, request.getMsisdn());
        assertEquals(campaignId, request.getCampaignId());
        assertEquals(CampaignMessageCallSource.OBD, request.getCallSource());
        assertEquals(Channel.IVR, request.getChannel());
    }
}
