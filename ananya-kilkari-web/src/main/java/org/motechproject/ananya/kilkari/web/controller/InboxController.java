package org.motechproject.ananya.kilkari.web.controller;


import org.motechproject.ananya.kilkari.request.InboxCallDetailsRequestsList;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public class InboxController {

    @RequestMapping(value = "/inbox/calldetails", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse recordInboxCallDetails(InboxCallDetailsRequestsList inboxCallDetailsRequestsList) {
        return BaseResponse.success("Inbox calldetails request submitted successfully");
    }
}
