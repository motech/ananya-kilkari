package org.motechproject.ananya.kilkari.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.domain.CampaignMessageDeliveryReportRequestMapper;
import org.motechproject.ananya.kilkari.domain.SubscriberCareRequest;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordRequestObject;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallRecordsRequest;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.request.OBDRequest;
import org.motechproject.ananya.kilkari.request.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
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
    private SubscriberCareService subscriberCareService;
    private CampaignMessageIdStrategy campaignMessageIdStrategy;
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    private CampaignMessageService campaignMessageService;
    private ReportingService reportingService;
    private OBDRequestCallbackPublisher obdRequestCallbackPublisher;

    private final Logger LOGGER = LoggerFactory.getLogger(KilkariCampaignService.class);

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService, SubscriberCareService subscriberCareService, CampaignMessageIdStrategy campaignMessageIdStrategy, AllCampaignMessageAlerts allCampaignMessageAlerts, CampaignMessageService campaignMessageService, ReportingService reportingService, OBDRequestCallbackPublisher obdRequestCallbackPublisher) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.subscriberCareService = subscriberCareService;
        this.campaignMessageIdStrategy = campaignMessageIdStrategy;
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
        this.obdRequestCallbackPublisher = obdRequestCallbackPublisher;
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

    public void processSuccessfulMessageDelivery(OBDRequestWrapper obdRequestWrapper) {
        CampaignMessage campaignMessage = campaignMessageService.find(obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId());
        if(campaignMessage == null) {
            LOGGER.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId()));
            return;
        }
        int retryCount = campaignMessage.getRetryCount();

        reportingService.reportCampaignMessageDelivered(new CampaignMessageDeliveryReportRequestMapper().mapFrom(obdRequestWrapper, retryCount));
        createSubscriberCareDoc(obdRequestWrapper.getObdRequest());
        campaignMessageService.deleteCampaignMessage(campaignMessage);
    }

    public void processInvalidCallRecords(InvalidCallRecordsRequest invalidCallRecordsRequest) {
        ArrayList<InvalidCallRecordRequestObject> requestCallRecords = invalidCallRecordsRequest.getCallrecords();
        ArrayList<InvalidCallRecord> invalidCallRecords = new ArrayList<>();
        for(InvalidCallRecordRequestObject requestObject : requestCallRecords){
            invalidCallRecords.add(new InvalidCallRecord(requestObject.getMsisdn(), requestObject.getSubscriptionId(),
                    requestObject.getCampaignId(), requestObject.getOperator(), requestObject.getDescription()));
        }
        campaignMessageService.processInvalidCallRecords(invalidCallRecords);
    }

    public void processOBDCallbackRequest(OBDRequestWrapper obdRequestWrapper) {
        obdRequestCallbackPublisher.publishObdCallbackRequest(obdRequestWrapper);
    }

    private void processExistingCampaignMessageAlert(Subscription subscription, String messageId, boolean renewed, CampaignMessageAlert campaignMessageAlert) {
        campaignMessageAlert.updateWith(messageId, renewed);

        if (!campaignMessageAlert.canBeScheduled()) {
            allCampaignMessageAlerts.update(campaignMessageAlert);
            return;
        }

        campaignMessageService.scheduleCampaignMessage(subscription.getSubscriptionId(), messageId, subscription.getMsisdn(), subscription.getOperator().name());
        campaignMessageAlert.clear();
        allCampaignMessageAlerts.update(campaignMessageAlert);
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

    private void createSubscriberCareDoc(OBDRequest obdRequest) {
        if(StringUtils.equalsIgnoreCase(obdRequest.getServiceOption(), ServiceOption.HELP.name())) {
            SubscriberCareRequest subscriberCareRequest = new SubscriberCareRequest(obdRequest.getMsisdn(), obdRequest.getServiceOption(), Channel.IVR.name());
            subscriberCareService.createSubscriberCareRequest(subscriberCareRequest);
        }
    }
}