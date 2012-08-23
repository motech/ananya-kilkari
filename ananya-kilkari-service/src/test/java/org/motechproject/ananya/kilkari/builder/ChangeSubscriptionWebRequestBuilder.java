package org.motechproject.ananya.kilkari.builder;

import org.motechproject.ananya.kilkari.request.ChangeSubscriptionWebRequest;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class ChangeSubscriptionWebRequestBuilder {

    private ChangeSubscriptionWebRequest changeSubscriptionWebRequest;

    public ChangeSubscriptionWebRequestBuilder() {
        changeSubscriptionWebRequest = new ChangeSubscriptionWebRequest();
    }

    public ChangeSubscriptionWebRequestBuilder withDefaults() {
        changeSubscriptionWebRequest = new ChangeSubscriptionWebRequest();
        changeSubscriptionWebRequest.setChangeType("change pack");
        changeSubscriptionWebRequest.setPack(SubscriptionPack.BARI_KILKARI.name());
        changeSubscriptionWebRequest.setChannel(Channel.CALL_CENTER.name());
        changeSubscriptionWebRequest.setDateOfBirth(null);
        changeSubscriptionWebRequest.setExpectedDateOfDelivery(null);
        changeSubscriptionWebRequest.setReason(null);
        return this;
    }

    public ChangeSubscriptionWebRequestBuilder withPack(String pack) {
        changeSubscriptionWebRequest.setPack(pack);
        return this;
    }

    public ChangeSubscriptionWebRequestBuilder withChannel(String channel) {
        changeSubscriptionWebRequest.setChannel(channel);
        return this;
    }

    public ChangeSubscriptionWebRequestBuilder withDOB(String dob) {
        changeSubscriptionWebRequest.setDateOfBirth(dob);
        return this;
    }

    public ChangeSubscriptionWebRequestBuilder withEDD(String edd) {
        changeSubscriptionWebRequest.setExpectedDateOfDelivery(edd);
        return this;
    }

    public ChangeSubscriptionWebRequestBuilder withChangeType(String changeType) {
        changeSubscriptionWebRequest.setChangeType(changeType);
        return this;
    }

    public ChangeSubscriptionWebRequestBuilder withReason(String reason) {
        changeSubscriptionWebRequest.setReason(reason);
        return this;
    }

    public ChangeSubscriptionWebRequest build() {
        return changeSubscriptionWebRequest;
    }
}