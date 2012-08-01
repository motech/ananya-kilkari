package org.motechproject.ananya.kilkari.subscription.service.response;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionStatus;

public interface SubscriptionResponse {
    String getMsisdn();
    String getSubscriptionId();
    DateTime getCreationDate();
    SubscriptionStatus getStatus();
    SubscriptionPack getPack();
    void setStatus(SubscriptionStatus status);
    Operator getOperator();
    void setOperator(Operator operator);
    DateTime endDate();
    DateTime currentWeeksMessageExpiryDate();
    boolean hasBeenActivated();
    boolean isInDeactivatedState();
    boolean isInProgress();
    DateTime getStartDate();
}
