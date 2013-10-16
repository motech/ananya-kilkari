package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunnerResponse;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.domain.OBDSubSlot;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@Component
public class OnMobileOBDVerifier {


    private OnMobileOBDGatewayBehavior onMobileOBDGatewayBehavior;
    private StubOnMobileOBDGateway stubOnMobileOBDGateway;

    @Autowired
    public OnMobileOBDVerifier(StubOnMobileOBDGateway stubOnMobileOBDGateway) {
        this.stubOnMobileOBDGateway = stubOnMobileOBDGateway;

        onMobileOBDGatewayBehavior = new OnMobileOBDGatewayBehavior();
        this.stubOnMobileOBDGateway.setBehavior(onMobileOBDGatewayBehavior);
    }


    public void reset() {
        onMobileOBDGatewayBehavior.reset();
    }

    public void verifyThatNewMessageWasDelivered(SubscriptionData subscriptionData, String campaignId) {
        OnMobileCampaignMessage newMessage = onMobileOBDGatewayBehavior.findNewMessage(subscriptionData, campaignId);
        assertNotNull(newMessage);
    }

    public void verifyThatRetryMessageWasDelivered(SubscriptionData subscriptionData, String campaignId) {
        OnMobileCampaignMessage retryMessage = onMobileOBDGatewayBehavior.findRetryMessage(subscriptionData, campaignId);
        assertNotNull(retryMessage);
    }

    private static class OnMobileCampaignMessage {
        private String campaignId;
        private String subscriptionId;

        private OnMobileCampaignMessage(String line) {
            String[] cells = line.split(",");
            campaignId = cells[1];
            subscriptionId = cells[2];
        }
    }

    private class OnMobileOBDGatewayBehavior implements OnMobileOBDGateway {

        private Boolean newMessagesSent;
        private Boolean retryMessagesSent;
        private ArrayList<OnMobileCampaignMessage> newCampaignMessages = new ArrayList<>();
        private ArrayList<OnMobileCampaignMessage> retryCampaignMessages = new ArrayList<>();

        public void reset() {
            newMessagesSent = false;
            retryMessagesSent = false;
            newCampaignMessages = new ArrayList<>();
            retryCampaignMessages = new ArrayList<>();
        }

        @Override
        public void sendMessages(String content, OBDSubSlot subSlot) {
            String[] lines = content.split("\n");
            boolean isRetrySlot = subSlot.getSlotName().contains("RETRY");
            List<OnMobileCampaignMessage> campaignMessages = isRetrySlot ? retryCampaignMessages : newCampaignMessages;
            Boolean messageSent = isRetrySlot ? retryMessagesSent : newMessagesSent;
            for (String line : lines) {
                campaignMessages.add(new OnMobileCampaignMessage(line));
            }
            messageSent = true;
        }

        @Override
        public void sendInvalidFailureRecord(InvalidFailedCallReports invalidFailedCallReports) {
        }

        public OnMobileCampaignMessage findNewMessage(final SubscriptionData subscriptionData, final String campaignId) {
            return new TimedRunner<OnMobileCampaignMessage>(300, 1000) {
                @Override
                protected TimedRunnerResponse<OnMobileCampaignMessage> run() {
                    if (!newMessagesSent) {
                        return null;
                    }
                    for (OnMobileCampaignMessage campaignMessage : newCampaignMessages) {
                        if (campaignMessage.campaignId.equals(campaignId) && campaignMessage.subscriptionId.equals(subscriptionData.getSubscriptionId())) {
                            return new TimedRunnerResponse<>(campaignMessage);
                        }
                    }
                    return TimedRunnerResponse.EMPTY;
                }
            }.executeWithTimeout();
        }

        public OnMobileCampaignMessage findRetryMessage(final SubscriptionData subscriptionData, final String campaignId) {
            return new TimedRunner<OnMobileCampaignMessage>(300, 1000) {
                @Override
                protected TimedRunnerResponse<OnMobileCampaignMessage> run() {
                    if (!retryMessagesSent) {
                        return null;
                    }
                    for (OnMobileCampaignMessage campaignMessage : retryCampaignMessages) {
                        if (campaignMessage.campaignId.equals(campaignId) && campaignMessage.subscriptionId.equals(subscriptionData.getSubscriptionId())) {
                            return new TimedRunnerResponse<>(campaignMessage);
                        }
                    }
                    return TimedRunnerResponse.EMPTY;
                }
            }.executeWithTimeout();
        }
    }
}
