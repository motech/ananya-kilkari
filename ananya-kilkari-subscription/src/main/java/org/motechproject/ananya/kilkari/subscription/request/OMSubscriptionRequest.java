package org.motechproject.ananya.kilkari.subscription.request;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import java.io.Serializable;

public class OMSubscriptionRequest implements Serializable {

    private static final long serialVersionUID = -3926690514453195727L;
    private String msisdn;
    private SubscriptionPack pack;
    private Channel channel;
    private String subscriptionId;

    public OMSubscriptionRequest(String msisdn, SubscriptionPack pack, Channel channel, String subscriptionId) {
        this.msisdn = msisdn;
        this.pack = pack;
        this.channel = channel;
        this.subscriptionId = subscriptionId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public SubscriptionPack getPack() {
        return pack;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("msisdn", msisdn)
                .append("pack", pack)
                .append("channel", channel)
                .append("subscriptionId", subscriptionId)
                .toString();
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
}
