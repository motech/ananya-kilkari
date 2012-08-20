package org.motechproject.ananya.kilkari.obd.service;

import org.motechproject.ananya.kilkari.obd.domain.ValidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReport;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidFailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.validator.CallDeliveryFailureRecordValidator;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
class OBDCallDetailsService {

    private CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator;
    private ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper;
    private OBDEventQueuePublisher obdEventQueuePublisher;

    OBDCallDetailsService() {
    }

    @Autowired
    OBDCallDetailsService(CallDeliveryFailureRecordValidator callDeliveryFailureRecordValidator,
                                 ValidCallDeliveryFailureRecordObjectMapper validCallDeliveryFailureRecordObjectMapper,
                                 OBDEventQueuePublisher obdEventQueuePublisher) {
        this.callDeliveryFailureRecordValidator = callDeliveryFailureRecordValidator;
        this.validCallDeliveryFailureRecordObjectMapper = validCallDeliveryFailureRecordObjectMapper;
        this.obdEventQueuePublisher = obdEventQueuePublisher;
    }

    @Transactional
    public void processCallDeliveryFailureRecord(FailedCallReports failedCallReports) {
        List<InvalidFailedCallReport> invalidFailedCallReports = new ArrayList<>();
        List<ValidFailedCallReport> validFailedCallReports = new ArrayList<>();
        validate(failedCallReports, validFailedCallReports, invalidFailedCallReports);

        publishErrorRecords(invalidFailedCallReports);
        publishValidRecords(validFailedCallReports);
    }

    private void validate(FailedCallReports failedCallReports, List<ValidFailedCallReport> validFailedCallReports, List<InvalidFailedCallReport> invalidFailedCallReports) {
        for (FailedCallReport failedCallReport : failedCallReports.getCallrecords()) {
            Errors errors = callDeliveryFailureRecordValidator.validate(failedCallReport);
            if (errors.hasErrors()) {
                InvalidFailedCallReport invalidCallDeliveryFailureRecordObject = new InvalidFailedCallReport(failedCallReport.getMsisdn(),
                        failedCallReport.getSubscriptionId(), errors.allMessages());
                invalidFailedCallReports.add(invalidCallDeliveryFailureRecordObject);
                continue;
            }

            ValidFailedCallReport validFailedCallReport = validCallDeliveryFailureRecordObjectMapper.mapFrom(failedCallReport);
            validFailedCallReports.add(validFailedCallReport);
        }
    }

    private void publishErrorRecords(List<InvalidFailedCallReport> invalidFailedCallReport) {
        if (invalidFailedCallReport.isEmpty())
            return;
        InvalidFailedCallReports invalidFailedCallReports = new InvalidFailedCallReports();
        invalidFailedCallReports.setRecordObjectFaileds(invalidFailedCallReport);

        obdEventQueuePublisher.publishInvalidCallDeliveryFailureRecord(invalidFailedCallReports);
    }

    private void publishValidRecords(List<ValidFailedCallReport> validFailedCallReports) {
        for (ValidFailedCallReport failedCallReport : validFailedCallReports) {
            obdEventQueuePublisher.publishValidCallDeliveryFailureRecord(failedCallReport);
        }
    }
}
