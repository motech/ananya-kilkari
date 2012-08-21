package org.motechproject.ananya.kilkari.subscription.service.request;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;

import java.util.List;

public class ChangeMsisdnRequest {

    private String oldMsisdn;

    private String newMsisdn;

    private List<SubscriptionPack> packs;

    private Channel channel;

    private boolean shouldChangeAllPacks = false;

    public ChangeMsisdnRequest(String oldMsisdn, String newMsisdn, Channel channel) {
        this.oldMsisdn = oldMsisdn;
        this.newMsisdn = newMsisdn;
        this.channel = channel;
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
}
