package org.motechproject.ananya.kilkari.obd.domain;

public class RetrySubSlot extends OBDSubSlot {

    public static final RetrySubSlot ONE = new RetrySubSlot("ONE");
    public static final RetrySubSlot TWO = new RetrySubSlot("TWO");
    public static final RetrySubSlot THREE = new RetrySubSlot("THREE");

    private RetrySubSlot(String slotNumber) {
        super("RETRY", slotNumber);
    }
}
