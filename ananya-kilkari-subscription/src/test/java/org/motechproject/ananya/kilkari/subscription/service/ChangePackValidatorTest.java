package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.*;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangePackValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private AllSubscriptions allSubscriptions;

    @Test
    public void shouldThrowExceptionIfSubscriptionOfPackChangeIsNotInActiveOrSuspendedState(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();
        String subscriptionId = subscription.getSubscriptionId();
        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, "1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");
        when(allSubscriptions.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription is not active for subscription "+subscriptionId);

        ChangePackValidator.validate(subscription, changeSubscriptionRequest);
    }

    @Test
    public void changePackRequestPackShouldBeDifferentFromCurrentPack() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(allSubscriptions.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, "1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s is already subscribed to requested pack ",subscriptionId));

        ChangePackValidator.validate(subscription, changeSubscriptionRequest);
    }
}
