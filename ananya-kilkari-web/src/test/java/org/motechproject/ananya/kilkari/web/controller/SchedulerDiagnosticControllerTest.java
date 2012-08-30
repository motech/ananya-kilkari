package org.motechproject.ananya.kilkari.web.controller;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.diagnostics.SchedulerDiagnosticService;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SchedulerDiagnosticControllerTest extends SpringIntegrationTest {
    private SchedulerDiagnosticController schedulerDiagnosticController;

    @Mock
    private SchedulerDiagnosticService schedulerDiagnosticService;
    @Autowired
    private VelocityEngine velocityEngine;

    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private ServletOutputStream servletOutputStream;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetSchedulesForAGivenSubscriptionId() throws Exception {
        String expectedMessage = "Some message";
        schedulerDiagnosticController = new SchedulerDiagnosticController(schedulerDiagnosticService, velocityEngine);
        when(schedulerDiagnosticService.diagnose(anyList())).thenReturn(new DiagnosticsResult(DiagnosticsStatus.PASS, expectedMessage));
        when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);

        schedulerDiagnosticController.getSchedule("subscriptionId", httpServletResponse);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(servletOutputStream).print(captor.capture());
        String actualMessage = captor.getValue();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnSuccessWhenObdSchedulesRunAsExpected() throws Exception {
        schedulerDiagnosticController = new SchedulerDiagnosticController(schedulerDiagnosticService, velocityEngine);
        when(schedulerDiagnosticService.AreSchedulerJobsRunning()).thenReturn(true);

        String status = schedulerDiagnosticController.obdSchedulerStatus();

        assertEquals("SUCCESS", status);
    }

    @Test
    public void shouldReturnFailureWhenObdSchedulesDoNotRunAsExpected() throws Exception {
        schedulerDiagnosticController = new SchedulerDiagnosticController(schedulerDiagnosticService, velocityEngine);
        when(schedulerDiagnosticService.AreSchedulerJobsRunning()).thenReturn(false);

        String status = schedulerDiagnosticController.obdSchedulerStatus();

        assertEquals("FAILURE", status);
    }
}