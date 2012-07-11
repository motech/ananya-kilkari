package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;

public enum CallbackAction {
    ACT, REN, DCT;

    public static CallbackAction getFor(String action) {
        final String standardizedAction = StringUtils.trimToEmpty(action).toUpperCase();
        return isValid(standardizedAction) ? CallbackAction.valueOf(standardizedAction) : null;
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
