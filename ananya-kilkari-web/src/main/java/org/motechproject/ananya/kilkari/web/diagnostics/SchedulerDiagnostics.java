package org.motechproject.ananya.kilkari.web.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SchedulerDiagnostics {
    private static final List<String> staticSchedulers = new ArrayList<String>() {
        {
            add("obd.send.new.messages");
            add("obd.send.retry.messages");
        }
    };
    private final SchedulerDiagnosticService schedulerDiagnosticService;

    @Autowired
    public SchedulerDiagnostics(SchedulerDiagnosticService schedulerDiagnosticService) {
        this.schedulerDiagnosticService = schedulerDiagnosticService;
    }

    @Diagnostic(name = "SCHEDULER DIAGNOSTICS")
    public DiagnosticsResult performDiagnosis() throws SchedulerException {
        return schedulerDiagnosticService.diagnose(staticSchedulers);
    }
}