package org.motechproject.ananya.kilkari.functional.test.utils;

public class CallBackRequestBuilder {
    public static String template = "{" +
            "\"msisdn\": \"%s\"," +
            "\"action\": \"%s\"," +
            "\"status\": \"%s\"," +
            "\"operator\": \"%s\"" +
            "}";
    private String msisdn;
    private String action;
    private String status;

    public CallBackRequestBuilder() {
    }

    public CallBackRequestBuilder forMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public CallBackRequestBuilder forAction(String action) {
        this.action = action;
        return this;
    }

    public CallBackRequestBuilder forStatus(String status) {
        this.status = status;
        return this;
    }

    public String build() {
        return String.format(template, msisdn, action, status, "airtel");
    }
}
