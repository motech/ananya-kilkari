package org.motechproject.ananya.kilkari.domain;

import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.reporting.domain.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;

public class CampaignMessageDeliveryReportRequestMapper {
    public CampaignMessageDeliveryReportRequest mapFrom(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest, Integer retryCount) {
        CallDetailRecord callDetailRecord = obdSuccessfulCallDetailsRequest.getCallDetailRecord();
        CallDetailsReportRequest callDetailsReportRequest = new CallDetailsReportRequest(callDetailRecord.getStartTime(), callDetailRecord.getEndTime());
        return new CampaignMessageDeliveryReportRequest(
                obdSuccessfulCallDetailsRequest.getSubscriptionId(),
                obdSuccessfulCallDetailsRequest.getMsisdn(),
                obdSuccessfulCallDetailsRequest.getCampaignId(),
                obdSuccessfulCallDetailsRequest.getServiceOption(),
                retryCount.toString(),
                CampaignMessageStatus.SUCCESS.name(),
                callDetailsReportRequest,
                CampaignMessageCallSource.OBD.name());
    }
}
