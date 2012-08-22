package org.motechproject.ananya.kilkari.web.interceptors;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.kilkari.web.HttpConstants;
import org.motechproject.web.context.HttpThreadContext;
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
        HttpThreadContext.set(getChannel(request));
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        String contentType = HttpConstants.forRequest(request).getResponseContentType();
        response.setContentType(contentType);
    }

    private String getChannel(HttpServletRequest request) {
        String channel = StringUtils.trim(request.getParameter("channel"));
        if (channel != null) channel = channel.toUpperCase();
        return channel;
    }
}