package org.motechproject.ananya.kilkari.web.views;

import org.motechproject.ananya.kilkari.web.contract.response.BaseResponse;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ExceptionView extends KilkariView {

    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Exception exceptionObject = (Exception) model.get(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);

        String responseJson = BaseResponse.failure(exceptionObject.getMessage()).toJson();
        response.getOutputStream().print(responseJson);

        setHttpStatusCodeBasedOnChannel(request, response);
        setContentTypeToJavaScriptForIVRChannel(request, response);
    }
}
