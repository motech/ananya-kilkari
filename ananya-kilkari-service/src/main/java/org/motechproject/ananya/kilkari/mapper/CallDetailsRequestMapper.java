package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.contract.request.CallDetailRecordRequest;
import org.motechproject.ananya.kilkari.contract.request.CallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;

public class CallDetailsRequestMapper {
    public static CallDetailsRequest mapFrom(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest, Integer retryCount) {
        CallDurationRequest callDurationRequest = obdSuccessfulCallDetailsRequest.getCallDurationRequest();
        CallDetailRecordRequest callDurationReportRequest = new CallDetailRecordRequest(callDurationRequest.getStartTime(), callDurationRequest.getEndTime());
        return new CallDetailsRequest(
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
