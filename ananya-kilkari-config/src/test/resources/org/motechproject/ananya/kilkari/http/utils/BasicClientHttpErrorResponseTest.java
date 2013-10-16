package org.motechproject.ananya.kilkari.http.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BasicClientHttpErrorResponseTest {

    @Mock
    ClientHttpResponse baseResponse;

    @Test
    public void should() throws IOException {
        String responseStatus = "Response Status";
        String statusText = "Status text";
        when(baseResponse.getStatusText()).thenReturn(responseStatus);

        BasicClientHttpErrorResponse basicClientHttpErrorResponse = new BasicClientHttpErrorResponse(statusText, baseResponse);

        assertEquals(statusText + responseStatus, basicClientHttpErrorResponse.getStatusText());
    }
}