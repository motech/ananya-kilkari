package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessageStatus;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@Component
public class CampaignMessageVerifier {


    @Autowired
    private AllCampaignMessages allCampaignMessages;


    public void verifyCampaignMessageExists(final SubscriptionData subscriptionData, final String weekMessageId) {
        CampaignMessage campaignMessage = findCampaignMessage(subscriptionData, weekMessageId);

        if (campaignMessage == null)
            throw new RuntimeException(String.format("Campaign Message for subscription id - %s not in OBD db for week %s ", subscriptionData.getSubscriptionId(), weekMessageId));
    }

    public void verifyCampaignMessageDoesNotExists(SubscriptionData subscriptionData, String weekMessageId) {
        CampaignMessage campaignMessage = findCampaignMessage(subscriptionData, weekMessageId);
        assertNull(campaignMessage);
    }


    public void verifyCampaignMessageFailedDueToDNP(SubscriptionData subscriptionData, String campaignId) {
        CampaignMessage campaignMessage = findCampaignMessageWithStatus(subscriptionData, campaignId, CampaignMessageStatus.DNP);
        assertTrue(campaignMessage.hasFailed());
        assertFalse(campaignMessage.isSent());
    }

    private CampaignMessage findCampaignMessageWithStatus(final SubscriptionData subscriptionData,
                                                          final String weekMessageId,
                                                          final CampaignMessageStatus status) {
        return new TimedRunner<CampaignMessage>(120, 1000) {
            public CampaignMessage run() {
                CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionData.getSubscriptionId(), weekMessageId);
                return campaignMessage != null && campaignMessage.getMessageId().equals(weekMessageId)
                        && campaignMessage.getStatus().equals(status) ? campaignMessage : null;
            }
        }.executeWithTimeout();
    }

    public void verifyCampaignMessageFailedDueToDNC(SubscriptionData subscriptionData, String campaignId) {
        CampaignMessage campaignMessage = findCampaignMessageWithStatus(subscriptionData, campaignId, CampaignMessageStatus.DNC);
        assertTrue(campaignMessage.hasFailed());
        assertFalse(campaignMessage.isSent());
    }

    public void verifyCampaignMessageNotReadyToBeDeliveredInFirstSlot(final SubscriptionData subscriptionData, final String weekMessageId) {
        CampaignMessage campaignMessage = findCampaignMessageInFirstSlot(subscriptionData, weekMessageId);
        assertNull("Campaign message found for first slot", campaignMessage);
    }

    private CampaignMessage findCampaignMessageInFirstSlot(final SubscriptionData subscriptionData, final String weekMessageId) {
        List<CampaignMessage> allUnsentNewMessages = allCampaignMessages.getAllUnsentNewMessages();
        for (CampaignMessage unsentNewMessage : allUnsentNewMessages)
        {
            if (subscriptionData.getMsisdn().equals(unsentNewMessage.getMsisdn()) && weekMessageId.equals(unsentNewMessage.getMessageId()))
                return unsentNewMessage;
        }
        return null;
    }

    private CampaignMessage findCampaignMessage(final SubscriptionData subscriptionData, final String weekMessageId) {
        return new TimedRunner<CampaignMessage>(20, 6000) {
            public CampaignMessage run() {
                CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionData.getSubscriptionId(), weekMessageId);
                return campaignMessage != null && campaignMessage.getMessageId().equals(weekMessageId) ? campaignMessage : null;
            }
        }.executeWithTimeout();
    }
}
