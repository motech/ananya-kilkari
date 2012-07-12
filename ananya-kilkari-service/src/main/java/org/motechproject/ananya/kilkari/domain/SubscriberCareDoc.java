package org.motechproject.ananya.kilkari.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.common.domain.PhoneNumber;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'SubscriberCareDoc'")
public class SubscriberCareDoc extends MotechBaseDataObject {
    @JsonProperty
    private String msisdn;

    @JsonProperty
    private String reason;

    @JsonProperty
    private Channel channel;

    @JsonProperty
    private DateTime createdAt;

    public SubscriberCareDoc() {
    }

    public SubscriberCareDoc(String msisdn, String reason, DateTime createdAt, Channel channel) {
        this.msisdn = PhoneNumber.formatPhoneNumberTo10Digits(msisdn).toString();
        this.reason = reason;
        this.channel = channel;
        this.createdAt = createdAt;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getReason() {
        return reason;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public Channel getChannel() {
        return channel;
    }
}
