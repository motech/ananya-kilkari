package org.motechproject.ananya.kilkari.web.controller.requests;

import org.junit.Test;
import org.motechproject.ananya.kilkari.web.domain.CallbackAction;
import org.motechproject.ananya.kilkari.web.domain.CallbackStatus;

import static org.junit.Assert.assertEquals;

public class CallbackRequestTest {

   @Test
   public void shouldReturnStringWithAllProperties() {
       CallbackRequest callbackRequest = new CallbackRequest();
       callbackRequest.setMsisdn("mymsisdn");
       callbackRequest.setReason("myreason");
       callbackRequest.setOperator("myoperator");
       callbackRequest.setGraceCount("10");
       callbackRequest.setAction(CallbackAction.ACT.name());
       callbackRequest.setStatus(CallbackStatus.SUCCESS.name());

       assertEquals("msisdn: mymsisdn; reason: myreason; operator: myoperator; graceCount: 10; action: ACT; status: SUCCESS", callbackRequest.toString());

   }
}
