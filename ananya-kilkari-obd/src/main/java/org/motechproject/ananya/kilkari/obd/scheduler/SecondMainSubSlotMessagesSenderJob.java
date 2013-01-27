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
public class SecondMainSubSlotMessagesSenderJob extends MainSlotMessagesSenderJob {

    private static final String SLOT_EVENT_SUBJECT = "obd.send.main.sub.slot.two.messages";
    private static final String RETRY_EVENT_SUBJECT = "obd.send.main.sub.slot.two.messages.with.retry";

    private static final String RETRY_GROUP_NAME = "obd-send-main-sub-slot-two-messages-group";
    private static final String RETRY_NAME = "obd-send-main-sub-slot-two-messages";

    private static final Logger logger = LoggerFactory.getLogger(SecondMainSubSlotMessagesSenderJob.class);
    private CampaignMessageService campaignMessageService;

    @Autowired
    public SecondMainSubSlotMessagesSenderJob(CampaignMessageService campaignMessageService, RetryService retryService, final OBDProperties obdProperties) {
        super(SLOT_EVENT_SUBJECT,
                new HashMap<SubSlot, String>() {{
                    put(SubSlot.TWO, obdProperties.getMainSlotCronJobExpressionFor(SubSlot.TWO.name()));
                }},
                obdProperties,
                retryService
        );
        this.campaignMessageService = campaignMessageService;
    }

    @MotechListener(subjects = {SecondMainSubSlotMessagesSenderJob.SLOT_EVENT_SUBJECT})
    public void handleMessages(MotechEvent motechEvent) {
        logger.info("Handling send main sub slot two messages event");
        RetryRequest retryRequest = new RetryRequest(SecondMainSubSlotMessagesSenderJob.RETRY_NAME, UUID.randomUUID().toString(), DateTime.now());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(SUB_SLOT_KEY, motechEvent.getParameters().get(SUB_SLOT_KEY));
        retryService.schedule(retryRequest, parameters);
    }

    @MotechListener(subjects = {SecondMainSubSlotMessagesSenderJob.RETRY_EVENT_SUBJECT})
    public void sendMessagesWithRetry(MotechEvent motechEvent) {
        logger.info("Handling send main sub slot two messages with retry event");
        try {
            sendMainSlotMessages(motechEvent, RETRY_GROUP_NAME);
        } catch (Exception ex) {
            logger.error("Error occurred while sending main sub slot two messages to obd", ex);
        }
    }

    @Override
    protected void sendMessages(SubSlot subSlot) {
        campaignMessageService.sendSecondMainSubSlotMessages(subSlot);
    }
}