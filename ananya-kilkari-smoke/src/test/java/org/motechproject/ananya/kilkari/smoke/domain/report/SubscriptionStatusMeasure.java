package org.motechproject.ananya.kilkari.smoke.domain.report;

public class SubscriptionStatusMeasure {
    private String msisdn;
    private String status;
    private String pack;
    private String channel;
    private String name;
    private String age;
    private String estimatedDateOfDelivery;
    private String dateOfBirth;

    public SubscriptionStatusMeasure(String msisdn, String status, String pack, String channel, String name, String age, String estimatedDateOfDelivery, String dateOfBirth) {
        this.msisdn = msisdn;
        this.status = status;
        this.pack = pack;
        this.channel = channel;
        this.name = name;
        this.age = age;
        this.estimatedDateOfDelivery = estimatedDateOfDelivery;
        this.dateOfBirth = dateOfBirth;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getStatus() {
        return status;
    }

    public String getPack() {
        return pack;
    }

    public String getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getEstimatedDateOfDelivery() {
        return estimatedDateOfDelivery;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
