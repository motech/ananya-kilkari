package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberCareServiceTest {

    @Mock
    private AllSubscriberCareDocs allSubscriberCareDocs;

    private SubscriberCareService subscriberCareService;

    @Before
    public void setUp() {
        subscriberCareService = new SubscriberCareService(allSubscriberCareDocs);
    }

    @Test
    public void shouldCreateSubscriptionCareDoc() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(
                "1234567890", SubscriberCareReasons.HELP.toString(), Channel.CONTACT_CENTER.toString(), DateTime.now());

        subscriberCareService.create(subscriberCareRequest);

        ArgumentCaptor<SubscriberCareDoc> docArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareDoc.class);
        verify(allSubscriberCareDocs).addOrUpdate(docArgumentCaptor.capture());
        SubscriberCareDoc subscriberCareDoc = docArgumentCaptor.getValue();

        assertEquals(subscriberCareRequest.getChannel(), subscriberCareDoc.getChannel().toString());
        assertEquals(subscriberCareRequest.getCreatedAt(), subscriberCareDoc.getCreatedAt());
        assertEquals(subscriberCareRequest.getMsisdn(), subscriberCareDoc.getMsisdn());
        assertEquals(subscriberCareRequest.getReason(), subscriberCareDoc.getReason().toString());
    }
}
