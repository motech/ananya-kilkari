package org.motechproject.ananya.kilkari.purge;

import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.InvalidOBDEntriesService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.SubscriberCareService;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CouchDbPurgeService {
    private CampaignMessageAlertService campaignMessageAlertService;
    private SubscriptionService subscriptionService;
    private InboxService inboxService;
    private CampaignMessageService campaignMessageService;
    private SubscriberCareService subscriberCareService;
    private InvalidOBDEntriesService invalidOBDEntriesService;
    private MessageCampaignService messageCampaignService;

    @Autowired
    public CouchDbPurgeService(CampaignMessageAlertService campaignMessageAlertService,
                               SubscriptionService subscriptionService,
                               InboxService inboxService,
                               CampaignMessageService campaignMessageService,
                               SubscriberCareService subscriberCareService,
                               InvalidOBDEntriesService invalidOBDEntriesService,
                               MessageCampaignService messageCampaignService) {
        this.campaignMessageAlertService = campaignMessageAlertService;
        this.subscriptionService = subscriptionService;
        this.inboxService = inboxService;
        this.campaignMessageService = campaignMessageService;
        this.subscriberCareService = subscriberCareService;
        this.invalidOBDEntriesService = invalidOBDEntriesService;
        this.messageCampaignService = messageCampaignService;
    }

    public void purge(String msisdn) {
        List<Subscription> subscriptionList = subscriptionService.findByMsisdn(msisdn);
        deleteBySubscriptionId(subscriptionList);
        deleteByMsisdn(msisdn);
    }

    private void deleteByMsisdn(String msisdn) {
        subscriberCareService.deleteCareDocsFor(msisdn);
    }

    private void deleteBySubscriptionId(List<Subscription> subscriptionList) {
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();
            campaignMessageAlertService.deleteFor(subscriptionId);
            inboxService.deleteInbox(subscriptionId);
            campaignMessageService.deleteCampaignMessagesFor(subscriptionId);
            subscriptionService.deleteSubscriptionFor(subscriptionId);
            invalidOBDEntriesService.deleteInvalidCallRecordsFor(subscriptionId);
            messageCampaignService.deleteCampaignEnrollmentsFor(subscriptionId);
        }
    }
}
