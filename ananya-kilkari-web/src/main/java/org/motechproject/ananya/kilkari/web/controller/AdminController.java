package org.motechproject.ananya.kilkari.web.controller;

import org.motechproject.ananya.kilkari.web.controller.page.InquiryPage;
import org.motechproject.ananya.kilkari.web.controller.page.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);
    private LoginPage loginPage;
    private InquiryPage inquiryPage;

    @Autowired
    public AdminController(LoginPage loginPage, InquiryPage inquiryPage) {
        this.loginPage = loginPage;
        this.inquiryPage = inquiryPage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/login")
    public ModelAndView login(HttpServletRequest request) {
        final String error = request.getParameter("login_error");
        return loginPage.display(error);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/inquiry")
    public ModelAndView displayInquiryPage() {
        return inquiryPage.display();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/inquiry/data")
    @ResponseBody
    public Map<String, Object> getInquiryData(@RequestParam String msisdn) {
        return inquiryPage.getSubscriptionDetails(msisdn);
    }
}
