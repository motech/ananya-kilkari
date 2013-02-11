package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.scheduler.MainSubSlot;
import org.motechproject.ananya.kilkari.obd.scheduler.OBDSubSlot;
import org.motechproject.ananya.kilkari.obd.service.utils.RetryTask;
import org.motechproject.ananya.kilkari.obd.service.utils.RetryTaskExecutor;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailRecordRequest;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignMessageService {

    private AllCampaignMessages allCampaignMessages;
    private OnMobileOBDGateway onMobileOBDGateway;
    private CampaignMessageCSVBuilder campaignMessageCSVBuilder;
    private final ReportingService reportingService;
    private final OBDProperties obdProperties;
    private RetryTaskExecutor retryTaskExecutor;

    private static final Logger logger = LoggerFactory.getLogger(CampaignMessageService.class);

    @Autowired
    public CampaignMessageService(AllCampaignMessages allCampaignMessages, OnMobileOBDGateway onMobileOBDGateway,
                                  CampaignMessageCSVBuilder campaignMessageCSVBuilder, ReportingService reportingService,
                                  OBDProperties obdProperties, RetryTaskExecutor retryTaskExecutor) {
        this.allCampaignMessages = allCampaignMessages;
        this.onMobileOBDGateway = onMobileOBDGateway;
        this.campaignMessageCSVBuilder = campaignMessageCSVBuilder;
        this.reportingService = reportingService;
        this.obdProperties = obdProperties;
        this.retryTaskExecutor = retryTaskExecutor;
    }

    public void scheduleCampaignMessage(String subscriptionId, String messageId, String msisdn, String operator, DateTime messageExpiryDate, DateTime creationDate) {
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, creationDate, msisdn, operator, messageExpiryDate));
    }

    public void sendFirstMainSubSlotMessages(final OBDSubSlot subSlot) {
        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        int numberOfMessagesToSend = (int) Math.ceil(allNewMessages.size() * obdProperties.getSlotMessagePercentageFor(subSlot) / 100.0);
        List<CampaignMessage> messagesToSend = allNewMessages.subList(0, numberOfMessagesToSend);
        sendMessagesToOBD(messagesToSend, subSlot);
    }

    public void sendSecondMainSubSlotMessages(final OBDSubSlot subSlot) {
        List<CampaignMessage> messagesToSend = allCampaignMessages.getAllUnsentRetryMessages();
        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        messagesToSend.addAll(getNewMessagesToSend(subSlot, allNewMessages));
        sendMessagesToOBD(messagesToSend, subSlot);
    }

    private List<CampaignMessage> getNewMessagesToSend(OBDSubSlot subSlot, List<CampaignMessage> allNewMessages) {
        double ratioOfNewMessagesToSend = obdProperties.getSlotMessagePercentageFor(subSlot) / (100.0 - obdProperties.getSlotMessagePercentageFor(MainSubSlot.ONE));
        int numberOfMessagesToSend = (int) Math.ceil(allNewMessages.size() * ratioOfNewMessagesToSend);
        return allNewMessages.subList(0, numberOfMessagesToSend);
    }

    public void sendThirdMainSubSlotMessages(final OBDSubSlot subSlot) {
        List<CampaignMessage> allNewAndNAMessages = allCampaignMessages.getAllUnsentNewAndNAMessages();
        sendMessagesToOBD(allNewAndNAMessages, subSlot);
    }

    public void sendRetrySlotMessages(final OBDSubSlot subSlot) {
        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentNAMessages();
        sendMessagesToOBD(allRetryMessages, subSlot);
    }

    public void deleteCampaignMessageIfExists(String subscriptionId, String messageId) {
        CampaignMessage campaignMessage = find(subscriptionId, messageId);
        if (campaignMessage != null)
            deleteCampaignMessage(campaignMessage);
    }

    public CampaignMessage find(String subscriptionId, String messageId) {
        return allCampaignMessages.find(subscriptionId, messageId);
    }

    public void deleteCampaignMessage(CampaignMessage campaignMessage) {
        allCampaignMessages.delete(campaignMessage);
    }

    public void update(CampaignMessage campaignMessage) {
        allCampaignMessages.update(campaignMessage);
    }

    public void processValidCallDeliveryFailureRecords(ValidFailedCallReport failedCallReport) {
        CampaignMessage campaignMessage = allCampaignMessages.find(failedCallReport.getSubscriptionId(), failedCallReport.getCampaignId());
        if (campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    failedCallReport.getSubscriptionId(), failedCallReport.getCampaignId()));
            return;
        }
        updateCampaignMessageStatus(campaignMessage, failedCallReport.getStatusCode());
        reportCampaignMessageStatus(failedCallReport);
    }

    public void deleteCampaignMessagesFor(String subscriptionId) {
        allCampaignMessages.removeAll(subscriptionId);
    }

    public CampaignMessageStatus getCampaignMessageStatusFor(String statusCode) {
        return obdProperties.getCampaignMessageStatusFor(statusCode);
    }

    private void updateCampaignMessageStatus(CampaignMessage campaignMessage, CampaignMessageStatus statusCode) {
        if (hasReachedMaximumRetryDays(campaignMessage))
            allCampaignMessages.delete(campaignMessage);
        else {
            campaignMessage.setStatusCode(statusCode);
            allCampaignMessages.update(campaignMessage);
        }
    }

    private boolean hasReachedMaximumRetryDays(CampaignMessage campaignMessage) {
        DateTime now = DateTime.now();
        return now.isAfter(minimum(campaignMessage.getCreationDate().plusDays(obdProperties.getMaximumOBDRetryDays()), campaignMessage.getWeekEndingDate()));
    }

    private DateTime minimum(DateTime obdRetryEndDate, DateTime weekEndingDate) {
        return obdRetryEndDate.isBefore(weekEndingDate) ? obdRetryEndDate : weekEndingDate;
    }

    private void sendMessagesToOBD(List<CampaignMessage> messages, OBDSubSlot subSlot) {
        logger.info(String.format("Sending %s campaign messages to obd", messages.size()));
        if (messages.isEmpty())
            return;
        String campaignMessageCSVContent = campaignMessageCSVBuilder.getCSV(messages);
        onMobileOBDGateway.sendMessages(campaignMessageCSVContent, subSlot);

        for (final CampaignMessage message : messages) {
            try {
                message.markSent();
                allCampaignMessages.update(message);
            } catch (Exception ex) {
                logger.error(String.format("Error when updating the sent message %s for subscription %s", message.getMessageId(), message.getSubscriptionId()));
                retryTaskExecutor.run(5, 5, 3, new RetryTask() {
                    @Override
                    public boolean execute() {
                        allCampaignMessages.update(message);
                        return true;
                    }
                });
            }
        }
    }

    private void reportCampaignMessageStatus(ValidFailedCallReport failedCallReport) {
        CallDetailRecordRequest callDetailRecordRequest = new CallDetailRecordRequest(failedCallReport.getCreatedAt(), failedCallReport.getCreatedAt());
        CallDetailsReportRequest callDetailsReportRequest = new CallDetailsReportRequest(failedCallReport.getSubscriptionId(), failedCallReport.getMsisdn(), failedCallReport.getCampaignId(),
                null, failedCallReport.getStatusCode().name(), callDetailRecordRequest, CampaignMessageCallSource.OBD.name());
        reportingService.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    private interface GatewayAction {
        public void send(String content);
    }
}