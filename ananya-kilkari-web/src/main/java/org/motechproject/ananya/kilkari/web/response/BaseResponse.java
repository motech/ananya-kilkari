package org.motechproject.ananya.kilkari.web.response;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.StringWriter;

@XmlRootElement(name = "response")
public class BaseResponse {

    private static final String FAILED = "FAILED";
    private static final String SUCCESS = "SUCCESS";

    @JsonProperty
    @XmlElement
    protected String status;
    @JsonProperty
    @XmlElement
    protected String description;

    private BaseResponse(String status, String description) {
        this.status = status;
        this.description = description;
    }

    BaseResponse() {
    }

    public static BaseResponse failure(String description) {
        return new BaseResponse(FAILED, description);
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
        return status.equals(FAILED);
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

    public String toXml(String contentType) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(BaseResponse.class);
        Marshaller marshaller = context.createMarshaller();
        setNoHeaderOptionOnMarshaller(marshaller);
        setCharset(contentType, marshaller);
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(this, stringWriter);
        return stringWriter.toString();
    }

    private void setNoHeaderOptionOnMarshaller(Marshaller marshaller) throws PropertyException {
        final String JAXB_FRAGMENT = "jaxb.fragment";
        final String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";
        marshaller.setProperty(JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    }

    private void setCharset(String contentType, Marshaller marshaller) throws PropertyException {
        if(StringUtils.isEmpty(contentType))
            return;
        MediaType mediaType = MediaType.parseMediaType(contentType);
        if (mediaType != null && mediaType.getCharSet() != null) {
            marshaller.setProperty(Marshaller.JAXB_ENCODING, mediaType.getCharSet().name());
        }
    }
}
