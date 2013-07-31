package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.ananya.kilkari.obd.domain.OBDSubSlot;
import org.motechproject.ananya.kilkari.obd.domain.RetrySubSlot;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class RetrySlotMessagesSenderJob extends MessagesSenderJob {

    private static final String SLOT_EVENT_SUBJECT = "obd.send.retry.slot.messages";
    private static final String RETRY_EVENT_SUBJECT = "obd.send.retry.slot.messages.with.retry";

    private static final String RETRY_GROUP_NAME = "obd-send-retry-slot-messages-group";
    private static final String RETRY_NAME = "obd-send-retry-slot-messages";

    public RetrySlotMessagesSenderJob() {
        super(SLOT_EVENT_SUBJECT,
                new ArrayList<OBDSubSlot>() {{
                    add(RetrySubSlot.ONE);
                    add(RetrySubSlot.TWO);
                    add(RetrySubSlot.THREE);
                }},
                RETRY_NAME,
                RETRY_GROUP_NAME);
    }

    @MotechListener(subjects = {RetrySlotMessagesSenderJob.SLOT_EVENT_SUBJECT})
    public void handleMessages(MotechEvent motechEvent) {
        scheduleMessagesWithRetry(motechEvent);
    }

    @MotechListener(subjects = {RetrySlotMessagesSenderJob.RETRY_EVENT_SUBJECT})
    public void handleMessagesWithRetry(MotechEvent motechEvent) {
        sendMessagesWithRetry(motechEvent);
    }

    @Override
    protected void sendMessages(OBDSubSlot subSlot) {
        campaignMessageService.sendRetrySlotMessages(subSlot);
    }
}