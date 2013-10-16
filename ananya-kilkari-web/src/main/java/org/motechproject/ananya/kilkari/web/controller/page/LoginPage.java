package org.motechproject.ananya.kilkari.web.controller.page;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginPage {

    private String viewName = "admin/login";

    public ModelAndView display(String error) {
        return new ModelAndView(viewName).addObject("error", error);
    }
}