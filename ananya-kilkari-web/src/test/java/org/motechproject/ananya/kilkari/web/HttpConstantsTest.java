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
        assertEquals(HttpConstants.CONTACT_CENTER, HttpConstants.forRequest(request));
    }

    @Test
    public void shouldCheckIfIVRChanneForRequestWhichDoesNotContainTheChannelKey() {
        assertEquals(HttpConstants.CONTACT_CENTER, HttpConstants.forRequest(request));
    }

    @Test
    public void shouldReturnAppropriateResponseTypeForIvr(){
        assertEquals(HttpHeaders.APPLICATION_JAVASCRIPT, HttpConstants.IVR.getResponseContentType(null));
    }

    @Test
    public void shouldReturnAppropriateResponseTypeForContactCenter(){
        assertEquals(HttpHeaders.APPLICATION_XML, HttpConstants.CONTACT_CENTER.getResponseContentType("application/xml"));
        assertEquals(HttpHeaders.APPLICATION_JSON, HttpConstants.CONTACT_CENTER.getResponseContentType("application/json"));
    }
}
