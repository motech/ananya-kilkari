package org.motechproject.ananya.kilkari.performance.tests.service.db;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;
import org.motechproject.ananya.kilkari.performance.tests.utils.HttpUtils;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionReportRequest;

public class SubscriptionDbService {

    private AllSubscriptions allSubscriptions;

    public SubscriptionDbService() {
        allSubscriptions = ContextUtils.getConfiguration().getAllSubscriptions();
    }

    public void addSubscription(Subscription subscription) {
        allSubscriptions.add(subscription);
        SubscriptionReportRequest subscriptionReportRequest = new SubscriptionReportRequest(subscription.getSubscriptionId(),
                Channel.CALL_CENTER.toString(), Long.parseLong(subscription.getMsisdn()), subscription.getPack().toString(),
                null, null, subscription.getCreationDate(), subscription.getStatus().toString(), null, null, null,
                subscription.getOperator().toString(), subscription.getStartDate(), null, null);
        HttpUtils.httpPostReports(null, subscriptionReportRequest, "subscription");
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
