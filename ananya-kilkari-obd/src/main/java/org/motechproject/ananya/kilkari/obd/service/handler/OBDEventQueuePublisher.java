package org.motechproject.ananya.kilkari.obd.service.handler;

import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.CallDeliveryFailureEventKeys;
import org.motechproject.scheduler.context.EventContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OBDEventQueuePublisher {

    private EventContext eventContext;

    @Autowired
    public OBDEventQueuePublisher(@Qualifier("eventContext") EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void publishCallDeliveryFailureRecord(FailedCallReports failedCallReports) {
        eventContext.send(CallDeliveryFailureEventKeys.PROCESS_CALL_DELIVERY_FAILURE_REQUEST, failedCallReports);
    }

    public void publishInvalidCallDeliveryFailureRecord(InvalidFailedCallReports invalidFailedCallReports) {
        eventContext.send(CallDeliveryFailureEventKeys.PROCESS_INVALID_CALL_DELIVERY_FAILURE_RECORD, invalidFailedCallReports);
    }

    public void publishValidCallDeliveryFailureRecord(ValidFailedCallReport validFailedCallReport) {
        eventContext.send(CallDeliveryFailureEventKeys.PROCESS_VALID_CALL_DELIVERY_FAILURE_RECORD, validFailedCallReport);
    }
}
