package org.motechproject.ananya.kilkari.message.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class InboxHandlerTest {

    private InboxHandler inboxHandler;
    @Mock
    private InboxService inboxService;

    @Before
    public void setup(){
        initMocks(this);
        inboxHandler = new InboxHandler(inboxService);
    }

    @Test
    public void shouldHandleInboxDeletion() {
        final String subscriptionId = "abcd1234";
        HashMap<String, Object> parameters = new HashMap<String, Object>(){
            {
                put("0", subscriptionId);
            }
        };

        inboxHandler.handleInboxDeletion(new MotechEvent(InboxEventKeys.DELETE_INBOX, parameters));

        verify(inboxService).deleteInbox(subscriptionId);
    }
}
