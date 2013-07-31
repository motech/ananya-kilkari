package org.motechproject.ananya.kilkari.subscription.service.request;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import java.util.List;

public class ChangeMsisdnRequest {

    private String oldMsisdn;

    private String newMsisdn;

    private List<SubscriptionPack> packs;

    private Channel channel;

    private boolean shouldChangeAllPacks = false;

    private String reason;

    private DateTime createdAt;

    public ChangeMsisdnRequest(String oldMsisdn, String newMsisdn, Channel channel, String reason, DateTime createdAt) {
        this.oldMsisdn = oldMsisdn;
        this.newMsisdn = newMsisdn;
        this.channel = channel;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public void setPacks(List<SubscriptionPack> packs) {
        this.packs = packs;
    }

    public void setShouldChangeAllPacks(boolean shouldChangeAllPacks) {
        this.shouldChangeAllPacks = shouldChangeAllPacks;
    }

    public String getOldMsisdn() {
        return oldMsisdn;
    }

    public String getNewMsisdn() {
        return newMsisdn;
    }

    public boolean getShouldChangeAllPacks() {
        return shouldChangeAllPacks;
    }

    public List<SubscriptionPack> getPacks() {
        return packs;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getReason() {
        return reason;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
