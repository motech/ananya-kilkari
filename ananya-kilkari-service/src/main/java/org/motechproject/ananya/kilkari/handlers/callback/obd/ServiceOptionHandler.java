package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;

public interface ServiceOptionHandler {
    public void process(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest);
}
