package org.motechproject.ananya.kilkari.factory;

import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OBDServiceOptionFactory {

    private HashMap<String, ServiceOptionHandler> handlerMappings;

    public OBDServiceOptionFactory() {
        initializeHandlerMap();
    }

    private void initializeHandlerMap() {
        handlerMappings = new HashMap<>();
    }

    public ServiceOptionHandler getHandler(String serviceOption) {
        return handlerMappings.get(serviceOption);
    }
}
