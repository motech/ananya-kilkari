package org.motechproject.ananya.kilkari.subscription.domain;

public enum ChangeSubscriptionType {
    CHANGE_PACK("change pack"), CHANGE_SCHEDULE("change schedule");
    private String description;

    ChangeSubscriptionType(String description) {

        this.description = description;
    }

    public static ChangeSubscriptionType from(String changeType) {
        for (ChangeSubscriptionType subscriptionType : ChangeSubscriptionType.values()) {
            if(subscriptionType.description.equalsIgnoreCase(changeType)) {
                return subscriptionType;
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
