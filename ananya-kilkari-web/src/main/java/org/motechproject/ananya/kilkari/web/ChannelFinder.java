package org.motechproject.ananya.kilkari.web;

import org.motechproject.ananya.kilkari.subscription.domain.Channel;

import javax.servlet.http.HttpServletRequest;

public class ChannelFinder
{
    public static final String CHANNEL_REQUEST_KEY = "channel";

    private HttpServletRequest request;

    public ChannelFinder(HttpServletRequest request) {
        this.request = request;
    }

    public Channel getChannel() {
        return Channel.isIVR(request.getParameter(CHANNEL_REQUEST_KEY)) ? Channel.IVR : Channel.CALL_CENTER;
    }
}
