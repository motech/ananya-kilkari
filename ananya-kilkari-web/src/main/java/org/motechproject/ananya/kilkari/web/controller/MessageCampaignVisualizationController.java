package org.motechproject.ananya.kilkari.web.controller;

import org.joda.time.DateTime;
import org.motechproject.ananya.kilkari.service.KilkariCampaignService;
import org.motechproject.ananya.kilkari.web.response.CampaignSchedule;
import org.motechproject.ananya.kilkari.web.response.UserCampaignSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
        Map<String, List<DateTime>> subscriptionCampaignMap = kilkariCampaignService.getMessageTimings(msisdn);

        UserCampaignSchedule userCampaignSchedule = new UserCampaignSchedule(msisdn);
        for(String subscriptionId :subscriptionCampaignMap.keySet()){
            userCampaignSchedule.addCampaignSchedule(new CampaignSchedule(subscriptionId, subscriptionCampaignMap.get(subscriptionId)));
        }
        return userCampaignSchedule;
    }

}
