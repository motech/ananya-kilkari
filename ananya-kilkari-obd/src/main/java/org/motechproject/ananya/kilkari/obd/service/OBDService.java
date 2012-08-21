package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OBDService {

    private OBDEventQueuePublisher obdEventQueuePublisher;
    private CampaignMessageService campaignMessageService;
    private ReportingService reportingService;

    private final Logger LOGGER = LoggerFactory.getLogger(OBDService.class);


    @Autowired
    public OBDService(OBDEventQueuePublisher obdEventQueuePublisher, CampaignMessageService campaignMessageService, ReportingService reportingService) {
        this.obdEventQueuePublisher = obdEventQueuePublisher;
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
    }

    public void processCallDeliveryFailure(FailedCallReports failedCallReports) {
        obdEventQueuePublisher.publishCallDeliveryFailureRecord(failedCallReports);
    }

    public void processInvalidOBDRequestEntries(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        obdEventQueuePublisher.publishInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    public boolean processSuccessfulCallDelivery(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        CampaignMessage campaignMessage = campaignMessageService.find(obdSuccessfulCallDetailsRequest.getSubscriptionId(), obdSuccessfulCallDetailsRequest.getCampaignId());
        if (campaignMessage == null) {
            LOGGER.error(String.format("Campaign Message not present for subscriptionId: %s, campaignId: %s",
                    obdSuccessfulCallDetailsRequest.getSubscriptionId(), obdSuccessfulCallDetailsRequest.getCampaignId()));
            return false;
        }

        int retryCount = campaignMessage.getDnpRetryCount();
        reportingService.reportCampaignMessageDeliveryStatus(CallDetailsReportRequestMapper.mapFrom(obdSuccessfulCallDetailsRequest, retryCount));
        campaignMessageService.deleteCampaignMessage(campaignMessage);
        return true;
    }
}
