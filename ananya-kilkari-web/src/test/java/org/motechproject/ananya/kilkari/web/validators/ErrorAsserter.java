package org.motechproject.ananya.kilkari.web.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class ErrorAsserter {
    private Errors errors;

    ErrorAsserter(Errors errors) {
        this.errors = errors;
    }

    public void hasErrors() {
        assertTrue(errors.hasErrors());
    }

    public void hasMessage(String message) {
        assertTrue(errors.allMessages().contains(message));
    }

    public void hasNoErrors() {
        assertTrue(errors.hasNoErrors());
    }

    public void hasErrorCount(int count) {
        assertEquals(count, errors.getCount());
    }
}
