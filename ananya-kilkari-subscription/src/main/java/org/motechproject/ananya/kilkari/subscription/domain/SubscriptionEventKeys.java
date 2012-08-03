package org.motechproject.ananya.kilkari.subscription.domain;

public class SubscriptionEventKeys {
    public static final String CREATE_SUBSCRIPTION = "org.motechproject.ananya.kilkari.domain.create.subscription";
    public static final String ACTIVATE_SUBSCRIPTION = "org.motechproject.ananya.kilkari.domain.process.osm.activate.subscription";
    public static final String DEACTIVATE_SUBSCRIPTION = "org.motechproject.ananya.kilkari.domain.process.osm.deactivate.subscription";
    public static final String PROCESS_CALLBACK_REQUEST = "org.motechproject.ananya.kilkari.domain.process.subscription.subscriptionCallback.request";
    public static final String PROCESS_SUBSCRIBER_CARE_REQUEST = "org.motechproject.ananya.kilkari.domain.process.subscriber.care.request";
    public static final String SUBSCRIPTION_COMPLETE = "org.motechproject.ananya.kilkari.domain.process.subscription.complete";
    public static final String EARLY_SUBSCRIPTION = "org.motechproject.ananya.kilkari.domain.schedule.early.subscription";
}
