package org.motechproject.ananya.kilkari.smoke.domain.kilkari;

import org.codehaus.jackson.annotate.JsonProperty;

public class BaseResponse {
    @JsonProperty
    protected String status;
    @JsonProperty
    protected String description;

    public BaseResponse(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public BaseResponse() {
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
