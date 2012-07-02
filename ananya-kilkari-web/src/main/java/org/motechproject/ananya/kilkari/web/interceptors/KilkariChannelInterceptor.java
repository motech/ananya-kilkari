package org.motechproject.ananya.kilkari.web.interceptors;

import org.motechproject.ananya.kilkari.web.ChannelFinder;
import org.motechproject.ananya.kilkari.web.HttpConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KilkariChannelInterceptor extends HandlerInterceptorAdapter {

    public static final String IVR_RESPONSE_PREFIX = "var response = ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws java.lang.Exception {
        if (new ChannelFinder(request).isIVRChannel()) {
            response.getOutputStream().print(IVR_RESPONSE_PREFIX);
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (new ChannelFinder(request).isIVRChannel()) {
            response.setContentType(HttpConstants.JAVASCRIPT_CONTENT_TYPE);
        }
    }
}