package org.motechproject.ananya.kilkari.reporting.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.motechproject.ananya.kilkari.reporting.domain.*;
import org.motechproject.ananya.kilkari.reporting.gateway.StubReportingGateway;
import org.motechproject.ananya.kilkari.reporting.gateway.ReportingGateway;
import org.motechproject.ananya.kilkari.reporting.it.TimedRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariReportingContext.xml")
@ActiveProfiles("test")
public class ReportingServiceImplIT {

    @Autowired
    private StubReportingGateway stubReportingGateway;

    @Autowired
    private ReportingPublisher reportingPublisher;

    private ReportingService reportingService;

    @Before
    public void setUp() {
        reportingService = new ReportingServiceImpl(stubReportingGateway, reportingPublisher);
    }

    @Test
    public void shouldGetLocationDetails() {
        String district = "d1";
        String block = "b1";
        String panchayat = "p1";

        ReportingGateway reportingGateway = mock(ReportingGateway.class);
        SubscriberLocation expectedLocation = new SubscriberLocation();
        when(reportingGateway.getLocation(district, block, panchayat)).thenReturn(expectedLocation);
        stubReportingGateway.setBehavior(reportingGateway);

        SubscriberLocation actualLocation = reportingService.getLocation(district, block, panchayat);

        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void shouldReportSubscriptionCreation() {
        SubscriptionCreationReportRequest subscriptionCreationReportRequest = new SubscriptionCreationReportRequest(new SubscriptionDetails(), "channel" , 12, "name", DateTime.now(), null, new SubscriberLocation());
        ReportingGateway reportingGateway = mock(ReportingGateway.class);
        stubReportingGateway.setBehavior(reportingGateway);

        reportingService.reportSubscriptionCreation(subscriptionCreationReportRequest);

        new TimedRunner(20, 1000) {
            @Override
            public boolean run(){
                return stubReportingGateway.isCreateSubscriptionCalled();
            }
        }.execute();
        stubReportingGateway.setCreateSubscriptionCalled(false);

        ArgumentCaptor<SubscriptionCreationReportRequest> subscriptionCreationReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionCreationReportRequest.class);
        verify(reportingGateway).createSubscription(subscriptionCreationReportRequestArgumentCaptor.capture());
        SubscriptionCreationReportRequest actualSubscriptionCreationReportRequest = subscriptionCreationReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionCreationReportRequest.getChannel(), actualSubscriptionCreationReportRequest.getChannel());
        assertEquals(subscriptionCreationReportRequest.getAgeOfBeneficiary(), actualSubscriptionCreationReportRequest.getAgeOfBeneficiary());
        assertEquals(subscriptionCreationReportRequest.getDob(), actualSubscriptionCreationReportRequest.getDob());
    }

    @Test
    public void shouldReportSubscriptionStateChange() {
        SubscriptionStateChangeReportRequest subscriptionStateChangeReportRequest = new SubscriptionStateChangeReportRequest("subscriptionId", "ACTIVE", DateTime.now(), "reason", "operator");
        ReportingGateway reportingGateway = mock(ReportingGateway.class);
        stubReportingGateway.setBehavior(reportingGateway);

        reportingService.reportSubscriptionStateChange(subscriptionStateChangeReportRequest);

        new TimedRunner(20, 1000) {
            @Override
            public boolean run(){
                return stubReportingGateway.isUpdateSubscriptionCalled();
            }
        }.execute();
        stubReportingGateway.setUpdateSubscriptionCalled(false);

        ArgumentCaptor<SubscriptionStateChangeReportRequest> subscriptionStateChangeReportRequestArgumentCaptor = ArgumentCaptor.forClass(SubscriptionStateChangeReportRequest.class);
        verify(reportingGateway).updateSubscriptionStateChange(subscriptionStateChangeReportRequestArgumentCaptor.capture());
        SubscriptionStateChangeReportRequest actualSubscriptionStateChangeReportRequest = subscriptionStateChangeReportRequestArgumentCaptor.getValue();

        assertEquals(subscriptionStateChangeReportRequest.getSubscriptionId(), actualSubscriptionStateChangeReportRequest.getSubscriptionId());
        assertEquals(subscriptionStateChangeReportRequest.getSubscriptionStatus(), actualSubscriptionStateChangeReportRequest.getSubscriptionStatus());
        assertEquals(subscriptionStateChangeReportRequest.getCreatedAt(), actualSubscriptionStateChangeReportRequest.getCreatedAt());
        assertEquals(subscriptionStateChangeReportRequest.getReason(), actualSubscriptionStateChangeReportRequest.getReason());
        assertEquals(subscriptionStateChangeReportRequest.getOperator(), actualSubscriptionStateChangeReportRequest.getOperator());
    }

    @Test
    public void shouldReportCampaignMessageDelivery() {
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = new CampaignMessageDeliveryReportRequest("subscriptionId", "msisdn", "campaignId", "serviceOption", "3", "SUCCESS", new CallDetailsReportRequest("25-12-2012", "27-12-2012"));
        ReportingGateway reportingGateway = mock(ReportingGateway.class);
        stubReportingGateway.setBehavior(reportingGateway);

        reportingService.reportCampaignMessageDeliveryStatus(campaignMessageDeliveryReportRequest);

        new TimedRunner(20, 1000) {
            @Override
            public boolean run(){
                return stubReportingGateway.isReportCampaignMessageDeliveryCalled();
            }
        }.execute();
        stubReportingGateway.setReportCampaignMessageDeliveryCalled(false);

        ArgumentCaptor<CampaignMessageDeliveryReportRequest> campaignMessageDeliveryReportRequestArgumentCaptor = ArgumentCaptor.forClass(CampaignMessageDeliveryReportRequest.class);
        verify(reportingGateway).reportCampaignMessageDelivery(campaignMessageDeliveryReportRequestArgumentCaptor.capture());
        CampaignMessageDeliveryReportRequest actualCampaignMessageDeliveryReportRequest = campaignMessageDeliveryReportRequestArgumentCaptor.getValue();

        assertEquals(campaignMessageDeliveryReportRequest.getSubscriptionId(), actualCampaignMessageDeliveryReportRequest.getSubscriptionId());
        assertEquals(campaignMessageDeliveryReportRequest.getMsisdn(), actualCampaignMessageDeliveryReportRequest.getMsisdn());
        assertEquals(campaignMessageDeliveryReportRequest.getCampaignId(), actualCampaignMessageDeliveryReportRequest.getCampaignId());
        assertEquals(campaignMessageDeliveryReportRequest.getServiceOption(), actualCampaignMessageDeliveryReportRequest.getServiceOption());
    }
}
