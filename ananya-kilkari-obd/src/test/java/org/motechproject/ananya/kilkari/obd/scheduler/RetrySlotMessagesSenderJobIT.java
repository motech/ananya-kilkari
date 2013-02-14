package org.motechproject.ananya.kilkari.obd.scheduler;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.domain.RetrySubSlot;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.motechproject.event.MotechEvent;
import org.motechproject.retry.dao.AllRetries;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

public class RetrySlotMessagesSenderJobIT extends SpringIntegrationTest {

    @Autowired
    private RetrySlotMessagesSenderJob retrySlotMessagesSenderJob;
    @Autowired
    private AllRetries allRetries;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Test
    public void shouldCreateARetrySchedule() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("sub_slot", RetrySubSlot.THREE);
        retrySlotMessagesSenderJob.handleMessages(new MotechEvent("subject", parameters));
    }

    @After
    public void tearDown() {
        allRetries.removeAll();
        removeQuartzJobs();
    }

    private void removeQuartzJobs() {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            List<String> groupNames = scheduler.getJobGroupNames();
            for (String group : groupNames) {
                Set<JobKey> jobNames = scheduler.getJobKeys(jobGroupEquals(group));
                for (JobKey jobKey : jobNames)
                    scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
