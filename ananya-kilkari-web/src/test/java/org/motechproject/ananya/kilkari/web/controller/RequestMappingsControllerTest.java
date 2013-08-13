package org.motechproject.ananya.kilkari.web.controller;

import org.junit.Test;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.motechproject.ananya.kilkari.web.SpringIntegrationTest;
import org.motechproject.ananya.kilkari.web.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.junit.Assert.assertTrue;
import static org.motechproject.ananya.kilkari.web.MVCTestUtils.mockMvc;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class RequestMappingsControllerTest extends SpringIntegrationTest {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    public void shouldRetrieveRequestMappings() throws Exception {
        RequestMappingsController controller = new RequestMappingsController(requestMappingHandlerMapping);

        mockMvc(controller).perform(get("/requestmappings"))
                .andExpect(status().isOk())
                .andExpect(content().type(HttpHeaders.APPLICATION_JSON))
                .andExpect(hasMappings());
    }

    private ResultMatcher hasMappings() {
        return new ResultMatcher() {
            @Override
            public void match(MvcResult mvcResult) throws Exception {
                String contentAsString = mvcResult.getResponse().getContentAsString();
                Mappings mappings = TestUtils.fromJson(contentAsString, Mappings.class);
                assertTrue(mappings.size() > 0);
            }
        };
    }
}
