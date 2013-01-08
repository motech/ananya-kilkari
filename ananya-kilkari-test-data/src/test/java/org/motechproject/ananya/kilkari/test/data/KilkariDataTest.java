package org.motechproject.ananya.kilkari.test.data;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class KilkariDataTest extends BaseDataSetup{


    @Test
    public void shouldCreateSubscription() {

        int scheduleDeltaDays = kilkariProperties.getCampaignScheduleDeltaDays();
        int deltaMinutes = kilkariProperties.getCampaignScheduleDeltaMinutes();
        DateTime futureDateForFirstCampaignAlertToBeRaised = DateTime.now().plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime futureDateOfSecondCampaignAlert = futureDateForFirstCampaignAlertToBeRaised.plusWeeks(1);
        DateTime futureDateForCampaignAlertToBeRaisedAfterDeactivation = futureDateOfSecondCampaignAlert.plusWeeks(1);

        moveToFutureTime(DateTime.now());
        String msisdn = createSubscriptionForCallCenter();
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn);

        String subscriptionId = subscriptionDetails.getSubscriptionId();

        System.out.println(subscriptionId +" "+msisdn);
        activateSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");

       // System.out.println("C1 "+futureDateForFirstCampaignAlertToBeRaised);
        moveToFutureTime(futureDateForFirstCampaignAlertToBeRaised);
        String currentCampaignId = "WEEK1";
        waitForCampaignAlert(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

       // System.out.println("C2 " + futureDateOfSecondCampaignAlert);
        moveToFutureTime(futureDateOfSecondCampaignAlert);
        renewSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");
        waitForCampaignAlert(subscriptionId,"WEEK2");
    }


}


