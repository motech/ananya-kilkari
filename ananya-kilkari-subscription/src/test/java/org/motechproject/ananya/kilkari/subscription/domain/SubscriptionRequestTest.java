package org.motechproject.ananya.kilkari.subscription.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.subscription.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SubscriptionRequestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private List<String> errors = new ArrayList<>();

    @Before
    @After
    public void tearDown() {
        errors.clear();
    }

    private void validateErrors(int size, String... msgs) {
        assertEquals(size, errors.size());
        for (String msg : msgs) {
            assertTrue(errors.contains(msg));
        }
    }

    @Test
    public void shouldCreateSubscriptionRequest() {
        DateTime createdAt = DateTime.now();
        String dob = "01-11-2013";
        String edd = "04-11-2016";
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(SubscriptionRequest.DATE_TIME_FORMAT);

        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(),
                Channel.IVR.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);
        assertEquals(SubscriptionPack.FIFTEEN_MONTHS.name(), subscriptionRequest.getPack());
        assertEquals(Channel.IVR.name(), subscriptionRequest.getChannel());
        assertEquals("1234567890", subscriptionRequest.getMsisdn());
        assertEquals("1234567890", subscriptionRequest.getMsisdn());
        assertEquals("myname", subscriptionRequest.getBeneficiaryName());
        assertEquals(12, subscriptionRequest.getBeneficiaryAge());
        assertEquals(dateTimeFormatter.parseDateTime(dob), subscriptionRequest.getDateOfBirth());
        assertEquals(dateTimeFormatter.parseDateTime(edd), subscriptionRequest.getExpectedDateOfDelivery());
        assertEquals("mydistrict", subscriptionRequest.getDistrict());
        assertEquals("myblock", subscriptionRequest.getBlock());
        assertEquals("mypanchayat", subscriptionRequest.getPanchayat());
        assertEquals(createdAt, subscriptionRequest.getCreatedAt());
    }

    @Test
    public void shouldNotValidateAgeDOBEDDForIVR() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("9876543210")
                .withChannel(Channel.IVR.name()).withPack(SubscriptionPack.FIFTEEN_MONTHS.name()).build();

        subscriptionRequest.validate(errors);

        validateErrors(0);
    }

    @Test
    public void shouldNotAddErrorWhenGivenSubscriptionDetailsAreAllValid() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS.name()).withChannel(Channel.IVR.name()).build();
        subscriptionRequest.validate(errors);
        validateErrors(0);

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "12", "myname", "01-11-2013", "04-11-2016", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), null, null, null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "", "", "", "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldAddErrorWhenInvalidPackIsGivenToCreateNewSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("1234567890").withPack("Invalid-Pack").withChannel(Channel.IVR.name()).build();

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid subscription pack Invalid-Pack");
    }

    @Test
    public void shouldAddErrorWhenInvalidChannelIsGivenToCreateNewSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel("Invalid-Channel").withCreatedAt(DateTime.now()).build();

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid channel Invalid-Channel");
    }

    @Test
    public void shouldAddErrorWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("12345").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel(Channel.IVR.name()).build();

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid msisdn 12345");
    }

    @Test
    public void shouldAddErrorWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("123456789a").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel(Channel.IVR.name()).build();

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid msisdn 123456789a");
    }


    @Test
    public void shouldAddErrorWhenNonNumericAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "1a", "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid beneficiary age 1a");
    }

    @Test
    public void shouldNotAddErrorWhenNoAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "", "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), null, "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldAddErrorWhenInvalidDOBIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-21-11", null, "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid date of birth 21-21-11");
    }

    @Test
    public void shouldNotAddErrorWhenEmptyDOBIsGivenToCreateNewSubscriptionForCC() {
        String edd = DateTime.now().plusDays(2).toString("dd-MM-yyyy");
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "", edd, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", null, edd, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldAddErrorWhenInvalidEDDIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", null, "21-21-11", "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid expected date of delivery 21-21-11");
    }

    @Test
    public void shouldNotAddErrorWhenEmptyEDDIsGivenToCreateNewSubscriptionForCC() {
        String dob = DateTime.now().minusDays(1).toString("dd-MM-yyyy");
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", dob, "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", dob, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate(errors);
        validateErrors(0);
    }

    @Test
    public void shouldConvertAgeToIntegerAndReturn() {
        assertEquals(12, new SubscriptionRequestBuilder().withDefaults().withBeneficiaryAge("12").build().getBeneficiaryAge());
        assertEquals(0, new SubscriptionRequestBuilder().withDefaults().withBeneficiaryAge(null).build().getBeneficiaryAge());
        assertEquals(0, new SubscriptionRequestBuilder().withDefaults().withBeneficiaryAge("").build().getBeneficiaryAge());
    }

    @Test
    public void shouldAddErrorWhenDOBIsGreaterThanSubscriptionDate() {
        DateTime createdAt = DateTime.now();
        String dob = createdAt.plusMonths(4).toString("dd-MM-yyyy");
        String edd = null;
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(),
                Channel.CALL_CENTER.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid date of birth " + dob);
    }

    @Test
    public void shouldAddErrorWhenEDDIsLesserThanSubscriptionDate() {
        DateTime createdAt = DateTime.now();
        String dob = null;
        String edd = createdAt.minusMonths(4).toString("dd-MM-yyyy");
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(),
                Channel.CALL_CENTER.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);

        subscriptionRequest.validate(errors);

        validateErrors(1, "Invalid expected date of delivery " + edd);
    }

    @Test
    public void shouldConvertEDDToDateTimeAndReturn() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(SubscriptionRequest.DATE_TIME_FORMAT);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withDOB("15-04-2013").withEDD("13-01-2012").build();
        assertEquals(formatter.parseDateTime("13-01-2012"), subscriptionRequest.getExpectedDateOfDelivery());
        assertEquals(formatter.parseDateTime("15-04-2013"), subscriptionRequest.getDateOfBirth());

        subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withDOB(null).withEDD(null).build();
        assertNull(subscriptionRequest.getDateOfBirth());
        assertNull(subscriptionRequest.getExpectedDateOfDelivery());

        subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withDOB("").withEDD("").build();
        assertNull(subscriptionRequest.getDateOfBirth());
        assertNull(subscriptionRequest.getExpectedDateOfDelivery());
    }

    @Test
    public void shouldAddMultipleErrorsIfMsisdnPackAndChannelAreInvalid() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withMsisdn("12345").withPack("invalid pack").withChannel("invalid channel").build();
        
        subscriptionRequest.validate(errors);

        validateErrors(3, "Invalid msisdn 12345", "Invalid subscription pack invalid pack", "Invalid channel invalid channel");
    }
    @Test
    public void shouldThrowExceptionWhenChannelIsInvalid() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withChannel("invalid channel").build();
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid channel invalid channel");
        
        subscriptionRequest.validateChannel();
    }

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel, String age, String name, String dob, String edd, String district, String block, String panchayat, DateTime createdAt) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults()
                .withPack(pack).withChannel(channel).withMsisdn(msisdn).withBeneficiaryAge(age)
                .withBeneficiaryName(name).withDOB(dob).withEDD(edd).withDistrict(district).withBlock(block).withPanchayat(panchayat).withCreatedAt(createdAt).build();

        return subscriptionRequest;
    }

}
