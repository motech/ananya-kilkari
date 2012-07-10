package org.motechproject.ananya.kilkari.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestLoggingFilter extends AbstractRequestLoggingFilter {
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.info(message);
    }
}
