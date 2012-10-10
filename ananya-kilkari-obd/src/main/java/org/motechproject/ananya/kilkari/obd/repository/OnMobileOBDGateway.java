package org.motechproject.ananya.kilkari.obd.repository;

import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;

import java.io.IOException;

public interface OnMobileOBDGateway {
    void sendNewMessages(String content);

    void sendRetryMessages(String content);

    void sendInvalidFailureRecord(InvalidFailedCallReports invalidFailedCallReports) throws IOException;
}
