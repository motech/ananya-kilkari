package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingServiceImpl;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.repository.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriptionDetailsResponseMapper;
import org.motechproject.ananya.kilkari.subscription.validators.ChangeMsisdnValidator;
import org.motechproject.ananya.kilkari.subscription.validators.SubscriptionValidator;
import org.motechproject.ananya.kilkari.subscription.validators.UnsubscriptionValidator;
import org.motechproject.ananya.kilkari.sync.service.RefdataSyncService;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionStateChangeRequest;
import org.motechproject.scheduler.MotechSchedulerService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionServiceStateCheckTest {

    private SubscriptionService subscriptionService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private OnMobileSubscriptionManagerPublisher onMobileSubscriptionManagerPublisher;
    @Mock
    private SubscriptionValidator subscriptionValidator;
    @Mock
    private ReportingServiceImpl reportingServiceImpl;
    @Mock
    private AllInboxMessages allInboxMessages;
    @Mock
    private InboxService inboxService;
    @Mock
    private MessageCampaignService messageCampaignService;
    @Mock
    private OnMobileSubscriptionGateway onMobileSubscriptionGateway;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private CampaignMessageAlertService campaignMessageAlertService;
    @Mock
    private KilkariPropertiesData kilkariPropertiesData;
    @Mock
    private MotechSchedulerService motechSchedulerService;
    @Mock
    private ChangeMsisdnValidator changeMsisdnValidator;
    @Mock
    private UnsubscriptionValidator unsubscriptionValidator;
    @Mock
    private RefdataSyncService refdataSyncService;
    @Mock
    private SubscriptionDetailsResponseMapper subscriptionDetailsResponseMapper;
    private String subscriptionId;
    private Subscription mockSubscription;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionService = new SubscriptionService(allSubscriptions, onMobileSubscriptionManagerPublisher, subscriptionValidator, reportingServiceImpl,
                inboxService, messageCampaignService, onMobileSubscriptionGateway, campaignMessageService, campaignMessageAlertService, kilkariPropertiesData, motechSchedulerService, changeMsisdnValidator, unsubscriptionValidator, refdataSyncService, subscriptionDetailsResponseMapper);
        subscriptionId = "subscriptionId";
        mockSubscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(mockSubscription);
    }

    @Test
    public void shouldNotActivateIfNotInTheRightState() {
        when(mockSubscription.canActivate()).thenReturn(false);

        subscriptionService.activate(subscriptionId, DateTime.now(), "AIRTEL", "ivr");

        verify(mockSubscription).canActivate();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotFailActivationIfNotInTheRightState() {
        when(mockSubscription.canFailActivation()).thenReturn(false);

        subscriptionService.activationFailed(subscriptionId, DateTime.now(), "reason", "AIRTEL", "ivr");

        verify(mockSubscription).canFailActivation();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotGoToPendingActivationStateIfNotInTheRightState() {
        when(mockSubscription.canSendActivationRequest()).thenReturn(false);

        subscriptionService.activationRequested(new OMSubscriptionRequest("123", SubscriptionPack.BARI_KILKARI, Channel.IVR, subscriptionId, "ivr"));

        verify(mockSubscription).canSendActivationRequest();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotGoToDeactivationRequestReceivedStateIfNotInTheRightState() {
        when(mockSubscription.canReceiveDeactivationRequest()).thenReturn(false);

        subscriptionService.requestDeactivation(new DeactivationRequest(subscriptionId, Channel.IVR, DateTime.now(),null, "ivr"));

        verify(mockSubscription).canReceiveDeactivationRequest();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotGoToPendingDeactivationStateIfNotInTheRightState() {
        when(mockSubscription.canMoveToPendingDeactivation()).thenReturn(false);

        subscriptionService.deactivationRequested(new OMSubscriptionRequest(null, null, null, subscriptionId, "ivr"));

        verify(mockSubscription).canMoveToPendingDeactivation();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotRenewIfNotInTheRightState() {
        when(mockSubscription.canActivate()).thenReturn(false);

        subscriptionService.renewSubscription(subscriptionId, null, null, "ivr");

        verify(mockSubscription).canActivate();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotSuspendIfNotInTheRightState() {
        when(mockSubscription.canSuspend()).thenReturn(false);

        subscriptionService.suspendSubscription(subscriptionId, null, null, null, "ivr");

        verify(mockSubscription).canSuspend();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void shouldNotDeactivateIfNotInTheRightState() {
        when(mockSubscription.canDeactivate()).thenReturn(false);

        subscriptionService.deactivateSubscription(subscriptionId, null, null, null, "ivr");

        verify(mockSubscription).canDeactivate();
        verifySubscriptionStatusUpdation();
    }

    @Test
    public void deactivationShouldCheckBothDeactivationAndCompletionState() {
        when(mockSubscription.canDeactivate()).thenReturn(false);
        when(mockSubscription.canComplete()).thenReturn(false);


        verify(mockSubscription).canDeactivate();
        verify(mockSubscription).canComplete();
        verifySubscriptionStatusUpdation();
    }

    private void verifySubscriptionStatusUpdation() {
        verify(allSubscriptions, never()).update(mockSubscription);
        verify(reportingServiceImpl, never()).reportSubscriptionStateChange(any(SubscriptionStateChangeRequest.class));
    }

}
