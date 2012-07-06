package org.motechproject.ananya.kilkari.messagecampaign.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.messagecampaign.mapper.KilkariMessageCampaignEnrollmentRecordMapper;
import org.motechproject.ananya.kilkari.messagecampaign.mapper.KilkariMessageCampaignRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignEnrollmentRecord;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignRequest;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class KilkariMessageCampaignService {

    public static final String CAMPAIGN_NAME = "kilkari-mother-child-campaign";
    public static final String CAMPAIGN_MESSAGE_NAME = "Mother Child Health Care";

    private MessageCampaignService campaignService;

    @Autowired
    public KilkariMessageCampaignService(MessageCampaignService campaignService) {
        this.campaignService = campaignService;
    }

    public void start(KilkariMessageCampaignRequest campaignRequest) {
        campaignService.startFor(KilkariMessageCampaignRequestMapper.newRequestFrom(campaignRequest));
    }

    public boolean stop(KilkariMessageCampaignRequest enrollRequest) {
        campaignService.stopAll(KilkariMessageCampaignRequestMapper.newRequestFrom(enrollRequest));
        return true;
    }

    public KilkariMessageCampaignEnrollmentRecord searchEnrollment(String externalId, String campaignName) {
        List<CampaignEnrollmentRecord> enrollmentRecords = campaignService.search(
                new CampaignEnrollmentsQuery().withExternalId(externalId).withCampaignName(campaignName));

        return enrollmentRecords.size() > 0
                ? KilkariMessageCampaignEnrollmentRecordMapper.map(enrollmentRecords.get(0))
                : null;
    }

    public List<DateTime> getMessageTimings(String subscriptionId, String campaignName, DateTime startDate, DateTime endDate) {

        Map<String, List<Date>> campaignTimings = campaignService.getCampaignTimings(subscriptionId, campaignName,
                startDate.toDate(), endDate.toDate());
        List<Date> campaignMessageTimings = campaignTimings.get(CAMPAIGN_MESSAGE_NAME);

        List<DateTime> alertTimings = new ArrayList<>();
        if (campaignMessageTimings == null || campaignMessageTimings.isEmpty())
            return alertTimings;

        for (Date date : campaignMessageTimings) {
            alertTimings.add(new DateTime(date.getTime()));
        }
        return alertTimings;
    }
}
