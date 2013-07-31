package org.motechproject.ananya.kilkari.web.controller;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.kilkari.web.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RequestMappingsController {

    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public RequestMappingsController(@Qualifier("handlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @RequestMapping(value = "/requestmappings", produces = HttpHeaders.APPLICATION_JSON, method = RequestMethod.GET)
    @ResponseBody
    public Mappings requestMappings() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        Mappings mappings = new Mappings();

        for (RequestMappingInfo requestMappingInfo : handlerMethods.keySet()) {
            mappings.add(new Mapping(requestMappingInfo, handlerMethods.get(requestMappingInfo)));
        }
        return mappings;
    }
}

class Mappings {
    @JsonProperty("mappings")
    private List<Mapping> mappingList = new ArrayList<>();

    @JsonIgnore
    public void add(Mapping mapping) {
        this.mappingList.add(mapping);
    }

    @JsonIgnore
    public int size() {
        return mappingList.size();
    }
}

class Mapping {
    @JsonProperty
    private String handler;
    @JsonProperty
    private String patterns;
    @JsonProperty
    private String methods;
    @JsonProperty
    private String headers;
    @JsonProperty
    private String parameters;
    @JsonProperty
    private String produces;
    @JsonProperty
    private String consumes;

    Mapping() {
    }

    public Mapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
        this.handler = handlerMethod.toString();
        this.patterns = requestMappingInfo.getPatternsCondition().toString();
        this.methods = requestMappingInfo.getMethodsCondition().toString();
        this.headers = requestMappingInfo.getHeadersCondition().toString();
        this.parameters = requestMappingInfo.getParamsCondition().toString();
        this.produces = requestMappingInfo.getProducesCondition().toString();
        this.consumes = requestMappingInfo.getConsumesCondition().toString();
    }
}