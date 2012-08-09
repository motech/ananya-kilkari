package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.junit.Test;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunnerResponse;
import org.motechproject.ananya.kilkari.obd.repository.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.repository.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.request.InvalidFailedCallReports;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
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
        assertNotNull(newMessage);
    }

    public void verifyThatRetryMessageWasDelivered(SubscriptionData subscriptionData, String campaignId) {
        OnMobileCampaignMessage retryMessage = onMobileOBDGatewayBehavior.findRetryMessage(subscriptionData, campaignId);
        assertNotNull(retryMessage);
    }

    private static class OnMobileCampaignMessage {
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
            System.out.println("########## new message sent ################");
            newMessagesSent = true;
            String[] lines = content.split("\n");
            for (String line : lines) {
                newCampaignMessages.add(new OnMobileCampaignMessage(line));
            }
        }

        @Override
        public void sendRetryMessages(String content) {
            System.out.println("########## retry message sent ################");
            retryMessagesSent = true;
            String[] lines = content.split("\n");
            for (String line : lines) {
                retryCampaignMessages.add(new OnMobileCampaignMessage(line));
            }
        }


        public void reset() {
            System.out.println("##########  reset called  ################");
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
            return new TimedRunner<OnMobileCampaignMessage>(300, 1000) {
                @Override
                protected TimedRunnerResponse<OnMobileCampaignMessage> run() {
                    if (!newMessagesSent) {
                        System.out.println("new messages sent is not true");
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
                        System.out.println("retry messages sent is not true");
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
