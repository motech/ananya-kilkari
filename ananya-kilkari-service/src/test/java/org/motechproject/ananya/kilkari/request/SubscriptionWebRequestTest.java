package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.ananya.kilkari.builder.SubscriptionWebRequestBuilder;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.request.Location;

import static org.junit.Assert.*;

public class SubscriptionWebRequestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    private void validateErrors(int size, Errors errors, String... msgs) {
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

        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI.name(),
                Channel.IVR.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);
        assertEquals(SubscriptionPack.BARI_KILKARI.name(), subscriptionWebRequest.getPack());
        assertEquals(Channel.IVR.name(), subscriptionWebRequest.getChannel());
        assertEquals("1234567890", subscriptionWebRequest.getMsisdn());
        assertEquals("1234567890", subscriptionWebRequest.getMsisdn());
        assertEquals("myname", subscriptionWebRequest.getBeneficiaryName());
        assertEquals(12, (int) subscriptionWebRequest.getBeneficiaryAge());
        assertEquals(dateTimeFormatter.parseDateTime(dob), subscriptionWebRequest.getDateOfBirth());
        assertEquals(dateTimeFormatter.parseDateTime(edd), subscriptionWebRequest.getExpectedDateOfDelivery());

        assertEquals(new Location("mydistrict", "myblock", "mypanchayat"), subscriptionWebRequest.getLocation());
        assertEquals(createdAt, subscriptionWebRequest.getCreatedAt());
    }

    @Test
    public void shouldNotValidateAgeDOBEDDForIVR() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("9876543210")
                .withChannel(Channel.IVR.name()).withPack(SubscriptionPack.BARI_KILKARI.name()).build();

        validateErrors(0, subscriptionWebRequest.validate());
    }

    @Test
    public void shouldNotAddErrorWhenGivenSubscriptionDetailsAreAllValid() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.BARI_KILKARI.name()).withChannel(Channel.IVR.name()).build();
        validateErrors(0, subscriptionWebRequest.validate());

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI.name(), Channel.IVR.name(), "12", "myname", "01-11-2013", "04-11-2016", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI.name(), Channel.IVR.name(), null, null, null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI.name(), Channel.IVR.name(), "", "", "", "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
         
        validateErrors(0, subscriptionWebRequest.validate());
    }

    @Test
    public void shouldAddErrorWhenInvalidPackIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("1234567890").withPack("Invalid-Pack").withChannel(Channel.IVR.name()).build();
        validateErrors(1, subscriptionWebRequest.validate(), "Invalid subscription pack Invalid-Pack");
    }

    @Test
    public void shouldAddErrorWhenMoreThanOneOfEddDobOrWeekNumberIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequestWithDobEdd = new SubscriptionWebRequestBuilder().withDefaults().withDOB("01-01-2012").withEDD("31-12-2012").build();
        SubscriptionWebRequest subscriptionWebRequestWithEddWeek = new SubscriptionWebRequestBuilder().withDefaults().withWeek("4").withEDD("31-12-2012").build();
        SubscriptionWebRequest subscriptionWebRequestWithDobWeek = new SubscriptionWebRequestBuilder().withDefaults().withWeek("4").withDOB("31-12-2011").build();
        SubscriptionWebRequest subscriptionWebRequestWithDobEddWeek = new SubscriptionWebRequestBuilder().withDefaults().withWeek("4").withDOB("31-12-2011").withEDD("31-12-2012").build();

        validateErrors(1, subscriptionWebRequestWithDobEdd.validate(), "Invalid request. Only one of expected date of delivery, date of birth and week number should be present");

        validateErrors(1, subscriptionWebRequestWithEddWeek.validate(), "Invalid request. Only one of expected date of delivery, date of birth and week number should be present");

        validateErrors(1, subscriptionWebRequestWithDobWeek.validate(), "Invalid request. Only one of expected date of delivery, date of birth and week number should be present");

        validateErrors(1, subscriptionWebRequestWithDobEddWeek.validate(), "Invalid request. Only one of expected date of delivery, date of birth and week number should be present");
    }

    @Test
    public void shouldAddErrorWhenInvalidChannelIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("1234567890").withPack(SubscriptionPack.NAVJAAT_KILKARI.name()).withChannel("Invalid-Channel").withCreatedAt(DateTime.now()).build();

        validateErrors(2, subscriptionWebRequest.validate(), "Invalid channel Invalid-Channel");
    }

    @Test
    public void shouldAddErrorWhenInvalidMsisdnNumberIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("12345").withPack(SubscriptionPack.NAVJAAT_KILKARI.name()).withChannel(Channel.IVR.name()).build();

        validateErrors(1, subscriptionWebRequest.validate(), "Invalid msisdn 12345");
    }

    @Test
    public void shouldAddErrorWhenNonNumericMsisdnNumberIsGivenToCreateNewSubscription() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withMsisdn("123456789a").withPack(SubscriptionPack.NAVJAAT_KILKARI.name()).withChannel(Channel.IVR.name()).build();

        validateErrors(1, subscriptionWebRequest.validate(), "Invalid msisdn 123456789a");
    }


    @Test
    public void shouldAddErrorWhenNonNumericAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "1a", "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());

        validateErrors(1, subscriptionWebRequest.validate(), "Invalid beneficiary age 1a");
    }

    @Test
    public void shouldNotAddErrorWhenNoAgeIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "", "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), null, "NAME", null, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());
    }

    @Test
    public void shouldAddErrorWhenInvalidDOBIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "122", "NAME", "21-21-11", null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(1, subscriptionWebRequest.validate(), "Invalid date of birth 21-21-11");
    }

    @Test
    public void shouldNotAddErrorWhenEmptyDOBIsGivenToCreateNewSubscriptionForCC() {
        String edd = DateTime.now().plusDays(2).toString("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "122", "NAME", "", edd, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "122", "NAME", null, edd, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());
    }

    @Test
    public void shouldAddErrorWhenInvalidEDDIsGivenToCreateNewSubscriptionForCC() {
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "122", "NAME", null, "21-21-11", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(1, subscriptionWebRequest.validate(), "Invalid expected date of delivery 21-21-11");
    }

    @Test
    public void shouldNotAddErrorWhenEmptyEDDIsGivenToCreateNewSubscriptionForCC() {
        String dob = DateTime.now().minusDays(1).toString("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "122", "NAME", dob, "", "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());

        subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.NAVJAAT_KILKARI.name(), Channel.CONTACT_CENTER.name(), "122", "NAME", dob, null, "mydistrict", "myblock", "mypanchayat", DateTime.now());
        validateErrors(0, subscriptionWebRequest.validate());
    }

    @Test
    public void shouldConvertAgeToIntegerAndReturn() {
        assertEquals(12, (int) new SubscriptionWebRequestBuilder().withDefaults().withBeneficiaryAge("12").build().getBeneficiaryAge());
        assertEquals(null, new SubscriptionWebRequestBuilder().withDefaults().withBeneficiaryAge(null).build().getBeneficiaryAge());
        assertEquals(null, new SubscriptionWebRequestBuilder().withDefaults().withBeneficiaryAge("").build().getBeneficiaryAge());
    }

    @Test
    public void shouldAddErrorWhenDOBIsGreaterThanSubscriptionDate() {
        DateTime createdAt = DateTime.now();
        String dob = createdAt.plusMonths(4).toString("dd-MM-yyyy");
        String edd = null;
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI.name(),
                Channel.CONTACT_CENTER.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);
        validateErrors(1, subscriptionWebRequest.validate(), "Invalid date of birth " + dob);
    }

    @Test
    public void shouldAddErrorWhenEDDIsLesserThanSubscriptionDate() {
        DateTime createdAt = DateTime.now();
        String dob = null;
        String edd = createdAt.minusMonths(4).toString("dd-MM-yyyy");
        SubscriptionWebRequest subscriptionWebRequest = createSubscriptionRequest("1234567890", SubscriptionPack.BARI_KILKARI.name(),
                Channel.CONTACT_CENTER.name(), "12", "myname", dob, edd, "mydistrict", "myblock", "mypanchayat", createdAt);
        validateErrors(1, subscriptionWebRequest.validate(), "Invalid expected date of delivery " + edd);
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
        validateErrors(3, subscriptionWebRequest.validate(), "Invalid msisdn 12345", "Invalid subscription pack invalid pack", "Invalid channel invalid channel");
    }

    @Test
    public void shouldNotFailValidationIfWeekNumberIsBlank() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withWeek("").build();

        validateErrors(0, subscriptionWebRequest.validate());
    }

    @Test
    public void shouldFailValidationIfWeekNumberIsNotANumber() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withWeek("a").build();
        validateErrors(1, subscriptionWebRequest.validate(), "Invalid week number a");
    }

    @Test
    public void shouldThrowExceptionWhenChannelIsInvalid() {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults().withChannel("invalid channel").build();
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid channel invalid channel");

        subscriptionWebRequest.validateChannel();
    }

    @Test
    public void shouldReturnNullLocationIfLocationIsNotProvided(){
        SubscriptionWebRequest webRequest = new SubscriptionWebRequestBuilder().withDefaults().withLocation(null).build();
        assertNull(webRequest.getLocation());
    }

    @Test
    public void shouldValidateLocationIfProvided() {
        SubscriptionWebRequest webRequest = new SubscriptionWebRequestBuilder().withDefaults().withChannel("contact_center").withLocation(new LocationRequest()).build();
        validateErrors(3, webRequest.validate(), "Missing district", "Missing block", "Missing panchayat");
    }

    @Test
    public void shouldValidateLocationIfNotProvided() {
        SubscriptionWebRequest webRequest = new SubscriptionWebRequestBuilder().withDefaults().withChannel("contact_center").withLocation(null).build();
        validateErrors(1, webRequest.validate(), "Missing location");
    }

    @Test
    public void shouldNotValidateLocationForRequestFromIVR(){
        SubscriptionWebRequest webRequest = new SubscriptionWebRequestBuilder().withDefaults().withChannel("ivr").withLocation(new LocationRequest()).build();
        validateErrors(0, webRequest.validate());
    }

    private SubscriptionWebRequest createSubscriptionRequest(String msisdn, String pack, String channel, String age, String name, String dob, String edd, String district, String block, String panchayat, DateTime createdAt) {
        SubscriptionWebRequest subscriptionWebRequest = new SubscriptionWebRequestBuilder().withDefaults()
                .withPack(pack).withChannel(channel).withMsisdn(msisdn).withBeneficiaryAge(age)
                .withBeneficiaryName(name).withDOB(dob).withEDD(edd).withLocation(district, block, panchayat).withCreatedAt(createdAt).build();

        return subscriptionWebRequest;
    }
}
