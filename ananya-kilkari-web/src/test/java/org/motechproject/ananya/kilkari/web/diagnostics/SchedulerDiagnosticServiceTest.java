package org.motechproject.ananya.kilkari.web.diagnostics;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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


    private final String schedulerDiagnosticsFormat = "%s\nPrevious Fire Time : %s\nNext Fire Time : %s";
    private SchedulerDiagnosticService schedulerDiagnosticService;

    @Before
    public void setUp() {
        when(schedulerFactoryBean.getScheduler()).thenReturn(motechScheduler);
        schedulerDiagnosticService = new SchedulerDiagnosticService(schedulerFactoryBean);
    }

    @Test
    public void shouldPrintSchedulerHasNotRunWhenSchedulerHasNotPreviouslyRun() throws SchedulerException {
        Set<TriggerKey> triggerKeys = new HashSet<>();
        TriggerKey triggerKey = new TriggerKey("name1", "default");
        triggerKeys.add(triggerKey);
        when(motechScheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(anyString()))).thenReturn(triggerKeys);

        Trigger mockTrigger = mock(Trigger.class);
        when(motechScheduler.getTrigger(triggerKey)).thenReturn(mockTrigger);
        final String jobName1 = "Job Name1";
        final String jobName2 = "Job Name2";
        when(mockTrigger.getJobKey()).thenReturn(new JobKey(jobName1));

        ArrayList jobKeyList = mock(ArrayList.class);
        when(motechScheduler.getTriggersOfJob(any(JobKey.class))).thenReturn(jobKeyList);
        when(jobKeyList.size()).thenReturn(2);

        when(mockTrigger.getPreviousFireTime()).thenReturn(null);
        when(mockTrigger.getNextFireTime()).thenReturn(DateTime.now().plusMonths(4).toDate());

        DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnose(new ArrayList<String>() {{
            add(jobName2);
            add(jobName1);
        }});

        assertTrue(diagnosticsResult.getMessage().contains(String.format(schedulerDiagnosticsFormat, jobName1, "This scheduler has not yet run", mockTrigger.getNextFireTime())));
    }

    @Test
    public void shouldReturnTrueIfTheSchedulerHasRunThePreviousDay() throws SchedulerException {
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

        boolean schedulerRunStatus = schedulerDiagnosticService.isSchedulerRunning();

        assertTrue(schedulerRunStatus);
    }

    @Test
    public void shouldFailIfOBDSchedulerDidNotRunYesterday() throws SchedulerException {
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

        boolean schedulerRunStatus = schedulerDiagnosticService.isSchedulerRunning();

        assertFalse(schedulerRunStatus);
    }

    @Test
    public void shouldFailIfOBDSchedulerDidNotRunYet() throws SchedulerException {
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

        boolean schedulerRunStatus = schedulerDiagnosticService.isSchedulerRunning();

        assertTrue(schedulerRunStatus);
    }
}