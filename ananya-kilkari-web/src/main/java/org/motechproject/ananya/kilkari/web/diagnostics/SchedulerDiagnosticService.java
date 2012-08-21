package org.motechproject.ananya.kilkari.web.diagnostics;

import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SchedulerDiagnosticService {

    private Scheduler motechScheduler;

    @Autowired
    public SchedulerDiagnosticService(SchedulerFactoryBean schedulerFactoryBean) {
        motechScheduler = schedulerFactoryBean.getScheduler();
    }

    public DiagnosticsResult diagnose(List<String> jobs) throws SchedulerException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        List<TriggerKey> triggerKeys = new ArrayList<>(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default")));
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = motechScheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            if (motechScheduler.getTriggersOfJob(jobKey).size() > 0 && isForJob(jobKey.getName(), jobs)) {
                Date previousFireTime = trigger.getPreviousFireTime();
                String previousFireStatus = previousFireTime == null ? "This scheduler has not yet run" : previousFireTime.toString();
                diagnosticLog.add("\n" + jobKey.getName() + "\nPrevious Fire Time : " + previousFireStatus + "\nNext Fire Time : " + trigger.getNextFireTime());
            }
        }
        return new DiagnosticsResult(motechScheduler.isStarted(), diagnosticLog.toString());
    }

    private boolean isForJob(String name, List<String> jobs) {
        for (String job : jobs) {
            if (name.contains(job))
                return true;
        }
        return false;
    }
}