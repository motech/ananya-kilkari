package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Component
public class HttpUtils {

    private Properties performanceProperties;
    private String baseurl;
    private String reportUrl;

    @Autowired
    public HttpUtils(@Qualifier("performanceProperties")Properties performanceProperties) {
        this.performanceProperties = performanceProperties;
        baseurl = performanceProperties.getProperty("baseurl");
        reportUrl = performanceProperties.getProperty("report.baseurl");
    }

    public String constructUrl(String url, String path, Map<String, String> parametersMap) {
        url += "/" + path + "?";
        if (parametersMap == null) return url;
        for (String key : parametersMap.keySet()) {
            url += (key + "=" + parametersMap.get(key) + "&");
        }
        return url;
    }

    public <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        ObjectMapper mapper = new ObjectMapper();
        T serializedObject = null;
        try {
            serializedObject = mapper.readValue(jsonString, subscriberResponseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }

    public <T> T fromJsonWithResponse(String jsonString, Class<T> subscriberResponseClass) {
        return fromJson(jsonString.replace("var response = ", ""), subscriberResponseClass);
    }

    public BaseResponse httpGetKilkariWithJsonResponse(Map<String, String> parametersMap, String path) {
        ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(
                constructUrl(baseurl, path, parametersMap), String.class);
        return fromJsonWithResponse(responseEntity.getBody(), BaseResponse.class);
    }

    public BaseResponse httpPostKilkariWithJsonResponse(Map<String, String> queryStringParametersMap, Object postParam, String urlPath) {
        ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(
                constructUrl(baseurl, urlPath, queryStringParametersMap), postParam, String.class);
        return fromJsonWithResponse(responseEntity.getBody(), BaseResponse.class);
    }

    public void httpPostReports(Map<String, String> queryStringParametersMap, Object postParam, String urlPath) {
        new RestTemplate().postForEntity(
                constructUrl(reportUrl, urlPath, queryStringParametersMap), postParam, String.class);
    }

    public void put(Object request, String urlPath) {
        try {
            String url = constructUrl(baseurl, urlPath, Collections.EMPTY_MAP);
            new RestTemplate().put(new URI(url), request);
        } catch (URISyntaxException e) {
            System.out.println("INVALID URI!");
        }
    }
}