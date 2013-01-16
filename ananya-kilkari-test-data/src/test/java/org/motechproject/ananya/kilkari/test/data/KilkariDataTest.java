package org.motechproject.ananya.kilkari.test.data;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
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
        moveToTime(DateTime.now());
    }

    @Test
    public void shouldCreateDeactivatedSubscription() {
        DateTime startDate = DateTime.now();
        String operator = getRandomOperator();
        DateTime activationDate = startDate.plusDays(2);
        DateTime week1 = activationDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime week2 = week1.plusWeeks(1);
        DateTime week3 = week2.plusWeeks(1);
        DateTime week4 = week3.plusWeeks(1);
        DateTime week5 = week4.plusWeeks(1);
        DateTime week6 = week5.plusWeeks(1);

        moveToTime(startDate);
        String msisdn = createSubscriptionForIVR(SubscriptionPack.BARI_KILKARI.name());
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToTime(activationDate);
        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week2);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week3);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK3";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week4);
        suspendSubscription(msisdn, subscriptionId, operator);

        moveToTime(week4.plusDays(3));
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK4";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week5);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK5";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week6);
        suspendSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK6";
        waitForCampaignMessageAlert(subscriptionId, currentCampaignId);

        moveToTime(week6.plusDays(10));
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK7";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(DateTime.now());
    }

    @Test
    public void shouldCreateDeactivatedSubscriptionDueToSuspension() {
        DateTime startDate = DateTime.now();
        DateTime activationDate = startDate.plusDays(2);
        DateTime week1 = activationDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime week2 = week1.plusWeeks(1);
        DateTime week3 = week2.plusWeeks(1);
        DateTime week4 = week3.plusWeeks(1);
        DateTime week5 = week4.plusWeeks(1);

        moveToTime(startDate);
        String msisdn = createSubscriptionForIVR(SubscriptionPack.BARI_KILKARI.name());
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToTime(activationDate);
        String operator = getRandomOperator();

        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week2);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week3);
        suspendSubscription(msisdn, subscriptionId, operator);

        moveToTime(week3.plusDays(2));
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK3";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week4);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK4";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week5);
        suspendSubscription(msisdn, subscriptionId, operator);

        moveToTime(week5.plusDays(20));
        deactivateSubscription(msisdn, subscriptionId, operator);

        moveToTime(DateTime.now());
    }

    @Test
    public void shouldCreateSubscriptionWhichGoesToCompletion() {
        DateTime startDate = DateTime.now();

        DateTime dateOfBirth = startDate.minusWeeks(44).plusDays(1);
        System.out.println("dateOfBirth "+dateOfBirth);


        DateTime week61 = startDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1).plusDays(1);
        DateTime week62 = week61.plusWeeks(1);
        DateTime week63 = week62.plusWeeks(1);
        DateTime week64 = week63.plusWeeks(1);

        moveToTime(startDate);

        String msisdn = createSubscriptionForCallCenter("nanhi_kilkari", dateOfBirth.toString("dd-MM-yyyy"), null);
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        String operator = getRandomOperator();

        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week61);
        String currentCampaignId = "WEEK61";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week62);
        suspendSubscription(msisdn, subscriptionId, operator);

        moveToTime(week62.plusDays(1));
        renewSubscription(msisdn,subscriptionId, operator);
        currentCampaignId = "WEEK62";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week63);
        renewSubscription(msisdn,subscriptionId, operator);
        currentCampaignId = "WEEK63";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week64);
        renewSubscription(msisdn,subscriptionId, operator);
        currentCampaignId = "WEEK64";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
        waitForSubscription(msisdn, subscriptionId, SubscriptionStatus.PENDING_COMPLETION.getDisplayString());
        completeSubscription(msisdn, subscriptionId, operator);

        moveToTime(DateTime.now());
    }

    @Test
    public void shouldCreateSubscriptionWithNewEarly() {
        DateTime startDate = DateTime.now().minusWeeks(1).plusDays(1);
        String operator = getRandomOperator();

        DateTime edd = startDate.plusWeeks(1);
        System.out.println("edd "+edd);

        DateTime week17 = startDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1).plusWeeks(1).plusDays(1);
        DateTime week18 = week17.plusWeeks(1);
        DateTime week19 = week18.plusWeeks(1);

        moveToTime(startDate);

        String msisdn = createSubscriptionForCallCenter(SubscriptionPack.NAVJAAT_KILKARI.name(), null, edd.toString("dd-MM-yyyy"));
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToTime(edd.plusDays(1));
        waitForSubscription(msisdn, subscriptionId, SubscriptionStatus.PENDING_ACTIVATION.getDisplayString());
        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week17);
        String currentCampaignId = "WEEK17";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week18);
        renewSubscription(msisdn,subscriptionId,operator);
        currentCampaignId = "WEEK18";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week19);
        suspendSubscription(msisdn,subscriptionId,operator);
        moveToTime(week19.plusDays(2));
        renewSubscription(msisdn,subscriptionId,operator);
        currentCampaignId = "WEEK19";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
    }

    @Test
    public void shouldCreateSubscriptionWithScheduleChange(){
        DateTime startDate = DateTime.now();
        String operator = getRandomOperator();
        String pack = SubscriptionPack.BARI_KILKARI.name();

        DateTime activationDate = startDate.plusDays(2);
        DateTime week1 = activationDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime week2 = week1.plusWeeks(1);
        DateTime eddToChange = week2.plusWeeks(1);
        DateTime week17 = eddToChange.plusWeeks(1);

        moveToTime(startDate);

        String msisdn = createSubscriptionForIVR(pack);
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToTime(activationDate);
        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week2);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        String otherSubscriptionId = changeSchedule(msisdn, subscriptionId, pack, eddToChange, null);
        deactivateSubscription(msisdn,subscriptionId,operator);

        moveToTime(eddToChange);
        waitForSubscription(msisdn, otherSubscriptionId, SubscriptionStatus.PENDING_ACTIVATION.getDisplayString());
        activateSubscription(msisdn, otherSubscriptionId, operator);

        moveToTime(week17);
        currentCampaignId = "WEEK17";
        waitForCampaignMessage(otherSubscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, otherSubscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
    }

    @Test
    public void shouldCreateSubscriptionWithPackChange(){
        String operator = getRandomOperator();
        String pack = SubscriptionPack.BARI_KILKARI.name();

        DateTime activationDate = DateTime.now();
        DateTime edd = DateTime.now().plusWeeks(2).plusDays(1);
        DateTime week18 = edd.plusWeeks(1);
        DateTime week15 = activationDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1).plusDays(1);
        DateTime week16 = week15.plusWeeks(1);

        moveToTime(DateTime.now());

        String msisdn = createSubscriptionForCallCenter(pack, null, edd.toString("dd-MM-yyyy"));
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToTime(activationDate);
        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week15);
        String currentCampaignId = "WEEK15";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week16);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK16";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        String otherSubscriptionId = changePack(msisdn, subscriptionId, SubscriptionPack.NAVJAAT_KILKARI.name(), null, null);
        deactivateSubscription(msisdn,subscriptionId,operator);

        moveToTime(edd.plusDays(1));
        waitForSubscription(msisdn, otherSubscriptionId, SubscriptionStatus.PENDING_ACTIVATION.getDisplayString());
        activateSubscription(msisdn,otherSubscriptionId,operator);

        moveToTime(week18.plusDays(1));
        currentCampaignId = "WEEK18";
        waitForCampaignMessage(otherSubscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, otherSubscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));
    }

    @Test
    public void shouldCreateSubscriptionWithMSISDNChange() {
        DateTime startDate = DateTime.now();
        String operator = getRandomOperator();
        DateTime activationDate = startDate.plusDays(2);
        DateTime week1 = activationDate.plusDays(scheduleDeltaDays).plusMinutes(deltaMinutes + 1);
        DateTime week2 = week1.plusWeeks(1);
        DateTime week3 = week2.plusWeeks(1);
        String pack = SubscriptionPack.BARI_KILKARI.name();

        moveToTime(DateTime.now());
        String msisdn = createSubscriptionForIVR(pack);
        SubscriptionDetails subscriptionDetails = getSubscriptionDetails(msisdn).getSubscriptionDetails().get(0);
        String subscriptionId = subscriptionDetails.getSubscriptionId();
        System.out.println(subscriptionId + " " + msisdn);

        moveToTime(activationDate);
        activateSubscription(msisdn, subscriptionId, operator);

        moveToTime(week1);
        String currentCampaignId = "WEEK1";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        moveToTime(week2);
        renewSubscription(msisdn, subscriptionId, operator);
        currentCampaignId = "WEEK2";
        waitForCampaignMessage(subscriptionId, currentCampaignId);
        makeOBDCallBack(msisdn, subscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

        String modifiedMsisdn = "1" + RandomStringUtils.randomNumeric(9);
        String modifiedSubscriptionId = changeMsisdn(msisdn, modifiedMsisdn, pack);
        System.out.println("Modified "+modifiedSubscriptionId+" "+modifiedMsisdn);

        waitForSubscription(msisdn, subscriptionId, SubscriptionStatus.PENDING_DEACTIVATION.getDisplayString());
        deactivateSubscription(msisdn,subscriptionId,operator);

        waitForSubscription(modifiedMsisdn,modifiedSubscriptionId,SubscriptionStatus.PENDING_ACTIVATION.getDisplayString());

        moveToTime(week3);
        activateSubscription(modifiedMsisdn,modifiedSubscriptionId,operator);
        currentCampaignId = "WEEK3";
        waitForCampaignMessage(modifiedSubscriptionId, currentCampaignId);
        makeOBDCallBack(modifiedMsisdn, modifiedSubscriptionId, currentCampaignId, "HANGUP", DateTime.now(), DateTime.now().plusMinutes(10));

    }

}


