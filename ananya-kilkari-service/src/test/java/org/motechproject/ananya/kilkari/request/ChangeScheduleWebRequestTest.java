package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeScheduleWebRequestTest {
    @Test
    public void shouldValidateChangeScheduleRequest() {
        ChangeScheduleWebRequest changeScheduleWebRequest = new ChangeScheduleWebRequest();
        changeScheduleWebRequest.setChangeType("wrong-type");
        changeScheduleWebRequest.setMsisdn("some-msisdn");
        changeScheduleWebRequest.setPack("some-pack");
        changeScheduleWebRequest.setChannel("some-channel");
        changeScheduleWebRequest.setDateOfBirth("some-dob");
        changeScheduleWebRequest.setExpectedDateOfDelivery("some-edd");

        Errors errors = changeScheduleWebRequest.validate();

        assertTrue(errors.hasErrors());
        assertEquals(7, errors.getCount());
        assertTrue(errors.hasMessage("Invalid msisdn some-msisdn"));
        assertTrue(errors.hasMessage("Invalid subscription pack some-pack"));
        assertTrue(errors.hasMessage("Invalid channel some-channel"));
        assertTrue(errors.hasMessage("Invalid date of birth some-dob"));
        assertTrue(errors.hasMessage("Invalid expected date of delivery some-edd"));
        assertTrue(errors.hasMessage("Invalid request. Only one of expected date of delivery or date of birth should be present"));
        assertTrue(errors.hasMessage("Invalid change type wrong-type"));
    }

    @Test
    public void shouldValidateValidChangePackRequest() {
        ChangeScheduleWebRequest changeScheduleWebRequest = new ChangeScheduleWebRequest();
        changeScheduleWebRequest.setChangeType("change paCK");
        changeScheduleWebRequest.setMsisdn("1234567890");
        changeScheduleWebRequest.setPack("choti_KilkarI");
        changeScheduleWebRequest.setChannel("call_center");
        changeScheduleWebRequest.setDateOfBirth(DateUtils.formatDate(DateTime.now().minusYears(1)));
        changeScheduleWebRequest.setExpectedDateOfDelivery(null);

        Errors errors = changeScheduleWebRequest.validate();

        assertTrue(errors.hasNoErrors());
    }
}
