package org.motechproject.ananya.kilkari.web.views;

import org.motechproject.ananya.kilkari.web.ChannelFinder;
import org.motechproject.ananya.kilkari.web.HttpConstants;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class KilkariView extends AbstractView {


    protected void setHttpStatusCodeBasedOnChannel(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(new ChannelFinder(request).isIVRChannel() ? HttpConstants.IVR_ERROR_CODE : HttpConstants.ERROR_CODE);
    }

    protected void setContentTypeToJavaScriptForIVRChannel(HttpServletRequest request, HttpServletResponse response) {
        if (new ChannelFinder(request).isIVRChannel()) {
            response.setContentType(HttpConstants.JAVASCRIPT_CONTENT_TYPE);
        } else {
            response.setContentType(HttpConstants.JSON_CONTENT_TYPE);
        }
    }
}
