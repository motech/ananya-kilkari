package org.motechproject.ananya.kilkari.request;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UnSubscriptionWebRequestTest {
    @Test
    public void shouldValidateUnsubscriptionWebRequest() {
        UnSubscriptionWebRequest unSubscriptionWebRequest = new UnSubscriptionWebRequest();

        unSubscriptionWebRequest.setChannel("InvalidChannel");
        Errors errors = unSubscriptionWebRequest.validate();
        assertFalse(errors.hasNoErrors());
        assertEquals(1, errors.getCount());

        unSubscriptionWebRequest.setChannel("IVR");
        errors = unSubscriptionWebRequest.validate();
        assertTrue(errors.hasNoErrors());

        unSubscriptionWebRequest.setChannel("CALL_CENTER");
        errors = unSubscriptionWebRequest.validate();
        assertTrue(errors.hasNoErrors());
    }
}
