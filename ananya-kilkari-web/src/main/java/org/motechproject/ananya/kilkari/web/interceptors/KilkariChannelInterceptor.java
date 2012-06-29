package org.motechproject.ananya.kilkari.web.interceptors;

import org.motechproject.ananya.kilkari.web.domain.KilkariConstants;
import org.motechproject.ananya.kilkari.web.utils.Util;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KilkariChannelInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws java.lang.Exception {
        if (Util.isIvrChannelRequest(request)) response.getOutputStream().print(KilkariConstants.IVR_RESPONSE_FORMAT);
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (Util.isIvrChannelRequest(request)) response.setContentType("application/javascript;charset=UTF-8");
    }

}