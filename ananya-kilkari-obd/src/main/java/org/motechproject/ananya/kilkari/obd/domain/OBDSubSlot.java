package org.motechproject.ananya.kilkari.obd.domain;

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

    @Override
    public String toString() {
        return "OBDSubSlot{" +
                "slotName='" + getSlotName() +
                "'}";
    }
}

