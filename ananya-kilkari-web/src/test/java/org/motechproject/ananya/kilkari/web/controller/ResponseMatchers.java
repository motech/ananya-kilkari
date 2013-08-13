package org.motechproject.ananya.kilkari.web.controller;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;

import javax.xml.bind.JAXBException;

public class ResponseMatchers {

    public static BaseMatcher<String> baseResponseMatcher(final String status, final String description) {
        return baseResponseMatcher(status, description, HttpHeaders.APPLICATION_JSON);
    }

    public static BaseMatcher<String> baseResponseMatcher(final String status, final String description, final String responseType) {
        return new BaseMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                BaseResponse baseResponse = null;
                String content = ((String) o).replace("var response = ", "");
                if(responseType.equals(HttpHeaders.APPLICATION_XML))
                    try {
                        baseResponse = TestUtils.fromXml(content, BaseResponse.class);
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                else
                    baseResponse = TestUtils.fromJson(content, BaseResponse.class);

                return baseResponse.getStatus().equals(status)
                        && baseResponse.getDescription().equals(description);
            }

            @Override
            public void describeTo(Description matcherDescription) {
            }
        };
    }
}
