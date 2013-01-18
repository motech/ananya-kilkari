package org.motechproject.ananya.kilkari.purge.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.message.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.message.repository.AllInboxMessages;
import org.motechproject.ananya.kilkari.messagecampaign.repository.AllKilkariCampaignEnrollments;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.AllInvalidCallRecords;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KilkariPurgeServiceTest {

    @Mock
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    @Mock
    private AllSubscriptions allSubscriptions;
    @Mock
    private AllInboxMessages allInboxMessages;
    @Mock
    private AllCampaignMessages allCampaignMessages;
    @Mock
    private AllSubscriberCareDocs allSubscriberCareDocs;
    @Mock
    private AllInvalidCallRecords allInvalidCallRecords;
    @Mock
    private AllKilkariCampaignEnrollments allKilkariCampaignEnrollments;

    private KilkariPurgeService couchdbPurgeService;
    private String msisdn;
    private Subscription subscription;

    @Before
    public void setUp(){
        couchdbPurgeService = new KilkariPurgeService(allCampaignMessageAlerts, allSubscriptions, allInboxMessages, allCampaignMessages, allSubscriberCareDocs, allInvalidCallRecords, allKilkariCampaignEnrollments);
        msisdn = "234566";
        subscription = new Subscription(msisdn, SubscriptionPack.NANHI_KILKARI, DateTime.now(), DateTime.now(), null);

    }

    @Test
    public void shouldDeleteCampaignMessageAlerts() {
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(allCampaignMessageAlerts).deleteFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteInboxMessage() {
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(allInboxMessages).deleteFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteCampaignMessage() {
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(allCampaignMessages).removeAll(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteSubscriberCareDocs() {
        couchdbPurgeService.purge(msisdn);

        verify(allSubscriberCareDocs).deleteFor(msisdn);
    }

    @Test
    public void shouldDeleteSubscription() {
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(allSubscriptions).deleteFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteInvalidObdEntries() {
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(allInvalidCallRecords).deleteFor(subscription.getSubscriptionId());
    }

    @Test
    public void shouldDeleteCampaignEnrollments() {
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Arrays.asList(subscription));

        couchdbPurgeService.purge(msisdn);

        verify(allCampaignMessages).removeAll(subscription.getSubscriptionId());
    }
    
    @Test
    public void shouldNotTryToDeleteBasedOnSubscriptionIdIfSubscriptionsDontExist(){
        when(allSubscriptions.findByMsisdn(msisdn)).thenReturn(Collections.EMPTY_LIST);
        
        couchdbPurgeService.purge(msisdn);

        verify(allCampaignMessageAlerts, never()).deleteFor(anyString());
        verify(allInboxMessages, never()).deleteFor(anyString());
        verify(allCampaignMessages, never()).removeAll(anyString());
        verify(allInvalidCallRecords, never()).deleteFor(anyString());
        verify(allKilkariCampaignEnrollments, never()).deleteFor(anyString());
        verify(allSubscriptions, never()).deleteFor(anyString());
    }
}
