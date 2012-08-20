package org.motechproject.ananya.kilkari.web.diagnostics;

import org.joda.time.DateTime;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.util.DateUtil;
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

    public DiagnosticsResult getSchedule(String subscriptionId) throws SchedulerException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        List<TriggerKey> triggerKeys = new ArrayList<>(
                motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default")));
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = motechScheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            if (motechScheduler.getTriggersOfJob(jobKey).size() > 0 && isForSubscription(jobKey.getName(), subscriptionId)) {
                Date previousFireTime = trigger.getPreviousFireTime();
                Date nextFireTime = trigger.getNextFireTime();
                String previousFireStatus = null;
                String nextFireTimeStatus;
                if (previousFireTime == null)
                    previousFireStatus = "This scheduler has not yet run";
                if (isLastTrigger(trigger))
                    nextFireTimeStatus = "This is the end of schedule";
                else {
                    previousFireStatus = previousFireTime.toString();
                    nextFireTimeStatus = nextFireTime.toString();
                }
                diagnosticLog.add("\n" + jobKey.getName() + "\nPrevious Fire Time : " + previousFireStatus + "\nNext Fire Time : " + nextFireTimeStatus);
            }
        }
        return new DiagnosticsResult(motechScheduler.isStarted(), diagnosticLog.toString());
    }

    private boolean isForSubscription(String name, String subscriptionId) {
        return name.contains(subscriptionId);
    }

    private boolean isLastTrigger(Trigger trigger) {
        DateTime finalFireTime = DateUtil.newDateTime(trigger.getFinalFireTime());
        return !DateTime.now().isBefore(finalFireTime);
    }

}