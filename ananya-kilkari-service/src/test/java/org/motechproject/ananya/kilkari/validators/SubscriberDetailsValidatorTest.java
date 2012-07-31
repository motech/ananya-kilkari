package org.motechproject.ananya.kilkari.validators;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.kilkari.request.SubscriberUpdateWebRequest;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubscriberDetailsValidatorTest {
    private SubscriberDetailsValidator subscriberDetailsValidator;

    @Before
    public void setUp() {
        subscriberDetailsValidator = new SubscriberDetailsValidator();
    }

    @Test
    public void shouldReturnErrorsForInvalidSubscriberDetailsRequest() {
        SubscriberUpdateWebRequest subscriberUpdateWebRequest = new SubscriberUpdateWebRequest();
        subscriberUpdateWebRequest.setBeneficiaryAge("23a");
        subscriberUpdateWebRequest.setChannel("invalid-channel");
        subscriberUpdateWebRequest.setCreatedAt(DateTime.now());
        subscriberUpdateWebRequest.setDateOfBirth("20/10/1985");
        String edd = getDate(DateTime.now().plusWeeks(1).toDate());
        subscriberUpdateWebRequest.setExpectedDateOfDelivery(edd);

        Errors errors = subscriberDetailsValidator.validate(subscriberUpdateWebRequest);

        assertEquals(4, errors.getCount());
        assertTrue(errors.hasMessage("Invalid channel invalid-channel"));
        assertTrue(errors.hasMessage("Invalid beneficiary age 23a"));
        assertTrue(errors.hasMessage("Invalid date of birth 20/10/1985"));
        assertTrue(errors.hasMessage("Invalid expected date of delivery " + edd));
    }

    private String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-YYYY");
        return simpleDateFormat.format(date);
    }
}
