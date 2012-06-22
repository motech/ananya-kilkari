package org.motechproject.ananya.kilkari.handlers;

import org.motechproject.ananya.kilkari.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessSubscriptionHandler {

    @MotechListener(subjects = {SubscriptionEventKeys.PROCESS_SUBSCRIPTION})
    public void handleProcessSubscription(MotechEvent event) throws ValidationException {
    }
}