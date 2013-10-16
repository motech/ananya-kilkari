package org.motechproject.ananya.kilkari.subscription.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.reporting.service.ReportingService;
import org.motechproject.ananya.kilkari.subscription.domain.SubscriberCareDoc;
import org.motechproject.ananya.kilkari.subscription.repository.AllSubscriberCareDocs;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriberCareReportRequestMapper;
import org.motechproject.ananya.kilkari.subscription.service.mapper.SubscriberCareRequestMapper;
import org.motechproject.ananya.kilkari.subscription.service.request.SubscriberCareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SubscriberCareService {

    private AllSubscriberCareDocs allSubscriberCareDocs;
    private ReportingService reportingService;

    @Autowired
    public SubscriberCareService(AllSubscriberCareDocs allSubscriberCareDocs, ReportingService reportingService) {
        this.allSubscriberCareDocs = allSubscriberCareDocs;
        this.reportingService = reportingService;
    }

    public void create(SubscriberCareRequest subscriberCareRequest) {
        allSubscriberCareDocs.add(SubscriberCareRequestMapper.map(subscriberCareRequest));
        reportingService.reportCareRequest(SubscriberCareReportRequestMapper.map(subscriberCareRequest));
    }

    public List<SubscriberCareDoc> getAllSortedByDate(DateTime startDate, DateTime endDate) {
        List<SubscriberCareDoc> subscriberCareDocList = allSubscriberCareDocs.findByCreatedAt(startDate, endDate);
        Collections.sort(subscriberCareDocList);
        return subscriberCareDocList;
    }
}
