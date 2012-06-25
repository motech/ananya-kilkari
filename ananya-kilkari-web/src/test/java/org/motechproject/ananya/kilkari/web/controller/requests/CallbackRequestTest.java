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
       callbackRequest.setSrvKey(SubscriptionPack.TWELVE_MONTHS);
       callbackRequest.setRefId("myrefId");
       callbackRequest.setReason("myreason");
       callbackRequest.setOperator("myoperator");
       callbackRequest.setGraceCount("10");
       callbackRequest.setAction(CallBackAction.ACT);
       callbackRequest.setStatus(CallBackStatus.SUCCESS);

       assertEquals("msisdn: mymsisdn; pack: TWELVE_MONTHS; refid: myrefId; reason: myreason; operator: myoperator; graceCount: 10; action: ACT; status: SUCCESS", callbackRequest.toString());

   }
}
