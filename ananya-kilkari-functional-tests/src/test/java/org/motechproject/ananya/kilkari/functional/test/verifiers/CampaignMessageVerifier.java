package org.motechproject.ananya.kilkari.functional.test.verifiers;

import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.TimedRunner;
import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CampaignMessageVerifier {


    @Autowired
    private AllCampaignMessages allCampaignMessages;


    public void verifyCampaignMessageExists(final SubscriptionData subscriptionData,final String weekMessageId) {
        CampaignMessage campaignMessage = new TimedRunner<CampaignMessage>(50, 6000) {
            public CampaignMessage run() {
                CampaignMessage campaignMessage = allCampaignMessages.find(subscriptionData.getSubscriptionId(),weekMessageId) ;
                return campaignMessage!=null && campaignMessage.getMessageId().equals(weekMessageId)? campaignMessage : null;
            }
        }.executeWithTimeout();

        if(campaignMessage==null)
            throw new RuntimeException(String.format("Campaign Message for subscription id - %s not in OBD db for week %s ",subscriptionData.getSubscriptionId(),weekMessageId ));
    }


}
