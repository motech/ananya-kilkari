package org.motechproject.ananya.kilkari.web.diagnostics;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;
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
	
	Logger logger = Logger.getLogger(SchedulerDiagnosticService.class);

    private Scheduler motechScheduler;
    private List<String> obdSchedules;

    @Autowired
    public SchedulerDiagnosticService(SchedulerFactoryBean schedulerFactoryBean) {
        motechScheduler = schedulerFactoryBean.getScheduler();
        obdSchedules = ObdSchedules.getAll();
    }

    public SchedulerDiagnosticService(SchedulerFactoryBean schedulerFactoryBean, List<String> obdSchedules) {
        this.motechScheduler = schedulerFactoryBean.getScheduler();
        this.obdSchedules = obdSchedules;
    }

    public DiagnosticsResult diagnoseAllOBDSchedules() throws SchedulerException {
        List<JobDetails> jobDetailsList = getJobDetailsFor(obdSchedules);
        logger.info("/scheduler/obd/ jobDetailsList="+jobDetailsList!=null?jobDetailsList.toString():null);
        DiagnosticsResult diagnosticsResult = checkIfAllJobsAreScheduled(obdSchedules, jobDetailsList);
        logger.info("/scheduler/obd/ diagnosticsResult="+diagnosticsResult!=null?diagnosticsResult.toString():null);
        return diagnosticsResult.getStatus().equals(DiagnosticsStatus.FAIL) ?
                diagnosticsResult :
                checkIfJobsAreScheduledAtTheRightTime(jobDetailsList);
    }

    private DiagnosticsResult checkIfAllJobsAreScheduled(List<String> jobs, List<JobDetails> jobDetailsList) {
        DiagnosticLog diagnosticLog = new DiagnosticLog();

        if (jobDetailsList.size() != jobs.size()) {
            ArrayList<String> unScheduledJobs = getUnscheduledJobs(jobs, jobDetailsList);
            for (String unScheduledJob : unScheduledJobs)
                diagnosticLog.add("Unscheduled Job: " + unScheduledJob);
            return new DiagnosticsResult(DiagnosticsStatus.FAIL, diagnosticLog.toString());
        }
        return new DiagnosticsResult(DiagnosticsStatus.PASS, diagnosticLog.toString());
    }

    public DiagnosticsResult diagnose(List<String> jobs) throws SchedulerException {
        List<JobDetails> jobDetailsList = getJobDetailsFor(jobs);
        return checkIfJobsAreScheduledAtTheRightTime(jobDetailsList);
    }

    private DiagnosticsResult checkIfJobsAreScheduledAtTheRightTime(List<JobDetails> jobDetailsList) throws SchedulerException {
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        DiagnosticsStatus status = DiagnosticsStatus.PASS;
        for (JobDetails jobDetails : jobDetailsList) {
            diagnosticLog.add("Job : " + jobDetails.getName());
            Date previousFireTime = jobDetails.getPreviousFireTime();
            diagnosticLog.add("Previous Fire Time : " + (previousFireTime == null ? "This job has not yet run" : previousFireTime.toString()));
            diagnosticLog.add("Next Fire Time : " + jobDetails.getNextFireTime());

            if (!hasJobRunInPreviousDay(jobDetails, diagnosticLog)) {
                status = DiagnosticsStatus.FAIL;
            }
            diagnosticLog.add("");
        }

        if (!checkMotechScheduler(diagnosticLog)) {
            status = DiagnosticsStatus.FAIL;
        }

        return new DiagnosticsResult(status, diagnosticLog.toString());
    }

    private ArrayList<String> getUnscheduledJobs(List<String> jobs, List<JobDetails> jobDetailsList) {
        ArrayList<String> jobDetailNamesList = (ArrayList<String>) CollectionUtils.collect(jobDetailsList, new Transformer() {
            @Override
            public Object transform(Object input) {
                JobDetails jobDetails = (JobDetails) input;
                return jobDetails.getName();
            }
        });
        return (ArrayList<String>) CollectionUtils.disjunction(jobs, jobDetailNamesList);
    }

    private boolean checkMotechScheduler(DiagnosticLog diagnosticLog) throws SchedulerException {
        boolean schedulerRunning = motechScheduler.isStarted();
        diagnosticLog.add(String.format("Motech Scheduler: %s", schedulerRunning ? "Running" : "Not Running"));
        return schedulerRunning;
    }

    private boolean hasJobRunInPreviousDay(JobDetails jobDetails, DiagnosticLog diagnosticLog) {
        String log = "Has job run in previous day : %s";
        Date previousFireTime = jobDetails.getPreviousFireTime();
        if (previousFireTime == null) {
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