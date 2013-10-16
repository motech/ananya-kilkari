package org.motechproject.ananya.kilkari.domain;

public enum CampaignTriggerType {
    RENEWAL, ACTIVATION, WEEKLY_MESSAGE;

    public boolean isNotActivation() {
        return !this.equals(ACTIVATION);
    }
}
