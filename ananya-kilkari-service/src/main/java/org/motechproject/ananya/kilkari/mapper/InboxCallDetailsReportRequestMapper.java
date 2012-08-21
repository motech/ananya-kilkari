package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailRecordRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

public class InboxCallDetailsReportRequestMapper {

    public static CallDetailsReportRequest mapFrom(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        CallDurationWebRequest callDurationWebRequest = inboxCallDetailsWebRequest.getCallDurationWebRequest();
        return new CallDetailsReportRequest(
                inboxCallDetailsWebRequest.getSubscriptionId(),
                inboxCallDetailsWebRequest.getMsisdn(),
                inboxCallDetailsWebRequest.getCampaignId(),
                null,
                null,
                CampaignMessageStatus.SUCCESS.name(),
                new CallDetailRecordRequest(DateUtils.parseDateTime(callDurationWebRequest.getStartTime()), DateUtils.parseDateTime(callDurationWebRequest.getEndTime())),
                CampaignMessageCallSource.INBOX.name());
    }

}
