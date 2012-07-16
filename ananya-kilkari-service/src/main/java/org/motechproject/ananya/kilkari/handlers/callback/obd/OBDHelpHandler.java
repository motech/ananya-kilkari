package org.motechproject.ananya.kilkari.handlers.callback.obd;

import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.springframework.stereotype.Component;

@Component
public class OBDHelpHandler implements ServiceOptionHandler {

    @Override
    public void process(OBDRequestWrapper obdRequestWrapper) {
        //For the far distant future
    }
}
