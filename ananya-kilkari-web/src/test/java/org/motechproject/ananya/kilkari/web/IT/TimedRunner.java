package org.motechproject.ananya.kilkari.web.IT;

public abstract class TimedRunner {

    /*
     * Function to run within the timeout. It returns a boolean. If the value is true, the code
     * will break out of the loop immediately else try again within the timeout period.
     */
    abstract boolean run();

    public void executeWithTimeout() {
        for (int i = 0; i < 5; i++) {
            if (run()) break;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread was interrupted.", e);
            }
        }
    }

}
