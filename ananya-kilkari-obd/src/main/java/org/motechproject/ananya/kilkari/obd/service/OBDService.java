package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.handler.OBDEventQueuePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OBDService {

    private OBDEventQueuePublisher obdEventQueuePublisher;

    @Autowired
    public OBDService(OBDEventQueuePublisher obdEventQueuePublisher) {
        this.obdEventQueuePublisher = obdEventQueuePublisher;
    }

    public void processCallDeliveryFailure(FailedCallReports failedCallReports) {
        obdEventQueuePublisher.publishCallDeliveryFailureRecord(failedCallReports);
    }
}
