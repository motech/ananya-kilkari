package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.contract.request.CallDetailRecordRequest;
import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

public class CallDetailsReportRequestMapper {
    public static CallDetailsReportRequest mapFrom(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest, Integer retryCount) {
        CallDurationRequest callDurationRequest = obdSuccessfulCallDetailsRequest.getCallDurationRequest();
        CallDetailRecordRequest callDurationReportRequest = new CallDetailRecordRequest(callDurationRequest.getStartTime(), callDurationRequest.getEndTime());
        return new CallDetailsReportRequest(
                obdSuccessfulCallDetailsRequest.getSubscriptionId(),
                obdSuccessfulCallDetailsRequest.getMsisdn(),
                obdSuccessfulCallDetailsRequest.getCampaignId(),
                obdSuccessfulCallDetailsRequest.getServiceOption().name(),
                retryCount.toString(),
                CampaignMessageStatus.SUCCESS.name(),
                callDurationReportRequest,
                obdSuccessfulCallDetailsRequest.getCallSource().name());
    }

    public static CallDetailsReportRequest mapFrom(InboxCallDetailsWebRequest inboxCallDetailsWebRequest, Subscription subscription) {
        CallDurationWebRequest callDurationWebRequest = inboxCallDetailsWebRequest.getCallDurationWebRequest();
        return new CallDetailsReportRequest(
                subscription.getSubscriptionId(),
                inboxCallDetailsWebRequest.getMsisdn(),
                inboxCallDetailsWebRequest.getCampaignId(),
                null,
                null,
                CampaignMessageStatus.SUCCESS.name(),
                new CallDetailRecordRequest(DateUtils.parseDateTime(callDurationWebRequest.getStartTime()), DateUtils.parseDateTime(callDurationWebRequest.getEndTime())),
                CampaignMessageCallSource.INBOX.name());
    }
}
