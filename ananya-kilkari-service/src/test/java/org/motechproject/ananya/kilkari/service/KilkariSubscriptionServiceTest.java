package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.factory.SubscriptionStateHandlerFactory;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.request.CampaignChangeRequest;
import org.motechproject.ananya.kilkari.request.SubscriptionWebRequest;
import org.motechproject.ananya.kilkari.request.UnsubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.DuplicateSubscriptionException;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class KilkariSubscriptionServiceTest {

    private KilkariSubscriptionService kilkariSubscriptionService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SubscriptionPublisher subscriptionPublisher;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private MessageCampaignService messageCampaignService;
    @Mock
    private SubscriptionStateHandlerFactory subscriptionStateHandlerFactory;
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private KilkariPropertiesData kilkariPropertiesData;

    @Before
    public void setup() {
        initMocks(this);
        kilkariSubscriptionService = new KilkariSubscriptionService(subscriptionPublisher, subscriptionService, motechSchedulerService, kilkariPropertiesData);
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @After
    public void clear() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldCreateSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequest();
        kilkariSubscriptionService.createSubscriptionAsync(subscriptionWebRequest);
        verify(subscriptionPublisher).createSubscription(subscriptionWebRequest);
    }

    @Test
    public void shouldGetSubscriptionsFor() {
        String msisdn = "1234567890";
        kilkariSubscriptionService.findByMsisdn(msisdn);
        verify(subscriptionService).findByMsisdn(msisdn);
    }

    @Test
    public void shouldCreateSubscriptionRequestAsynchronously() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequest();
        subscriptionWebRequest.setCreatedAt(DateTime.now());

        kilkariSubscriptionService.createSubscriptionAsync(subscriptionWebRequest);

        verify(subscriptionPublisher).createSubscription(subscriptionWebRequest);
    }

    @Test
    public void shouldCreateSubscriptionSynchronously() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().build();

        kilkariSubscriptionService.createSubscription(subscriptionWebRequest);

        ArgumentCaptor<SubscriptionRequest> subscriptionArgumentCaptor = ArgumentCaptor.forClass(SubscriptionRequest.class);
        verify(subscriptionService).createSubscription(subscriptionArgumentCaptor.capture(), eq(Channel.CALL_CENTER));
        SubscriptionRequest actualSubscription = subscriptionArgumentCaptor.getValue();
        assertEquals(subscriptionWebRequest.getMsisdn(), actualSubscription.getMsisdn());
    }

    @Test
    public void shouldValidateSubscriptionRequest() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withMsisdn("abcd").build();
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid msisdn abcd");

        kilkariSubscriptionService.createSubscription(subscriptionWebRequest);

        verify(subscriptionService, never()).createSubscription(any(SubscriptionRequest.class), any(Channel.class));
    }

    @Test
    public void shouldJustLogIfSubscriptionAlreadyExists() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().build();

        when(subscriptionService.createSubscription(any(SubscriptionRequest.class), any(Channel.class))).thenThrow(new DuplicateSubscriptionException(""));

        try {
            kilkariSubscriptionService.createSubscription(subscriptionWebRequest);
        } catch (Exception e) {
            Assert.fail("Unexpected Exception " + e.getMessage());
        }
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
        OMSubscriptionRequest OMSubscriptionRequest = (OMSubscriptionRequest) runOnceSchedulableJob.getMotechEvent().getParameters().get("0");
        assertEquals(OMSubscriptionRequest.class, OMSubscriptionRequest.getClass());
        assertEquals(now.plusDays(3).toDate(), runOnceSchedulableJob.getStartDate());
        assertEquals(Channel.MOTECH, OMSubscriptionRequest.getChannel());
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

    @Test
    public void shouldProcessCampaignChange(){
        CampaignChangeRequest campaignChangeRequest = new CampaignChangeRequest();
        String subscriptionId = "subscriptionId";
        String reason = "MISCARRIAGE";
        DateTime createdAt = DateTime.now();
        campaignChangeRequest.setSubscriptionId(subscriptionId);
        campaignChangeRequest.setReason(reason);
        campaignChangeRequest.setCreatedAt(createdAt);

        kilkariSubscriptionService.processCampaignChange(campaignChangeRequest);

        ArgumentCaptor<CampaignRescheduleRequest> campaignRescheduleRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignRescheduleRequest.class);
        verify(subscriptionService).rescheduleCampaign(campaignRescheduleRequestArgumentCaptor.capture());
        CampaignRescheduleRequest campaignRescheduleRequest = campaignRescheduleRequestArgumentCaptor.getValue();
        assertEquals(subscriptionId, campaignRescheduleRequest.getSubscriptionId());
        assertEquals(reason, campaignRescheduleRequest.getReason().name());
        assertEquals(createdAt, campaignRescheduleRequest.getCreatedAt());
    }
}
