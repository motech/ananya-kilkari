package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.springframework.stereotype.Service;

@Service
public class KilkariCallDetailsService {

    private OBDService obdService;

    public KilkariCallDetailsService(OBDService obdService) {
        this.obdService = obdService;
    }

    public void processInvalidOBDRequestEntries(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        obdService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    public void processCallDeliveryFailureRequest(FailedCallReports failedCallReports) {
        obdService.processCallDeliveryFailure(failedCallReports);
    }

}
