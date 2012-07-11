package org.motechproject.ananya.kilkari.web.contract.response;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class BaseResponse {

    private static final String ERROR = "ERROR";
    private static final String SUCCESS = "SUCCESS";

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

    public static BaseResponse failure(String description) {
        return new BaseResponse(ERROR, description);
    }

    public static BaseResponse success(String description) {
        return new BaseResponse(SUCCESS, description);
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseResponse)) return false;

        BaseResponse that = (BaseResponse) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    public boolean isError() {
        return status.equals(ERROR);
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }
}
