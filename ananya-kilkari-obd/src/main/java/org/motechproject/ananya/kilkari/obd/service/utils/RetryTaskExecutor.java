package org.motechproject.ananya.kilkari.obd.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class RetryTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RetryTaskExecutor.class);

    private final TaskExecutor taskExecutor;

    @Autowired
    public RetryTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void run(int initialWait, int retryInterval, int maxRetryCount, RetryTask retryTask) {
        taskExecutor.execute(new RetryRunnable(initialWait, retryInterval, maxRetryCount, retryTask));
    }

    private class RetryRunnable implements Runnable {
        private final int initialWait;
        private final int retryInterval;
        private final int maxRetryCount;
        private final RetryTask retryTask;

        RetryRunnable(int initialWait, int retryInterval, int maxRetryCount, RetryTask retryTask) {
            this.initialWait = initialWait;
            this.retryInterval = retryInterval;
            this.maxRetryCount = maxRetryCount;
            this.retryTask = retryTask;
        }

        @Override
        public void run() {
            initialWait();
            int retryCount = 0;
            while (true) {
                if (retryCount >= maxRetryCount) {
                    return;
                }
                retryCount++;
                try {
                    if (retryTask.execute())
                        return;
                } catch (Exception ex) {
                    logger.error(String.format("Error executing task: %s", ex.getMessage()));
                }
                sleepFor(retryInterval);
            }
        }

        private void initialWait() {
            if (initialWait > 0) {
                sleepFor(initialWait);
            }
        }

        private void sleepFor(int seconds) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
