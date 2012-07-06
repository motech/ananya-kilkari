package org.motechproject.ananya.kilkari.web;

import org.motechproject.ananya.kilkari.domain.Channel;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;


public enum HttpConstants {

    IVR("application/javascript;charset=UTF-8", HttpStatus.OK.value(), HttpStatus.OK.value(), "var response = "),

    CALL_CENTER("application/json;charset=UTF-8", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.BAD_REQUEST.value(), "");

    private static final String CHANNEL_REQUEST_KEY = "channel";

    private int httpStatusError;
    private String responseContentType;
    private int httpStatusBadRequest;
    private String responsePrefix;

    HttpConstants(String responseContentType, int httpStatusError, int httpStatusBadRequest, String responsePrefix) {
        this.responseContentType = responseContentType;
        this.httpStatusError = httpStatusError;
        this.httpStatusBadRequest = httpStatusBadRequest;
        this.responsePrefix = responsePrefix;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public int getHttpStatusError() {
        return httpStatusError;
    }

    public int getHttpStatusBadRequest() {
        return httpStatusBadRequest;
    }

    public static HttpConstants forRequest(HttpServletRequest request) {
        return Channel.isIVR(request.getParameter(CHANNEL_REQUEST_KEY)) ? HttpConstants.IVR : HttpConstants.CALL_CENTER;
    }

    public String getResponsePrefix() {
        return responsePrefix;
    }
}
