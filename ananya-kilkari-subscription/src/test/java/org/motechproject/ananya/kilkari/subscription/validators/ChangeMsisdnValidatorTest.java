package org.motechproject.ananya.kilkari.subscription.validators;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.ChangeMsisdnRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChangeMsisdnValidatorTest {

    @Mock
    private AllSubscriptions allSubscriptions;

    private ChangeMsisdnValidator changeMsisdnValidator;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        changeMsisdnValidator = new ChangeMsisdnValidator(allSubscriptions);
    }

    @Test
    public void shouldValidateChangeMsisdnRequest() {
        String oldMsisdn = "9876543210";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, "9876543211", Channel.CALL_CENTER);
        changeMsisdnRequest.setPacks(Arrays.asList(SubscriptionPack.BARI_KILKARI, SubscriptionPack.CHOTI_KILKARI));

        Subscription subscription1 = new Subscription(oldMsisdn, SubscriptionPack.BARI_KILKARI, null, DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);


        Subscription subscription2 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, null, DateTime.now());
        subscription2.setStatus(SubscriptionStatus.SUSPENDED);


        when(allSubscriptions.findUpdatableSubscriptions(changeMsisdnRequest.getOldMsisdn())).thenReturn(Arrays.asList(subscription1,subscription2));

        changeMsisdnValidator.validate(changeMsisdnRequest);
    }

    @Test
    public void shouldValidateChangeMsisdnRequestWhenOnePackIsMissing() {
        String oldMsisdn = "9876543210";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, "9876543211", Channel.CALL_CENTER);
        changeMsisdnRequest.setPacks(Arrays.asList(SubscriptionPack.BARI_KILKARI, SubscriptionPack.CHOTI_KILKARI, SubscriptionPack.NANHI_KILKARI));

        Subscription subscription1 = new Subscription(oldMsisdn, SubscriptionPack.BARI_KILKARI, null, DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);

        Subscription subscription2 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, null, DateTime.now());
        subscription2.setStatus(SubscriptionStatus.NEW_EARLY);

        when(allSubscriptions.findUpdatableSubscriptions(changeMsisdnRequest.getOldMsisdn())).thenReturn(Arrays.asList(subscription1, subscription2));

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Requested Msisdn doesn't actively subscribe to all the packs which have been requested");

        changeMsisdnValidator.validate(changeMsisdnRequest);
    }

    @Test
    public void shouldValidateChangeMsisdnRequestWhenAllPacksSpecified() {
        String oldMsisdn = "9876543210";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, "9876543211", Channel.CALL_CENTER);
        changeMsisdnRequest.setShouldChangeAllPacks(true);

        Subscription subscription1 = new Subscription(oldMsisdn, SubscriptionPack.BARI_KILKARI, null, DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);

        Subscription subscription2 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, null, DateTime.now());
        subscription2.setStatus(SubscriptionStatus.SUSPENDED);

        Subscription subscription3 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, null, DateTime.now());
        subscription3.setStatus(SubscriptionStatus.PENDING_ACTIVATION);

        when(allSubscriptions.findByMsisdn(changeMsisdnRequest.getOldMsisdn())).thenReturn(Arrays.asList(subscription1, subscription2, subscription3));

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Requested Msisdn doesn't have all subscriptions in updatable state");
        changeMsisdnValidator.validate(changeMsisdnRequest);
    }

    @Test
    public void shouldValidateChangeMsisdnRequestWhenNoSubscriptionIsPresent() {
        String oldMsisdn = "9876543210";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, "9876543211", Channel.CALL_CENTER);
        changeMsisdnRequest.setPacks(Arrays.asList(SubscriptionPack.BARI_KILKARI, SubscriptionPack.CHOTI_KILKARI, SubscriptionPack.NANHI_KILKARI));

        when(allSubscriptions.findUpdatableSubscriptions(changeMsisdnRequest.getOldMsisdn())).thenReturn(new ArrayList<Subscription>());

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Requested Msisdn has no subscriptions in the updatable state");

        changeMsisdnValidator.validate(changeMsisdnRequest);
    }

    @Test
    public void shouldValidateChangeMsisdnRequestWhenOneOfTheRequestedSubscriptionIsNotUpdatable() {
        String oldMsisdn = "9876543210";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, "9876543211", Channel.CALL_CENTER);
        changeMsisdnRequest.setPacks(Arrays.asList(SubscriptionPack.BARI_KILKARI, SubscriptionPack.CHOTI_KILKARI));

        Subscription subscription1 = new Subscription(oldMsisdn, SubscriptionPack.BARI_KILKARI, null, DateTime.now());
        subscription1.setStatus(SubscriptionStatus.ACTIVE);

        Subscription subscription2 = new Subscription(oldMsisdn, SubscriptionPack.CHOTI_KILKARI, null, DateTime.now());
        subscription2.setStatus(SubscriptionStatus.PENDING_ACTIVATION);

        when(allSubscriptions.findUpdatableSubscriptions(changeMsisdnRequest.getOldMsisdn())).thenReturn(Arrays.asList(subscription1));

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Requested Msisdn doesn't actively subscribe to all the packs which have been requested");

        changeMsisdnValidator.validate(changeMsisdnRequest);
    }

    @Test
    public void shouldValidateWhenAllPacksSpecifiedAndSubscriptionDoesNotExist() {
        String oldMsisdn = "9876543210";
        ChangeMsisdnRequest changeMsisdnRequest = new ChangeMsisdnRequest(oldMsisdn, "9876543211", Channel.CALL_CENTER);
        changeMsisdnRequest.setShouldChangeAllPacks(true);

        when(allSubscriptions.findByMsisdn(changeMsisdnRequest.getOldMsisdn())).thenReturn(Collections.EMPTY_LIST);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Requested Msisdn has no subscriptions in the updatable state");
        changeMsisdnValidator.validate(changeMsisdnRequest);
    }
}
