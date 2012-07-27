package org.motechproject.ananya.kilkari.functional;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.ananya.kilkari.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.TimedRunner;
import org.motechproject.ananya.kilkari.messagecampaign.service.MessageCampaignService;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.repository.OnMobileSubscriptionGateway;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.request.OMSubscriptionRequest;
import org.motechproject.ananya.kilkari.subscription.service.stub.StubOnMobileSubscriptionGateway;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SubscriptionCompletionFunctionalTest extends SpringIntegrationTest {

    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private AllCampaignEnrollments allCampaignEnrollments;
    @Autowired
    private KilkariCampaignService kilkariCampaignService;
    @Autowired
    private StubOnMobileSubscriptionGateway stubOnMobileSubscriptionGateway;

    @Before
    @After
    public void setUp() {
        allCampaignEnrollments.removeAll();
    }

    @Test
    public void shouldMarkSubscriptionAsCompletedOnPackCompletion() {
        //Given
        OnMobileSubscriptionGateway onMobileSubscriptionGatewayMock = mock(OnMobileSubscriptionGateway.class);
        stubOnMobileSubscriptionGateway.setBehavior(onMobileSubscriptionGatewayMock);
        Subscription subscription = addASubscriptionWithCreationDate15MonthsBack();
        final String subscriptionId = subscription.getSubscriptionId();

        //When
        setCurrentDateToThePastToTriggerSubscriptionCompletionEvent();
        kilkariCampaignService.processCampaignCompletion(subscriptionId, MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY);
        resetCurrentDateToSystemDate();

        //Expect
        Subscription subscriptionWithPendingCompletedStatus = waitForSubscriptionToChangeToPendingCompletionStatus(subscriptionId);
        verify(onMobileSubscriptionGatewayMock).deactivateSubscription(Matchers.<OMSubscriptionRequest>any());
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

    private void setCurrentDateToThePastToTriggerSubscriptionCompletionEvent() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().minusDays(3).minusMinutes(1).getMillis());
    }

    private Subscription addASubscriptionWithCreationDate15MonthsBack() {
        DateTime createdAt = DateTime.now().minusMonths(15);
        Subscription subscription = new Subscription("9988776655", SubscriptionPack.FIFTEEN_MONTHS, createdAt);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        allSubscriptions.add(subscription);
        CampaignEnrollment campaignEnrollment = new CampaignEnrollment(subscription.getSubscriptionId(), MessageCampaignService.FIFTEEN_MONTHS_CAMPAIGN_KEY);
        campaignEnrollment.setStartDate(createdAt.toLocalDate());
        allCampaignEnrollments.add(campaignEnrollment);
        return subscription;
    }
}
