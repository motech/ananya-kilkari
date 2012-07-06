package org.motechproject.ananya.kilkari.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class HttpConstantsTest{

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCheckIfIVRChannelGivenValidRequestParam() {
        when(request.getParameter("channel")).thenReturn("ivr");
        assertEquals(HttpConstants.IVR, HttpConstants.forRequest(request));
    }

    @Test
    public void shouldCheckIfIVRChannelGivenValidRequestParamButCaseInsensitive() {
        when(request.getParameter("channel")).thenReturn("  iVr ");
        assertEquals(HttpConstants.IVR, HttpConstants.forRequest(request));
    }

    @Test
    public void shouldCheckIfIVRChannelGivenInValidRequestParam() {
        when(request.getParameter("channel")).thenReturn("invalid");
        assertEquals(HttpConstants.CALL_CENTER, HttpConstants.forRequest(request));
    }

    @Test
    public void shouldCheckIfIVRChanneForRequestWhichDoesNotContainTheChannelKey() {
        assertEquals(HttpConstants.CALL_CENTER, HttpConstants.forRequest(request));
    }
}
