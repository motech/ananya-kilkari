package org.motechproject.ananya.kilkari.web;

import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;


public enum HttpConstants {

    IVR(HttpStatus.OK.value(), HttpStatus.OK.value(), "var response = ") {
        @Override
        public String getResponseContentType(String acceptHeader) {
            return HttpHeaders.APPLICATION_JAVASCRIPT;
        }
    },
    CONTACT_CENTER(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.BAD_REQUEST.value(), "") {
        @Override
        public String getResponseContentType(String acceptHeader) {
            return MediaType.APPLICATION_XML.toString().equals(acceptHeader)
                    ? HttpHeaders.APPLICATION_XML
                    : HttpHeaders.APPLICATION_JSON;
        }
    };

    private static final String CHANNEL_REQUEST_KEY = "channel";

    private int httpStatusError;
    private int httpStatusBadRequest;
    private String responsePrefix;

    HttpConstants(int httpStatusError, int httpStatusBadRequest, String responsePrefix) {
        this.httpStatusError = httpStatusError;
        this.httpStatusBadRequest = httpStatusBadRequest;
        this.responsePrefix = responsePrefix;
    }

    public abstract String getResponseContentType(String acceptHeader);

    public int getHttpStatusError() {
        return httpStatusError;
    }

    public int getHttpStatusBadRequest() {
        return httpStatusBadRequest;
    }

    public static HttpConstants forRequest(HttpServletRequest request) {
        return Channel.isIVR(request.getParameter(CHANNEL_REQUEST_KEY)) ? HttpConstants.IVR : HttpConstants.CONTACT_CENTER;
    }

    public String getResponsePrefix() {
        return responsePrefix;
    }
}
