package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.CallDetailsEventKeys;
import org.motechproject.ananya.kilkari.service.KilkariCallDetailsService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallDetailsRequestHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(CallDetailsRequestHandler.class);

    private KilkariCallDetailsService kilkariCallDetailsService;

    @Autowired
    public CallDetailsRequestHandler(KilkariCallDetailsService kilkariCallDetailsService) {
        this.kilkariCallDetailsService = kilkariCallDetailsService;
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT})
    public void handleOBDCallbackRequest(MotechEvent motechEvent) {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = (OBDSuccessfulCallDetailsWebRequest) motechEvent.getParameters().get("0");
        LOGGER.info("Handling OBD callback for : " + obdSuccessfulCallDetailsRequest.getSubscriptionId());
        kilkariCallDetailsService.processSuccessfulMessageDelivery(obdSuccessfulCallDetailsRequest);
        LOGGER.info("Completed handling OBD callback for : " + obdSuccessfulCallDetailsRequest.getSubscriptionId());
    }

    @MotechListener(subjects = {CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT})
    public void handleInboxCallDetailsRequest(MotechEvent motechEvent) {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = (InboxCallDetailsWebRequest) motechEvent.getParameters().get("0");
        LOGGER.info(String.format("Handling inbox call details for msisdn:%s, subscription:%s", inboxCallDetailsWebRequest.getMsisdn(), inboxCallDetailsWebRequest.getSubscriptionId()));
        kilkariCallDetailsService.processInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }
}
