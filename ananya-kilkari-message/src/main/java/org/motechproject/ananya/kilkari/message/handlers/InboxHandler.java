package org.motechproject.ananya.kilkari.message.handlers;

import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InboxHandler {
    private final static Logger logger = LoggerFactory.getLogger(InboxHandler.class);
    
    private InboxService inboxService;

    @Autowired
    public InboxHandler(InboxService inboxService) {
        this.inboxService = inboxService;
    }

    @MotechListener(subjects = {InboxEventKeys.DELETE_INBOX})
    public void handleInboxDeletion(MotechEvent event) {
        String subscriptionId = (String) event.getParameters().get("0");
        logger.info(String.format("Handling inbox deletion event for subscriptionid: %s", subscriptionId));
        inboxService.deleteInbox(subscriptionId);
    }
}
