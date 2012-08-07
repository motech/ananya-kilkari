package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangePackRequest;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangePackProcessorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ChangePackProcessor changePackProcessor;
    @Mock
    private AllSubscriptions allSubscripitons;

    @Before
    public void setUp(){
        changePackProcessor = new ChangePackProcessor(allSubscripitons);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionOfPackChangeIsNotInActiveOrSuspendedState(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();
        String subscriptionId = subscription.getSubscriptionId();
        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);
        when(allSubscripitons.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription is not active for subscription "+subscriptionId);

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void changePackRequestPackShouldBeDifferentFromCurrentPack() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscripitons.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s is already subscribed to requested pack ",subscriptionId));

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void shouldInvalidateIfCurrentlyIn12MonthPackAndChangePackRequestedFor15Month(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscripitons.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscripiton pack requested is not applicable for subscription ", subscriptionId));

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void shouldInvalidateIfCurrentlyIn7MonthPackAndChangePackRequestedFor12Month(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscripitons.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.CHOTI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscripiton pack requested is not applicable for subscription ", subscriptionId));

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void shouldNotAllowChangePackIfNumberOfWeeksLeftInCurrentPackIsLessThanChangePackRequest() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).build();
        subscription.setStartDate(DateTime.now().minusWeeks(24));
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscripitons.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.NANHI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscripiton pack requested is not applicable for subscription ", subscriptionId));

        changePackProcessor.process(changePackRequest);
    }
}
