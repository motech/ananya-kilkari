package org.motechproject.ananya.kilkari.builder;

import org.motechproject.ananya.kilkari.request.ChangePackWebRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

public class ChangePackWebRequestBuilder {

    private ChangePackWebRequest changePackWebRequest;

    public ChangePackWebRequestBuilder() {
        changePackWebRequest = new ChangePackWebRequest();
    }

    public ChangePackWebRequestBuilder withDefaults() {
        changePackWebRequest = new ChangePackWebRequest();
        changePackWebRequest.setPack(SubscriptionPack.BARI_KILKARI.name());
        changePackWebRequest.setChannel(Channel.CALL_CENTER.name());
        changePackWebRequest.setMsisdn("9876543210");
        changePackWebRequest.setDateOfBirth(null);
        changePackWebRequest.setExpectedDateOfDelivery(null);
        return this;
    }

    public ChangePackWebRequestBuilder withPack(String pack) {
        changePackWebRequest.setPack(pack);
        return this;
    }

    public ChangePackWebRequestBuilder withChannel(String channel) {
        changePackWebRequest.setChannel(channel);
        return this;
    }

    public ChangePackWebRequestBuilder withMsisdn(String msisdn) {
        changePackWebRequest.setMsisdn(msisdn);
        return this;
    }

    public ChangePackWebRequestBuilder withDOB(String dob) {
        changePackWebRequest.setDateOfBirth(dob);
        return this;
    }

    public ChangePackWebRequestBuilder withEDD(String edd) {
        changePackWebRequest.setExpectedDateOfDelivery(edd);
        return this;
    }

    public ChangePackWebRequest build() {
        return changePackWebRequest;
    }
}