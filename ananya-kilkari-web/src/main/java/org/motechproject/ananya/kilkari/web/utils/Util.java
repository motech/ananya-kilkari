package org.motechproject.ananya.kilkari.web.utils;

import org.motechproject.ananya.kilkari.domain.Channel;
import org.motechproject.ananya.kilkari.web.domain.KilkariConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Util {

    public static boolean isIvrChannelRequest(HttpServletRequest request) {
        return request.getParameterMap().containsKey(KilkariConstants.CHANNEL_REQUEST_KEY) &&
                Channel.isIVR(request.getParameter(KilkariConstants.CHANNEL_REQUEST_KEY));
    }

    public static void setErrorResponseStatusBasedOnRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(isIvrChannelRequest(request) ? KilkariConstants.IVR_ERROR_CODE : KilkariConstants.ERROR_CODE);
    }

    public static void setContentTypeBaseOnRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(isIvrChannelRequest(request)
                ? KilkariConstants.HTTP_RESPONSE_CONTENT_TYPE_IVR : KilkariConstants.HTTP_RESPONSE_CONTENT_TYPE);
    }
}
