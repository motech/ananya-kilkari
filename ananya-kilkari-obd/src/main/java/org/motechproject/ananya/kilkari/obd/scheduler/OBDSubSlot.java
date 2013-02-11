package org.motechproject.ananya.kilkari.obd.scheduler;

import java.io.Serializable;

public abstract class OBDSubSlot implements Serializable {
    private static final long serialVersionUID = -2132501809034108670L;

    protected String slotNumber;
    protected String name;

    protected OBDSubSlot(String name, String slotNumber) {
        this.name = name;
        this.slotNumber = slotNumber;
    }

    public String getSlotName() {
        return name + "." + slotNumber;
    }
}

