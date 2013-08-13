package org.motechproject.ananya.kilkari.factory;

import org.motechproject.ananya.kilkari.handlers.callback.obd.OBDDeactivateHandler;
import org.motechproject.ananya.kilkari.handlers.callback.obd.OBDHelpHandler;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OBDServiceOptionFactory {

    private HashMap<ServiceOption, ServiceOptionHandler> handlerMappings;
    private OBDDeactivateHandler obdDeactivateHandler;
    private OBDHelpHandler obdHelpHandler;

    @Autowired
    public OBDServiceOptionFactory(OBDDeactivateHandler obdDeactivateHandler, OBDHelpHandler obdHelpHandler) {
        this.obdDeactivateHandler = obdDeactivateHandler;
        this.obdHelpHandler = obdHelpHandler;
        initializeHandlerMap();
    }

    private void initializeHandlerMap() {
        handlerMappings = new HashMap<>();
        handlerMappings.put(ServiceOption.UNSUBSCRIPTION, obdDeactivateHandler);
        handlerMappings.put(ServiceOption.HELP, obdHelpHandler);
    }

    public ServiceOptionHandler getHandler(ServiceOption serviceOption) {
        return handlerMappings.get(serviceOption);
    }
}
