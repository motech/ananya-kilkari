package org.motechproject.ananya.kilkari.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.messagecampaign.request.KilkariMessageCampaignEnrollmentRecord;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KilkariCampaignService {

    public static final String KILKARI_MESSAGE_CAMPAIGN_NAME = "kilkari-mother-child-campaign";

    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Autowired
    public KilkariCampaignService(KilkariMessageCampaignService kilkariMessageCampaignService) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
    }

    public List<DateTime> getMessageTimings(String msisdn) {
        return kilkariMessageCampaignService.getMessageTimings(msisdn, KILKARI_MESSAGE_CAMPAIGN_NAME);
    }

    public KilkariMessageCampaignEnrollmentRecord getCampaignEnrollmentRecordFor(String subscriptionId){
        return kilkariMessageCampaignService.searchEnrollment(subscriptionId, KILKARI_MESSAGE_CAMPAIGN_NAME);
    }
}
