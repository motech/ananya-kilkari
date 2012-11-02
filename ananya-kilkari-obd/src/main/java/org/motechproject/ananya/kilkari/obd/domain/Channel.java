package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public enum Channel {
    IVR("IVR"), CONTACT_CENTER("CC"), MOTECH("MOTECH");
    private String omsmName;

    Channel(String name) {
        this.omsmName = name;
    }

    public String getOMSMName() {
        return omsmName;
    }

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

