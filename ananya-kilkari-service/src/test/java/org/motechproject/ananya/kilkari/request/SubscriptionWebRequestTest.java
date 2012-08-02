package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.*;

public class SubscriptionWebRequestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Errors errors;

    @Before
    public void tearDown() {
        errors = new Errors();
    }

    private void validateErrors(int size, String... msgs) {
        assertEquals(size, errors.getCount());
        for (String msg : msgs) {
            assertTrue(errors.hasMessage(msg));
        }
    }

    @Test
    public void shouldCreateSubscriptionRequest() {
        DateTime createdAt = DateTime.now();
        String dob = "01-11-2013";
        String edd = "04-11-2016";
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy");

        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(),
                Channel.IVR.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS.name(), subscriptionWebRequest.getPack());
        assertEquals(Channel.IVR.name(), subscriptionWebRequest.getChannel());
        assertEquals("1234567890", subscriptionWebRequest.getMsisdn());
        assertEquals("1234567890", subscriptionWebRequest.getMsisdn());
        assertEquals("myname", subscriptionWebRequest.getBeneficiaryName());
        assertEquals(12, (int) subscriptionWebRequest.getBeneficiaryAge());
        assertEquals(dateTimeFormatter.parseDateTime(dob), subscriptionWebRequest.getDateOfBirth());
        assertEquals(dateTimeFormatter.parseDateTime(edd), subscriptionWebRequest.getExpectedDateOfDelivery());
        assertEquals("mydistrict", subscriptionWebRequest.getDistrict());
        assertEquals("myblock", subscriptionWebRequest.getBlock());
        assertEquals("mypanchayat", subscriptionWebRequest.getPanchayat());
        assertEquals(createdAt, subscriptionWebRequest.getCreatedAt());
    }

    @Test
    public void shouldNotValidateAgeDOBEDDForIVR() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("9876543210")
                .withChannel(Channel.IVR.name()).withPack(SubscriptionPack.FIFTEEN_MONTHS.name()).build();

        subscriptionWebRequest.validate(errors);

        validateErrors(0);
    }

    @Test
    public void shouldNotAddErrorWhenGivenSubscriptionDetailsAreAllValid() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS.name()).withChannel(Channel.IVR.name()).build();
        subscriptionWebRequest.validate(errors);
        validateErrors(0);

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "12", "myname", "01-11-2013", "04-11-2016", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), null, null, null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "", "", "", "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldAddErrorWhenInvalidPackIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("1234567890").withPack("Invalid-Pack").withChannel(Channel.IVR.name()).build();

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid subscription pack Invalid-Pack");
    }

    @Test
    public void shouldAddErrorWhenMoreThanOneOfEddDobOrWeekNumberIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequestWithDobEdd = new SubscriptionWebRequestBuilder().withDefaults().withDOB("01-01-2012").withEDD("31-12-2012").build();
        SubscriptionWebRequest subscriptionWebRequestWithEddWeek = new SubscriptionWebRequestBuilder().withDefaults().withWeek("4").withEDD("31-12-2012").build();
        SubscriptionWebRequest subscriptionWebRequestWithDobWeek = new SubscriptionWebRequestBuilder().withDefaults().withWeek("4").withDOB("31-12-2011").build();
        SubscriptionWebRequest subscriptionWebRequestWithDobEddWeek = new SubscriptionWebRequestBuilder().withDefaults().withWeek("4").withDOB("31-12-2011").withEDD("31-12-2012").build();

        errors = new Errors();
        subscriptionWebRequestWithDobEdd.validate(errors);
        validateErrors(1, "Invalid request. Only one of date of delivery, date of birth and week number should be present");

        errors = new Errors();
        subscriptionWebRequestWithEddWeek.validate(errors);
        validateErrors(1, "Invalid request. Only one of date of delivery, date of birth and week number should be present");

        errors = new Errors();
        subscriptionWebRequestWithDobWeek.validate(errors);
        validateErrors(1, "Invalid request. Only one of date of delivery, date of birth and week number should be present");

        errors = new Errors();
        subscriptionWebRequestWithDobEddWeek.validate(errors);
        validateErrors(1, "Invalid request. Only one of date of delivery, date of birth and week number should be present");
    }

    @Test
    public void shouldAddErrorWhenInvalidChannelIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel("Invalid-Channel").withCreatedAt(DateTime.now()).build();

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid channel Invalid-Channel");
    }

    @Test
    public void shouldAddErrorWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("12345").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel(Channel.IVR.name()).build();

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid msisdn 12345");
    }

    @Test
    public void shouldAddErrorWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("123456789a").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel(Channel.IVR.name()).build();

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid msisdn 123456789a");
    }


    @Test
    public void shouldAddErrorWhenNonNumericAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "1a", "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid beneficiary age 1a");
    }

    @Test
    public void shouldNotAddErrorWhenNoAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "", "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), null, "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldAddErrorWhenInvalidDOBIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-21-11", null, "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid date of birth 21-21-11");
    }

    @Test
    public void shouldNotAddErrorWhenEmptyDOBIsGivenToCreateNewSubscriptionForCC() {
        String edd = DateTime.now().plusDays(2).toString("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "", edd, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", null, edd, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldAddErrorWhenInvalidEDDIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", null, "21-21-11", "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid expected date of delivery 21-21-11");
    }

    @Test
    public void shouldNotAddErrorWhenEmptyEDDIsGivenToCreateNewSubscriptionForCC() {
        String dob = DateTime.now().minusDays(1).toString("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", dob, "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", dob, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionWebRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldConvertAgeToIntegerAndReturn() {
        assertEquals(12, (int)new SubscriptionWebRequestBuilder().withDefaults().withBeneficiaryAge("12").build().getBeneficiaryAge());
        assertEquals(null, new SubscriptionWebRequestBuilder().withDefaults().withBeneficiaryAge(null).build().getBeneficiaryAge());
        assertEquals(null, new SubscriptionWebRequestBuilder().withDefaults().withBeneficiaryAge("").build().getBeneficiaryAge());
    }

    @Test
    public void shouldAddErrorWhenDOBIsGreaterThanSubscriptionDate() {
        DateTime createdAt = DateTime.now();
        String dob = createdAt.plusMonths(4).toString("dd-MM-yyyy");
        String edd = null;
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(),
                Channel.CALL_CENTER.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid date of birth " + dob);
    }

    @Test
    public void shouldAddErrorWhenEDDIsLesserThanSubscriptionDate() {
        DateTime createdAt = DateTime.now();
        String dob = null;
        String edd = createdAt.minusMonths(4).toString("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(),
                Channel.CALL_CENTER.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid expected date of delivery " + edd);
    }

    @Test
    public void shouldConvertEDDToDateTimeAndReturn() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withDOB("15-04-2013").withEDD("13-01-2012").build();
        assertEquals(formatter.parseDateTime("13-01-2012"), subscriptionWebRequest.getExpectedDateOfDelivery());
        assertEquals(formatter.parseDateTime("15-04-2013"), subscriptionWebRequest.getDateOfBirth());

        subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withDOB(null).withEDD(null).build();
        assertNull(subscriptionWebRequest.getDateOfBirth());
        assertNull(subscriptionWebRequest.getExpectedDateOfDelivery());

        subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withDOB("").withEDD("").build();
        assertNull(subscriptionWebRequest.getDateOfBirth());
        assertNull(subscriptionWebRequest.getExpectedDateOfDelivery());
    }

    @Test
    public void shouldAddMultipleErrorsIfMsisdnPackAndChannelAreInvalid() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withMsisdn("12345").withPack("invalid pack").withChannel("invalid channel").build();

        subscriptionWebRequest.validate(errors);

        validateErrors(3, "Invalid msisdn 12345", "Invalid subscription pack invalid pack", "Invalid channel invalid channel");
    }

    @Test
    public void shouldNotFailValidationIfWeekNumberIsBlank() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withWeek("").build();

        subscriptionWebRequest.validate(errors);

        validateErrors(0);
    }

    @Test
    public void shouldFailValidationIfWeekNumberIsNotANumber() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withWeek("a").build();

        subscriptionWebRequest.validate(errors);

        validateErrors(1, "Invalid week number a");
    }

    @Test
    public void shouldThrowExceptionWhenChannelIsInvalid() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withChannel("invalid channel").build();
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid channel invalid channel");

        subscriptionWebRequest.validateChannel();
    }

    private SubscriptionWebRequest createSubscriptionRequest(String msisdn, String pack, String channel, String age, String name, String dob, String edd, String district, String block, String panchayat, DateTime createdAt) {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults()
                .withPack(pack).withChannel(channel).withMsisdn(msisdn).withBeneficiaryAge(age)
                .withBeneficiaryName(name).withDOB(dob).withEDD(edd).withDistrict(district).withBlock(block).withPanchayat(panchayat).withCreatedAt(createdAt).build();

        return subscriptionWebRequest;
    }
}
