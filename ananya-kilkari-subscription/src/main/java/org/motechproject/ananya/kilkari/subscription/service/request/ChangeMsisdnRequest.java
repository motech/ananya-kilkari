package org.motechproject.ananya.kilkari.subscription.service.request;

import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import java.util.List;

public class ChangeMsisdnRequest {

    private String oldMsisdn;

    private String newMsisdn;

    private List<SubscriptionPack> packs;

    private boolean shouldChangeAllPacks = false;

    public ChangeMsisdnRequest(String oldMsisdn, String newMsisdn) {
        this.oldMsisdn = oldMsisdn;
        this.newMsisdn = newMsisdn;
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
}
