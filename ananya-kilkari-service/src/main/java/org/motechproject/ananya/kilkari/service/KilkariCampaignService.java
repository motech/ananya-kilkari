package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.mapper.CallDetailsReportRequestMapper;
import org.motechproject.ananya.kilkari.mapper.CallDetailsRequestMapper;
import org.motechproject.ananya.kilkari.message.service.CampaignMessageAlertService;
import org.motechproject.ananya.kilkari.message.service.InboxService;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.CampaignMessageService;
import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.validator.CallDetailsRequestValidator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.utils.CampaignMessageIdStrategy;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KilkariCampaignService {

    private MessageCampaignService messageCampaignService;
    private KilkariSubscriptionService kilkariSubscriptionService;
    private CampaignMessageAlertService campaignMessageAlertService;
    private CampaignMessageService campaignMessageService;
    private ReportingService reportingService;
    private CallDetailsRequestPublisher callDetailsRequestPublisher;
    private InboxService inboxService;
    private OBDServiceOptionFactory obdServiceOptionFactory;
    private CallDetailsRequestValidator callDetailsRequestValidator;
    private OBDService obdService;

    private final Logger logger = LoggerFactory.getLogger(KilkariCampaignService.class);

    KilkariCampaignService() {
    }

    @Autowired
    public KilkariCampaignService(MessageCampaignService messageCampaignService,
                                  KilkariSubscriptionService kilkariSubscriptionService,
                                  CampaignMessageAlertService campaignMessageAlertService,
                                  CampaignMessageService campaignMessageService,
                                  ReportingService reportingService,
                                  CallDetailsRequestPublisher callDetailsRequestPublisher,
                                  InboxService inboxService,
                                  OBDServiceOptionFactory obdServiceOptionFactory,
                                  CallDetailsRequestValidator callDetailsRequestValidator,
                                  OBDService obdService) {
        this.messageCampaignService = messageCampaignService;
        this.kilkariSubscriptionService = kilkariSubscriptionService;
        this.campaignMessageAlertService = campaignMessageAlertService;
        this.campaignMessageService = campaignMessageService;
        this.reportingService = reportingService;
        this.callDetailsRequestPublisher = callDetailsRequestPublisher;
        this.inboxService = inboxService;
        this.obdServiceOptionFactory = obdServiceOptionFactory;
        this.callDetailsRequestValidator = callDetailsRequestValidator;
        this.obdService = obdService;
    }

    public Map<String, List<DateTime>> getMessageTimings(String msisdn) {
        List<Subscription> subscriptionList = kilkariSubscriptionService.findByMsisdn(msisdn);
        Map<String, List<DateTime>> campaignMessageMap = new HashMap<>();
        for (Subscription subscription : subscriptionList) {
            String subscriptionId = subscription.getSubscriptionId();

            try {
                List<DateTime> messageTimings = messageCampaignService.getMessageTimings(
                        subscriptionId, subscription.getCreationDate(), subscription.endDate());
                campaignMessageMap.put(subscriptionId, messageTimings);
            } catch (NullPointerException ne) {
                //ignore
            }
        }
        return campaignMessageMap;
    }

    public void scheduleWeeklyMessage(String subscriptionId, String campaignName) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);

        final String messageId = new CampaignMessageIdStrategy().createMessageId(campaignName, messageCampaignService.getCampaignStartDate(subscriptionId, campaignName), subscription.getPack());
        final DateTime messageExpiryDate = subscription.getCurrentWeeksMessageExpiryDate();

        campaignMessageAlertService.scheduleCampaignMessageAlert(subscriptionId, messageId, messageExpiryDate, subscription.getMsisdn(), subscription.getOperator().name());

        if (subscription.hasBeenActivated())
            inboxService.newMessage(subscriptionId, messageId);
    }

    public void activateSchedule(String subscriptionId) {
        logger.info(String.format("Processing activation for subscriptionId: %s", subscriptionId));

        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);

        String currentMessageId = campaignMessageAlertService.scheduleCampaignMessageAlertForActivation(subscriptionId, subscription.getMsisdn(), subscription.getOperator().name());

        if (currentMessageId != null)
            inboxService.newMessage(subscriptionId, currentMessageId);
    }

    public void renewSchedule(String subscriptionId) {
        logger.info(String.format("Processing activation for subscriptionId: %s", subscriptionId));
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        campaignMessageAlertService.scheduleCampaignMessageAlertForRenewal(subscriptionId, subscription.getMsisdn(), subscription.getOperator().name());
    }

    public void processCampaignCompletion(String subscriptionId) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(subscriptionId);
        if (!subscription.isInDeactivatedState())
            kilkariSubscriptionService.processSubscriptionCompletion(subscription);
    }

    public void processSuccessfulMessageDelivery(OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsWebRequest) {
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = validateSuccessfulCallRequest(obdSuccessfulCallDetailsWebRequest);

        CampaignMessage campaignMessage = campaignMessageService.find(obdSuccessfulCallDetailsRequest.getSubscriptionId(), obdSuccessfulCallDetailsRequest.getCampaignId());
        if (campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId: %s, campaignId: %s",
                    obdSuccessfulCallDetailsRequest.getSubscriptionId(), obdSuccessfulCallDetailsRequest.getCampaignId()));
            return;
        }
        int retryCount = campaignMessage.getDnpRetryCount();

        reportingService.reportCampaignMessageDeliveryStatus(CallDetailsReportRequestMapper.mapFrom(obdSuccessfulCallDetailsRequest, retryCount));
        campaignMessageService.deleteCampaignMessage(campaignMessage);

        ServiceOptionHandler serviceOptionHandler = obdServiceOptionFactory.getHandler(obdSuccessfulCallDetailsRequest.getServiceOption());
        if (serviceOptionHandler != null) {
            serviceOptionHandler.process(obdSuccessfulCallDetailsRequest);
        }
    }

    public void processInboxCallDetailsRequest(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        Errors errors = inboxCallDetailsWebRequest.validate();
        if (errors.hasErrors())
            throw new ValidationException(String.format("Invalid inbox call details request: %s", errors.allMessages()));
        validateSubscription(inboxCallDetailsWebRequest);
        CallDetailsReportRequest callDetailsReportRequest = CallDetailsReportRequestMapper.mapFrom(inboxCallDetailsWebRequest);
        reportingService.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    private void validateSubscription(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        Subscription subscription = kilkariSubscriptionService.findBySubscriptionId(inboxCallDetailsWebRequest.getSubscriptionId());
        if (subscription == null)
            throw new ValidationException("Invalid inbox call details request: Subscription not found");
    }

    public void publishInvalidCallRecordsRequest(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        callDetailsRequestPublisher.publishInvalidCallRecordsRequest(invalidOBDRequestEntries);
    }

    public void publishInboxCallDetailsRequest(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        callDetailsRequestPublisher.publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }

    public void publishSuccessfulCallRequest(OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest) {
        callDetailsRequestPublisher.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);
    }

    public void processCallDeliveryFailureRequest(FailedCallReports failedCallReports) {
        obdService.processCallDeliveryFailure(failedCallReports);
    }

    private OBDSuccessfulCallDetailsRequest validateSuccessfulCallRequest(OBDSuccessfulCallDetailsWebRequest webRequest) {
        Errors validationErrors = webRequest.validate();
        if (validationErrors.hasErrors()) {
            throw new ValidationException(String.format("OBD Request Invalid: %s", validationErrors.allMessages()));
        }

        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new CallDetailsRequestMapper().mapOBDRequest(webRequest);
        validateSubscription(obdSuccessfulCallDetailsRequest);
        return obdSuccessfulCallDetailsRequest;
    }

    private void validateSubscription(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        Errors validationErrors;
        validationErrors = callDetailsRequestValidator.validate(obdSuccessfulCallDetailsRequest);
        if (validationErrors.hasErrors()) {
            throw new ValidationException(String.format("OBD Request Invalid: %s", validationErrors.allMessages()));
        }
    }
}