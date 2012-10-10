package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public enum Channel {
    IVR, CONTACT_CENTER, MOTECH;

    public static boolean isIVR(String channel) {
        return Channel.IVR.name().equalsIgnoreCase(StringUtils.trim(channel));
    }

    public static boolean isCallCenter(String channel) {
        return Channel.CONTACT_CENTER.name().equalsIgnoreCase(StringUtils.trim(channel));
    }

    public static Channel from(String string) {
        return Channel.valueOf(StringUtils.trimToEmpty(string).toUpperCase());
    }

    public static boolean isValid(String channel) {
        try {
            Channel channelValue = from(channel);
            return channelValue != MOTECH;
        } catch (Exception e) {
            return false;
        }
    }
}

