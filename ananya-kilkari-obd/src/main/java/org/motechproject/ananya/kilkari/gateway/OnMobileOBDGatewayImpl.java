package org.motechproject.ananya.kilkari.gateway;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.motechproject.ananya.kilkari.profile.OBDProductionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

@Component
@OBDProductionProfile
public class OnMobileOBDGatewayImpl implements OnMobileOBDGateway {

    private HttpClient obdHttpClient;
    private Properties obdProperties;

    private static final Logger logger = LoggerFactory.getLogger(OnMobileOBDGatewayImpl.class);
    private static final String DELIVERY_URL_PROPERTY = "obd.message.delivery.url";
    private static final String DELIVERY_FILENAME_PROPERTY = "obd.message.delivery.filename";
    private static final String DELIVERY_FILE_PROPERTY = "obd.message.delivery.file";


    @Autowired
    public OnMobileOBDGatewayImpl(HttpClient obdHttpClient, Properties obdProperties) {
        this.obdHttpClient = obdHttpClient;
        this.obdProperties = obdProperties;
    }

    @Override
    public void send(String content) {
        String url = obdProperties.getProperty(DELIVERY_URL_PROPERTY);
        logger.info(String.format("Uploading the campaign messages to url: {1}\nContent:\n{2}", url, content));

        String fileName = obdProperties.getProperty(DELIVERY_FILENAME_PROPERTY);
        String file = obdProperties.getProperty(DELIVERY_FILE_PROPERTY);

        HttpPost httpPost = new HttpPost(url);
        InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(content.getBytes()), fileName);
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart(file, inputStreamBody);
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
        if(!isStatusOk(statusCode)) {
            String errorMessage = String.format("Sending messages to OBD failed with code: %s, reason: %s", statusCode, reasonPhrase);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    private boolean isStatusOk(int status) {
        return status >= 200 && status <= 299;
    }
}
