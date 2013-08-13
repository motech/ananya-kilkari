package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ActionStatus {
    private CallbackAction action;
    private CallbackStatus status;

    public ActionStatus(CallbackAction action, CallbackStatus status) {
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

    public static ActionStatus createFor(String action, String status) {
        return new ActionStatus(CallbackAction.getFor(action), CallbackStatus.getFor(status));
    }
}
