package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.junit.Test;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.request.InvalidFailedCallReports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.junit.Assert.fail;

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
        if (newMessage == OnMobileCampaignMessage.NULL || newMessage == null) {
            fail("new campaign message was not delivered");
        }
    }

    public void verifyThatRetryMessageWasDelivered(SubscriptionData subscriptionData, String campaignId) {
        OnMobileCampaignMessage newMessage = onMobileOBDGatewayBehavior.findRetryMessage(subscriptionData, campaignId);
        if (newMessage == OnMobileCampaignMessage.NULL || newMessage == null) {
            fail("retry campaign message was not delivered");
        }
    }

    private static class OnMobileCampaignMessage {
        private static OnMobileCampaignMessage NULL = new OnMobileCampaignMessage();
        private String campaignId;
        private String subscriptionId;

        private OnMobileCampaignMessage() {

        }

        private OnMobileCampaignMessage(String line) {
            String[] cells = line.split(",");
            campaignId = cells[1];
            subscriptionId = cells[2];
        }
    }

    private class OnMobileOBDGatewayBehavior implements OnMobileOBDGateway {

        private boolean newMessagesSent;
        private boolean retryMessagesSent;
        private ArrayList<OnMobileCampaignMessage> newCampaignMessages = new ArrayList<>();
        private ArrayList<OnMobileCampaignMessage> retryCampaignMessages = new ArrayList<>();

        @Override
        public void sendNewMessages(String content) {
            newMessagesSent = true;
            String[] lines = content.split("\n");
            for (String line : lines) {
                newCampaignMessages.add(new OnMobileCampaignMessage(line));
            }
        }

        @Override
        public void sendRetryMessages(String content) {
            retryMessagesSent = true;
            String[] lines = content.split("\n");
            for (String line : lines) {
                retryCampaignMessages.add(new OnMobileCampaignMessage(line));
            }
        }


        public void reset() {
            newMessagesSent = false;
            retryMessagesSent = false;
            newCampaignMessages = new ArrayList<>();
            retryCampaignMessages = new ArrayList<>();
        }

        @Override
        public void sendInvalidFailureRecord(InvalidFailedCallReports invalidFailedCallReports) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public OnMobileCampaignMessage findNewMessage(final SubscriptionData subscriptionData, final String campaignId) {
            return new TimedRunner<OnMobileCampaignMessage>(120, 1000) {
                @Override
                protected OnMobileCampaignMessage run() {
                    if (!newMessagesSent) {
                        return null;
                    }
                    for (OnMobileCampaignMessage campaignMessage : newCampaignMessages) {
                        if (campaignMessage.campaignId.equals(campaignId) && campaignMessage.subscriptionId.equals(subscriptionData.getSubscriptionId())) {
                            return campaignMessage;
                        }
                    }
                    return OnMobileCampaignMessage.NULL;
                }
            }.executeWithTimeout();
        }

        public OnMobileCampaignMessage findRetryMessage(final SubscriptionData subscriptionData, final String campaignId) {
            return new TimedRunner<OnMobileCampaignMessage>(120, 1000) {
                @Override
                protected OnMobileCampaignMessage run() {
                    if (!retryMessagesSent) {
                        return null;
                    }
                    for (OnMobileCampaignMessage campaignMessage : retryCampaignMessages) {
                        if (campaignMessage.campaignId.equals(campaignId) && campaignMessage.subscriptionId.equals(subscriptionData.getSubscriptionId())) {
                            return campaignMessage;
                        }
                    }
                    return OnMobileCampaignMessage.NULL;
                }
            }.executeWithTimeout();
        }
    }
}
