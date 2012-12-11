package org.motechproject.ananya.kilkari.functional.test.utils;

import org.junit.Ignore;
import org.motechproject.ananya.kilkari.web.KilkariExceptionResolver;
import org.motechproject.ananya.kilkari.web.KilkariViewResolver;
import org.motechproject.ananya.kilkari.web.interceptors.KilkariChannelInterceptor;
import org.motechproject.web.message.converters.CSVHttpMessageConverter;
import org.motechproject.web.message.converters.CustomJaxb2RootElementHttpMessageConverter;
import org.motechproject.web.message.converters.CustomMappingJacksonHttpMessageConverter;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.test.web.server.setup.StandaloneMockMvcBuilder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

@Ignore
public class MVCTestUtils {

    public static MockMvc mockMvc(Object controller) {
        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(new KilkariChannelInterceptor())
                .setViewResolvers(new KilkariViewResolver());
        mockMvcBuilder.setMessageConverters(new CustomMappingJacksonHttpMessageConverter(), new CustomJaxb2RootElementHttpMessageConverter(), new CSVHttpMessageConverter());
        mockMvcBuilder.setHandlerExceptionResolvers(Arrays.asList(new HandlerExceptionResolver[]{new KilkariExceptionResolver()}));
        return mockMvcBuilder.build();
    }
}
