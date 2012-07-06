package org.motechproject.ananya.kilkari.utils;

import com.google.gson.Gson;

import java.util.Map;

public class TestUtils {
    public static final String KILKARI_URL = "http://localhost:8080/ananya-kilkari";

    public static String constructUrl(String url, String path, Map<String, String> parametersMap) {
        url += "/" + path + "?";
        for (String key : parametersMap.keySet()) {
            url += key + "=" + parametersMap.get(key) + "&";
        }
        return url;
    }

    public static <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, subscriberResponseClass);
    }
}
