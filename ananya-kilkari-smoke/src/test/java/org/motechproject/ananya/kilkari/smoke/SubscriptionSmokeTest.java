package org.motechproject.ananya.kilkari.smoke;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.motechproject.ananya.kilkari.domain.CampaignMessageAlert;
import org.motechproject.ananya.kilkari.repository.AllCampaignMessageAlerts;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.smoke.utils.TimedRunner;
import org.motechproject.ananya.kilkari.subscription.domain.ProcessSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.gateway.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.stub.StubOnMobileSubscriptionGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariSmokeWithStubsContext.xml")
public class SubscriptionSmokeTest {

    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private KilkariCampaignService kilkariCampaignService;
    @Autowired
    private AllCampaignMessageAlerts allCampaignMessageAlerts;
    @Autowired
    private StubOnMobileSubscriptionGateway stubOnMobileSubscriptionGateway;

    @Test
    public void shouldMarkSubscriptionAsCompletedOnPackCompletion() {
        //Given
        OnMobileSubscriptionGateway onMobileSubscriptionGatewayMock = mock(OnMobileSubscriptionGateway.class);
        stubOnMobileSubscriptionGateway.setBehavior(onMobileSubscriptionGatewayMock);
        Subscription subscription = addASubscriptionWithCreationDate15MonthsBack();
        final String subscriptionId = subscription.getSubscriptionId();
        allCampaignMessageAlerts.add(new CampaignMessageAlert(subscriptionId, "week59"));

        //When
        setCurrentDateToTheFutureToTriggerSubscriptionCompletionEvent();
        kilkariCampaignService.scheduleWeeklyMessage(subscriptionId);
        resetCurrentDateToSystemDate();

        //Expect
        Subscription subscriptionWithPendingCompletedStatus = waitForSubscriptionToChangeToPendingCompletionStatus(subscriptionId);
        verify(onMobileSubscriptionGatewayMock).deactivateSubscription(Matchers.<ProcessSubscriptionRequest>any());
        assertNotNull(subscriptionWithPendingCompletedStatus);
    }

    private Subscription waitForSubscriptionToChangeToPendingCompletionStatus(final String subscriptionId) {
        return new TimedRunner<Subscription>(40, 3000) {
                public Subscription run() {
                    Subscription subscription = allSubscriptions.findBySubscriptionId(subscriptionId);
                    return subscription.getStatus()== SubscriptionStatus.PENDING_COMPLETION? subscription : null;
                }
            }.executeWithTimeout();
    }

    private void resetCurrentDateToSystemDate() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    private void setCurrentDateToTheFutureToTriggerSubscriptionCompletionEvent() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().minusDays(3).minusMinutes(1).getMillis());
    }

    private Subscription addASubscriptionWithCreationDate15MonthsBack() {
        Subscription subscription = new Subscription("9988776655", SubscriptionPack.FIFTEEN_MONTHS, DateTime.now().minusMonths(15));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(subscription);
        return subscription;
    }
}
