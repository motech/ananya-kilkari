package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeSubscriptionWebRequestTest {
    @Test
    public void shouldValidateChangeSubscriptionRequest() {
        ChangeSubscriptionWebRequest changeSubscriptionWebRequest = new ChangeSubscriptionWebRequest();
        changeSubscriptionWebRequest.setChangeType("wrong-type");
        changeSubscriptionWebRequest.setPack("some-pack");
        changeSubscriptionWebRequest.setChannel("some-channel");
        changeSubscriptionWebRequest.setDateOfBirth("some-dob");
        changeSubscriptionWebRequest.setExpectedDateOfDelivery("some-edd");

        Errors errors = changeSubscriptionWebRequest.validate();

        assertTrue(errors.hasErrors());
        assertEquals(6, errors.getCount());
        assertTrue(errors.hasMessage("Invalid subscription pack some-pack"));
        assertTrue(errors.hasMessage("Invalid channel some-channel"));
        assertTrue(errors.hasMessage("Invalid date of birth some-dob"));
        assertTrue(errors.hasMessage("Invalid expected date of delivery some-edd"));
        assertTrue(errors.hasMessage("Invalid request. Only one of expected date of delivery or date of birth should be present"));
        assertTrue(errors.hasMessage("Invalid change type wrong-type"));
    }

    @Test
    public void shouldValidateValidChangePackRequest() {
        ChangeSubscriptionWebRequest changeSubscriptionWebRequest = new ChangeSubscriptionWebRequest();
        changeSubscriptionWebRequest.setChangeType("change_paCK");
        changeSubscriptionWebRequest.setPack("navjaat_KilkarI");
        changeSubscriptionWebRequest.setChannel("CONTACT_CENTER");
        changeSubscriptionWebRequest.setDateOfBirth(DateUtils.formatDate(DateTime.now().minusYears(1), org.joda.time.tz.FixedDateTimeZone.forOffsetHoursMinutes(5, 30)));
        changeSubscriptionWebRequest.setExpectedDateOfDelivery(null);

        Errors errors = changeSubscriptionWebRequest.validate();

        assertTrue(errors.hasNoErrors());
    }
}
