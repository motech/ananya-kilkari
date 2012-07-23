package org.motechproject.ananya.kilkari.obd.gateway;

import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.profile.TestProfile;
import org.springframework.stereotype.Component;

@Component
@TestProfile
public class StubOnMobileOBDGateway implements OnMobileOBDGateway {

    private OnMobileOBDGateway behavior;
    private boolean invalidFailureRecordCalled;

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

    @Override
    public void sendInvalidFailureRecord(InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord) {
        if(verify()) {
            behavior.sendInvalidFailureRecord(invalidCallDeliveryFailureRecord);
            invalidFailureRecordCalled = true;
        }
    }

    private boolean verify() {
        if (behavior == null) {
            System.err.println(String.format("WARNING: %s: You need to set behavior before calling this method. Use setBehavior method.", StubOnMobileOBDGateway.class.getCanonicalName()));
            return false;
        }
        return true;
    }

    public void setBehavior(OnMobileOBDGateway behavior) {
        this.behavior = behavior;
    }

    public void setInvalidFailureRecordCalled(boolean invalidFailureRecordCalled) {
        this.invalidFailureRecordCalled = invalidFailureRecordCalled;
    }

    public boolean isInvalidFailureRecordCalled() {
        return invalidFailureRecordCalled;
    }
}
