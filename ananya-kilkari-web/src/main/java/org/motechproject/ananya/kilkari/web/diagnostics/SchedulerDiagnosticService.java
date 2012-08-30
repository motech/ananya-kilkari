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

        DiagnosticsStatus status = DiagnosticsStatus.PASS;

        for (JobDetails jobDetails : jobDetailsList) {
            diagnosticLog.add("Job : " + jobDetails.getName());
            Date previousFireTime = jobDetails.getPreviousFireTime();
            diagnosticLog.add("Previous Fire Time : " + (previousFireTime == null ? "This job has not yet run" : previousFireTime.toString()));
            diagnosticLog.add("Next Fire Time : " + jobDetails.getNextFireTime());

            if(!hasJobRunInPreviousDay(jobDetails, diagnosticLog)) {
                status = DiagnosticsStatus.FAIL;
            }
            diagnosticLog.add("");
        }

        if(!checkMotechScheduler(diagnosticLog)) {
            status = DiagnosticsStatus.FAIL;
        }

        return new DiagnosticsResult(status, diagnosticLog.toString());
    }

    public boolean AreSchedulerJobsRunning() throws SchedulerException {
        DiagnosticsResult diagnosticsResult = diagnose(ObdSchedulers.getAll());
        return diagnosticsResult.getStatus() == DiagnosticsStatus.PASS;
    }

    private boolean checkMotechScheduler(DiagnosticLog diagnosticLog) throws SchedulerException {
        boolean schedulerRunning = motechScheduler.isStarted();
        diagnosticLog.add(String.format("Motech Scheduler: %s", schedulerRunning ? "Running": "Not Running"));
        return schedulerRunning;
    }

    private boolean hasJobRunInPreviousDay(JobDetails jobDetails, DiagnosticLog diagnosticLog) {
        String log = "Has job run in previous day : %s";
        Date previousFireTime = jobDetails.getPreviousFireTime();
        if(previousFireTime == null) {
            diagnosticLog.add(String.format(log, "N/A"));
            return true;
        }

        boolean hasRunInPreviousDay = !DateUtil.newDateTime(previousFireTime).isBefore(DateTime.now().minusHours(25));
        diagnosticLog.add(String.format(log, hasRunInPreviousDay ? "Yes" : "No"));
        return hasRunInPreviousDay;
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