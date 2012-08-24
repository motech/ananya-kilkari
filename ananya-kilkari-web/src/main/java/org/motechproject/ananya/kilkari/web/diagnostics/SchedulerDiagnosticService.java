package org.motechproject.ananya.kilkari.web.diagnostics;

import org.joda.time.DateTime;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
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

    public DiagnosticsResult diagnose(List<String> jobs) throws SchedulerException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        List<JobDetails> jobDetailsList = getJobDetailsFor(jobs);
        for (JobDetails jobDetails : jobDetailsList) {
            Date previousFireTime = jobDetails.getPreviousFireTime();
            String previousFireStatus = previousFireTime == null ? "This scheduler has not yet run" : previousFireTime.toString();
            diagnosticLog.add("\n" + jobDetails.getName() + "\nPrevious Fire Time : " + previousFireStatus + "\nNext Fire Time : " + jobDetails.getNextFireTime());
        }
        return new DiagnosticsResult(motechScheduler.isStarted() ? DiagnosticsStatus.PASS : DiagnosticsStatus.FAIL, diagnosticLog.toString());
    }

    public boolean isSchedulerRunning() throws SchedulerException {
        List<JobDetails> jobDetailsList = getJobDetailsFor(ObdSchedulers.getAll());
        for (JobDetails jobDetails : jobDetailsList) {
            if (jobDetails.getPreviousFireTime() != null &&
                    DateUtil.newDateTime(jobDetails.getPreviousFireTime()).isBefore(DateTime.now().minusHours(25)))
                return false;
        }
        return true;
    }

    private List<JobDetails> getJobDetailsFor(List<String> jobs) throws SchedulerException {
        List<TriggerKey> triggerKeys = new ArrayList<>(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default")));
        List<JobDetails> jobDetailsList = new ArrayList<>();
        for (TriggerKey triggerKey : triggerKeys) {
            Trigger trigger = motechScheduler.getTrigger(triggerKey);
            JobKey jobKey = trigger.getJobKey();
            if (motechScheduler.getTriggersOfJob(jobKey).size() > 0 && isForJob(jobKey.getName(), jobs)) {
                Date previousFireTime = trigger.getPreviousFireTime();
                jobDetailsList.add(new JobDetails(previousFireTime, jobKey.getName(), trigger.getNextFireTime()));
            }
        }
        return jobDetailsList;
    }

    private boolean isForJob(String name, List<String> jobs) {
        for (String job : jobs) {
            if (name.contains(job))
                return true;
        }
        return false;
    }
}