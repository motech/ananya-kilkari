package org.motechproject.ananya.kilkari.performance.tests.utils;

import org.codehaus.jackson.map.ObjectMapper;

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
}