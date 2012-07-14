package org.motechproject.ananya.kilkari.smoke;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.kilkari.smoke.domain.kilkari.*;
import org.motechproject.ananya.kilkari.smoke.domain.report.SubscriptionStatusMeasure;
import org.motechproject.ananya.kilkari.smoke.service.ReportServiceAsync;
import org.motechproject.ananya.kilkari.smoke.service.SubscriptionServiceAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.KILKARI_SUBSCRIPTION_POST_URL;
import static org.motechproject.ananya.kilkari.smoke.utils.TestUtils.fromJson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationKilkariSmokeContext.xml")
public class CallCenterSmokeTest {
    RestTemplate restTemplate;
    SubscriptionServiceAsync subscriptionServiceAsync;
    @Autowired
    ReportServiceAsync reportServiceAsync;

    @Before
    public void setUp() throws SQLException {
        subscriptionServiceAsync = new SubscriptionServiceAsync();
        restTemplate = new RestTemplate();
        reportServiceAsync.deleteAll();
        reportServiceAsync.createNewLocation("D1", "B1", "P1");
    }

    @Test(timeout = 20000)
    public void shouldPostHttpRequestAndVerifyEntriesInReportDbAndCouchDb() throws InterruptedException, SQLException {
        String channel = "CALL_CENTER";
        String msisdn = "9000000002";
        String pack = "FIFTEEN_MONTHS";
        String expectedStatus = "PENDING_ACTIVATION";
        DateTime createdAt = DateTime.now();
        String beneficiaryName = "John Doe";
        String beneficiaryAge = "24";
        String dateOfBirth = DateTime.now().minusYears(24).toString("dd-MM-yyyy");
        String estimatedDateOfDelivery = DateTime.now().plusDays(20).toString("dd-MM-yyyy");
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(msisdn, pack, channel, createdAt, beneficiaryName, beneficiaryAge, estimatedDateOfDelivery, dateOfBirth, new Location("D1", "B1", "P1"));

        String responseEntity = restTemplate.postForObject(KILKARI_SUBSCRIPTION_POST_URL, subscriptionRequest, String.class);

        BaseResponse baseResponse = fromJson(responseEntity.replace("var response = ", ""), BaseResponse.class);
        assertEquals("SUCCESS", baseResponse.getStatus());

        SubscriberResponse response = subscriptionServiceAsync.getSubscriptionData(msisdn, channel, expectedStatus);
        assertEquals(1, response.getSubscriptionDetails().size());
        assertKilkariData(pack, expectedStatus, response);

        List<SubscriptionStatusMeasure> subscriptionStatusMeasures = reportServiceAsync.getSubscriptionStatusMeasureForMsisdn(msisdn);
        assertEquals(2, subscriptionStatusMeasures.size());
        assertReportData(subscriptionStatusMeasures, channel, msisdn, pack, expectedStatus, beneficiaryName, beneficiaryAge, dateOfBirth, estimatedDateOfDelivery);
    }

    private void assertKilkariData(String pack, String expectedStatus, SubscriberResponse response) {
        SubscriptionDetails subscriptionDetails = response.getSubscriptionDetails().get(0);
        assertEquals(pack, subscriptionDetails.getPack());
        assertEquals(expectedStatus, subscriptionDetails.getStatus());
    }

    private void assertReportData(List<SubscriptionStatusMeasure> subscriptionStatusMeasures, String channel, String msisdn, String pack, String expectedStatus, String beneficiaryName, String beneficiaryAge, String dateOfBirth, String estimatedDateOfDelivery) {
        assertEquals(msisdn, subscriptionStatusMeasures.get(0).getMsisdn());
        assertEquals(pack, subscriptionStatusMeasures.get(0).getPack().toUpperCase());
        assertEquals(channel, subscriptionStatusMeasures.get(0).getChannel().toUpperCase());
        assertEquals("NEW", subscriptionStatusMeasures.get(0).getStatus().toUpperCase());
        assertEquals(beneficiaryName, subscriptionStatusMeasures.get(0).getName());
        assertEquals(beneficiaryAge, subscriptionStatusMeasures.get(0).getAge());
        assertEquals(dateOfBirth, new DateTime(subscriptionStatusMeasures.get(0).getDateOfBirth()).toString("dd-MM-yyyy"));
        assertEquals(estimatedDateOfDelivery, new DateTime(subscriptionStatusMeasures.get(0).getEstimatedDateOfDelivery()).toString("dd-MM-yyyy"));

        assertEquals(msisdn, subscriptionStatusMeasures.get(1).getMsisdn());
        assertEquals(pack, subscriptionStatusMeasures.get(1).getPack().toUpperCase());
        assertEquals(channel, subscriptionStatusMeasures.get(1).getChannel().toUpperCase());
        assertEquals(expectedStatus, subscriptionStatusMeasures.get(1).getStatus().toUpperCase());
    }
}