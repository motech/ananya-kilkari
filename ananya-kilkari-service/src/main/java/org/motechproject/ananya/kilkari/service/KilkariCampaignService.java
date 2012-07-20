package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.domain.CampaignMessageDeliveryReportRequestMapper;
import org.motechproject.ananya.kilkari.domain.CampaignTriggerType;
import org.motechproject.ananya.kilkari.mapper.ValidCallDeliveryFailureRecordObjectMapper;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.obd.contract.*;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.motechproject.ananya.kilkari.validators.CallDeliveryFailureRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;
    private ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper;

    private final Logger logger = LoggerFactory.getLogger(KilkariCampaignService.class);

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService,
                                  CampaignMessageIdStrategy campaignMessageIdStrategy,
                                  AllCampaignMessageAlerts allCampaignMessageAlerts,
                                  CampaignMessageService campaignMessageService,
                                  ReportingService reportingService,
                                  OBDRequestPublisher obdRequestPublisher,
                                  CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator,
                                  ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageIdStrategy = campaignMessageIdStrategy;
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
        this.obdRequestPublisher = obdRequestPublisher;
        this.callDeliveryFailureRecordValidator = callDeliveryFailureRecordValidator;
        this.validCallDeliveryFailureRecordObjectMapper = validCallDeliveryFailureRecordObjectMapper;
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
                processNewCampaignMessageAlert(subscriptionId, messageId, false, subscription.expiryDate());
                return;
            }

            boolean renewed = campaignMessageAlert.isRenewed();
            processExistingCampaignMessageAlert(subscription, messageId, renewed, campaignMessageAlert, subscription.expiryDate(), CampaignTriggerType.WEEKLY_MESSAGE);

            if (subscription.hasPackBeenCompleted())
                kilkariSubscriptionService.scheduleSubscriptionPackCompletionEvent(subscription);
        }
    }

    public void activateOrRenewSchedule(String subscriptionId, CampaignTriggerType campaignTriggerType) {
        synchronized (getLockName(subscriptionId)) {
            logger.info("Processing renew schedule for subscriptionId: %s");

            Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
            CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

            if (campaignMessageAlert == null) {
                processNewCampaignMessageAlert(subscriptionId, null, true, null);
                return;
            }

            String messageId = campaignMessageAlert.getMessageId();
            DateTime messageExpiryTime = campaignMessageAlert.getMessageExpiryDate();
            processExistingCampaignMessageAlert(subscription, messageId, true, campaignMessageAlert, messageExpiryTime, campaignTriggerType);
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

    public void publishInvalidCallRecordsRequest(InvalidCallRecordsRequest invalidCallRecordsRequest) {
        obdRequestPublisher.publishInvalidCallRecordsRequest(invalidCallRecordsRequest);
    }

    public void publishSuccessfulCallRequest(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        obdRequestPublisher.publishSuccessfulCallRequest(successfulCallRequestWrapper);
    }

    public void publishCallDeliveryFailureRequest(CallDeliveryFailureRecord callDeliveryFailureRecord) {
        obdRequestPublisher.publishCallDeliveryFailureRecord(callDeliveryFailureRecord);
    }

    public void processCallDeliveryFailureRecord(CallDeliveryFailureRecord callDeliveryFailureRecord) {
        List<InvalidCallDeliveryFailureRecordObject> invalidCallDeliveryFailureRecordObjects = new ArrayList<>();
        List<ValidCallDeliveryFailureRecordObject> validCallDeliveryFailureRecordObjects = new ArrayList<>();
        validate(callDeliveryFailureRecord, validCallDeliveryFailureRecordObjects, invalidCallDeliveryFailureRecordObjects);

        publishErrorRecords(invalidCallDeliveryFailureRecordObjects);
        publishValidRecords(validCallDeliveryFailureRecordObjects);
    }

    private void processExistingCampaignMessageAlert(Subscription subscription, String messageId, boolean renewed,
                                                     CampaignMessageAlert campaignMessageAlert, DateTime messageExpiryTime,
                                                     CampaignTriggerType campaignTriggerType) {
        campaignMessageAlert.updateWith(messageId, renewed, messageExpiryTime);
        logger.info("Found campaign message: %s", campaignMessageAlert);


        if (!campaignMessageAlert.canBeScheduled(campaignTriggerType)) {
            logger.info("Campaign message can not be scheduled");
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return;
        }

        logger.info(String.format("Campaign message can be scheduled. Updating it with: Renewed: %s, messageId: %s", renewed, messageId));
        campaignMessageService.scheduleCampaignMessage(subscription.getSubscriptionId(), messageId, subscription.getMsisdn(), subscription.getOperator().name());
        allCampaignMessageAlerts.remove(campaignMessageAlert);
    }

    private void processNewCampaignMessageAlert(String subscriptionId, String messageId, boolean renew, DateTime messageExpiryTime) {
        logger.info(String.format("Creating a new record for campaign message alert - subscriptionId: %s, messageId: %s, renew: %s", subscriptionId, messageId, renew));
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId, renew, messageExpiryTime);
        allCampaignMessageAlerts.add(campaignMessageAlert);
    }

    private String getLockName(String subscriptionId) {
        String lockName = getClass().getCanonicalName() + ":" + subscriptionId;
        return lockName.intern();
    }

    private void validate(CallDeliveryFailureRecord callDeliveryFailureRecord, List<ValidCallDeliveryFailureRecordObject> validCallDeliveryFailureRecordObjects, List<InvalidCallDeliveryFailureRecordObject> invalidCallDeliveryFailureRecordObjects) {
        for (CallDeliveryFailureRecordObject callDeliveryFailureRecordObject : callDeliveryFailureRecord.getCallrecords()) {
            List<String> errors = callDeliveryFailureRecordValidator.validate(callDeliveryFailureRecordObject);
            if (!errors.isEmpty()) {
                InvalidCallDeliveryFailureRecordObject invalidCallDeliveryFailureRecordObject = new InvalidCallDeliveryFailureRecordObject(callDeliveryFailureRecordObject.getMsisdn(),
                        callDeliveryFailureRecordObject.getSubscriptionId(), StringUtils.join(errors, ","));
                invalidCallDeliveryFailureRecordObjects.add(invalidCallDeliveryFailureRecordObject);
                continue;
            }

            ValidCallDeliveryFailureRecordObject validCallDeliveryFailureRecordObject = validCallDeliveryFailureRecordObjectMapper.mapFrom(callDeliveryFailureRecordObject, callDeliveryFailureRecord);
            validCallDeliveryFailureRecordObjects.add(validCallDeliveryFailureRecordObject);
        }
    }

    private void publishErrorRecords(List<InvalidCallDeliveryFailureRecordObject> invalidCallDeliveryFailureRecordObjects) {
        if (invalidCallDeliveryFailureRecordObjects.isEmpty())
            return;
        InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord = new InvalidCallDeliveryFailureRecord();
        invalidCallDeliveryFailureRecord.setRecordObjects(invalidCallDeliveryFailureRecordObjects);

        obdRequestPublisher.publishInvalidCallDeliveryFailureRecord(invalidCallDeliveryFailureRecord);
    }

    private void publishValidRecords(List<ValidCallDeliveryFailureRecordObject> validCallDeliveryFailureRecordObjects) {
        for (ValidCallDeliveryFailureRecordObject recordObject : validCallDeliveryFailureRecordObjects) {
            obdRequestPublisher.publishValidCallDeliveryFailureRecord(recordObject);
        }
    }
}