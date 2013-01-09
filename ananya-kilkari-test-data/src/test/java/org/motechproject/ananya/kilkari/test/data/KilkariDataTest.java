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
    public void shouldCreateCase1() {

        int scheduleDeltaDays = kilkariProperties.getCampaignScheduleDeltaDays();
        int deltaMinutes = kilkariProperties.getCampaignScheduleDeltaMinutes();

        DateTime startDate = DateTime.now();
        DateTime activationDate = startDate.plusDays(2);
        DateTime week1 = activationDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime week2 = week1.plusWeeks(1);
        DateTime week3 = week2.plusWeeks(1);
        DateTime week4 = week3.plusWeeks(1);

        DateTime secondRenew = week4.plusDays(3);

        DateTime week5 = week4.plusWeeks(1);
        DateTime week6 = week5.plusWeeks(1);

        moveToFutureTime(startDate);
        String msisdn = createSubscriptionForCallCenter();
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId +" "+msisdn);

        moveToFutureTime(activationDate);
        activateSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");

        moveToFutureTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week2);
        renewSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week3);
        renewSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");
        currentCampaignId = "WEEK3";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week4);
        renewSubscription(msisdn, subscriptionId, "BAL-LOW", "AIRTEL");

        moveToFutureTime(secondRenew);
        renewSubscription(msisdn, subscriptionId, "SUCCESS", "AIRTEL");
        currentCampaignId = "WEEK4";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week5);
        renewSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");
        currentCampaignId = "WEEK5";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week6);
        renewSubscription(msisdn, subscriptionId,"BAL-LOW","AIRTEL");
        currentCampaignId = "WEEK6";
        waitForCampaignMessageAlert(subscriptionId, currentCampaignId);

        moveToFutureTime(week6.plusDays(10));
        renewSubscription(msisdn, subscriptionId,"SUCCESS","AIRTEL");
        currentCampaignId = "WEEK7";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
    }




}


