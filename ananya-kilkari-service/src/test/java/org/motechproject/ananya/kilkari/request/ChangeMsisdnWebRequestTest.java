package org.motechproject.ananya.kilkari.request;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangeMsisdnWebRequestTest {
    @Test
    public void shouldValidateInvalidChangeMsisdnWebRequest() {
        ChangeMsisdnWebRequest changeMsisdnWebRequest = new ChangeMsisdnWebRequest();
        changeMsisdnWebRequest.setChannel("InvalidChannel");
        changeMsisdnWebRequest.setOldMsisdn("InvalidMsisdn1");
        changeMsisdnWebRequest.setNewMsisdn("InvalidMsisdn2");
        changeMsisdnWebRequest.setPacks(Collections.EMPTY_LIST);

        Errors errors = changeMsisdnWebRequest.validate();

        assertTrue(errors.hasErrors());
        assertEquals(4, errors.getCount());
    }

    @Test
    public void shouldValidateInvalidChangeMsisdnWebRequestWithSameMsisdn(){
        ChangeMsisdnWebRequest changeMsisdnWebRequest = new ChangeMsisdnWebRequest();
        changeMsisdnWebRequest.setChannel("call_center");
        changeMsisdnWebRequest.setOldMsisdn("9876543210");
        changeMsisdnWebRequest.setNewMsisdn("9876543210");
        changeMsisdnWebRequest.setPacks(Arrays.asList("all"));

        Errors errors = changeMsisdnWebRequest.validate();

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Old and new msisdn cannot be same"));
    }

    @Test
    public void shouldValidateChangeMsisdnWebRequest() {
        ChangeMsisdnWebRequest changeMsisdnWebRequest = new ChangeMsisdnWebRequest();
        changeMsisdnWebRequest.setChannel("IVR");
        changeMsisdnWebRequest.setOldMsisdn("1234567890");
        changeMsisdnWebRequest.setNewMsisdn("1234456789");
        changeMsisdnWebRequest.setPacks(new ArrayList<String>(){{add("NANHI_KILKARI");}});

        Errors errors = changeMsisdnWebRequest.validate();

        assertFalse(errors.hasErrors());
    }
}
