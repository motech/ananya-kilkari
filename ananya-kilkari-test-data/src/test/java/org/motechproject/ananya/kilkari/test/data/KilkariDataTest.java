package org.motechproject.ananya.kilkari.test.data;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

@Ignore
public class KilkariDataTest extends BaseDataSetup{

    @Ignore
    @Test
    public void shouldCreateSubscription() {

        /*int scheduleDeltaDays = 0;
        int deltaMinutes = 1;
        DateTime futureDateForFirstCampaignAlertToBeRaised = DateTime.now().plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime futureDateOfSecondCampaignAlert = futureDateForFirstCampaignAlertToBeRaised.plusWeeks(1);
        DateTime futureDateForCampaignAlertToBeRaisedAfterDeactivation = futureDateOfSecondCampaignAlert.plusWeeks(1);
*/
        String msisdn = createSubscriptionForCallCenter();
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn);

        System.out.println(subscriptionDetails.getSubscriptionId());
        activateSubscription(msisdn,subscriptionDetails.getSubscriptionId(),"SUCCESS","AIRTEL");

        /*FakeTimeUtils.moveToFutureTime(futureDateForFirstCampaignAlertToBeRaised);
        waitForCampaignAlert(subscriptionDetails.getSubscriptionId(),"WEEK1");

        renewSubscription(msisdn,subscriptionDetails.getSubscriptionId(),"SUCCESS","AIRTEL");
        FakeTimeUtils.moveToFutureTime(futureDateOfSecondCampaignAlert);
        waitForCampaignAlert(subscriptionDetails.getSubscriptionId(),"WEEK2");*/
    }
}


