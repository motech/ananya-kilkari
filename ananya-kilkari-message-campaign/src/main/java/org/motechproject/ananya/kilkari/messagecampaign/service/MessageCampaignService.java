package org.motechproject.ananya.kilkari.messagecampaign.service;

import ch.lambdaj.Lambda;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequest;
import org.motechproject.ananya.kilkari.messagecampaign.request.MessageCampaignRequestMapper;
import org.motechproject.ananya.kilkari.messagecampaign.response.MessageCampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;

@Service("kilkariMessageCampaignService")
public class MessageCampaignService {

    public static final String FIFTEEN_MONTHS_CAMPAIGN_KEY = "kilkari-mother-child-campaign-fifteen-months";
    public static final String TWELVE_MONTHS_CAMPAIGN_KEY = "kilkari-mother-child-campaign-twelve-months";
    public static final String SEVEN_MONTHS_CAMPAIGN_KEY = "kilkari-mother-child-campaign-seven-months";
    public static final String INFANT_DEATH_CAMPAIGN_KEY = "kilkari-mother-child-campaign-infant-death";
    public static final String MISCARRIAGE_CAMPAIGN_KEY = "kilkari-mother-child-campaign-miscarriage";
    private final static Logger logger = LoggerFactory.getLogger(MessageCampaignService.class);

    public static final String CAMPAIGN_MESSAGE_NAME = "Mother Child Health Care";
    private org.motechproject.server.messagecampaign.service.MessageCampaignService campaignService;

    @Autowired
    public MessageCampaignService(org.motechproject.server.messagecampaign.service.MessageCampaignService campaignService) {
        this.campaignService = campaignService;
    }

    public void start(MessageCampaignRequest campaignRequest, Integer campaignScheduleDeltaDays, Integer campaignScheduleDeltaMinutes) {
        campaignService.startFor(MessageCampaignRequestMapper.newRequestFrom(campaignRequest, campaignScheduleDeltaDays, campaignScheduleDeltaMinutes));
    }

    public void stop(MessageCampaignRequest enrollRequest) {
        if(StringUtils.isEmpty(enrollRequest.getCampaignName())) {
            logger.warn("No active campaign found to unenroll for subscriptionId : " + enrollRequest.getExternalId());
            return;
        }
        campaignService.stopAll(MessageCampaignRequestMapper.newRequestFrom(enrollRequest, 0, 0));
    }

    public List<MessageCampaignEnrollment> searchEnrollments(String externalId) {
        List<CampaignEnrollmentRecord> enrollmentRecords = campaignService.search(
                new CampaignEnrollmentsQuery().withExternalId(externalId));

        if (enrollmentRecords.isEmpty())
            return null;

        List<MessageCampaignEnrollment> messageCampaignEnrollments = new ArrayList<>();
        for (CampaignEnrollmentRecord campaignEnrollmentRecord : enrollmentRecords) {
            messageCampaignEnrollments.add(new MessageCampaignEnrollment(campaignEnrollmentRecord.getExternalId(),
                    campaignEnrollmentRecord.getCampaignName(), campaignEnrollmentRecord.getReferenceDate(),
                    campaignEnrollmentRecord.getStatus()));
        }
        return messageCampaignEnrollments;
    }

    public List<DateTime> getMessageTimings(String subscriptionId, DateTime startDate, DateTime endDate) {
        String campaignName = getActiveCampaignName(subscriptionId);
        List < DateTime > alertTimings = new ArrayList<>();
        if (StringUtils.isEmpty(campaignName))
            return alertTimings;

        Map<String, List<Date>> campaignTimings = campaignService.getCampaignTimings(subscriptionId, campaignName,
                startDate.toDate(), endDate.toDate());
        List<Date> campaignMessageTimings = campaignTimings.get(CAMPAIGN_MESSAGE_NAME);

        if (campaignMessageTimings == null || campaignMessageTimings.isEmpty())
            return alertTimings;

        for (Date date : campaignMessageTimings) {
            alertTimings.add(new DateTime(date));
        }
        return alertTimings;
    }

    public DateTime getCampaignStartDate(String subscriptionId, String campaignName) {
        List<MessageCampaignEnrollment> enrollmentsForCampaign = Lambda.select(searchEnrollments(subscriptionId),
                having(on(MessageCampaignEnrollment.class).getCampaignName(),
                        Matchers.is(campaignName)));
        return enrollmentsForCampaign.get(0).getStartDate();
    }

    public DateTime getActiveCampaignStartDate(String subscriptionId) {
        List<MessageCampaignEnrollment> activeCampaigns = getActiveCampaigns(subscriptionId);
        if(activeCampaigns.isEmpty())
            return null;
        return activeCampaigns.get(0).getStartDate();
    }

    public String getActiveCampaignName(String subscriptionId) {
        List<MessageCampaignEnrollment> activeCampaigns = getActiveCampaigns(subscriptionId);
        if(activeCampaigns.isEmpty())
            return null;
        return activeCampaigns.get(0).getCampaignName();
    }

    private List<MessageCampaignEnrollment> getActiveCampaigns(String subscriptionId) {
        return Lambda.select(searchEnrollments(subscriptionId),
                having(on(MessageCampaignEnrollment.class).getStatus(),
                        Matchers.is(CampaignEnrollmentStatus.ACTIVE.name())));
    }
}
