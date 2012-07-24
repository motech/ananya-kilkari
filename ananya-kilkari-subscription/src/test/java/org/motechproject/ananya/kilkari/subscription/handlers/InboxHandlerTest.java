package org.motechproject.ananya.kilkari.subscription.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class InboxHandlerTest {

    private InboxHandler inboxHandler;
    @Mock
    private KilkariInboxService kilkariInboxService;

    @Before
    public void setup(){
        initMocks(this);
        inboxHandler = new InboxHandler(kilkariInboxService);
    }

    @Test
    public void shouldHandleInboxDeletion() {
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){
            {
                put("0", subscriptionId);
            }
        };

        inboxHandler.handleInboxDeletion(new MotechEvent(SubscriptionEventKeys.DELETE_INBOX, parameters));

        verify(kilkariInboxService).deleteInbox(subscriptionId);
    }
}
