package org.motechproject.ananya.kilkari.web.diagnostics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.quartz.SchedulerException;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerDiagnosticsTest {
    @Mock
    private SchedulerDiagnosticService schedulerDiagnosticService;

    private SchedulerDiagnostics schedulerDiagnostics;
    @Captor
    private ArgumentCaptor<List<String>> jobsArgumentCaptor;

    @Before
    public void setUp() {
        schedulerDiagnostics = new SchedulerDiagnostics(schedulerDiagnosticService);
    }

    @Test
    public void shouldGetAllOBDSchedulers() throws SchedulerException {
        DiagnosticsResult expectedDiagnosticsResult = new DiagnosticsResult(DiagnosticsStatus.PASS, "some message");
        when(schedulerDiagnosticService.diagnoseAllOBDSchedules()).thenReturn(expectedDiagnosticsResult);

        DiagnosticsResult actualDiagnosticsResult = schedulerDiagnostics.performDiagnosis();

        verify(schedulerDiagnosticService).diagnoseAllOBDSchedules();
        assertEquals(expectedDiagnosticsResult, actualDiagnosticsResult);
    }
}