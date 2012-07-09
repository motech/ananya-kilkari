package org.motechproject.ananya.kilkari.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.domain.SubscriberCareReasons;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.validators.SubscriberCareRequestValidator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriberCareServiceTest {
    private SubscriberCareService subscriberCareService;

    @Mock
    private AllSubscriberCareDocs allSubscriberCareDocs;
    @Mock
    private SubscriptionPublisher subscriptionPublisher;
    private SubscriberCareRequestValidator careRequestValidator;

    @Before
    public void setUp() {
        initMocks(this);
        careRequestValidator = new SubscriberCareRequestValidator();
        subscriberCareService = new SubscriberCareService(allSubscriberCareDocs, careRequestValidator, subscriptionPublisher);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionForInvalidSubscriberCareRequest() {
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest("12345", SubscriberCareReasons.HELP.name(), "ivr");

        subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
    }

    @Test
    public void shouldSaveValidSubscriberCareRequest() {
        String msisdn = "1234567890";
        String reason = SubscriberCareReasons.HELP.name();
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason, "ivr");

        subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);

        ArgumentCaptor<SubscriberCareDoc> subscriberCareDocArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareDoc.class);
        verify(allSubscriberCareDocs).add(subscriberCareDocArgumentCaptor.capture());
        SubscriberCareDoc subscriberCareDoc = subscriberCareDocArgumentCaptor.getValue();

        assertEquals(msisdn, subscriberCareDoc.getMsisdn());
        assertEquals(reason, subscriberCareDoc.getReason());
    }

    @Test
    public void shouldPublishSubscriberCareRequestEvent() {
        String msisdn = "1234566789";
        String reason = SubscriberCareReasons.HELP.name();
        String channel = "ivr";
        SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(msisdn, reason, channel);

        subscriberCareService.processSubscriberCareRequest(subscriberCareRequest);

        ArgumentCaptor<SubscriberCareRequest> subscriberCareRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriberCareRequest.class);
        verify(subscriptionPublisher).processSubscriberCareRequest(subscriberCareRequestArgumentCaptor.capture());
        SubscriberCareRequest careRequest = subscriberCareRequestArgumentCaptor.getValue();

        Assert.assertEquals(msisdn, careRequest.getMsisdn());
        Assert.assertEquals(reason, careRequest.getReason());
    }

}
