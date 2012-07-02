package org.motechproject.ananya.kilkari.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ChannelFinderTest {

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCheckIfIVRChannelGivenValidRequestParam() {
        when(request.getParameter(ChannelFinder.CHANNEL_REQUEST_KEY)).thenReturn("ivr");

        boolean isIVR = new ChannelFinder(request).isIVRChannel();

        assertTrue(isIVR);
    }

    @Test
    public void shouldCheckIfIVRChannelGivenInValidRequestParam() {
        when(request.getParameter(ChannelFinder.CHANNEL_REQUEST_KEY)).thenReturn("invalid");

        boolean isIVR = new ChannelFinder(request).isIVRChannel();

        assertFalse(isIVR);
    }

    @Test
    public void shouldCheckIfIVRChanneForRequestWhichDoesNotContainTheChannelKey() {
        when(request.getParameter(ChannelFinder.CHANNEL_REQUEST_KEY)).thenReturn(null);

        boolean isIVR = new ChannelFinder(request).isIVRChannel();

        assertFalse(isIVR);
    }
}
