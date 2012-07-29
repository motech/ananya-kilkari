package org.motechproject.ananya.kilkari.functional.test;

import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariFunctionalTestContext.xml")
@Ignore
public class SubscriptionCompletionFlowTest {
    @Autowired
    private FlowSystem flowSystem;

    @Test
    public void shouldSubscribeAndProgressAndCompleteSubscriptionSuccessfully() throws Exception {
        DateTime futureDateForFirstCampaignAlertToBeRaised = DateTime.now().plusDays(3);
        DateTime futureDateOfSecondCampaignAlert = DateTime.now().plusWeeks(1).plusDays(4);
        DateTime futureDateOfLastCampaignAlert = DateTime.now().plusWeeks(3).plusDays(2);
        SubscriptionData subscriptionData = new SubscriptionData(SubscriptionPack.FIFTEEN_MONTHS, "ivr", "9876543398");
        flowSystem.subscribe(subscriptionData).
                activate(subscriptionData).
                moveToFutureTime(futureDateForFirstCampaignAlertToBeRaised).
                verifyCampaignMessageInOBD(subscriptionData, "WEEK1").
                renew(subscriptionData).
                moveToFutureTime(futureDateOfSecondCampaignAlert).
                verifyCampaignMessageInOBD(subscriptionData, "WEEK2");
//                moveToFutureTime(futureDateOfLastCampaignAlert).
//                verifyPackCompletion();


    }


}
