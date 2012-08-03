package org.motechproject.ananya.kilkari.request;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;

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
        subscriberWebRequest.setDateOfBirth("20/10/1985");
        String edd = getDate(now.minusWeeks(1).toDate());
        subscriberWebRequest.setExpectedDateOfDelivery(edd);

        Errors errors = subscriberWebRequest.validate();

        assertEquals(5, errors.getCount());
        assertTrue(errors.hasMessage("Invalid channel ivr"));
        assertTrue(errors.hasMessage("Invalid beneficiary age 23a"));
        assertTrue(errors.hasMessage("Invalid date of birth 20/10/1985"));
        assertTrue(errors.hasMessage("Invalid expected date of delivery " + edd));
        assertTrue(errors.hasMessage("Invalid request. Only one of expected date of delivery or date of birth should be present"));
    }

    private String getDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date);
    }

}
