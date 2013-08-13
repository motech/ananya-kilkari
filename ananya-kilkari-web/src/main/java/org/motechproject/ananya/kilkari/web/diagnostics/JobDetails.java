package org.motechproject.ananya.kilkari.web.diagnostics;

import java.util.Date;

public class JobDetails {
    private Date previousFireTime;
    private Date nextFireTime;
    private String name;

    public JobDetails(Date previousFireTime, String name, Date nextFireTime) {
        this.previousFireTime = previousFireTime;
        this.name = name;
        this.nextFireTime = nextFireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public String getName() {
        return name;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }
}
