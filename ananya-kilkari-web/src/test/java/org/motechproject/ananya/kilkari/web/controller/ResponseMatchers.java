package org.motechproject.ananya.kilkari.web.controller;

import com.google.gson.Gson;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ananya.kilkari.web.contract.response.BaseResponse;

public class ResponseMatchers {

    public static BaseMatcher<String> baseResponseMatcher(final String status, final String description) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                BaseResponse baseResponse = fromJson(((String) o).replace("var response = ", ""), BaseResponse.class);

                return baseResponse.getStatus().equals(status)
                        && baseResponse.getDescription().equals(description);
            }

            @Override
            public void describeTo(Description matcherDescription) {
            }
        };
    }


    private static <T> T fromJson(String jsonString, Class<T> subscriberResponseClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, subscriberResponseClass);
    }
}
