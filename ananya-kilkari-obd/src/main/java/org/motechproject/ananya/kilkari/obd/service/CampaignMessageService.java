package org.motechproject.ananya.kilkari.obd.service;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ananya.kilkari.obd.builder.CampaignMessageCSVBuilder;
import org.motechproject.ananya.kilkari.obd.contract.ValidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.gateway.OBDProperties;
import org.motechproject.ananya.kilkari.obd.gateway.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.reporting.domain.CallDetailsReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
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
    public CampaignMessageService(AllCampaignMessages allCampaignMessages, OnMobileOBDGateway onMobileOBDGateway, CampaignMessageCSVBuilder campaignMessageCSVBuilder, ReportingService reportingService, OBDProperties obdProperties) {
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

    public void processValidCallDeliveryFailureRecords(ValidCallDeliveryFailureRecordObject recordObject) {
        CampaignMessage campaignMessage = allCampaignMessages.find(recordObject.getSubscriptionId(), recordObject.getCampaignId());
        if (campaignMessage == null) {
            logger.error(String.format("Campaign Message not present for subscriptionId[%s] and campaignId[%s].",
                    recordObject.getSubscriptionId(), recordObject.getCampaignId()));
            return;
        }
        updateCampaignMessageStatus(campaignMessage, recordObject.getStatusCode());
        reportCampaignMessageStatus(recordObject, campaignMessage);
    }

    private void updateCampaignMessageStatus(CampaignMessage campaignMessage, CampaignMessageStatus statusCode) {
        if (hasReachedMaximumRetries(campaignMessage))
            allCampaignMessages.delete(campaignMessage);
        else {
            campaignMessage.setStatusCode(statusCode);
            allCampaignMessages.update(campaignMessage);
        }
    }

    private boolean hasReachedMaximumRetries(CampaignMessage campaignMessage) {
        return campaignMessage.getDnpRetryCount() == obdProperties.getMaximumDNPRetryCount() ||
                campaignMessage.getDncRetryCount() == obdProperties.getMaximumDNCRetryCount();
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

    private void reportCampaignMessageStatus(ValidCallDeliveryFailureRecordObject recordObject, CampaignMessage campaignMessage) {
        String retryCount = getRetryCount(campaignMessage);
        CallDetailsReportRequest callDetailRecord = new CallDetailsReportRequest(format(recordObject.getCreatedAt()), format(recordObject.getCreatedAt()));
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = new CampaignMessageDeliveryReportRequest(recordObject.getSubscriptionId(), recordObject.getMsisdn(), recordObject.getCampaignId(), null, retryCount, recordObject.getStatusCode().name(), callDetailRecord);

        reportingService.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);
    }

    private String getRetryCount(CampaignMessage campaignMessage) {
        return campaignMessage.getStatus() == CampaignMessageStatus.DNP ? String.valueOf(campaignMessage.getDnpRetryCount())
                : String.valueOf(campaignMessage.getDncRetryCount());
    }

    private String format(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH-mm-ss");
        return formatter.print(dateTime);
    }

    private interface GatewayAction {
        public void send(String content);
    }
}