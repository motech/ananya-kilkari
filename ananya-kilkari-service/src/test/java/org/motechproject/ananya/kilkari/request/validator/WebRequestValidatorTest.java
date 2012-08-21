package org.motechproject.ananya.kilkari.request.validator;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.subscription.domain.ChangeSubscriptionType;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class WebRequestValidatorTest {
    @Test
    public void shouldReturnFalseWhenInvalidPackIsGiven() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validatePack("Invalid-Pack");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid subscription pack Invalid-Pack"));
    }

    @Test
    public void shouldReturnFalseWhenInvalidReasonIsGiven() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateCampaignChangeReason("Invalid-Reason");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid reason Invalid-Reason"));
    }

    @Test
    public void shouldReturnFalseIfDOBDateFormatIsInvalid() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateDOB("25/11/1986", DateTime.now());

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid date of birth 25/11/1986"));
    }

    @Test
    public void shouldReturnFalseIfDOBDateIsAfterCreatedAt() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateDOB("25/11/1986", new DateTime(1984, 11, 25, 0, 0, 0));

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid date of birth 25/11/1986"));
    }

    @Test
    public void shouldReturnFalseIfEDDDateFormatIsInvalid() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateEDD("25/11/1986", DateTime.now());

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid expected date of delivery 25/11/1986"));
    }

    @Test
    public void shouldReturnFalseIfEDDDateIsBeforeCreatedAt() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateEDD("25-11-1984", new DateTime(1985, 11, 25, 0, 0, 0));

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid expected date of delivery 25-11-1984"));
    }

    @Test
    public void shouldReturnFalseIfBeneficiaryAgeIsNotNumeric() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateAge("25a");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid beneficiary age 25a"));
    }
    
    @Test
    public void shouldReturnErrorForInvalidWeekNumber() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateWeekNumber("25a");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid week number 25a"));
    }

    @Test
    public void shouldReturnErrorForInvalidChannel() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel("Aragorn");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid channel Aragorn"));
    }

    @Test
    public void shouldReturnErrorForInvalidMsisdn() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateMsisdn("8878564");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid msisdn 8878564"));
    }

    @Test
    public void shouldReturnErrorIfMoreThanOneOfDobOrEddOrWeekIsPresent() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateOnlyOneOfEDDOrDOBOrWeekNumberPresent("edd","dob","week");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid request. Only one of expected date of delivery, date of birth and week number should be present"));
    }

    @Test
    public void shouldReturnErrorIfMoreThanOneOfDobOrEddIsPresent() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateOnlyOneOfEDDOrDOBIsPresent("edd", "dob");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid request. Only one of expected date of delivery or date of birth should be present"));
    }

    @Test
    public void shouldReturnErrorIfNoneOfDobOrEddIsPresent() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateExactlyOneOfEDDOrDOBIsPresent(null, "");

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid request. One of expected date of delivery or date of birth should be present"));
    }

    @Test
    public void shouldReturnErrorIfChangeTypeIsChangeScheduleAndNoneOfDobOrEddIsPresent() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChangeType("change schedule", null, null);

        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid request. One of expected date of delivery or date of birth should be present"));
    }

    @Test
    public void shouldValidateSubscriptionPackListForChangeMsisdn() {
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateSubscriptionPacksForChangeMsisdn(Arrays.asList("All"));
        assertEquals(0, webRequestValidator.getErrors().getCount());

        webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateSubscriptionPacksForChangeMsisdn(Arrays.asList("nanhi_kilkari", "choti_kilkari"));
        assertEquals(0, webRequestValidator.getErrors().getCount());

        webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateSubscriptionPacksForChangeMsisdn(Arrays.asList("All", "nanhi_kilkari"));
        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("No other pack allowed when ALL specified"));

        webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateSubscriptionPacksForChangeMsisdn(Arrays.asList("bad_pack", "nanhi_kilkari"));
        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("Invalid subscription pack bad_pack"));

        webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateSubscriptionPacksForChangeMsisdn(new ArrayList<String>());
        assertEquals(1, webRequestValidator.getErrors().getCount());
        assertTrue(webRequestValidator.getErrors().hasMessage("At least one pack should be specified"));
    }
}
