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

import java.util.*;

@Service
public class KilkariMessageCampaignService {

    private MessageCampaignService campaignService;

    @Autowired
    public KilkariMessageCampaignService(MessageCampaignService campaignService) {
        this.campaignService = campaignService;
    }

    public Boolean start(KilkariMessageCampaignRequest campaignRequest) {
        campaignService.startFor(KilkariMessageCampaignRequestMapper.map(campaignRequest));
        return true;
    }

    public Boolean stop(KilkariMessageCampaignRequest enrollRequest) {
        campaignService.stopAll(KilkariMessageCampaignRequestMapper.map(enrollRequest));
        return true;
    }

    public KilkariMessageCampaignEnrollmentRecord searchEnrollment(String externalId, String campaignName) {
        List<CampaignEnrollmentRecord> enrollmentRecords = campaignService.search(
                new CampaignEnrollmentsQuery().withExternalId(externalId).withCampaignName(campaignName));

        return enrollmentRecords.size() > 0
                ? KilkariMessageCampaignEnrollmentRecordMapper.map(enrollmentRecords.get(0))
                : null;
    }

    public List<DateTime> getMessageTimings(String subscriptionId, String campaignName) {
          //TODO:commenting the actual method until new platform release
//        DateTime now = DateTime.now();
//        Map<String,List<Date>> campaignTimings = campaignService.getCampaignTimings(subscriptionId, campaignName,
//                now.toDate(), now.plusYears(1).toDate());
//        List<Date> dateList = campaignTimings.get(campaignName);
//        List<DateTime> messageTimings = new ArrayList<>();
//        if(dateList == null ||dateList.isEmpty())
//            return messageTimings;
//        for (Date date : dateList) {
//            messageTimings.add(new DateTime(date.getTime()));
//        }
//        return messageTimings;
        return Collections.emptyList();
    }
}
