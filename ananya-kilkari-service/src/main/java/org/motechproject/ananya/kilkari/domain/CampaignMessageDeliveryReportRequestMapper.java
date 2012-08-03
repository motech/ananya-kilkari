package org.motechproject.ananya.kilkari.domain;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.reporting.domain.CallDurationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;

public class CampaignMessageDeliveryReportRequestMapper {
    public CampaignMessageDeliveryReportRequest mapFrom(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest, Integer retryCount) {
        CallDurationRequest callDurationRequest = obdSuccessfulCallDetailsRequest.getCallDurationRequest();
        CallDurationReportRequest callDurationReportRequest = new CallDurationReportRequest(callDurationRequest.getStartTime(), callDurationRequest.getEndTime());
        return new CampaignMessageDeliveryReportRequest(
                obdSuccessfulCallDetailsRequest.getSubscriptionId(),
                obdSuccessfulCallDetailsRequest.getMsisdn(),
                obdSuccessfulCallDetailsRequest.getCampaignId(),
                obdSuccessfulCallDetailsRequest.getServiceOption().name(),
                retryCount.toString(),
                CampaignMessageStatus.SUCCESS.name(),
                callDurationReportRequest,
                obdSuccessfulCallDetailsRequest.getCallSource().name());
    }
}
