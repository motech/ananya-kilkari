package org.motechproject.ananya.kilkari.web;

import org.motechproject.ananya.kilkari.web.interceptors.KilkariChannelInterceptor;
import org.motechproject.ananya.kilkari.web.views.ExceptionView;
import org.motechproject.ananya.kilkari.web.views.ValidationExceptionView;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.test.web.server.setup.StandaloneMockMvcBuilder;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.*;

public class TestUtils {

    private static class KilkariTestViewResolver implements ViewResolver {

        private Map<String, View> viewMap;

        public KilkariTestViewResolver() {
            this.viewMap = new HashMap<>();
            this.viewMap.put("exceptionView", new ExceptionView());
            this.viewMap.put("validationExceptionView", new ValidationExceptionView());
        }

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return this.viewMap.get(viewName);
        }
    }

    public static MockMvc mockMvc(Object controller) {
        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(new KilkariChannelInterceptor())
                .setViewResolvers(new KilkariTestViewResolver());

        Properties props = new Properties();
        props.put(".Exception", "exceptionView");
        props.put("org.motechproject.ananya.kilkari.exceptions.ValidationException", "validationExceptionView");

        KilkariExceptionResolver exceptionResolver = new KilkariExceptionResolver();
        exceptionResolver.setExceptionMappings(props);

        mockMvcBuilder.setHandlerExceptionResolvers(Arrays.asList(new HandlerExceptionResolver[]{exceptionResolver}));

        return mockMvcBuilder.build();
    }

}
