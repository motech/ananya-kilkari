package org.motechproject.ananya.kilkari.web.controller;

import org.motechproject.MotechException;
import org.motechproject.ananya.kilkari.web.controller.page.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    private LoginPage loginPage;

    @Autowired
    public AdminController(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admin/login")
    public ModelAndView login(HttpServletRequest request) {
        final String error = request.getParameter("login_error");
        return loginPage.display(error);
    }

    private boolean validLogin(UserDetails userDetails) {
        return userDetails.isAccountNonExpired() &&
                userDetails.isAccountNonLocked() &&
                userDetails.isCredentialsNonExpired() &&
                userDetails.isEnabled();
    }
}
