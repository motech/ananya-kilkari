package org.motechproject.ananya.kilkari.subscription.service.request;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class SubscriptionRequest {
    private String msisdn;
    private DateTime creationDate;
    private SubscriptionPack pack;
    private Location location;
    private Subscriber subscriber;
    private String oldSubscriptionId;
    private String reason;

    public SubscriptionRequest(String msisdn, DateTime creationDate,
                               SubscriptionPack pack, Location location, Subscriber subscriber, String reason) {
        this.msisdn = msisdn;
        this.creationDate = creationDate;
        this.pack = pack;
        this.location = location == null ? Location.NULL : location;
        this.subscriber = subscriber;
        this.reason = reason;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public Location getLocation() {
        return location;
    }

    public Subscriber getSubscriber() {
        return subscriber == null ? Subscriber.NULL : subscriber;
    }

    public String getReason() {
        return reason;
    }

    public boolean hasLocation() {
        return location != Location.NULL;
    }

    public String getOldSubscriptionId() {
        return oldSubscriptionId;
    }

    public void setOldSubscriptionId(String oldSubscriptionId) {
        this.oldSubscriptionId = oldSubscriptionId;
    }

    public DateTime getSubscriptionStartDate() {
        Integer weekNumber = subscriber.getWeek();
        if (weekNumber != null && weekNumber >= 1) {
            return pack.getStartDateForWeek(creationDate, weekNumber);
        }

        DateTime dateOfBirth = subscriber.getDateOfBirth();
        if (dateOfBirth != null) {
            return pack.getStartDate(dateOfBirth);
        }

        DateTime expectedDateOfDelivery = subscriber.getExpectedDateOfDelivery();
        if (expectedDateOfDelivery != null) {
            return pack.getStartDate(expectedDateOfDelivery);
        }

        return creationDate;
    }
}