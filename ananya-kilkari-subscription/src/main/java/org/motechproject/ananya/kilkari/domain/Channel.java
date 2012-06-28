package org.motechproject.ananya.kilkari.domain;

import org.apache.commons.lang.StringUtils;

public enum Channel {
    IVR;

    public static Channel getFor(String pack) {
        return Channel.valueOf(StringUtils.trimToEmpty(pack).toUpperCase());
    }

    public static boolean isValid(String channel) {
        return (channel != null && Channel.contains(channel));
    }

    private static boolean contains(String value) {
        for (Channel channel : Channel.values()) {
            if (channel.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}