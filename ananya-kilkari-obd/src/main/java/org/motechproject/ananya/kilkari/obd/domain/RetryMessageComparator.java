package org.motechproject.ananya.kilkari.obd.domain;

import java.util.Comparator;

public class RetryMessageComparator implements Comparator<CampaignMessage> {
    @Override
    public int compare(CampaignMessage thisObject, CampaignMessage otherObject) {
        return thisObject.getDnpRetryCount() > otherObject.getDnpRetryCount() ? -1 : (thisObject.getDnpRetryCount() == otherObject.getDnpRetryCount() ? 0 : 1);
    }
}
