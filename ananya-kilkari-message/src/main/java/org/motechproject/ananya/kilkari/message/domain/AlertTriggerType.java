package org.motechproject.ananya.kilkari.message.domain;

public enum AlertTriggerType {

    ACTIVATION, RENEWAL, WEEKLY_MESSAGE;

    public boolean isActivation() {
        return this == AlertTriggerType.ACTIVATION;
    }
}
