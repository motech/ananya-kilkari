package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertNull;

@Component
public class CampaignMessageVerifier {


    @Autowired
    private AllCampaignMessages allCampaignMessages;


    public void verifyCampaignMessageExists(final SubscriptionData subscriptionData,final String weekMessageId) {
        CampaignMessage campaignMessage = verifyAllCampaignMessagesForExistence(subscriptionData, weekMessageId);

        if(campaignMessage==null)
            throw new RuntimeException(String.format("Campaign Message for subscription id - %s not in OBD db for week %s ",subscriptionData.getSubscriptionId(),weekMessageId ));
    }

    public void verifyCampaignMessageDoesNotExists(SubscriptionData subscriptionData, String weekMessageId) {
        CampaignMessage campaignMessage = verifyAllCampaignMessagesForExistence(subscriptionData, weekMessageId);
        assertNull(campaignMessage);
    }


    private CampaignMessage verifyAllCampaignMessagesForExistence(final SubscriptionData subscriptionData, final String weekMessageId) {
        return new TimedRunner<CampaignMessage>(20, 6000) {
            public CampaignMessage run() {
                CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionData.getSubscriptionId(),weekMessageId) ;
                return campaignMessage!=null && campaignMessage.getMessageId().equals(weekMessageId)? campaignMessage : null;
            }
        }.executeWithTimeout();
    }
}
