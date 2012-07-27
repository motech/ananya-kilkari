package org.motechproject.ananya.kilkari.obd.domain;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewMessageComparator implements Comparator<CampaignMessage> {

    private static final Pattern INT_PATTERN = Pattern.compile(".*?([0-9]+)$");
    @Override
    public int compare(CampaignMessage thisObject, CampaignMessage otherObject) {
        if (thisObject.hasFailed() && !otherObject.hasFailed()) return -1;
        if (otherObject.hasFailed() && !thisObject.hasFailed()) return 1;
        if(thisObject.getDncRetryCount() > otherObject.getDncRetryCount()) return -1;
        if(thisObject.getDncRetryCount() < otherObject.getDncRetryCount()) return 1;
        return compareMessageId(thisObject, otherObject);
    }

    private int compareMessageId(CampaignMessage thisObject, CampaignMessage otherObject) {
        int thisMessageId = findWeekNumber(thisObject.getMessageId());
        int otherMessageId = findWeekNumber(otherObject.getMessageId());
        return thisMessageId < otherMessageId ? -1 : thisMessageId > otherMessageId ? 1 : 0;
    }


    private int findWeekNumber(String messageId) {
        Matcher matcher = INT_PATTERN.matcher(messageId);
        if(!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Wrong format for messageId: %s", messageId));
        }
        return Integer.parseInt(matcher.group(1));
    }
}
