package org.motechproject.ananya.kilkari.web.diagnostics;

import java.util.ArrayList;
import java.util.List;

public class ObdSchedulers {
    private static final List<String> schedulers = new ArrayList<String>() {
        {
            add("obd.send.new.messages");
            add("obd.send.retry.messages");
        }
    };

    public static List<String> getAll() {
        return schedulers;
    }
}
