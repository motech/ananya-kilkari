package org.motechproject.ananya.kilkari.obd.scheduler;

import org.motechproject.ananya.kilkari.obd.service.OBDProperties;
import org.motechproject.event.MotechEvent;
import org.motechproject.retry.EventKeys;
import org.motechproject.retry.service.RetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public abstract class MainSlotMessagesSenderJob extends MessagesSenderJob {
    protected RetryService retryService;
    protected OBDProperties obdProperties;

    @Autowired
    public MainSlotMessagesSenderJob(String subject, final HashMap<SubSlot, String> cronJobDetails, OBDProperties obdProperties, RetryService retryService) {
        super(subject, cronJobDetails);
        this.obdProperties = obdProperties;
        this.retryService = retryService;
    }

    protected abstract void sendMessages(SubSlot subSlot);

    protected void sendMainSlotMessages(MotechEvent motechEvent, String retryGroupName) throws Exception {
        Map<String, Object> parameters = motechEvent.getParameters();
        SubSlot subSlot = (SubSlot) parameters.get(SUB_SLOT_KEY);
        if (!canSendMessages(obdProperties.getMainSlotStartTimeLimitFor(subSlot), obdProperties.getMainSlotEndTimeLimitFor(subSlot))) {
            retryService.fulfill((String) parameters.get(EventKeys.EXTERNAL_ID), retryGroupName);
            return;
        }
        sendMessages(subSlot);
        retryService.fulfill((String) parameters.get(EventKeys.EXTERNAL_ID), retryGroupName);
    }
}