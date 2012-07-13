package org.motechproject.ananya.kilkari.domain;

import org.motechproject.ananya.kilkari.obd.contract.OBDRequest;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.CallDetailRecord;
import org.motechproject.ananya.kilkari.reporting.domain.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;

public class CampaignMessageDeliveryReportRequestMapper {
    public CampaignMessageDeliveryReportRequest mapFrom(OBDRequestWrapper obdRequestWrapper, Integer retryCount) {
        OBDRequest obdRequest = obdRequestWrapper.getObdRequest();
        CallDetailRecord callDetailRecord = obdRequest.getCallDetailRecord();
        CallDetailsReportRequest callDetailsReportRequest = new CallDetailsReportRequest(callDetailRecord.getStartTime(), callDetailRecord.getEndTime());
        return new CampaignMessageDeliveryReportRequest(obdRequestWrapper.getSubscriptionId(), obdRequest.getMsisdn(), obdRequest.getCampaignId(), obdRequest.getServiceOption(), retryCount.toString(), callDetailsReportRequest);
    }
}
