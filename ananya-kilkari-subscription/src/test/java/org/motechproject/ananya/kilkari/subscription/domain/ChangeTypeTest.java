package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangeTypeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnChangeType() {
        assertEquals(ChangeType.CHANGE_PACK, ChangeType.from("change pack"));
        assertEquals(ChangeType.CHANGE_SCHEDULE, ChangeType.from("change schedule"));
    }

    @Test
    public void shouldThrowExceptionWhileGettingWrongChangeType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Wrong change type wrong change");
        ChangeType.from("wrong change");
    }

    @Test
    public void shouldValidateChangeType() {
        assertFalse(ChangeType.isValid("Wrong type"));
        assertFalse(ChangeType.isValid(null));
        assertFalse(ChangeType.isValid(""));
        assertFalse(ChangeType.isValid("change  pack"));

        assertTrue(ChangeType.isValid("change pack"));
        assertTrue(ChangeType.isValid("Change pAck"));
        assertTrue(ChangeType.isValid("change schedule"));
    }
}
