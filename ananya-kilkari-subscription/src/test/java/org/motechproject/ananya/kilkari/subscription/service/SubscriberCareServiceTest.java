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

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        verify(allSubscriberCareDocs).add(docArgumentCaptor.capture());
        SubscriberCareDoc subscriberCareDoc = docArgumentCaptor.getValue();

        assertEquals(subscriberCareRequest.getChannel(), subscriberCareDoc.getChannel().toString());
        assertEquals(subscriberCareRequest.getCreatedAt(), subscriberCareDoc.getCreatedAt());
        assertEquals(subscriberCareRequest.getMsisdn(), subscriberCareDoc.getMsisdn());
        assertEquals(subscriberCareRequest.getReason(), subscriberCareDoc.getReason().toString());
    }

    @Test
    public void shouldGetAllSubscriberCareDocsSortedByDate() {
        DateTime now = DateTime.now();
        SubscriberCareDoc subscriberCareDoc1 = new SubscriberCareDoc("1234567890", SubscriberCareReasons.HELP, now, Channel.IVR);
        SubscriberCareDoc subscriberCareDoc2 = new SubscriberCareDoc("1234567891", SubscriberCareReasons.HELP, now.minusDays(1), Channel.IVR);
        SubscriberCareDoc subscriberCareDoc3 = new SubscriberCareDoc("1234567892", SubscriberCareReasons.HELP, now.plusDays(1), Channel.IVR);
        List<SubscriberCareDoc> subscriberCareDocList = new ArrayList<>();
        subscriberCareDocList.add(subscriberCareDoc1);
        subscriberCareDocList.add(subscriberCareDoc2);
        subscriberCareDocList.add(subscriberCareDoc3);
        DateTime startDate = now.minusDays(2);
        DateTime endDate = now.plusDays(2);
        when(allSubscriberCareDocs.findByCreatedAt(startDate, endDate)).thenReturn(subscriberCareDocList);

        List<SubscriberCareDoc> allSortedByDate = subscriberCareService.getAllSortedByDate(startDate, endDate);

        assertEquals(3, allSortedByDate.size());
        assertEquals(subscriberCareDoc2, allSortedByDate.get(0));
        assertEquals(subscriberCareDoc1, allSortedByDate.get(1));
        assertEquals(subscriberCareDoc3, allSortedByDate.get(2));
    }
}
