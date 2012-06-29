package org.motechproject.ananya.kilkari.web.domain;

import org.apache.commons.lang.StringUtils;

public enum CallbackAction {
    ACT;

    public static CallbackAction getFor(String action) {
        return CallbackAction.valueOf(StringUtils.trimToEmpty(action).toUpperCase());
    }

    public static boolean isValid(String callbackAction) {
        return (callbackAction != null && CallbackAction.contains(callbackAction));
    }

    private static boolean contains(String value) {
        for (CallbackAction callbackAction : CallbackAction.values()) {
            if (callbackAction.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
