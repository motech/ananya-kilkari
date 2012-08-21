package org.motechproject.ananya.kilkari.service;

import org.motechproject.ananya.kilkari.factory.OBDServiceOptionFactory;
import org.motechproject.ananya.kilkari.handlers.callback.obd.ServiceOptionHandler;
import org.motechproject.ananya.kilkari.mapper.CallDetailsRequestMapper;
import org.motechproject.ananya.kilkari.obd.service.OBDService;
import org.motechproject.ananya.kilkari.obd.service.request.FailedCallReports;
import org.motechproject.ananya.kilkari.obd.service.request.InvalidOBDRequestEntries;
import org.motechproject.ananya.kilkari.obd.service.request.OBDSuccessfulCallDetailsRequest;
import org.motechproject.ananya.kilkari.obd.service.validator.Errors;
import org.motechproject.ananya.kilkari.request.OBDSuccessfulCallDetailsWebRequest;
import org.motechproject.ananya.kilkari.service.validator.CallDetailsRequestValidator;
import org.motechproject.ananya.kilkari.subscription.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KilkariCallDetailsService {

    private OBDService obdService;
    private CallDetailsRequestValidator callDetailsRequestValidator;
    private OBDServiceOptionFactory obdServiceOptionFactory;

    KilkariCallDetailsService() {
    }

    @Autowired
    public KilkariCallDetailsService(OBDService obdService, CallDetailsRequestValidator callDetailsRequestValidator,
                                     OBDServiceOptionFactory obdServiceOptionFactory) {
        this.obdService = obdService;
        this.callDetailsRequestValidator = callDetailsRequestValidator;
        this.obdServiceOptionFactory = obdServiceOptionFactory;
    }

    public void processInvalidOBDRequestEntries(InvalidOBDRequestEntries invalidOBDRequestEntries) {
        obdService.processInvalidOBDRequestEntries(invalidOBDRequestEntries);
    }

    public void processCallDeliveryFailureRequest(FailedCallReports failedCallReports) {
        obdService.processCallDeliveryFailure(failedCallReports);
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
