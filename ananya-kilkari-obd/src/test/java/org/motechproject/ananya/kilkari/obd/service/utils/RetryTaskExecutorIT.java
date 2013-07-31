package org.motechproject.ananya.kilkari.obd.service.utils;

import org.junit.Test;
import org.motechproject.ananya.kilkari.obd.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class RetryTaskExecutorIT extends SpringIntegrationTest {

    @Autowired
    private RetryTaskExecutor retryTaskExecutor;

    @Test
    public void shouldRetryATaskAccordingToGivenConditions() throws InterruptedException {
        TestRetryTask testRetryTask = new TestRetryTask();

        retryTaskExecutor.run(0, 1, 2, testRetryTask);

        Thread.sleep(5000);
        assertEquals(2, testRetryTask.getCallCount());
    }

    private class TestRetryTask implements RetryTask {
        private int callCount;

        @Override
        public boolean execute() {
            callCount++;
            return false;
        }

        public int getCallCount() {
            return callCount;
        }
    }
}
