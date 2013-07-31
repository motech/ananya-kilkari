package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.ananya.kilkari.obd.domain.MainSubSlot;
import org.motechproject.ananya.kilkari.obd.domain.OBDSubSlot;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ThirdMainSubSlotMessagesSenderJob extends MessagesSenderJob {

    private static final String SLOT_EVENT_SUBJECT = "obd.send.main.sub.slot.three.messages";
    private static final String RETRY_EVENT_SUBJECT = "obd.send.main.sub.slot.three.messages.with.retry";

    private static final String RETRY_GROUP_NAME = "obd-send-main-sub-slot-three-messages-group";
    private static final String RETRY_NAME = "obd-send-main-sub-slot-three-messages";

    public ThirdMainSubSlotMessagesSenderJob() {
        super(SLOT_EVENT_SUBJECT,
                new ArrayList<OBDSubSlot>() {{
                    add(MainSubSlot.THREE);
                }},
                RETRY_NAME,
                RETRY_GROUP_NAME
        );
    }

    @MotechListener(subjects = {ThirdMainSubSlotMessagesSenderJob.SLOT_EVENT_SUBJECT})
    public void handleMessages(MotechEvent motechEvent) {
        scheduleMessagesWithRetry(motechEvent);
    }

    @MotechListener(subjects = {ThirdMainSubSlotMessagesSenderJob.RETRY_EVENT_SUBJECT})
    public void handleMessagesWithRetry(MotechEvent motechEvent) {
        sendMessagesWithRetry(motechEvent);
    }

    @Override
    protected void sendMessages(OBDSubSlot subSlot) {
        campaignMessageService.sendThirdMainSubSlotMessages(subSlot);
    }
}