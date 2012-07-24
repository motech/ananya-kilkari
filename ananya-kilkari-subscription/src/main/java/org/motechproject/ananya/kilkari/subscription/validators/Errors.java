package org.motechproject.ananya.kilkari.subscription.validators;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Errors {
    private List<String> errorMessages = new ArrayList<>();

    public void add(String messageFormat, Object... args) {
        errorMessages.add(String.format(messageFormat, args));
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    public String allMessages() {
        return StringUtils.join(errorMessages, ",");
    }

    public boolean hasNoErrors() {
        return !hasErrors();
    }

    public void addAll(List<String> strings) {
        errorMessages.addAll(strings);
    }

    public int getCount() {
        return errorMessages.size();
    }

    public boolean hasMessage(String message) {
        return errorMessages.contains(message);
    }
}
