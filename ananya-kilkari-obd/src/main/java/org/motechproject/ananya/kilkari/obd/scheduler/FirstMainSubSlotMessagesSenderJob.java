package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class FirstMainSubSlotMessagesSenderJob extends MessagesSenderJob {

    private static final String SLOT_EVENT_SUBJECT = "obd.send.main.sub.slot.one.messages";
    private static final String RETRY_EVENT_SUBJECT = "obd.send.main.sub.slot.one.messages.with.retry";

    private static final String RETRY_GROUP_NAME = "obd-send-main-sub-slot-one-messages-group";
    private static final String RETRY_NAME = "obd-send-main-sub-slot-one-messages";

    public FirstMainSubSlotMessagesSenderJob() {
        super(SLOT_EVENT_SUBJECT,
                new ArrayList<OBDSubSlot>() {{
                    add(MainSubSlot.ONE);
                }},
                RETRY_NAME,
                RETRY_GROUP_NAME
        );
    }

    @MotechListener(subjects = {FirstMainSubSlotMessagesSenderJob.SLOT_EVENT_SUBJECT})
    public void handleMessages(MotechEvent motechEvent) {
        scheduleMessagesWithRetry(motechEvent);
    }

    @MotechListener(subjects = {FirstMainSubSlotMessagesSenderJob.RETRY_EVENT_SUBJECT})
    public void handleMessagesWithRetry(MotechEvent motechEvent) {
        sendMessagesWithRetry(motechEvent);
    }

    @Override
    protected void sendMessages(OBDSubSlot subSlot) {
        campaignMessageService.sendFirstMainSubSlotMessages(subSlot);
    }
}