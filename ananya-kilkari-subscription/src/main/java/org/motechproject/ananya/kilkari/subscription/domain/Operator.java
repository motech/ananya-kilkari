package org.motechproject.ananya.kilkari.subscription.domain;

import org.apache.commons.lang.StringUtils;

public enum Operator {
    AIRTEL, BSNL, IDEA, RELIANCEGSM, VODAFONE, TATADOCOMO;

    public static Operator getFor(String operator) {
        return Operator.valueOf(StringUtils.trimToEmpty(operator).toUpperCase());
    }

    public static boolean isValid(String operator) {
        return (operator != null && Operator.contains(operator));
    }

    private static boolean contains(String value) {
        for (Operator operator : Operator.values()) {
            if (operator.name().equals(StringUtils.trimToEmpty(value).toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
