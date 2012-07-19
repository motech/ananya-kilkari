package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.domain.CampaignMessageDeliveryReportRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.obd.contract.CallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordsRequest;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
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

            logger.info(String.format("Processing weekly message alert for subscriptionId: %s, messageId: %s", subscriptionId, messageId));

            CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

            if (campaignMessageAlert == null) {
                processNewCampaignMessageAlert(subscriptionId, messageId, false, calculateMessageExpiryTime(subscription));
                return;
            }

            boolean renewed = campaignMessageAlert.isRenewed();
            processExistingCampaignMessageAlert(subscription, messageId, renewed, campaignMessageAlert, calculateMessageExpiryTime(subscription));

            if (subscription.hasPackBeenCompleted())
                kilkariSubscriptionService.scheduleSubscriptionPackCompletionEvent(subscription);
        }
    }

    public void renewSchedule(String subscriptionId) {
        synchronized (getLockName(subscriptionId)) {
            logger.info("Processing renew schedule for subscriptionId: %s");

            Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
            CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

            if (campaignMessageAlert == null) {
                processNewCampaignMessageAlert(subscriptionId, null, true, null);
                return;
            }

            String messageId = campaignMessageAlert.getMessageId();
            DateTime messageExpiryTime = campaignMessageAlert.getMessageExpiryTime();
            processExistingCampaignMessageAlert(subscription, messageId, true, campaignMessageAlert, messageExpiryTime);
        }
    }

    public void processSuccessfulMessageDelivery(OBDSuccessfulCallRequestWrapper obdRequestWrapper) {
        CampaignMessage campaignMessage = campaignMessageService.find(obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId());
        if (campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId()));
            return;
        }
        int retryCount = campaignMessage.getRetryCount();

        reportingService.reportCampaignMessageDeliveryStatus(new CampaignMessageDeliveryReportRequestMapper().mapFrom(obdRequestWrapper, retryCount));
        campaignMessageService.deleteCampaignMessage(campaignMessage);
    }

    public void processInvalidCallRecordsRequest(InvalidCallRecordsRequest invalidCallRecordsRequest) {
        obdRequestPublisher.publishInvalidCallRecordsRequest(invalidCallRecordsRequest);
    }

    public void processSuccessfulCallRequest(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        obdRequestPublisher.publishSuccessfulCallRequest(successfulCallRequestWrapper);
    }

    public void processCallDeliveryFailureRequest(CallDeliveryFailureRecord callDeliveryFailureRecord) {
        obdRequestPublisher.publishCallDeliveryFailureRecord(callDeliveryFailureRecord);
    }

    private void processExistingCampaignMessageAlert(Subscription subscription, String messageId, boolean renewed,
                                                     CampaignMessageAlert campaignMessageAlert, DateTime messageExpiryTime) {
            campaignMessageAlert.updateWith(messageId, renewed, messageExpiryTime);
            logger.info(String.format("Found campaign message. Renewed: %s, messageId: %s", campaignMessageAlert.isRenewed(), campaignMessageAlert.getMessageId()));

        if (!campaignMessageAlert.canBeScheduled()) {
            logger.info("Campaign message can be scheduled. Moving it to obd module.");
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return;
        }

        logger.info(String.format("Campaign message can not be scheduled. Updating it with: Renewed: %s, messageId: %s", renewed, messageId));
        campaignMessageService.scheduleCampaignMessage(subscription.getSubscriptionId(), messageId, subscription.getMsisdn(), subscription.getOperator().name());
        allCampaignMessageAlerts.remove(campaignMessageAlert);
    }

    private DateTime calculateMessageExpiryTime(Subscription subscription) {
        int currentWeek = subscription.getWeeksElapsedAfterCreationDate();
        return isActivationWeek(currentWeek) ? null : subscription.getCreationDate().plusWeeks(currentWeek + 1);
    }

    private boolean isActivationWeek(int currentWeek) {
        return currentWeek == 0;
    }

    private void processNewCampaignMessageAlert(String subscriptionId, String messageId, boolean renew, DateTime messageExpiryTime) {
        logger.info(String.format("Creating a new record for campaign message alert - subscriptionId: %s, messageId: %s, renew: %s",subscriptionId,messageId,renew));
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId, renew, messageExpiryTime);
        allCampaignMessageAlerts.add(campaignMessageAlert);
        return;
    }

    private String getLockName(String subscriptionId) {
        String lockName = getClass().getCanonicalName() + ":" + subscriptionId;
        return lockName.intern();
    }
}