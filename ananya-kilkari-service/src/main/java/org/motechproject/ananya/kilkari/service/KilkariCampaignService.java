package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.domain.CampaignMessageDeliveryReportRequestMapper;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordsRequest;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KilkariCampaignService {

    private KilkariMessageCampaignService kilkariMessageCampaignService;
    private KilkariSubscriptionService kilkariSubscriptionService;
    private CampaignMessageIdStrategy campaignMessageIdStrategy;
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    private CampaignMessageService campaignMessageService;
    private ReportingService reportingService;
    private OBDRequestPublisher obdRequestPublisher;

    private final Logger logger = LoggerFactory.getLogger(KilkariCampaignService.class);

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService, CampaignMessageIdStrategy campaignMessageIdStrategy, AllCampaignMessageAlerts allCampaignMessageAlerts, CampaignMessageService campaignMessageService, ReportingService reportingService, OBDRequestPublisher obdRequestPublisher) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageIdStrategy = campaignMessageIdStrategy;
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
        this.obdRequestPublisher = obdRequestPublisher;
    }

    public Map<String, List<DateTime>> getMessageTimings(String msisdn) {
        List<Subscription> subscriptionList = kilkariSubscriptionService.findByMsisdn(msisdn);
        Map<String, List<DateTime>> campaignMessageMap = new HashMap<>();
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();

            List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(
                    subscriptionId, subscription.getPack().name(),
                    subscription.getCreationDate(), subscription.endDate());

            campaignMessageMap.put(subscriptionId, messageTimings);
        }
        return campaignMessageMap;
    }

    public void scheduleWeeklyMessage(String subscriptionId) {
        synchronized (getLockName(subscriptionId)) {
            Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
            String messageId = campaignMessageIdStrategy.createMessageId(subscription);
            CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

            if (campaignMessageAlert == null) {
                processNewCampaignMessageAlert(subscriptionId, messageId, false);
                return;
            }

            boolean renewed = campaignMessageAlert.isRenewed();
            processExistingCampaignMessageAlert(subscription, messageId, renewed, campaignMessageAlert);

            if (campaignMessageIdStrategy.hasPackBeenCompleted(subscription))
                kilkariSubscriptionService.scheduleSubscriptionPackCompletionEvent(subscription);
        }
    }

    public void renewSchedule(String subscriptionId) {
        synchronized (getLockName(subscriptionId)) {
            Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
            CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

            if (campaignMessageAlert == null) {
                processNewCampaignMessageAlert(subscriptionId, null, true);
                return;
            }

            String messageId = campaignMessageAlert.getMessageId();
            processExistingCampaignMessageAlert(subscription, messageId, true, campaignMessageAlert);
        }
    }

    public void processSuccessfulMessageDelivery(OBDSuccessfulCallRequestWrapper obdRequestWrapper) {
        CampaignMessage campaignMessage = campaignMessageService.find(obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId());
        if(campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId()));
            return;
        }
        int retryCount = campaignMessage.getRetryCount();

        reportingService.reportCampaignMessageDelivered(new CampaignMessageDeliveryReportRequestMapper().mapFrom(obdRequestWrapper, retryCount));
        campaignMessageService.deleteCampaignMessage(campaignMessage);
    }

    public void processInvalidCallRecordsRequest(InvalidCallRecordsRequest invalidCallRecordsRequest) {
        obdRequestPublisher.publishInvalidCallRecordsRequest(invalidCallRecordsRequest);

    }

    public void processSuccessfulCallRequest(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        obdRequestPublisher.publishSuccessfulCallRequest(successfulCallRequestWrapper);
    }

    private void processExistingCampaignMessageAlert(Subscription subscription, String messageId, boolean renewed, CampaignMessageAlert campaignMessageAlert) {
        campaignMessageAlert.updateWith(messageId, renewed);

        if (!campaignMessageAlert.canBeScheduled()) {
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return;
        }

        campaignMessageService.scheduleCampaignMessage(subscription.getSubscriptionId(), messageId, subscription.getMsisdn(), subscription.getOperator().name());
        allCampaignMessageAlerts.remove(campaignMessageAlert);
    }

    private void processNewCampaignMessageAlert(String subscriptionId, String messageId, boolean renew) {
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId, renew);
        allCampaignMessageAlerts.add(campaignMessageAlert);
        return;
    }

    private String getLockName(String subscriptionId) {
        String lockName = getClass().getCanonicalName() + ":" + subscriptionId;
        return lockName.intern();
    }
}