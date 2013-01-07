package org.motechproject.ananya.kilkari.test.data;

import org.motechproject.ananya.kilkari.obd.domain.CampaignMessage;
import org.motechproject.ananya.kilkari.obd.repository.AllCampaignMessages;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.subscription.repository.KilkariPropertiesData;
import org.motechproject.ananya.kilkari.test.data.contract.SubscriberSubscriptions;
import org.motechproject.ananya.kilkari.test.data.contract.SubscriptionRequest;
import org.motechproject.ananya.kilkari.test.data.contract.builders.SubscriptionRequestBuilder;
import org.motechproject.ananya.kilkari.test.data.utils.TimedRunner;
import org.motechproject.ananya.kilkari.test.data.utils.TimedRunnerResponse;
import org.motechproject.ananya.kilkari.web.response.SubscriptionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.motechproject.ananya.kilkari.test.data.utils.TestUtils.constructUrl;
import static org.motechproject.ananya.kilkari.test.data.utils.TestUtils.fromJson;

@ContextConfiguration("classpath:applicationKilkariTestDataContext.xml")
public class BaseDataSetup {
    @Autowired
    private TestDataConfig testDataConfig;

    @Autowired
    protected KilkariPropertiesData kilkariProperties;

    @Autowired
    private AllCampaignMessages allCampaignMessages;

    private RestTemplate restTemplate;


    public BaseDataSetup() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        System.out.println(ctx.getEnvironment().getActiveProfiles()[0]);
        restTemplate = new RestTemplate();
    }



    protected String createSubscriptionForCallCenter() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestBuilder().withDefaults().withEDD(null).withDOB(null).build();
        Map<String, String> parametersMap = new HashMap<>();
        String channel = "CONTACT_CENTER";
        parametersMap.put("channel", channel);
        restTemplate.postForEntity(constructUrl(baseUrl(), "subscription", parametersMap), subscriptionRequest, String.class);
        return subscriptionRequest.getMsisdn();
    }

    protected SubscriptionDetails getSubscriptionDetails(final String msisdn) {
        Map<String, String> parametersMap = new HashMap<String, String>(){{
            put("channel", "CONTACT_CENTER");
            put("msisdn",msisdn);
        }};
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(new ArrayList<MediaType>(){{
            add(MediaType.APPLICATION_JSON);
        }});

        ResponseEntity<String> subscriber = restTemplate.exchange(constructUrl(baseUrl(), "subscriber", parametersMap), HttpMethod.GET, new HttpEntity<byte[]>(headers), String.class);
        SubscriberSubscriptions subscriptionDetails = fromJson(subscriber.getBody(), SubscriberSubscriptions.class);
        return subscriptionDetails.getSubscriptionDetails().get(0);
    }

    protected void activateSubscription(String msisdn, String subscriptionId, String status, String operator){
        Map<String, String> parametersMap = new HashMap<String, String>(){{
            put("_method", "PUT");

        }};
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn(msisdn);
        callbackRequest.setAction("ACT");
        callbackRequest.setReason("By script");
        callbackRequest.setOperator(operator);
        callbackRequest.setStatus(status);
        restTemplate.put(constructUrl(baseUrl(), "subscription/" + subscriptionId, parametersMap), callbackRequest);
    }

    protected void renewSubscription(String msisdn, String subscriptionId, String status, String operator){
        Map<String, String> parametersMap = new HashMap<String, String>(){{
            put("_method", "PUT");

        }};
        CallbackRequest callbackRequest = new CallbackRequest();
        callbackRequest.setMsisdn(msisdn);
        callbackRequest.setAction("REN");
        callbackRequest.setReason("By script");
        callbackRequest.setOperator(operator);
        callbackRequest.setStatus(status);
        restTemplate.put(constructUrl(baseUrl(), "subscription/" + subscriptionId, parametersMap), callbackRequest);
    }

    protected void waitForCampaignAlert(final String subscriptionId, final String weekMessageId) {
        CampaignMessage campaignMessage = new TimedRunner<CampaignMessage>(120, 1000) {
            public TimedRunnerResponse<CampaignMessage> run() {
                CampaignMessage campaignMessage = findOBDCampaignMessage(subscriptionId, weekMessageId);
                return campaignMessage == null ? null : new TimedRunnerResponse<>(campaignMessage);
            }
        }.executeWithTimeout();
        assertNotNull(campaignMessage);
    }

    private CampaignMessage findOBDCampaignMessage(String subscriptionId, String weekMessageId) {
        return allCampaignMessages.find(subscriptionId, weekMessageId);
    }
    protected String baseUrl() {
        return testDataConfig.baseUrl();
    }
}
