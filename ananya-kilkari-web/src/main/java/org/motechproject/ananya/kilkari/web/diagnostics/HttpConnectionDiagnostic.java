package org.motechproject.ananya.kilkari.web.diagnostics;

import org.motechproject.diagnostics.annotation.Diagnostic;
import org.motechproject.diagnostics.diagnostics.DiagnosticLog;
import org.motechproject.diagnostics.response.DiagnosticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

@Component
public class HttpConnectionDiagnostic {

    private Properties properties;

    @Autowired(required = false)
    public void setProperties(@Qualifier("kilkariProperties") Properties properties) {
        this.properties = properties;
    }

    @Diagnostic(name = "HttpConnection to Ananya-Reports")
    public DiagnosticsResult performDiagnosis() {
        HttpURLConnection connection;
        DiagnosticLog diagnosticLog = new DiagnosticLog();
        diagnosticLog.add("Opening http connection to Ananya-Reports");

        if (properties == null) return null;
        if (properties.getProperty("reporting.service.base.url") == null) {
            diagnosticLog.add("Property reporting.service.base.url does not exist.");
            return new DiagnosticsResult(false, diagnosticLog.toString());
        }
        boolean httpConnectionStatus = false;
        String errorMessage = "Http connection to Ananya-Reports failed. ";

        try {
            connection = getConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                httpConnectionStatus = true;
                diagnosticLog.add("Successful http connection to Ananya-Reports");
            } else {
                diagnosticLog.add("Status: " + responseCode + "\nMessage :" + errorMessage + connection.getResponseMessage());
            }

        } catch (Exception e) {
            diagnosticLog.add(errorMessage + e.getMessage());
        }
        return new DiagnosticsResult(httpConnectionStatus, diagnosticLog.toString());
    }

    protected HttpURLConnection getConnection() throws IOException {
        HttpURLConnection connection;
        URL url = new URL(properties.getProperty("reporting.service.base.url") + "/index.jsp");
        connection = (HttpURLConnection) url.openConnection();
        return connection;
    }
}