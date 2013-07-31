package org.motechproject.ananya.kilkari.obd.domain;

import org.apache.commons.lang.StringUtils;

public class PhoneNumber {

    public static boolean isValid(String phoneNumber) {
        return validate(phoneNumber);
    }

    public static boolean isNotValid(String phoneNumber) {
        return !validate(phoneNumber);
    }

    private static boolean validate(String phoneNumber) {
        return StringUtils.isNotBlank(phoneNumber) && StringUtils.isNumeric(phoneNumber) && (phoneNumber.length() == 10);
    }
}
