package org.motechproject.ananya.kilkari.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriberCareDocs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriberCareServiceTest {
    private SubscriberCareService subscriberCareService;

    @Mock
    private AllSubscriberCareDocs allSubscriberCareDocs;

    @Before
    public void setUp() {
        initMocks(this);
        subscriberCareService = new SubscriberCareService(allSubscriberCareDocs);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidSubscriberCareRequest() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("12345", SubscriberCareReasons.CHANGE_PACK.name());

        subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }

    @Test
    public void shouldSaveValidSubscriberCareRequest() {
        String msisdn = "1234567890";
        String reason = SubscriberCareReasons.CHANGE_PACK.name();
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason);

        subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);

        ArgumentCaptor<SubscriberCareDoc> subscriberCareDocArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareDoc.class);
        verify(allSubscriberCareDocs).add(subscriberCareDocArgumentCaptor.capture());
        SubscriberCareDoc subscriberCareDoc = subscriberCareDocArgumentCaptor.getValue();

        assertEquals(msisdn, subscriberCareDoc.getMsisdn());
        assertEquals(reason, subscriberCareDoc.getReason());
    }
}
