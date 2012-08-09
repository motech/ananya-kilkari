package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.contract.request.CallDetailRecordRequest;
import org.motechproject.ananya.kilkari.contract.request.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.obd.builder.CampaignMessageCSVBuilder;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageCallSource;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
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

    public void sendNewMessages() {
        List<CampaignMessage> allNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        GatewayAction gatewayAction = new GatewayAction() {
            @Override
            public void send(String content) {
                onMobileOBDGateway.sendNewMessages(content);
            }
        };
        sendMessagesToOBD(allNewMessages, gatewayAction);
    }

    public void sendRetryMessages() {
        List<CampaignMessage> allRetryMessages = allCampaignMessages.getAllUnsentRetryMessages();
        GatewayAction gatewayAction = new GatewayAction() {
            @Override
            public void send(String content) {
                onMobileOBDGateway.sendRetryMessages(content);
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

    public CampaignMessageStatus getCampaignMessageStatusFor(String statusCode){
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
        return (campaignMessage.getDnpRetryCount() == obdProperties.getMaximumDNPRetryCount() && statusCode == CampaignMessageStatus.DNP) ||
                (campaignMessage.getDncRetryCount() == obdProperties.getMaximumDNCRetryCount() && statusCode == CampaignMessageStatus.DNC);
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
        String retryCount = getRetryCount(campaignMessage);
        CallDetailRecordRequest callDetailRecordRequest = new CallDetailRecordRequest(failedCallReport.getCreatedAt(), failedCallReport.getCreatedAt());
        CallDetailsReportRequest callDetailsReportRequest = new CallDetailsReportRequest(failedCallReport.getSubscriptionId(), failedCallReport.getMsisdn(), failedCallReport.getCampaignId(),
                null, retryCount, failedCallReport.getStatusCode().name(), callDetailRecordRequest, CampaignMessageCallSource.OBD.name());
        reportingService.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    private String getRetryCount(CampaignMessage campaignMessage) {
        return campaignMessage.getStatus() == CampaignMessageStatus.DNP ? String.valueOf(campaignMessage.getDnpRetryCount())
                : String.valueOf(campaignMessage.getDncRetryCount());
    }

    private interface GatewayAction {
        public void send(String content);
    }
}