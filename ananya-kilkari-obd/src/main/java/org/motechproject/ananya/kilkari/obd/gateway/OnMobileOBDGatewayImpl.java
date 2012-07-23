package org.motechproject.ananya.kilkari.obd.gateway;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ananya.kilkari.obd.contract.InvalidCallDeliveryFailureRecord;
import org.motechproject.ananya.kilkari.obd.profile.ProductionProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@ProductionProfile
public class OnMobileOBDGatewayImpl implements OnMobileOBDGateway {

    private HttpClient obdHttpClient;
    private OBDProperties obdProperties;
    private RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OnMobileOBDGatewayImpl.class);

    @Autowired
    public OnMobileOBDGatewayImpl(HttpClient obdHttpClient, OBDProperties obdProperties, RestTemplate kilkariRestTemplate) {
        this.obdHttpClient = obdHttpClient;
        this.obdProperties = obdProperties;
        this.restTemplate = kilkariRestTemplate;
    }

    @Override
    public void sendNewMessages(String content) {
        String url = getUrl(obdProperties.getNewMessageDeliveryUrlQueryString());
        send(content, url);
    }

    @Override
    public void sendRetryMessages(String content) {
        String url = getUrl(obdProperties.getRetryMessageDeliveryUrlQueryString());
        send(content, url);
    }

    @Override
    public void sendInvalidFailureRecord(InvalidCallDeliveryFailureRecord invalidCallDeliveryFailureRecord) {
        restTemplate.postForLocation(obdProperties.getFailureReportUrl(), invalidCallDeliveryFailureRecord);
    }

    private void send(String content, String url) {
        logger.info(String.format("Uploading the campaign messages to url: %s\nContent:\n%s", url, content));

        String fileName = obdProperties.getMessageDeliveryFileName();
        String file = obdProperties.getMessageDeliveryFile();

        HttpPost httpPost = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        reqEntity.addPart(file, new ByteArrayBody(content.getBytes(), "text/plain", fileName));
        httpPost.setEntity(reqEntity);
        try {
            HttpResponse response = obdHttpClient.execute(httpPost);
            String responseContent = readResponse(response); //Read the response from stream first thing. As it should be read completely before making any other request.
            validateResponse(response, responseContent);
            logger.info(String.format("Uploaded campaign messages successfully.\nResponse:\n%s", responseContent));
        } catch (IOException ex) {
            logger.error("Sending messages to OBD failed", ex);
            throw new RuntimeException(ex);
        }
    }

    private String getUrl(String queryString) {
        String baseUrl = obdProperties.getMessageDeliveryBaseUrl();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        String date = dateTimeFormatter.print(DateTime.now());
        return String.format(baseUrl + queryString, date, date);
    }

    private void validateResponse(HttpResponse response, String responseContent) {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reasonPhrase = statusLine.getReasonPhrase();
        if (!isStatusOk(statusCode)) {
            String errorMessage = String.format("Sending messages to OBD failed with code: %s, reason: %s", statusCode, reasonPhrase);
            logger.error(errorMessage);
            logger.error(String.format("response:\n%s", responseContent));
            throw new RuntimeException(errorMessage);
        }
    }

    private String readResponse(HttpResponse response) {
        try {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            String content = "";
            String line;
            while ((line = reader.readLine()) != null) {
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
