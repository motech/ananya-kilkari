package org.motechproject.ananya.kilkari.purge;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.InvalidOBDEntriesService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CouchDbPurgeServiceTest {
    @Mock
    private CampaignMessageAlertService campaignMessageAlertService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private InboxService inboxService;
    @Mock
    private CampaignMessageService campaignMessageService;
    @Mock
    private SubscriberCareService subscriberCareService;
    @Mock
    private InvalidOBDEntriesService invalidOBDEntriesService;
    @Mock
    private MessageCampaignService messageCampaignService;

    private CouchDbPurgeService couchdbPurgeService;
    private String msisdn;
    private Subscription subscription;

    @Before
    public void setUp(){
        couchdbPurgeService = new CouchDbPurgeService(campaignMessageAlertService, subscriptionService, inboxService, campaignMessageService, subscriberCareService, invalidOBDEntriesService, messageCampaignService);
        msisdn = "234566";
        subscription = new Subscription(msisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now(), DateTime.now());

    }

    @Test
    public void shouldDeleteCampaignMessageAlerts() {
        when(subscriptionService.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(campaignMessageAlertService).deleteFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteInboxMessage() {
        when(subscriptionService.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(inboxService).deleteInbox(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteCampaignMessage() {
        when(subscriptionService.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(campaignMessageService).deleteCampaignMessagesFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteSubscriberCareDocs() {
        couchdbPurgeService.purge(msisdn);

        verify(subscriberCareService).deleteCareDocsFor(msisdn);
    }

    @Test
    public void shouldDeleteSubscription() {
        when(subscriptionService.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(subscriptionService).deleteSubscriptionFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteInvalidObdEntries() {
        when(subscriptionService.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(invalidOBDEntriesService).deleteInvalidCallRecordsFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteCampaignEnrollments() {
        when(subscriptionService.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(messageCampaignService).deleteCampaignEnrollmentsFor(subscription.getSubscriptionId());
    }
}
