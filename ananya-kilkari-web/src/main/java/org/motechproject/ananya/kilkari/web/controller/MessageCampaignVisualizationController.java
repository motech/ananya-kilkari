package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.messagecampaign.service.KilkariMessageCampaignService;
import org.motechproject.ananya.kilkari.web.response.CampaignSchedule;
import org.motechproject.ananya.kilkari.web.response.UserCampaignSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/messagecampaign")
public class MessageCampaignVisualizationController {

    public static final String KILKARI_MESSAGE_CAMPAIGN_NAME = "kilkari-mother-child-campaign";

    private KilkariMessageCampaignService kilkariMessageCampaignService;

    @Autowired
    public MessageCampaignVisualizationController(
            KilkariMessageCampaignService kilkariMessageCampaignService) {
        this.kilkariMessageCampaignService = kilkariMessageCampaignService;
    }

    @RequestMapping(value = "/visualize", method = RequestMethod.GET)
    @ResponseBody
    public UserCampaignSchedule getVisualizationForUser(@RequestParam String msisdn) {
        List<DateTime> messageTimings = kilkariMessageCampaignService.getMessageTimings(msisdn, KILKARI_MESSAGE_CAMPAIGN_NAME);

        UserCampaignSchedule userCampaignSchedule = new UserCampaignSchedule(msisdn);
        userCampaignSchedule.addCampaignSchedule(new CampaignSchedule(msisdn, messageTimings));

        return userCampaignSchedule;
    }

}
