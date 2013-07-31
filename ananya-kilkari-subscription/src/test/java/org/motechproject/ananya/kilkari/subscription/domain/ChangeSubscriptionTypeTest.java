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
        assertEquals(ChangeSubscriptionType.CHANGE_PACK, ChangeSubscriptionType.from("change_pack"));
        assertEquals(ChangeSubscriptionType.CHANGE_SCHEDULE, ChangeSubscriptionType.from("change_schedule"));
    }

    @Test
    public void shouldValidateChangeType() {
        assertFalse(ChangeSubscriptionType.isValid("Wrong type"));
        assertFalse(ChangeSubscriptionType.isValid(null));
        assertFalse(ChangeSubscriptionType.isValid(""));
        assertFalse(ChangeSubscriptionType.isValid("change _pack"));

        assertTrue(ChangeSubscriptionType.isValid("change_pack  "));
        assertTrue(ChangeSubscriptionType.isValid("Change_pAck"));
        assertTrue(ChangeSubscriptionType.isValid("change_schedule"));
    }

    @Test
    public void shouldCheckIfTypeIsChangePack() {
        assertTrue(ChangeSubscriptionType.isChangePack(ChangeSubscriptionType.CHANGE_PACK));
        assertFalse(ChangeSubscriptionType.isChangePack(ChangeSubscriptionType.CHANGE_SCHEDULE));
    }
}
