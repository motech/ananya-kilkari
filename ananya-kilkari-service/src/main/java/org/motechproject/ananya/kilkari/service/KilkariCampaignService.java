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
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.service.KilkariInboxService;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.motechproject.ananya.kilkari.validators.CallDeliveryFailureRecordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private KilkariInboxService kilkariInboxService;

    private ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper;
    private final Logger logger = LoggerFactory.getLogger(KilkariCampaignService.class);

    KilkariCampaignService() {
    }

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService,
                                  CampaignMessageIdStrategy campaignMessageIdStrategy,
                                  AllCampaignMessageAlerts allCampaignMessageAlerts,
                                  CampaignMessageService campaignMessageService,
                                  ReportingService reportingService,
                                  OBDRequestPublisher obdRequestPublisher,
                                  CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator,
                                  KilkariInboxService kilkariInboxService,
                                  ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageIdStrategy = campaignMessageIdStrategy;
        this.allCampaignMessageAlerts = allCampaignMessageAlerts;
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
        this.obdRequestPublisher = obdRequestPublisher;
        this.callDeliveryFailureRecordValidator = callDeliveryFailureRecordValidator;
        this.kilkariInboxService = kilkariInboxService;
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
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        String messageId = campaignMessageIdStrategy.createMessageId(subscription);
        logger.info(String.format("Processing weekly message alert for subscriptionId: %s, messageId: %s", subscriptionId, messageId));

        CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);


        if (campaignMessageAlert == null)
            processNewCampaignMessageAlert(subscriptionId, messageId, false, subscription.currentWeeksMessageExpiryDate());
        else
            processExistingCampaignMessageAlert(subscription, messageId, campaignMessageAlert.isRenewed(), campaignMessageAlert, subscription.currentWeeksMessageExpiryDate(), CampaignTriggerType.WEEKLY_MESSAGE);

        if (subscription.hasBeenActivated())
            kilkariInboxService.newMessage(subscriptionId, messageId);

        if (subscription.hasPackBeenCompleted() && !subscription.isInDeactivatedState())
            kilkariSubscriptionService.processSubscriptionCompletion(subscription);
    }

    public void activateOrRenewSchedule(String subscriptionId, CampaignTriggerType campaignTriggerType) {
        logger.info("Processing renew schedule for subscriptionId: %s");

        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        CampaignMessageAlert campaignMessageAlert = allCampaignMessageAlerts.findBySubscriptionId(subscriptionId);

        if (campaignMessageAlert == null) {
            processNewCampaignMessageAlert(subscriptionId, null, true, null);
            return;
        }

        String messageId = campaignMessageAlert.getMessageId();
        processExistingCampaignMessageAlert(subscription, messageId, true, campaignMessageAlert, campaignMessageAlert.getMessageExpiryDate(), campaignTriggerType);

        if(CampaignTriggerType.ACTIVATION.equals(campaignTriggerType))
            kilkariInboxService.newMessage(subscriptionId, messageId);
    }

    public void processSuccessfulMessageDelivery(OBDSuccessfulCallRequestWrapper obdRequestWrapper) {
        CampaignMessage campaignMessage = campaignMessageService.find(obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId());
        if (campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getCampaignId()));
            return;
        }
        int retryCount = campaignMessage.getDnpRetryCount();

        reportingService.reportCampaignMessageDeliveryStatus(new CampaignMessageDeliveryReportRequestMapper().mapFrom(obdRequestWrapper, retryCount));
        campaignMessageService.deleteCampaignMessage(campaignMessage);
    }

    public void publishInvalidCallRecordsRequest(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        obdRequestPublisher.publishInvalidCallRecordsRequest(invalidOBDRequestEntries);
    }

    public void publishSuccessfulCallRequest(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        obdRequestPublisher.publishSuccessfulCallRequest(successfulCallRequestWrapper);
    }

    public void publishCallDeliveryFailureRequest(FailedCallReports failedCallReports) {
        obdRequestPublisher.publishCallDeliveryFailureRecord(failedCallReports);
    }

    @Transactional
    public void processCallDeliveryFailureRecord(FailedCallReports failedCallReports) {
        List<InvalidFailedCallReport> invalidFailedCallReports = new ArrayList<>();
        List<ValidFailedCallReport> validFailedCallReports = new ArrayList<>();
        validate(failedCallReports, validFailedCallReports, invalidFailedCallReports);

        publishErrorRecords(invalidFailedCallReports);
        publishValidRecords(validFailedCallReports);
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
        campaignMessageService.scheduleCampaignMessage(subscription.getSubscriptionId(), messageId, subscription.getMsisdn(), subscription.getOperator().name(), messageExpiryTime);
        allCampaignMessageAlerts.remove(campaignMessageAlert);
    }

    private void processNewCampaignMessageAlert(String subscriptionId, String messageId, boolean renew, DateTime messageExpiryTime) {
        logger.info(String.format("Creating a new record for campaign message alert - subscriptionId: %s, messageId: %s, renew: %s", subscriptionId, messageId, renew));
        CampaignMessageAlert campaignMessageAlert = new CampaignMessageAlert(subscriptionId, messageId, renew, messageExpiryTime);
        allCampaignMessageAlerts.add(campaignMessageAlert);
    }

    private void validate(FailedCallReports failedCallReports, List<ValidFailedCallReport> validFailedCallReports, List<InvalidFailedCallReport> invalidFailedCallReports) {
        for (FailedCallReport failedCallReport : failedCallReports.getCallrecords()) {
            List<String> errors = callDeliveryFailureRecordValidator.validate(failedCallReport);
            if (!errors.isEmpty()) {
                InvalidFailedCallReport invalidFailedCallReport = new InvalidFailedCallReport(failedCallReport.getMsisdn(),
                        failedCallReport.getSubscriptionId(), StringUtils.join(errors, ","));
                invalidFailedCallReports.add(invalidFailedCallReport);
                continue;
            }

            ValidFailedCallReport validFailedCallReport = validCallDeliveryFailureRecordObjectMapper.mapFrom(failedCallReport, failedCallReports);
            validFailedCallReports.add(validFailedCallReport);
        }
    }

    private void publishErrorRecords(List<InvalidFailedCallReport> invalidCallDeliveryFailureRecordObjects) {
        if (invalidCallDeliveryFailureRecordObjects.isEmpty())
            return;
        InvalidFailedCallReports invalidFailedCallReports = new InvalidFailedCallReports();
        invalidFailedCallReports.setRecordObjectFaileds(invalidCallDeliveryFailureRecordObjects);

        obdRequestPublisher.publishInvalidCallDeliveryFailureRecord(invalidFailedCallReports);
    }

    private void publishValidRecords(List<ValidFailedCallReport> validFailedCallReports) {
        for (ValidFailedCallReport failedCallReport : validFailedCallReports) {
            obdRequestPublisher.publishValidCallDeliveryFailureRecord(failedCallReport);
        }
    }
}