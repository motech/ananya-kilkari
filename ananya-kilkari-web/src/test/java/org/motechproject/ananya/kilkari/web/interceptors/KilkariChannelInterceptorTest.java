package org.motechproject.ananya.kilkari.web.interceptors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.kilkari.obd.domain.Channel;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.web.context.HttpThreadContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KilkariChannelInterceptorTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletOutputStream outputStream;

    @Test
    public void shouldSetJavascriptResponseForIVRCalls() throws Exception {
        KilkariChannelInterceptor kilkariChannelInterceptor = new KilkariChannelInterceptor();
        when(request.getParameter("channel")).thenReturn(Channel.IVR.name());
        when(response.getOutputStream()).thenReturn(outputStream);

        kilkariChannelInterceptor.preHandle(request, response, null);

        verify(outputStream).print("var response = ");
    }

    @Test
    public void shouldNotSetJavascriptResponseForCallCenterCalls() throws Exception {
        KilkariChannelInterceptor kilkariChannelInterceptor = new KilkariChannelInterceptor();
        when(request.getParameter("channel")).thenReturn(Channel.CONTACT_CENTER.name());
        when(response.getOutputStream()).thenReturn(outputStream);

        kilkariChannelInterceptor.preHandle(request, response, null);

        verify(outputStream).print("");
    }

    @Test
    public void shouldSetTheChannelInHttpThreadContext() throws Exception {
        KilkariChannelInterceptor kilkariChannelInterceptor = new KilkariChannelInterceptor();
        when(request.getParameter("channel")).thenReturn(Channel.CONTACT_CENTER.name());
        when(response.getOutputStream()).thenReturn(outputStream);

        kilkariChannelInterceptor.preHandle(request, response, null);

        assertEquals(Channel.CONTACT_CENTER.name(), HttpThreadContext.get());
    }

    @Test
    public void shouldSetJSONResponseContentForCallCenter() throws Exception {
        KilkariChannelInterceptor kilkariChannelInterceptor = new KilkariChannelInterceptor();
        when(request.getParameter("channel")).thenReturn(Channel.CONTACT_CENTER.name());

        kilkariChannelInterceptor.postHandle(request, response, null, null);

        verify(response).setContentType(HttpHeaders.APPLICATION_JSON);
    }

    @Test
    public void shouldSetJavaScriptResponseContentForIVR() throws Exception {
        KilkariChannelInterceptor kilkariChannelInterceptor = new KilkariChannelInterceptor();
        when(request.getParameter("channel")).thenReturn(Channel.IVR.name());

        kilkariChannelInterceptor.postHandle(request, response, null, null);

        verify(response).setContentType(HttpHeaders.APPLICATION_JAVASCRIPT);
    }
}
