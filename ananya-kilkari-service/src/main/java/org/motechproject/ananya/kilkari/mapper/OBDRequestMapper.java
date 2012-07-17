package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;

public class OBDRequestMapper {
    public static DeactivationRequest mapFrom(OBDSuccessfulCallRequestWrapper obdRequestWrapper) {
        return new DeactivationRequest(obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getChannel());
    }
}
