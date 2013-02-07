package org.motechproject.ananya.kilkari.obd.scheduler;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.OBDProperties;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.motechproject.retry.EventKeys;
import org.motechproject.retry.domain.RetryRequest;
import org.motechproject.retry.service.RetryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class RetrySlotMessagesSenderJob extends MessagesSenderJob {

    private static final String SLOT_EVENT_SUBJECT = "obd.send.retry.slot.messages";
    private static final String RETRY_EVENT_SUBJECT = "obd.send.retry.slot.messages.with.retry";

    private static final String RETRY_GROUP_NAME = "obd-send-retry-slot-messages-group";
    private static final String RETRY_NAME = "obd-send-retry-slot-messages";

    private static final Logger logger = LoggerFactory.getLogger(RetrySlotMessagesSenderJob.class);
    private CampaignMessageService campaignMessageService;
    private RetryService retryService;
    private OBDProperties obdProperties;

    @Autowired
    public RetrySlotMessagesSenderJob(CampaignMessageService campaignMessageService, RetryService retryService, final OBDProperties obdProperties) {
        super(SLOT_EVENT_SUBJECT,
                new HashMap<SubSlot, String>() {{
                    put(SubSlot.ONE, obdProperties.getRetrySlotCronJobExpressionFor(SubSlot.ONE));
                    put(SubSlot.TWO, obdProperties.getRetrySlotCronJobExpressionFor(SubSlot.TWO));
                    put(SubSlot.THREE, obdProperties.getRetrySlotCronJobExpressionFor(SubSlot.THREE));
                }}
        );
        this.campaignMessageService = campaignMessageService;
        this.retryService = retryService;
        this.obdProperties = obdProperties;
    }

    @MotechListener(subjects = {RetrySlotMessagesSenderJob.SLOT_EVENT_SUBJECT})
    public void handleMessages(MotechEvent motechEvent) {
        SubSlot subSlot = (SubSlot) motechEvent.getParameters().get(SUB_SLOT_KEY);
        logger.info(String.format("Handling send retry sub slot %s messages event", subSlot.name()));
        RetryRequest retryRequest = new RetryRequest(RetrySlotMessagesSenderJob.RETRY_NAME, UUID.randomUUID().toString(), DateTime.now());
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(SUB_SLOT_KEY, subSlot);
        retryService.schedule(retryRequest, parameters);
    }

    @MotechListener(subjects = {RetrySlotMessagesSenderJob.RETRY_EVENT_SUBJECT})
    public void sendMessagesWithRetry(MotechEvent motechEvent) {
        Map<String, Object> parameters = motechEvent.getParameters();
        SubSlot subSlot = (SubSlot) parameters.get(SUB_SLOT_KEY);
        logger.info(String.format("Handling send retry sub slot %s messages with retry event", subSlot.name()));

        if (!canSendMessages(obdProperties.getRetrySlotStartTimeLimitFor(subSlot), obdProperties.getRetrySlotEndTimeLimitFor(subSlot))) {
            retryService.fulfill((String) parameters.get(EventKeys.EXTERNAL_ID), RetrySlotMessagesSenderJob.RETRY_GROUP_NAME);
            return;
        }

        try {
            campaignMessageService.sendRetrySlotMessages(subSlot);
            retryService.fulfill((String) parameters.get(EventKeys.EXTERNAL_ID), RetrySlotMessagesSenderJob.RETRY_GROUP_NAME);
        } catch (Exception ex) {
            logger.error(String.format("Error occurred while sending retry sub slot %s messages to obd.", subSlot.name()), ex);
        }
    }
}