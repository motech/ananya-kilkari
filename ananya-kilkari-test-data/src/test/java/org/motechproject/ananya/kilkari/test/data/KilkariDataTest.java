package org.motechproject.ananya.kilkari.test.data;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class KilkariDataTest extends BaseDataSetup {

    private int scheduleDeltaDays;
    private int deltaMinutes;

    @Before
    public void setUp() {
        scheduleDeltaDays = kilkariProperties.getCampaignScheduleDeltaDays();
        deltaMinutes = kilkariProperties.getCampaignScheduleDeltaMinutes();
    }

    @After
    public void tearDown(){
        moveToFutureTime(DateTime.now());
    }

    @Test
    public void shouldCreateCase1() {
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
        String msisdn = createSubscriptionForIVR();
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToFutureTime(activationDate);
        activateSubscription(msisdn, subscriptionId, "AIRTEL");

        moveToFutureTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week2);
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week3);
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK3";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week4);
        suspendSubscription(msisdn, subscriptionId, "AIRTEL");

        moveToFutureTime(secondRenew);
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK4";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week5);
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK5";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week6);
        suspendSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK6";
        waitForCampaignMessageAlert(subscriptionId, currentCampaignId);

        moveToFutureTime(week6.plusDays(10));
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK7";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
    }

    @Test
    public void createCase2() {
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
        String msisdn = createSubscriptionForIVR();
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToFutureTime(activationDate);
        activateSubscription(msisdn, subscriptionId, "AIRTEL");

        moveToFutureTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week2);
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week3);
        suspendSubscription(msisdn, subscriptionId, "AIRTEL");

        moveToFutureTime(week3.plusDays(2));
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK3";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week4);
        renewSubscription(msisdn, subscriptionId, "AIRTEL");
        currentCampaignId = "WEEK4";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week5);
        suspendSubscription(msisdn, subscriptionId, "AIRTEL");

        moveToFutureTime(week5.plusDays(20));
        deactivateSubscription(msisdn, subscriptionId, "AIRTEL");
    }

    @Test
    public void createCase3() {
        DateTime startDate = DateTime.now();

        DateTime dateOfBirth = startDate.minusWeeks(44).plusDays(1);
        System.out.println("dateOfBirth "+dateOfBirth);


        DateTime week61 = startDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1).plusDays(1);
        DateTime week62 = week61.plusWeeks(1);
        DateTime week63 = week62.plusWeeks(1);
        DateTime week64 = week63.plusWeeks(1);

        moveToFutureTime(startDate);

        String msisdn = createSubscriptionForCallCenter("nanhi_kilkari", dateOfBirth.toString("dd-MM-yyyy"), null);
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        String operator = Operator.BSNL.name();

        activateSubscription(msisdn, subscriptionId, operator);

        moveToFutureTime(week61);
        String currentCampaignId = "WEEK61";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week62);
        suspendSubscription(msisdn, subscriptionId, operator);

        moveToFutureTime(week62.plusDays(1));
        renewSubscription(msisdn,subscriptionId, operator);
        currentCampaignId = "WEEK62";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week63);
        renewSubscription(msisdn,subscriptionId, operator);
        currentCampaignId = "WEEK63";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToFutureTime(week64);
        renewSubscription(msisdn,subscriptionId, operator);
        currentCampaignId = "WEEK64";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
        waitForSubscription(msisdn, SubscriptionStatus.PENDING_COMPLETION.getDisplayString());
        completeSubscription(msisdn, subscriptionId, operator);
    }

    @Test
    public void createCase4() {

    }
}


