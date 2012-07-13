package org.motechproject.ananya.kilkari.reporting.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriberLocation;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionCreationReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.SubscriptionStateChangeReportRequest;
import org.motechproject.ananya.kilkari.reporting.gateway.ReportingGateway;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportingServiceImplTest {

    @Mock
    private ReportingGateway reportGateway;

    @Mock
    private ReportingPublisher reportingPublisher;

    private ReportingServiceImpl reportingServiceImpl;

    @Before
    public void setUp() {
        initMocks(this);
        reportingServiceImpl = new ReportingServiceImpl(reportGateway, reportingPublisher);
    }
   
    @Test
    public void shouldGetLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        when(reportGateway.getLocation(district, block, panchayat)).thenReturn(new SubscriberLocation(district, block, panchayat));

        SubscriberLocation location = reportingServiceImpl.getLocation(district, block, panchayat);

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
    }
    
    @Test
    public void shouldReportASubscriptionCreation() {
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = mock(SubscriptionCreationReportRequest.class);
        reportingServiceImpl.reportSubscriptionCreation(subscriptionCreationReportRequest);

        verify(reportingPublisher).reportSubscriptionCreation(subscriptionCreationReportRequest);
    }

    @Test
    public void shouldReportASubscriptionStateChange() {
        SubscriptionStateChangeReportRequest subscriptionCreationReportRequest = mock(SubscriptionStateChangeReportRequest.class);
        reportingServiceImpl.reportSubscriptionStateChange(subscriptionCreationReportRequest);

        verify(reportingPublisher).reportSubscriptionStateChange(subscriptionCreationReportRequest);
    }
}
