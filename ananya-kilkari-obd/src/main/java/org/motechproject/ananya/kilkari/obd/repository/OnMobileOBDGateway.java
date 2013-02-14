package org.motechproject.ananya.kilkari.obd.repository;

import org.motechproject.ananya.kilkari.obd.domain.OBDSubSlot;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;

public interface OnMobileOBDGateway {
    void sendMessages(String content, OBDSubSlot subSlot);

    void sendInvalidFailureRecord(InvalidFailedCallReports invalidFailedCallReports);
}
