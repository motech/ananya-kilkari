package org.motechproject.ananya.kilkari.reporting.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.reporting.domain.CampaignMessageDeliveryReportRequest;
import org.motechproject.ananya.kilkari.reporting.domain.ReportingEventKeys;
import org.motechproject.ananya.kilkari.reporting.gateway.ReportingGateway;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDetailsReportHandlerTest {
    @Mock
    private ReportingGateway reportingGateway;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldInvokeReportingServiceToReportCampaignMessageDelivery() {
        CampaignMessageDeliveryReportRequest campaignMessageDeliveryReportRequest = new CampaignMessageDeliveryReportRequest(null, null, null, null, null, null);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("0", campaignMessageDeliveryReportRequest);

        new CallDetailsReportHandler(reportingGateway).handleSuccessfulMessageDelivery(new MotechEvent(ReportingEventKeys.REPORT_CAMPAIGN_MESSAGE_DELIVERED, parameters));

        verify(reportingGateway).reportCampaignMessageDelivery(campaignMessageDeliveryReportRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionsRaisedByReportingServiceToReportCampaignMessageDelivery() {
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put("0", null);
        }};

        doThrow(new RuntimeException()).when(reportingGateway).reportCampaignMessageDelivery(any(CampaignMessageDeliveryReportRequest.class));

        new CallDetailsReportHandler(reportingGateway).handleSuccessfulMessageDelivery(new MotechEvent(ReportingEventKeys.REPORT_CAMPAIGN_MESSAGE_DELIVERED, parameters));
    }
}
