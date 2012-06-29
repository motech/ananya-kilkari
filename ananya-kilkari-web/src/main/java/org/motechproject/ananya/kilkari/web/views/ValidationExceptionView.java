package org.motechproject.ananya.kilkari.web.views;

import org.motechproject.ananya.kilkari.exceptions.ValidationException;
import org.motechproject.ananya.kilkari.web.domain.KilkariConstants;
import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.motechproject.ananya.kilkari.web.utils.Util;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ValidationExceptionView extends AbstractView {
    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ValidationException exceptionObject =
                (ValidationException) model.get(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);

        response.getOutputStream().print(
                new BaseResponse(KilkariConstants.ERROR_STATUS_VALIDATION_EXCEPTION, exceptionObject.getMessage()).toJson());

        Util.setErrorResponseStatusBasedOnRequest(request, response);
        Util.setContentTypeBaseOnRequest(request, response);
    }
}
