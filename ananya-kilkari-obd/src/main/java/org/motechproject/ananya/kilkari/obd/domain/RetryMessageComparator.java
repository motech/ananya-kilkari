package org.motechproject.ananya.kilkari.obd.domain;

import java.util.Comparator;

public class RetryMessageComparator implements Comparator<CampaignMessage> {

    @Override
    public int compare(CampaignMessage thisObject, CampaignMessage otherObject) {
        if(thisObject.getNARetryCount() > otherObject.getNARetryCount()) return -1;
        if(thisObject.getNARetryCount() < otherObject.getNARetryCount()) return 1;
        return CompareMessageIdStrategy.compare(thisObject, otherObject);
    }
}