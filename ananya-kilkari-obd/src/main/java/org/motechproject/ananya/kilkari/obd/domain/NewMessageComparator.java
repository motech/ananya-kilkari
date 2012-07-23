package org.motechproject.ananya.kilkari.obd.domain;

import java.util.Comparator;

public class NewMessageComparator implements Comparator<CampaignMessage> {
    @Override
    public int compare(CampaignMessage thisObject, CampaignMessage otherObject) {
        if (thisObject.hasFailed() && !otherObject.hasFailed()) return -1;
        if (otherObject.hasFailed() && !thisObject.hasFailed()) return 1;
        return thisObject.getDncRetryCount() > otherObject.getDncRetryCount() ? -1 : (thisObject.getDncRetryCount() == otherObject.getDncRetryCount() ? 0 : 1);
    }
}
