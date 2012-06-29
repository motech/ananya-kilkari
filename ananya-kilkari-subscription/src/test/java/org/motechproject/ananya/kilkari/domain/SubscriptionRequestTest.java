package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubscriptionRequestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCreateSubscriptionRequest()  {
        DateTime beforeCreated = DateTime.now();
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "12", "myname", "01-11-2013", "04-11-2016");
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS.name(), subscriptionRequest.getPack());
        assertEquals(Channel.IVR.name(), subscriptionRequest.getChannel());
        assertEquals("1234567890", subscriptionRequest.getMsisdn());
        assertEquals("1234567890", subscriptionRequest.getMsisdn());
        assertEquals("myname", subscriptionRequest.getBeneficiaryName());
        assertEquals("12", subscriptionRequest.getBeneficiaryAge());
        assertEquals("01-11-2013", subscriptionRequest.getDateOfBirth());
        assertEquals("04-11-2016", subscriptionRequest.getExpectedDateOfDelivery());
        DateTime createdAt = subscriptionRequest.getCreatedAt();
        assertTrue(createdAt.isEqual(beforeCreated) || createdAt.isAfter(beforeCreated));
        assertTrue(createdAt.isEqualNow() || createdAt.isBeforeNow());
    }

    @Test
    public void shouldNotThrowExceptionWhenGivenSubscriptionDetailsAreAllValid()  {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "12", "myname", "01-11-2013", "04-11-2016");
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), null, null, null, null);
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "", "", "", "");
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidPackIsGivenToCreateNewSubscription()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid subscription pack Invalid-Pack"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequestForIVR("1234567890", "Invalid-Pack", Channel.IVR.name());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidChannelIsGivenToCreateNewSubscription()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid channel Invalid-Channel"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequestForIVR("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), "Invalid-Channel");
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid msisdn 12345"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequestForIVR("12345", SubscriptionPack.TWELVE_MONTHS.name(), Channel.IVR.name());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid msisdn 123456789a"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequestForIVR("123456789a", SubscriptionPack.TWELVE_MONTHS.name(), Channel.IVR.name());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidPackIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid subscription pack Invalid-Pack"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", "Invalid-Pack", Channel.CALL_CENTER.name(), null, null, null, null);
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidChannelIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid channel Invalid-Channel"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), "Invalid-Channel", null, null, null, null);
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidMsisdnNumberIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid msisdn 12345"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("12345", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), null, null, null, null);
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid msisdn 123456789a"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("123456789a", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), null, null, null, null);
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenNonNumericAgeIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid beneficiary age 1a"));

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "1a", "NAME", "21-01-2011", "21-01-2011");
        subscriptionRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionWhenNoAgeIsGivenToCreateNewSubscriptionForCC()  {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "", "NAME", "21-01-2011", "21-01-2011");
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), null, "NAME", "21-01-2011", "21-01-2011");
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidDOBIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid date of birth 21-21-11"));
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-21-11", "21-01-2011");

        subscriptionRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionWhenEmptyDOBIsGivenToCreateNewSubscriptionForCC()  {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "", "21-01-2011");
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", null, "21-01-2011");
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidEDDIsGivenToCreateNewSubscriptionForCC()  {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage(is("Invalid expected date of delivery 21-21-11"));
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-12-2012", "21-21-11");

        subscriptionRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionWhenEmptyEDDIsGivenToCreateNewSubscriptionForCC()  {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-12-2012", "");
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-12-2012", null);
        subscriptionRequest.validate();
    }


    private SubscriptionRequest createSubscriptionRequestForIVR(String msisdn, String pack, String channel) {
        return createSubscriptionRequest(msisdn, pack, channel, null, null, null, null);
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel, String age, String name, String dob, String edd) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults()
                .withPack(pack).withChannel(channel).withMsisdn(msisdn).withBeneficiaryAge(age)
                .withBeneficiaryName(name).withDOB(dob).withEDD(edd).build();

        return subscriptionRequest;
    }
}
