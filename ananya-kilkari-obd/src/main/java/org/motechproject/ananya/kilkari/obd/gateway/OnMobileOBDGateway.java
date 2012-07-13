package org.motechproject.ananya.kilkari.obd.gateway;

public interface OnMobileOBDGateway {
    void sendNewMessages(String content);

    void sendRetryMessages(String content);
}
