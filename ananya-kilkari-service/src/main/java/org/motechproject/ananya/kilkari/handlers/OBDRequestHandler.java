package org.motechproject.ananya.kilkari.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.obd.contract.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.contract.InvalidOBDRequestEntry;
import org.motechproject.ananya.kilkari.obd.domain.InvalidCallRecord;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.domain.ServiceOption;
import org.motechproject.ananya.kilkari.obd.service.CallRecordsService;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallRequestWrapper;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.validators.Errors;
import org.motechproject.ananya.kilkari.validators.OBDSuccessfulCallRequestValidator;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OBDRequestHandler {

    Logger logger = Logger.getLogger(OBDRequestHandler.class);
    private OBDServiceOptionFactory obdServiceOptionFactory;
    private KilkariCampaignService kilkariCampaignService;
    private OBDSuccessfulCallRequestValidator successfulCallRequestValidator;
    private CallRecordsService callRecordsService;

    @Autowired
    public OBDRequestHandler(OBDServiceOptionFactory obdServiceOptionFactory, KilkariCampaignService kilkariCampaignService, OBDSuccessfulCallRequestValidator successfulCallRequestValidator, CallRecordsService callRecordsService) {
        this.obdServiceOptionFactory = obdServiceOptionFactory;
        this.kilkariCampaignService = kilkariCampaignService;
        this.successfulCallRequestValidator = successfulCallRequestValidator;
        this.callRecordsService = callRecordsService;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_SUCCESSFUL_CALL_REQUEST_SUBJECT})
    public void handleOBDCallbackRequest(MotechEvent motechEvent) {
        OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper = (OBDSuccessfulCallRequestWrapper) motechEvent.getParameters().get("0");
        logger.info("Handling OBD callback for : " + successfulCallRequestWrapper.getSubscriptionId());

        validateSuccessfulCallRequest(successfulCallRequestWrapper);

        kilkariCampaignService.processSuccessfulMessageDelivery(successfulCallRequestWrapper);
        ServiceOption serviceOption = ServiceOption.getFor(successfulCallRequestWrapper.getSuccessfulCallRequest().getServiceOption());
        if (obdServiceOptionFactory.getHandler(serviceOption) != null) {
            obdServiceOptionFactory.getHandler(serviceOption).process(successfulCallRequestWrapper);
        }
        logger.info("Completed handling OBD callback for : " + successfulCallRequestWrapper.getSubscriptionId());
    }

    private void validateSuccessfulCallRequest(OBDSuccessfulCallRequestWrapper successfulCallRequestWrapper) {
        Errors validationErrors = successfulCallRequestValidator.validate(successfulCallRequestWrapper);
        if (validationErrors.hasErrors()) {
            throw new ValidationException(String.format("OBD Request Invalid: %s", validationErrors.allMessages()));
        }
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_INVALID_CALL_RECORDS_REQUEST_SUBJECT})
    public void handleInvalidCallRecordsRequest(MotechEvent event) {
        InvalidOBDRequestEntries invalidOBDRequestEntries = (InvalidOBDRequestEntries) event.getParameters().get("0");
        List<InvalidOBDRequestEntry> requestOBDs = invalidOBDRequestEntries.getInvalidOBDRequestEntryList();
        List<InvalidCallRecord> invalidCallRecords = new ArrayList<>();
        for (InvalidOBDRequestEntry requestEntry : requestOBDs) {
            invalidCallRecords.add(new InvalidCallRecord(requestEntry.getMsisdn(), requestEntry.getSubscriptionId(),
                    requestEntry.getCampaignId(), requestEntry.getOperator(), requestEntry.getDescription()));
        }
        callRecordsService.processInvalidCallRecords(invalidCallRecords);
    }
}
