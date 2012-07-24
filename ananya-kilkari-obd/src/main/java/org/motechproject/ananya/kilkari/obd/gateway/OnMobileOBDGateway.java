package org.motechproject.ananya.kilkari.obd.gateway;

import org.motechproject.ananya.kilkari.obd.contract.InvalidFailedCallReports;

public interface OnMobileOBDGateway {
    void sendNewMessages(String content);

    void sendRetryMessages(String content);

    void sendInvalidFailureRecord(InvalidFailedCallReports invalidFailedCallReports);
}
