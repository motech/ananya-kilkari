package org.motechproject.ananya.kilkari.obd.domain;

import org.junit.Test;

import static org.junit.Assert.*;


public class ChannelTest {

    @Test
    public void shouldConvertStringToEnum() {
        assertEquals(Channel.from("ivr"), Channel.IVR);
        assertEquals(Channel.from("IVR"), Channel.IVR);
        assertEquals(Channel.from("IVr"), Channel.IVR);
        assertEquals(Channel.from("contact_center"), Channel.CONTACT_CENTER);
    }

    @Test
    public void shouldReturnFalseIfChannelIsInvalid() {
        assertFalse(Channel.isValid("abc"));
        assertFalse(Channel.isValid(""));
        assertFalse(Channel.isValid(" "));
        assertFalse(Channel.isValid(null));
    }
    @Test
    public void shouldReturnFalseIfChannelIsMotech() {
        assertFalse(Channel.isValid("motech"));
    }

    @Test
    public void shouldReturnTrueIfChannelIsValid() {
        assertTrue(Channel.isValid("ivr"));
        assertTrue(Channel.isValid("IVR"));
        assertTrue(Channel.isValid("IVr"));
        assertTrue(Channel.isValid(" IVr "));
    }

    @Test
    public void shouldReturnTrueIfChannelIsIVR() {
        assertTrue(Channel.isIVR("ivr"));
        assertTrue(Channel.isIVR("IVR"));
        assertTrue(Channel.isIVR("IVr"));
        assertTrue(Channel.isIVR(" IVr "));
    }

    @Test
    public void shouldReturnFalseIfChannelIsNotIVR() {
        assertFalse(Channel.isIVR(""));
        assertFalse(Channel.isIVR(" "));
        assertFalse(Channel.isIVR(null));
        assertFalse(Channel.isIVR("abc"));
        assertFalse(Channel.isIVR("CONTACT_CENTER"));
    }

    @Test
    public void shouldReturnTrueIfChannelIsCallCenter() {
        assertTrue(Channel.isCallCenter("contact_center"));
        assertTrue(Channel.isCallCenter("CONTACT_CENTER"));
        assertTrue(Channel.isCallCenter("CONTACT_center"));
        assertTrue(Channel.isCallCenter(" Contact_center "));
    }

    @Test
    public void shouldReturnFalseIfChannelIsNotCallCenter() {
        assertFalse(Channel.isCallCenter(""));
        assertFalse(Channel.isCallCenter(" "));
        assertFalse(Channel.isCallCenter(null));
        assertFalse(Channel.isCallCenter("abc"));
        assertFalse(Channel.isCallCenter("ivr"));
    }

    @Test
    public void shouldGetOMSMNameForChannel() {
        assertEquals("CC",Channel.CONTACT_CENTER.getOMSMName());
        assertEquals("IVR",Channel.IVR.getOMSMName());
        assertEquals("MOTECH",Channel.MOTECH.getOMSMName());
    }
}
