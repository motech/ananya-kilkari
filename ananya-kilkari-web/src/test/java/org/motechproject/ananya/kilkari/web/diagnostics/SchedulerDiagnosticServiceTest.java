package org.motechproject.ananya.kilkari.web.diagnostics;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.motechproject.diagnostics.response.DiagnosticsStatus;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerDiagnosticServiceTest {
    @Mock
    private Scheduler motechScheduler;
    @Mock
    private SchedulerFactoryBean schedulerFactoryBean;

    private final String schedulerDiagnosticsFormat = "Job : %s\nPrevious Fire Time : %s\nNext Fire Time : %s\nHas job run in previous day : %s";
    private SchedulerDiagnosticService schedulerDiagnosticService;

    @Before
    public void setUp() {
        when(schedulerFactoryBean.getScheduler()).thenReturn(motechScheduler);
        schedulerDiagnosticService = new SchedulerDiagnosticService(schedulerFactoryBean);
    }

    @Test
    public void shouldPrintJobHasNotRunWhenJobHasNotPreviouslyRun() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = "Job Name1";
        final String jobName2 = "Job Name2";
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));
        when(motechScheduler.isStarted()).thenReturn(true);

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnose(new ArrayList<String>() {{
            add(jobName2);
            add(jobName1);
        }});

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, jobName1, "This job has not yet run", mockTrigger.getNextFireTime(), "N/A")));
        assertEquals(DiagnosticsStatus.PASS, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldPrintMotechSchedulerIsNotRunning() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = "Job Name1";
        final String jobName2 = "Job Name2";
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));
        when(motechScheduler.isStarted()).thenReturn(false);

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnose(new ArrayList<String>() {{
            add(jobName2);
            add(jobName1);
        }});

        assertTrue(diagnosticsResult.getMessage().contains("Motech Scheduler: Not Running"));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldFailIfAnyOfTheJobsHasNotRunInLastDay() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey1 = new TriggerKey("Name1", "default");
        TriggerKey triggerKey2 = new TriggerKey("Name2", "default");
        triggerKeys.add(triggerKey1);
        triggerKeys.add(triggerKey2);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains("default"))).thenReturn(triggerKeys);

        Trigger mockTrigger1 = mock(Trigger.class);
        Trigger mockTrigger2 = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey1)).thenReturn(mockTrigger1);
        when(motechScheduler.getTrigger(triggerKey2)).thenReturn(mockTrigger2);
        final String jobName1 = "Job Name1";
        final String jobName2 = "Job Name2";
        JobKey jobKey1 = new JobKey(jobName1);
        JobKey jobKey2 = new JobKey(jobName2);
        when(mockTrigger1.getJobKey()).thenReturn(jobKey1);
        when(mockTrigger2.getJobKey()).thenReturn(jobKey2);

        when(motechScheduler.isStarted()).thenReturn(true);

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(jobKey1)).thenReturn(jobKeyList);
        when(motechScheduler.getTriggersOfJob(jobKey2)).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger1.getPreviousFireTime()).thenReturn(DateTime.now().minusHours(26).toDate());
        when(mockTrigger1.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());


        when(mockTrigger2.getPreviousFireTime()).thenReturn(DateTime.now().minusHours(21).toDate());
        when(mockTrigger2.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnose(new ArrayList<String>() {{
            add(jobName1);
            add(jobName2);
        }});

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, jobName1, mockTrigger1.getPreviousFireTime(), mockTrigger1.getNextFireTime(), "No")));
        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, jobName2, mockTrigger2.getPreviousFireTime(), mockTrigger2.getNextFireTime(), "Yes")));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsResult.getStatus());

    }


    @Test
    public void shouldFailDiagnosticsIfJobHasNotRunForGivenInterval() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        final String jobName1 = ObdSchedulers.getAll().get(0);
        TriggerKey triggerKey = new TriggerKey(jobName1, "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));
        when(motechScheduler.isStarted()).thenReturn(true);

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(DateTime.now().minusHours(26).toDate());
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnose(new ArrayList<String>() {{
            add(jobName1);
        }});

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, jobName1, mockTrigger.getPreviousFireTime(), mockTrigger.getNextFireTime(), "No")));
        assertEquals(DiagnosticsStatus.FAIL, diagnosticsResult.getStatus());
    }

    @Test
    public void shouldReturnTrueIfTheJobHasRunThePreviousDay() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = ObdSchedulers.getAll().get(0);
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(DateTime.now().minusHours(24).toDate());
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusHours(1).toDate());

        when(motechScheduler.isStarted()).thenReturn(true);

        boolean schedulerRunStatus = schedulerDiagnosticService.AreSchedulerJobsRunning();

        assertTrue(schedulerRunStatus);
    }

    @Test
    public void shouldReturnFalseIfMotechSchedulerIsStopped() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = ObdSchedulers.getAll().get(0);
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(DateTime.now().minusHours(24).toDate());
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusHours(1).toDate());

        when(motechScheduler.isStarted()).thenReturn(false);

        boolean schedulerRunStatus = schedulerDiagnosticService.AreSchedulerJobsRunning();

        assertFalse(schedulerRunStatus);
    }

    @Test
    public void shouldFailIfJobDidNotRunYesterday() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = ObdSchedulers.getAll().get(0);
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(DateTime.now().minusHours(26).toDate());
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusHours(4).toDate());

        when(motechScheduler.isStarted()).thenReturn(true);

        boolean schedulerRunStatus = schedulerDiagnosticService.AreSchedulerJobsRunning();

        assertFalse(schedulerRunStatus);
    }

    @Test
    public void shouldNotFailIfJobHasNotRunYet() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = ObdSchedulers.getAll().get(0);
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusHours(4).toDate());

        when(motechScheduler.isStarted()).thenReturn(true);

        boolean schedulerRunStatus = schedulerDiagnosticService.AreSchedulerJobsRunning();

        assertTrue(schedulerRunStatus);
    }
}