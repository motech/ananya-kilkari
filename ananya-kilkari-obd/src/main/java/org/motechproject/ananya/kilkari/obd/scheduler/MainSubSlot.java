package org.motechproject.ananya.kilkari.obd.scheduler;

public class MainSubSlot extends OBDSubSlot {

    public static final MainSubSlot ONE = new MainSubSlot("ONE");
    public static final MainSubSlot TWO = new MainSubSlot("TWO");
    public static final MainSubSlot THREE = new MainSubSlot("THREE");

    private MainSubSlot(String slotNumber) {
        super("MAIN", slotNumber);
    }
}
