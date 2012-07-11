package org.motechproject.ananya.kilkari.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
public class OnMobileOBDGateway {

    private HttpClient obdHttpClient;

    private static final Logger logger = LoggerFactory.getLogger(OnMobileOBDGateway.class);

    @Autowired
    public OnMobileOBDGateway(HttpClient obdHttpClient) {
        this.obdHttpClient = obdHttpClient;
    }

    public void send(String content) {
        HttpPost httpPost = new HttpPost("https://www.commcarehq.org/a/ananya-care/receiver/");
        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(content.getBytes()), "file.csv");
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", inputStreamBody);
        httpPost.setEntity(reqEntity);
        try {
            HttpResponse response = obdHttpClient.execute(httpPost);
            validate(response) ;
        } catch (IOException ex) {
            logger.error("Sending messages to OBD failed", ex);
            throw new RuntimeException(ex);
        }
    }

    private void validate(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        if(201 != statusCode || !"CREATED".equalsIgnoreCase(reasonPhrase)) {
            String errorMessage = String.format("Sending messages to OBD failed with code: %s, reason: %s", statusCode, reasonPhrase);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
