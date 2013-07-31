package org.motechproject.ananya.kilkari.test.data.utils;

public class TimedRunnerResponse<T> {
    public static final TimedRunnerResponse EMPTY = new TimedRunnerResponse(null);
    private T obj;

    public TimedRunnerResponse(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }
}
