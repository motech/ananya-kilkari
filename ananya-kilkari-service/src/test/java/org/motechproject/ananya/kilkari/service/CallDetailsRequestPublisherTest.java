package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.request.CallDurationWebRequest;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.scheduler.context.EventContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CallDetailsRequestPublisherTest {

    @Mock
    private EventContext eventContext;

    private CallDetailsRequestPublisher callDetailsRequestPublisher;

    @Before
    public void setUp() {
        callDetailsRequestPublisher = new CallDetailsRequestPublisher(eventContext);
    }

    @Test
    public void shouldPublishCallBackRequests() {
        OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest = new OBDSuccessfulCallDetailsWebRequest();

        callDetailsRequestPublisher.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);

        verify(eventContext).send(CallDetailsEventKeys.PROCESS_OBD_SUCCESSFUL_CALL_REQUEST_SUBJECT, obdSuccessfulCallDetailsRequest);
    }

    @Test
    public void shouldPublishInboxCallDetailsRequest() {
        InboxCallDetailsWebRequest inboxCallDetailsWebRequest = new InboxCallDetailsWebRequest("1234567890", "WEEK12", new CallDurationWebRequest(), "choti_kilkari", "subscriptionId");

        callDetailsRequestPublisher.publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);

        verify(eventContext).send(CallDetailsEventKeys.PROCESS_INBOX_CALL_REQUEST_SUBJECT, inboxCallDetailsWebRequest);
    }
}
