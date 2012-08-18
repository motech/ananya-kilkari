package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.ananya.kilkari.performance.tests.domain.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

public class HttpUtils {

    public static String constructUrl(String url, String path, Map<String, String> parametersMap) {
        url += "/" + path + "?";
        for (String key : parametersMap.keySet()) {
            url += (key + "=" + parametersMap.get(key) + "&");
        }
        return url;
    }

    public static <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        ObjectMapper mapper = new ObjectMapper();
        T serializedObject = null;
        try {
            serializedObject = mapper.readValue(jsonString, subscriberResponseClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }

    public static <T> T fromJsonWithResponse(String jsonString, Class<T> subscriberResponseClass) {
        return fromJson(jsonString.replace("var response = ", ""), subscriberResponseClass);
    }

    public static BaseResponse httpGetWithJsonResponse(Map<String, String> parametersMap, String minusBaseUrl) {
        ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(constructUrl(BaseConfiguration.baseUrl(), minusBaseUrl, parametersMap), String.class);
        return  fromJsonWithResponse(responseEntity.getBody(), BaseResponse.class);

    }

}