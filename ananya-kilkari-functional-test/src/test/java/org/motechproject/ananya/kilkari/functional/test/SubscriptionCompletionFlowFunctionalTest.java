package org.motechproject.ananya.kilkari.functional.test;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.functional.test.utils.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.messagecampaign.utils.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.springframework.beans.factory.annotation.Autowired;

public class SubscriptionCompletionFlowFunctionalTest extends SpringIntegrationTest{

    @Autowired
    private FlowSystem flowSystem;

    @Autowired
    private KilkariPropertiesData kilkariProperties;

    @Test
    public void shouldSubscribeAndProgressAndCompleteSubscriptionSuccessfully() throws Exception {
        int scheduleDeltaDays = kilkariProperties.getCampaignScheduleDeltaDays();
        int deltaMinutes = kilkariProperties.getCampaignScheduleDeltaMinutes();
        DateTime futureDateForFirstCampaignAlertToBeRaised = DateTime.now().plusDays(scheduleDeltaDays).plusHours(1).plusMinutes(deltaMinutes);
        DateTime futureDateOfSecondCampaignAlert = futureDateForFirstCampaignAlertToBeRaised.plusWeeks(1);
        String msisdn = RandomStringUtils.randomNumeric(10);
        SubscriptionData subscriptionData = new SubscriptionData(SubscriptionPack.FIFTEEN_MONTHS, "ivr", msisdn);

        flowSystem.subscribe(subscriptionData).
                activate(subscriptionData).
                moveToFutureTime(futureDateForFirstCampaignAlertToBeRaised).
                verifyCampaignMessageInOBD(subscriptionData, "WEEK1").
                renew(subscriptionData).
                moveToFutureTime(futureDateOfSecondCampaignAlert).
                verifyCampaignMessageInOBD(subscriptionData, "WEEK2");

    }

}
