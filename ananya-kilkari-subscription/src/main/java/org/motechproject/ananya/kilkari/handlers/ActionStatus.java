package org.motechproject.ananya.kilkari.handlers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

class ActionStatus {
    private String action;
    private String status;

    ActionStatus(String action, String status) {
        this.action = action;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActionStatus)) return false;

        ActionStatus that = (ActionStatus) o;

        return new EqualsBuilder()
                .append(this.action, that.action)
                .append(this.status, that.status)
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.action)
                .append(this.status)
                .hashCode();
    }
}
