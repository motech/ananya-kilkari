package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import static org.junit.Assert.*;

public class SubscriberWebRequestTest {
    @Test
    public void shouldReturnErrorsForInvalidSubscriberDetailsRequest() {
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        subscriberWebRequest.setBeneficiaryAge("23a");
        subscriberWebRequest.setChannel("random");
        DateTime now = DateTime.now();
        subscriberWebRequest.setCreatedAt(now);

        Errors errors = subscriberWebRequest.validate();

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("Invalid beneficiary age 23a"));
        assertTrue(errors.hasMessage("Invalid channel random"));
        assertTrue(errors.hasMessage("Missing location"));
    }

    @Test
    public void shouldValidateIfLocationHasMissingDetails(){
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        subscriberWebRequest.setChannel("ivr");
        subscriberWebRequest.setLocation(new LocationRequest(){{
            setDistrict("d");
        }});

        Errors errors = subscriberWebRequest.validate();

        assertEquals(2, errors.getCount());
        assertTrue(errors.hasMessage("Missing block"));
        assertTrue(errors.hasMessage("Missing panchayat"));
    }

    @Test
    public void shouldReturnNullLocationIfLocationIsNotProvided(){
        SubscriberWebRequest webRequest = new SubscriberWebRequest();
        assertNull(webRequest.getLocation());
    }

}
