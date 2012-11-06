package org.motechproject.ananya.kilkari.request;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

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
    public void shouldValidateInvalidChangeMsisdnWebRequestWithSameMsisdn() {
        ChangeMsisdnWebRequest changeMsisdnWebRequest = new ChangeMsisdnWebRequest();
        changeMsisdnWebRequest.setChannel("CONTACT_CENTER");
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
        changeMsisdnWebRequest.setPacks(new ArrayList<String>() {{
            add("NANHI_KILKARI");
        }});

        Errors errors = changeMsisdnWebRequest.validate();

        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldValidateSubscriptionPackListForChangeMsisdn() {
        String oldMsisdn = "9876543210";
        String newMsisdn = "9876543211";
        ChangeMsisdnWebRequest request = new ChangeMsisdnWebRequest(oldMsisdn, newMsisdn, null, Channel.CONTACT_CENTER.toString());

        request.setPacks(Arrays.asList("All"));
        Errors errors = request.validate();
        assertEquals(0, errors.getCount());

        request.setPacks(Arrays.asList("nanhi_kilkari", "navjaat_kilkari"));
        errors = request.validate();
        assertEquals(0, errors.getCount());

        request.setPacks(Arrays.asList("All", "nanhi_kilkari"));
        errors = request.validate();
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("No other pack allowed when ALL specified"));

        request.setPacks(Arrays.asList("bad_pack", "nanhi_kilkari"));
        errors = request.validate();
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Invalid subscription pack bad_pack"));

        request.setPacks(new ArrayList<String>());
        errors = request.validate();
        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("At least one pack should be specified"));
    }

    @Test
    public void shouldReturnErrorIfOldAndNewMsisdnAreSameForChangeMsisdn() {
        String msisdn = "9876543210";
        ChangeMsisdnWebRequest request = new ChangeMsisdnWebRequest(msisdn, msisdn, Arrays.asList("all"), Channel.CONTACT_CENTER.toString());

        Errors errors = request.validate();

        assertEquals(1, errors.getCount());
        assertTrue(errors.hasMessage("Old and new msisdn cannot be same"));
    }
}
