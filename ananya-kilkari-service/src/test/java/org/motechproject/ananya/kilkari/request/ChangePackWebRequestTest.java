package org.motechproject.ananya.kilkari.request;

import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import static org.junit.Assert.assertTrue;

public class ChangePackWebRequestTest {
    @Test
    public void shouldValidateChangePackRequest() {
        ChangePackWebRequest changePackWebRequest = new ChangePackWebRequest();
        changePackWebRequest.setMsisdn("some-msisdn");
        changePackWebRequest.setPack("some-pack");
        changePackWebRequest.setChannel("some-channel");
        changePackWebRequest.setDateOfBirth("some-dob");
        changePackWebRequest.setExpectedDateOfDelivery("some-edd");

        Errors errors = changePackWebRequest.validate();

        assertTrue(errors.hasErrors());
        assertTrue(errors.hasMessage("Invalid msisdn some-msisdn"));
        assertTrue(errors.hasMessage("Invalid subscription pack some-pack"));
        assertTrue(errors.hasMessage("Invalid channel some-channel"));
        assertTrue(errors.hasMessage("Invalid request. One of expected date of delivery or date of birth should be present"));
    }
}
