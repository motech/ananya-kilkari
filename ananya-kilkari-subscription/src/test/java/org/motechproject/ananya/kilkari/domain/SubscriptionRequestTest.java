package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.builder.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.exceptions.ValidationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SubscriptionRequestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        subscriptionRequest.validate();
    }


    @Test
    public void shouldNotThrowExceptionWhenGivenSubscriptionDetailsAreAllValid() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.FIFTEEN_MONTHS.name()).withChannel(Channel.IVR.name()).build();
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "12", "myname", "01-11-2013", "04-11-2016", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), null, null, null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.FIFTEEN_MONTHS.name(), Channel.IVR.name(), "", "", "", "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidPackIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid subscription pack Invalid-Pack");
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("1234567890").withPack("Invalid-Pack").withChannel(Channel.IVR.name()).build();

        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidChannelIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid channel Invalid-Channel");
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel("Invalid-Channel").withCreatedAt(DateTime.now()).build();

        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid msisdn 12345");
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("12345").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel(Channel.IVR.name()).build();

        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid msisdn 123456789a");
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withMsisdn("123456789a").withPack(SubscriptionPack.TWELVE_MONTHS.name()).withChannel(Channel.IVR.name()).build();

        subscriptionRequest.validate();
    }


    @Test
    public void shouldThrowExceptionWhenNonNumericAgeIsGivenToCreateNewSubscriptionForCC() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid beneficiary age 1a");
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "1a", "NAME", "21-01-2011", "21-01-2011", "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionWhenNoAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "", "NAME", "21-01-2011", "21-01-2011", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), null, "NAME", "21-01-2011", "21-01-2011", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidDOBIsGivenToCreateNewSubscriptionForCC() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid date of birth 21-21-11");
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-21-11", "21-01-2011", "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionWhenEmptyDOBIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "", "21-01-2011", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", null, "21-01-2011", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldThrowExceptionWhenInvalidEDDIsGivenToCreateNewSubscriptionForCC() {
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid expected date of delivery 21-21-11");
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-12-2012", "21-21-11", "mydistrict", "myblock", "mypanchayat", DateTime.now());

        subscriptionRequest.validate();
    }

    @Test
    public void shouldNotThrowExceptionWhenEmptyEDDIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionRequest subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-12-2012", "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();

        subscriptionRequest = createSubscriptionRequest("1234567890", SubscriptionPack.TWELVE_MONTHS.name(), Channel.CALL_CENTER.name(), "122", "NAME", "21-12-2012", null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        subscriptionRequest.validate();
    }

    @Test
    public void shouldConvertAgeToIntegerAndReturn() {
        assertEquals(12, new SubscriptionRequestBuilder().withDefaults().withBeneficiaryAge("12").build().getBeneficiaryAge());
        assertEquals(0, new SubscriptionRequestBuilder().withDefaults().withBeneficiaryAge(null).build().getBeneficiaryAge());
        assertEquals(0, new SubscriptionRequestBuilder().withDefaults().withBeneficiaryAge("").build().getBeneficiaryAge());
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

    private SubscriptionRequest createSubscriptionRequest(String msisdn, String pack, String channel, String age, String name, String dob, String edd, String district, String block, String panchayat, DateTime createdAt) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults()
                .withPack(pack).withChannel(channel).withMsisdn(msisdn).withBeneficiaryAge(age)
                .withBeneficiaryName(name).withDOB(dob).withEDD(edd).withDistrict(district).withBlock(block).withPanchayat(panchayat).withCreatedAt(createdAt).build();

        return subscriptionRequest;
    }
}
