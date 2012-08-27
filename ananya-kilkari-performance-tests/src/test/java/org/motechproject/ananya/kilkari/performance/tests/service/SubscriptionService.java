package org.motechproject.ananya.kilkari.performance.tests.service;

import org.apache.commons.lang.RandomStringUtils;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.TimedRunner;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.subscription.domain.Operator;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SubscriptionService {

    public Subscription getSubscriptionData(final String msisdn, final String status) throws InterruptedException {
        return new TimedRunner<Subscription>(100, 1000) {
            @Override
            protected Subscription run() {
                List<Subscription> subscriptionList = ContextUtils.getConfiguration().getAllSubscriptions().findByMsisdn(msisdn);
                if (subscriptionList.size() != 0 && subscriptionList.get(0).getStatus().name().equals(status))
                    return subscriptionList.get(0);
                return null;
            }
        }.executeWithTimeout();
    }

    public void createASubscription() {
        Map<String, String> parametersMap = constructParameters();

        BaseResponse baseResponse = HttpUtils.httpGetWithJsonResponse(parametersMap, "subscription");
        assertEquals("SUCCESS", baseResponse.getStatus());
    }

    public void activate(Subscription subscription) {
        CallbackRequest request = new CallbackRequest();
        request.setAction("ACT");
        request.setStatus("SUCCESS");
        request.setMsisdn(subscription.getMsisdn());
        request.setOperator(Operator.AIRTEL.name());
        HttpUtils.put(request, "subscription/" + subscription.getSubscriptionId());
    }

    private Map<String, String> constructParameters() {
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("msisdn", getRandomMsisdn());
        parametersMap.put("channel", "IVR");
        parametersMap.put("pack", "bari_kilkari");
        return parametersMap;

    }

    private String getRandomMsisdn() {
        return "9" + RandomStringUtils.randomNumeric(9);
    }

    public List<Subscription> getAll() {
        return ContextUtils.getConfiguration().getAllSubscriptions().getAll();
    }
}
