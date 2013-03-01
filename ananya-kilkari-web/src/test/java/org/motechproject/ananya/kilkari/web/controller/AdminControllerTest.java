package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.web.controller.page.InquiryPage;
import org.motechproject.ananya.kilkari.web.controller.page.LoginPage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @Mock
    private LoginPage loginPage;
    @Mock
    private InquiryPage inquiryPage;
    private AdminController adminController;

    @Before
    public void setUp() {
        adminController = new AdminController(loginPage, inquiryPage);
    }

    @Test
    public void shouldLoginErrors() {
        String loginError = "error";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("login_error")).thenReturn(loginError);
        ModelAndView expectedModelAndView = new ModelAndView();
        when(loginPage.display(loginError)).thenReturn(expectedModelAndView);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn("anonymousUser");

        ModelAndView actualModelAndView = adminController.login(request);

        assertEquals(expectedModelAndView, actualModelAndView);
    }

    @Test
    public void shouldDisplayInquiryPage() {
        ModelAndView expectedModelAndView = new ModelAndView();
        when(inquiryPage.display()).thenReturn(expectedModelAndView);

        ModelAndView modelAndView = adminController.displayInquiryPage();

        assertEquals(expectedModelAndView, modelAndView);
    }

    @Test
    public void shouldGetInquiryData() {
        String msisdn = "msisdn";
        HashMap<String, Object> expectedSubscriptionDetails = new HashMap<>();
        when(inquiryPage.getSubscriptionDetails(msisdn)).thenReturn(expectedSubscriptionDetails);

        Map<String, Object> actualSubscriptionDetails = adminController.getInquiryData(msisdn);

        assertEquals(expectedSubscriptionDetails, actualSubscriptionDetails);
    }
}