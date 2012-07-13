package org.motechproject.ananya.kilkari.obd.gateway;

import org.motechproject.ananya.kilkari.obd.profile.OBDTestProfile;
import org.springframework.stereotype.Component;

@Component
@OBDTestProfile
public class StubOnMobileOBDGateway implements OnMobileOBDGateway {

    private OnMobileOBDGateway behavior;

    @Override
    public void sendNewMessages(String content) {
        if(verify()) {
            behavior.sendNewMessages(content);
        }
    }

    @Override
    public void sendRetryMessages(String content) {
        if(verify()) {
            behavior.sendRetryMessages(content);
        }
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println("WARNING: You need to set behavior before calling this method. Use setBehavior method.");
            return false;
        }
        return true;
    }

    public void setBehavior(OnMobileOBDGateway behavior) {
        this.behavior = behavior;
    }
}
