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
        if (shouldProcessRequest(request)) {
            String responsePrefix = HttpConstants.forRequest(request).getResponsePrefix();
            response.getOutputStream().print(responsePrefix);
            HttpThreadContext.set(getChannel(request));
        }
        return true;
    }

    private boolean shouldProcessRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        requestURI = StringUtils.remove(requestURI, request.getContextPath());
        return !StringUtils.startsWith(requestURI, "/admin");
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (shouldProcessRequest(request)) {
            String contentType = HttpConstants.forRequest(request).getResponseContentType(request.getHeader("accept"));
            response.setContentType(contentType);
        }
    }

    private String getChannel(HttpServletRequest request) {
        String channel = StringUtils.trim(request.getParameter("channel"));
        if (channel != null) channel = channel.toUpperCase();
        return channel;
    }
}