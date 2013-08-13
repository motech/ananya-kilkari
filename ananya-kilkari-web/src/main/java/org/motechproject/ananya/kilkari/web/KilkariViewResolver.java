package org.motechproject.ananya.kilkari.web;

import org.motechproject.ananya.kilkari.web.views.ExceptionView;
import org.motechproject.ananya.kilkari.web.views.ValidationExceptionView;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class KilkariViewResolver implements ViewResolver {

    private Map<String, View> viewMap;

    public KilkariViewResolver() {
        this.viewMap = new HashMap<>();
        this.viewMap.put("exceptionView", new ExceptionView());
        this.viewMap.put("validationExceptionView", new ValidationExceptionView());
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        return this.viewMap.get(viewName);
    }
}
