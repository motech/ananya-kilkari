package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.springframework.stereotype.Component;

@Component
public class OBDHelpHandler implements ServiceOptionHandler {

    @Override
    public void process(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        //For the far distant future
    }
}
