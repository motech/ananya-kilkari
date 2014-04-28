package org.motechproject.ananya.kilkari.performance.tests.service.db;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionDbService {

    @Autowired
    private AllSubscriptions allSubscriptions;
    @Autowired
    private HttpUtils httpUtils;

    public void addSubscription(Subscription subscription) {
        allSubscriptions.add(subscription);
        SubscriptionReportRequest subscriptionReportRequest = new SubscriptionReportRequest(subscription.getSubscriptionId(),
                Channel.CONTACT_CENTER.toString(), Long.parseLong(subscription.getMsisdn()), subscription.getPack().toString(),
                null, null, subscription.getCreationDate(), subscription.getStatus().toString(), null, null, null,
                subscription.getOperator().toString(), subscription.getStartDate(), null, null, null, null, false, false, "ivr");
        httpUtils.httpPostReports(null, subscriptionReportRequest, "subscription");
    }

    public void warmIndexes() {
        for(int i = 0; i < 10 ; i++) {
            try {
                allSubscriptions.findBySubscriptionId("asdasd");
            } catch (Exception e) {
                System.out.println("Exception warming indexes : " + e.toString());
            }
        }
    }
}
