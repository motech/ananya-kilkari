package org.motechproject.ananya.kilkari.gateway;

import org.motechproject.ananya.kilkari.profile.OBDTestProfile;
import org.springframework.stereotype.Component;

@Component
@OBDTestProfile
public class StubOnMobileOBDGateway implements OnMobileOBDGateway {

    private OnMobileOBDGateway behavior;

    @Override
    public void send(String content) {
        if(verify()) {
            behavior.send(content);
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
