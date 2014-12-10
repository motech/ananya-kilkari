package org.motechproject.ananya.kilkari.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.kilkari.request.CallbackRequest;
import org.motechproject.ananya.kilkari.request.CallbackRequestWrapper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CallbackRequestWrapperTest {
	   
	
    @Test
    public void shouldReturnGraceCountIfValid() {
        String graceCountString = "3";
        CallbackRequest callbackRequest = mock(CallbackRequest.class);
        when(callbackRequest.getGraceCount()).thenReturn(graceCountString);
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "subId", DateTime.now(), true);

        Integer graceCount = callbackRequestWrapper.getGraceCount();
        assertEquals(Integer.valueOf(graceCountString), graceCount);
    }

    @Test
    public void shouldReturnNullGraceCountIfInvalid() {
        String graceCountString = "a";
        CallbackRequest callbackRequest = mock(CallbackRequest.class);
        when(callbackRequest.getGraceCount()).thenReturn(graceCountString);
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "subId", DateTime.now(), true);

        Integer graceCount = callbackRequestWrapper.getGraceCount();
        assertNull(graceCount);
    }

    @Test
    public void shouldReturnNullGraceCountIfEmpty() {
        String graceCountString = "";
        CallbackRequest callbackRequest = mock(CallbackRequest.class);
        when(callbackRequest.getGraceCount()).thenReturn(graceCountString);
        CallbackRequestWrapper callbackRequestWrapper = new CallbackRequestWrapper(callbackRequest, "subId", DateTime.now(), true);

        Integer graceCount = callbackRequestWrapper.getGraceCount();
        assertNull(graceCount);
    }
    
    
}
