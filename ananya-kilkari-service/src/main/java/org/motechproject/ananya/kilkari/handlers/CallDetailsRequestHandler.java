package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.CallDetailsEventKeys;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDetailsRequestHandler {

    Logger logger = Logger.getLogger(CallDetailsRequestHandler.class);
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public CallDetailsRequestHandler(KilkariCampaignService kilkariCampaignService) {
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT})
    public void handleOBDCallbackRequest(MotechEvent motechEvent) {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = (OBDSuccessfulCallDetailsWebRequest) motechEvent.getParameters().get("0");
        logger.info("Handling OBD callback for : " + obdSuccessfulCallDetailsRequest.getSubscriptionId());
        kilkariCampaignService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
        logger.info("Completed handling OBD callback for : " + obdSuccessfulCallDetailsRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT})
    public void handleInboxCallDetailsRequest(MotechEvent motechEvent) {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = (InboxCallDetailsWebRequest) motechEvent.getParameters().get("0");
        logger.info(String.format("Handling inbox call details for msisdn:%s, subscription:%s", inboxCallDetailsWebRequest.getMsisdn(), inboxCallDetailsWebRequest.getSubscriptionId()));
        kilkariCampaignService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }
}
