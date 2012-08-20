package org.motechproject.ananya.kilkari.builder;

import org.motechproject.ananya.kilkari.request.ChangeScheduleWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class ChangeScheduleWebRequestBuilder {

    private ChangeScheduleWebRequest changeScheduleWebRequest;

    public ChangeScheduleWebRequestBuilder() {
        changeScheduleWebRequest = new ChangeScheduleWebRequest();
    }

    public ChangeScheduleWebRequestBuilder withDefaults() {
        changeScheduleWebRequest = new ChangeScheduleWebRequest();
        changeScheduleWebRequest.setChangeType("change pack");
        changeScheduleWebRequest.setPack(SubscriptionPack.BARI_KILKARI.name());
        changeScheduleWebRequest.setChannel(Channel.CALL_CENTER.name());
        changeScheduleWebRequest.setMsisdn("9876543210");
        changeScheduleWebRequest.setDateOfBirth(null);
        changeScheduleWebRequest.setExpectedDateOfDelivery(null);
        changeScheduleWebRequest.setReason("reason for change pack");
        return this;
    }

    public ChangeScheduleWebRequestBuilder withPack(String pack) {
        changeScheduleWebRequest.setPack(pack);
        return this;
    }

    public ChangeScheduleWebRequestBuilder withChannel(String channel) {
        changeScheduleWebRequest.setChannel(channel);
        return this;
    }

    public ChangeScheduleWebRequestBuilder withMsisdn(String msisdn) {
        changeScheduleWebRequest.setMsisdn(msisdn);
        return this;
    }

    public ChangeScheduleWebRequestBuilder withDOB(String dob) {
        changeScheduleWebRequest.setDateOfBirth(dob);
        return this;
    }

    public ChangeScheduleWebRequestBuilder withEDD(String edd) {
        changeScheduleWebRequest.setExpectedDateOfDelivery(edd);
        return this;
    }

    public ChangeScheduleWebRequestBuilder withChangeType(String changeType) {
        changeScheduleWebRequest.setChangeType(changeType);
        return this;
    }

    public ChangeScheduleWebRequestBuilder withReason(String reason) {
        changeScheduleWebRequest.setReason(reason);
        return this;
    }

    public ChangeScheduleWebRequest build() {
        return changeScheduleWebRequest;
    }
}