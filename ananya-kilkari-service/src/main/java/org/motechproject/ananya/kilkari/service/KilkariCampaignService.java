package org.motechproject.ananya.kilkari.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxEventKeys;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionEventKeys;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.motechproject.scheduler.MotechSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KilkariCampaignService {

    private MessageCampaignService messageCampaignService;
    private KilkariSubscriptionService kilkariSubscriptionService;
    private CampaignMessageAlertService campaignMessageAlertService;
    private InboxService inboxService;

    private final Logger logger = LoggerFactory.getLogger(KilkariCampaignService.class);
    private MotechSchedulerService motechSchedulerService;

    KilkariCampaignService() {
    }

    @Autowired
    public KilkariCampaignService(MessageCampaignService messageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService,
                                  CampaignMessageAlertService campaignMessageAlertService,
                                  InboxService inboxService, MotechSchedulerService motechSchedulerService) {
        this.messageCampaignService = messageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageAlertService = campaignMessageAlertService;
        this.inboxService = inboxService;
        this.motechSchedulerService = motechSchedulerService;
    }

    public Map<String, List<DateTime>> getTimings(String msisdn) {
        List<Subscription> subscriptionList = kilkariSubscriptionService.findByMsisdn(msisdn);
        Map<String, List<DateTime>> subscriptionEventsMap = new HashMap<>();
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();
            try {
                subscriptionEventsMap.put("Message Schedule: " + subscriptionId, getMessageTimings(subscription));
                subscriptionEventsMap.put("Inbox Deletion: " + subscriptionId, getScheduleTimings(subscription, InboxEventKeys.DELETE_INBOX));
                subscriptionEventsMap.put("Subscription Deactivation: " + subscriptionId, getScheduleTimings(subscription, SubscriptionEventKeys.DEACTIVATE_SUBSCRIPTION));
                subscriptionEventsMap.put("Subscription Completion: " + subscriptionId, getScheduleTimings(subscription, SubscriptionEventKeys.SUBSCRIPTION_COMPLETE));
            } catch (NullPointerException ne) {
                //ignore
            }
        }
        return subscriptionEventsMap;
    }

    private List<DateTime> getScheduleTimings(Subscription subscription, String subject) {
        Date startDate = subscription.getCreationDate().toDate();
        Date endDate = subscription.endDate().plusWeeks(2).toDate();
        List<Date> timings = motechSchedulerService.getScheduledJobTimingsWithPrefix(subject, subscription.getSubscriptionId(), startDate, endDate);

        return (List<DateTime>) CollectionUtils.collect(timings, new Transformer() {
            @Override
            public Object transform(Object input) {
                return new DateTime(input);
            }
        });
    }

    private List<DateTime> getMessageTimings(Subscription subscription) {
        return messageCampaignService.getMessageTimings(subscription.getSubscriptionId(), subscription.getCreationDate(), subscription.endDate());
    }

    public void scheduleWeeklyMessage(String subscriptionId, String campaignName) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);

        final String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, messageCampaignService.getCampaignStartDate(subscriptionId, campaignName), subscription.getPack());
        final DateTime messageExpiryDate = subscription.getCurrentWeeksMessageExpiryDate();

        campaignMessageAlertService.scheduleCampaignMessageAlert(subscriptionId, messageId, messageExpiryDate, subscription.getMsisdn(), subscription.getOperator().name());

        if (subscription.hasBeenActivated())
            inboxService.newMessage(subscriptionId, messageId);
    }

    public void processCampaignCompletion(String subscriptionId) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        if (!subscription.isInDeactivatedState())
            kilkariSubscriptionService.processSubscriptionCompletion(subscription);
    }
}