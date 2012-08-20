package org.motechproject.ananya.kilkari.web.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SchedulerDiagnostics {

    private Scheduler motechScheduler;
    private final List<String> staticSchedulers = new ArrayList<String>() {
        {
            add("obd.send.new.messages-null");
            add("obd.send.retry.messages-null");
        }
    };

    @Autowired
    public SchedulerDiagnostics(SchedulerFactoryBean schedulerFactoryBean) {
        motechScheduler = schedulerFactoryBean.getScheduler();
    }

    @Diagnostic(name = "SCHEDULER DIAGNOSTICS")
    public DiagnosticsResult performDiagnostic() throws SchedulerException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        List<TriggerKey> triggerKeys = new ArrayList<>(
                motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default")));
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = motechScheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            if (motechScheduler.getTriggersOfJob(jobKey).size() > 0 && isStaticSchedule(jobKey.getName())) {
                Date previousFireTime = trigger.getPreviousFireTime();
                Date nextFireTime = trigger.getNextFireTime();
                String previousFireStatus = null;
                String nextFireTimeStatus;
                if (previousFireTime == null)
                    previousFireStatus = "This scheduler has not yet run";
                else previousFireStatus = previousFireTime.toString();
                if (!isLastTrigger(nextFireTime))
                    nextFireTimeStatus = nextFireTime.toString();
                else nextFireTimeStatus = "This is the end of schedule";
                diagnosticLog.add("\n" + jobKey.getName() + "\nPrevious Fire Time : " + previousFireStatus + "\nNext Fire Time : " + nextFireTimeStatus);
            }
        }
        return new DiagnosticsResult(motechScheduler.isStarted(), diagnosticLog.toString());
    }

    private boolean isLastTrigger(Date nextFireTime) {
        return nextFireTime == null;
    }

    private boolean isStaticSchedule(String scheduleName) {
        return staticSchedulers.contains(scheduleName);
    }
}