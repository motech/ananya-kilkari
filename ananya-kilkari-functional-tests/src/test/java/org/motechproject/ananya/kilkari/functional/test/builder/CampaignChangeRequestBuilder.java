package org.motechproject.ananya.kilkari.functional.test.builder;

public class CampaignChangeRequestBuilder {
    public static String template = "{" +
            "\"reason\": \"%s\"" +
            "}";
    private String reason;

    public CampaignChangeRequestBuilder() {
    }

    public CampaignChangeRequestBuilder forReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String build() {
        return String.format(template, reason);
    }
}
