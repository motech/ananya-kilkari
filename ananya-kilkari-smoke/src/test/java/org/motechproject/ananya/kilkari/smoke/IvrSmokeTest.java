package org.motechproject.ananya.kilkari.smoke;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.smoke.domain.kilkari.BaseResponse;
import org.motechproject.ananya.kilkari.smoke.domain.kilkari.SubscriberResponse;
import org.motechproject.ananya.kilkari.smoke.domain.kilkari.SubscriptionDetails;
import org.motechproject.ananya.kilkari.smoke.domain.report.SubscriptionStatusMeasure;
import org.motechproject.ananya.kilkari.smoke.service.ReportService;
import org.motechproject.ananya.kilkari.smoke.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariSmokeContext.xml")
public class IvrSmokeTest {
    RestTemplate restTemplate;
    SubscriptionService subscriptionService;
    @Autowired
    ReportService reportService;

    @Before
    public void setUp() throws SQLException {
        subscriptionService = new SubscriptionService();
        restTemplate = new RestTemplate();
        reportService.deleteAll();
    }

    @Test(timeout = 20000)
    public void shouldPostHttpRequestAndVerifyEntriesInReportDbAndCouchDb() throws InterruptedException, SQLException {
        String channel = "IVR";
        String msisdn = "9000000001";
        String pack = "FIFTEEN_MONTHS";
        String expectedStatus = "PENDING_ACTIVATION";

        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", msisdn);
        parametersMap.put("channel", channel);
        parametersMap.put("pack", pack);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(constructUrl(KILKARI_URL, "subscription", parametersMap), String.class);

        BaseResponse baseResponse = fromJson(responseEntity.getBody().replace("var response = ", ""), BaseResponse.class);
        assertEquals("SUCCESS", baseResponse.getStatus());

        SubscriberResponse response = subscriptionService.getSubscriptionData(msisdn, channel, expectedStatus);
        assertEquals(1, response.getSubscriptionDetails().size());
        assertKilkariData(pack, expectedStatus, response);

        List<SubscriptionStatusMeasure> subscriptionStatusMeasures = reportService.getSubscriptionStatusMeasureForMsisdn(msisdn);
        assertEquals(2, subscriptionStatusMeasures.size());
        assertReportData(subscriptionStatusMeasures, channel, msisdn, pack, expectedStatus);
    }

    private void assertReportData(List<SubscriptionStatusMeasure> subscriptionStatusMeasures, String channel, String msisdn, String pack, String expectedStatus) {
        assertEquals(msisdn, subscriptionStatusMeasures.get(0).getMsisdn());
        assertEquals(pack, subscriptionStatusMeasures.get(0).getPack().toUpperCase());
        assertEquals(channel, subscriptionStatusMeasures.get(0).getChannel().toUpperCase());
        assertEquals("NEW", subscriptionStatusMeasures.get(0).getStatus().toUpperCase());

        assertEquals(msisdn, subscriptionStatusMeasures.get(1).getMsisdn());
        assertEquals(pack, subscriptionStatusMeasures.get(1).getPack().toUpperCase());
        assertEquals(channel, subscriptionStatusMeasures.get(1).getChannel().toUpperCase());
        assertEquals(expectedStatus, subscriptionStatusMeasures.get(1).getStatus().toUpperCase());
    }

    private void assertKilkariData(String pack, String expectedStatus, SubscriberResponse response) {
        SubscriptionDetails subscriptionDetails = response.getSubscriptionDetails().get(0);
        assertEquals(pack, subscriptionDetails.getPack());
        assertEquals(expectedStatus, subscriptionDetails.getStatus());
    }
}