package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.OBDProperties;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.motechproject.retry.domain.RetryRequest;
import org.motechproject.retry.service.RetryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class FirstMainSubSlotMessagesSenderJob extends MainSlotMessagesSenderJob {

    private static final String SLOT_EVENT_SUBJECT = "obd.send.main.sub.slot.one.messages";
    private static final String RETRY_EVENT_SUBJECT = "obd.send.main.sub.slot.one.messages.with.retry";

    private static final String RETRY_GROUP_NAME = "obd-send-main-sub-slot-one-messages-group";
    private static final String RETRY_NAME = "obd-send-main-sub-slot-one-messages";

    private static final Logger logger = LoggerFactory.getLogger(FirstMainSubSlotMessagesSenderJob.class);
    private CampaignMessageService campaignMessageService;

    @Autowired
    public FirstMainSubSlotMessagesSenderJob(CampaignMessageService campaignMessageService, RetryService retryService, final OBDProperties obdProperties) {
        super(SLOT_EVENT_SUBJECT,
                new HashMap<SubSlot, String>() {{
                    put(SubSlot.ONE, obdProperties.getMainSlotCronJobExpressionFor(SubSlot.ONE.name()));
                }},
                obdProperties,
                retryService
        );
        this.campaignMessageService = campaignMessageService;
    }

    @MotechListener(subjects = {FirstMainSubSlotMessagesSenderJob.SLOT_EVENT_SUBJECT})
    public void handleMessages(MotechEvent motechEvent) {
        logger.info("Handling send main sub slot one messages event");
        RetryRequest retryRequest = new RetryRequest(FirstMainSubSlotMessagesSenderJob.RETRY_NAME, UUID.randomUUID().toString(), DateTime.now());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(SUB_SLOT_KEY, motechEvent.getParameters().get(SUB_SLOT_KEY));
        retryService.schedule(retryRequest, parameters);
    }

    @MotechListener(subjects = {FirstMainSubSlotMessagesSenderJob.RETRY_EVENT_SUBJECT})
    public void sendMessagesWithRetry(MotechEvent motechEvent) {
        logger.info("Handling send main sub slot one messages with retry event");
        try {
            sendMainSlotMessages(motechEvent, RETRY_GROUP_NAME);
        } catch (Exception ex) {
            logger.error("Error occurred while sending main sub slot one messages to obd", ex);
        }
    }

    @Override
    protected void sendMessages(SubSlot subSlot) {
        campaignMessageService.sendFirstMainSubSlotMessages(subSlot);
    }
}