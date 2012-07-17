package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;

public interface ServiceOptionHandler {
    public void process(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper);
}
