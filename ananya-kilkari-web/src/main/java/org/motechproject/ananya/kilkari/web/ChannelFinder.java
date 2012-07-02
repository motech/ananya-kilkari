package org.motechproject.ananya.kilkari.web;

import org.motechproject.ananya.kilkari.domain.Channel;

import javax.servlet.http.HttpServletRequest;

public class ChannelFinder
{
    public static final String CHANNEL_REQUEST_KEY = "channel";

    private HttpServletRequest request;

    public ChannelFinder(HttpServletRequest request) {
        this.request = request;
    }

    public boolean isIVRChannel() {
        return Channel.isIVR(request.getParameter(CHANNEL_REQUEST_KEY));
    }
}
