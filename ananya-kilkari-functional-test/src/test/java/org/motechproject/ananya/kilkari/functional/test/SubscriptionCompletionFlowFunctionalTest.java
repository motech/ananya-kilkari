package org.motechproject.ananya.kilkari.functional.test;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.functional.test.utils.SubscriptionDataBuilder;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionCompletionFlowFunctionalTest extends SpringIntegrationTest {

    @Autowired
    private FlowSystem flowSystem;

    @Autowired
    private KilkariPropertiesData kilkariProperties;

    @Test
    public void shouldSubscribeAndProgressAndCompleteSubscriptionSuccessfully() throws Exception {
        int scheduleDeltaDays = kilkariProperties.getCampaignScheduleDeltaDays();
        int deltaMinutes = kilkariProperties.getCampaignScheduleDeltaMinutes();
        DateTime futureDateForFirstCampaignAlertToBeRaised = DateTime.now().plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime futureDateOfSecondCampaignAlert = futureDateForFirstCampaignAlertToBeRaised.plusWeeks(1);
        DateTime futureDateOfPackCompletion = futureDateForFirstCampaignAlertToBeRaised.plusWeeks(59);

        SubscriptionData subscriptionData = new SubscriptionDataBuilder().withDefaults().build();

        flowSystem.subscribe(subscriptionData).
                activate(subscriptionData).
                moveToFutureTime(futureDateForFirstCampaignAlertToBeRaised).
                verifyCampaignMessageInOBD(subscriptionData, "WEEK1").
                renew(subscriptionData).
                moveToFutureTime(futureDateOfSecondCampaignAlert).
                verifyCampaignMessageInOBD(subscriptionData, "WEEK2").
                moveToFutureTime(futureDateOfPackCompletion.plusWeeks(1)).
                verifyPackCompletion(subscriptionData);
    }
}
