package org.motechproject.ananya.kilkari.web.views;

import org.motechproject.ananya.kilkari.web.response.BaseResponse;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ExceptionView extends AbstractView {

    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> stringObjectMap,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Exception exceptionObject = (Exception) stringObjectMap.get(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE);

        response.getOutputStream().print(new BaseResponse("ERR", exceptionObject.getMessage()).toJson());

        if (isIvrChannelRequest(request)) response.setStatus(200);
        else response.setStatus(550);
    }

    private boolean isIvrChannelRequest(HttpServletRequest request) {
        return request.getParameterMap().containsKey("channel") &&
                request.getParameter("channel").equalsIgnoreCase("ivr");
    }
}
