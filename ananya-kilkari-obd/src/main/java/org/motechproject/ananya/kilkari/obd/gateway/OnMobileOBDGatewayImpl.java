package org.motechproject.ananya.kilkari.obd.gateway;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.motechproject.ananya.kilkari.obd.profile.OBDProductionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        logger.info(String.format("Uploading the campaign messages to url: %s\nContent:\n%s", url, content));

        String fileName = obdProperties.getProperty(DELIVERY_FILENAME_PROPERTY);
        String file = obdProperties.getProperty(DELIVERY_FILE_PROPERTY);

        HttpPost httpPost = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart(file, new ByteArrayBody(content.getBytes(), "text/plain", fileName));
        httpPost.setEntity(reqEntity);
        try {
            HttpResponse response = obdHttpClient.execute(httpPost);
            String responseContent = readResponse(response); //Read the response from stream first thing. As it should be read completely before making any other request.
            validate(response, responseContent);
            logger.info(String.format("Uploaded campaign messages successfully.\nResponse:\n%s", responseContent));
        } catch (IOException ex) {
            logger.error("Sending messages to OBD failed", ex);
            throw new RuntimeException(ex);
        }
    }

    private void validate(HttpResponse response, String responseContent) {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        if(!isStatusOk(statusCode)) {
            String errorMessage = String.format("Sending messages to OBD failed with code: %s, reason: %s", statusCode, reasonPhrase);
            logger.error(errorMessage);
            logger.error(String.format("response:\n%s", responseContent));
            throw new RuntimeException(errorMessage);
        }
    }

    private String readResponse(HttpResponse response) {
        try {
            HttpEntity entity = response.getEntity();
            if(entity == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            String content = "";
            String line;
            while((line  = reader.readLine()) != null ) {
                content += line;
            }
            return content;
        } catch (IOException ex) {
            logger.warn("Could not read response");
            return null;
        }
    }

    private boolean isStatusOk(int status) {
        return status >= 200 && status <= 299;
    }
}
