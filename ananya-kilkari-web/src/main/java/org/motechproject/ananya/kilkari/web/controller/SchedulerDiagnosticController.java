package org.motechproject.ananya.kilkari.web.controller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.motechproject.ananya.kilkari.web.diagnostics.SchedulerDiagnosticService;
import org.motechproject.diagnostics.response.DiagnosticsResponse;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

@Controller
public class SchedulerDiagnosticController {

    private SchedulerDiagnosticService schedulerDiagnosticService;
    private VelocityEngine velocityEngine;

    @Autowired
    public SchedulerDiagnosticController(SchedulerDiagnosticService schedulerDiagnosticService, VelocityEngine velocityEngine) {
        this.schedulerDiagnosticService = schedulerDiagnosticService;
        this.velocityEngine = velocityEngine;
    }

    @RequestMapping(value = "/diagnostics/scheduler/{subscriptionId}", method = RequestMethod.GET)
    @ResponseBody
    public void getSchedule(@PathVariable final String subscriptionId, final HttpServletResponse response) throws SchedulerException, IOException {
        final DiagnosticsResult diagnosticsResult = schedulerDiagnosticService.diagnose(new ArrayList<String>() {{
            add(subscriptionId);
        }});
        String responseMessage = createResponseMessage(new DiagnosticsResponse("Schedulers for subscriptionId : " + subscriptionId, diagnosticsResult));
        response.getOutputStream().print(responseMessage);
    }

    private String createResponseMessage(DiagnosticsResponse diagnosticsResponse) {
        Template template = velocityEngine.getTemplate("/schedulerResponse.vm");
        VelocityContext context = new VelocityContext();
        context.put("diagnosticsResponse", diagnosticsResponse);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}