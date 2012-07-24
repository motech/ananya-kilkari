package org.motechproject.ananya.kilkari.subscription.contract;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.ananya.kilkari.subscription.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import java.io.Serializable;

public class OMSubscriptionRequest implements Serializable {
    private final String msisdn;
    private final SubscriptionPack pack;
    private final Channel channel;
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

}
