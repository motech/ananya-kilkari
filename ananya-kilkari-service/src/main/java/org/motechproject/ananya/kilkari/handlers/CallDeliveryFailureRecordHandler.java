package org.motechproject.ananya.kilkari.handlers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.mapper.ValidCallDeliveryFailureRecordObjectMapper;
import org.motechproject.ananya.kilkari.obd.contract.*;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.service.OBDRequestPublisher;
import org.motechproject.ananya.kilkari.validators.CallDeliveryFailureRecordValidator;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallDeliveryFailureRecordHandler {

    Logger logger = Logger.getLogger(CallDeliveryFailureRecordHandler.class);
    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;
    private OBDRequestPublisher obdRequestPublisher;
    private ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper;

    @Autowired
    public CallDeliveryFailureRecordHandler(CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator, OBDRequestPublisher obdRequestPublisher, ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper) {
        this.callDeliveryFailureRecordValidator = callDeliveryFailureRecordValidator;
        this.obdRequestPublisher = obdRequestPublisher;
        this.validCallDeliveryFailureRecordObjectMapper = validCallDeliveryFailureRecordObjectMapper;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST})
    public void handleCallDeliveryFailureRecord(MotechEvent motechEvent) {
        CallDeliveryFailureRecord callDeliveryFailureRecord = (CallDeliveryFailureRecord) motechEvent.getParameters().get("0");
        logger.info("Handling OBD call delivery failure record");

        List<InvalidCallDeliveryFailureRecordObject> invalidCallDeliveryFailureRecordObjects = new ArrayList<>();
        List<ValidCallDeliveryFailureRecordObject> validCallDeliveryFailureRecordObjects = new ArrayList<>();
        validate(callDeliveryFailureRecord, validCallDeliveryFailureRecordObjects, invalidCallDeliveryFailureRecordObjects);

        publishErrorRecords(invalidCallDeliveryFailureRecordObjects);
        publishValidRecords(validCallDeliveryFailureRecordObjects);
    }

    private void publishValidRecords(List<ValidCallDeliveryFailureRecordObject> validCallDeliveryFailureRecordObjects) {
        for(ValidCallDeliveryFailureRecordObject recordObject : validCallDeliveryFailureRecordObjects){
            obdRequestPublisher.publishValidCallDeliveryFailureRecord(recordObject);
        }
    }

    private void publishErrorRecords(List<InvalidCallDeliveryFailureRecordObject> invalidCallDeliveryFailureRecordObjects) {
        if(invalidCallDeliveryFailureRecordObjects.isEmpty())
            return;
        InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord = new InvalidCallDeliveryFailureRecord();
        invalidCallDeliveryFailureRecord.setRecordObjects(invalidCallDeliveryFailureRecordObjects);

        obdRequestPublisher.publishInvalidCallDeliveryFailureRecord(invalidCallDeliveryFailureRecord);
    }

    private void validate(CallDeliveryFailureRecord callDeliveryFailureRecord, List<ValidCallDeliveryFailureRecordObject> validCallDeliveryFailureRecordObjects, List<InvalidCallDeliveryFailureRecordObject> invalidCallDeliveryFailureRecordObjects) {
        for(CallDeliveryFailureRecordObject callDeliveryFailureRecordObject : callDeliveryFailureRecord.getCallrecords()) {
            List<String> errors = callDeliveryFailureRecordValidator.validate(callDeliveryFailureRecordObject);
            if(!errors.isEmpty()) {
                InvalidCallDeliveryFailureRecordObject invalidCallDeliveryFailureRecordObject = new InvalidCallDeliveryFailureRecordObject(callDeliveryFailureRecordObject.getMsisdn(),
                        callDeliveryFailureRecordObject.getSubscriptionId(), StringUtils.join(errors, ","));
                invalidCallDeliveryFailureRecordObjects.add(invalidCallDeliveryFailureRecordObject);
                continue;
            }

            ValidCallDeliveryFailureRecordObject validCallDeliveryFailureRecordObject = validCallDeliveryFailureRecordObjectMapper.mapFrom(callDeliveryFailureRecordObject, callDeliveryFailureRecord);
            validCallDeliveryFailureRecordObjects.add(validCallDeliveryFailureRecordObject);
        }
    }
}