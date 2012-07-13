package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;

public enum Channel {
    IVR, CALL_CENTER;

    public static boolean isIVR(String channel) {
        return Channel.IVR.name().equalsIgnoreCase(StringUtils.trim(channel));
    }

    public static boolean isCallCenter(String channel) {
        return Channel.CALL_CENTER.name().equalsIgnoreCase(StringUtils.trim(channel));
    }

    public static Channel from(String string) {
        return Channel.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String channel) {
        try {
            from(channel);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

