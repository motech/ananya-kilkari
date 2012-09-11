package org.motechproject.ananya.kilkari.performance.tests.service.db;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.performance.tests.utils.ContextUtils;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.Subscription;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriptions;
import org.motechproject.ananya.reports.kilkari.contract.request.SubscriptionReportRequest;

public class SubscriptionDbService {

    private AllSubscriptions allSubscriptions;

    private ReportingService reportingService;

    public SubscriptionDbService() {
        allSubscriptions = ContextUtils.getConfiguration().getAllSubscriptions();
        reportingService = ContextUtils.getConfiguration().getReportingService();
    }

    public void addSubscription(Subscription subscription) {
        allSubscriptions.add(subscription);
        reportingService.reportSubscriptionCreation(new SubscriptionReportRequest(subscription.getSubscriptionId(),
                Channel.CALL_CENTER.toString(), Long.parseLong(subscription.getMsisdn()), subscription.getPack().toString(),
                null, null, subscription.getCreationDate(), subscription.getStatus().toString(), null, null, null,
                subscription.getOperator().toString(), subscription.getStartDate(), null, null));
    }
}
