package org.motechproject.ananya.kilkari.handlers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.request.OBDRequestWrapper;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.validators.OBDRequestValidator;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OBDServiceOptionHandler {

    Logger logger = Logger.getLogger(OBDServiceOptionHandler.class);
    private OBDServiceOptionFactory obdServiceOptionFactory;
    private KilkariCampaignService kilkariCampaignService;
    private OBDRequestValidator obdRequestValidator;

    @Autowired
    public OBDServiceOptionHandler(OBDServiceOptionFactory obdServiceOptionFactory, KilkariCampaignService kilkariCampaignService, OBDRequestValidator obdRequestValidator) {
        this.obdServiceOptionFactory = obdServiceOptionFactory;
        this.kilkariCampaignService = kilkariCampaignService;
        this.obdRequestValidator = obdRequestValidator;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_CALLBACK_REQUEST})
    public void handleOBDCallbackRequest(MotechEvent motechEvent) {
        OBDRequestWrapper obdRequestWrapper = (OBDRequestWrapper) motechEvent.getParameters().get("0");
        logger.info("Handling OBD callback for : " + obdRequestWrapper.getSubscriptionId());

        validateRequest(obdRequestWrapper);

        kilkariCampaignService.processSuccessfulMessageDelivery(obdRequestWrapper);
        ServiceOption serviceOption = ServiceOption.getFor(obdRequestWrapper.getObdRequest().getServiceOption());
        obdServiceOptionFactory.getHandler(serviceOption).process(obdRequestWrapper);
        logger.info("Completed handling OBD callback for : " + obdRequestWrapper.getSubscriptionId());
    }

    private void validateRequest(OBDRequestWrapper obdRequestWrapper) {
        List<String> validationErrors = obdRequestValidator.validate(obdRequestWrapper);
        if (!(validationErrors.isEmpty())) {
            throw new ValidationException(String.format("OBD Request Invalid: %s", StringUtils.join(validationErrors.toArray(), ",")));
        }
    }
}
