package org.motechproject.ananya.kilkari.subscription.domain;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChangeSubscriptionTypeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnChangeType() {
        assertEquals(ChangeSubscriptionType.CHANGE_PACK, ChangeSubscriptionType.from("change pack"));
        assertEquals(ChangeSubscriptionType.CHANGE_SCHEDULE, ChangeSubscriptionType.from("change schedule"));
    }

    @Test
    public void shouldThrowExceptionWhileGettingWrongChangeType() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Wrong change type wrong change");
        ChangeSubscriptionType.from("wrong change");
    }

    @Test
    public void shouldValidateChangeType() {
        assertFalse(ChangeSubscriptionType.isValid("Wrong type"));
        assertFalse(ChangeSubscriptionType.isValid(null));
        assertFalse(ChangeSubscriptionType.isValid(""));
        assertFalse(ChangeSubscriptionType.isValid("change  pack"));

        assertTrue(ChangeSubscriptionType.isValid("change pack"));
        assertTrue(ChangeSubscriptionType.isValid("Change pAck"));
        assertTrue(ChangeSubscriptionType.isValid("change schedule"));
    }
}
