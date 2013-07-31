package org.motechproject.ananya.kilkari.web.diagnostics;

import java.util.ArrayList;
import java.util.List;

public class ObdSchedules {
    private static final List<String> schedulers = new ArrayList<String>() {
        {
            add("obd.send.main.sub.slot.one.messages-MAIN.ONE");
            add("obd.send.main.sub.slot.two.messages-MAIN.TWO");
            add("obd.send.main.sub.slot.three.messages-MAIN.THREE");
            add("obd.send.retry.slot.messages-RETRY.ONE");
            add("obd.send.retry.slot.messages-RETRY.TWO");
            add("obd.send.retry.slot.messages-RETRY.THREE");
        }
    };

    public static List<String> getAll() {
        return schedulers;
    }
}
