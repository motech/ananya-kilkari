package org.motechproject.ananya.kilkari.mapper;

import org.motechproject.ananya.kilkari.request.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.subscription.domain.DeactivationRequest;

public class OBDRequestMapper {
    public static DeactivationRequest mapFrom(OBDRequestWrapper obdRequestWrapper) {
        return new DeactivationRequest(obdRequestWrapper.getSubscriptionId(), obdRequestWrapper.getChannel());
    }
}
