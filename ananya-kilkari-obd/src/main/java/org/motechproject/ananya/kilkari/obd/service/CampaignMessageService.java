package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.scheduler.SubSlot;
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

    private static final Logger logger = LoggerFactory.getLogger(CampaignMessageService.class);

    @Autowired
    public CampaignMessageService(AllCampaignMessages allCampaignMessages, OnMobileOBDGateway onMobileOBDGateway,
                                  CampaignMessageCSVBuilder campaignMessageCSVBuilder, ReportingService reportingService,
                                  OBDProperties obdProperties) {
        this.allCampaignMessages = allCampaignMessages;
        this.onMobileOBDGateway = onMobileOBDGateway;
        this.campaignMessageCSVBuilder = campaignMessageCSVBuilder;
        this.reportingService = reportingService;
        this.obdProperties = obdProperties;
    }

    public void scheduleCampaignMessage(String subscriptionId, String messageId, String msisdn, String operator, DateTime messageExpiryDate) {
        allCampaignMessages.add(new CampaignMessage(subscriptionId, messageId, msisdn, operator, messageExpiryDate));
    }

    public void sendFirstMainSubSlotMessages(final SubSlot subSlot) {
        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        int numberOfMessagesToSend = (int) Math.ceil(allNewMessages.size() * obdProperties.getMainSlotMessagePercentageFor(subSlot) / 100.0);
        List<CampaignMessage> messagesToSend = allNewMessages.subList(0, numberOfMessagesToSend);
        sendMainSlotMessages(subSlot, messagesToSend);
    }

    public void sendSecondMainSubSlotMessages(final SubSlot subSlot) {
        List<CampaignMessage> messagesToSend = allCampaignMessages.getAllUnsentRetryMessages();
        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        messagesToSend.addAll(getNewMessagesToSend(subSlot, allNewMessages));
        sendMainSlotMessages(subSlot, messagesToSend);
    }

    private List<CampaignMessage> getNewMessagesToSend(SubSlot subSlot, List<CampaignMessage> allNewMessages) {
        double ratioOfNewMessagesToSend = obdProperties.getMainSlotMessagePercentageFor(subSlot) / (100.0 - obdProperties.getMainSlotMessagePercentageFor(SubSlot.ONE));
        int numberOfMessagesToSend = (int) Math.ceil(allNewMessages.size() * ratioOfNewMessagesToSend);
        return allNewMessages.subList(0, numberOfMessagesToSend);
    }

    public void sendThirdMainSubSlotMessages(final SubSlot subSlot) {
        List<CampaignMessage> allNewAndNAMessages = allCampaignMessages.getAllUnsentNewAndNAMessages();
        sendMainSlotMessages(subSlot, allNewAndNAMessages);
    }

    private void sendMainSlotMessages(final SubSlot subSlot, List<CampaignMessage> messagesToSend) {
        GatewayAction gatewayAction = new GatewayAction() {
            @Override
            public void send(String content) {
                onMobileOBDGateway.sendMainSlotMessages(content, subSlot);
            }
        };
        sendMessagesToOBD(messagesToSend, gatewayAction);
    }

    public void sendRetrySlotMessages(final SubSlot subSlot) {
        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentNAMessages();
        GatewayAction gatewayAction = new GatewayAction() {
            @Override
            public void send(String content) {
                onMobileOBDGateway.sendRetrySlotMessages(content, subSlot);
            }
        };
        sendMessagesToOBD(allRetryMessages, gatewayAction);
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
        reportCampaignMessageStatus(failedCallReport, campaignMessage);
    }

    public void deleteCampaignMessagesFor(String subscriptionId) {
        allCampaignMessages.removeAll(subscriptionId);
    }

    public CampaignMessageStatus getCampaignMessageStatusFor(String statusCode) {
        return obdProperties.getCampaignMessageStatusFor(statusCode);
    }

    private void updateCampaignMessageStatus(CampaignMessage campaignMessage, CampaignMessageStatus statusCode) {
        if (hasReachedMaximumRetries(campaignMessage, statusCode))
            allCampaignMessages.delete(campaignMessage);
        else {
            campaignMessage.setStatusCode(statusCode);
            allCampaignMessages.update(campaignMessage);
        }
    }

    private boolean hasReachedMaximumRetries(CampaignMessage campaignMessage, CampaignMessageStatus statusCode) {
        return (campaignMessage.getNARetryCount() == obdProperties.getMaximumDNPRetryCount() && statusCode == CampaignMessageStatus.NA) ||
                (campaignMessage.getNDRetryCount() == obdProperties.getMaximumDNCRetryCount() && statusCode == CampaignMessageStatus.ND);
    }

    private void sendMessagesToOBD(List<CampaignMessage> messages, GatewayAction gatewayAction) {
        logger.info(String.format("Sending %s campaign messages to obd", messages.size()));
        if (messages.isEmpty())
            return;
        String campaignMessageCSVContent = campaignMessageCSVBuilder.getCSV(messages);
        gatewayAction.send(campaignMessageCSVContent);
        for (CampaignMessage message : messages) {
            message.markSent();
            allCampaignMessages.update(message);
        }
    }

    private void reportCampaignMessageStatus(ValidFailedCallReport failedCallReport, CampaignMessage campaignMessage) {
        String retryCount = String.valueOf(campaignMessage.getRetryCountForCurrentStatus());
        CallDetailRecordRequest callDetailRecordRequest = new CallDetailRecordRequest(failedCallReport.getCreatedAt(), failedCallReport.getCreatedAt());
        CallDetailsReportRequest callDetailsReportRequest = new CallDetailsReportRequest(failedCallReport.getSubscriptionId(), failedCallReport.getMsisdn(), failedCallReport.getCampaignId(),
                null, retryCount, failedCallReport.getStatusCode().name(), callDetailRecordRequest, CampaignMessageCallSource.OBD.name());
        reportingService.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    private interface GatewayAction {
        public void send(String content);
    }
}