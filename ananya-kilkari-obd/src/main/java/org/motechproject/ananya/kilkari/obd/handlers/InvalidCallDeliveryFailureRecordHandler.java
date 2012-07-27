package org.motechproject.ananya.kilkari.obd.handlers;

import org.apache.log4j.Logger;
import org.motechproject.ananya.kilkari.obd.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.domain.OBDEventKeys;
import org.motechproject.ananya.kilkari.obd.gateway.OnMobileOBDGateway;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvalidCallDeliveryFailureRecordHandler {

    Logger logger = Logger.getLogger(InvalidCallDeliveryFailureRecordHandler.class);
    private OnMobileOBDGateway onMobileOBDGateway;

    @Autowired
    public InvalidCallDeliveryFailureRecordHandler(OnMobileOBDGateway onMobileOBDGateway) {
        this.onMobileOBDGateway = onMobileOBDGateway;
    }

    @MotechListener(subjects = {OBDEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD})
    public void handleInvalidCallDeliveryFailureRecord(MotechEvent motechEvent) {
        InvalidFailedCallReports invalidFailedCallReports = (InvalidFailedCallReports) motechEvent.getParameters().get("0");
        logger.info("Handling OBD invalid call delivery failure records");
        onMobileOBDGateway.sendInvalidFailureRecord(invalidFailedCallReports);
    }
}