package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OBDRequestCallbackPublisher {
    private EventContext eventContext;

    @Autowired
    public OBDRequestCallbackPublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishObdCallbackRequest(OBDRequestWrapper obdRequestWrapper) {
        eventContext.send(OBDEventKeys.PROCESS_CALLBACK_REQUEST, obdRequestWrapper);
    }
}
