package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.junit.Before;
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
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeSubscriptionRequest;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ChangeSubscriptionValidatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SubscriptionService subscriptionService;

    private ChangeSubscriptionValidator changeSubscriptionValidator;

    @Before
    public void setUp(){
        initMocks(this);
        changeSubscriptionValidator = new ChangeSubscriptionValidator(subscriptionService);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionForChangePackDoesNotExist(){
        String subscriptionId = "subscriptionId";
        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, "1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");

        when(subscriptionService.findBySubscriptionId(subscriptionId)).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription does not exist for subscriptionId %s", subscriptionId));

        changeSubscriptionValidator.validate(changeSubscriptionRequest);
    }

    @Test
    public void shouldThrowExceptionIfSubscriptionOfPackChangeIsNotInActiveOrSuspendedState(){
        Subscription subscription = new SubscriptionBuilder().withDefaults().withStatus(SubscriptionStatus.ACTIVATION_FAILED).build();
        String subscriptionId = subscription.getSubscriptionId();
        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, "1111111111", subscriptionId, SubscriptionPack.BARI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");
        when(subscriptionService.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription is not active for subscriptionId "+subscriptionId);

        changeSubscriptionValidator.validate(changeSubscriptionRequest);
    }

    @Test
    public void changePackRequestPackShouldBeDifferentFromCurrentPack_ForChangePack() {
        String msisdn = "1111111111";
        SubscriptionPack pack = SubscriptionPack.BARI_KILKARI;
        Subscription subscription = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(pack).build();
        String subscriptionId = subscription.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);
        when(subscriptionService.findByMsisdnAndPack(msisdn, pack)).thenReturn(Arrays.asList(subscription));

        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, msisdn, subscriptionId, pack, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Active subscription already exists for %s and %s",msisdn,pack));

        changeSubscriptionValidator.validate(changeSubscriptionRequest);
    }

    @Test
    public void changePackRequestPackShouldBeDifferentFromAnExistingActiveSubscriptionPack_ForChangePack() {
        String msisdn = "1111111111";
        SubscriptionPack requestedPack = SubscriptionPack.BARI_KILKARI;
        Subscription subscription1 = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(SubscriptionPack.CHOTI_KILKARI).build();
        Subscription subscription2 = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(SubscriptionPack.BARI_KILKARI).withStatus(SubscriptionStatus.ACTIVE).build();
        Subscription subscription3 = new SubscriptionBuilder().withDefaults().withMsisdn(msisdn).withPack(SubscriptionPack.BARI_KILKARI).withStatus(SubscriptionStatus.DEACTIVATED).build();
        String subscriptionId = subscription1.getSubscriptionId();

        when(subscriptionService.findBySubscriptionId(subscription1.getSubscriptionId())).thenReturn(subscription1);
        when(subscriptionService.findByMsisdnAndPack(msisdn, requestedPack)).thenReturn(Arrays.asList(subscription2,subscription3));

        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_PACK, msisdn, subscriptionId, requestedPack, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Active subscription already exists for %s and %s",msisdn,requestedPack));

        changeSubscriptionValidator.validate(changeSubscriptionRequest);
    }

    @Test
    public void shouldThrowExceptionIfPackIsDifferentFromCurrentPack_ForChangeSchedule() {
        Subscription subscription = new SubscriptionBuilder().withDefaults().withPack(SubscriptionPack.BARI_KILKARI).build();
        String subscriptionId = subscription.getSubscriptionId();
        when(subscriptionService.findBySubscriptionId(subscription.getSubscriptionId())).thenReturn(subscription);

        ChangeSubscriptionRequest changeSubscriptionRequest = new ChangeSubscriptionRequest(ChangeSubscriptionType.CHANGE_SCHEDULE, "1111111111", subscriptionId, SubscriptionPack.CHOTI_KILKARI, Channel.CALL_CENTER, DateTime.now(), null, null, "reason");

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Subscription %s is not subscribed to requested pack",subscriptionId));

        changeSubscriptionValidator.validate(changeSubscriptionRequest);
    }
}
