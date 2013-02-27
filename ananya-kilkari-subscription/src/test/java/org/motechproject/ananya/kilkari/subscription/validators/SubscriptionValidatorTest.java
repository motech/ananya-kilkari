package org.motechproject.ananya.kilkari.subscription.validators;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.messagecampaign.domain.MessageCampaignPack;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionBuilder;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.CampaignChangeReason;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberRequest;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriptionRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SubscriptionValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private AllSubscriptions allSubscriptions;

    private SubscriptionValidator subscriptionValidator;

    @Before
    public void setUp() {
        initMocks(this);
        subscriptionValidator = new SubscriptionValidator(allSubscriptions);
    }

    @Test
    public void shouldValidateIfSubscriptionAlreadyExists() {
        SubscriptionRequest subscription = new SubscriptionRequestBuilder().withDefaults().build();

        Subscription existingActiveSubscription = new SubscriptionBuilder().withDefaults().build();
        when(allSubscriptions.findSubscriptionInProgress(subscription.getMsisdn(), subscription.getPack())).thenReturn(existingActiveSubscription);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Active subscription already exists for msisdn[9876543210] and pack[BARI_KILKARI]");

        subscriptionValidator.validate(subscription);
    }

    @Test
    public void shouldValidateIfDOBIsWithinPacksWeekRange() {
        DateTime now = DateTime.now();
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withCreationDate(now).withDateOfBirth(now.minusWeeks(48)).withPack(SubscriptionPack.BARI_KILKARI).build();
        when(allSubscriptions.findSubscriptionInProgress(subscriptionRequest.getMsisdn(), subscriptionRequest.getPack())).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(String.format("Given dateOfBirth[%s] is not within the pack[%s] range", subscriptionRequest.getSubscriber().getDateOfBirth(), subscriptionRequest.getPack()));

        subscriptionValidator.validate(subscriptionRequest);
    }

    @Test
    public void shouldFailValidationIfWeekNumberIsOutsidePacksRange() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withWeek(30).build();

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Given week[30] is not within the pack[NANHI_KILKARI] range");

        subscriptionValidator.validate(subscriptionRequest);
    }

    @Test
    public void shouldFailValidationAndAppendErrorMessagesIfThereAreMultipleFailures() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withWeek(29).build();

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Given week[29] is not within the pack[NANHI_KILKARI] range");

        subscriptionValidator.validate(subscriptionRequest);
    }

    @Test
    public void blankWeekNumberIsValid() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withPack(SubscriptionPack.NANHI_KILKARI).withWeek(null).build();

        try {
            subscriptionValidator.validate(subscriptionRequest);
        } catch (ValidationException e) {
            Assert.fail("Unexpected ValidationException");
        }
    }

    @Test
    public void shouldValidateInvalidSubscriptionIdInSubscriberDetails() {
        Location location = new Location("district", "block", "panchayat");
        String subscriptionId = "subscriptionId";
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription does not exist for subscriptionId subscriptionId");

        subscriptionValidator.validateSubscriberDetails(new SubscriberRequest(subscriptionId, Channel.CONTACT_CENTER.name(), DateTime.now(), "name", 23,
                location));
    }

    @Test
    public void shouldValidateAndThrowIfSubscriptionIsNotActive() {
        Subscription subscription = mock(Subscription.class);
        String subscriptionId = "subscriptionId";

        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscription.isActiveOrSuspended()).thenReturn(false);
        when(subscription.getMessageCampaignPack()).thenReturn(MessageCampaignPack.BARI_KILKARI);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription is not active for subscriptionId subscriptionId");

        subscriptionValidator.validateChangeCampaign(subscriptionId, CampaignChangeReason.INFANT_DEATH);
    }

    @Test
    public void shouldValidateAndThrowIfSubscriptionIsNotThere() {
        String subscriptionId = "subscriptionId";

        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription does not exist for subscriptionId subscriptionId");

        subscriptionValidator.validateChangeCampaign(subscriptionId, CampaignChangeReason.MISCARRIAGE);
    }

    @Test
    public void shouldValidateIfSubscriptionIsAlreadyInMiscarriage() {
        String subscriptionId = "subscriptionId";
        Subscription subscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscription.isActiveOrSuspended()).thenReturn(true);
        when(subscription.getMessageCampaignPack()).thenReturn(MessageCampaignPack.BARI_KILKARI);

        subscriptionValidator.validateChangeCampaign(subscriptionId, CampaignChangeReason.INFANT_DEATH);
    }

    @Test
    public void shouldValidateAndThrowExceptionIfSubscriptionIsAlreadyInMiscarriage() {
        String subscriptionId = "subscriptionId";
        Subscription subscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscription.getMessageCampaignPack()).thenReturn(MessageCampaignPack.MISCARRIAGE);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription with subscriptionId subscriptionId is already in MISCARRIAGE");

        subscriptionValidator.validateChangeCampaign(subscriptionId, CampaignChangeReason.INFANT_DEATH);
    }

    @Test
    public void shouldValidateAndThrowExceptionIfSubscriptionIsAlreadyInInfantDeath() {
        String subscriptionId = "subscriptionId";
        Subscription subscription = mock(Subscription.class);
        when(allSubscriptions.findBySubscriptionId(subscriptionId)).thenReturn(subscription);
        when(subscription.getMessageCampaignPack()).thenReturn(MessageCampaignPack.INFANT_DEATH);

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Subscription with subscriptionId subscriptionId is already in INFANT_DEATH");

        subscriptionValidator.validateChangeCampaign(subscriptionId, CampaignChangeReason.INFANT_DEATH);
    }
}