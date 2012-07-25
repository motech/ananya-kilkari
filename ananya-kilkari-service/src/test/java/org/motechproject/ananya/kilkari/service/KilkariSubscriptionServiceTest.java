package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.messagecampaign.contract.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class KilkariSubscriptionServiceTest {

    private KilkariSubscriptionService kilkariSubscriptionService;
    @Mock
    private SubscriptionPublisher subscriptionPublisher;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private KilkariMessageCampaignService kilkariMessageCampaignService;
    @Mock
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private KilkariPropertiesData kilkariPropertiesData;

    @Before
    public void setup() {
        initMocks(this);
        kilkariSubscriptionService = new KilkariSubscriptionService(subscriptionPublisher, subscriptionService, kilkariMessageCampaignService, motechSchedulerService,kilkariPropertiesData);
    }

    @Test
    public void shouldCreateSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        kilkariSubscriptionService.createSubscription(subscriptionRequest);
        verify(subscriptionPublisher).createSubscription(subscriptionRequest);
    }

    @Test
    public void shouldGetSubscriptionsFor() {
        String msisdn = "1234567890";
        kilkariSubscriptionService.findByMsisdn(msisdn);
        verify(subscriptionService).findByMsisdn(msisdn);
    }

    @Test
    public void shouldProcessSubscriptionRequest() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        SubscriptionPack pack = SubscriptionPack.FIFTEEN_MONTHS;
        subscriptionRequest.setCreatedAt(DateTime.now());
        subscriptionRequest.setPack(pack.name());
        Subscription subscription = new Subscription("1234567890", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());

        when(subscriptionService.createSubscription(subscriptionRequest)).thenReturn(subscription);

        kilkariSubscriptionService.processSubscriptionRequest(subscriptionRequest);

        ArgumentCaptor<MessageCampaignRequest> captor = ArgumentCaptor.forClass(MessageCampaignRequest.class);
        verify(kilkariMessageCampaignService).start(captor.capture());
        MessageCampaignRequest messageCampaignRequest = captor.getValue();
        assertNotNull(messageCampaignRequest.getExternalId());
        assertEquals(pack.name(), messageCampaignRequest.getSubscriptionPack());

        assertEquals(subscriptionRequest.getCreatedAt().toLocalDate(), messageCampaignRequest.getSubscriptionCreationDate().toLocalDate());
    }

    @Test
    public void shouldNotScheduleMessageCampaignIfDuplicateSubscriptionIsRequested() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults()
                .withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS.toString()).build();

        doThrow(new DuplicateSubscriptionException("")).when(subscriptionService).createSubscription(subscriptionRequest);

        kilkariSubscriptionService.processSubscriptionRequest(subscriptionRequest);

        verify(kilkariMessageCampaignService, never()).start(any(MessageCampaignRequest.class));
    }

    @Test
    public void shouldReturnSubscriptionGivenASubscriptionId() {
        Subscription exptectedSubscription = new Subscription();
        String susbscriptionid = "susbscriptionid";
        when(subscriptionService.findBySubscriptionId(susbscriptionid)).thenReturn(exptectedSubscription);

        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(susbscriptionid);

        assertEquals(exptectedSubscription, subscription);
    }

    @Test
    public void shouldScheduleASubscriptionCompletionEvent() {
        String subscriptionId = "subscriptionId";
        DateTime now = DateTime.now();
        Subscription mockedSubscription = mock(Subscription.class);
        when(mockedSubscription.getSubscriptionId()).thenReturn(subscriptionId);
         when(kilkariPropertiesData.getBufferDaysToAllowRenewalForPackCompletion()).thenReturn(3);

        kilkariSubscriptionService.processSubscriptionCompletion(mockedSubscription);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRunOnceJob(runOnceSchedulableJobArgumentCaptor.capture());
        RunOnceSchedulableJob runOnceSchedulableJob = runOnceSchedulableJobArgumentCaptor.getValue();
        assertEquals(SubscriptionEventKeys.SUBSCRIPTION_COMPLETE, runOnceSchedulableJob.getMotechEvent().getSubject());
        assertEquals(subscriptionId, runOnceSchedulableJob.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        ProcessSubscriptionRequest processSubscriptionRequest = (ProcessSubscriptionRequest) runOnceSchedulableJob.getMotechEvent().getParameters().get("0");
        assertEquals(ProcessSubscriptionRequest.class, processSubscriptionRequest.getClass());
        assertEquals(now.plusDays(3).toString("dd-mm-yyyy"), new DateTime(runOnceSchedulableJob.getStartDate()).toString("dd-mm-yyyy"));
        assertEquals(Channel.MOTECH,processSubscriptionRequest.getChannel());
    }

    @Test
    public void shouldDeactivateSubscription() {
        String subscriptionId = "abcd1234";
        UnsubscriptionRequest unsubscriptionRequest = new UnsubscriptionRequest();
        unsubscriptionRequest.setChannel(Channel.CALL_CENTER.name());

        kilkariSubscriptionService.requestDeactivation(subscriptionId, unsubscriptionRequest);

        ArgumentCaptor<DeactivationRequest> deactivationRequestArgumentCaptor = ArgumentCaptor.forClass(DeactivationRequest.class);
        verify(subscriptionService).requestDeactivation(deactivationRequestArgumentCaptor.capture());
        DeactivationRequest deactivationRequest = deactivationRequestArgumentCaptor.getValue();

        assertEquals(subscriptionId, deactivationRequest.getSubscriptionId());
        assertEquals(Channel.CALL_CENTER, deactivationRequest.getChannel());
    }
}
