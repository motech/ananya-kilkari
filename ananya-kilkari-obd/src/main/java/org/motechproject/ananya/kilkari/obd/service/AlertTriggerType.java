package org.motechproject.ananya.kilkari.obd.service;

public enum AlertTriggerType {

    ACTIVATION, RENEWAL, WEEKLY_MESSAGE;

    public boolean isActivation() {
        return this == AlertTriggerType.ACTIVATION;
    }
}
