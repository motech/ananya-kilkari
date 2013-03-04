package org.motechproject.ananya.kilkari.web.listeners;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FileNotFoundException;

public class KilkariLog4jListener implements ServletContextListener {
    private static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";
    private static final String REFRESH_INTERVAL_PARAM = "log4jRefreshInterval";
    private static KilkariWatchdog watchdog;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initLogging(event.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        watchdog.interrupt();
    }

    private static void initLogging(ServletContext servletContext) {
        String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        if (location != null) {
            File file = getConfigFile(location, servletContext);
            Long refreshInterval = getRefreshInterval(servletContext);
            if (refreshInterval != null) {
                configureAndWatch(file.getAbsolutePath(), refreshInterval);
            } else {
                PropertyConfigurator.configure(location);
            }
        }
    }

    private static File getConfigFile(String location, ServletContext servletContext) {
        File file = null;
        try {
            if (!ResourceUtils.isUrl(location)) {
                location = SystemPropertyUtils.resolvePlaceholders(location);
                location = WebUtils.getRealPath(servletContext, location);
            }
            file = ResourceUtils.getFile(location);
            if (!file.exists()) {
                throw new FileNotFoundException("Log4j config file [" + location + "] not found");
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + e.getMessage());
        }
        return file;
    }

    private static Long getRefreshInterval(ServletContext servletContext) {
        Long refreshInterval = null;
        String intervalString = servletContext.getInitParameter(REFRESH_INTERVAL_PARAM);
        if (intervalString != null) {
            try {
                refreshInterval = Long.parseLong(intervalString);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid 'log4jRefreshInterval' parameter: " + ex.getMessage());
            }
        }
        return refreshInterval;
    }

    private static void configureAndWatch(String configFileName, long refreshInterval) {
        watchdog = new KilkariWatchdog(configFileName);
        watchdog.setDelay(refreshInterval);
        watchdog.start();
    }
}

class KilkariWatchdog extends FileWatchdog {
    KilkariWatchdog(String filename) {
        super(filename);
    }

    public void doOnChange() {
        new PropertyConfigurator().doConfigure(filename,
                LogManager.getLoggerRepository());

    }
}
