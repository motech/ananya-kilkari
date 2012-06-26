package org.motechproject.ananya.kilkari.web.controller.requests;

import org.junit.Test;
import org.motechproject.ananya.kilkari.domain.SubscriptionPack;
import org.motechproject.ananya.kilkari.web.domain.CallBackAction;
import org.motechproject.ananya.kilkari.web.domain.CallBackStatus;

import static org.junit.Assert.assertEquals;

public class CallbackRequestTest {

   @Test
   public void shouldReturnStringWithAllProperties() {
       CallbackRequest callbackRequest = new CallbackRequest();
       callbackRequest.setMsisdn("mymsisdn");
       callbackRequest.setReason("myreason");
       callbackRequest.setOperator("myoperator");
       callbackRequest.setRenewalAttempt("10");
       callbackRequest.setAction(CallBackAction.ACT);
       callbackRequest.setStatus(CallBackStatus.SUCCESS);

       assertEquals("msisdn: mymsisdn; reason: myreason; operator: myoperator; renewalAttempt: 10; action: ACT; status: SUCCESS", callbackRequest.toString());

   }
}
