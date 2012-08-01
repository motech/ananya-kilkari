package org.motechproject.ananya.kilkari.functional.test.utils;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.functional.test.domain.SubscriptionData;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.reporting.service.StubReportingService;
import org.motechproject.ananya.kilkari.request.LocationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@Component
public class ReportVerifier {

    private StubReportingService stubReportingService;

    @Mock
    private ReportingService reportingService;

    @Autowired
    public ReportVerifier(StubReportingService stubReportingService) {
        initMocks(this);
        this.stubReportingService = stubReportingService;
        stubReportingService.setBehavior(reportingService);
    }

    public void verifySubscriptionCreationRequest(SubscriptionData subscriptionData) {
        ArgumentCaptor<SubscriptionCreationReportRequest> requestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        verify(reportingService).reportSubscriptionCreation(requestArgumentCaptor.capture());

        SubscriptionCreationReportRequest reportRequest = requestArgumentCaptor.getValue();
        assertEquals(subscriptionData.getMsisdn(), reportRequest.getMsisdn());
        assertEquals(subscriptionData.getBeneficiaryName(), reportRequest.getName());
        assertEquals(subscriptionData.getPack().name(), reportRequest.getPack());
        assertEquals(subscriptionData.getChannel(), reportRequest.getChannel());
        assertEquals(subscriptionData.getDateOfBirth(), reportRequest.getDob().toString("dd-MM-yyyy"));
        assertEquals(subscriptionData.getExpectedDateOfDelivery(), reportRequest.getEdd().toString("dd-MM-yyyy"));
        assertEquals(subscriptionData.getBeneficiaryAge(), String.valueOf(reportRequest.getAgeOfBeneficiary()));
        LocationRequest location = subscriptionData.getLocation();
        assertEquals(location.getBlock(), reportRequest.getLocation().getBlock());
        assertEquals(location.getDistrict(), reportRequest.getLocation().getDistrict());
        assertEquals(location.getPanchayat(), reportRequest.getLocation().getPanchayat());
    }

    public void setUpReporting(SubscriptionData subscriptionData) {
        LocationRequest location = subscriptionData.getLocation();
        if (location == null) return;
        when(reportingService.getLocation(anyString(), anyString(), anyString())).thenReturn(new SubscriberLocation(location.getDistrict(), location.getBlock(), location.getPanchayat()));
    }
}