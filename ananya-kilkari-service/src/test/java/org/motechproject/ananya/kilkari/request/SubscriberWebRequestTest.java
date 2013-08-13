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
        subscriberWebRequest.setBeneficiaryAge("22");
        subscriberWebRequest.setChannel("ivr");
        subscriberWebRequest.setLocation(new LocationRequest(){{
            setDistrict("d");
        }});

        Errors errors = subscriberWebRequest.validate();

        assertEquals(3, errors.getCount());
        assertTrue(errors.hasMessage("Missing state"));
        assertTrue(errors.hasMessage("Missing block"));
        assertTrue(errors.hasMessage("Missing panchayat"));
    }

    @Test
    public void shouldReturnNullLocationIfLocationIsNotProvided(){
        SubscriberWebRequest webRequest = new SubscriberWebRequest();
        assertNull(webRequest.getLocation());
    }


    @Test
    public void shouldUpdateStateWithDefaultValue_whenStateIsNull() {
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        LocationRequest locationWithNoState = new LocationRequest();
        subscriberWebRequest.setLocation(locationWithNoState);

        subscriberWebRequest.defaultState("BIHAR");
        assertEquals("BIHAR", subscriberWebRequest.getLocation().getState());
    }

    @Test
    public void shouldNotUpdateStateWithDefaultValue_whenStateIsNotNull() {
        String validState = "ORISSA";
        SubscriberWebRequest subscriberWebRequest = new SubscriberWebRequest();
        LocationRequest locationWithState = new LocationRequest();
        locationWithState.setState(validState);
        subscriberWebRequest.setLocation(locationWithState);

        subscriberWebRequest.defaultState("BIHAR");
        assertEquals(validState, subscriberWebRequest.getLocation().getState());
    }

}
