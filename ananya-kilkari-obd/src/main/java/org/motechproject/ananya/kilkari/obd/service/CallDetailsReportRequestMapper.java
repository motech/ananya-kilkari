package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.service.request.CallDurationRequest;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailRecordRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;

public class CallDetailsReportRequestMapper {
    public static CallDetailsReportRequest mapFrom(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        CallDurationRequest callDurationRequest = obdSuccessfulCallDetailsRequest.getCallDurationRequest();
        CallDetailRecordRequest callDurationReportRequest = new CallDetailRecordRequest(callDurationRequest.getStartTime(), callDurationRequest.getEndTime());
        return new CallDetailsReportRequest(
                obdSuccessfulCallDetailsRequest.getSubscriptionId(),
                obdSuccessfulCallDetailsRequest.getMsisdn(),
                obdSuccessfulCallDetailsRequest.getCampaignId(),
                getServiceOption(obdSuccessfulCallDetailsRequest),
                CampaignMessageStatus.SUCCESS.name(),
                callDurationReportRequest,
                obdSuccessfulCallDetailsRequest.getCallSource().name());
    }

    private static String getServiceOption(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        return obdSuccessfulCallDetailsRequest.getServiceOption() == null ? null : obdSuccessfulCallDetailsRequest.getServiceOption().name();
    }

}
