package org.motechproject.ananya.kilkari.reporting.service;

public abstract class TimedRunner {

    private int tries;
    private int intervalSleep;

    public TimedRunner() {
        this(5, 1000);
    }

    public TimedRunner(int tries, int intervalSleep) {
        this.tries = tries;
        this.intervalSleep = intervalSleep;
    }

    /*
    * Function to run within the timeout. It returns a boolean. If the value is true, the code
    * will break out of the loop immediately else try again within the timeout period.
    */
    public abstract boolean run();

    public void execute() {
        boolean result;
        for (int i = 0; i < tries; i++) {
            result = run();
            if (result) break;

            try {
                Thread.sleep(intervalSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread was interrupted.", e);
            }
        }
    }
}
