package org.motechproject.ananya.kilkari.web.it;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecordObject;
import org.motechproject.ananya.kilkari.obd.gateway.OnMobileOBDGateway;
import org.motechproject.ananya.kilkari.obd.gateway.StubOnMobileOBDGateway;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.controller.OBDController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.motechproject.ananya.kilkari.web.controller.ResponseMatchers.baseResponseMatcher;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class OBDControllerIT extends SpringIntegrationTest {
    @Autowired
    private OBDController obdController;
    @Autowired
    private AllSubscriptions allSubscriptions;

    @Autowired
    private StubOnMobileOBDGateway stubOnMobileOBDGateway;

    @Before
    public void setUp() {
        allSubscriptions.removeAll();
    }

    @Test
    public void shouldSendInvalidCallDeliveryFailureRecordsToObd() throws Exception {
        String msisdn = "1234567890";
        Subscription subscription1 = new Subscription(msisdn, SubscriptionPack.TWELVE_MONTHS, DateTime.now());
        Subscription subscription2 = new Subscription(msisdn, SubscriptionPack.FIFTEEN_MONTHS, DateTime.now());
        allSubscriptions.add(subscription1);
        allSubscriptions.add(subscription2);
        markForDeletion(subscription1);
        markForDeletion(subscription2);

        OnMobileOBDGateway onMobileOBDGateway = mock(OnMobileOBDGateway.class);
        stubOnMobileOBDGateway.setBehavior(onMobileOBDGateway);

        String callDeliveryFailureRecord1 = createCallDeliveryFailureRecordJSON(subscription1.getSubscriptionId(), subscription1.getMsisdn(), "WEEK13", "DNP");
        String callDeliveryFailureRecord2 = createCallDeliveryFailureRecordJSON(subscription2.getSubscriptionId(), subscription2.getMsisdn(), "WEEK", "DNP");
        String requestBody = "{\"callrecords\": [" + callDeliveryFailureRecord1 + "," + callDeliveryFailureRecord2 + "]}";
        mockMvc(obdController)
                .perform(post("/obd/calldetails").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(content().string(baseResponseMatcher("SUCCESS", "OBD call delivery failure records received successfully")));

        new TimedRunner<Boolean>(20, 1000) {
            @Override
            public Boolean run() {
                return stubOnMobileOBDGateway.isInvalidFailureRecordCalled() ? Boolean.TRUE : null;
            }
        }.execute();

        ArgumentCaptor<InvalidCallDeliveryFailureRecord> invalidCallDeliveryFailureRecordArgumentCaptor = ArgumentCaptor.forClass(InvalidCallDeliveryFailureRecord.class);
        verify(onMobileOBDGateway).sendInvalidFailureRecord(invalidCallDeliveryFailureRecordArgumentCaptor.capture());
        List<InvalidCallDeliveryFailureRecordObject> recordObjects = invalidCallDeliveryFailureRecordArgumentCaptor.getValue().getRecordObjects();

        assertEquals(1, recordObjects.size());
        assertEquals(msisdn, recordObjects.get(0).getMsisdn());
        assertEquals(subscription2.getSubscriptionId(), recordObjects.get(0).getSubscriptionId());
        assertEquals("Invalid campaign id WEEK", recordObjects.get(0).getDescription());
    }

    private String createCallDeliveryFailureRecordJSON(String subscriptionId, String msisdn, String campaignId, String statusCode) {
        String jsonTemplate = "{ \"subscriptionId\":\"%s\",\"msisdn\":\"%s\",\"campaignId\":\"%s\",\"statusCode\":\"%s\"}";
        return String.format(jsonTemplate, subscriptionId, msisdn, campaignId, statusCode);
    }
}
