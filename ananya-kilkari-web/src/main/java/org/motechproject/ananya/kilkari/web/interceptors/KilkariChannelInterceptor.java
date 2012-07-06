package org.motechproject.ananya.kilkari.web.interceptors;

import org.motechproject.ananya.kilkari.web.HttpConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KilkariChannelInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws java.lang.Exception {

        String responsePrefix = HttpConstants.forRequest(request).getResponsePrefix();
        response.getOutputStream().print(responsePrefix);
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        String contentType = HttpConstants.forRequest(request).getResponseContentType();
        response.setContentType(contentType);
    }
}