package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.mapper.CallDetailsRequestMapper;
import org.motechproject.ananya.kilkari.mapper.InboxCallDetailsReportRequestMapper;
import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.request.InboxCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.validator.CallDetailsRequestValidator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.subscription.service.SubscriptionService;
import org.motechproject.ananya.reports.kilkari.contract.request.CallDetailsReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KilkariCallDetailsService {

    private OBDService obdService;
    private SubscriptionService subscriptionService;
    private ReportingService reportingService;
    private CallDetailsRequestValidator callDetailsRequestValidator;
    private OBDServiceOptionFactory obdServiceOptionFactory;
    private CallDetailsRequestPublisher callDetailsRequestPublisher;

    KilkariCallDetailsService() {
    }

    @Autowired
    public KilkariCallDetailsService(OBDService obdService, SubscriptionService subscriptionService,
                                     ReportingService reportingService,
                                     CallDetailsRequestValidator callDetailsRequestValidator,
                                     OBDServiceOptionFactory obdServiceOptionFactory,
                                     CallDetailsRequestPublisher callDetailsRequestPublisher) {
        this.obdService = obdService;
        this.subscriptionService = subscriptionService;
        this.reportingService = reportingService;
        this.callDetailsRequestValidator = callDetailsRequestValidator;
        this.obdServiceOptionFactory = obdServiceOptionFactory;
        this.callDetailsRequestPublisher = callDetailsRequestPublisher;
    }

    public void processInvalidOBDRequestEntries(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        obdService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    public void processCallDeliveryFailureRequest(FailedCallReports failedCallReports) {
        obdService.processCallDeliveryFailure(failedCallReports);
    }

    public void publishInboxCallDetailsRequest(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        callDetailsRequestPublisher.publishInboxCallDetailsRequest(inboxCallDetailsWebRequest);
    }

    public void processInboxCallDetailsRequest(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        Errors errors = inboxCallDetailsWebRequest.validate();
        if (errors.hasErrors())
            throw new ValidationException(String.format("Invalid inbox call details request: %s", errors.allMessages()));
        validateSubscription(inboxCallDetailsWebRequest);
        CallDetailsReportRequest callDetailsReportRequest = InboxCallDetailsReportRequestMapper.mapFrom(inboxCallDetailsWebRequest);
        reportingService.reportCampaignMessageDeliveryStatus(callDetailsReportRequest);
    }

    private void validateSubscription(InboxCallDetailsWebRequest inboxCallDetailsWebRequest) {
        Subscription subscription = subscriptionService.findBySubscriptionId(inboxCallDetailsWebRequest.getSubscriptionId());
        if (subscription == null)
            throw new ValidationException("Invalid inbox call details request: Subscription not found");
    }


    public void publishSuccessfulCallRequest(OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsRequest) {
        callDetailsRequestPublisher.publishSuccessfulCallRequest(obdSuccessfulCallDetailsRequest);
    }

    public void processSuccessfulMessageDelivery(OBDSuccessfulCallDetailsWebRequest obdSuccessfulCallDetailsWebRequest) {
        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = validateSuccessfulCallRequest(obdSuccessfulCallDetailsWebRequest);
        boolean processed = obdService.processSuccessfulCallDelivery(obdSuccessfulCallDetailsRequest);
        if (!processed) {
            return;
        }

        ServiceOptionHandler serviceOptionHandler = obdServiceOptionFactory.getHandler(obdSuccessfulCallDetailsRequest.getServiceOption());
        if (serviceOptionHandler != null) {
            serviceOptionHandler.process(obdSuccessfulCallDetailsRequest);
        }
    }

    private OBDSuccessfulCallDetailsRequest validateSuccessfulCallRequest(OBDSuccessfulCallDetailsWebRequest webRequest) {
        Errors validationErrors = webRequest.validate();
        if (validationErrors.hasErrors()) {
            throw new ValidationException(String.format("OBD Request Invalid: %s", validationErrors.allMessages()));
        }

        OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest = new CallDetailsRequestMapper().mapOBDRequest(webRequest);
        validateSubscription(obdSuccessfulCallDetailsRequest);
        return obdSuccessfulCallDetailsRequest;
    }

    private void validateSubscription(OBDSuccessfulCallDetailsRequest obdSuccessfulCallDetailsRequest) {
        Errors validationErrors;
        validationErrors = callDetailsRequestValidator.validate(obdSuccessfulCallDetailsRequest);
        if (validationErrors.hasErrors()) {
            throw new ValidationException(String.format("OBD Request Invalid: %s", validationErrors.allMessages()));
        }
    }

}
