package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.web.contract.response.CampaignSchedule;
import org.motechproject.ananya.kilkari.web.contract.response.UserCampaignSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/messagecampaign")
public class MessageCampaignVisualizationController {

    private KilkariCampaignService kilkariCampaignService;

    @Autowired
    public MessageCampaignVisualizationController(KilkariCampaignService kilkariCampaignService) {
        this.kilkariCampaignService = kilkariCampaignService;
    }

    @RequestMapping(value = "/visualize", method = RequestMethod.GET)
    @ResponseBody
    public UserCampaignSchedule getVisualizationForUser(@RequestParam String msisdn) {
        HashMap<String, List<DateTime>> subscriptionCampaignMap = kilkariCampaignService.getMessageTimings(msisdn);

        UserCampaignSchedule userCampaignSchedule = new UserCampaignSchedule(msisdn);
        for(List<DateTime> subscriptionCampaign : subscriptionCampaignMap.values()){
            userCampaignSchedule.addCampaignSchedule(new CampaignSchedule(msisdn, subscriptionCampaign));
        }
        return userCampaignSchedule;
    }

}
