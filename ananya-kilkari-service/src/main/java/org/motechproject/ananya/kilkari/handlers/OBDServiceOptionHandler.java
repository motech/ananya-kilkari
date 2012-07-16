package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.obd.contract.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OBDServiceOptionHandler {

    Logger logger = Logger.getLogger(OBDServiceOptionHandler.class);
    private OBDServiceOptionFactory obdServiceOptionFactory;
    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public OBDServiceOptionHandler(OBDServiceOptionFactory obdServiceOptionFactory, KilkariCampaignService kilkariCampaignService) {
        this.obdServiceOptionFactory = obdServiceOptionFactory;
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_CALLBACK_REQUEST})
    public void handleOBDCallbackRequest(MotechEvent motechEvent) {
        OBDRequestWrapper obdRequestWrapper = (OBDRequestWrapper) motechEvent.getParameters().get("0");
        kilkariCampaignService.processSuccessfulMessageDelivery(obdRequestWrapper);
        ServiceOption serviceOption = ServiceOption.getFor(obdRequestWrapper.getObdRequest().getServiceOption());
        obdServiceOptionFactory.getHandler(serviceOption).process(obdRequestWrapper);
    }
}
