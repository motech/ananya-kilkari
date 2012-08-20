package org.motechproject.ananya.kilkari.subscription.domain;

public enum ChangeType {
    CHANGE_PACK("change pack"), CHANGE_SCHEDULE("change schedule");
    private String description;

    ChangeType(String description) {

        this.description = description;
    }

    public static ChangeType from(String changeType) {
        for (ChangeType type : ChangeType.values()) {
            if(type.description.equalsIgnoreCase(changeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("Wrong change type %s", changeType));
    }

    public static boolean isValid(String changeType) {
       try {
            from(changeType);
           return true;
       } catch (IllegalArgumentException iae) {
           return false;
       }
    }
}
