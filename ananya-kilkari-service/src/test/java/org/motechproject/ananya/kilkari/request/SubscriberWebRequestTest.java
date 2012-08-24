package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubscriberWebRequestTest {
    @Test
    public void shouldReturnErrorsForInvalidSubscriberDetailsRequest() {
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        subscriberWebRequest.setBeneficiaryAge("23a");
        subscriberWebRequest.setChannel("ivr");
        DateTime now = DateTime.now();
        subscriberWebRequest.setCreatedAt(now);

        Errors errors = subscriberWebRequest.validate();

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid beneficiary age 23a"));
    }
}
