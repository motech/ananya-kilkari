package org.motechproject.ananya.kilkari.web.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerDiagnostics {
    private final SchedulerDiagnosticService schedulerDiagnosticService;

    @Autowired
    public SchedulerDiagnostics(SchedulerDiagnosticService schedulerDiagnosticService) {
        this.schedulerDiagnosticService = schedulerDiagnosticService;
    }

    @Diagnostic(name = "SCHEDULER DIAGNOSTICS")
    public DiagnosticsResult performDiagnosis() throws SchedulerException {
        return schedulerDiagnosticService.diagnose(ObdSchedulers.getAll());
    }
}