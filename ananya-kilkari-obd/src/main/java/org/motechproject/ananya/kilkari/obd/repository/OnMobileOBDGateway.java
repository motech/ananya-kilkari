package org.motechproject.ananya.kilkari.obd.repository;

import org.motechproject.ananya.kilkari.obd.scheduler.SubSlot;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;

public interface OnMobileOBDGateway {
    void sendNewMessages(String content, SubSlot subSlot);

    void sendRetryMessages(String content, SubSlot subSlot);

    void sendInvalidFailureRecord(InvalidFailedCallReports invalidFailedCallReports);
}
