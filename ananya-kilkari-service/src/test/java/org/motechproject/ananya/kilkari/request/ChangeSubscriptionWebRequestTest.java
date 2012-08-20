package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.validators.DateUtils;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeSubscriptionWebRequestTest {
    @Test
    public void shouldValidateChangeSubscriptionRequest() {
        ChangeSubscriptionWebRequest changeSubscriptionWebRequest = new ChangeSubscriptionWebRequest();
        changeSubscriptionWebRequest.setChangeType("wrong-type");
        changeSubscriptionWebRequest.setMsisdn("some-msisdn");
        changeSubscriptionWebRequest.setPack("some-pack");
        changeSubscriptionWebRequest.setChannel("some-channel");
        changeSubscriptionWebRequest.setDateOfBirth("some-dob");
        changeSubscriptionWebRequest.setExpectedDateOfDelivery("some-edd");

        Errors errors = changeSubscriptionWebRequest.validate();

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
        ChangeSubscriptionWebRequest changeSubscriptionWebRequest = new ChangeSubscriptionWebRequest();
        changeSubscriptionWebRequest.setChangeType("change paCK");
        changeSubscriptionWebRequest.setMsisdn("1234567890");
        changeSubscriptionWebRequest.setPack("choti_KilkarI");
        changeSubscriptionWebRequest.setChannel("call_center");
        changeSubscriptionWebRequest.setDateOfBirth(DateUtils.formatDate(DateTime.now().minusYears(1)));
        changeSubscriptionWebRequest.setExpectedDateOfDelivery(null);

        Errors errors = changeSubscriptionWebRequest.validate();

        assertTrue(errors.hasNoErrors());
    }
}
