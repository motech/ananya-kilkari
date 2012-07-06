package org.motechproject.ananya.kilkari.response;

public class BaseResponse {
    protected String status;
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
