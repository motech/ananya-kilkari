package org.motechproject.ananya.kilkari.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CallbackRequestTest {

   @Test
   public void shouldReturnStringWithAllProperties() {
       CallbackRequest callbackRequest = new CallbackRequest();
       callbackRequest.setMsisdn("mymsisdn");
       callbackRequest.setReason("myreason");
       callbackRequest.setOperator("myoperator");
       callbackRequest.setGraceCount("10");
       callbackRequest.setAction("ACT");
       callbackRequest.setStatus("SUCCESS");

       assertEquals("msisdn: mymsisdn; reason: myreason; operator: myoperator; graceCount: 10; action: ACT; status: SUCCESS", callbackRequest.toString());

   }
}
