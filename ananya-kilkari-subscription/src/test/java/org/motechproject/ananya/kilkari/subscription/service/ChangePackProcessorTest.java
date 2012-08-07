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
import org.motechproject.ananya.kilkari.subscription.service.request.ChangePackRequest;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangePackProcessorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private SubscriptionService subscriptionService;

    private ChangePackProcessor changePackProcessor;

    @Before
    public void setUp(){
        changePackProcessor = new ChangePackProcessor(subscriptionService);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionOfPackChangeIsNotInActiveOrSuspendedState(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription is not active for subscription "+subscriptionId);

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void changePackRequestPackShouldBeDifferentFromCurrentPack() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s is already subscribed to requested pack ",subscriptionId));

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void shouldInvalidateIfCurrentlyIn12MonthPackAndChangePackRequestedFor15MonthWithoutEddOrDobChange(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s is already in %s pack and cannot be moved to an earlier pack ",subscriptionId,subscription.getPack().name()));
        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void shouldInvalidateIfCurrentlyIn7MonthPackAndChangePackRequestedFor12MonthWithoutEddOrDobChange(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.CHOTI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s is already in %s pack and cannot be moved to an earlier pack ",subscriptionId,subscription.getPack().name()));

        changePackProcessor.process(changePackRequest);
    }

    @Test
    public void shouldNotAllowChangePackIfNumberOfWeeksLeftInCurrentPackIsLessThanChangePackRequest() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.CHOTI_KILKARI).build();
        subscription.setStartDate(DateTime.now().minusWeeks(24));
        String subscriptionId = subscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        ChangePackRequest changePackRequest = new ChangePackRequest("1111111111", subscriptionId, SubscriptionPack.NANHI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s has fewer weeks left than the new pack request",subscriptionId));

        changePackProcessor.process(changePackRequest);
    }
}
